package com.example.chatapp.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatapp.Models.User;
import com.example.chatapp.R;
import com.example.chatapp.Utils.ImageAPI;

import java.util.List;

public class MemberAdapter extends RecyclerView.Adapter<MemberAdapter.ViewHolder> {
    Context context;
    List<User> list;
    Listener listener;

    public MemberAdapter(Context context, List<User> list, Listener listener) {
        this.context = context;
        this.list = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_member, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User user = list.get(position);

        holder.tvName.setText(user.Fullname());
        holder.tvEmail.setText(user.email);

        ImageAPI.getDefaultImage(user.Fullname(), holder.avatar);

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView avatar;
        TextView tvName, tvEmail;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            avatar = itemView.findViewById(R.id.img_member);
            tvName = itemView.findViewById(R.id.tv_name_member);
            tvEmail = itemView.findViewById(R.id.tv_email);

        }
    }

    public interface Listener {
        void onClick(User user);
    }
}
