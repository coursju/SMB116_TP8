package com.smb116.tp8;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.IBinder;
import android.os.Messenger;
import android.util.Log;

public class BroadcastService extends Service {
    public BroadcastService() {
    }

    private final IBinder binder = new LocalBinder();

    private static final String TAG = "BroadcastService";
    private Messenger messenger;
    private String filter;
    private SMSReceiver smsReceiver;
    private Intent intent;

    public class LocalBinder extends Binder {
        BroadcastService getService() {
            return BroadcastService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        this.intent = intent;
        startSMSReceiver();
        return binder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        stopSMSReceiver();
        return super.onUnbind(intent);
    }

    public void startSMSReceiver(){
        Log.i(TAG, "startSMSReceiver");

        if (intent != null) {
            this.filter = intent.getStringExtra("filter");
            this.messenger = (Messenger) intent.getExtras().get("messager");
            smsReceiver = new SMSReceiver(getApplicationContext(), messenger, filter);

            registerReceiver(smsReceiver, new IntentFilter("android.provider.Telephony.SMS_RECEIVED"));
        }
    }

    public void stopSMSReceiver(){
        Log.i(TAG, "stopSMSReceiver");
        unregisterReceiver(smsReceiver);
    }
}


