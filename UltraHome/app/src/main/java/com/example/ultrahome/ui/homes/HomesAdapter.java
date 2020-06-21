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

public abstract class HomesAdapter extends RecyclerView.Adapter<HomesAdapter.HomesViewHolder> {
    private List<String> homesNames;
    protected Context context;
    private HomesFragment currentFragment;

    public HomesAdapter(Context context, List<String> namesList, HomesFragment currentFragment) {
        this.context = context;
        homesNames = namesList;
        this.currentFragment = currentFragment;
    }

    @Override
    public void onBindViewHolder(@NonNull HomesViewHolder holder, int position) {
        holder.homeName.setText(homesNames.get(position));
        holder.homesConstraintLayout.setOnClickListener(view -> {
            // we navigate to the Rooms screen of this Particular Home
            currentFragment.navigateToRoomsFragment(view, position);
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
            homesConstraintLayout = v.findViewById(R.id.home_item);
            amountOfRoomsInside = v.findViewById(R.id.number_rooms_inside);
        }
    }
}
