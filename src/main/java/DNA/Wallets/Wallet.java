package DNA.Wallets;

import java.lang.Thread.State;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;

import org.bouncycastle.math.ec.ECPoint;

import DNA.Fixed8;
import DNA.UInt160;
import DNA.UInt256;
import DNA.Core.Block;
import DNA.Core.Blockchain;
import DNA.Core.IssueTransaction;
import DNA.Core.SignatureContext;
import DNA.Core.Transaction;
import DNA.Core.TransactionAttribute;
import DNA.Core.TransactionInput;
import DNA.Core.TransactionOutput;
import DNA.Core.Scripts.Program;
import DNA.Cryptography.AES;
import DNA.Cryptography.Base58;
import DNA.Cryptography.Digest;
import DNA.Cryptography.ECC;
import DNA.IO.Serializable;
import DNA.IO.Caching.TrackState;
import DNA.IO.Caching.TrackableCollection;
import DNA.Network.Rest.RestRuntimeException;
import DNA.sdk.helper.DataFormat;

public abstract class Wallet implements AutoCloseable {

    public static final byte COIN_VERSION = 0x17;
    private byte[] iv;
    private byte[] masterKey;
    private Map<UInt160, Account> accounts;
    private Map<UInt160, Contract> contracts;
    private TrackableCollection<TransactionInput, Coin> coins;
    private int current_height;

    private String path;
    private Thread thread;
    private boolean isrunning = false;

    protected final Object locker = new Object();
    
    private Wallet(String path, byte[] passwordKey, boolean create) throws BadPaddingException, IllegalBlockSizeException {
        this.path = path;
    	if (create) {
            this.iv = AES.generateIV();
            this.masterKey = AES.generateKey();
            this.accounts = new HashMap<UInt160, Account>();
            this.contracts = new HashMap<UInt160, Contract>();
            this.coins = new TrackableCollection<TransactionInput, Coin>();
            try {
				this.current_height = Blockchain.current() != null ? Blockchain.current().headerHeight() + 1 : 1;
			} catch (Exception ex) {
            	this.current_height = 1;
			}
            buildDatabase();
            saveStoredData("PasswordHash", Digest.sha256(passwordKey));
            saveStoredData("IV", iv);
            saveStoredData("MasterKey", AES.encrypt(masterKey, passwordKey, iv));
            saveStoredData("Version", new byte[] { 0, 7, 0, 13 });
            saveStoredData("Height", ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(current_height).array());
        } else {
            byte[] passwordHash = loadStoredData("PasswordHash");
            if (passwordHash != null && !Arrays.equals(passwordHash, Digest.sha256(passwordKey))) {
                throw new BadPaddingException();
            }
            this.iv = loadStoredData("IV");
			this.masterKey = AES.decrypt(loadStoredData("MasterKey"), passwordKey, iv);
            this.accounts = Arrays.stream(loadAccounts()).collect(Collectors.toMap(p -> p.publicKeyHash, p -> p));
            this.contracts = Arrays.stream(loadContracts()).collect(Collectors.toMap(p -> p.scriptHash(), p -> p));
            this.coins = new TrackableCollection<TransactionInput, Coin>(loadCoins());
            this.current_height = ByteBuffer.wrap(loadStoredData("Height")).order(ByteOrder.LITTLE_ENDIAN).getInt();
//            checkCoinState();
        }
        Arrays.fill(passwordKey, (byte) 0);
    }
    
    public void checkCoinState() {
    	Coin[] spending = findSpendingCoins();
    	if(spending == null || spending.length == 0) {
    		return;
    	}
    	Arrays.stream(spending).filter(p -> {
    		Transaction tx = null;
    		try {
    			tx = Blockchain.current().getTransaction(p.input.prevHash);
    		} catch (Exception e) {
    			tx = null;
    		}
    		return tx == null;
    	}).forEach(p -> coins.get(p.input).setState(CoinState.Unspent));
    	Coin[] changeset = coins.getChangeSet(Coin[]::new);
    	onSaveTransaction(null, new Coin[0], changeset);
    	coins.commit();
    }
    
    /**
     * 启动同步线程
     */
    public void start() {
    	if(!isrunning) {
    		isrunning = true;
    		this.thread = new Thread(this::processBlocks);
    		this.thread.setDaemon(true);
    		this.thread.setName("Wallet.ProcessBlocks");
    		this.thread.start();
    	}
    }
    
