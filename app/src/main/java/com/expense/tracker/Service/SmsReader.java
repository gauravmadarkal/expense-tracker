package com.expense.tracker.Service;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import com.expense.tracker.model.Sms;
import com.expense.tracker.views.TrackerActivity;

import java.util.ArrayList;
import java.util.List;

public class SmsReader {
    public static List<Sms> getAllSms(TrackerActivity mActivity) {
        List<Sms> lstSms = new ArrayList<>();
        Sms objSms;
        Uri message = Uri.parse("content://sms/");
        ContentResolver cr = mActivity.getContentResolver();

        Cursor c = cr.query(message, null, null, null, null);
//        mActivity.startManagingCursor(c);
        if(c!=null) {
            int totalSMS = c.getCount();

            if (c.moveToFirst()) {
                for (int i = 0; i < totalSMS; i++) {

                    objSms = new Sms();
                    objSms.setId(c.getString(c.getColumnIndexOrThrow("_id")));
                    objSms.setAddress(c.getString(c
                            .getColumnIndexOrThrow("address")));
                    objSms.setMsg(c.getString(c.getColumnIndexOrThrow("body")));
                    objSms.setTime(c.getString(c.getColumnIndexOrThrow("date")));

                    lstSms.add(objSms);
                    c.moveToNext();
                }
            }
            c.close();
        }
        return lstSms;
    }
}
