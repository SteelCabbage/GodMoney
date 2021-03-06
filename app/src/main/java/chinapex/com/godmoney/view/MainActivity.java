package chinapex.com.godmoney.view;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import chinapex.com.godmoney.R;
import chinapex.com.godmoney.adapter.AddressRVA;
import chinapex.com.godmoney.bean.Nep5TxBean;
import chinapex.com.godmoney.bean.TxRecord;
import chinapex.com.godmoney.db.GodMoneyDbDao;
import chinapex.com.godmoney.global.Constant;
import chinapex.com.godmoney.global.GodMoneyApplication;
import chinapex.com.godmoney.task.TaskController;
import chinapex.com.godmoney.task.callback.ICreateNep5TxCallback;
import chinapex.com.godmoney.task.callback.IFromKeystoreToWalletCallback;
import chinapex.com.godmoney.task.callback.IReadAccountsCallback;
import chinapex.com.godmoney.task.callback.ISendRawTransactionCallback;
import chinapex.com.godmoney.task.callback.IWriteRecordsCallback;
import chinapex.com.godmoney.task.runnable.CreateNep5Tx;
import chinapex.com.godmoney.task.runnable.FromKeystoreToWallet;
import chinapex.com.godmoney.task.runnable.ReadAccounts;
import chinapex.com.godmoney.task.runnable.SendRawTransaction;
import chinapex.com.godmoney.task.runnable.WriteRecords;
import chinapex.com.godmoney.utils.CpLog;
import chinapex.com.godmoney.utils.PhoneUtils;
import chinapex.com.godmoney.utils.ToastUtils;
import neomobile.Tx;
import neomobile.Wallet;

