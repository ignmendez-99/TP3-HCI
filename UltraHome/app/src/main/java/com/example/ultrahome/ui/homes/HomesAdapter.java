package com.example.ultrahome.ui.homes;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ultrahome.R;

import java.util.List;

public class HomesAdapter extends RecyclerView.Adapter<HomesAdapter.HomesViewHolder> {
    private List<String> homesNames;
    private Context context;
    private HomeToRoomViewModel model;
    private HomesFragment currentFragment;

    @NonNull
    @Override
    public HomesAdapter.HomesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.home_row_layout, parent, false);
        return new HomesViewHolder(v);
    }

    public HomesAdapter(Context context, List<String> namesList, HomesFragment currentFragment) {
        this.context = context;
        homesNames = namesList;
        this.currentFragment = currentFragment;
    }

    @Override
    public void onBindViewHolder(@NonNull HomesViewHolder holder, int position) {
        holder.homeName.setText(homesNames.get(position));
        holder.homesConstraintLayout.setOnClickListener(view -> {

            // we send the homeId to the RoomsFragment, so that the correct Rooms are loaded
            List<String> idList = currentFragment.getIdList();
            String idOfHomeClicked = idList.get(position);
            model = new ViewModelProvider(currentFragment.requireActivity()).get(HomeToRoomViewModel.class);
            model.storeHomeId(idOfHomeClicked);

            // we navigate to the Rooms screen of this Particular Home
            currentFragment.navigateToRoomsFragment(view);
        });
    }

    public Context getContext() {
        return currentFragment.getContext();
    }

    public void deleteItem(int position) {
        currentFragment.deleteHome(currentFragment.getView(), position);
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
