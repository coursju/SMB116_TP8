package com.smb116.tp8.CoR;

import android.content.Context;
import android.os.Messenger;
import android.telephony.SmsManager;
import android.util.Log;

import com.smb116.tp8.tp9_sensors_extras.Tp9Sensors;

public class RequestStatutHandler extends BaseHandler {

    private final String TAG = "RequestStatutHandler";
    private Context context;

    public RequestStatutHandler(Context context, ChainHandler<String, String, Messenger, String> successor){
        super(context, successor);
        this.context = context;
    }

    public boolean handleRequest(String value, String number, Messenger messenger, String filter){

        String statut = "";
        String[] strTab = value.split("#");

        if( strTab[0].equals("88") && filterOK(filter) ) {

            /**Interrogation statut*/
            String accessNumber = readConfiguration("188");
            if (accessNumber == null) {
                Log.i(TAG, "OK!");
                /**Question TP8 */
//                SmsManager.getDefault().sendTextMessage(number, null, "OK!", null, null);
                /**Question TP9 */
                Tp9Sensors.getInstance(context).getStatus(number,messenger);
                sendMessage(messenger, TAG+": "+getDate());
            }else if(accessNumber.equals(number)
                        || accessNumber.equals("null")){
                Log.i(TAG, "OK!");
                /**Question TP8 */
//                SmsManager.getDefault().sendTextMessage(number, null, "OK!", null, null);
                /**Question TP9 */
                Tp9Sensors.getInstance(context).getStatus(number,messenger);
                sendMessage(messenger, TAG+": "+getDate());
            }else{
                Log.i(TAG, "wrong number! try again");
                SmsManager.getDefault().sendTextMessage(number, null, "wrong number! try again", null, null);
            }
        }else{
            Log.i(TAG, "filter used!");
        }

        return super.handleRequest(value, number, messenger, filter);
    }
}

