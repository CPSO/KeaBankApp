package com.example.keabankapp.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.keabankapp.R;
import com.example.keabankapp.models.AccountTransactionModel;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class AccountTransferAdapter extends FirestoreRecyclerAdapter<AccountTransactionModel, AccountTransferAdapter.TransferHolder > {


    public AccountTransferAdapter(@NonNull FirestoreRecyclerOptions<AccountTransactionModel> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull TransferHolder holder, int position, @NonNull AccountTransactionModel model) {
        holder.tvTransListAmount.setText(Double.toString(model.gettAmount()));
        holder.tvTransListType.setText(model.gettType());
        holder.tvTransListTime.setText(model.gettTimestamp().toString());

    }

    @NonNull
    @Override
    public TransferHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.transactions_list,viewGroup,false);
        return new TransferHolder(v);

    }

    class TransferHolder extends RecyclerView.ViewHolder{
        TextView  tvTransListAmount;
        TextView tvTransListTime;
        TextView tvTransListType;

        public TransferHolder(@NonNull View itemView) {
            super(itemView);
            tvTransListAmount = itemView.findViewById(R.id.trans_list_amount);
            tvTransListTime = itemView.findViewById(R.id.trans_list_time);
            tvTransListType = itemView.findViewById(R.id.trans_list_type);
            //tvAccName = itemView.findViewById(R.id.tvAccountName);
            //tvAccBalance = itemView.findViewById(R.id.tvAccountBalance);
        }
    }
}
