import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import DNA.Network.Rest.RestException;
import DNA.sdk.info.account.AccountAsset;
import DNA.sdk.info.account.AccountInfo;
import DNA.sdk.info.asset.AssetInfo;
import DNA.sdk.info.mutil.TxJoiner;
import DNA.sdk.info.transaction.TransactionInfo;
import DNA.sdk.wallet.AccountManager;


/**
 * DNA sdk 测试Demo
 * 
 * @author 12146
 * 
 */
public class TestMain4CLI {

	public static void main(String[] args) throws Exception {
		boolean flag = true;
		
		// 实例化账户管理类
		AccountManager wm = getAccountManager();
	
		// 启动同步
		wm.startSyncBlock();
		
		// 等待同步完成
		while(!wm.hasFinishedSyncBlock()) {
			Thread.sleep(1000*1);
		}
		
		// 创建多个账户
		List<String> list = wm.createAccount(3);
		String addr1 = list.get(0);
		String addr2 = list.get(1);
		String addr3 = list.get(2);
		print("user1:"+addr1 + ","+wm.address2UInt160(addr1) + ",addr.len="+addr1.length()+",uint160.len="+wm.address2UInt160(addr1).length());
		print("user2:"+addr2 + ","+wm.address2UInt160(addr2));
		print("user3:"+addr3 + ","+wm.address2UInt160(addr3));
		
		String txid = null,assetid = null;
		if(flag) {
		// 注册资产(资产控制者为addr1)
		print("test regAsset..............................................[st]");
		txid = wm.reg(addr1, "Token001", 10000, "用户1注册资产S01", addr1);
		print("test regAsset..............................................[ed],txid="+txid);
		
		assetid = txid;
		
		// 分发资产(账户addr1分发资产给addr2)
		print("test issAsset..............................................[st]");
		txid = wm.iss(addr1, assetid, 100, addr2, "用户1 分发给 用户2");
		print("test issAsset..............................................[ed],txid="+txid);
		
		// 转移资产(账户addr2转移资产只addr3)
		print("test trfAsset..............................................[st]");
		txid = wm.trf(addr2, assetid, 11, addr3, "用户2转账给用户3");
		print("test trfAsset..............................................[ed],txid="+txid);
		
		
		
		// 存证
		String content = "ts_"+new Date();
		print("test storeCert..............................................[st]");
		txid = wm.storeCert(content, "存证交易test");
		print("test storeCert..............................................[ed],txid="+txid+",len="+txid.length());
		
		Thread.sleep(1000*10);
		
		// 取证
		print("test queryCert..............................................[st]");
		String newContent = wm.queryCert(txid);
		print(String.format("\n\told=%s\n\tnew=%s", content, newContent));
		print("test queryCert..............................................[ed],txid=end");
		
		
		// 分发给多个接收者
		List<TxJoiner> tlist = new ArrayList<TxJoiner>();
		tlist.add(newTxJoiner(addr2, assetid, 45));
		tlist.add(newTxJoiner(addr3, assetid, 55));
		txid = wm.iss(tlist, addr1, "u1 转账给 u2 和 u3");
		
		// 转账给多个接收者
		tlist.clear();
		tlist.add(newTxJoiner(addr1, assetid, 1));
		tlist.add(newTxJoiner(addr2, assetid, 2));
		txid = wm.trf(tlist, addr3, "u3 转账给 u1 和 u2");
		
				
		// 查询所有账户地址
		List<String> addrList = wm.listAccount();
		System.out.println("Query All Account:-------------------------\n"+addrList);
		
		// 查询账户信息
		AccountInfo accountInfo = wm.getAccountInfo(addr1);
		System.out.println("Query Account info:-------------------------\n"+accountInfo);
		
		// 查询账户资产
		AccountAsset accountAsset = wm.getAccountAsset(addr1);
		System.out.println("Query Account asset:-------------------------\n"+accountAsset);
		
		// 查询资产信息
		AssetInfo assetInfo = wm.getAssetInfo(assetid);
		System.out.println("Query Asset info:-------------------------\n"+assetInfo);
		
		// 查询交易信息
		TransactionInfo transactionInfo = wm.getTransactionInfo(txid);
		System.out.println("Query Transaction info:-------------------------\n"+transactionInfo);
		
		// 查询所有账户资产
		wm.listAccount().forEach(p -> {
			print("query acc.asset:"+wm.getAccountAsset(p));
		});
		// 账户管理器
		System.out.println("#######################################################################################");
		}
	}
	
