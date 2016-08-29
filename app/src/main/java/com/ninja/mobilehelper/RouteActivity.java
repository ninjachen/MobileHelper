package com.ninja.mobilehelper;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Telephony;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import timber.log.Timber;

import static com.ninja.mobilehelper.R.id.clear_all_SMS_text;
import static com.ninja.mobilehelper.R.id.contact_list_text;

/**
 * Created by ninja on 8/29/16.
 */

public class RouteActivity extends Activity {

    @Bind(contact_list_text)
    TextView contactListText;

    @Bind(clear_all_SMS_text)
    TextView cleatAllSMSText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.route_activity);
        ButterKnife.bind(this);
        cleatAllSMSText.setVisibility(View.VISIBLE);
    }

    @OnClick(contact_list_text)
    public void onClickContactlist(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    @OnClick(clear_all_SMS_text)
    public void onClickClearAllSMS(View view) {
        clearAllSMS();
//        Toaster.showShort(this, "finish delete sms");
    }

    /**
     * Unless your app is marked as default SMS app in device,
     * you wont be able to play with SMS Provider, please read SMS guidelines for the same for KITKAT
     * http://android-developers.blogspot.in/2013/10/getting-your-sms-apps-ready-for-kitkat.html
     */
    public void clearAllSMS() {
        Cursor c = getApplicationContext().getContentResolver().query(Uri.parse("content://sms/"), null, null, null, null);
        Timber.e("total sms " + c.getCount());
        int delete = 0;
        try {
            markAsDefaultSMSappIfPossible();
            while (c.moveToNext()) {
                int id = c.getInt(0);
                delete += getApplicationContext().getContentResolver().delete(Uri.parse("content://sms/" + id), null, null);
//                Log.e(this.getClass().getSimpleName(), delete + "deleted");
            }

        } catch (Exception e) {
            Log.e(this.toString(), "Error deleting sms", e);
        } finally {
            c.close();
            Log.e(this.getClass().getSimpleName(), delete + "total delete");
        }
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void markAsDefaultSMSappIfPossible() {
        String defaultSmsPackage = Telephony.Sms.getDefaultSmsPackage(this);
        Timber.e("default app is " + defaultSmsPackage);
        String myPackageName = getPackageName();
        if (!defaultSmsPackage.equals(myPackageName)) {
//            Toaster.showShort(this, "set as default SMS application");
            Intent intent =
                    new Intent(Telephony.Sms.Intents.ACTION_CHANGE_DEFAULT);
            intent.putExtra(Telephony.Sms.Intents.EXTRA_PACKAGE_NAME,
                    myPackageName);
            startActivity(intent);
        }
    }


}
