package com.example.ultrahome.ui.favorites;

import android.os.Bundle;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.example.ultrahome.R;

public class FavoritesFragment extends Fragment {
    CardView expandable;
    CardView cardTemp;
    Button button;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_favorites, container, false);

        cardTemp = root.findViewById(R.id.card);
        expandable=root.findViewById(R.id.expandable_card);
        button = root.findViewById(R.id.button_expand);


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(expandable.getVisibility()==View.GONE){
                    TransitionManager.beginDelayedTransition(expandable, new AutoTransition());
                    expandable.setVisibility(View.VISIBLE);
                    button.setBackgroundResource(R.drawable.ic_keyboard_arrow_up_black_24dp);
                }
                else{
                    TransitionManager.beginDelayedTransition(expandable, new AutoTransition());
                    expandable.setVisibility(View.GONE);
                    button.setBackgroundResource(R.drawable.ic_keyboard_arrow_down_black_24dp);
                }
            }
        });
        return root;
    }
}