    /**
     * 关闭同步线程
     */
    @Override
    public void close() {
        isrunning = false;
        if (thread.getState() != State.NEW) {
			try {
				thread.join();
			} catch (InterruptedException ex) {
			}
        }
    }

    
    protected Wallet(String path, String password, boolean create) throws BadPaddingException, IllegalBlockSizeException {
        this(path, AES.generateKey(password), create);
    }
    
    protected void buildDatabase() {
    }

    public static Fixed8 calculateClaimAmount(Iterable<TransactionInput> inputs) {
    	return Fixed8.ZERO;
    }

    public boolean changePassword(String password_old, String password_new) {
        byte[] passwordHash = loadStoredData("PasswordHash");
        if (!Arrays.equals(passwordHash, Digest.sha256(AES.generateKey(password_old)))) {
            return false;
        }
        byte[] passwordKey = AES.generateKey(password_new);
        try {
            saveStoredData("MasterKey", AES.encrypt(masterKey, passwordKey, iv));
            return true;
        } finally {
            Arrays.fill(passwordKey, (byte)0);
        }
    }

    /**
     * 账户/合约
     */
    public Account createAccount() {
        byte[] privateKey = ECC.generateKey();
        Account account = createAccount(privateKey);
        Arrays.fill(privateKey, (byte) 0);
        return account;
    }

    public Account createAccount(byte[] privateKey) {
        Account account = new Account(privateKey);
        synchronized (accounts) {
            accounts.put(account.publicKeyHash, account);
        }
        return account;
    }
    
    public void addContract(Contract contract) {
        synchronized (accounts) {
            if (!accounts.containsKey(contract.publicKeyHash)) {
            	throw new RuntimeException();
            }
            synchronized(contracts) {
                contracts.put(contract.scriptHash(), contract);
            }
        }
    }

    public boolean containsAccount(ECPoint publicKey) {
        return containsAccount(Program.toScriptHash(publicKey.getEncoded(true)));
    }

    public boolean containsAccount(UInt160 publicKeyHash) {
        synchronized (accounts) {
            return accounts.containsKey(publicKeyHash);
        }
    }

    public boolean containsAddress(String address) {
    	return containsAddress(Wallet.toScriptHash(address));
    }
    
    public boolean containsAddress(UInt160 scriptHash) {
        synchronized (contracts) {
            return contracts.containsKey(scriptHash);
        }
    }
    
    public boolean deleteAccount(UInt160 publicKeyHash) {
        synchronized (accounts) {
            synchronized (contracts) {
                for (Contract contract : contracts.values().stream().filter(p -> p.publicKeyHash == publicKeyHash).toArray(Contract[]::new)) {
                    deleteContract(contract.scriptHash());
                }
            }
            return accounts.remove(publicKeyHash) != null;
        }
    }
    
    public boolean deleteContract(UInt160 scriptHash) {
        synchronized (contracts) {
            synchronized (coins) {
            	Iterator<Coin> iterator = coins.iterator();
            	while (iterator.hasNext()) {
            		if (iterator.next().scriptHash.equals(scriptHash)) {
            			iterator.remove();
            		}
            	}
                coins.commit();
                return contracts.remove(scriptHash) != null;
            }
        }
    }
    
    protected byte[] decryptPrivateKey(byte[] encryptedPrivateKey) throws IllegalBlockSizeException, BadPaddingException {
        if (encryptedPrivateKey == null) {
        	throw new NullPointerException("encryptedPrivateKey");
        }
        if (encryptedPrivateKey.length != 112) { 
        	throw new IllegalArgumentException();
        }
        return AES.decrypt(encryptedPrivateKey, masterKey, iv);
    }

    protected byte[] encryptPrivateKey(byte[] decryptedPrivateKey) {
    	return AES.encrypt(decryptedPrivateKey, masterKey, iv);
    }

    /**
     * 小蚁币相关信息接口
     */
    public Coin[] findCoins() {
        synchronized (coins) {
            return coins.stream().filter(p -> p.getState() == CoinState.Unconfirmed || p.getState() == CoinState.Unspent).toArray(Coin[]::new);
        }
    }
    
    public Coin[] getCoin() {
    	return coins.stream().toArray(Coin[]::new);
    }
    
