package com.example.chatapp.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatapp.Models.Chat;
import com.example.chatapp.R;
import com.example.chatapp.Utils.ImageAPI;

import java.text.SimpleDateFormat;
import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {
    Context context;

    public List<Chat> list;
    Listener listener;
    String parttern = "HH:mm";


    public ChatAdapter(Context context, List<Chat> list, Listener listener) {
        this.context = context;
        this.list = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Chat chat = list.get(position);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(parttern);

        holder.ChatName.setText(chat.name);

        if (chat.imagePath == null) {
            ImageAPI.getCircle(String.format("https://avatars.dicebear.com/api/initials/%s.png?size=128", chat.name), holder.ChatImage);
        }

        if (chat.last_message == null) {
            holder.LastMessage.setText("not content");
        } else {
            holder.LastMessage.setText(chat.last_message.sender + ": " + chat.last_message.message);
            String sendAt = simpleDateFormat.format(chat.last_message.send_at);
            holder.SendAt.setText(sendAt);
        }

        holder.itemView.setOnClickListener(v -> listener.onClick(chat));

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ChatImage;
        TextView ChatName;
        TextView LastMessage;
        TextView SendAt;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ChatImage = itemView.findViewById(R.id.chatImage);
            ChatName = itemView.findViewById(R.id.ChatName);
            LastMessage = itemView.findViewById(R.id.lastMessage);
            SendAt = itemView.findViewById(R.id.send_at);
        }
    }

    public interface Listener {
        void onClick(Chat chat);
    }
}
