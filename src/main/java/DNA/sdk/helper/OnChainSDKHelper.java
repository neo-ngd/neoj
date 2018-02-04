package DNA.sdk.helper;

import java.util.Arrays;
import java.util.Date;

import org.bouncycastle.math.ec.ECPoint;

import DNA.UInt256;
import DNA.Core.Block;
import DNA.Core.Blockchain;
import DNA.Core.RecordTransaction;
import DNA.Core.RegisterTransaction;
import DNA.Core.Transaction;
import DNA.Core.TransactionAttribute;
import DNA.Core.TransactionInput;
import DNA.Core.TransactionOutput;
import DNA.Core.Scripts.Program;
import DNA.IO.Caching.TrackableCollection;
import DNA.Implementations.Wallets.SQLite.UserWallet;
import DNA.Wallets.Account;
import DNA.Wallets.Coin;
import DNA.Wallets.Contract;
import DNA.Wallets.Wallet;

/**
 * SDK帮助类
 * 
 * @author 12146
 *
 */
public class OnChainSDKHelper {
	
	public static String byte2str(byte[] bt) {
		if(bt == null || bt.length == 0) {
			return "";
		}
		StringBuilder sb = new StringBuilder();
		for(int i=0; i<bt.length; ++i) {
			sb.append(",").append(Byte.toUnsignedInt(bt[i]));
		}
		return "len="+bt.length+",dat="+sb.substring(1).toString();
	}
	
	public static byte[] hexToBytes(String value) {
        if (value == null || value.length() == 0)
            return new byte[0];
        if (value.length() % 2 == 1)
            throw new IllegalArgumentException();
        byte[] result = new byte[value.length() / 2];
        for (int i = 0; i < result.length; i++)
            result[i] = (byte) Integer.parseInt(value.substring(i * 2, i * 2 + 2), 16);
        return result;
    }
	public static String toHexString(byte[] value) {
        StringBuilder sb = new StringBuilder();
        for (byte b : value) {
            int v = Byte.toUnsignedInt(b);
            sb.append(Integer.toHexString(v >>> 4));
            sb.append(Integer.toHexString(v & 0x0f));
        }
        return sb.toString();
    }
	public static String toHexString2(byte[] bs) {
		StringBuilder sb2 = new StringBuilder();
		for(byte b: bs) {
			int v = Byte.toUnsignedInt(b);
			sb2.append(Integer.toHexString(v));
		}
		print(sb2.toString());
		
		return sb2.toString();
	}
	
	public static void printBlockByHeight(int height) throws Exception {
		print(getBlock(height));
	}
	public static void printBlockByHash(String blockhash) throws Exception {
		print(getBlock(blockhash));
	}
	public static void printBlockByHash(UInt256 blockhash) throws Exception {
		print(getBlock(blockhash));
	}
	public static void printBlock(Block blk) {
		print(blk);
	}
	public static void printBlock2(Block blk) {
		print2(blk);
	}
	private static Block getBlock(int height) throws Exception {
		return Blockchain.current().getBlock(height);
	}
	private static Block getBlock(String blockhash) throws Exception {
		return getBlock(UInt256.parse(blockhash));
	}
	private static Block getBlock(UInt256 hash) throws Exception {
		return Blockchain.current().getBlock(hash);
	}
	
	
	public static void printTransactionByTxid(String txid) throws Exception {
		print(getTransaction(txid));
	}
	public static void printTransactionByTxid(UInt256 txid) throws Exception {
		print(getTransaction(txid));
	}
	public static void printTransaction(Transaction tx) {
		print(tx);
	}
	private static Transaction getTransaction(String txid) throws Exception {
		return getTransaction(UInt256.parse(txid));
	}
	private static Transaction getTransaction(UInt256 txid) throws Exception {
		return Blockchain.current().getTransaction(txid);
	}
	
	
	private static void print(Transaction tx) {
		print("\ttype:"+tx.type);
		if(tx.attributes != null) {
		print("\tattrs:"+tx.attributes.length);
		for(TransactionAttribute txAttr: tx.attributes) {
			print("\t\tattr.usage:"+txAttr.usage);
			print("\t\tattr.toStr:"+txAttr.toString()+"\n\t\t    newStr:"+new String(txAttr.data));
		}
		}
		if(tx.inputs != null) {
		print("\tinputs:"+tx.inputs.length);
		for(TransactionInput in: tx.inputs) {
			print("\t\tinput.prevHash:"+byte2str(in.prevHash.toArray()));
			print("\t\tinput.prevHash:"+in.prevHash.toString());
			print("\t\tinput.prevIndx:"+in.prevIndex);
		}
		}
		if(tx.outputs != null) {
		print("\toutputs:"+tx.outputs.length);
		for(TransactionOutput out: tx.outputs) {
			print("\t\tout.assetId:"+out.assetId);
			print("\t\tout.scriptHash:"+out.scriptHash);
			print("\t\tout.value:"+out.value);
		}
		}
		if(tx.scripts != null) {
		print("\tscripts:"+tx.scripts.length);
		for(Program sc: tx.scripts) {
			print("\n\t\tsc.parameter:"+toHexString(sc.parameter) + "\n\t\tsc.code :"+toHexString(sc.code));
			print("\n\t\tsc.parameter.byte:"+byte2str(sc.parameter) + "\n\t\tsc.stack.code :"+byte2str(sc.code));
		}
		}
		print("\ttx.hash():"+tx.hash());
		if(tx instanceof RegisterTransaction) {
			RegisterTransaction reg = (RegisterTransaction) tx;
			print("\ttx.amount:"+reg.amount);
			print("\ttx.type:"+reg.assetType);
			print("\ttx.name:"+reg.name);
			print("\ttx.nonce:"+reg.nonce);
			print("\ttx.precision:"+reg.precision);
			print("\ttx.pubkey(true):"+byte2str(reg.issuer.getEncoded(true)));
			print("\ttx.pubkey(false):"+byte2str(reg.issuer.getEncoded(false)));
			print("\ttx.issuer:"+Contract.createSignatureContract(reg.issuer).address());
			print("\ttx.admin:"+reg.admin);
			print("\ttx.unsign:"+byte2str(reg.getHashData()));
			print("\ttx.unsign:"+byte2str(reg.toArray()));
		}
		if(tx instanceof RecordTransaction) {
			RecordTransaction rr = (RecordTransaction) tx;
			print("rr.type:"+rr.recordType.toString());
			print("rr.data:"+new String(rr.recordData));
		}
		print();
	
	}
	private static void print2(Block blk) {
		String dat = String.format("Height:%6s, txs.len:%6s, blockTime:%s", new Object[] {
				blk.height, blk.transactions.length, new Date(blk.timestamp * 1000L)
		});
		print(dat);
	}

