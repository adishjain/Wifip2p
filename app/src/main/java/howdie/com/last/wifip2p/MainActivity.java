package howdie.com.last.wifip2p;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.p2p.WifiP2pManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;

import java.nio.channels.Channel;

import static android.net.wifi.p2p.WifiP2pManager.*;
import static android.os.Looper.getMainLooper;

public class MainActivity extends AppCompatActivity {
    private final IntentFilter intentFilter = new IntentFilter();
    public static final String TAG = "wifidirectdemo";
    private BroadcastReceiver receiver = null;
    private boolean isWifiP2pEnabled = false;
    private boolean retryChannel = false;
    WifiP2pManager mManager;
    WifiP2pManager.Channel mChannel;
    BroadcastReceiver mReceiver;
    IntentFilter mIntentFilter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mReceiver, mIntentFilter);
    }
    /* unregister the broadcast receiver */
    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mReceiver);
    }


}