public class MainActivity extends BaseActivity implements SwipeRefreshLayout
        .OnRefreshListener, AddressRVA.OnItemClickListener, View.OnClickListener,
        IFromKeystoreToWalletCallback, IReadAccountsCallback, IWriteRecordsCallback {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int REQUEST_PERMISSION = 201;
    private SwipeRefreshLayout mSl_address;
    private RecyclerView mRv_address;
    private List<TxRecord> mTxRecords;
    private AddressRVA mAddressRVA;
    private EditText mEt_keystore;
    private EditText mEt_keystore_pwd;
    private Wallet mGodWallet;
    private TextView mTv_god_address;
    private Button mBt_generate_records;
    private boolean isCanRefresh = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkPermission();
        initData();
        initView();
    }

    private void checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                    || checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA},
                        REQUEST_PERMISSION);
            }
        }
    }

    private void initData() {
        mTxRecords = new ArrayList<>();
        GodMoneyDbDao godMoneyDbDao = GodMoneyDbDao.getInstance(GodMoneyApplication.getInstance());
        if (null == godMoneyDbDao) {
            CpLog.e(TAG, "initData() -> godMoneyDbDao is null!");
            return;
        }

        List<TxRecord> txRecords = godMoneyDbDao.queryTxRecords();
        if (null == txRecords || txRecords.isEmpty()) {
            CpLog.e(TAG, "initData() -> txRecords is null or empty!");
            return;
        }

        mTxRecords.addAll(txRecords);
    }

    private void initView() {
        mEt_keystore = (EditText) findViewById(R.id.et_keystore);
        mEt_keystore_pwd = (EditText) findViewById(R.id.et_keystore_pwd);
        mTv_god_address = (TextView) findViewById(R.id.tv_god_address);
        mSl_address = (SwipeRefreshLayout) findViewById(R.id.sl_address);
        mRv_address = (RecyclerView) findViewById(R.id.rv_address);
        mBt_generate_records = (Button) findViewById(R.id.bt_generate_records);

        Button bt_generate_wallet = (Button) findViewById(R.id.bt_generate_wallet);
        Button bt_import_addresses = (Button) findViewById(R.id.bt_import_addresses);
        Button bt_god_send = (Button) findViewById(R.id.bt_god_send);

        mAddressRVA = new AddressRVA(mTxRecords);
        mAddressRVA.setOnItemClickListener(this);
        mRv_address.setLayoutManager(new LinearLayoutManager(GodMoneyApplication.getInstance(),
                LinearLayoutManager.VERTICAL, false));
        mRv_address.setAdapter(mAddressRVA);

        mSl_address.setColorSchemeColors(getResources().getColor(R.color.c_4C8EFA, null));
        mSl_address.setOnRefreshListener(this);

        bt_generate_wallet.setOnClickListener(this);
        bt_import_addresses.setOnClickListener(this);
        bt_god_send.setOnClickListener(this);
        mBt_generate_records.setOnClickListener(this);
    }

    @Override
    public void onItemClick(int position) {
        if (null == mTxRecords || mTxRecords.isEmpty()) {
            CpLog.e(TAG, "onItemClick() -> mTxRecords is null or empty!");
            return;
        }

        TxRecord txRecord = mTxRecords.get(position);
        if (null == txRecord) {
            CpLog.e(TAG, "onItemClick() -> txRecord is null!");
            return;
        }

        String txId = txRecord.getTxId();
        if (TextUtils.isEmpty(txId)) {
            CpLog.e(TAG, "txId is null!");
            return;
        }

        PhoneUtils.copy2Clipboard(GodMoneyApplication.getInstance(), txId);
        ToastUtils.getInstance().showToast("num: " + position + ", copy success! txId: " + txId);
    }

    @Override
    public void onRefresh() {
        if (!isCanRefresh) {
            mSl_address.setRefreshing(false);
            ToastUtils.getInstance().showToast("交易记录还未写入文件！请不要刷新！");
            CpLog.e(TAG, "txRecords have not been written! Don't refresh!");
            return;
        }

        loadTxRecords();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_generate_wallet:
                String keystore = mEt_keystore.getText().toString().trim();
                String pwd = mEt_keystore_pwd.getText().toString().trim();
                if (TextUtils.isEmpty(keystore) || TextUtils.isEmpty(pwd)) {
                    ToastUtils.getInstance().showToast("keystore or pwd is null!");
                    CpLog.e(TAG, "keystore or pwd is null!");
                    return;
                }

                TaskController.getInstance().submit(new FromKeystoreToWallet(keystore, pwd, this));
                break;
            case R.id.bt_import_addresses:
                readFile();
                break;
            case R.id.bt_god_send:
                startNep5Tx();
                break;
            case R.id.bt_generate_records:
                writeFile();
                break;
            default:
                break;
        }
    }

    @Override
    public void fromKeystoreWallet(Wallet wallet) {
        if (null == wallet) {
            CpLog.e(TAG, "wallet is null!");
            ToastUtils.getInstance().showToast("wallet is null!");
            return;
        }

        mGodWallet = wallet;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mTv_god_address.setText(mGodWallet.address());
            }
        });
    }

    private void readFile() {
        TaskController.getInstance().submit(new ReadAccounts(this));
    }

    @Override
    public void readAccounts(final List<TxRecord> txRecords) {
        if (null == txRecords || txRecords.isEmpty()) {
            CpLog.e(TAG, "readAccounts() -> txRecords is null or empty!");
            return;
        }

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                CpLog.i(TAG, "readAccounts() -> import addresses ok!");
                ToastUtils.getInstance().showToast("import addresses ok!");
            }
        });

        loadTxRecords();
    }

    private void writeFile() {
        TaskController.getInstance().submit(new WriteRecords(this, mTxRecords));
    }

    @Override
    public void writeRecords(final boolean writeState) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (writeState) {
                    ToastUtils.getInstance().showToast("交易记录写入完成");
                    isCanRefresh = true;
                } else {
                    ToastUtils.getInstance().showToast("交易记录写入异常");
                }
            }
        });
    }

    private void loadTxRecords() {
        GodMoneyDbDao godMoneyDbDao = GodMoneyDbDao.getInstance(GodMoneyApplication.getInstance());
        if (null == godMoneyDbDao) {
            CpLog.e(TAG, "godMoneyDbDao is null!");
            return;
        }

        final List<TxRecord> txRecords = godMoneyDbDao.queryTxRecords();
        if (null == txRecords || txRecords.isEmpty()) {
            CpLog.e(TAG, "loadTxRecords() -> txRecords is null or empty!");

            if (mSl_address.isRefreshing()) {
                mSl_address.setRefreshing(false);
            }

            return;
        }

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                int preClearSize = mTxRecords.size();
                mTxRecords.clear();
                mAddressRVA.notifyItemRangeRemoved(0, preClearSize);

                mTxRecords.addAll(txRecords);
                int size = mTxRecords.size();
                mAddressRVA.notifyItemRangeInserted(0, size);

                if (mSl_address.isRefreshing()) {
                    mSl_address.setRefreshing(false);
                }
            }
        });

    }

    private void startNep5Tx() {
        if (null == mGodWallet) {
            CpLog.e(TAG, "startNep5Tx() -> mGodWallet is null!");
            ToastUtils.getInstance().showToast("人类！上帝不在线哟~~");
            return;
        }

        if (null == mTxRecords || mTxRecords.isEmpty()) {
            CpLog.e(TAG, "startNep5Tx() -> mTxRecords is null or empty!");
            return;
        }

        isCanRefresh = false;

        for (final TxRecord txRecord : mTxRecords) {
            if (null == txRecord) {
                CpLog.e(TAG, "txRecord is null!");
                continue;
            }

            Nep5TxBean nep5TxBean = new Nep5TxBean();
            nep5TxBean.setAssetID(Constant.ASSET_CPX);
            nep5TxBean.setAssetDecimal(8);
            nep5TxBean.setAddrFrom(mGodWallet.address());
            nep5TxBean.setAddrTo(txRecord.getAddress());
            nep5TxBean.setTransferAmount(txRecord.getAmount());
            nep5TxBean.setUtxos("[]");

            TaskController.getInstance().submit(new CreateNep5Tx(mGodWallet, nep5TxBean, new
                    ICreateNep5TxCallback() {


                        @Override
                        public void createNep5Tx(Tx tx) {
                            if (null == tx) {
                                CpLog.e(TAG, "tx is null！");
                                return;
                            }

                            final String order = "0x" + tx.getID();
                            CpLog.i(TAG, "createNep5Tx order:" + order);
                            CpLog.i(TAG, "createNep5Tx data:" + tx.getData());

                            TaskController.getInstance().submit(new SendRawTransaction(tx.getData(),
                                    new ISendRawTransactionCallback() {

                                        @Override
                                        public void sendTxData(Boolean isSuccess) {
                                            txRecord.setTxId(order);
                                            if (isSuccess) {
                                                CpLog.i(TAG, "order broadcast ok:" + order);
                                                txRecord.setState(1);
                                            } else {
                                                CpLog.e(TAG, "order broadcast fail:" + order);
                                                txRecord.setState(0);
                                            }
                                        }
                                    }));
                        }
                    }));
        }
    }

}