	public static String toString(Block bb, int cc) {
		StringBuilder sb = new StringBuilder();
		sb.append("\n-----------------------------------------------------------------------hh:"+cc);
		sb.append("\n").append("version:"+bb.version);
		sb.append("\n").append("prevBlock:"+bb.prevBlock.toString());
		sb.append("\n").append("merkleRoot:"+bb.merkleRoot.toString());
		sb.append("\n").append("timestamp:"+new Date(bb.timestamp * 1000L));
		sb.append("\n").append("height:"+bb.height);
		sb.append("\n").append("nonce:"+bb.nonce);
		sb.append("\n").append("nextMiner:"+bb.nextMiner.toString());
		sb.append("\n").append("script:\n\t"+toHexString(bb.script.parameter) + "\n\t"+toHexString(bb.script.code));
		sb.append("\n").append("transactions:..."+bb.transactions.length);
		if(bb.transactions.length > 0) {
			Arrays.stream(bb.transactions).forEach(p -> print(p));
			Arrays.stream(bb.transactions).forEach(p -> {
				sb.append("\n\t").append("type:"+ p.type.toString());
				sb.append("\n\t").append("version:"+p.version);
				sb.append("\n\t").append("nonce:"+p.nonce);
				sb.append("\n\t").append("attrs:"+p.attributes.length);
				if(p.attributes.length > 0) {
					Arrays.stream(p.attributes).forEach(i -> {
						sb.append("\n\t\t").append("usage:"+i.usage.toString());
						sb.append("\n\t\t").append("data:"+toHexString(i.data));
					});
				}
				sb.append("\n\t").append("inputs:"+p.inputs.length);
				if(p.inputs.length > 0) {
					Arrays.stream(p.inputs).forEach(j -> {
						sb.append("\n\t\t").append("prevHash:"+j.prevHash.toString());
						sb.append("\n\t\t").append("prevIndx:"+j.prevIndex);
					});
				}
				sb.append("\n\t").append("outputs:"+p.outputs.length);
				if(p.outputs.length > 0) {
					Arrays.stream(p.outputs).forEach(k -> {
						sb.append("\n\t\t").append("assetId:"+k.assetId.toString());
						sb.append("\n\t\t").append("amount:"+k.value.toLong());
						sb.append("\n\t\t").append("scriptHash:"+k.scriptHash.toString());
					});
				}
				sb.append("\n\t").append("scripts:"+p.scripts.length);
				if(p.inputs.length > 0) {
					Arrays.stream(p.scripts).forEach(i -> {
						sb.append("\n\t\t").append("redeemScript:"+toHexString(i.parameter));
						sb.append("\n\t\t").append("redeemScript:"+toHexString(i.parameter));
					});
				}
			});
		}
		return sb.toString();
	}
	private static void print(Block blk) {
		print("Height:"+blk.height);
		print("nonce :"+blk.nonce);
		print("versio:"+blk.version);
		print("hash  :"+blk.hash());
		print("nextM :"+blk.nextMiner);
		print("prevB :"+blk.prevBlock);
		print("prevB :"+blk.timestamp);
		print("prevB :"+System.currentTimeMillis());
		print("bkTm  :"+new Date(blk.timestamp * 1000L));
		print("sc.toS:\n\t"+toHexString(blk.script.parameter) + "\n\t"+toHexString(blk.script.code));
		print("bk.mer:"+blk.merkleRoot);
		print("blk.tx.len:"+blk.transactions.length);
		for(Transaction tx: blk.transactions) {
			print(tx);
		}
	}
	
