package com.example.ultrahome.ui.routines;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
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
        holder.execute_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentFragment.execute(v, position);
            }
        });
        holder.button_expand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(holder.layout_expand.getVisibility()==View.GONE){
                    holder.layout_expand.setVisibility(View.VISIBLE);
                    holder.button_expand.setBackgroundResource(R.drawable.ic_keyboard_arrow_up_black_24dp);
                }
                else{
                    holder.layout_expand.setVisibility(View.GONE);
                    holder.button_expand.setBackgroundResource(R.drawable.ic_keyboard_arrow_down_black_24dp);
                }
                currentFragment.expand(v, holder.routineDescription, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return routinesNames.size();
    }

    public static class RoutinesViewHolder extends RecyclerView.ViewHolder {
        TextView routineName;
        TextView routineDescription;
        ConstraintLayout routinesConstraintLayout;
        LinearLayout layout_expand;
        Button button_expand;
        Button execute_button;

        public RoutinesViewHolder(View v) {
            super(v);
            routineName = v.findViewById(R.id.routine_name);
            routinesConstraintLayout = v.findViewById(R.id.routine_item);
            button_expand = v.findViewById(R.id.button_expand);
            layout_expand = v.findViewById(R.id.expanded_layout);
            execute_button = v.findViewById(R.id.execute_button);
            routineDescription = v.findViewById(R.id.routine_description);
        }
    }
}


