package chinapex.com.godmoney.bean;

/**
 * Created by SteelCabbage on 2018/7/19 0019 11:19.
 * E-Mail：liuyi_61@163.com
 */

public class TxRecord {
    private String txId;
    private String address;
    private String amount;
    private int state;
    private long txTime;

    public String getTxId() {
        return txId;
    }

    public void setTxId(String txId) {
        this.txId = txId;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public long getTxTime() {
        return txTime;
    }

    public void setTxTime(long txTime) {
        this.txTime = txTime;
    }
}
