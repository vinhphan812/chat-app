package com.example.chatapp.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatapp.Models.Message;
import com.example.chatapp.R;
import com.example.chatapp.Utils.ImageAPI;
import com.example.chatapp.Utils.Services;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {
    Context context;
    public List<Message> list;
    LongListener listener;

    String patternTime = "HH:mm";
    String patternDate = "dd/MM/yyyy";
    String patternDateTime = "HH:mm dd/MM/yyyy";


    public MessageAdapter(Context context, List<Message> list, LongListener listener) {
        this.context = context;
        this.list = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Log.d("POSITION", getItemCount() + " " + position);
        Message curMessage = null;
        Message message = list.get(position);
        Message nextMessage = null;
        if (position > 0) curMessage = list.get(position - 1);
        if (position + 1 < getItemCount()) nextMessage = list.get(position + 1);

        if (message.sender_id.equals(Services.getUserID())) {
            holder.receiver.setVisibility(View.GONE);

            if(message.type.equals("text")) {
                holder.textMessageSend.setText(message.message);
                holder.sendImage.setVisibility(View.GONE);
            } else {
                holder.textMessageSend.setVisibility(View.GONE);

                ImageAPI.getCorner(Services.getImageUri(message.src).toString(), holder.sendImage);

            }


            if (nextMessage != null && message.sender_id.equals(nextMessage.sender_id) && nextMessage.send_at - message.send_at < 10000) {
                holder.DateSend.setVisibility(View.GONE);
            }

            holder.DateSend.setText(makeSendTime(message.send_at));
        } else {
            holder.send.setVisibility(View.GONE);

            if(message.type.equals("text")) {
                holder.textMessageReceiver.setText(message.message);
                holder.imageReceiver.setVisibility(View.GONE);
            } else {
                holder.textMessageReceiver.setVisibility(View.GONE);
                ImageAPI.getCorner(Services.getImageUri(message.src).toString(), holder.imageReceiver);
            }

            if (curMessage != null && message.sender_id.equals(curMessage.sender_id)) {
                holder.senderName.setVisibility(View.GONE);
            }

            if (nextMessage != null && message.sender_id.equals(nextMessage.sender_id)) {
                holder.senderImage.setVisibility(View.INVISIBLE);
                holder.DateReceiver.setVisibility(View.GONE);
            }
            holder.senderName.setText(message.sender);
            holder.DateReceiver.setText(makeSendTime(message.send_at));
            ImageAPI.getDefaultImage(message.sender, holder.senderImage);
        }

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (message.type == "text")
                    listener.onClick(message);

                return true;
            }
        });
    }

    private String makeSendTime(long time) {
        Date date = new Date(time), current = new Date();

        SimpleDateFormat formatDate = new SimpleDateFormat(patternDate);
        SimpleDateFormat formatTime = new SimpleDateFormat(patternTime);

        SimpleDateFormat formatDateTime = new SimpleDateFormat(patternDateTime);

        return formatDate.format(date).equals(formatDate.format(current)) ? formatTime.format(date) : formatDateTime.format(date);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView senderName, textMessageReceiver, DateReceiver, textMessageSend, DateSend;
        LinearLayout receiver;
        ImageView senderImage, imageReceiver, sendImage;
        ConstraintLayout send;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            senderImage = itemView.findViewById(R.id.senderImage);
            senderName = itemView.findViewById(R.id.senderName);
            textMessageReceiver = itemView.findViewById(R.id.textMessageReceiver);
            DateReceiver = itemView.findViewById(R.id.DateReceiver);
            textMessageSend = itemView.findViewById(R.id.textMessageSend);
            DateSend = itemView.findViewById(R.id.DateSend);
            receiver = itemView.findViewById(R.id.receiver);
            send = itemView.findViewById(R.id.send);
            sendImage = itemView.findViewById(R.id.sendImage);
            imageReceiver = itemView.findViewById(R.id.imageReceiver);
        }
    }

    public interface LongListener {
        void onClick(Message message);
    }
}
