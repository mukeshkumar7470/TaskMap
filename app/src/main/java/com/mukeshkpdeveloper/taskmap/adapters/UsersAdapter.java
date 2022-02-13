package com.mukeshkpdeveloper.taskmap.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.mukeshkpdeveloper.taskmap.R;
import com.mukeshkpdeveloper.taskmap.models.Users;
import com.mukeshkpdeveloper.taskmap.ui.UserDetsailActivity;

import java.util.ArrayList;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.MyViewHolder> {
    Context mContext;
    private final ArrayList<Users> data;

    public UsersAdapter(ArrayList<Users> data, Context mContext) {
        this.data = data;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public UsersAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_user_list_item, parent, false);
        return new MyViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(UsersAdapter.MyViewHolder holder, int position) {
        Users users = data.get(position);
        holder.tvUserName.setText(users.getName());
        holder.tvUserEmail.setText(users.getEmail());

        holder.cvDetail.setOnClickListener(view -> {
            Intent intent = new Intent(view.getContext(), UserDetsailActivity.class);
            intent.putExtra("NAME", users.getName());
            intent.putExtra("EMAIL", users.getEmail());
            view.getContext().startActivity(intent);
        });


    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvUserName;
        private final TextView tvUserEmail;
        private final CardView cvDetail;

        MyViewHolder(View itemView) {
            super(itemView);
            tvUserName = itemView.findViewById(R.id.tv_user_name);
            tvUserEmail = itemView.findViewById(R.id.tv_user_email);
            cvDetail = itemView.findViewById(R.id.cv_detail);


        }
    }
}


