package com.ghostspycontroller;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.fragment.app.Fragment;
import org.json.JSONObject;

public class ControlFragment extends Fragment {
    private String deviceId;
    public ControlFragment(String deviceId) { this.deviceId = deviceId; }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_control, container, false);
        // setup button listeners...
        return v;
    }

    private void sendCommand(String cmd) {
        SocketHandler.sendCommand(deviceId, cmd, null);
    }
}
