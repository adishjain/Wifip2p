package howdie.com.last.wifip2p;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

import static android.net.wifi.p2p.WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION;
import static android.net.wifi.p2p.WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION;
import static android.net.wifi.p2p.WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION;
import static android.net.wifi.p2p.WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION;

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
    //PeerListListener myPeerListListener;


    TextView textView;
    String host;
    String dataPort;
    String controlPort;

    ClientCodeRunner clientCodeRunner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = (TextView)findViewById(R.id.textView);

        mManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        mChannel = mManager.initialize(this, getMainLooper(), null);
        mReceiver = new WiFiDirectBroadcastReceiver(mManager, mChannel, this);
        WifiManager wm = (WifiManager) getSystemService(WIFI_SERVICE);
        String ip = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());

        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(WIFI_P2P_STATE_CHANGED_ACTION);
        mIntentFilter.addAction(WIFI_P2P_PEERS_CHANGED_ACTION);
        mIntentFilter.addAction(WIFI_P2P_CONNECTION_CHANGED_ACTION);
        mIntentFilter.addAction(WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
        mManager.discoverPeers(mChannel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onFailure(int reasonCode) {

            }
        });
        textView.setText(ip);
        host = ((EditText) findViewById(R.id.server_edittext)).getText().toString();
        dataPort = ((EditText) findViewById(R.id.port_edittext)).getText().toString();
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


    public void onClient(View view) {
        if(clientCodeRunner == null){
            clientCodeRunner = new ClientCodeRunner(this,
                    host, Integer.parseInt(dataPort));
            clientCodeRunner.start();
        }
    }
    public void onServer(View view){

        new FileServerAsyncTask(this,textView).execute();
        Log.d("HAHAHA","HEHEHE");
    }

    class ClientCodeRunner extends Thread {

        private Context myContext;
        private String host;
        private int port;

        ClientCodeRunner(Context myContext, String host, int port){
            this.myContext = myContext;
            this.host = host;
            this.port = port;
        }

        @Override
        public void run() {
            super.run();

            this.clientCode();
        }

        public void clientCode() {
            Context context = myContext.getApplicationContext();
            int len;
            Socket socket = new Socket();
            byte buf[] = new byte[1024];

            try {
                /**
                 * Create a client socket with the host,
                 * dataPort, and timeout information.
                 */
                socket.bind(null);
                socket.connect((new InetSocketAddress(host, port)), 500);

                /**
                 * Create a byte stream from a JPEG file and pipe it to the output stream
                 * of the socket. This data will be retrieved by the server device.
                 */
                Log.d("MAinAct","Sending file");
                OutputStream outputStream = socket.getOutputStream();
                ContentResolver cr = context.getContentResolver();
                File myFile = new File(Environment.getExternalStorageDirectory() + "/"
                        + "Music/GOD/SUNDAY.mp3");  //  + "DCIM/Camera/IMG_20160724_110133.jpg");;
                InputStream inputStream = null;
                inputStream = new FileInputStream(myFile);//(Uri.parse("DCIM/Camera/IMG_20160724_110133.jpg"));
                while ((len = inputStream.read(buf)) != -1) {
                    outputStream.write(buf, 0, len);
                }
                outputStream.close();
                inputStream.close();
            } catch (FileNotFoundException e) {
                //catch logic
                e.printStackTrace();
            } catch (IOException e) {
                //catch logic
                e.printStackTrace();
            }

        /**
         * Clean up any open sockets when done
         * transferring or if an exception occurred.
         */ finally {
                if (socket != null) {
                    if (socket.isConnected()) {
                        try {
                            socket.close();
                        } catch (IOException e) {
                            //catch logic
                        }
                    }
                }
                MainActivity.this.clientCodeRunner = null;
            }

        }
    }

}
