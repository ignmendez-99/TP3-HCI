package com.example.ultrahome.ui.rooms;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ultrahome.R;
import com.example.ultrahome.ui.homes.HomeToRoomViewModel;

import java.util.List;

public class RoomsAdapter extends RecyclerView.Adapter<RoomsAdapter.RoomsViewHolder> {

    private List<String> roomsNames;
    private Context context;
    private RoomsFragment currentFragment;
    private RoomToDeviceViewModel model;

    public RoomsAdapter(Context context, List<String> namesList, RoomsFragment currentFragment) {
        this.context = context;
        roomsNames = namesList;
        this.currentFragment = currentFragment;
    }

    public Context getContext() {
        return currentFragment.getContext();
    }

    public void deleteItem(int position) {
        currentFragment.deleteRoom(currentFragment.getView(), position);
    }

    @NonNull
    @Override
    public RoomsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.room_row_layout, parent, false);
        return new RoomsAdapter.RoomsViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RoomsViewHolder holder, final int position) {
        holder.roomName.setText(roomsNames.get(position));
        holder.roomsConstraintLayout.setOnClickListener(view -> {

            // we send the roomId to the DevicesFragment, so that the correct Devices are loaded
            List<String> idList = currentFragment.getIdList();
            String idOfRoomClicked = idList.get(position);
            model = new ViewModelProvider(currentFragment.requireActivity()).get(RoomToDeviceViewModel.class);
            model.storeRoomId(idOfRoomClicked);

            // we navigate to the Devices screen of this particular Room
            currentFragment.navigateToDevicesFragment(view);
        });
    }

    @Override
    public int getItemCount() {
        return roomsNames.size();
    }

    public static class RoomsViewHolder extends RecyclerView.ViewHolder{
        TextView roomName;
        TextView amountOfDevicesInside;
        ConstraintLayout roomsConstraintLayout;

        public RoomsViewHolder(View v) {
            super(v);
            roomName = v.findViewById(R.id.room_name);
            roomsConstraintLayout = v.findViewById(R.id.rooms_row_layout);
            amountOfDevicesInside = v.findViewById(R.id.number_devices_inside);
        }
    }
}