    public Coin[] findSpendingCoins() {
    	synchronized (coins) {
            return coins.stream().filter(p -> p.getState() == CoinState.Spending).toArray(Coin[]::new);
        }
    }
    
    public Coin[] findUnconfirmedCoins() {
        synchronized (coins) {
            return coins.stream().filter(p -> p.getState() == CoinState.Unconfirmed).toArray(Coin[]::new);
        }
    }

    public Coin[] findUnspentCoins() {
        synchronized (coins) {
            return coins.stream().filter(p -> p.getState() == CoinState.Unspent).toArray(Coin[]::new);
        }
    }

    public Coin[] findUnspentCoins(UInt256 asset_id, Fixed8 amount) {
        return findUnspentCoins(asset_id, amount, null);
    }
    
    public Coin[] findUnspentCoins(UInt256 asset_id, Fixed8 amount, UInt160 from) {
    	synchronized (coins) {
    		Stream<Coin> unspents = coins.stream().filter(p -> p.getState() == CoinState.Unspent);
    		if (from != null) {
    			unspents = unspents.filter(p -> p.scriptHash.equals(from));
    		}
    		return findUnspentCoins(unspents, asset_id, amount);
    	}
    }

    protected static Coin[] findUnspentCoins(Stream<Coin> unspents, UInt256 asset_id, Fixed8 amount) {
        Coin[] unspents_asset = unspents.filter(p -> p.assetId.equals(asset_id)).toArray(Coin[]::new);
        Fixed8 sum = Fixed8.sum(unspents_asset, p -> p.value);
        if (sum.compareTo(amount) < 0) throw new RuntimeException(DataFormat.getErrorDesc4NoBalance(String.format("insuficient balance, sum=%s, amount=%s", sum, amount)));
        if (sum.equals(amount)) return unspents_asset;
        Arrays.sort(unspents_asset, (a, b) -> -a.value.compareTo(b.value));
        int i = 0;
        while (unspents_asset[i].value.compareTo(amount) <= 0)
            amount = amount.subtract(unspents_asset[i++].value);
        if (amount.equals(Fixed8.ZERO)) {
            return Arrays.stream(unspents_asset).limit(i).toArray(Coin[]::new);
        } else {
        	Coin[] result = new Coin[i + 1];
        	System.arraycopy(unspents_asset, 0, result, 0, i);
        	for (int j = unspents_asset.length - 1; j >= 0; j--)
        		if (unspents_asset[j].value.compareTo(amount) >= 0) {
        			result[i] = unspents_asset[j];
        			break;
        		}
        	return result;
        }
    }
    
    public Coin[] getUnclaimedCoins() {
        synchronized (coins) {
            return coins.stream().filter(p -> p.getState() == CoinState.Spent).toArray(Coin[]::new);
        }
    }

    public Fixed8 getAvailable(UInt256 asset_id) {
        synchronized (coins) {
        	return Fixed8.sum(coins.stream().filter(p -> p.getState() == CoinState.Unspent && p.assetId.equals(asset_id)).toArray(Coin[]::new), p -> p.value);
        }
    }

    public Fixed8 getBalance(UInt256 asset_id) {
        synchronized (coins) {
        	return Fixed8.sum(coins.stream().filter(p -> (p.getState() == CoinState.Unconfirmed || p.getState() == CoinState.Unspent) && p.assetId.equals(asset_id)).toArray(Coin[]::new), p -> p.value);
        }
    }

    public UInt160 getChangeAddress() {
        synchronized (contracts) {
        	return contracts.values().stream().filter(p -> p.isStandard()).findAny().map(p -> p.scriptHash()).orElse(contracts.keySet().stream().findAny().get());
        }
    }

    /**
     * 账户相关信息接口
     */
    public Account getAccount(ECPoint publicKey) {
        return getAccount(Program.toScriptHash(publicKey.getEncoded(true)));
    }

    public Account getAccount(UInt160 publicKeyHash) {
        synchronized (accounts) {
            if (!accounts.containsKey(publicKeyHash)) 
            	return null;
            return accounts.get(publicKeyHash);
        }
    }

    public Account getAccountByScriptHash(UInt160 scriptHash) {
        synchronized (accounts) {
            synchronized (contracts) {
                if (!contracts.containsKey(scriptHash)) 
                	return null;
                return accounts.get(contracts.get(scriptHash).publicKeyHash);
            }
        }
    }

