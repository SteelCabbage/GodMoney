package chinapex.com.godmoney.global;

/**
 * Created by SteelCabbage on 2018/7/19 0019 14:18.
 * E-Mail：liuyi_61@163.com
 */

public class Constant {

    // net
    public static final long CONNECT_TIMEOUT = 20;
    public static final long READ_TIMEOUT = 20;
    public static final long WRITE_TIMEOUT = 20;
    public static final int NET_ERROR = -1;
    public static final int NET_SUCCESS = 1;
    public static final int NET_BODY_NULL = 0;

    // hostname verifier
    public static final String HOSTNAME_VERIFIER = "tracker.chinapex.com.cn";

    // java server root path
    public static final String SERVER_ROOT_PATH_JAVA = "https://tracker.chinapex.com.cn/tool/jar-serv/";

    // neo正式网
    public static final String URL_CLI = "https://tracker.chinapex.com.cn/neo-cli-2-9/";
    public static final String URL_UTXOS = SERVER_ROOT_PATH_JAVA + "utxos?address=";

    // asset
    public static final String ASSET_CPX = "0x45d493a6f73fa5f404244a5fb8472fc014ca5885";

    // table tx record
    public static final String TABLE_TX_RECORD = "tx_record";

    public static final String FIELD_ID = "_id";
    public static final String FIELD_TX_ID = "tx_id";
    public static final String FIELD_TX_ADDRESS = "tx_address";
    public static final String FIELD_TX_AMOUNT = "tx_amount";
    public static final String FIELD_TX_STATE = "tx_state";
    public static final String FIELD_TX_TIME = "tx_time";


    public static final String SQL_CREATE_TX_RECORD = "create table " +
            TABLE_TX_RECORD
            + " (" + FIELD_ID + " integer primary key autoincrement, "
            + FIELD_TX_ID + " text, "
            + FIELD_TX_ADDRESS + " text, "
            + FIELD_TX_AMOUNT + " text, "
            + FIELD_TX_STATE + " integer, "
            + FIELD_TX_TIME + " integer)";
}
