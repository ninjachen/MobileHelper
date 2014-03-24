package com.ninja.mobilehelper;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.net.URISyntaxException;

public class MainActivity extends ActionBarActivity {

    final static int FILE_SELECT_CODE = 6333;
    final static String TAG = "ninjaTest";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
        }else if(id == R.id.export_contacts){
            export_contacts();
        }else if(id == R.id.import_contacts){

        }
        return super.onOptionsItemSelected(item);
    }

    public boolean import_contacts(){

        return true;
    }

    public boolean export_contacts(){
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        try {
            startActivityForResult(Intent.createChooser(intent, "请选择一个要上传的文件"),
                    FILE_SELECT_CODE);
        } catch (android.content.ActivityNotFoundException ex) {
            // Potentially direct the user to the Market with a Dialog
            Toast.makeText(MainActivity.this, "请安装文件管理器", Toast.LENGTH_SHORT)
                    .show();
        }
        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        if (resultCode == Activity.RESULT_OK
                && requestCode == FILE_SELECT_CODE) {
            // Get the Uri of the selected file
            Uri uri = data.getData();
            String url = uri.toString();
            String url;
            //                url = FFileUtils.getPath(getActivity(), uri);
            Log.i(TAG, "uri" + uri);
//                String fileName = url.substring(url.lastIndexOf("/") + 1);
//                intent = new Intent(getActivity(), UploadServices.class);
//                intent.putExtra("fileName", fileName);
//                intent.putExtra("url", url);
//                intent.putExtra("type ", "");
//                intent.putExtra("fuid", "");
//                intent.putExtra("type", "");

//                MainActivity.this.startService(intent);

        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