    public Account[] getAccounts() {
        synchronized (accounts) {
        	return accounts.values().toArray(new Account[accounts.size()]);
        }
    }

    /**
     * 合约相关信息接口
     */
    public Contract getContract(String address) {
    	return getContract(toScriptHash(address));
    }
    
    public Contract getContract(UInt160 scriptHash) {
        synchronized (contracts) {
            if (!contracts.containsKey(scriptHash)) 
            	return null;
            return contracts.get(scriptHash);
        }
    }
    
    public Contract[] getContracts() {
        synchronized (contracts) {
        	return contracts.values().toArray(new Contract[contracts.size()]);
        }
    }

    public Contract[] getContracts(UInt160 publicKeyHash) {	// 多签名合约:一个公钥和可以创建多个合约
        synchronized (contracts) {
        	return contracts.values().stream().filter(p -> p.publicKeyHash.equals(publicKeyHash)).toArray(Contract[]::new);
        }
    }
    
    /**
     * 地址(全称：合约地址)-账户-合约
     */
    public UInt160[] getAddresses() {
        synchronized (contracts) {
        	return contracts.keySet().toArray(new UInt160[contracts.size()]);
        }
    }
    public String getAddressByPubKeyHash(UInt160 publicKeyHash) {// 一个公钥可以创建多个合约
    	Contract[] cons = getContracts(publicKeyHash);
    	if(cons.length != 1) {
    		throw new RuntimeException("Multi address");
    	}
    	return cons[0].address();
    }

    public static byte[] getPrivateKeyFromWIF(String wif) {
        if (wif == null) throw new NullPointerException();
        byte[] data = Base58.decode(wif);
        if (data.length != 38 || data[0] != (byte)0x80 || data[33] != 0x01)
            throw new IllegalArgumentException();
        byte[] checksum = Digest.sha256(Digest.sha256(data, 0, data.length - 4));
        for (int i = 0; i < 4; i++)
        	if (data[data.length - 4 + i] != checksum[i])
        		throw new IllegalArgumentException();
        byte[] privateKey = new byte[32];
        System.arraycopy(data, 1, privateKey, 0, privateKey.length);
        Arrays.fill(data, (byte) 0);
        return privateKey;
    }

    public Account importAccount(String wif) {
        byte[] privateKey = getPrivateKeyFromWIF(wif);
        Account account = createAccount(privateKey);
        Arrays.fill(privateKey, (byte) 0);
        return account;
    }
    
    protected boolean isWalletTransaction(Transaction tx) {
    	synchronized (contracts) {
            if (Arrays.stream(tx.outputs).anyMatch(p -> contracts.containsKey(p.scriptHash))) {
                return true;
            }
            if (Arrays.stream(tx.scripts).anyMatch(p -> contracts.containsKey(Program.toScriptHash(p.parameter)))) {
                return true;
            }
        }
        return false;
    }

    protected abstract Account[] loadAccounts();

    protected abstract Contract[] loadContracts();

    protected abstract Coin[] loadCoins();

    protected abstract byte[] loadStoredData(String name);
    
    protected abstract void saveStoredData(String name, byte[] value);
    
    public Map<DNA.Core.Transaction,Integer> LoadTransactions() { return null;}
    
    public <T extends Transaction> T makeTransaction(T tx, Fixed8 fee) throws CoinException {
    	return makeTransaction(tx, fee, null);
    }

