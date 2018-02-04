package DNA.Implementations.Wallets.SQLite;

import java.io.File;
import java.sql.*;
import java.util.*;

import DNA.UInt160;

class WalletDataContext implements AutoCloseable {
	private Connection connection;
	
	static {
		try {
			Class.forName("org.sqlite.JDBC");
		} catch (ClassNotFoundException ex) {
			throw new RuntimeException(ex);
		}
	}

    public WalletDataContext(String filename) {
    	File file = new File(filename);
    	boolean creating = !file.exists();
    	filename = filename.replace('\\', '/');
    	try {
    		connection = DriverManager.getConnection("jdbc:sqlite:" + filename);
		} catch (SQLException ex) {
    		throw new RuntimeException(ex);
		}
    	if (creating) {
    		createModel();
    	}
    }
    
    public void beginTransaction() {
    	try {
			connection.setAutoCommit(false);
		} catch (SQLException ex) {
    		throw new RuntimeException(ex);
		}
    }
    
    @Override
    public void close() {
    	try {
    		connection.close();
		} catch (SQLException ex) {
    		throw new RuntimeException(ex);
		}
    }
    
    public void commit() {
    	try {
    		connection.commit();
		} catch (SQLException ex) {
    		throw new RuntimeException(ex);
		}
    }

    private void createModel() {
    	try {
			Statement statement = connection.createStatement();
			statement.executeUpdate("CREATE TABLE [Key] ([Name] VarChar NOT NULL CONSTRAINT [PK_Key] PRIMARY KEY, [Value] VarBinary NOT NULL)");
			statement.executeUpdate("CREATE TABLE [Account] ([PrivateKeyEncrypted] VarBinary NOT NULL, [PublicKeyHash] Binary NOT NULL CONSTRAINT [PK_Account] PRIMARY KEY)");
			statement.executeUpdate("CREATE TABLE [Contract] ([ScriptHash] Binary NOT NULL CONSTRAINT [PK_Contract] PRIMARY KEY, [PublicKeyHash] Binary NOT NULL, [RawData] VarBinary NOT NULL, CONSTRAINT [FK_Contract_Account_PublicKeyHash] FOREIGN KEY ([PublicKeyHash]) REFERENCES [Account] ([PublicKeyHash]) ON DELETE CASCADE)");
			statement.executeUpdate("CREATE TABLE [Coin] ([TxId] Binary NOT NULL, [Index] INTEGER NOT NULL, [AssetId] Binary NOT NULL, [ScriptHash] Binary NOT NULL, [State] INTEGER NOT NULL, [Value] INTEGER NOT NULL, CONSTRAINT [PK_Coin] PRIMARY KEY ([TxId], [Index]), CONSTRAINT [FK_Coin_Contract_ScriptHash] FOREIGN KEY ([ScriptHash]) REFERENCES [Contract] ([ScriptHash]) ON DELETE CASCADE)");
			statement.executeUpdate("CREATE TABLE [Transaction] ([Hash] Binary NOT NULL CONSTRAINT [PK_Transaction] PRIMARY KEY, [Height] INTEGER, [RawData] VarBinary NOT NULL, [Time] TEXT NOT NULL, [Type] INTEGER NOT NULL)");
			statement.executeUpdate("CREATE INDEX [IX_Coin_AssetId] ON [Coin] ([AssetId])");
			statement.executeUpdate("CREATE INDEX [IX_Coin_ScriptHash] ON [Coin] ([ScriptHash])");
			statement.executeUpdate("CREATE INDEX [IX_Contract_PublicKeyHash] ON [Contract] ([PublicKeyHash])");
			statement.executeUpdate("CREATE INDEX [IX_Transaction_Height] ON [Transaction] ([Height])");
			statement.executeUpdate("CREATE INDEX [IX_Transaction_Type] ON [Transaction] ([Type])");
		} catch (SQLException ex) {
    		throw new RuntimeException(ex);
		}
    }
    
