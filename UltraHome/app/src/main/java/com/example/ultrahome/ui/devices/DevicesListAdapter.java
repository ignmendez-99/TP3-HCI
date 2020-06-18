package com.example.ultrahome.ui.devices;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ultrahome.R;

import java.util.List;

public class DevicesListAdapter extends RecyclerView.Adapter<DevicesListAdapter.DevicesListViewHolder> {

    private List<String> devicesNames;
    private Context context;
    private DevicesListFragment currentFragment;
    private View view;

    public DevicesListAdapter(Context context, List<String> list, @NonNull DevicesListFragment currentFragment) {
        this.context = context;
        devicesNames = list;
        this.currentFragment = currentFragment;
        view = currentFragment.getView();
    }

    @NonNull
    @Override
    public DevicesListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.horizontal_devices_row, parent, false);
        return new DevicesListAdapter.DevicesListViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull DevicesListViewHolder holder, final int position) {
        holder.deviceName.setText(devicesNames.get(position));
        holder.devicesConstraintLayout.setOnClickListener(view -> {
            List<String> deviceTypeIds = currentFragment.getDeviceTypeIds();
            List<String> deviceIds = currentFragment.getIdList();
            String clickedDeviceTypeId = deviceTypeIds.get(position);
            String clickedDeviceId = deviceIds.get(position);
            currentFragment.insertNestedFragment(clickedDeviceTypeId, clickedDeviceId, view, position);
        });
    }

    @Override
    public int getItemCount() {
        return devicesNames.size();
    }

    public static class DevicesListViewHolder extends RecyclerView.ViewHolder {
        TextView deviceName;
        ConstraintLayout devicesConstraintLayout;

        public DevicesListViewHolder(View v) {
            super(v);
            deviceName = v.findViewById(R.id.device_name);
            devicesConstraintLayout = v.findViewById(R.id.horizontal_devices_row_layout);
        }
    }
}