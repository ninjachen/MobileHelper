package com.ninja.mobilehelper.entity;

import android.text.TextUtils;

import com.ninja.mobilehelper.PinyinHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ninja_chen on 14-3-13.
 * Contact Model
 */
public class MyContact implements Comparable {

    private String nicknamePinyin;

    public MyContact(){
        phoneNumbers = new ArrayList<String>();
    }

    private String name;
    private String pinyin;
    private List<String> phoneNumbers;

    private char searchKey = Character.MIN_VALUE;


    public String getName() {
        return name;
    }

    public List<String> getPhoneNumbers() {
        return phoneNumbers;
    }

    public String getFirstPhoneNumber() {
        if(phoneNumbers != null && phoneNumbers.size()>0)
            return phoneNumbers.get(0);
        return "-999999999";
    }

    public void setName(String name) {
        this.name = name;
        createSeachKey(name);
    }

    public void setPhoneNumbers(List<String> phoneNumbers) {
        this.phoneNumbers = phoneNumbers;
    }

    public String getPinyin() {
        return pinyin;
    }

    public void setPinyin(String pinyin) {
        this.pinyin = pinyin;
    }

    @Override
    public int compareTo(Object another) {
        if(!(another instanceof  MyContact) )
            return -1;

        MyContact other = (MyContact)another;
        return this.getPinyin().toLowerCase().compareTo(other.getPinyin().toLowerCase());
    }

    public char getSearchKey() {
        return searchKey;
    }

    public void setSearchKey(char searchKey) {
        this.searchKey = searchKey;
    }
    private final void createSeachKey(String nickname) {

        if (TextUtils.isEmpty(nickname)) {
            return;
        }

        nicknamePinyin = PinyinHelper.getInstance().getPinyins(nickname, "");

        if (nicknamePinyin != null && nicknamePinyin.length() > 0) {
            char key = nicknamePinyin.charAt(0);
            if (key >= 'A' && key <= 'Z') {

            } else if (key >= 'a' && key <= 'z') {
                key -= 32;
            } else if (key == '★' ) {
                key = '★';
            }else {
                // unexpected first char
                key = '#';
            }
            searchKey = key;
        } else {
            searchKey = '#';
        }
    }
}

