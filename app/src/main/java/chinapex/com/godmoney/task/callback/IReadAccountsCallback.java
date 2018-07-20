package chinapex.com.godmoney.task.callback;

import java.util.List;

import chinapex.com.godmoney.bean.TxRecord;

/**
 * Created by SteelCabbage on 2018/7/20 0020 15:50.
 * E-Mail：liuyi_61@163.com
 */

public interface IReadAccountsCallback {
    void readAccounts(List<TxRecord> txRecords);
}
