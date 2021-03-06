package com.erkprog.madlocationtracker.ui.btScanActivity;

import android.bluetooth.BluetoothDevice;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.erkprog.madlocationtracker.R;

import java.util.ArrayList;
import java.util.List;

public class BtDevicesAdapter extends RecyclerView.Adapter<BtDevicesAdapter.BtDeviceViewHolder> {

  private List<BluetoothDevice> mData;
  private OnDeviceClickListener mListener;

  interface OnDeviceClickListener {
    void onDeviceClicked(String deviceAddress);
  }

  BtDevicesAdapter(OnDeviceClickListener listener) {
    mData = new ArrayList<>();
    mListener = listener;
  }

  @NonNull
  @Override
  public BtDeviceViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
    View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.bt_device_item, viewGroup, false);
    return new BtDeviceViewHolder(v);
  }

  @Override
  public void onBindViewHolder(@NonNull BtDeviceViewHolder viewHolder, int i) {
    BluetoothDevice btDevice = mData.get(i);
    if (btDevice == null) {
      return;
    }

    viewHolder.tvName.setText(btDevice.getName());
    viewHolder.tvAddress.setText(btDevice.getAddress());
    viewHolder.view.setOnClickListener(v -> mListener.onDeviceClicked(btDevice.getAddress()));
  }

  void addDevice(BluetoothDevice device) {
    if (!mData.contains(device)) {
      mData.add(device);
    }
  }

  void clearData() {
    mData.clear();
    notifyDataSetChanged();
  }

  @Override
  public int getItemCount() {
    return mData.size();
  }

  class BtDeviceViewHolder extends RecyclerView.ViewHolder {
    private TextView tvName;
    private TextView tvAddress;
    private View view;

    BtDeviceViewHolder(@NonNull View itemView) {
      super(itemView);
      view = itemView;
      tvName = itemView.findViewById(R.id.bt_device_name);
      tvAddress = itemView.findViewById(R.id.bt_device_address);
    }
  }
}
