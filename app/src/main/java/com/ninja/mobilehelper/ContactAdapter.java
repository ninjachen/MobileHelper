package com.ninja.mobilehelper;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ninja.mobilehelper.entity.MyContact;

import java.util.List;

/**
 * Created by ninja_chen on 14-6-8.
 */
public class ContactAdapter extends BaseAdapter {

    List<MyContact> contacts;
    Context context;

    public ContactAdapter(Context ctx, List<MyContact> contacts) {
        this.context = ctx;
        this.contacts = contacts;
    }

    @Override
    public int getCount() {
        return contacts.size();
    }

    @Override
    public Object getItem(int position) {
        return contacts.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MyContact contact = contacts.get(position);

        if (convertView == null) {
            LayoutInflater inflater;
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_item, null);

        }
        TextView tvName = (TextView) convertView.findViewById(R.id.tvName);
        TextView tvPhone = (TextView) convertView.findViewById(R.id.tvPhone);
        tvName.setText(String.valueOf(contact.getName()));
        tvPhone.setText(String.valueOf(contact.getPhoneNumbers()));


//        convertView.setTag();
        return convertView;
    }
}
