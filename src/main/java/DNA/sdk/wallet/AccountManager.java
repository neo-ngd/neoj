package DNA.sdk.wallet;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import DNA.Fixed8;
import DNA.Helper;
import DNA.UInt160;
import DNA.UInt256;
import DNA.Core.AssetType;
import DNA.Core.Blockchain;
import DNA.Core.DestroyTransaction;
import DNA.Core.IssueTransaction;
import DNA.Core.RecordTransaction;
import DNA.Core.RecordType;
import DNA.Core.RegisterTransaction;
import DNA.Core.SignatureContext;
import DNA.Core.Transaction;
import DNA.Core.TransactionAttribute;
import DNA.Core.TransactionAttributeUsage;
import DNA.Core.TransactionInput;
import DNA.Core.TransactionOutput;
import DNA.Core.TransferTransaction;
import DNA.Core.Scripts.Program;
import DNA.Cryptography.ECC;
import DNA.Implementations.Blockchains.Rest.RestBlockchain;
import DNA.Implementations.Wallets.IUserManager;
import DNA.Implementations.Wallets.SQLite.UserWallet;
import DNA.Network.Rest.RestException;
import DNA.Network.Rest.RestNode;
import DNA.Wallets.Account;
import DNA.Wallets.Coin;
import DNA.Wallets.Contract;
import DNA.Wallets.Wallet;
import DNA.sdk.helper.OnChainSDKHelper;
import DNA.sdk.info.account.AccountAsset;
import DNA.sdk.info.account.AccountInfo;
import DNA.sdk.info.account.Asset;
import DNA.sdk.info.asset.AssetInfo;
import DNA.sdk.info.mutil.TxJoiner;
import DNA.sdk.info.transaction.TransactionInfo;
import DNA.sdk.info.transaction.TxInputInfo;
import DNA.sdk.info.transaction.TxOutputInfo;

import com.alibaba.fastjson.JSON;


/**
 * 账户管理器
 * 
 * 1. 账户类操作
 * 创建账户/查询账户
 * 
 * 2. 资产类操作
 * 注册资产/分发资产/转移资产/注销资产
 * 
 * 3. 存证取证
 * 
 * 4. 查询类操作
 * 查询账户信息
 * 查询账户资产
 * 查询资产信息
 * 查询交易信息
 * 
 */
public class AccountManager {
	private IUserManager uw;
	private RestNode restNode;
	private boolean isWaitSync = true;
	
	public void setWaitSync(boolean isWaitSync) {
		this.isWaitSync = isWaitSync;
	}
	
	public static void setPKGenerateAlgorithm(String algorithm) {
		// ECC or SM2
	}
	
	public static void main(String[] args) {
		
	}
	
	public static AccountManager getWallet(String path, String url, String accessToken) {
		AccountManager wm = new AccountManager();
		wm.initBlockRestNode(url, accessToken);
		wm.initRestNode(url, accessToken);
		wm.initWallet(path);
		return wm;
	}
	
	
	public static AccountManager getWallet(String path, String password, String url, String accessToken) {
		AccountManager wm = new AccountManager();
		wm.initBlockRestNode(url, accessToken);
		wm.initRestNode(url, accessToken);
		wm.initWallet(path);
		return wm;
	}
	
	public static AccountManager getWallet(String url, String accessToken) {
		AccountManager wm = new AccountManager();
		wm.initRestNode(url, accessToken);
		return wm;
	}
	
	public static AccountManager getWallet(String path) {
		AccountManager wm = new AccountManager();
		wm.initWallet(path);
		return wm;
	}
	
	private AccountManager() {
	}
	
	private void initBlockRestNode(String url, String token) {
		Blockchain.register(new RestBlockchain(new RestNode(url, token)));
	}
	
	private void initRestNode(String url, String token) {
		this.restNode = new RestNode(url, token);
	}
	
	public void setRestNode(String url, String token) {
		this.restNode = new RestNode(url, token);
	}
	
	private void initWallet(String path) {
		initWallet(path, "0x123456");
	}
	private void initWallet(String path, String password) {
		if(new File(path).exists() && new File(path).isFile()) {
			uw = UserWallet.open(path, password);
		} else {
			uw = UserWallet.create(path, password);
		}
	}
	
