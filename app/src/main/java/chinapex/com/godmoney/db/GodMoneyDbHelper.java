package chinapex.com.godmoney.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import chinapex.com.godmoney.global.Constant;

/**
 * Created by SteelCabbage on 2018/3/28 0028.
 */

public class GodMoneyDbHelper extends SQLiteOpenHelper {

    private static final String TAG = GodMoneyDbHelper.class.getSimpleName();

    private static final int DB_VERSION = 1;

    private static final String DB_NAME = "godData";

    public GodMoneyDbHelper(Context context, String name, SQLiteDatabase.CursorFactory factory,
                            int version) {
        super(context, name, factory, version);
    }

    public GodMoneyDbHelper(Context context) {
        this(context, DB_NAME, null, DB_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(Constant.SQL_CREATE_TX_RECORD);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

}
