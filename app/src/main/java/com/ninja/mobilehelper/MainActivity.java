package com.ninja.mobilehelper;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import com.google.gson.Gson;
import org.apache.http.util.EncodingUtils;

import java.io.*;
import java.util.ArrayList;

public class MainActivity extends ListActivity {

    final static int FILE_SELECT_CODE = 6333;
    final static String TAG = "ninjaTest";

    ArrayList<String> contactsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Uri contactsUri = ContactsContract.Contacts.CONTENT_URI;
        String[] proj1 = new String[]{ContactsContract.Contacts.DISPLAY_NAME,
                ContactsContract.Contacts.HAS_PHONE_NUMBER,
                ContactsContract.Contacts.LOOKUP_KEY};
        Cursor curContacts = getContentResolver().query(contactsUri, proj1, null, null, null);

        //declare a ArrayList object to store the data that will present to the user
        contactsList = new ArrayList<String>();
        String allPhoneNo = "";
        if (curContacts.getCount() > 0) {
            while (curContacts.moveToNext()) {
                // get all the phone numbers if exist
                if (curContacts.getInt(1) > 0) {
                    allPhoneNo = getAllPhoneNumbers(curContacts.getString(2));
                }
                contactsList.add(curContacts.getString(0) + " , " + allPhoneNo);
                allPhoneNo = "";
            }
        }

        // binding the data to ListView
        setListAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, contactsList));
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

    public boolean import_contacts() {
        //todo
        return true;
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
            String url = uri.toString();

            String contacts = null;
            try {
                contacts = readFile(url);
            } catch (IOException e) {
                e.printStackTrace();
            }
            Gson gson = new Gson();
            ArrayList<String> contactList = gson.fromJson(contacts, ArrayList.class);
            new AlertDialog.Builder(this)
                    .setMessage("Are you sure " + contactList.size() +" contacts will be import?")
                    .setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            import_contacts();
                        }
                    })
                    .setNegativeButton("No", null)
                    .show();
            Log.i(TAG, "uri" + uri);

        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * Get all the phone numbers of a specific contact person
     *
     * @param lookUp_Key lookUp key for a specific contact
     * @return a string containing all the phone numbers
     */
    public String getAllPhoneNumbers(String lookUp_Key) {
        String allPhoneNo = "";

        // Phone info are stored in the ContactsContract.Data table
        Uri phoneUri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        String[] proj2 = {ContactsContract.CommonDataKinds.Phone.NUMBER};
        // using lookUp key to search the phone numbers
        String selection = ContactsContract.Data.LOOKUP_KEY + "=?";
        String[] selectionArgs = {lookUp_Key};
        Cursor cur = getContentResolver().query(phoneUri, proj2, selection, selectionArgs, null);
        while (cur.moveToNext()) {
            allPhoneNo += cur.getString(0) + " ";
        }

        return allPhoneNo;
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
    public String readFile(String filePath) throws IOException{
        String res="";
        try{
            FileInputStream fin = new FileInputStream(filePath);

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
}
