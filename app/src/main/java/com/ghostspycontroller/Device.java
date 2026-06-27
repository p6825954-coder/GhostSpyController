package com.ghostspycontroller;

public class Device {
    public String id, ip, model, android, lastSeen;
    public Device(String id, String ip, String model, String android, String lastSeen) {
        this.id = id; this.ip = ip; this.model = model; this.android = android; this.lastSeen = lastSeen;
    }
}
