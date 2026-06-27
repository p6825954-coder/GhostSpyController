package com.ghostspycontroller;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class DeviceListFragment extends Fragment implements DeviceAdapter.OnDeviceClickListener {
    RecyclerView rv;
    DeviceAdapter adapter;
    List<Device> devices = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_device_list, container, false);
        rv = v.findViewById(R.id.recycler);
        rv.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new DeviceAdapter(devices, this);
        rv.setAdapter(adapter);
        return v;
    }

    public void updateDevices(List<Device> devs) {
        devices = devs;
        adapter.updateList(devs);
    }

    @Override public void onDeviceClick(Device device) {
        ((MainActivity) getActivity()).onDeviceSelected(device.id, device.ip);
    }
}
