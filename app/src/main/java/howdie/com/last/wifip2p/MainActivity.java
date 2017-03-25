package howdie.com.last.wifip2p;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.IntentFilter;
import android.net.Uri;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;

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

    String host;
    String port;

    ClientCodeRunner clientCodeRunner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        host = ((EditText) findViewById(R.id.server_edittext)).toString();
        port = ((EditText) findViewById(R.id.port_edittext)).toString();

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
            try {
                clientCodeRunner = new ClientCodeRunner(this,
                        InetAddress.getByName(host), Integer.parseInt(port));
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
        }
        clientCodeRunner.start();
    }

    class ClientCodeRunner extends Thread {

        private Context myContext;
        private InetAddress host;
        private int port;

        ClientCodeRunner(Context myContext, InetAddress host, int port){
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
                 * port, and timeout information.
                 */
                socket.bind(null);
                socket.connect((new InetSocketAddress(host, port)), 500);

                /**
                 * Create a byte stream from a JPEG file and pipe it to the output stream
                 * of the socket. This data will be retrieved by the server device.
                 */
                OutputStream outputStream = socket.getOutputStream();
                ContentResolver cr = context.getContentResolver();
                InputStream inputStream = null;
                inputStream = cr.openInputStream(Uri.parse("wifip2pTest/something.dontknow"));
                while ((len = inputStream.read(buf)) != -1) {
                    outputStream.write(buf, 0, len);
                }
                outputStream.close();
                inputStream.close();
            } catch (FileNotFoundException e) {
                //catch logic
            } catch (IOException e) {
                //catch logic
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
            }

        }
    }

}
