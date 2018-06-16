package com.nieldeokar.hurumessenger.ui.chat;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nieldeokar.hurumessenger.R;
import com.nieldeokar.hurumessenger.models.User;
import com.nieldeokar.hurumessenger.packets.LocalAddressCard;
import com.nieldeokar.hurumessenger.ui.main.UsersAdapter;

import java.util.List;
import java.util.Objects;

/**
 * Created by @nieldeokar on 13/06/18.
 */

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {

    public List<User> userList;

    public ChatAdapter(List<User> users) {
        this.userList = users;
    }

    public void addUser(User user){
        this.userList.add(user);
        notifyItemInserted(userList.size());
    }

    @Override
    public ChatViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_user, parent, false);

        return new ChatViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(ChatViewHolder holder, int position) {
        User user = userList.get(position);

        String name = user.getName() + " : " +  user.getDeviceId();

        holder.tvName.setText(name);

        if(user.getLocalAddressCard() !=null){

            LocalAddressCard addressCard = new LocalAddressCard(Objects.requireNonNull(user.getLocalAddressCard()));
            holder.tvIp.setText((addressCard.getLocalV4Address()).toString());
        }

    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public class ChatViewHolder extends RecyclerView.ViewHolder {
        public TextView tvName, tvIp;

        public ChatViewHolder(View view) {
            super(view);
            tvName = (TextView) view.findViewById(R.id.tvName);
            tvIp = (TextView) view.findViewById(R.id.tvIp);
        }
    }
}