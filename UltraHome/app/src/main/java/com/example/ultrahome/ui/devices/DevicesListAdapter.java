package com.example.ultrahome.ui.devices;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ultrahome.R;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

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
        View v = inflater.inflate(R.layout.device_horizontal_layout, parent, false);
        return new DevicesListAdapter.DevicesListViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull DevicesListViewHolder holder, final int position) {
        holder.deviceName.setText(devicesNames.get(position));

        List<String> deviceTypeIds = currentFragment.getDeviceTypeIds();
        String deviceTypeId = deviceTypeIds.get(position);
        String drawableImage = null;
        switch(deviceTypeId) {
            case "c89b94e8581855bc":
                drawableImage = "@drawable/speaker_icon_foreground";
                break;
            case "dbrlsh7o5sn8ur4i":
                drawableImage = "@drawable/faucet_icon_foreground";
                break;
            case "eu0v2xgprrhhg41g":
                drawableImage = "@drawable/blinds_icon_foreground";
                break;
            case "go46xmbqeomjrsjr":
                drawableImage = "@drawable/lights_icon_foreground";
                break;
            case "lsf78ly0eqrjbz91":
                drawableImage = "@drawable/door_icon_foreground";
                break;
            case "ofglvd9gqx8yfl3l":
                drawableImage = "@drawable/vacuum_icon_foreground";
                break;
            case "rnizejqr2di0okho":
                drawableImage = "@drawable/refrigerator_icon_foreground";
                break;
        }
        int imageResource = currentFragment.getResources().getIdentifier(drawableImage, "drawable",
                currentFragment.requireActivity().getPackageName());
        holder.deviceImage.setImageResource(imageResource);

        holder.devicesConstraintLayout.setOnClickListener(view -> {
            List<String> deviceTypeIdsWhenClicked = currentFragment.getDeviceTypeIds();
            List<String> deviceIds = currentFragment.getIdList();
            String clickedDeviceTypeId = deviceTypeIdsWhenClicked.get(position);
            String clickedDeviceId = deviceIds.get(position);
            currentFragment.insertNestedFragment(clickedDeviceTypeId, clickedDeviceId, devicesNames.get(position), position);
        });
    }

    @Override
    public int getItemCount() {
        return devicesNames.size();
    }

    public static class DevicesListViewHolder extends RecyclerView.ViewHolder {
        TextView deviceName;
        ConstraintLayout devicesConstraintLayout;
        ImageView deviceImage;

        public DevicesListViewHolder(View v) {
            super(v);
            deviceName = v.findViewById(R.id.device_name);
            devicesConstraintLayout = v.findViewById(R.id.horizontal_devices_row_layout);
            deviceImage = v.findViewById(R.id.device_image);
        }
    }
}