    public <T extends Transaction> T makeTransaction(T tx, Fixed8 fee, UInt160 from) throws CoinException{
        if (tx.outputs == null) throw new IllegalArgumentException("tx.output is null");
        if (tx.attributes == null) tx.attributes = new TransactionAttribute[0];
        fee = fee.add(tx.systemFee());
        Map<UInt256, Fixed8> pay_total = Arrays.stream(tx instanceof IssueTransaction ? new TransactionOutput[0] : tx.outputs).collect(Collectors.groupingBy(p -> p.assetId)).entrySet().stream().collect(Collectors.toMap(p -> p.getKey(), p -> Fixed8.sum(p.getValue().toArray(new TransactionOutput[0]), o -> o.value)));
        Map<UInt256, Coin[]> pay_coins = null;
        try {
        	pay_coins = pay_total.entrySet().stream().collect(Collectors.toMap(p -> p.getKey(), p -> findUnspentCoins(p.getKey(), p.getValue(), from)));
        } catch (CoinRuntimeException ex) {
        	throw new CoinException(ex.getMessage(), ex);
        }
        if (pay_coins.values().stream().anyMatch(p -> p == null)) throw new CoinException(DataFormat.getErrorDesc4NoBalance("insuficient balance"));
        Map<UInt256, Fixed8> input_sum = pay_coins.entrySet().stream().collect(Collectors.toMap(p -> p.getKey(), p -> Fixed8.sum(p.getValue(), c -> c.value)));
        UInt160 change_address = from == null ? getChangeAddress() : from;
        List<TransactionOutput> outputs_new = new ArrayList<TransactionOutput>(Arrays.asList(tx.outputs));
        for (Entry<UInt256, Fixed8> entry : input_sum.entrySet()) {
        	Fixed8 pay = pay_total.get(entry.getKey());
            if (entry.getValue().compareTo(pay) > 0) {
            	TransactionOutput output = new TransactionOutput();
            	output.assetId = entry.getKey();
            	output.value = entry.getValue().subtract(pay);
            	output.scriptHash = change_address;
            	outputs_new.add(output);
            }
        }
        tx.inputs = pay_coins.values().stream().flatMap(p -> Arrays.stream(p)).map(p -> p.input).toArray(TransactionInput[]::new);
        tx.outputs = outputs_new.toArray(new TransactionOutput[outputs_new.size()]);
        return tx;
    }

    protected abstract void onProcessNewBlock(Block block, Coin[] added, Coin[] changed, Coin[] deleted);	// for synchronize
    protected abstract void onSaveTransaction(Transaction tx, Coin[] added, Coin[] changed);				// for wallet

    private void processBlocks() {
        while (isrunning) {
        	Blockchain blockchain = Blockchain.current();
            while (true) {
            	int height;
            	try {
					height = blockchain == null ? 0 : blockchain.height();
				} catch (Exception ex) {
					break;
				}
            	if (current_height > height || !isrunning) {
            		break;
            	}
                synchronized (locker) {
                    Block block;
					try {
						block = blockchain.getBlock(current_height);
					} catch (Exception ex) {
						ex.printStackTrace();
						break;
					}
                    if (block != null) {
                    	processNewBlock(block);
                    }
                }
            }
            try {
	            for (int i = 0; i < 20 && isrunning; i++) {
	                Thread.sleep(100);
	            }
            } catch (InterruptedException ex) {
            	break;
            }
        }
    }
    private void processNewBlock(Block block) {
        Coin[] changeset;
        synchronized (contracts) {
            synchronized (coins) {
            	// 1. 更新内存coin
            	// tx.output
                for (Transaction tx : block.transactions) {
                    for (int index = 0; index < tx.outputs.length; ++index) {
                        TransactionOutput output = tx.outputs[index];
                        if (contracts.containsKey(output.scriptHash)) {
                            TransactionInput key = new TransactionInput();
                            key.prevHash = tx.hash();
                            key.prevIndex = (short)index;
                            if (coins.containsKey(key)) {
                                coins.get(key).setState(CoinState.Unspent); // change Unconfimed to Unspent
                            } else {
                            	Coin coin = new Coin();
                            	coin.input = key;
                            	coin.assetId = output.assetId;
                            	coin.value = output.value;
                            	coin.scriptHash = output.scriptHash;
                            	coin.setState(CoinState.Unspent);	// add unspent coin
                                coins.add(coin);
                            }  
                        }
                    }
                }
                // tx.input
                for (Transaction tx : block.transactions) {
                    for (TransactionInput input : tx.getAllInputs().toArray(TransactionInput[]::new)) {
                        if (coins.containsKey(input)) {
                        	coins.get(input).setState(CoinState.Spent);	// change spending to spent
                        }
                    }
                }
                // 2. 更新入库coin/tx/height
                changeset = coins.getChangeSet(Coin[]::new);
                Coin[] added = Arrays.stream(changeset).filter(p -> p.getTrackState() == TrackState.Added).toArray(Coin[]::new);
                Coin[] changed = Arrays.stream(changeset).filter(p -> p.getTrackState() == TrackState.Changed).toArray(Coin[]::new);
                Coin[] deleted = Arrays.stream(changeset).filter(p -> p.getTrackState() == TrackState.Deleted).toArray(Coin[]::new);
                onProcessNewBlock(block, added, changed, deleted);
                coins.commit();
                current_height++;
            }
        }
    }

