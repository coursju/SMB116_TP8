package com.smb116.tp8;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.Parcelable;
import android.os.RemoteException;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

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

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    public final int REQUEST_CODE = 1;
    private static Handler handler;
    private Messenger messager;
    private BroadcastService mService;
    private boolean mBound = false;
    private ServiceConnection connection = this.configureConnection();

    private LinearLayout containerLayout;
    private TextView infosTxt;

    private String filter =
                    "PSWHandler , " +
                    "ConfigureAccessHandler , " +
                    "RequestAccessHandler , " +
                    "DeleteAccessHandler , " +
                    "RequestStatutHandler , " +
                    "GetStatutOnHandler , " +
                    "GetStatutOffHandler , " +
                    "GetPositionOnHandler , " +
                    "GetPositionOffHandler ";
    private String mess ="";

    IBroadcastService iBroadcastService;

    @Override
    protected void onStart() {
        super.onStart();

        /** Question 2 */
        bindBroadcastService();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.containerLayout = findViewById(R.id.container_layout);
        this.infosTxt = findViewById(R.id.infos_txt);

        configureHandler();
        checkForPermission();
    }

    public void onClickStart(View view) throws RemoteException {
        /** Question 1 */
//        bindBroadcastService();
        /** Question 2 */
        iBroadcastService.startAIDLSMSReceiver();

        Toast.makeText(getApplicationContext(),"Service started",Toast.LENGTH_SHORT).show();
        Log.i(TAG,"Service started");
    }

    public void onClickStop(View view) throws RemoteException {
        /** Question 1 */
//        if (mBound) unbindService(connection);
        /** Question 2 */
        iBroadcastService.stopAIDLSMSReceiver();

        Toast.makeText(getApplicationContext(),"Service stopped",Toast.LENGTH_SHORT).show();
        Log.i(TAG,"Service stopped");
        mBound = false;
    }

    public void bindBroadcastService(){
        Intent intent = new Intent(this,BroadcastService.class);
        messager = new Messenger(handler);
        intent.putExtra("messager", messager);
        intent.putExtra("filter", filter);
        bindService(intent, connection, Context.BIND_AUTO_CREATE);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void checkForPermission(){
        if (ContextCompat.checkSelfPermission(
                getApplicationContext(), Manifest.permission.RECEIVE_SMS) ==
                PackageManager.PERMISSION_GRANTED
        && ContextCompat.checkSelfPermission(
                getApplicationContext(), Manifest.permission.SEND_SMS) ==
                PackageManager.PERMISSION_GRANTED)
        {
            this.containerLayout.setVisibility(View.VISIBLE);
        } else {
            requestPermissions(new String[] { Manifest.permission.RECEIVE_SMS, Manifest.permission.SEND_SMS }, REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_CODE:
                if (grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    this.containerLayout.setVisibility(View.VISIBLE);
                } else {
                    Log.i(TAG, "Permission denied");
                }
                return;
        }
    }

    public void configureHandler(){
        handler = new Handler() {
            public void handleMessage(Message message) {
                Bundle extras = message.getData();
                if (extras != null) {
                    mess += extras.getString("mess")+"\n";
                    infosTxt.setText(mess);
                }
            };
        };
    }

    public ServiceConnection configureConnection(){
        return new ServiceConnection() {

            @Override
            public void onServiceConnected(ComponentName className,
                                           IBinder service) {
                /** Question 1 */
//                BroadcastService.LocalBinder binder = (BroadcastService.LocalBinder) service;
//                mService = binder.getService();
                /** Question 2 */
                iBroadcastService = IBroadcastService.Stub.asInterface(service);
                mBound = true;
            }

            @Override
            public void onServiceDisconnected(ComponentName arg0) {
                mBound = false;
            }
        };
    }
}