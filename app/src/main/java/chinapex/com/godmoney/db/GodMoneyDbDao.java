package chinapex.com.godmoney.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import chinapex.com.godmoney.bean.TxRecord;
import chinapex.com.godmoney.global.Constant;
import chinapex.com.godmoney.utils.CpLog;

/**
 * Created by SteelCabbage on 2018/3/29 0029.
 */

public class GodMoneyDbDao {

    private static final String TAG = GodMoneyDbDao.class.getSimpleName();

    private static GodMoneyDbDao sGodMoneyDbDao;

    private AtomicInteger mOpenCounter = new AtomicInteger();

    private GodMoneyDbHelper mGodMoneyDbHelper;

    private SQLiteDatabase mDatabase;

    private GodMoneyDbDao(Context context) {
        mGodMoneyDbHelper = new GodMoneyDbHelper(context);
    }

    public static GodMoneyDbDao getInstance(Context context) {
        if (null == context) {
            CpLog.e(TAG, "context is null!");
            return null;
        }

        if (null == sGodMoneyDbDao) {
            synchronized (GodMoneyDbDao.class) {
                if (null == sGodMoneyDbDao) {
                    sGodMoneyDbDao = new GodMoneyDbDao(context);
                }
            }
        }

        return sGodMoneyDbDao;
    }

    public synchronized SQLiteDatabase openDatabase() {
        if (mOpenCounter.incrementAndGet() == 1) {
            mDatabase = mGodMoneyDbHelper.getWritableDatabase();
        }
        return mDatabase;
    }

    public synchronized void closeDatabase() {
        if (mOpenCounter.decrementAndGet() == 0) {
            mDatabase.close();
        }
    }

    public synchronized void insertTxRecord(TxRecord txRecord) {
        if (null == txRecord) {
            CpLog.e(TAG, "insertTxRecord() -> txRecord is null!");
            return;
        }

        ContentValues contentValues = new ContentValues();
        contentValues.put(Constant.FIELD_TX_ID, txRecord.getTxId());
        contentValues.put(Constant.FIELD_TX_ADDRESS, txRecord.getAddress());
        contentValues.put(Constant.FIELD_TX_AMOUNT, txRecord.getAmount());
        contentValues.put(Constant.FIELD_TX_STATE, txRecord.getState());
        contentValues.put(Constant.FIELD_TX_TIME, txRecord.getTxTime());


        SQLiteDatabase db = openDatabase();
        try {
            db.beginTransaction();
            db.insertOrThrow(Constant.TABLE_TX_RECORD, null, contentValues);
            db.setTransactionSuccessful();
            CpLog.i(TAG, "insertTxRecord() -> insert " + txRecord.getAddress() + " ok!");
        } catch (SQLException e) {
            CpLog.e(TAG, "insertTxRecord exception:" + e.getMessage());
        } finally {
            db.endTransaction();
        }
        closeDatabase();
    }

    public List<TxRecord> queryTxRecords() {
        List<TxRecord> txRecords = new ArrayList<>();
        SQLiteDatabase db = openDatabase();
        Cursor cursor = db.query(Constant.TABLE_TX_RECORD, null, null, null, null, null, null);
        if (null != cursor) {
            while (cursor.moveToNext()) {
                int txIdIndex = cursor.getColumnIndex(Constant.FIELD_TX_ID);
                int txAddressIndex = cursor.getColumnIndex(Constant.FIELD_TX_ADDRESS);
                int txAmountIndex = cursor.getColumnIndex(Constant.FIELD_TX_AMOUNT);
                int txStateIndex = cursor.getColumnIndex(Constant.FIELD_TX_STATE);
                int txTimeIndex = cursor.getColumnIndex(Constant.FIELD_TX_TIME);

                String txId = cursor.getString(txIdIndex);
                String txAddress = cursor.getString(txAddressIndex);
                String txAmount = cursor.getString(txAmountIndex);
                int txState = cursor.getInt(txStateIndex);
                long txTime = cursor.getLong(txTimeIndex);

                TxRecord txRecord = new TxRecord();
                txRecord.setTxId(txId);
                txRecord.setAddress(txAddress);
                txRecord.setAmount(txAmount);
                txRecord.setState(txState);
                txRecord.setTxTime(txTime);

                txRecords.add(txRecord);
            }
            cursor.close();
        }
        closeDatabase();
        return txRecords;
    }

    private static final String WHERE_CLAUSE_TX_ADDRESS_EQ = Constant.FIELD_TX_ADDRESS + " = ?";

    public void updateTxRecord(String address, String txID, int txState) {
        if (TextUtils.isEmpty(address) || TextUtils.isEmpty(txID)) {
            CpLog.e(TAG, "updateTxRecord() -> address or txID is null!");
            return;
        }

        ContentValues contentValues = new ContentValues();
        contentValues.put(Constant.FIELD_TX_ID, txID);
        contentValues.put(Constant.FIELD_TX_STATE, txState);

        SQLiteDatabase db = openDatabase();
        try {
            db.beginTransaction();
            db.update(Constant.TABLE_TX_RECORD,
                    contentValues,
                    WHERE_CLAUSE_TX_ADDRESS_EQ,
                    new String[]{address});
            db.setTransactionSuccessful();
            CpLog.i(TAG, "updateTxRecord -> update: " + address + " ok!");
        } catch (SQLException e) {
            CpLog.e(TAG, "updateTxState exception:" + e.getMessage());
        } finally {
            db.endTransaction();
        }
        closeDatabase();
    }

}
