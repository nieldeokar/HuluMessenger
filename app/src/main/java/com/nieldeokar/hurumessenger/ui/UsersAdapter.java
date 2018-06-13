package com.nieldeokar.hurumessenger.ui;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nieldeokar.hurumessenger.R;
import com.nieldeokar.hurumessenger.database.entity.UserEntity;
import com.nieldeokar.hurumessenger.packets.MePacket;

import java.util.List;

/**
 * Created by @nieldeokar on 13/06/18.
 */

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.MyViewHolder> {

    public List<UserEntity> userEntityList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tvName, tvIp;

        public MyViewHolder(View view) {
            super(view);
            tvName = (TextView) view.findViewById(R.id.tvName);
            tvIp = (TextView) view.findViewById(R.id.tvIp);
        }
    }


    public UsersAdapter(List<UserEntity> usersList) {
        this.userEntityList = usersList;
    }

    public void addUser(UserEntity userEntity){
        this.userEntityList.add(userEntity);
        notifyItemInserted(userEntityList.size());
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_user, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        UserEntity userEntity = userEntityList.get(position);

        String name = userEntity.getName() + " : " +  userEntity.getDevice_id();

        holder.tvName.setText(name);

        if(userEntity.getMePacket() !=null){
            MePacket mePacket = new MePacket();
            mePacket.setPacket(userEntity.getMePacket());

            holder.tvIp.setText(mePacket.getLocalAddressCard().getLocalV4Address().toString());
        }

    }

    @Override
    public int getItemCount() {
        return userEntityList.size();
    }

}