	private static void print(String ss) {
		System.out.println(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date()) + " " + ss);
	}
	
	private static TxJoiner newTxJoiner(String address, String assetid, long value) {
		TxJoiner tt = new TxJoiner();
		tt.address = address;
		tt.assetid = assetid;
		tt.value = value;
		return tt;
	}
	
	public void testCreateAccount() {
		// 打开账户管理器
		AccountManager wm = getAccountManager();
		// 创建账户
		String user01 = wm.createAccount();			// 创建单个账户
		List<String> list = wm.createAccount(10); 	// 批量创建10个账户
		System.out.println("user:"+user01);
		System.out.println("list:"+list);
	}
	public void testRegAsset() throws Exception {
		// 打开账户管理器
		AccountManager wm = getAccountManager();
		// 注册资产
		String issuer= "";			// 资产发行者地址
		String name = "";			// 资产名称
		long amount = 10000;		// 资产数量
		String desc = "";			// 描述
		String controller = "";		// 资产控制者地址
		String assetid = wm.reg(issuer, name, amount , desc, controller);
		System.out.println("rs:"+assetid);
	}
	public void testIssAsset() throws Exception {
		// 打开账户管理器
		AccountManager wm = getAccountManager();
		// 分发资产
		String controller= "";		// 资产控制者地址
		String assetid = "";		// 资产编号(由注册资产产生)
		long amount = 100;			// 分发数量
		String recver = "";			// 分发资产接收者地址
		String desc = "";			// 描述
		String txid = wm.iss(controller, assetid, amount , recver , desc );
		System.out.println("rs:"+txid);
	}
	public void testTrfAsset() throws Exception {
		// 打开账户管理器
		AccountManager wm = getAccountManager();
		// 转移资产
		String controller= "";		// 资产控制者地址
		String assetid = "";		// 资产编号(由注册资产产生)
		long amount = 100;		// 转移数量
		String recver = "";		// 转移资产接收者地址
		String desc = "";		// 描述
		String txid = wm.trf(controller, assetid, amount , recver , desc );
		System.out.println("rs:"+txid);
	}
	
	public static void testStoreCert() throws Exception {
		// 打开账户管理器
		AccountManager wm = getAccountManager();
		// 存证
		String content = "ts";		// 待存储的信息
		String desc = "dd";			// 描述
		String txid = wm.storeCert(content, desc);
		System.out.println("rs:"+txid);
	}
	
	public void testQueryCert() throws Exception {
		// 打开账户管理器
		AccountManager wm = getAccountManager();
		// 取证
		String txid = "";		// 存证编号
		String contetn= wm.queryCert(txid);
		System.out.println("rs:"+contetn);
	}
	
	public static void testAccountInfo() {
		// 打开账户管理器
		AccountManager wm = getAccountManager(); 
		// 查询账户信息
		String userAddr = "AZg3vyDawyHtET8tNhs1odKPa6yy8qFgxK";		// 账户地址
		AccountInfo info = wm.getAccountInfo(userAddr);
		System.out.println("rs:"+info);
	}
	public static void testAccountAsset() {
		// 打开账户管理器
		AccountManager wm = getAccountManager(); 
		// 查询账户资产
		String userAddr = "AZsbZYCFh9Xat2HdBwniZKDFfau4WWy9Sg";
		AccountAsset info = wm.getAccountAsset(userAddr);
		System.out.println("rs:"+info);
	}
	public static void testAssetInfo() throws RestException {
		// 打开账户管理器
		AccountManager wm = getAccountManager(); 
		// 查询账户资产
		String assetid = "c35d2195b197f8f75a2fb0367d667d2f63a02ba9e1929ab6378ffe91218f0446";
		AssetInfo info = wm.getAssetInfo(assetid);
		System.out.println("rs:"+info);
	}
	public static void testTransactionInfo() throws Exception {
		// 打开账户管理器
		AccountManager wm = getAccountManager(); 
		// 查询账户资产
		String txid = "05eee614559bf42dbedb2b062a6d5ef6b813abcc130ed3f55b3d0cdfec8c86ad";
		TransactionInfo info = wm.getTransactionInfo(txid);
		System.out.println("rs:"+info);
	}
	
	public static void testListAccount() {
		AccountManager wm = getAccountManager(); 
		
		wm.listAccount().forEach(p -> System.out.println(p));
	}
	
	public static AccountManager getAccountManager() {
		// 打开账户管理器
		// v1.0
//		String path = "./dat/tsGo_01.db3";
//		String url = "http://localhost:20334";
//		String accessToken = "";				// 从认证服务器获取该访问令牌
//		UserWalletManager wm = UserWalletManager.getWallet(path, url, accessToken);
		
		// v1.1
		String dnaUrl = "http://127.0.0.1:20334";
		String dnaToken = "";
		String path = "./4.db3";
		AccountManager wm = AccountManager.getWallet(path, dnaUrl, dnaToken);
		print(String.format("[param=%s,%s]", dnaUrl, path));
		print(String.format("start to test....hh:%s", wm.getBlockHeight()));
		return wm;
	}
}