	public static void printWallet(UserWallet userWallet) {
		print(userWallet);
	}
	private static void print(UserWallet userWallet) {
		print("\nts.printWallet.......................................................................[st]");
		print("path:"+userWallet.getWalletPath());
		int c = 0;
		
		// 1
		print("accs.......................start");c = 0;
		for(Account acc: userWallet.getAccounts()) {
			print("acc......................."+(++c));
			print(acc);
		}
		print();
		// 2
		print("contracts.......................start");c = 0;
		for(Contract con: userWallet.getContracts()) {
			print("contract......................."+(++c));
			print(con);
		}
		print();
		// 3
		print("trans....................start");c = 0;
		for(Transaction tx: userWallet.LoadTransactions().keySet()) {
			print("tran...................."+(++c));
			print(tx);
		}
		print();
		// 4
		long amount = 0L;
		print("findCoins........................start"); c=0;
		for(Coin coin: userWallet.findCoins()) {
			print("coin............."+(++c));
			print(coin);
			amount += coin.value.toLong();
		}
		print("SpentAmount:"+amount);
		print();
		// 5
		amount  = 0L;
		print("findUnspentCoins.................start");c=0;
		for(Coin coin: userWallet.findUnspentCoins()) {
			print("unspent........."+(++c));
			print(coin);
			amount += coin.value.toLong();
			print(coin.value + "----" + amount);
		}
		
		// 5
		amount  = 0L;
		print("getCoin.................start");c=0;
		for(Coin coin: userWallet.getCoin()) {
			print("getCoin........."+(++c));
			print(coin);
		}
		print();
		
		print("\nts.printWallet.......................................................................[ed]");
	
	}

	public static String getbyteStr(byte[] bs)  {
    	StringBuilder sb = new StringBuilder();
    	for(byte b: bs) {
    		sb.append(" ").append(Byte.toUnsignedInt(b));
    		
    	}
    	return sb.substring(1);
    }
	
	public static void print(Account acc) {
		// addr
		print("acc..................st");
		print("acc.addr:"+Wallet.toAddress(Program.toScriptHash(Contract.createSignatureRedeemScript(acc.publicKey))));
		print("acc.uint:"+Program.toScriptHash(Contract.createSignatureRedeemScript(acc.publicKey)).toString());
		print("acc.uint(byte):"+getbyteStr(Program.toScriptHash(Contract.createSignatureRedeemScript(acc.publicKey)).toArray()));
		print("acc.uint(hex):"+toHexString(Program.toScriptHash(Contract.createSignatureRedeemScript(acc.publicKey)).toArray()));
		// pubKey
		ECPoint pubKey = acc.publicKey;
		String pubKeyStr = toHexString(acc.publicKey.getEncoded(true));
		print(String.format("acc.PubKey:\n\tECPoint: %s\n\tPubKStr:%s", pubKey, pubKeyStr));
		
		// priKey
		String priKey = toHexString(acc.privateKey);
		print(String.format("acc.PriKey:\n\tHEX:%s", priKey));
		
		String wif = acc.export();
		print(String.format("\tWIF:%s", wif));
		
		// pubKeyHash
		print("acc.PubKeyHash:"+acc.publicKeyHash.toString());
		print("acc..................ed");
	}

	public static void print(Contract con) {
		print("contract.address:"+con.address());
		print("contract.publicKey:"+con.publicKeyHash.toString());
	}

	public static void print(Coin coin) {
		print("coin.addr:"+coin.address());
		print("coin.toSt:"+coin.toString());
		print("coin.assetId:"+coin.assetId.toString());
		print("coin.scriptHash:"+coin.scriptHash);
		print("coin.value:"+coin.value.toString());
		print("coin.state:"+coin.getState());
		print("coin.TrackState:"+coin.getTrackState());
		print("coin.input.prevHash:"+coin.input.prevHash);
	}
	
   public static void print(TrackableCollection<TransactionInput, Coin> coins, String key) {
		print("Wallet.print.coins.......................[start]............."+key);
		coins.forEach(p -> {
			print("1:"+p.assetId);
			print("2:"+p.input.prevHash.toString());
			print("3:"+p.scriptHash.toString());
			print("4:"+p.value);
			print("5:"+p.address());
			print("6:"+p.getState());
			print("7:"+p.getTrackState());
			print("8:"+p.key().prevHash);
		});
		print("Wallet.print.coins.......................[end]................"+key);
	}
   
   
   
   public static void print(String str) {
	   System.out.println(str);
   }
   public static void print() {
	   System.out.println();
   }
}
