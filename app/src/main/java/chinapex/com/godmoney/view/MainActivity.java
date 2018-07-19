package chinapex.com.godmoney.view;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;

import chinapex.com.godmoney.R;
import chinapex.com.godmoney.adapter.AddressRVA;
import chinapex.com.godmoney.bean.Nep5TxBean;
import chinapex.com.godmoney.bean.TxRecord;
import chinapex.com.godmoney.global.Constant;
import chinapex.com.godmoney.global.GodMoneyApplication;
import chinapex.com.godmoney.task.TaskController;
import chinapex.com.godmoney.task.callback.ICreateNep5TxCallback;
import chinapex.com.godmoney.task.callback.IFromKeystoreToWalletCallback;
import chinapex.com.godmoney.task.callback.IGetUtxosCallback;
import chinapex.com.godmoney.task.callback.ISendRawTransactionCallback;
import chinapex.com.godmoney.task.runnable.CreateNep5Tx;
import chinapex.com.godmoney.task.runnable.FromKeystoreToWallet;
import chinapex.com.godmoney.task.runnable.GetUtxos;
import chinapex.com.godmoney.task.runnable.SendRawTransaction;
import chinapex.com.godmoney.utils.CpLog;
import chinapex.com.godmoney.utils.ToastUtils;
import neomobile.Tx;
import neomobile.Wallet;

public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout
        .OnRefreshListener, AddressRVA.OnItemClickListener, View.OnClickListener,
        IFromKeystoreToWalletCallback {

    private static final String TAG = MainActivity.class.getSimpleName();
    private SwipeRefreshLayout mSl_address;
    private RecyclerView mRv_address;
    private List<TxRecord> mTxRecords;
    private AddressRVA mAddressRVA;
    private EditText mEt_keystore;
    private EditText mEt_keystore_pwd;
    private Wallet mGodWallet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initData();
        initView();
    }

    private void initData() {
        mTxRecords = new ArrayList<>();
    }

    private void initView() {
        mEt_keystore = (EditText) findViewById(R.id.et_keystore);
        mEt_keystore_pwd = (EditText) findViewById(R.id.et_keystore_pwd);
        mSl_address = (SwipeRefreshLayout) findViewById(R.id.sl_address);
        mRv_address = (RecyclerView) findViewById(R.id.rv_address);

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
    }

    @Override
    public void onItemClick(int position) {

    }

    @Override
    public void onRefresh() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mSl_address.isRefreshing()) {
                    mSl_address.setRefreshing(false);
                }
            }
        });
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
                loadTxRecords();
                break;
            case R.id.bt_god_send:
                startNep5Tx();
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
    }

    private void loadTxRecords() {
        int preClearSize = mTxRecords.size();
        mTxRecords.clear();
        mAddressRVA.notifyItemRangeRemoved(0, preClearSize);
        for (int i = 0; i < 9; i++) {
            TxRecord txRecord = new TxRecord();
            txRecord.setAddress("ALDbmTMY54RZnLmibH3eXfHvrZt4fLiZhh");
            txRecord.setAmount("0.0000000" + i);
            txRecord.setState(-1);
            mTxRecords.add(txRecord);
        }
        int size = mTxRecords.size();
        mAddressRVA.notifyItemRangeInserted(0, size);
        ToastUtils.getInstance().showToast("import addresses ok!");
    }

    private void startNep5Tx() {
        if (null == mTxRecords || mTxRecords.isEmpty()) {
            CpLog.e(TAG, "mTxRecords is null or empty!");
            return;
        }

        for (TxRecord txRecord : mTxRecords) {
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

                            String order = "0x" + tx.getID();
                            CpLog.i(TAG, "createNep5Tx order:" + order);

                            TaskController.getInstance().submit(new SendRawTransaction(tx.getData(),
                                    new ISendRawTransactionCallback() {

                                        @Override
                                        public void sendTxData(Boolean isSuccess) {
                                            CpLog.i(TAG, "isSuccess:" + isSuccess);
                                        }
                                    }));
                        }
                    }));
        }
    }

}
