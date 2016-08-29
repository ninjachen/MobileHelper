package com.ninja.mobilehelper.pinyin;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.ninja.mobilehelper.entity.MyContact;

import java.util.ArrayList;
import java.util.List;

public class ContactsSingleChoiceAdapter extends ContactsAdapter {

    private static final String TAG = ContactsSingleChoiceAdapter.class.getSimpleName();
    private ArrayList<MyContact> mFriends;


    public ContactsSingleChoiceAdapter(Context context, List<MyContact> friends) {
        super(context, friends);
        this.mFriends = (ArrayList<MyContact>) friends;
    }

    @Override
    protected void bindView(View v, int partition, List<MyContact> data, int position) {
        super.bindView(v, partition, data, position);

        ViewHolder holder = (ViewHolder) v.getTag();
        TextView name = holder.name;
//        ImageView photo = holder.photo;

        MyContact friend = data.get(position);
        name.setText(friend.getName());
        //@author qinhuang 将这边的头像设置去掉
        /**
        if(TextUtils.isEmpty(friend.getPortrait())){
            Picasso.with(mContext)
                    .load(R.drawable.de_default_portrait)
                    .fit()
                    .centerCrop()
                    .into(photo);
        }else {
            Picasso.with(mContext)
                    .load(friend.getPortrait())
                    .fit()
                    .centerCrop()
                    .into(photo);
        }
         */
//        String userId = friend.getUserId();
//        holder.userId = userId;
    }

    @Override
    protected void newSetTag(View view, ViewHolder holder, int position, List<MyContact> data) {
        super.newSetTag(view, holder, position, data);
    }

    @Override
    public void onItemClick(String friendId) {
    }
}
