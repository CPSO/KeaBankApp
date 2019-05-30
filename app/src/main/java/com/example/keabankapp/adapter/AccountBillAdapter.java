package com.example.keabankapp.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.keabankapp.R;
import com.example.keabankapp.models.PaymentModel;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class AccountBillAdapter extends FirestoreRecyclerAdapter<PaymentModel, AccountBillAdapter.PaymentHolder > {

    public AccountBillAdapter(FirestoreRecyclerOptions<PaymentModel> options) {
        super(options);

    }
    @Override
    protected void onBindViewHolder(@NonNull PaymentHolder holder, int position, @NonNull PaymentModel model) {
        holder.tvPaymentListAmount.setText(Double.toString(model.getpAmount()));
        holder.tvPaymentListName.setText(model.getpTitle());
        holder.tvPaymentListNextPay.setText(model.getpPayTime().toString());
    }

    @NonNull
    @Override
    public PaymentHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.autopayment_list,viewGroup,false);
        return new PaymentHolder(v);

    }

    class PaymentHolder extends RecyclerView.ViewHolder{
        TextView tvPaymentListAmount;
        TextView tvPaymentListNextPay;
        TextView tvPaymentListName;

        public PaymentHolder(@NonNull View itemView) {
            super(itemView);
            tvPaymentListAmount = itemView.findViewById(R.id.payment_list_amount);
            tvPaymentListNextPay = itemView.findViewById(R.id.payment_list_nextpay);
            tvPaymentListName = itemView.findViewById(R.id.payment_list_name);
            //tvAccName = itemView.findViewById(R.id.tvAccountName);
            //tvAccBalance = itemView.findViewById(R.id.tvAccountBalance);
        }
    }
}
