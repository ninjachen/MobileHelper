package com.ninja.mobilehelper;

import java.util.List;

/**
 * Created by ninja_chen on 14-3-13.
 * Contact Model
 */
public class MyContact {
    private String name;
    private List<String> phoneNumbers;

    public String getName() {
        return name;
    }

    public List<String> getPhoneNumbers() {
        return phoneNumbers;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPhoneNumbers(List<String> phoneNumbers) {
        this.phoneNumbers = phoneNumbers;
    }
}

