package com.ninja.mobilehelper;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.*;
import android.database.Cursor;
import android.net.Uri;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import com.google.gson.Gson;
import opensource.jpinyin.ChineseHelper;
import opensource.jpinyin.PinyinFormat;
import opensource.jpinyin.PinyinHelper;
import org.apache.http.util.EncodingUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;

public class MainActivity extends ListActivity {

    final static int FILE_SELECT_CODE = 6333;
    final static String TAG = "ninjaTest";

    ArrayList<MyContact> contactsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Uri contactsUri = ContactsContract.Contacts.CONTENT_URI;
        String[] proj1 = new String[]{ContactsContract.Contacts.DISPLAY_NAME,
                ContactsContract.Contacts.HAS_PHONE_NUMBER,
                ContactsContract.Contacts.LOOKUP_KEY};
        Cursor curContacts = getContentResolver().query(contactsUri, proj1, null, null, null);

        //declare a ArrayList object to store the data that will present to the user
        contactsList = new ArrayList<MyContact>();
        MyContact myContact ;
        if (curContacts.getCount() > 0) {
            while (curContacts.moveToNext()) {
                // get all the phone numbers if exist
                if (curContacts.getInt(1) > 0) {
                    //0:DISPLAY_NAME,2:LOOKUP_KEY
                    myContact = initContact(curContacts.getString(0), curContacts.getString(2));
                    contactsList.add(myContact);
                }
                myContact = null;
            }
        }

        //sort by the pinyin Name
        Collections.sort(contactsList);

        // binding the data to ListView
        setListAdapter(new ContactAdapter(this, contactsList));
        ListView lv = getListView();
        lv.setTextFilterEnabled(true);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        } else if (id == R.id.export_contacts) {
            export_contacts();
        } else if (id == R.id.import_contacts) {
            selectFile();
        }
        return super.onOptionsItemSelected(item);
    }

    public boolean selectFile() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        try {
            startActivityForResult(Intent.createChooser(intent, "请选择一个联系人备份文件"),
                    FILE_SELECT_CODE);
        } catch (android.content.ActivityNotFoundException ex) {
            // Potentially direct the user to the Market with a Dialog
            Toast.makeText(MainActivity.this, "plz install file explorer", Toast.LENGTH_SHORT)
                    .show();
        }
        return true;
    }

    public boolean import_contacts(ArrayList<MyContact> contactList) {
        //todo
        boolean isOK = true;
        for(MyContact contact : contactList)
            isOK &= addContact(contact);

        if(isOK){
            Toast.makeText(this, "import success", Toast.LENGTH_SHORT).show();
        return true;
        }
        return false;
    }


    public boolean export_contacts() {
        if (contactsList != null && contactsList.size() > 0) {
            Gson gson = new Gson();
            String json = gson.toJson(contactsList);
            Log.i(TAG, json);
            writeToFile(json);
        }
        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //import contacts
        if (resultCode == Activity.RESULT_OK
                && requestCode == FILE_SELECT_CODE) {
            // Get the Uri of the selected file
            Uri uri = data.getData();
            ContentResolver cr = getContentResolver();
            String contacts = null;
            try {
                InputStream is = cr.openInputStream(uri);
                contacts = readFile(is);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
//            String url = uri.toString();
            Gson gson = new Gson();
            final ArrayList<MyContact> contactList = gson.fromJson(contacts, ArrayList.class);
            new AlertDialog.Builder(this)
                    .setMessage("Are you sure " + contactList.size() +" contacts will be import?")
                    .setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            import_contacts(contactList);
                        }
                    })
                    .setNegativeButton("No", null)
                    .show();
            Log.i(TAG, "uri" + uri);

        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * init phone numbers and contact names.
     * Get all the phone numbers of a specific contact person
     *
     * @param lookUp_Key lookUp key for a specific contact
     * @return a string containing all the phone numbers
     */
    public MyContact initContact(String name, String lookUp_Key) {
        MyContact myContact = new MyContact();
        //init name
        if(name !=null && name.length() > 0){
            if(ChineseHelper.isChinese(name.charAt(0))){
                //if name is chinese,need to add pinyin to index,such as:小李-> xiaoli（小李）
                name = PinyinHelper.convertToPinyinString(name, "", PinyinFormat.WITHOUT_TONE) + "(" + name + ")";
            }
        }
        myContact.setName(name);

        //init phone number
        // Phone info are stored in the ContactsContract.Data table
        Uri phoneUri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        String[] proj2 = {ContactsContract.CommonDataKinds.Phone.NUMBER};
        // using lookUp key to search the phone numbers
        String selection = ContactsContract.Data.LOOKUP_KEY + "=?";
        String[] selectionArgs = {lookUp_Key};
        Cursor cur = getContentResolver().query(phoneUri, proj2, selection, selectionArgs, null);
        while (cur.moveToNext()) {
            myContact.getPhoneNumbers().add(cur.getString(0));
        }

        return myContact;
    }

    private void writeToFile(String data) {
        try {
            File myFile = new File("/sdcard/contacts.nin");
            myFile.createNewFile();
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(new FileOutputStream(myFile));
            outputStreamWriter.write(data);
            outputStreamWriter.close();
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }

    //读SD中的文件
    public String readFile(InputStream fin){
        String res="";
        try{
//            FileInputStream fin = new FileInputStream(filePath);

            int length = fin.available();

            byte [] buffer = new byte[length];
            fin.read(buffer);

            res = EncodingUtils.getString(buffer, "UTF-8");

            fin.close();
        }

        catch(Exception e){
            e.printStackTrace();
        }
        return res;
    }

    public boolean addContact(MyContact contact){
        ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
        int rawContactInsertIndex = ops.size();

        ops.add(ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI)
                .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
                .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null).build());
        ops.add(ContentProviderOperation
                .newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID,rawContactInsertIndex)
                .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, contact.getName()) // Name of the person
                .build());
        ops.add(ContentProviderOperation
                .newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(
                        ContactsContract.Data.RAW_CONTACT_ID,   rawContactInsertIndex)
                .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, contact.getFirstPhoneNumber()) // Number of the person
                .withValue(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE).build()); // Type of mobile number
        try
        {
            ContentProviderResult[] res = getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
            return true;
        }
        catch (RemoteException e)
        {
            // error
            return false;
        }
        catch (OperationApplicationException e)
        {
            // error
            return false;
        }
    }
}
