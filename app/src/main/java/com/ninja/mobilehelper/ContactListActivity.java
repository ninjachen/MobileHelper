package com.ninja.mobilehelper;

import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.view.LayoutInflater;

import com.ninja.mobilehelper.entity.MyContact;
import com.ninja.mobilehelper.pinyin.ContactsSingleChoiceAdapter;
import com.ninja.mobilehelper.pinyin.PinnedHeaderListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

import static com.ninja.mobilehelper.MainActivity.initContact;

/**
 * Created by ninja on 8/29/16.
 */

public class ContactListActivity extends Activity {
    @Bind(R.id.de_ui_friend_list)
    PinnedHeaderListView mListView;
    private ContactsSingleChoiceAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contact_list_actvity);
        ButterKnife.bind(this);
        mListView.setPinnedHeaderView(LayoutInflater.from(this).inflate(R.layout.de_item_friend_index,
                mListView, false));
        mListView.setFastScrollEnabled(false);
        mListView.setHeaderDividersEnabled(false);
        mListView.setFooterDividersEnabled(false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        fillData();
    }

    private void fillData() {
//        Log.e("qinhuang", com.clink.android.bootstrap.core.Constants.loginUser.getUid() + "");
        ArrayList<MyContact> contacts = getContacts();

        ArrayList<MyContact> mFriendsList = contacts;
        mFriendsList = sortFriends(mFriendsList, true);
        mAdapter = new ContactsSingleChoiceAdapter(this, mFriendsList);
        mListView.setAdapter(mAdapter);
        mAdapter.setAdapterData(mFriendsList);

    }

    private ArrayList<MyContact> getContacts() {
        Uri contactsUri = ContactsContract.Contacts.CONTENT_URI;
        String[] proj1 = new String[]{ContactsContract.Contacts.DISPLAY_NAME,
                ContactsContract.Contacts.HAS_PHONE_NUMBER,
                ContactsContract.Contacts.LOOKUP_KEY};
        Cursor curContacts = getContentResolver().query(contactsUri, proj1, null, null, null);

        //declare a ArrayList object to store the data that will present to the user
        ArrayList contactsList = new ArrayList<>();
        MyContact myContact;
        if (curContacts.getCount() > 0) {
            //0:DISPLAY_NAME,2:LOOKUP_KEY
            int DISPLAY_NAME = 0;
            int LOOKUP_KEY = 2;
            while (curContacts.moveToNext()) {
                // get all the phone numbers if exist
                if (curContacts.getInt(1) > 0) {
                    myContact = MainActivity.initContact(curContacts.getString(DISPLAY_NAME), curContacts.getString(LOOKUP_KEY), this);
                    contactsList.add(myContact);
                }
            }
        }
        return  contactsList;
    }

    private ArrayList<MyContact> sortFriends(List<MyContact> friends, boolean addExtra) {

        String[] searchLetters = BootstrapApplication.getInstance().getResources().getStringArray(R.array.de_search_letters);

        HashMap<String, ArrayList<MyContact>> userMap = new HashMap<>();

        ArrayList<MyContact> friendsArrayList = new ArrayList<>();

        for (MyContact friend : friends) {
            // search key is generate when setNickName()
            if (friend.getSearchKey() == Character.MIN_VALUE && !TextUtils.isEmpty(friend.getName())) {
                friend.setName(friend.getName());
            }
            String letter = new String(new char[]{friend.getSearchKey()});

            if (userMap.containsKey(letter)) {
                ArrayList<MyContact> friendList = userMap.get(letter);
                friendList.add(friend);

            } else {
                ArrayList<MyContact> friendList = new ArrayList<>();
                friendList.add(friend);
                userMap.put(letter, friendList);
            }

        }
        ArrayList<MyContact> friendList = new ArrayList<>();
        // solid menu
//        if (addExtra) {
//            friendList.add(new Friend(NEW_FRIEND_ID, "新的朋友", ImageUtils.drawableToUri(R.drawable.de_address_new_friend, getContext()).toString()));
//            friendList.add(new Friend(FOLLING_MERCHANT, "关注的厂商", ImageUtils.drawableToUri(R.drawable.ic_people_focused, getContext()).toString()));
//            friendList.add(new Friend(GROUP_CHAT_ID, "群组", ImageUtils.drawableToUri(R.drawable.de_address_group, getContext()).toString()));
//            friendList.add(new Friend(MY_FOLLOWING_ID, "关注的人", ImageUtils.drawableToUri(R.drawable.ic_eye, getContext()).toString()));
//            friendList.add(new Friend(MY_FOLLOWER_ID, "粉丝", ImageUtils.drawableToUri(R.drawable.ic_heart, getContext()).toString()));
//            //暂时不需要后面两个
////            friendList.add(new Friend(PUBLIC_ACCOUNT_ID, "公众号", ImageUtils.drawableToUri(R.drawable.ic_public_account, getContext()).toString()));
////            friendList.add(new Friend(TAGS_ID, "标签", ImageUtils.drawableToUri(R.drawable.ic_tags, getContext()).toString()));
//        }
        userMap.put("★", friendList);
        for (int i = 0; i < searchLetters.length; i++) {
            String letter = searchLetters[i];
            ArrayList<MyContact> fArrayList = userMap.get(letter);
            if (fArrayList != null) {
                friendsArrayList.addAll(fArrayList);
            }
        }
        return friendsArrayList;
    }
}
