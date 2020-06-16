package com.example.ultrahome.ui.devices;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ultrahome.R;

import java.util.List;

public class HomesAdapter extends RecyclerView.Adapter<HomesAdapter.HomesViewHolder> {
    private List<String> homesNames;
    private Context context;  // useful for doing TOAST

    @NonNull
    @Override
    public HomesAdapter.HomesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.home_row_layout, parent, false);
        return new HomesViewHolder(v);
    }

    public HomesAdapter(Context context, List<String> list) {
        this.context = context;
        homesNames = list;
    }

    @Override
    public void onBindViewHolder(@NonNull HomesViewHolder holder, final int position) {
        holder.homeName.setText(homesNames.get(position));
        holder.homesConstraintLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // we navigate to the Rooms screen of this Particular Home
                final NavController navController =  Navigation.findNavController(view);
                navController.navigate(R.id.roomsFragment);
            }
        });
    }

    @Override
    public int getItemCount() {
        return homesNames.size();
    }

    public static class HomesViewHolder extends RecyclerView.ViewHolder {

        TextView homeName;
        TextView amountOfRoomsInside;
        ConstraintLayout homesConstraintLayout;

        public HomesViewHolder(View v) {
            super(v);
            homeName = v.findViewById(R.id.home_name);
            homesConstraintLayout = v.findViewById(R.id.homes_row_layout);
            amountOfRoomsInside = v.findViewById(R.id.number_rooms_inside);
        }
    }
}