    public void delete(Coin[] coins) {
    	if (coins.length == 0) {
    		return;
    	}
    	try {
    		PreparedStatement statement = connection.prepareStatement("DELETE FROM [Coin] WHERE [TxId] = ? AND [Index] = ?");
    		for (Coin coin : coins) {
    			statement.setBytes(1, coin.txid);
    			statement.setInt(2, coin.index);
    			statement.addBatch();
    		}
    		statement.executeBatch();
		} catch (SQLException ex) {
    		throw new RuntimeException(ex);
		}
    }
    
    public void deleteAccount(UInt160 publicKeyHash) {
    	try {
    		PreparedStatement statement = connection.prepareStatement("DELETE FROM [Account] WHERE [PublicKeyHash] = ?");
    		statement.setBytes(1, publicKeyHash.toArray());
    		statement.executeUpdate();
		} catch (SQLException ex) {
    		throw new RuntimeException(ex);
		}
    }
    
    public void deleteContract(UInt160 scriptHash) {
    	try {
    		PreparedStatement statement = connection.prepareStatement("DELETE FROM [Contract] WHERE [ScriptHash] = ?");
    		statement.setBytes(1, scriptHash.toArray());
    		statement.executeUpdate();
		} catch (SQLException ex) {
    		throw new RuntimeException(ex);
		}
    }
    
    public Account[] getAccounts() {
    	try {
    		ArrayList<Account> accounts = new ArrayList<Account>();
			Statement statement = connection.createStatement();
			ResultSet result = statement.executeQuery("SELECT * FROM [Account]");
			while (result.next()) {
				Account account = new Account();
				account.privateKeyEncrypted = result.getBytes("PrivateKeyEncrypted");
				account.publicKeyHash = result.getBytes("PublicKeyHash");
				accounts.add(account);
			}
			return accounts.toArray(new Account[accounts.size()]);
		} catch (SQLException ex) {
    		throw new RuntimeException(ex);
		}
    }
    
    public Coin[] getCoins() {
    	try {
    		ArrayList<Coin> coins = new ArrayList<Coin>();
			Statement statement = connection.createStatement();
			ResultSet result = statement.executeQuery("SELECT * FROM [Coin]");
			while (result.next()) {
				Coin coin = new Coin();
				coin.txid = result.getBytes("TxId");
				coin.index = result.getInt("Index");
				coin.assetId = result.getBytes("AssetId");
				coin.scriptHash = result.getBytes("ScriptHash");
				coin.state = result.getInt("State");
				coin.value = result.getLong("Value");
				coins.add(coin);
			}
			return coins.toArray(new Coin[coins.size()]);
		} catch (SQLException ex) {
    		throw new RuntimeException(ex);
		}
    }
    
    public Contract[] getContracts() {
    	try {
    		ArrayList<Contract> contracts = new ArrayList<Contract>();
			Statement statement = connection.createStatement();
			ResultSet result = statement.executeQuery("SELECT * FROM [Contract]");
			while (result.next()) {
				Contract contract = new Contract();
				contract.scriptHash = result.getBytes("ScriptHash");
				contract.publicKeyHash = result.getBytes("PublicKeyHash");
				contract.rawData = result.getBytes("RawData");
				contracts.add(contract);
			}
			return contracts.toArray(new Contract[contracts.size()]);
		} catch (SQLException ex) {
    		throw new RuntimeException(ex);
		}
    }
    
    public Key getKey(String name) {
    	try {
    		PreparedStatement statement = connection.prepareStatement("SELECT * FROM [Key] WHERE [Name] = ?");
    		statement.setString(1, name);
    		ResultSet result = statement.executeQuery();
			if (!result.next()) return null;
			Key key = new Key();
			key.name = result.getString("Name");
			key.value = result.getBytes("Value");
			return key;
		} catch (SQLException ex) {
    		throw new RuntimeException(ex);
		}
    }
    
    public Transaction[] getTransaction() {
    	try {
    		ArrayList<Transaction> trans = new ArrayList<Transaction>();
			Statement statement = connection.createStatement();
			ResultSet result = statement.executeQuery("SELECT * FROM [Transaction]");
			while (result.next()) {
				Transaction tran = new Transaction();
				tran.hash = result.getBytes("Hash");
				tran.height = result.getInt("Height");
				tran.rawData = result.getBytes("RawData");
//				tran.time = result.getDate("Time");
				tran.type = result.getByte("Type");
				trans.add(tran);
			}
			return trans.toArray(new Transaction[trans.size()]);
		} catch (SQLException ex) {
			throw new RuntimeException(ex);
		}
    }
    
