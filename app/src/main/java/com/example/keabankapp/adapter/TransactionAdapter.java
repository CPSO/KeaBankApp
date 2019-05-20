package com.example.keabankapp.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.keabankapp.R;
import com.example.keabankapp.models.AccountTransactionModel;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.annotations.NotNull;



public class TransactionAdapter extends FirebaseRecyclerAdapter<AccountTransactionModel, TransactionAdapter.TransactionHolder> {
    private onItemClickListener listener;

    /**
     * Initialize a {@link RecyclerView.Adapter} that listens to a Firebase query. See
     * {@link FirebaseRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public TransactionAdapter(@NonNull FirestoreRecyclerOptions<AccountTransactionModel> options) {
        super(options);
    }



    @Override
    protected void onBindViewHolder(@NotNull TransactionHolder holder, int position, @NotNull AccountTransactionModel model){
        holder.tvTransListAmount.setText(Double.toString(model.gettAmount()));
        holder.tvTransListType.setText(model.gettType());
        holder.tvTransListTime.setText(model.gettTimestamp().toString());


    }

    @NotNull
    @Override
    public TransactionHolder onCreateViewHolder(@NotNull ViewGroup viewGroup, int i){
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.account_list, viewGroup, false);
        return new TransactionHolder(v);
    }

    public class TransactionHolder extends RecyclerView.ViewHolder {
        TextView  tvTransListAmount;
        TextView tvTransListTime;
        TextView tvTransListType;

        public TransactionHolder(@NotNull View itemView){
            super(itemView);

            tvTransListAmount = itemView.findViewById(R.id.trans_list_amount);
            tvTransListTime = itemView.findViewById(R.id.trans_list_time);
            tvTransListType = itemView.findViewById(R.id.trans_list_type);
            //tvAccName = itemView.findViewById(R.id.tvAccountName);
            //tvAccBalance = itemView.findViewById(R.id.tvAccountBalance);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    //Makes sure you cant click element out of bounds and null object
                    if (position != RecyclerView.NO_POSITION && listener != null){
                        listener.onItemClick(getSnapshots().getSnapshot(position), position);
                    }

                }
            });
        }
    }

    //onClick listener that gets the document repesentet in the view and x position.
    public interface onItemClickListener {
        void onItemClick(DataSnapshot documentSnapshot, int position);
    }
    public void setOnItemClickListener(TransactionAdapter.onItemClickListener listener) {
        this.listener = listener;

    }


}
