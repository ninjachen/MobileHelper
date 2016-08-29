package com.ninja.mobilehelper.pinyin;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.ninja.mobilehelper.R;
import com.ninja.mobilehelper.entity.MyContact;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@SuppressLint("UseSparseArrays")
public class ContactsAdapter extends PinnedHeaderAdapter<MyContact> implements Filterable {

    private static String TAG = ContactsAdapter.class.getSimpleName();
    protected LayoutInflater mInflater;
    //    private FriendFilter mFilter;
    protected ArrayList<View> mViewList;

    public ContactsAdapter(Context context, List<MyContact> friends) {
        super(context);
        setAdapterData(friends);

        mViewList = new ArrayList<>();

        if (context != null)
            mInflater = LayoutInflater.from(context);

    }

    public void setAdapterData(List<MyContact> friends) {


        HashMap<Integer, Integer> hashMap = new HashMap<Integer, Integer>();

        List<List<MyContact>> result = new ArrayList<>();
        int key = 0;

        for (MyContact friend : friends) {
            key = friend.getSearchKey();

            if (hashMap.containsKey(key)) {
                int position = (Integer) hashMap.get(key);
                if (position <= result.size() - 1) {
                    result.get(position).add(friend);
                }
            } else {
                result.add(new ArrayList<MyContact>());
                int length = result.size() - 1;
                result.get(length).add(friend);
                hashMap.put(key, length);
            }
        }
        updateCollection(result);

    }

    @Override
    protected View newView(Context context, int partition, List<MyContact> data, int position, ViewGroup parent) {
        /**
         * @author qinhuang
         * 修改拼音首字母联动相关的item
         */
       /* View view = mInflater.inflate(R.layout.de_item_addresslist, parent, false);*/
        View view = mInflater.inflate(R.layout.common_friendlist, parent, false);
        ViewHolder holder = new ViewHolder();
        newSetTag(view, holder, position, data);
        view.setTag(holder);
        return view;
    }

    @Override
    protected void bindView(View v, int partition, List<MyContact> data, int position) {

        ViewHolder holder = (ViewHolder) v.getTag();

        TextView name = holder.name;
//        ImageView photo = holder.photo;
//        TextView choice = holder.unreadnum;
        MyContact friend = data.get(position);
        name.setText(friend.getName());
        //@author qinhuang 将新的朋友等的一致设置取消 16-3-19
//        String friendId = friend.getUserId();
//        if (friendId.equalsIgnoreCase(ContactsFragment.NEW_FRIEND_ID)
//                || friendId.equalsIgnoreCase(ContactsFragment.FOLLING_MERCHANT)
//                || friendId.equalsIgnoreCase(ContactsFragment.GROUP_CHAT_ID) ||
//                friendId.equalsIgnoreCase(ContactsFragment.MY_FOLLOWER_ID) ||
//                friendId.equalsIgnoreCase(ContactsFragment.MY_FOLLOWING_ID)) {
//
//            holder.common_ic_gendor.setVisibility(View.GONE);
//            holder.common_divider_center.setVisibility(View.GONE);
//            holder.common_ic_idinfo.setVisibility(View.GONE);
//            holder.common_text_idinfo.setVisibility(View.GONE);
//        }

//        Picasso.with(mContext)
//                .load(friend.getPortrait())
//                .placeholder(R.drawable.de_default_portrait)
//                .fit()
//                .centerCrop()
//                .transform(new PicassoCirclTransform())
//                .into(photo);

//        photo.setTag(position);

        holder.friend = friend;

        /**
         * @author qinhuang 在这里加入修改数据的操作
         * 性别   ，专家
         */
//        ImageView common_ic_gendor = holder.common_ic_gendor;
//        Bitmap bitmap = IDUtil.validateGendor(friend.gender, mContext);
//        common_ic_gendor.setImageBitmap(bitmap);
//        String simpleValidateInfo = IDUtil.getSimpleValidateInfo(friend.certificate_status, friend.user_tag_status);
//        holder.common_text_idinfo.setText(simpleValidateInfo);


    }

    @Override
    protected View newHeaderView(Context context, int partition, List<MyContact> data, ViewGroup parent) {
        View view = mInflater.inflate(R.layout.de_item_friend_index, parent, false);
        view.setTag(view.findViewById(R.id.index));
        return view;
    }

    @Override
    protected void bindHeaderView(View view, int partition, List<MyContact> data) {
        Object objTag = view.getTag();

        if (objTag != null) {
            ((TextView) objTag).setText(String.valueOf(data.get(0).getSearchKey()));
        }
    }


    @Override
    protected View newView(Context context, int position, ViewGroup group) {
        return null;
    }

    @Override
    protected void bindView(View v, int position, MyContact data) {

    }

    class PinnedHeaderCache {
        TextView titleView;
        ColorStateList textColor;
        Drawable background;
    }

    @Override
    protected SectionIndexer updateIndexer(Partition<MyContact>[] data) {
        return new FriendSectionIndexer(data);
    }

    @Override
    public void configurePinnedHeader(View header, int position, int alpha) {


        PinnedHeaderCache cache = (PinnedHeaderCache) header.getTag();

        if (cache == null) {
            cache = new PinnedHeaderCache();
            cache.titleView = (TextView) header.findViewById(R.id.index);
            cache.textColor = cache.titleView.getTextColors();
            cache.background = header.getBackground();
            header.setTag(cache);
        }

        int section = getSectionForPosition(position);
        if (section != -1) {
            if (section == 0) {
//fixed by Ninja, * is now always appears
//                cache.titleView.setText("★");
                String title = (String) getSectionIndexer().getSections()[section];
                cache.titleView.setText(title);
            } else if (section > 0) {
                String title = (String) getSectionIndexer().getSections()[section];
                cache.titleView.setText(title);
            }
        }

    }

    public static class ViewHolder {
        public TextView name;
//        public ImageView photo;
//        public String userId;
        public MyContact friend;
//        public TextView unreadnum;
        /**
         * @author qinhuang
         * 新界面添加新图标
         */
//        public ImageView common_ic_gendor;
//        public ImageView common_ic_idinfo;
//        public TextView common_text_idinfo;
//        public View common_divider_center;



    }

    protected void newSetTag(View view, ViewHolder holder, int position, List<MyContact> data) {

//        ImageView photo = (ImageView) view.findViewById(R.id.de_ui_friend_icon);

        if (mViewList != null && !mViewList.contains(view)) {
            mViewList.add(view);
        }

        holder.name = (TextView) view.findViewById(R.id.de_ui_friend_name);
//        holder.unreadnum = (TextView) view.findViewById(R.id.de_unread_num);
//        holder.photo = photo;
        /**
         * @author qinhuang
         * 添加几句新icon 的findviewbyid
         */
//        holder.common_ic_gendor = (ImageView) view.findViewById(R.id.common_ic_gendor);
//        holder.common_ic_idinfo = (ImageView) view.findViewById(R.id.common_ic_idinfo);
//        holder.common_text_idinfo = (TextView) view.findViewById(R.id.common_text_idinfo);
//        holder.common_divider_center = (View) view.findViewById(R.id.common_divider_center);


    }

    public void destroy() {

        if (mViewList != null) {
            mViewList.clear();
            mViewList = null;
        }
    }


    @Override
    public Filter getFilter() {
        return null;
    }

    public void onItemClick(String friendId) {

    }

}
