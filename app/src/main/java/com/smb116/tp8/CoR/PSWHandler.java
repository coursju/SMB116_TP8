package com.smb116.tp8.CoR;

import android.content.Context;
import android.os.Messenger;
import android.telephony.SmsManager;
import android.util.Log;

public class PSWHandler extends BaseHandler {

    private final String TAG = "PSWHandler";
    private Context context;

    public PSWHandler(Context context, ChainHandler<String, String, Messenger, String> successor){
        super(context, successor);
        this.context = context;
    }

    public boolean handleRequest(String value, String number, Messenger messenger, String filter){

        String statut = "";
        String[] strTab = value.split("#");

        if( strTab[0].equals("088") && filterOK(filter) ) {

            if ( strTab.length == 3){

                /**Définir le mot de passe*/
                if (readConfiguration("088") == null && strTab[1].equals("0000") ){
                    if (writeConfiguration("088", strTab[2])){
                        Log.i(TAG, "OK!");
                        SmsManager.getDefault().sendTextMessage(number, null, "OK!", null, null);
                        sendMessage(messenger, TAG+": "+getDate());
                    }else{
                        Log.i(TAG, "someting wrong! try again");
                        SmsManager.getDefault().sendTextMessage(number, null, "someting wrong! try again", null, null);
                    }
                }else{
                    Log.i(TAG, "someting wrong! try again");
                    SmsManager.getDefault().sendTextMessage(number, null, "someting wrong! try again", null, null);
                }
                /**Modifier le motdepasse*/
                if (readConfiguration("088") == null && !strTab[1].equals("0000") ) {
                    Log.i(TAG, "Define the password first! or use 0000");
                    SmsManager.getDefault().sendTextMessage(number, null, "Define the password first! or use 0000", null, null);
                }else if (readConfiguration("088").equals(strTab[1])){
                    if (writeConfiguration("088", strTab[2])){
                        SmsManager.getDefault().sendTextMessage(number, null, "OK!", null, null);
                        sendMessage(messenger, TAG+": "+getDate());
                    }else{
                        Log.i(TAG, "someting wrong! try again");
                        SmsManager.getDefault().sendTextMessage(number, null, "someting wrong! try again", null, null);
                    }
                }else{
                    Log.i(TAG, "wrong password! try again");
                    SmsManager.getDefault().sendTextMessage(number, null, "wrong password! try again", null, null);
                }

            }else{
                Log.i(TAG, "informations manquantes");
                SmsManager.getDefault().sendTextMessage(number, null, "informations manquantes", null, null);
            }
        }else{
            Log.i(TAG, "filter used!");
        }

        return super.handleRequest(value, number, messenger, filter);
    }
}