	/**
	 * 启动同步线程
	 */
	public void startSyncBlock() {
		uw.start();
	}
	
	public void stopSyncBlock() {
		uw.close();
	}
	
	public boolean hasFinishedSyncBlock() {
		try {
			return uw.hasFinishedSyncBlock();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public int getBlockHeight() {
		return uw.getBlockHeight();
	}
	public int getWalletHeight() {
		return uw.getWalletHeight();
	}
	
	public void rebuild() {
		uw.rebuild();
	}
	
	/**
	 * 更新访问令牌
	 * 
	 * @param accessToken
	 */
	public void updateToken(String accessToken) {
		restNode.setAccessToken(accessToken);
	}
	
	
	/**
	 * 创建单个账户
	 */
	public String createAccount() {
		return createAddress();
	}
	
	/**
	 * 创建多个账户
	 */
	public List<String> createAccount(int n) {
		return Stream.generate(() -> createAddress()).limit(n).collect(Collectors.toList());
	}
	// 
	private String createAddress() {
		return uw.getContract(Contract.createSignatureContract(uw.createAccount().publicKey).address()).address();
	}
	
	/**
	 * 根据私钥创建账户
	 * 
	 * @param prikey
	 * @return
	 */
	public String createAccountsFromPrivateKey(String prikey) {
		Account acc = uw.createAccount(Helper.hexToBytes(prikey));
		return uw.getContract(Contract.createSignatureContract(acc.publicKey).address()).address();
	}
	
	/**
	 * 导出所有账户地址
	 * 
	 * @return
	 */
	public List<String> listAccount() {
		return Arrays.stream(uw.getContracts()).map(p -> p.address()).collect(Collectors.toList());
	}
	
	public String address2UInt160(String address) {
		return Wallet.toScriptHash(address).toString();
	}
	
	public String uint1602Address(String uint160) {
		return Wallet.toAddress(UInt160.parse(uint160));
	}
	
	public void setAuthType(String authType) {
		this.restNode.setAuthType(authType);
	}
	
	public void setAccessToken(String accessToken) {
		this.restNode.setAccessToken(accessToken);
	}
	
	/**
	 * 注册资产
	 * @throws Exception 
	 */
	public String reg() throws Exception {// in-null,out-null
		throw new UnsupportedOperationException();
	}
	/**
	 * 注册资产
	 * 
	 * @param issuer	资产发行者地址
	 * @param name		资产名称
	 * @param amount	资产数量
	 * @param desc		描述
	 * @param controller 资产控制者地址
	 * @return	交易编号
	 * @throws Exception
	 */
	public String reg(String issuer, String name, long amount, String desc, String controller) throws Exception {
		return reg(issuer, name, amount, desc, controller, 8);
	}
	public String reg(String issuer, String name, long amount, String desc, String controller, int precision) throws Exception {
		return regToken(issuer, name, amount, desc, controller, precision);
	}
	/**
	 * 注册Token资产
	 * 
	 * @param issuer	资产发行者地址
	 * @param name		资产名称
	 * @param amount	资产数量
	 * @param desc		描述
	 * @param controller 资产控制者地址
	 * @param precision	 精度
	 * @return	交易编号
	 * @throws Exception
	 */
	public String regToken(String issuer, String name, long amount, String desc, String controller, int precision) throws Exception {
		return regToken(getAccount(issuer), name, amount, desc, controller, precision);
	}
	public String regToken(Account acc, String assetName, long assetAmount, String txDesc, String controller, int precision) throws Exception {
		return reg(getRegTx(acc, assetName, assetAmount, txDesc, AssetType.Token, controller, precision));
	}
	/**
	 * 注册Share资产
	 * 
	 * @param issuer	资产发行者地址
	 * @param name		资产名称
	 * @param amount	资产数量
	 * @param desc		描述
	 * @param controller 资产控制者地址
	 * @param precision	 精度
	 * @return	交易编号
	 * @throws Exception
	 */
	public String regShare(String issuer, String name, long amount, String desc, String controller, int precision) throws Exception {
		return regShare(getAccount(issuer), name, amount, desc, controller, precision);
	}
	public String regShare(Account acc, String assetName, long assetAmount, String txDesc, String controller, int precision) throws Exception {
		return reg(getRegTx(acc, assetName, assetAmount, txDesc, AssetType.Share, controller, precision));
	}
	private String reg(RegisterTransaction regTx) throws Exception {
		RegisterTransaction signedTx4Reg = uw.makeTransaction(regTx, Fixed8.ZERO);
		SignatureContext context = new SignatureContext(signedTx4Reg);
		boolean f1 = uw.sign(context);
		if(context.isCompleted()){
			signedTx4Reg.scripts = context.getScripts();
		}
		String txHex = Helper.toHexString(signedTx4Reg.toArray());
		boolean f2 = restNode.sendRawTransaction(txHex);
		String txid = signedTx4Reg.hash().toString();
		print("send reg tx.sign:"+f1+",rst:"+f2+",txid:"+ txid);
//		if(f2 && isWaitSync) {
//			uw.saveTransaction(signedTx4Reg);
//			wait(uw,txid); // 等待生效
//		}
		Thread.sleep(1000*6);
		return txid;
	}
	
	/**
	 * 分发资产
	 * @throws Exception 
	 */
	public String iss() {//in-null
		throw new UnsupportedOperationException();
	}
	/**
	 * 分发资产
	 * 
	 * @param sendAddr	资产控制者地址
	 * @param assetid	资产编号
	 * @param amount	资产数量
	 * @param recvAddr	接收者地址
	 * @param desc		描述
	 * @return	交易编号
	 * @throws Exception
	 */
	public String iss(String sendAddr, String assetid, long amount, String recvAddr, String desc) throws Exception {
		return iss(getIssTx(assetid, amount, recvAddr, desc), getAddress(sendAddr));
	}
	private String iss(IssueTransaction issueTx, UInt160 from) throws Exception {
		IssueTransaction signedTx4Iss = uw.makeTransaction(issueTx, Fixed8.ZERO, from);
		SignatureContext context4Iss = new SignatureContext(signedTx4Iss);
		boolean f3 = uw.sign(context4Iss);
		if(context4Iss.isCompleted()){
			signedTx4Iss.scripts = context4Iss.getScripts();
		}
		uw.saveTransaction(signedTx4Iss);
		String txHex = Helper.toHexString(signedTx4Iss.toArray());;
		boolean f4 = restNode.sendRawTransaction(txHex);
		
		String txid4Iss = signedTx4Iss.hash().toString();
		print("send iss tx.sign:"+f3+",rst:"+f4+",txid:"+ txid4Iss);
		if(f4 && isWaitSync) {
			wait(uw,txid4Iss); // 等待生效
		}
		return txid4Iss;
	}
	/**
	 * 分发给多个接收者
	 * 
	 * @param recvlist	接收者列表
	 * @param sendAddr	发送者地址
	 * @param desc		描述
	 * @return
	 * @throws Exception
	 */
	public String iss(List<TxJoiner> recvlist, String sendAddr, String desc) throws Exception {
		return iss(getIssTx(recvlist, desc), getAddress(sendAddr));
	}
	
	/**
	 * 转账
	 */
	public String trf() {
		throw new UnsupportedOperationException();
	}
	/**
	 * 转移资产
	 * 
	 * @param sendAddr	资产控制者地址
	 * @param assetid	资产编号
	 * @param amount	资产数量
	 * @param recvAddr	接收者地址
	 * @param desc		描述
	 * @return	交易编号
	 * @throws Exception
	 */
	public String trf(String sendAddr, String assetid, long amount, String recvAddr, String desc) throws Exception {
		return trf(getTrfTx(assetid, amount, recvAddr, desc), getAddress(sendAddr));
	}
	private String trf(TransferTransaction trfTx, UInt160 from) throws Exception {
		TransferTransaction signedTx4Trf = uw.makeTransaction(trfTx, Fixed8.ZERO, from);
		SignatureContext context4Trf = new SignatureContext(signedTx4Trf);
		boolean f5 = uw.sign(context4Trf);
		if(context4Trf.isCompleted()){
			signedTx4Trf.scripts = context4Trf.getScripts();
		}
		uw.saveTransaction(signedTx4Trf);
		String txHex = Helper.toHexString(signedTx4Trf.toArray());;
		boolean f6 = restNode.sendRawTransaction(txHex);
		
		String txid4Trf = signedTx4Trf.hash().toString();
		print("send trf tx.sign:"+f5+",rst:"+f6+",txid:"+ txid4Trf);
		if(f6 && isWaitSync) {
			wait(uw,txid4Trf); // 等待生效
		}
		return txid4Trf;
	}
	/**
	 * 转账给多个接收者
	 * 
	 * @param recvlist	接收者列表
	 * @param sendAddr	发送者地址
	 * @param desc		描述
	 * @return
	 * @throws Exception
	 */
	public String trf(List<TxJoiner> recvlist, String sendAddr, String desc) throws Exception {
		return trf(getTrfTx(recvlist, desc), getAddress(sendAddr));
	}
	
	/**
	 * 注销资产
	 * 
	 * @param issuer	资产发行者
	 * @param assetId	资产编号
	 * @param txDesc	描述
	 * @return
	 * @throws Exception 
	 */
	public String des(String issuer, String assetId, String txDesc) throws Exception {
		return des(getDesTx(issuer, assetId, txDesc), null);
	}
	public String des(DestroyTransaction desTx, UInt160 from) throws Exception {
		DestroyTransaction signedTx4Des = desTx;//uw.makeTransaction(trfTx, Fixed8.ZERO, from);
		SignatureContext context4Trf = new SignatureContext(signedTx4Des);
		boolean f7 = uw.sign(context4Trf);
		if(context4Trf.isCompleted()){
			signedTx4Des.scripts = context4Trf.getScripts();
		}
		uw.saveTransaction(signedTx4Des);
		String txHex = Helper.toHexString(signedTx4Des.toArray());;
		OnChainSDKHelper.printTransaction(signedTx4Des);
		System.out.println("tx.a:"+Helper.getbyteStr(Helper.hexToBytes(txHex)));
		System.out.println("tx.s:"+txHex);
		System.out.println("txHex:"+txHex);
		boolean f8 = false;//restNode.sendRawTransaction(txHex);
		
		String txid4Des = signedTx4Des.hash().toString();
		print("send des tx.sign:"+f7+",rst:"+f8+",txid:"+ txid4Des);
		if(f7 && isWaitSync) {
			wait(uw,txid4Des); // 等待生效
		}
		return txid4Des;
	}
	private DestroyTransaction getDesTx(String issuer, String assetId, String txDesc) {
		DestroyTransaction tx = new DestroyTransaction();
		tx.inputs = Arrays.stream(uw.getCoin()).filter(p -> Wallet.toAddress(p.scriptHash).equals(issuer)).filter(p -> p.assetId.toString().equals(assetId)).map(p -> p.input).toArray(TransactionInput[]::new);
		tx.outputs = new TransactionOutput[0];
		if(txDesc != null && txDesc.length() > 0) {
			tx.attributes = new TransactionAttribute[1];
			tx.attributes[0] = new TransactionAttribute();
			tx.attributes[0].usage = TransactionAttributeUsage.Description;
			tx.attributes[0].data = (txDesc+new Date().toString()).getBytes();
		} else {
			tx.attributes = new TransactionAttribute[0];
		}
		return tx;
	}
	
	/**
	 * 存证
	 * 
	 * @param data	存证内容
	 * @param desc	描述
	 * @return	交易编号
	 * @throws Exception
	 */
	public String storeCert(String data, String desc) throws Exception {
		RecordTransaction tx = getRcdTx(data, desc);
		String txHex = Helper.toHexString(tx.toArray());;
		boolean f = restNode.sendRawTransaction(txHex);
		
		String txid = tx.hash().toString();
		print("rcd.sign:null, rst:"+f+",txid:"+txid);
		return txid;
	}
	/**
	 * 取证
	 * 
	 * @param txid	交易编号
	 * @return	存证内容
	 * @throws Exception
	 */
	public String queryCert(String txid) throws Exception {
		Transaction tx = restNode.getRawTransaction(txid);
		if(tx instanceof RecordTransaction) {
			RecordTransaction rr = (RecordTransaction) tx;
			return new String(rr.recordData);
		}
		return null;
	}
	
	
	// 获取账户
	public Account getAccount(String address) {
		return uw.getAccount(uw.getContract(address).publicKeyHash);
	}
	// 获取地址
	private UInt160 getAddress(String address) {
		return Wallet.toScriptHash(address);
	}
	
	/**
	 * 等待该笔交易同步至账户管理器中
	 * 
	 * @param txid
	 */
	public void wait(String txid) {
		wait(uw, txid);
	}
	// 等待Tx生效
	private void wait(IUserManager uw, String txid) {
		int count = 3;	// 最长等待1分钟
		while(--count > 0) {
			Map<Transaction, Integer> txs = uw.LoadTransactions();
			if(txs != null && txs.keySet().stream().filter(p -> txs.get(p).intValue() > 1).filter(p -> p.hash().toString().equals(txid)).count() == 1) {
				print("sync finish, txid:"+txid);
				return;
			}
			try {
				Thread.sleep(1000*5);
			} catch (InterruptedException e) {
			}
			print("sleep.....5s");
		}
		print("sync timeout,txid:"+txid);
	}
	
	private RegisterTransaction getRegTx(Account acc, String assetName, long assetAmount, String txDesc, AssetType assetType, String controller, int precision) {
		RegisterTransaction tx = new RegisterTransaction();
		
		tx.precision = (byte) precision;						// 精度
		tx.assetType = AssetType.Token;			// 资产类型
		tx.recordType = RecordType.UTXO;			// 记账模式
		tx.nonce = (int)Math.random()*10;		// 随机数
		
		tx.assetType = assetType ;	
		tx.name = assetName;	
		tx.description = txDesc;
		tx.amount = Fixed8.parse(String.valueOf(assetAmount));	
		tx.issuer = acc.publicKey;	
		tx.admin = Wallet.toScriptHash(controller); 
		tx.outputs = new TransactionOutput[0];
		if(txDesc != null && txDesc.length() > 0) {
			tx.attributes = new TransactionAttribute[1];
			tx.attributes[0] = new TransactionAttribute();
			tx.attributes[0].usage = TransactionAttributeUsage.Description;
			tx.attributes[0].data = toAttr(txDesc);
		}
		return tx;
	}
	private byte[] generateKey64Bit() {
		return ECC.generateKey(64);
	}
	private IssueTransaction getIssTx(String assetId, long assetAmount, String recvAddr, String txDesc) {
		IssueTransaction tx = new IssueTransaction();
		tx.outputs = new TransactionOutput[1];
		tx.outputs[0] = new TransactionOutput();
		tx.outputs[0].assetId = UInt256.parse(assetId);
		tx.outputs[0].value = Fixed8.parse(String.valueOf(assetAmount));
		tx.outputs[0].scriptHash = Wallet.toScriptHash(recvAddr);
		int len = txDesc != null && txDesc.length() > 0 ? 2:1;
		tx.attributes = new TransactionAttribute[len];
		tx.attributes[0] = new TransactionAttribute();
		tx.attributes[0].usage = TransactionAttributeUsage.Description;
		tx.attributes[0].data = generateKey64Bit();	// 标识区分不同txid
		for(int i=1; i<len; ++i) {
			tx.attributes[i] = new TransactionAttribute();
			tx.attributes[i].usage = TransactionAttributeUsage.Description;
			tx.attributes[i].data = toAttr(txDesc);
		}
		return tx;
	}
	private IssueTransaction getIssTx(List<TxJoiner> recvlist, String txDesc) {
		int size = recvlist.size();
		IssueTransaction tx = new IssueTransaction();
		tx.outputs = new TransactionOutput[1];
		tx.outputs = new TransactionOutput[size];
		for(int i=0; i<size; ++i) {
			TxJoiner recv = recvlist.get(i);
			tx.outputs[i] = new TransactionOutput();
			tx.outputs[i].assetId = UInt256.parse(recv.assetid);
			tx.outputs[i].value = Fixed8.parse(String.valueOf(recv.value));
			tx.outputs[i].scriptHash = Wallet.toScriptHash(recv.address);
		}
		int len = txDesc != null && txDesc.length() > 0 ? 2:1;
		tx.attributes = new TransactionAttribute[len];
		tx.attributes[0] = new TransactionAttribute();
		tx.attributes[0].usage = TransactionAttributeUsage.Description;
		tx.attributes[0].data = generateKey64Bit();	// 标识区分不同txid
		for(int i=1; i<len; ++i) {
			tx.attributes[i] = new TransactionAttribute();
			tx.attributes[i].usage = TransactionAttributeUsage.Description;
			tx.attributes[i].data = toAttr(txDesc);
		}
		return tx;
	}
	
	private TransferTransaction getTrfTx(String assetId, long assetAmount, String recvAddr, String txDesc) {
		TransferTransaction tx = new TransferTransaction();
		tx.outputs = new TransactionOutput[1];
		tx.outputs[0] = new TransactionOutput();
		tx.outputs[0].assetId = UInt256.parse(assetId);
		tx.outputs[0].value = Fixed8.parse(String.valueOf(assetAmount));
		tx.outputs[0].scriptHash = Wallet.toScriptHash(recvAddr);
		if(txDesc != null && txDesc.length() > 0) {
			tx.attributes = new TransactionAttribute[1];
			tx.attributes[0] = new TransactionAttribute();
			tx.attributes[0].usage = TransactionAttributeUsage.Description;
			tx.attributes[0].data = toAttr(txDesc);
		} else {
			tx.attributes = new TransactionAttribute[0];
		}
		return tx;
	}
	
	private TransferTransaction getTrfTx(List<TxJoiner> recvList, String txDesc) {
		int size = recvList.size();
		TransferTransaction tx = new TransferTransaction();
		tx.outputs = new TransactionOutput[size];
		for(int i=0; i<size; ++i) {
			TxJoiner recv = recvList.get(i);
			tx.outputs[i] = new TransactionOutput();
			tx.outputs[i].assetId = UInt256.parse(recv.assetid);
			tx.outputs[i].value = Fixed8.parse(String.valueOf(recv.value));
			tx.outputs[i].scriptHash = Wallet.toScriptHash(recv.address);
		}
		if(txDesc != null && txDesc.length() > 0) {
			tx.attributes = new TransactionAttribute[1];
			tx.attributes[0] = new TransactionAttribute();
			tx.attributes[0].usage = TransactionAttributeUsage.Description;
			tx.attributes[0].data = toAttr(txDesc);
		} else {
			tx.attributes = new TransactionAttribute[0];
		}
		return tx;
	}
	
	private RecordTransaction getRcdTx(String data, String txDesc) {
		RecordTransaction rcdTx = new RecordTransaction();
		rcdTx.recordType = "";
		rcdTx.recordData = data.getBytes();
		rcdTx.outputs = new TransactionOutput[0];
		rcdTx.inputs = new TransactionInput[0];
		rcdTx.attributes = new TransactionAttribute[0];
		if(txDesc != null && txDesc.length() > 0) {
			rcdTx.attributes = new TransactionAttribute[1];
			rcdTx.attributes[0] = new TransactionAttribute();
			rcdTx.attributes[0].usage = TransactionAttributeUsage.Description;
			rcdTx.attributes[0].data = toAttr(txDesc);
		}  else {
			rcdTx.attributes = new TransactionAttribute[0];
		}
		rcdTx.scripts = new Program[0];
		return rcdTx;
	}
	
	/**
	 * 获取账户信息
	 * 
	 * @param address
	 * @return
	 */
	public AccountInfo getAccountInfo(String address) {
		AccountInfo info = new AccountInfo();
		Contract con = uw.getContract(address);
		Account acc = uw.getAccountByScriptHash(Wallet.toScriptHash(address));
		info.address = con.address();
		info.pubkey = Helper.toHexString(acc.publicKey.getEncoded(true));
		info.prikey = Helper.toHexString(acc.privateKey);
		info.priwif = acc.export();
		info.pkhash = acc.publicKeyHash.toString();
		return info;
	}

	/**
	 * 获取账户资产
	 * 
	 * @param address
	 * @return
	 */
	public AccountAsset getAccountAsset(String address) {
		AccountAsset asset = new AccountAsset();
		Contract con = uw.getContract(address);
		asset.address = con.address();
		asset.canUseAssets = new ArrayList<Asset>();
		asset.freezeAssets = new ArrayList<Asset>();
		Arrays.stream(uw.findUnspentCoins()).filter(p -> address.equals(Wallet.toAddress(p.scriptHash))).forEach(p -> {
			Asset as = new Asset();
			as.assetid = p.assetId.toString();
			as.amount = p.value.toLong();
			asset.canUseAssets.add(as);
		});
		Arrays.stream(uw.findUnconfirmedCoins()).filter(p -> address.equals(Wallet.toAddress(p.scriptHash))).forEach(p -> {
			Asset as = new Asset();
			as.assetid = p.assetId.toString();
			as.amount = p.value.toLong();
			asset.freezeAssets.add(as);
		});
		return asset;
	}
	public void print() {
		Arrays.stream(uw.getCoin()).forEach(p -> {
			System.out.println("-----------------------------------------------");
			System.out.println("coin.input.prevHash:"+p.input.prevHash.toString());
			System.out.println("coin.input.prevIndex:"+p.input.prevIndex);
			System.out.println("coin.assetid:"+p.assetId.toString());
			System.out.println("coin.value:"+p.value);
			System.out.println("coin.scripthash:"+Wallet.toAddress(p.scriptHash));
			System.out.println("coin.state:"+p.getState());
		});
	}
	public Coin[] getCoin() {
		return uw.getCoin();
	}
	
	/**
	 * 获取资产信息
	 * 
	 * @param assetid
	 * @return
	 * @throws RestException
	 */
	public AssetInfo getAssetInfo(String assetid) throws RestException {
		String ss = restNode.getAsset(assetid);
		return JSON.parseObject(ss, AssetInfo.class);
	}
	
	/**
	 * 获取交易信息
	 * 
	 * @param txid
	 * @return
	 * @throws RestException
	 */
	public TransactionInfo getTransactionInfo(String txid) throws RestException {
		TransactionInfo info = new TransactionInfo();
		info.txid = txid;
		Transaction tx = restNode.getRawTransaction(txid);
		if(tx instanceof RegisterTransaction) {
			info.type = RegisterTransaction.class.getSimpleName();
		} else if(tx instanceof IssueTransaction) {
			info.type = IssueTransaction.class.getSimpleName();
		} else if(tx instanceof TransferTransaction) {
			info.type = TransferTransaction.class.getSimpleName();
		} else if(tx instanceof RecordTransaction) {
			info.type = RecordTransaction.class.getSimpleName();
		}
		
		info.inputs = new ArrayList<TxInputInfo>();
		Arrays.stream(tx.inputs).map(p -> getTxByNextTxInput(p)).forEach(p -> {
			TxInputInfo in = new TxInputInfo();
			in.address = Wallet.toAddress(p.scriptHash);
			in.assetid = p.assetId.toString();
			in.amount = p.value.toLong();
			info.inputs.add(in);
		});
		info.outputs = new ArrayList<TxOutputInfo>();
		Arrays.stream(tx.outputs).forEach(p -> {
			TxOutputInfo out = new TxOutputInfo();
			out.address = Wallet.toAddress(p.scriptHash);
			out.assetid = p.assetId.toString();
			out.amount = p.value.toLong();
			info.outputs.add(out);
		});
		StringBuilder sb = new StringBuilder();
		for(TransactionAttribute attr: tx.attributes) {
			sb.append(Helper.toHexString(attr.data));
		}
		if(sb.toString().length() > 0) {
			info.attrs = new String(Helper.hexToBytes(sb.toString()));
		}
		return info;
	}
	private TransactionOutput getTxByNextTxInput(TransactionInput input){
		Transaction tx;
		try {
			tx = restNode.getRawTransaction(input.prevHash.toString());
		} catch (RestException e) {
			throw new RuntimeException("Not find tx by next txInput");
		}
		return tx.outputs[input.prevIndex];
	}
	
	public String getStateUpdate(String namespace, String key) {
		try {
			return restNode.getStateUpdate(namespace, key);
		} catch (RestException e) {
			throw new RuntimeException(e);
		}
	}
	
	public static void print(String ss) {
		System.out.println(now() + ss);
	}
	private byte[] toAttr(String txDesc) {
		return (now() + " " + txDesc).getBytes();
	}
	public static String now() {
		return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date()) + " ";
	}
}
