package com.example.keabankapp.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.keabankapp.R;
import com.example.keabankapp.models.AccountModel;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.database.annotations.NotNull;
import com.google.firebase.firestore.DocumentSnapshot;
/*
    Adapter: A subclass of RecyclerView.Adapter responsible for providing views that represent items in a data set.
    FirestoreRecyclerAdapter — binds a Query to a RecyclerView and responds to all real-time events
    included items being added, removed, moved, or changed.
    Best used with small result sets since all results are loaded at once.

    Binds and sets elements on the UI to the data it recives from firestore based on models.

 */

public class AccountAdapter extends FirestoreRecyclerAdapter<AccountModel, AccountAdapter.AccountHolder> {
    private onItemClickListener listener;

    public AccountAdapter(@NotNull FirestoreRecyclerOptions<AccountModel> options){
    super(options);
    }

    @Override
    protected void onBindViewHolder(@NotNull AccountHolder holder, int position, @NotNull AccountModel model){
        holder.tvAccName.setText(model.getaName());
        holder.tvAccBalance.setText(Double.toString(model.getaAmount()));
        holder.tvAccountType.setText(model.getaType());

    }

    @NotNull
    @Override
    public AccountHolder onCreateViewHolder(@NotNull ViewGroup viewGroup, int i){
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.account_list, viewGroup, false);
        return new AccountHolder(v);
    }

    public class AccountHolder extends RecyclerView.ViewHolder {
        TextView tvAccName;
        TextView tvAccBalance;
        TextView tvAccountType;

        public AccountHolder(@NotNull View itemView){
            super(itemView);

            tvAccName = itemView.findViewById(R.id.tvAccountName);
            tvAccBalance = itemView.findViewById(R.id.tvTransferAccountBalance);
            tvAccountType = itemView.findViewById(R.id.tvAccountType);

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
        void onItemClick(DocumentSnapshot documentSnapshot, int position);
    }
    public void setOnItemClickListener(onItemClickListener listener) {
        this.listener = listener;

    }
}
