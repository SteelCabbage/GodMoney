package chinapex.com.godmoney.task.runnable;

import java.util.ArrayList;

import chinapex.com.godmoney.bean.request.RequestSendRawTransaction;
import chinapex.com.godmoney.bean.response.ResponseSendRawTransaction;
import chinapex.com.godmoney.global.Constant;
import chinapex.com.godmoney.net.INetCallback;
import chinapex.com.godmoney.net.OkHttpClientManager;
import chinapex.com.godmoney.task.callback.ISendRawTransactionCallback;
import chinapex.com.godmoney.utils.CpLog;
import chinapex.com.godmoney.utils.GsonUtils;

/**
 * Created by SteelCabbage on 2018/5/30 0030.
 */

public class SendRawTransaction implements Runnable, INetCallback {

    private static final String TAG = SendRawTransaction.class.getSimpleName();
    private String mTxData;
    private ISendRawTransactionCallback mISendRawTransactionCallback;

    public SendRawTransaction(String txData, ISendRawTransactionCallback
            iSendRawTransactionCallback) {
        mTxData = txData;
        mISendRawTransactionCallback = iSendRawTransactionCallback;
    }

    @Override
    public void run() {
        if (null == mTxData) {
            CpLog.e(TAG, "mTxData is null!");
            return;
        }

        if (null == mISendRawTransactionCallback) {
            CpLog.e(TAG, "mISendRawTransactionCallback is null!");
            return;
        }

        RequestSendRawTransaction requestSendRawTransaction = new RequestSendRawTransaction();
        requestSendRawTransaction.setJsonrpc("2.0");
        requestSendRawTransaction.setMethod("sendrawtransaction");
        ArrayList<String> sendDatas = new ArrayList<>();
        sendDatas.add(mTxData);
        requestSendRawTransaction.setParams(sendDatas);
        requestSendRawTransaction.setId(1);

        OkHttpClientManager.getInstance().postJson(Constant.URL_CLI, GsonUtils.toJsonStr
                (requestSendRawTransaction), this);
    }

    @Override
    public void onSuccess(int statusCode, String msg, String result) {
        ResponseSendRawTransaction responseSendRawTransaction = GsonUtils.json2Bean(result,
                ResponseSendRawTransaction.class);
        if (null == responseSendRawTransaction) {
            mISendRawTransactionCallback.sendTxData(false);
            return;
        }

        CpLog.i(TAG, "onSuccess() -> broadcast:" + responseSendRawTransaction.isResult());
        mISendRawTransactionCallback.sendTxData(responseSendRawTransaction.isResult());
    }

    @Override
    public void onFailed(int failedCode, String msg) {
        CpLog.e(TAG, "onFailed() -> msg:" + msg);
        mISendRawTransactionCallback.sendTxData(false);
    }
}
