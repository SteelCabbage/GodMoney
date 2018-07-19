package chinapex.com.godmoney.global;

/**
 * Created by SteelCabbage on 2018/7/19 0019 14:18.
 * E-Mail：liuyi_61@163.com
 */

public class Constant {

    // net
    public static final long CONNECT_TIMEOUT = 5;
    public static final long READ_TIMEOUT = 5;
    public static final long WRITE_TIMEOUT = 5;
    public static final int NET_ERROR = -1;
    public static final int NET_SUCCESS = 1;
    public static final int NET_BODY_NULL = 0;

    // neo正式网
    public static final String HOSTNAME_VERIFIER = "tracker.chinapex.com.cn";
    public static final String URL_CLI = "https://tracker.chinapex.com.cn/neo-cli/";
    public static final String URL_UTXOS = "https://tracker.chinapex.com.cn/tool/utxos/";

    // asset
    public static final String ASSET_CPX = "0x45d493a6f73fa5f404244a5fb8472fc014ca5885";
}
