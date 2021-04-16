package com.smb116.tp8;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

import com.smb116.tp8.CoR.ChainHandler;
import com.smb116.tp8.CoR.ConfigureAccessHandler;
import com.smb116.tp8.CoR.DeleteAccessHandler;
import com.smb116.tp8.CoR.GetPositionOffHandler;
import com.smb116.tp8.CoR.GetPositionOnHandler;
import com.smb116.tp8.CoR.GetStatutOffHandler;
import com.smb116.tp8.CoR.GetStatutOnHandler;
import com.smb116.tp8.CoR.PSWHandler;
import com.smb116.tp8.CoR.RequestAccessHandler;
import com.smb116.tp8.CoR.RequestStatutHandler;
import com.smb116.tp8.CoR.TraceHandler;

public class SMSReceiver extends BroadcastReceiver
{
    private static final String TAG = "SMSReceiver";
    private Context context;
    private Messenger messenger;
    private String filter;

    public SMSReceiver(Context context, Messenger messenger, String filter){
        this.context = context;
        this.messenger = messenger;
        this.filter = filter;
    }

    @Override
    public void onReceive(Context context, Intent intent)
    {
        Bundle bundle = intent.getExtras();
        SmsMessage[] msgs = null;
        String value = "";
        String number = "";

        if (bundle != null){
            Object[] pdus = (Object[]) bundle.get("pdus");
            msgs = new SmsMessage[pdus.length];

            for (int i=0; i<msgs.length; i++){
                msgs[i] = SmsMessage.createFromPdu((byte[])pdus[i]);
                number = msgs[i].getOriginatingAddress();
                value = msgs[i].getMessageBody().toString();
            }
            Log.i(TAG, "value = "+value+" number = "+number);

            ChainHandler chainHandler =
                    new PSWHandler(context,
                            new ConfigureAccessHandler(context,
                                    new RequestAccessHandler(context,
                                            new DeleteAccessHandler(context,
                                                    new RequestStatutHandler(context,
                                                            new GetStatutOnHandler(context,
                                                                    new GetStatutOffHandler(context,
                                                                            new GetPositionOnHandler(context,
                                                                                    new GetPositionOffHandler(context, null)))))))));

            chainHandler.handleRequest(value, number, messenger, filter);
        }
    }
}