    public void insert(Coin[] coins) {
    	if (coins.length == 0) {
    		return;
    	}
    	try {
    		PreparedStatement statement = connection.prepareStatement("INSERT INTO [Coin] ([TxId], [Index], [AssetId], [ScriptHash], [State], [Value]) VALUES (?, ?, ?, ?, ?, ?)");
    		for (Coin coin : coins) {
    			statement.setBytes(1, coin.txid);
    			statement.setInt(2, coin.index);
    			statement.setBytes(3, coin.assetId);
    			statement.setBytes(4, coin.scriptHash);
    			statement.setInt(5, coin.state);
    			statement.setLong(6, coin.value);
    			statement.addBatch();
    		}
    		statement.executeBatch();
		} catch (SQLException ex) {
    		throw new RuntimeException(ex);
		}
    }
    
    public void insertOrUpdate(Account account) {
    	try {
    		PreparedStatement statement = connection.prepareStatement("INSERT OR REPLACE INTO [Account] ([PublicKeyHash], [PrivateKeyEncrypted]) VALUES (?, ?)");
    		statement.setBytes(1, account.publicKeyHash);
    		statement.setBytes(2, account.privateKeyEncrypted);
    		statement.executeUpdate();
		} catch (SQLException ex) {
    		throw new RuntimeException(ex);
		}
    }
    
    public void insertOrUpdate(Contract contract) {
    	try {
    		PreparedStatement statement = connection.prepareStatement("INSERT OR REPLACE INTO [Contract] ([ScriptHash], [PublicKeyHash], [RawData]) VALUES (?, ?, ?)");
    		statement.setBytes(1, contract.scriptHash);
    		statement.setBytes(2, contract.publicKeyHash);
    		statement.setBytes(3, contract.rawData);
    		statement.executeUpdate();
		} catch (SQLException ex) {
    		throw new RuntimeException(ex);
		}
    }
    
    public void insertOrUpdate(Key key) {
    	try {
    		PreparedStatement statement = connection.prepareStatement("INSERT OR REPLACE INTO [Key] ([Name], [Value]) VALUES (?, ?)");
    		statement.setString(1, key.name);
    		statement.setBytes(2, key.value);
    		statement.executeUpdate();
		} catch (SQLException ex) {
    		throw new RuntimeException(ex);
		}
    }
    
    public void insertOrUpdate(Transaction transaction) {
    	try {
    		PreparedStatement statement = connection.prepareStatement("INSERT OR REPLACE INTO [Transaction] ([Hash], [Height], [RawData], [Time], [Type]) VALUES (?, ?, ?, ?, ?)");
    		statement.setBytes(1, transaction.hash);
    		if (transaction.height == -1)
    			statement.setNull(2, Types.INTEGER);
    		else
    			statement.setInt(2, transaction.height);
    		statement.setBytes(3, transaction.rawData);
    		statement.setDate(4, transaction.time);
    		statement.setByte(5, transaction.type);
    		statement.executeUpdate();
		} catch (SQLException ex) {
    		throw new RuntimeException(ex);
		}
    }
    
    public void rollback() {
    	try {
    		connection.rollback();
		} catch (SQLException ex) {
    		throw new RuntimeException(ex);
		}
    }
    
    public void update(Coin[] coins) {
    	if (coins.length == 0) {
    		return;
    	}
    	try {
    		PreparedStatement statement = connection.prepareStatement("UPDATE [Coin] SET [State] = ? WHERE [TxId] = ? AND [Index] = ?");
    		for (Coin coin : coins) {
    			statement.setInt(1, coin.state);
    			statement.setBytes(2, coin.txid);
    			statement.setInt(3, coin.index);
    			statement.addBatch();
    		}
    		statement.executeBatch();
		} catch (SQLException ex) {
    		throw new RuntimeException(ex);
		}
    }
}
