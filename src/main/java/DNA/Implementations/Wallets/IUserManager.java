package DNA.Implementations.Wallets;

import java.util.Map;

import DNA.Fixed8;
import DNA.UInt160;
import DNA.Core.SignatureContext;
import DNA.Core.Transaction;
import DNA.Wallets.Account;
import DNA.Wallets.Coin;
import DNA.Wallets.CoinException;
import DNA.Wallets.Contract;

public interface IUserManager {
	
	public void start();
	
	public void close();
	
	public Account createAccount();
	
	public Account createAccount(byte[] privateKey);
	
	public Contract getContract(String address);
	
	public Account[] getAccounts();
	
	public Contract[] getContracts();
	
	public void rebuild();

	public <T extends Transaction> T makeTransaction(T regTx,Fixed8 zero) throws CoinException;
	
	public <T extends Transaction> T makeTransaction(T tx, Fixed8 fee, UInt160 from) throws CoinException;
	
    public boolean saveTransaction(Transaction tx);

    public boolean sign(SignatureContext context);

    public boolean hasFinishedSyncBlock() throws Exception;

	public Account getAccount(UInt160 publicKeyHash);

	public Account getAccountByScriptHash(UInt160 scriptHash);

	public Coin[] findUnspentCoins();
	
	public Coin[] findUnconfirmedCoins();

	public Map<Transaction, Integer> LoadTransactions();
	
	public Coin[] getCoin();
	
	public int getBlockHeight();
	public int getWalletHeight();
	
}
