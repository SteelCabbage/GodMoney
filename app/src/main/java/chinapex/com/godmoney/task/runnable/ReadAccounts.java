package chinapex.com.godmoney.task.runnable;

import android.content.res.AssetManager;
import android.os.Environment;
import android.text.TextUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import chinapex.com.godmoney.bean.TxRecord;
import chinapex.com.godmoney.db.GodMoneyDbDao;
import chinapex.com.godmoney.global.GodMoneyApplication;
import chinapex.com.godmoney.task.callback.IReadAccountsCallback;
import chinapex.com.godmoney.utils.CpLog;
import neomobile.Tx;

/**
 * Created by SteelCabbage on 2018/7/20 0020 15:50.
 * E-Mail：liuyi_61@163.com
 */

public class ReadAccounts implements Runnable {
    private static final String TAG = ReadAccounts.class.getSimpleName();
    private IReadAccountsCallback mIReadAccountsCallback;

    public ReadAccounts(IReadAccountsCallback IReadAccountsCallback) {
        mIReadAccountsCallback = IReadAccountsCallback;
    }

    @Override
    public void run() {
        if (null == mIReadAccountsCallback) {
            CpLog.e(TAG, "mIReadAccountsCallback is null!");
            return;
        }

        List<String> accounts = readFile();
        if (null == accounts || accounts.isEmpty()) {
            CpLog.e(TAG, "accounts is null or empty!");
            return;
        }

        GodMoneyDbDao godMoneyDbDao = GodMoneyDbDao.getInstance(GodMoneyApplication.getInstance());
        if (null == godMoneyDbDao) {
            CpLog.e(TAG, "godMoneyDbDao is null!");
            return;
        }

        List<TxRecord> txRecords = new ArrayList<>();
        for (String account : accounts) {
            if (TextUtils.isEmpty(account)) {
                CpLog.e(TAG, "account is null or empty!");
                continue;
            }

            String[] split = account.split(",");
            if (split.length != 2) {
                CpLog.e(TAG, "split.length != 2");
                continue;
            }

            TxRecord txRecord = new TxRecord();
            txRecord.setAddress(split[0].trim());
            txRecord.setAmount(split[1].trim());
            txRecord.setState(-1);

            txRecords.add(txRecord);
            godMoneyDbDao.insertTxRecord(txRecord);
        }

        mIReadAccountsCallback.readAccounts(txRecords);
    }

    private List<String> readFile() {
        File externalStorageDirectory = Environment.getExternalStorageDirectory();
        File file = new File(externalStorageDirectory, "0Apex/apex.txt");
        CpLog.i(TAG, "file:" + file.toString());
        if (!file.exists()) {
            CpLog.e(TAG, "file is not exist!");
            return null;
        }

        List<String> txtList = new ArrayList<>();
        try {
            InputStream inputStream = new FileInputStream(file);
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            String line;
            while ((line = bufferedReader.readLine()) != null) {
                txtList.add(line);
            }
            inputStream.close();
        } catch (FileNotFoundException e) {
            CpLog.e(TAG, "FileNotFoundException:" + e.getMessage());
        } catch (IOException e) {
            CpLog.e(TAG, "IOException:" + e.getMessage());
        }
        return txtList;
    }

}
