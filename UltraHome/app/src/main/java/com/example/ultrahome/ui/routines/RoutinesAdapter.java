package com.example.ultrahome.ui.routines;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ultrahome.R;

import java.util.List;

public abstract class RoutinesAdapter extends RecyclerView.Adapter<RoutinesAdapter.RoutinesViewHolder> {

    protected List<String> routinesNames;
    protected Context context;
    protected RoutinesFragment currentFragment;

    public RoutinesAdapter(Context context, List<String> namesList, RoutinesFragment currentFragment) {
        this.context = context;
        routinesNames = namesList;
        this.currentFragment = currentFragment;
    }

    public Context getContext() {
        return currentFragment.getContext();
    }

    public void deleteItem(int position) {
        currentFragment.showDeleteRoutineDialog(position);
    }

    @Override
    public void onBindViewHolder(@NonNull RoutinesViewHolder holder, final int position) {
        holder.routineName.setText(routinesNames.get(position));
    }

    @Override
    public int getItemCount() {
        return routinesNames.size();
    }

    public static class RoutinesViewHolder extends RecyclerView.ViewHolder{
        TextView routineName;
        ConstraintLayout routinesConstraintLayout;
        Button button_expand;

        public RoutinesViewHolder(View v) {
            super(v);
            routineName = v.findViewById(R.id.routine_name);
            routinesConstraintLayout = v.findViewById(R.id.routine_item);
            button_expand = v.findViewById(R.id.button_expand);
        }
    }
}


