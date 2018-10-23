package NEO;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class Settings {

    public static final String CONFIG_MAINNET = "protocol.mainnet.json";
    public static final String CONFIG_TESTNET = "protocol.testnet.json";
    public static final String CONFIG_PRIVNET = "protocol.privnet.json";


    public static JSONObject PROTOCOL_CONFIGURATION;

    public static long MAGIC;
    public static long ADDRESS_VERSION;
    public static List<String> STANDBY_VALIDATORS;
    public static List<String> SEED_LIST;
    public static List<String> RPC_LIST;

    public static JSONObject ALL_FEES;
    public static long ENROLLMENT_TX_FEE;
    public static long ISSUE_TX_FEE;
    public static long PUBLISH_TX_FEE;
    public static long REGISTER_TX_FEE;


    public static JSONObject APPLICATION_CONFIGURATION;

    public static String DATA_DIRECTORY_PATH;
    public static String NOTIFICATION_DATA_PATH;
    public static long RPC_PORT;
    public static long NODE_PORT;
    public static long WS_PORT;
    public static List<String> URI_PREFIX;
    public static String SSL_CERT;
    public static String SSL_CERT_PASSWORD;
    public static String BOOTSTRAP_FILE;
    public static String NOTIFICATION_BOOTSTRAP_FILE;
    public static long DEBUG_STORAGE;

    private Settings(){
    }

    public static void getSettings() throws IOException{
        getSettings(CONFIG_TESTNET);
    }

    public static void getSettings(String config_file) throws IOException{

        // Need a better path solution
        String path = Settings.class.getClassLoader().getResource(config_file).getPath();
        InputStream is = new FileInputStream(path);

        byte[] bys = new byte[is.available()];
        is.read(bys);
        is.close();
        JSONObject json = JSONObject.parseObject(new String(bys));

        PROTOCOL_CONFIGURATION = json.getJSONObject("ProtocolConfiguration");

        MAGIC = PROTOCOL_CONFIGURATION.getLong("Magic");
        ADDRESS_VERSION = PROTOCOL_CONFIGURATION.getLong("AddressVersion");
        STANDBY_VALIDATORS = JSON.parseArray(PROTOCOL_CONFIGURATION.getJSONArray("StandbyValidators").toString(), String.class);
        SEED_LIST = JSON.parseArray(PROTOCOL_CONFIGURATION.getJSONArray("SeedList").toString(), String.class);
        RPC_LIST = JSON.parseArray(PROTOCOL_CONFIGURATION.getJSONArray("RPCList").toString(), String.class);

        ALL_FEES = PROTOCOL_CONFIGURATION.getJSONObject("SystemFee");

        ENROLLMENT_TX_FEE = ALL_FEES.getLong("EnrollmentTransaction");
        ISSUE_TX_FEE = ALL_FEES.getLong("IssueTransaction");
        PUBLISH_TX_FEE = ALL_FEES.getLong("PublishTransaction");
        REGISTER_TX_FEE = ALL_FEES.getLong("RegisterTransaction");


        APPLICATION_CONFIGURATION = json.getJSONObject("ApplicationConfiguration");
    }
}