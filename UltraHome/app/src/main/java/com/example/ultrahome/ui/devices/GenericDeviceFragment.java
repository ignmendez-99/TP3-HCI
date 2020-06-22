package com.example.ultrahome.ui.devices;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.ultrahome.R;
import com.example.ultrahome.ui.devices.controllers.BlindsControllerFragment;
import com.example.ultrahome.ui.devices.controllers.DoorControllerFragment;
import com.example.ultrahome.ui.devices.controllers.FaucetControllerFragment;
import com.example.ultrahome.ui.devices.controllers.LightsControllerFragment;
import com.example.ultrahome.ui.devices.controllers.RefrigeratorControllerFragment;
import com.example.ultrahome.ui.devices.controllers.SpeakerControllerFragment;
import com.example.ultrahome.ui.devices.controllers.VacuumControllerFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.HashMap;
import java.util.Map;

public class GenericDeviceFragment extends Fragment {
    private Button deleteButton;
    private FloatingActionButton editButton;
    private FloatingActionButton doneButton;
    private EditText nameEdited;
    private TextView name;

    private String currentName;

    private String deviceId;
    private String deviceTypeId;
    private int positionInRecyclerView;

    private boolean editing = false;

    private Map<String, Integer> supportedDeviceTypeIds;
    private Fragment childFragment;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.generic_device, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        readBundle(getArguments());

        deleteButton = view.findViewById(R.id.delete_button);
        deleteButton.setOnClickListener(this::deletePressed);
        deleteButton.setVisibility(View.INVISIBLE);
        deleteButton.setClickable(false);

        doneButton = view.findViewById(R.id.done_button);
        doneButton.setOnClickListener(this::editPressed);
        doneButton.setVisibility(View.INVISIBLE);
        doneButton.setClickable(false);

        editButton = view.findViewById(R.id.edit_button);
        editButton.setOnClickListener(this::editPressed);

        nameEdited = view.findViewById(R.id.name_edited);
        nameEdited.setVisibility(View.INVISIBLE);

        name = view.findViewById(R.id.name);
        name.setText(currentName);

        nameEdited.setText(currentName);

        initDeviceTypeIdMap();

        insertNestedFragment(view);
    }

    @NonNull
    public static GenericDeviceFragment newInstance(String deviceId, String deviceName, String deviceTypeId, int positionInRecyclerView) {
        Bundle bundle = new Bundle();
        bundle.putString("deviceName", deviceName);
        bundle.putString("deviceId", deviceId);
        bundle.putString("deviceTypeId", deviceTypeId);
        bundle.putInt("positionInRecyclerView", positionInRecyclerView);

        GenericDeviceFragment fragment = new GenericDeviceFragment();
        fragment.setArguments(bundle);

        return fragment;
    }

    private void readBundle(Bundle bundle) {
        if (bundle != null) {
            deviceId = bundle.getString("deviceId");
            currentName = bundle.getString("deviceName");
            deviceTypeId = bundle.getString("deviceTypeId");
            positionInRecyclerView = bundle.getInt("positionInRecyclerView");
        }
    }

    private void insertNestedFragment(View v) {
        Integer layoutToChoose = supportedDeviceTypeIds.get(deviceTypeId);
        if(layoutToChoose != null) {
            switch(layoutToChoose) {
                case R.layout.fragment_lights_controller:
                    childFragment = LightsControllerFragment.newInstance(deviceId, positionInRecyclerView);
                    break;
                case R.layout.fragment_blinds_controller:
                    childFragment = BlindsControllerFragment.newInstance(deviceId, positionInRecyclerView);
                    break;
                case R.layout.fragment_door_controller:
                    childFragment = DoorControllerFragment.newInstance(deviceId, positionInRecyclerView);
                    break;
                case R.layout.fragment_faucet_controller:
                    childFragment = FaucetControllerFragment.newInstance(deviceId, positionInRecyclerView);
                    break;
                case R.layout.fragment_refrigerator_controller:
                    childFragment = RefrigeratorControllerFragment.newInstance(deviceId, positionInRecyclerView);
                    break;
                case R.layout.fragment_speaker_controller:
                    childFragment = SpeakerControllerFragment.newInstance(deviceId, positionInRecyclerView);
                    break;
                case R.layout.fragment_vacuum_controller:
                    childFragment = VacuumControllerFragment.newInstance(deviceId, positionInRecyclerView);
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + layoutToChoose);
            }
            FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
            transaction.replace(R.id.device_controls, childFragment).commit();
        } else {
            Snackbar.make(v, "Could not load device", Snackbar.LENGTH_SHORT);
        }
    }

    // todo: esto quizas se puede llevar a otra Clase, ya que sino esta clase hace demasiado
    private void initDeviceTypeIdMap() {
        supportedDeviceTypeIds = new HashMap<>();
        // SPEAKER
        supportedDeviceTypeIds.put("c89b94e8581855bc", R.layout.fragment_speaker_controller);
        // FAUCET
        supportedDeviceTypeIds.put("dbrlsh7o5sn8ur4i", R.layout.fragment_faucet_controller);
        // BLINDS
        supportedDeviceTypeIds.put("eu0v2xgprrhhg41g", R.layout.fragment_blinds_controller);
        // LIGHTS
        supportedDeviceTypeIds.put("go46xmbqeomjrsjr", R.layout.fragment_lights_controller);
        // DOOR
        supportedDeviceTypeIds.put("lsf78ly0eqrjbz91", R.layout.fragment_door_controller);
        // VACUUM
        supportedDeviceTypeIds.put("ofglvd9gqx8yfl3l", R.layout.fragment_vacuum_controller);
        // REFRIGERATOR
        supportedDeviceTypeIds.put("rnizejqr2di0okho", R.layout.fragment_refrigerator_controller);
    }

    private void editPressed(View view) {
        if (editing) {
            editButton.setVisibility(View.VISIBLE);
            editButton.setClickable(true);
            deleteButton.setVisibility(View.INVISIBLE);
            deleteButton.setClickable(false);
            doneButton.setVisibility(View.INVISIBLE);
            doneButton.setClickable(false);
            nameEdited.setVisibility(View.INVISIBLE);
            name.setVisibility(View.VISIBLE);

            if (! nameEdited.getText().toString().equals(currentName)) {
                currentName = nameEdited.getText().toString();
                name.setText(currentName);
                // API CALL PARA RENOMBRAR
            }
        } else {
            editButton.setVisibility(View.INVISIBLE);
            editButton.setClickable(false);
            deleteButton.setVisibility(View.VISIBLE);
            deleteButton.setClickable(true);
            doneButton.setVisibility(View.VISIBLE);
            doneButton.setClickable(true);
            nameEdited.setVisibility(View.VISIBLE);
            name.setVisibility(View.INVISIBLE);
        }
        editing = !editing;
    }

    private void deletePressed(View view) {
        // detach the Controller Fragment from screen
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.detach(childFragment).commit();

        DevicesListFragment containerFragment = (DevicesListFragment) getParentFragment();
        assert containerFragment != null;
        containerFragment.showDeleteDeviceDialog(positionInRecyclerView);
    }
}
