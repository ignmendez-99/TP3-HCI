package com.example.ultrahome.ui.rooms;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ultrahome.R;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class RoomsAdapter extends RecyclerView.Adapter<RoomsAdapter.RoomsViewHolder> {
    protected List<String> roomsNames;
    protected Context context;
    protected RoomsFragment currentFragment;
    private Map<Integer, String> numberOfDevicesInEachRoom;

    public RoomsAdapter(Context context, List<String> namesList, RoomsFragment currentFragment) {
        this.context = context;
        roomsNames = namesList;
        this.currentFragment = currentFragment;
    }

    public void notifyNumberOfDevicesRetrieved(int positionToChange, int numberOfDevices) {
        if(numberOfDevicesInEachRoom == null)
            numberOfDevicesInEachRoom = new HashMap<>();
        numberOfDevicesInEachRoom.put(positionToChange, String.valueOf(numberOfDevices));
    }

    public Context getContext() {
        return currentFragment.getContext();
    }

    void deleteItem(int position) {
        currentFragment.showDeleteRoomDialog(position);
    }

    @Override
    public void onBindViewHolder(@NonNull RoomsViewHolder holder, final int position) {
        holder.roomName.setText(roomsNames.get(position));
        if(numberOfDevicesInEachRoom != null) {
            String aux = numberOfDevicesInEachRoom.get(position);
            if (aux != null) {
                holder.amountOfDevicesInside.setText(aux);
                if(aux.equals("1"))
                    holder.textDevicesInside.setText(R.string.one_device_inside_string);
                else
                    holder.textDevicesInside.setText(R.string.multiple_devices_inside_string);
            }
        }
        holder.roomsConstraintLayout.setOnClickListener(view -> {
            // we navigate to the Devices screen of this particular Room
            currentFragment.navigateToDevicesFragment(view, position);
        });
    }

    @Override
    public int getItemCount() {
        return roomsNames.size();
    }

    public static class RoomsViewHolder extends RecyclerView.ViewHolder{
        TextView roomName;
        TextView amountOfDevicesInside;
        TextView textDevicesInside;
        ConstraintLayout roomsConstraintLayout;

        public RoomsViewHolder(View v) {
            super(v);
            roomName = v.findViewById(R.id.room_name);
            roomsConstraintLayout = v.findViewById(R.id.room_item);
            amountOfDevicesInside = v.findViewById(R.id.number_devices_inside);
            textDevicesInside = v.findViewById(R.id.devices_inside);
        }
    }
}
