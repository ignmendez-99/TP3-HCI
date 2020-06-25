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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class HomesAdapter extends RecyclerView.Adapter<HomesAdapter.HomesViewHolder> {
    private List<String> homesNames;
    protected Context context;
    private HomesFragment currentFragment;
    private Map<Integer, String> numberOfRoomsInEachHome;

    public HomesAdapter(Context context, List<String> namesList, HomesFragment currentFragment) {
        this.context = context;
        homesNames = namesList;
        this.currentFragment = currentFragment;
    }

    @Override
    public void onBindViewHolder(@NonNull HomesViewHolder holder, int position) {
        holder.homeName.setText(homesNames.get(position));
        if(numberOfRoomsInEachHome != null) {
            String aux = numberOfRoomsInEachHome.get(position);
            if (aux != null) {
                holder.amountOfRoomsInside.setText(aux);
                if(aux.equals("0"))
                    holder.textRoomsInside.setText(R.string.one_room_inside_string);
                else
                    holder.textRoomsInside.setText(R.string.multiple_rooms_inside_string);
            }
        }
        holder.homesConstraintLayout.setOnClickListener(view -> {
            // we navigate to the Rooms screen of this Particular Home
            currentFragment.navigateToRoomsFragment(view, position);
        });
    }

    public void notifyNumberOfRoomsRetrieved(int positionToChange, int numberOfRooms) {
        if(numberOfRoomsInEachHome == null)
            numberOfRoomsInEachHome = new HashMap<>();
        numberOfRoomsInEachHome.put(positionToChange, String.valueOf(numberOfRooms));
    }

    public Context getContext() {
        return currentFragment.getContext();
    }

    public void deleteItem(int position) {
        currentFragment.showDeleteHomeDialog(position);
    }

    @Override
    public int getItemCount() {
        return homesNames.size();
    }

    public static class HomesViewHolder extends RecyclerView.ViewHolder {

        TextView homeName;
        TextView amountOfRoomsInside;
        TextView textRoomsInside;
        ConstraintLayout homesConstraintLayout;

        public HomesViewHolder(View v) {
            super(v);
            homeName = v.findViewById(R.id.home_name);
            homesConstraintLayout = v.findViewById(R.id.home_item);
            amountOfRoomsInside = v.findViewById(R.id.number_rooms_inside);
            textRoomsInside = v.findViewById(R.id.rooms_inside);
        }
    }
}
