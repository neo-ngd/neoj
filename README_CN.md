# DNA SDK 手册



## 1 准备工作

*** 下载DNA SKD(java版本)， 配置JAVA8运行环境

> > 注意： 配置java运行环境后运行程序时如出现如下错误：
> >
> > java.security.InvalidKeyException: Illegal key size
> >
> > 则这是秘钥长度大于128，安全策略文件受限的原因。可以去官网下载local_policy.jar和US_export_policy.jar，替换jre目录中${java_home}/jre/lib/security原有的与安全策略这两个jar即可。下载地址：
> >
> > [http://www.oracle.com/technetwork/java/javase/downloads/jce8-download-2133166.html](http://www.oracle.com/technetwork/java/javase/downloads/jce8-download-2133166.html)





## 2 接入步骤

### 2.1  OAuth认证

DNA默认关闭认证选项，使用SDK可以直接访问DNA。在联盟链或私有链有认证要求的场景下，您需要首先完成用户注册，并获取授权accessToken才能访问应用。用户注册和获取Token请参考: [https://forum.dnaproject.org/t/dna/112](https://forum.dnaproject.org/t/dna/112)。



### 2.2  SDK调用

调用sdk时，将该访问令牌(非联盟链可任意)作为其中一个初始化参数传递进来，实现数字资产的注册、分发、转移、存证、取证等操作。





## 3 接口说明##

调用每一个接口方法之前必须实例化账户管理器，后续的接口都是基于账户管理来调用的。实例化账户管理器所需参数包括：连接地址url，账户存储位置路径path，访问令牌accessToken。

```
String dnaUrl = "http://127.0.0.1:20334";
String dnaToken = "";
String path = "./dat/3.db3";
AccountManager wm = AccountManager.getWallet(path, dnaUrl, dnaToken);
```





### 3.1 创建账户

通过传递一个int类型的数值，可以创建多个账户合约，以合约地址列表形式返回。

| 参数   | 字段       | 类型           | 描述       | 说明               |
| ---- | -------- | ------------ | -------- | ---------------- |
| 输入参数 | numCount | int          | 单次创建账户个数 | int类型占4字节        |
| 输出参数 | list     | List<String> | 账户合约地址列表 | 合约地址是以A开头的34位字符串 |

eg: 

List<String>  list = wm.createAccount(numCount);



### 3.2 导入私钥

根据导入可视化私钥(字节数组转换成十六进制的字符串)方式来创建账户

| 参数   | 字段         | 类型     | 描述   | 说明                       |
| ---- | ---------- | ------ | ---- | ------------------------ |
| 输入参数 | privateKey | String | 私钥信息 | 32位字节数组转成十六进制的64位字符串类型数据 |
| 输出参数 | address    | String | 账户地址 | 合约地址是以A开头的34位字符串         |

eg:

String address = wm.createAccountsFromPrivateKey(String privateKey);



### 3.3 注册资产

通过传递资产的基本信息来产生一笔区块链上合法的资产，返回资产编号。后续的资产类操作就可以使用该资产编号。

| 参数   | 字段         | 类型     | 描述            | 说明             |
| ---- | ---------- | ------ | ------------- | -------------- |
| 输入参数 | issuer     | String | 发行者地址         | 地址是以A开头的34位字符串 |
|      | name       | String | 资产名称          | 长度可任意          |
|      | amount     | long   | 资产数量          | long类型占8字节     |
|      | desc       | String | 描述            | 长度可任意          |
|      | controller | String | 控制者地址         | 地址是以A开头的34位字符串 |
| 输出参数 | txid       | String | 交易编号，这里代表资产编号 | 交易编号是64位字符串    |

eg：

String assetid = wm.reg(issuer, name, amount , desc, controller);



### 3.4 分发资产

通过传递分发资产的基本信息来完成一笔资产的分发操作，返回交易编号。

| 参数   | 字段         | 类型     | 描述      | 说明                        |
| ---- | ---------- | ------ | ------- | ------------------------- |
| 输入参数 | controller | String | 资产控制者地址 | 地址是以A开头的34位字符串            |
|      | assetid    | String | 资产编号    | 资产编号对应注册资产的交易编号，长度为64位字符串 |
|      | amount     | long   | 分发数量    | long类型占8字节                |
|      | recver     | String | 接收者地址   | 地址是以A开头的34位字符串            |
|      | desc       | String | 描述      | 长度可任意                     |
| 输出参数 | txid       | String | 交易编号    | 交易编号是64位字符串               |

eg:

String txid = wm.iss(controller, assetid, amount , recver , desc );



### 3.5 转移资产

通过传递分发资产的基本信息来完成一笔资产的分发操作，返回交易编号。

| 参数   | 字段         | 类型     | 描述      | 说明                        |
| ---- | ---------- | ------ | ------- | ------------------------- |
| 输入参数 | controller | String | 资产控制者地址 | 地址是以A开头的34位字符串            |
|      | assetid    | String | 资产编号    | 资产编号对应注册资产的交易编号，长度为64位字符串 |
|      | amount     | long   | 转移数量    | long类型占8字节                |
|      | recver     | String | 接收者地址   | 地址是以A开头的34位字符串            |
|      | desc       | String | 描述      | 长度可任意                     |
| 输出参数 | txid       | String | 交易编号    | 交易编号是64位字符串               |

eg:

String txid = wm.trf(controller, assetid, amount , recver , desc );



### 3.6 存证

通过传递存证交易的基本信息来完成一笔资产的分发操作，返回交易编号。

| 参数   | 字段      | 类型     | 描述   | 说明          |
| ---- | ------- | ------ | ---- | ----------- |
| 输入参数 | content | String | 存证信息 | 长度任意的字符串    |
|      | desc    | String | 描述   | 长度任意的字符串    |
| 输出参数 | txid    | String | 交易编号 | 交易编号是64位字符串 |

eg:

String txid = wm.storeCert(content, desc);



### 3.7 取证

查询类操作，传递存证时的交易编号，输出具体存证内容

| 参数   | 字段     | 类型     | 描述   | 说明          |
| ---- | ------ | ------ | ---- | ----------- |
| 输入参数 | txid   | String | 交易编号 | 交易编号是64位字符串 |
| 输出参数 | cotent | String | 存证内容 | 长度任意的字符串    |

eg：

String content= wm.queryCert(txid);



### 3.8 账户信息

查询类操作，传递账户地址，输出账户具体信息

| 参数   | 字段      | 类型          | 描述   | 说明                              |
| ---- | ------- | ----------- | ---- | ------------------------------- |
| 输入参数 | address | String      | 合约地址 | 地址是以A开头的34位字符串                  |
| 输出参数 | info    | AccountInfo | 账户信息 | 自定义类型，包括合约地址/公钥/私钥/公钥hash/私钥wif |

| 自定义类型       | 子字段     | 子类型    | 描述    | 说明                    |
| ----------- | ------- | ------ | ----- | --------------------- |
| AccountInfo | address | String | 合约地址  | 地址是以A开头的34位字符串        |
|             | pubkey  | String | 公钥    | 压缩模式下公钥的十六进制字符串表示     |
|             | prikey  | String | 私钥    | 私钥的十六进制字符串表示          |
|             | priwif  | String | 私钥WIF | 私钥Base58算法之后的字符串表示    |
|             | pkhash  | String | 公钥哈希  | 压缩模式下公钥哈希之后的十六进制字符串表示 |

eg:

AccountInfo info = wm.getAccountInfo(address);



### 3.9 账户资产

查询类操作。传递账户地址，输出账户资产详情

| 参数   | 字段      | 类型           | 描述     | 说明                     |
| ---- | ------- | ------------ | ------ | ---------------------- |
| 输入参数 | address | String       | 合约地址   | 合约地址是以A开头的34位字符串       |
| 输出参数 | info    | AccountAsset | 账户资产信息 | 账户资产信息包括合约地址、可用资产/冻结资产 |

| 自定义类型        | 子字段          | 子类型         | 描述     | 说明                     |
| ------------ | ------------ | ----------- | ------ | ---------------------- |
| AccountAsset | address      | String      | 合约地址   | 合约地址是以A开头的34位字符串       |
|              | canUseAssets | List<Asset> | 可用资产列表 | 可用资产列表，资产类型包括资产编号和资产数量 |
|              | freezeAssets | List<Asset> | 冻结资产列表 | 不可用资产列表，资产包括资产编号和资产数量  |
| Asset        | assetid      | String      | 资产编号   | 64位字符串                 |
|              | amount       | long        | 资产数量   | 8字节的long类型             |

eg:

AccountAsset info = wm.getAccountAsset(userAddr);



### 3.10 资产信息

查询类操作。传递资产编号，输出资产详情。

| 参数   | 字段      | 类型        | 描述   | 说明                         |
| ---- | ------- | --------- | ---- | -------------------------- |
| 输入参数 | assetid | String    | 资产编号 | 64位字符串                     |
| 输出参数 | info    | AssetInfo | 资产信息 | 资产信息包括资产编号、名称、注册数量、注册者、控制者 |

| 自定义类型     | 子字段        | 子类型    | 描述   | 说明                              |
| --------- | ---------- | ------ | ---- | ------------------------------- |
| AssetInfo | name       | String | 资产名称 | 长度任意的字符串                        |
|           | precision  | int    | 精度   | 4字节的int类型                       |
|           | assetType  | int    | 资产类型 | 4字节的int类型，取值：17代表代币，1代表股权，0代表法币 |
|           | recordType | int    | 记账模式 | 4字节的int类型，取值：0代表utxo模式，1代表余额模式  |

eg:

AssetInfo info = wm.getAssetInfo(assetid);



### 3.11 交易信息

查询类操作。传递交易编号，返回交易具体信息。

| 参数   | 字段   | 类型              | 描述   | 说明                   |
| ---- | ---- | --------------- | ---- | -------------------- |
| 输入参数 | txid | String          | 交易编号 | 64位字符串               |
| 输出参数 | info | TransactionInfo | 交易信息 | 交易信息包括交易编号、交易输入、交易输出 |

| 自定义类型           | 子字段     | 子类型                | 描述   | 说明                     |
| --------------- | ------- | ------------------ | ---- | ---------------------- |
| TransactionInfo | txid    | String             | 交易编号 | 64位字符串                 |
|                 | type    | String             | 交易类型 | 取值：注册交易/分发交易/转账交易/存证交易 |
|                 | inputs  | List<TxInputInfo>  | 交易输入 | 交易资产的来源                |
|                 | outputs | List<TxOutputInfo> | 交易输出 | 交易资产的去向                |
|                 | attrs   | String             | 描述   | 任意长度的字符串               |
| TxInputInfo     | address | String             | 合约地址 | 合约地址是以A开头的34位字符串       |
|                 | assetid | String             | 资产编号 | 64位字符串                 |
|                 | amount  | long               | 资产数量 | 8字节的long类型             |
| TxOutputInfo    | address | String             | 合约地址 | 合约地址是以A开头的34位字符串       |
|                 | assetid | String             | 资产编号 | 64位字符串                 |
|                 | amount  | long               | 资产数量 | 8字节的long类型             |

eg：

TransactionInfo info = wm.getTransactionInfo(txid);



### 3.12 所有账户

通过账户管理器实例可以查询当前管理器中所有账户地址。

| 参数   | 字段   | 类型           | 描述     | 说明     |
| ---- | ---- | ------------ | ------ | ------ |
| 输入参数 | 空    | 空            | 参数为空   | 参数为空   |
| 输出参数 | list | List<String> | 账户地址列表 | 账户地址列表 |

eg:

List<String> list = wm.listAccount();



## 4 调用示例

```
//实例化账户管理器
String dnaUrl = "http://127.0.0.1:20334";	  // DNA节点地址
String dnaToken = "";						// 访问令牌，非必需项，如开启OAuth认证，则需要填写	
String path = "./dat/3.db3";				 // 钱包路径
AccountManager wm = AccountManager.getWallet(path, dnaUrl, dnaToken);
```





### 4.1 创建账户

```
// 打开账户管理器(参考实例化账户管理器)
AccountManager wm = getAccountManager(); 

// 创建账户
String user01 = wm.createAccount();			// 创建单个账户
List<String> list = wm.createAccount(10); 	// 批量创建10个账户
```



### 4.2 导入私钥

```
// 打开账户管理器(参考实例化账户管理器)
AccountManager wm = getAccountManager(); 

// 创建账户
String privatekey = "";
String address = wm.loadPrivateKey(privatekey);
```



### 4.3 注册资产

```
// 打开账户管理器(参考实例化账户管理器)
AccountManager wm = getAccountManager(); 

// 注册资产
String issuer= "";		// 资产发行者地址
String name = "";			// 资产名称
long amount = 10000;		// 资产数量
String desc = "";			// 描述
String controller = "";		// 资产控制者地址
String assetid = wm.reg(issuer, name, amount , desc, controller);
System.out.println("rs:"+assetid);
```



### 4.4 发行资产

```
// 打开账户管理器(参考实例化账户管理器)
AccountManager wm = getAccountManager(); 

// 分发资产
String controller= "";		// 资产控制者地址
String assetid = "";		// 资产编号(由注册资产产生)
long amount = 100;			// 分发数量
String recver = "";			// 分发资产接收者地址
String desc = "";			// 描述
String txid = wm.iss(controller, assetid, amount , recver , desc );
System.out.println("rs:"+txid);
```



### 4.5 转移资产

```
// 打开账户管理器(参考实例化账户管理器)
AccountManager wm = getAccountManager(); 

// 转移资产
String controller= "";		// 资产控制者地址
String assetid = "";		// 资产编号(由注册资产产生)
long amount = 100;		// 转移数量
String recver = "";		// 转移资产接收者地址
String desc = "";		// 描述
String txid = wm.trf(controller, assetid, amount , recver , desc );
System.out.println("rs:"+txid);
```



### 4.6 存证

```
// 打开账户管理器(参考实例化账户管理器)
AccountManager wm = getAccountManager(); 

// 方式1：存证
String content = "";		// 待存储的信息
String desc = "";			// 描述
String txid = wm.storeCert(content, desc);
System.out.println("rs:"+txid);
```



### 4.7 取证

```
// 打开账户管理器(参考实例化账户管理器)
AccountManager wm = getAccountManager(); 

// 取证
String txid = "";		// 存证编号
String content= wm.queryCert(txid);
System.out.println("rs:"+content);
```



### 4.8 账户信息

```
// 打开账户管理器(参考实例化账户管理器)
AccountManager wm = getAccountManager(); 

// 查询账户信息
String userAddr = "";		// 账户地址
AccountInfo info = wm.getAccountInfo(userAddr);
```



### 4.9 账户资产

```
// 打开账户管理器(参考实例化账户管理器)
AccountManager wm = getAccountManager(); 

// 查询账户资产
String userAddr = "";		// 账户地址
AccountAsset info = wm.getAccountAsset(userAddr);
```



### 4.10 资产信息

```
// 打开账户管理器(参考实例化账户管理器)
AccountManager wm = getAccountManager(); 

// 查询账户资产
String assetid = "";
AssetInfo info = wm.getAssetInfo(assetid);
```



### 4.11 交易信息

```
// 打开账户管理器(参考实例化账户管理器)
AccountManager wm = getAccountManager(); 

// 查询账户资产
String txid = "";
TransactionInfo info = wm.getTransactionInfo(txid);
```



### 4.12 所有账户

```
// 打开账户管理器(参考实例化账户管理器)
AccountManager wm = getAccountManager(); 

// 查询管理器中所有账户
List<String> list = wm.listAccount();
```





## 5 开发说明

​	该SDK供客户端使用，其中含有账户信息的管理，比如合约地址、公钥、私钥，这些信息保存至客户端数据库中。具体的数据库可根据需求自由选择，目前实现的数据库有sqlite、mysql，sqlite是一个文件数据库，初始化时需要传递路径，上面的示例是根据该sqlite保存账户信息的数据库实现给出的，mysql使用时还需要创建对应的表，以及实现具体的数据库连接。通过SDK接入DNA区块链时，可以直接使用当前Sqlite数据库保存账户的公私钥信息的UserWalletManager，也可自定义一个账户管理器来管理账户私密信息。自己实现账户管理器可参考DNA.Implementations.Wallets.SQLite.UserWallet 类。





## 6 错误代码

| 返回代码  | 描述信息                | 说明                |
| :---- | ------------------- | ----------------- |
| 0     | SUCCESS             | 成功                |
| 41001 | SESSION_EXPIRED     | 会话无效或已过期（ 需要重新登录） |
| 41002 | SERVICE_CEILING     | 达到服务上限            |
| 41003 | ILLEGAL_DATAFORMAT  | 不合法数据格式           |
| 42001 | INVALID_METHOD      | 无效的方法             |
| 42002 | INVALID_PARAMS      | 无效的参数             |
| 42003 | INVALID_TOKEN       | 无效的令牌             |
| 43001 | INVALID_TRANSACTION | 无效的交易             |
| 43002 | INVALID_ASSET       | 无效的资产             |
| 43003 | INVALID_BLOCK       | 无效的块              |
| 44001 | UNKNOWN_TRANSACTION | 找不到交易             |
| 44002 | UNKNOWN_ASSET       | 找不到资产             |
| 44003 | UNKNOWN_BLOCK       | 找不到块              |
| 45001 | INVALID_VERSION     | 协议版本错误            |
| 45002 | INTERNAL_ERROR      | 内部错误              |