	public void rebuild() {
        synchronized (locker) {
            synchronized (coins) {
                coins.clear();
                coins.commit();
                current_height = 1;
            }
        }
    }

	/**
	 * UserWallet发送交易前，存储Tx
	 * @param tx
	 * @return
	 */
    public boolean saveTransaction(Transaction tx) {
        Coin[] changeset;
        synchronized (contracts) {
            synchronized (coins) {
                if (tx.getAllInputs().anyMatch(p -> !coins.containsKey(p) 
                		|| coins.get(p).getState() != CoinState.Unspent)) {
                    return false;
                }
                // 更新内存coin
                for (TransactionInput input : tx.getAllInputs().toArray(TransactionInput[]::new)) {
                    coins.get(input).setState(CoinState.Spending);
                }
                for (int i = 0; i < tx.outputs.length; i++) {
                    if (contracts.containsKey(tx.outputs[i].scriptHash)) {
                    	Coin coin = new Coin();
                    	coin.input = new TransactionInput();
                    	coin.input.prevHash = tx.hash();
                    	coin.input.prevIndex = (short)i;
                    	coin.assetId = tx.outputs[i].assetId;
                    	coin.value = tx.outputs[i].value;
                    	coin.scriptHash = tx.outputs[i].scriptHash;
                    	coin.setState(CoinState.Unconfirmed);
                    	coins.add(coin);
                    }
                }
                // 更新入库coin/tx
                changeset = coins.getChangeSet(Coin[]::new);
                if (changeset.length > 0) {
                	Coin[] added = Arrays.stream(changeset).filter(p -> p.getTrackState() == TrackState.Added).toArray(Coin[]::new);
                	Coin[] changed = Arrays.stream(changeset).filter(p -> p.getTrackState() == TrackState.Changed).toArray(Coin[]::new);
                    onSaveTransaction(tx, added, changed);
                    coins.commit();
                }
            }
        }
        return true;
    }

    public boolean sign(SignatureContext context) {
        boolean fSuccess = false;
        for (UInt160 scriptHash : context.scriptHashes) {
            Contract contract = getContract(scriptHash);
            if (contract == null) {
            	continue;
            }
            Account account = getAccountByScriptHash(scriptHash);
            if (account == null) {
            	continue;
            }
            byte[] signature = context.signable.sign(account);
            fSuccess |= context.add(contract, account.publicKey, signature);
        }
        return fSuccess;
    }

    public static String toAddress(UInt160 scriptHash) {
    	byte[] data = new byte[25];
    	data[0] = COIN_VERSION;
    	System.arraycopy(scriptHash.toArray(), 0, data, 1, 20);
    	byte[] checksum = Digest.sha256(Digest.sha256(data, 0, 21));
    	System.arraycopy(checksum, 0, data, 21, 4);
        return Base58.encode(data);
    }

    public static UInt160 toScriptHash(String address) {
        byte[] data = Base58.decode(address);
        if (data.length != 25) {
            throw new IllegalArgumentException();
        }
        if (data[0] != COIN_VERSION) {
            throw new IllegalArgumentException();
        }
        byte[] checksum = Digest.sha256(Digest.sha256(data, 0, 21));
        for (int i = 0; i < 4; i++) {
        	if (data[data.length - 4 + i] != checksum[i]) {
        		throw new IllegalArgumentException();
        	}
        }
        byte[] buffer = new byte[20];
        System.arraycopy(data, 1, buffer, 0, 20);
        return new UInt160(buffer);
    }
    
    public String dbPath() {
    	return path;
    }
    
    
    public int getBlockHeight(){
    	try {
			return Blockchain.current().height();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
    }
	public int getWalletHeight() {
		return current_height;
	}
	
    static Block from(String ss) {
    	try {
			return Serializable.from(DNA.Helper.hexToBytes(ss), Block.class);
		} catch (InstantiationException | IllegalAccessException e) {
			throw new RestRuntimeException("Block.deserialize(height) failed", e);
		}
    }
}
