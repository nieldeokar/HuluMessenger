package com.nieldeokar.hurumessenger.ui.chat;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nieldeokar.hurumessenger.R;
import com.nieldeokar.hurumessenger.models.Message;
import com.nieldeokar.hurumessenger.models.User;
import com.nieldeokar.hurumessenger.packets.LocalAddressCard;
import com.nieldeokar.hurumessenger.utils.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Created by @nieldeokar on 16/06/18.
 */

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {

    public List<Message> messageList = new ArrayList<>();

    public ChatAdapter(List<Message> messages) {
        this.messageList = messages;
    }

    public void addMessage(Message message){
        this.messageList.add(message);
        notifyItemInserted(messageList.size());
    }

    @Override
    public ChatViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(viewType, parent, false);

        return new ChatViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(ChatViewHolder holder, int position) {
        Message message = messageList.get(position);


        holder.tvMessageBody.setText(message.getTextBody());

        holder.tvTime.setText(Utils.formatTime(message.getTimeOfCreation()));



    }

    @Override
    public int getItemViewType(int position) {

        if(messageList.get(position).isOutgoingMessage()) {
            return R.layout.row_message_sent;
        }else{
            return R.layout.row_message_received;
        }
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    public class ChatViewHolder extends RecyclerView.ViewHolder {
        public TextView tvMessageBody, tvTime;
        public ImageView imgStatus;

        public ChatViewHolder(View view) {
            super(view);
            tvMessageBody = (TextView) view.findViewById(R.id.tvTextBody);
            tvTime= (TextView) view.findViewById(R.id.tvTime);
            imgStatus = (ImageView) view.findViewById(R.id.imgStatus);
        }
    }
}