package Demo;

import NEO.*;
import NEO.Core.*;
import NEO.Core.Scripts.Program;
import NEO.IO.Json.JArray;
import NEO.IO.Json.JNumber;
import NEO.IO.Json.JObject;
import NEO.IO.Json.JString;
import NEO.Wallets.Account;
import NEO.Wallets.Contract;
import NEO.Wallets.Wallet;
import NEO.sdk.wallet.AccountManager;

import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class RawTxDemo {

    // exported WIF from wallet
    public static final String WALLET_WIF2 = "KxDgvEKzgSBPPfuVfw67oPQBSjidEiqTHURKSDL1R7yGaGYAeYnr";
    public static final String WALLET_WIF1 = "L4vr3XmcqGNJzwVfABiVtZbJajhNUWv6ny9Ggv7HYbhBy8rxMReU";
    public Contract contract;

    public static void main(String[] args) throws Exception {

        // import setting
        Settings.getSettings(Settings.CONFIG_PRIVNET);

        // new AccountManager
        String neoUrl = Settings.RPC_LIST.get(0);
        String neoToken = "";
        String path = "./1.db3";
        AccountManager wm = AccountManager.getWallet(path, neoUrl, neoToken);
        System.out.println("[param=" + neoUrl + "," + path + "]");
        System.out.println("start to test....hh:" + wm.getBlockHeight());

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
        ContractTransaction tx = new ContractTransaction();

        // neo input
        TransactionInput inputN = new TransactionInput();
        inputN.prevIndex = 0;
        inputN.prevHash = UInt256.parse("072497994952d898e4f302d7b7022cbfd368d2b2ede4f51a0a3e41af22278e87");

        // gas input
        TransactionInput inputG = new TransactionInput();
        inputG.prevIndex = 0;
        inputG.prevHash = UInt256.parse("ede0cfea66811f194c1d82b8d8377f941c8cc8bae3adfed0a77c5eb7bb5e96a9");

        // neo output
        TransactionOutput outputN = new TransactionOutput();
        outputN.assetId = Blockchain.GoverningToken;
        outputN.value = Fixed8.ONE;
        outputN.scriptHash = Wallet.toScriptHash(address2);

        // gas output
        TransactionOutput outputG = new TransactionOutput();
        outputG.assetId = Blockchain.UtilityToken;
        outputG.value = new Fixed8((long)(0.999 * (long)Math.pow(10, 8)));
        outputG.scriptHash = Wallet.toScriptHash(address1);

        tx.version = 0;
        tx.attributes = new TransactionAttribute[0];
        tx.inputs = new TransactionInput[2];
        tx.inputs[0] = inputN;
        tx.inputs[1] = inputG;
        tx.outputs = new TransactionOutput[2];
        tx.outputs[0] = outputN;
        tx.outputs[1] = outputG;
        tx.scripts = new Program[0];

        System.out.println(tx.hash().toString());

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
}
