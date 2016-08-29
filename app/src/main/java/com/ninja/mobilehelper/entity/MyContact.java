package com.ninja.mobilehelper.entity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ninja_chen on 14-3-13.
 * Contact Model
 */
public class MyContact implements Comparable {

    public MyContact(){
        phoneNumbers = new ArrayList<String>();
    }

    private String name;
    private String pinyin;
    private List<String> phoneNumbers;
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
}

