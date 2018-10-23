package Demo;

import NEO.Core.Blockchain;
import NEO.Core.SignatureContext;
import NEO.Core.Transaction;
import NEO.Fixed8;
import NEO.Helper;
import NEO.IO.Json.JArray;
import NEO.IO.Json.JNumber;
import NEO.IO.Json.JObject;
import NEO.IO.Json.JString;
import NEO.Settings;
import NEO.UInt160;
import NEO.Wallets.Account;
import NEO.Wallets.Contract;
import NEO.Wallets.Wallet;
import NEO.sdk.SmartContractTx;
import NEO.sdk.wallet.AccountManager;

import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

public class GlobalAssetDemo {

    // exported WIF from wallet
    public static final String WALLET_WIF1 = "KxQVgnGyoHNA9h4k2as9TDPczsSwzUbH2Y8Bfku65AJiomB3wkg6";
    public static final String WALLET_WIF2 = "L4vr3XmcqGNJzwVfABiVtZbJajhNUWv6ny9Ggv7HYbhBy8rxMReU";
    public Contract contract;

    public static void main(String[] args) throws Exception {

        // import setting
        Settings.getSettings(Settings.CONFIG_PRIVNET);

        // new AccountManager
        AccountManager wm = getAccountManager();

        // start block sync
        wm.startSyncBlock();

        // check height
        while (!wm.hasFinishedSyncBlock()) {
            Thread.sleep(1000 * 1);
        }

        // import two account from WIF
        String address1 = wm.createAccountsFromPrivateKey(Helper.toHexString(Wallet.getPrivateKeyFromWIF(WALLET_WIF1)));
        Account account1 = wm.getAccount(address1);
        Contract contract1 = Contract.createSignatureContract(account1.publicKey);
        System.out.println("contract1 address:" + address1);

        String address2 = wm.createAccountsFromPrivateKey(Helper.toHexString(Wallet.getPrivateKeyFromWIF(WALLET_WIF2)));
        Account account2 = wm.getAccount(address2);
        Contract contract2 = Contract.createSignatureContract(account2.publicKey);
        System.out.println("contract2 address:" + address2);

        // check balance
        System.out.println(wm.getAccountAsset(address1));
        System.out.println(wm.getAccountAsset(address2));

        // make transaction
        Fixed8 fee = new Fixed8();
        Transaction tx = SmartContractTx.makeContractTransaction(address2, Blockchain.GoverningToken, Fixed8.ONE, fee);
        System.out.println(tx.hash().toString());
        tx = wm.makeTransaction(tx, fee, Wallet.toScriptHash(address1));

        // sign tx
        SignatureContext context = new SignatureContext(tx, new UInt160[]{Wallet.toScriptHash(contract1.address())});
        byte[] signature = context.signable.sign(account1);
        if (!context.add(contract1, account1.publicKey, signature))
            throw new Exception();
        if (context.isCompleted()) {
            tx.scripts = context.getScripts();
            System.out.println("scripts:" + tx.scripts[0].json());
        }

        // checkout transaction
        String txHex = Helper.toHexString(tx.toArray());
        System.out.println("tx:" + tx.json());

        // make request
        JObject[] params = new JObject[]{ new JString(Helper.toHexString(tx.toArray())) };

        JObject request = new JObject();
        request.set("jsonrpc", new JString("2.0"));
        request.set("method", new JString("sendrawtransaction"));
        request.set("params", new JArray(params));
        request.set("id", new JNumber(1));

        // send request
        HttpURLConnection connection = (HttpURLConnection) new URL(Settings.RPC_LIST.get(0)).openConnection();
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);

        // wait result
        try (OutputStreamWriter w = new OutputStreamWriter(connection.getOutputStream())) {
            w.write(request.toString());
        }
        try (InputStreamReader r = new InputStreamReader(connection.getInputStream())) {
            System.out.println(JObject.parse(r));
        }
    }

    public static AccountManager getAccountManager() throws MalformedURLException {
        // v1.2
        String neoUrl = Settings.RPC_LIST.get(0);
        String neoToken = "";
        String path = "./1.db3";
        AccountManager wm = AccountManager.getWallet(path, neoUrl, neoToken);
        print(String.format("[param=%s,%s]", neoUrl, path));
        print(String.format("start to test....hh:%s", wm.getBlockHeight()));
        return wm;
    }

    private static void print(String ss) {
        System.out.println(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date()) + " " + ss);
    }
}
