package com.example.ultrahome.ui.devices;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ultrahome.R;

import java.util.List;

public class RoomsAdapter extends RecyclerView.Adapter<RoomsAdapter.RoomsViewHolder> {

    private List<String> roomsNames;
    private Context context;  // useful for doing TOAST

    public RoomsAdapter(Context context, List<String> roomsList) {
        this.context = context;
        roomsNames = roomsList;
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
        holder.roomsConstraintLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // we navigate to the Rooms screen of this Particular Home
                Toast.makeText(context, "Clicked on " + roomsNames.get(position), Toast.LENGTH_SHORT).show();
            }
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
