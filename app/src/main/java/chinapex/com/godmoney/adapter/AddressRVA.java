package chinapex.com.godmoney.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import chinapex.com.godmoney.R;
import chinapex.com.godmoney.bean.TxRecord;
import chinapex.com.godmoney.utils.CpLog;

public class AddressRVA extends RecyclerView.Adapter<AddressRVA.AddressRVAHolder> implements View
        .OnClickListener {

    private static final String TAG = AddressRVA.class.getSimpleName();
    private OnItemClickListener mOnItemClickListener;
    private List<TxRecord> mTxRecords;


    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public AddressRVA(List<TxRecord> txRecords) {
        mTxRecords = txRecords;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    @Override
    public void onClick(View v) {
        if (null == mOnItemClickListener) {
            CpLog.e(TAG, "mOnItemClickListener is null!");
            return;
        }
        mOnItemClickListener.onItemClick((Integer) v.getTag());
    }

    @NonNull
    @Override
    public AddressRVAHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_tx_records,
                parent, false);
        AddressRVAHolder holder = new AddressRVAHolder(view);
        view.setOnClickListener(this);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull AddressRVAHolder holder, int position) {
        TxRecord txRecord = mTxRecords.get(position);
        if (null == txRecord) {
            CpLog.e(TAG, "txRecord is null!");
            return;
        }

        holder.num.setText(String.valueOf(position));
        holder.address.setText(txRecord.getAddress());
        holder.txId.setText(txRecord.getTxId());
        holder.amount.setText(txRecord.getAmount());
        switch (txRecord.getState()) {
            case -1:
                holder.state.setText("unknown");
                break;
            case 0:
                holder.state.setText("fail");
                break;
            case 1:
                holder.state.setText("ok");
                break;
            default:
                holder.state.setText("exception");
                break;
        }

        holder.itemView.setTag(position);
    }

    @Override
    public int getItemCount() {
        return null == mTxRecords ? 0 : mTxRecords.size();
    }

    class AddressRVAHolder extends RecyclerView.ViewHolder {
        TextView num;
        TextView address;
        TextView txId;
        TextView amount;
        TextView state;

        AddressRVAHolder(View itemView) {
            super(itemView);
            num = itemView.findViewById(R.id.tv_address_num);
            address = itemView.findViewById(R.id.tv_payee_address);
            txId = itemView.findViewById(R.id.tv_tx_id);
            amount = itemView.findViewById(R.id.tv_amount);
            state = itemView.findViewById(R.id.tv_state);
        }
    }
}
