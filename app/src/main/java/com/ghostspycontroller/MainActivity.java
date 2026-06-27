package com.ghostspycontroller;

import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import org.json.JSONArray;
import org.json.JSONObject;
import java.net.URL;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private TextView statusText, deviceCountText;
    private Handler handler = new Handler();
    private Runnable deviceRefresher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        statusText = findViewById(R.id.statusText);
        deviceCountText = findViewById(R.id.deviceCount);

        SocketHandler.init(this);
        SocketHandler.connect();

        startDeviceRefresher();
    }

    public void updateConnectionStatus(boolean connected) {
        runOnUiThread(() -> {
            statusText.setText(connected ? "GhostSpy C2 [ONLINE]" : "GhostSpy C2 [OFFLINE]");
        });
    }

    public void handleIncomingData(JSONObject data) {
        // bisa dikembangkan nanti
    }

    private void startDeviceRefresher() {
        deviceRefresher = new Runnable() {
            @Override
            public void run() {
                if (SocketHandler.isConnected()) {
                    fetchDeviceCount();
                } else {
                    deviceCountText.setText("Tidak terhubung ke server");
                }
                handler.postDelayed(this, 5000);
            }
        };
        handler.post(deviceRefresher);
    }

    private void fetchDeviceCount() {
        new Thread(() -> {
            try {
                URL url = new URL("https://ghostspy.bruang.biz.id/api/devices");
                BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) sb.append(line);
                JSONArray arr = new JSONArray(sb.toString());
                int count = arr.length();
                runOnUiThread(() -> deviceCountText.setText("Perangkat online: " + count));
            } catch (Exception e) {
                runOnUiThread(() -> deviceCountText.setText("Gagal mengambil data"));
            }
        }).start();
    }
}
