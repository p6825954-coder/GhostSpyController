package com.ghostspycontroller;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class DeviceAdapter extends RecyclerView.Adapter<DeviceAdapter.VH> {
    public interface OnDeviceClickListener { void onDeviceClick(Device device); }
    private List<Device> devices;
    private OnDeviceClickListener listener;

    public DeviceAdapter(List<Device> devices, OnDeviceClickListener listener) {
        this.devices = devices; this.listener = listener;
    }

    @NonNull @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_device, parent, false);
        return new VH(v);
    }

    @Override public void onBindViewHolder(@NonNull VH h, int pos) {
        Device d = devices.get(pos);
        h.id.setText(d.id);
        h.model.setText(d.model + " | " + d.android + " | IP: " + d.ip);
        h.last.setText("Last: " + d.lastSeen);
        h.itemView.setOnClickListener(v -> listener.onDeviceClick(d));
    }

    @Override public int getItemCount() { return devices.size(); }

    public void updateList(List<Device> newList) {
        devices = newList; notifyDataSetChanged();
    }

    static class VH extends RecyclerView.ViewHolder {
        TextView id, model, last;
        VH(View v) {
            super(v);
            id = v.findViewById(R.id.deviceId);
            model = v.findViewById(R.id.deviceModel);
            last = v.findViewById(R.id.deviceLastSeen);
        }
    }
}
