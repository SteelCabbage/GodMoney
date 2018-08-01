package chinapex.com.godmoney.task.runnable;

import android.os.Environment;
import android.text.TextUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import chinapex.com.godmoney.bean.TxRecord;
import chinapex.com.godmoney.task.callback.IWriteRecordsCallback;
import chinapex.com.godmoney.utils.CpLog;

/**
 * Created by SteelCabbage on 2018/8/1 0001 20:22.
 * E-Mail：liuyi_61@163.com
 */

public class WriteRecords implements Runnable {
    private static final String TAG = WriteRecords.class.getSimpleName();
    private IWriteRecordsCallback mIWriteRecordsCallback;
    List<TxRecord> mTxRecords;

    public WriteRecords(IWriteRecordsCallback IWriteRecordsCallback, List<TxRecord> txRecords) {
        mIWriteRecordsCallback = IWriteRecordsCallback;
        mTxRecords = txRecords;
    }

    @Override
    public void run() {
        if (null == mIWriteRecordsCallback) {
            CpLog.e(TAG, "mIWriteRecordsCallback is null!");
            return;
        }

        if (null == mTxRecords || mTxRecords.isEmpty()) {
            CpLog.e(TAG, "mTxRecords is null or empty!");
            mIWriteRecordsCallback.writeRecords(false);
            return;
        }

        ArrayList<String> records = new ArrayList<>();
        for (TxRecord txRecord : mTxRecords) {
            if (null == txRecord) {
                CpLog.e(TAG, "txRecord is null!");
                continue;
            }

            CpLog.i(TAG, "txRecord:" + txRecord.getAddress() + ","
                    + txRecord.getAmount() + ","
                    + txRecord.getTxId());

            records.add(txRecord.getAddress() + ","
                    + txRecord.getAmount() + ","
                    + txRecord.getTxId());
        }

        boolean writeResult = writeFile(records);
        if (writeResult) {
            mIWriteRecordsCallback.writeRecords(true);
        } else {
            mIWriteRecordsCallback.writeRecords(false);
        }
    }

    private boolean writeFile(List<String> records) {
        if (null == records || records.isEmpty()) {
            CpLog.e(TAG, "records is null or empty!");
            return false;
        }

        File externalStorageDirectory = Environment.getExternalStorageDirectory();
        File file = new File(externalStorageDirectory, "0Apex/apexRecords.txt");
        CpLog.i(TAG, "file:" + file.toString());
        if (!file.exists()) {
            CpLog.i(TAG, "file is not exist!");
            try {
                boolean newFile = file.createNewFile();
                CpLog.i(TAG, "newFile:" + newFile);
            } catch (IOException e) {
                CpLog.e(TAG, "IOException:" + e.getMessage());
            }
        }

        FileOutputStream fos;
        try {
            fos = new FileOutputStream(file);
            OutputStreamWriter osw = new OutputStreamWriter(fos, "UTF-8");
            BufferedWriter bw = new BufferedWriter(osw);
            for (String record : records) {
                if (TextUtils.isEmpty(record)) {
                    CpLog.e(TAG, "record is null or empty!");
                    continue;
                }
                bw.write(record + "\t\n");
            }

            bw.close();
            osw.close();
            fos.close();
        } catch (FileNotFoundException e) {
            CpLog.e(TAG, "FileNotFoundException:" + e.getMessage());
            return false;
        } catch (UnsupportedEncodingException e) {
            CpLog.e(TAG, "UnsupportedEncodingException:" + e.getMessage());
            return false;
        } catch (IOException e) {
            CpLog.e(TAG, "IOException:" + e.getMessage());
            return false;
        }

        return true;
    }
}
