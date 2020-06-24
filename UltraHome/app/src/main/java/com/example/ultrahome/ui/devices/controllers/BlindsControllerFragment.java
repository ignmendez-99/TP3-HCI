package com.example.ultrahome.ui.devices.controllers;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.ultrahome.R;
import com.example.ultrahome.apiConnection.ApiClient;
import com.example.ultrahome.apiConnection.ErrorHandler;
import com.example.ultrahome.apiConnection.entities.Error;
import com.example.ultrahome.apiConnection.entities.Result;
import com.example.ultrahome.apiConnection.entities.deviceEntities.blinds.BlindsState;

import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BlindsControllerFragment extends Fragment {

    private String deviceId;

    private Button openButton, closeButton;
    private SeekBar levelSeekBar;
    private ProgressBar currentLevelProgressBar, loadingProgressBar;
    private TextView levelTextView, statusTextView;

    private int currentLevel;
    private int level;
    private String status;
    private boolean runThreads = false, shouldShowFinished = false;

    private ApiClient api;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_blinds_controller, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        readBundle(getArguments());

        init(requireView());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        runThreads = false;
    }

    private void init(@NonNull View view) {
        openButton = view.findViewById(R.id.open_blinds_button);
        closeButton = view.findViewById(R.id.close_blinds_button);
        levelSeekBar = view.findViewById(R.id.level_blinds_seekBar);
        currentLevelProgressBar = view.findViewById(R.id.currentLevel_progressBar);
        levelTextView = view.findViewById(R.id.max_level);
        statusTextView = view.findViewById(R.id.status_textView);
        loadingProgressBar = view.findViewById(R.id.loading_progressBar);


        statusTextView.setVisibility(View.INVISIBLE);
        loadingProgressBar.setVisibility(View.INVISIBLE);

        openButton.setOnClickListener(this::openBlinds);
        closeButton.setOnClickListener(this::closeBlinds);

        api = ApiClient.getInstance();

        api.getBlindsState(deviceId, new Callback<Result<BlindsState>>() {
            @Override
            public void onResponse(@NonNull Call<Result<BlindsState>> call, @NonNull Response<Result<BlindsState>> response) {
                if (response.isSuccessful()) {
                    Result<BlindsState> result = response.body();
                    if (result != null) {
                        BlindsState blindsState = result.getResult();
                        status = blindsState.getStatus();
                        runThreads = true;
                        updateProgressBar();
                        level = blindsState.getLevel();
                        levelTextView.setText(level + "%");
                        levelSeekBar.setMax(100);
                        levelSeekBar.setProgress(level);
                        if (currentLevel == level)
                            closeButton.setEnabled(false);
                        else if (currentLevel == 0)
                            openButton.setEnabled(false);
                    } else {
                        ErrorHandler.handleError(response, requireView(), "MENSAJE");
// todo: falta poner mensaje amigable de error y PASARSELO a HandleError
                    }
                } else {
                    ErrorHandler.handleError(response, requireView(), "MENSAJE");
// todo: falta poner mensaje amigable de error y PASARSELO a HandleError
                }
            }

            @Override
            public void onFailure(@NonNull Call<Result<BlindsState>> call, @NonNull Throwable t) {
                ErrorHandler.handleUnexpectedError(t, requireView(), BlindsControllerFragment.this);
                // todo: aca no va mensaje amigable, ya que la misma funcion ya lanza un Snackbar
            }
        });


        levelSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                setLevel(levelSeekBar.getProgress());
            }
        });
    }

    private void readBundle(Bundle bundle) {
        if (bundle != null) {
            deviceId = bundle.getString("deviceId");
        }
    }

    @NonNull
    public static BlindsControllerFragment newInstance(String deviceId) {
        Bundle bundle = new Bundle();
        bundle.putString("deviceId", deviceId);

        BlindsControllerFragment fragment = new BlindsControllerFragment();
        fragment.setArguments(bundle);

        return fragment;
    }

    private void openBlinds(View view) {
        if (currentLevel == 0) {
            Toast.makeText(getContext(), "ALREADY FULLY OPEN!", Toast.LENGTH_SHORT).show();
            return;
        }

        api.openBlinds(deviceId, new Callback<Result<Boolean>>() {
            @Override
            public void onResponse(@NonNull Call<Result<Boolean>> call, @NonNull Response<Result<Boolean>> response) {
                if (response.isSuccessful()) {
                    Result<Boolean> result = response.body();
                    if (result != null) {
                        Toast.makeText(getContext(), "OPENING COMPLETELY", Toast.LENGTH_SHORT).show();
                        runThreads = true;
                        updateProgressBar();
                    } else {
                        ErrorHandler.handleError(response, requireView(), "MENSAJE");
// todo: falta poner mensaje amigable de error y PASARSELO a HandleError
                    }
                } else {
                    ErrorHandler.handleError(response, requireView(), "MENSAJE");
// todo: falta poner mensaje amigable de error y PASARSELO a HandleError
                }
            }

            @Override
            public void onFailure(@NonNull Call<Result<Boolean>> call, @NonNull Throwable t) {
                ErrorHandler.handleUnexpectedError(t, requireView(), BlindsControllerFragment.this);
                // todo: aca no va mensaje amigable, ya que la misma funcion ya lanza un Snackbar
            }
        });
    }

    private void closeBlinds(View view) {
        if (currentLevel == level) {
            Toast.makeText(getContext(), "ALREADY CLOSED TO MAX LEVEL!", Toast.LENGTH_SHORT).show();
            return;
        }

        api.closeBlinds(deviceId, new Callback<Result<Boolean>>() {
            @Override
            public void onResponse(@NonNull Call<Result<Boolean>> call, @NonNull Response<Result<Boolean>> response) {
                if (response.isSuccessful()) {
                    Result<Boolean> result = response.body();
                    if (result != null) {
                        Toast.makeText(getContext(), "CLOSING COMPLETELY", Toast.LENGTH_SHORT).show();
                        runThreads = true;
                        updateProgressBar();
                    } else {
                        ErrorHandler.handleError(response, requireView(), "MENSAJE");
// todo: falta poner mensaje amigable de error y PASARSELO a HandleError
                    }
                } else {
                    ErrorHandler.handleError(response, requireView(), "MENSAJE");
// todo: falta poner mensaje amigable de error y PASARSELO a HandleError
                }
            }

            @Override
            public void onFailure(@NonNull Call<Result<Boolean>> call, @NonNull Throwable t) {
                ErrorHandler.handleUnexpectedError(t, requireView(), BlindsControllerFragment.this);
                // todo: aca no va mensaje amigable, ya que la misma funcion ya lanza un Snackbar
            }
        });
    }

    private void setLevel(int newLevel) {
        if (newLevel == level)
            return;

        levelTextView.setText(newLevel + "%");

        api.setBlindsLevel(deviceId, newLevel, new Callback<Result<Integer>>() {
            @Override
            public void onResponse(@NonNull Call<Result<Integer>> call, @NonNull Response<Result<Integer>> response) {
                if (response.isSuccessful()) {
                    Result<Integer> result = response.body();
                    if (result != null) {
                        Toast.makeText(getContext(), "NEW MAX LEVEL SET AT  " + newLevel + "%", Toast.LENGTH_SHORT).show();
                        level = newLevel;
                        runThreads = true;
                        updateProgressBar();
                    } else {
                        ErrorHandler.handleError(response, requireView(), "MENSAJE");
// todo: falta poner mensaje amigable de error y PASARSELO a HandleError
                    }
                } else {
                    ErrorHandler.handleError(response, requireView(), "MENSAJE");
// todo: falta poner mensaje amigable de error y PASARSELO a HandleError
                }
            }

            @Override
            public void onFailure(@NonNull Call<Result<Integer>> call, @NonNull Throwable t) {
                ErrorHandler.handleUnexpectedError(t, requireView(), BlindsControllerFragment.this);
                // todo: aca no va mensaje amigable, ya que la misma funcion ya lanza un Snackbar
            }
        });
    }

    private void updateProgressBar() {  // todo: DOESN'T WORK
        new Thread(() -> {
            while (runThreads) {
                api.getBlindsState(deviceId, new Callback<Result<BlindsState>>() {
                    @Override
                    public void onResponse(@NonNull Call<Result<BlindsState>> call, @NonNull Response<Result<BlindsState>> response) {
                        if (response.isSuccessful()) {
                            Result<BlindsState> result = response.body();
                            if (result != null && runThreads) {
                                BlindsState blindsState = result.getResult();
                                currentLevel = blindsState.getCurrentLevel();
                                status = blindsState.getStatus();
                                switch (status) {
                                    case "opening":
                                        closeButton.setEnabled(true);
                                        openButton.setEnabled(false);
                                        statusTextView.setText("OPENING...");
                                        statusTextView.setVisibility(View.VISIBLE);
                                        loadingProgressBar.setVisibility(View.VISIBLE);
                                        shouldShowFinished = true;
                                        break;
                                    case "opened":
                                        closeButton.setEnabled(true);
                                        openButton.setEnabled(false);
                                        statusTextView.setVisibility(View.INVISIBLE);
                                        loadingProgressBar.setVisibility(View.INVISIBLE);
                                        if(shouldShowFinished) {
                                            shouldShowFinished = false;
                                            Toast.makeText(getContext(), "FINISHED OPENING!", Toast.LENGTH_SHORT).show();
                                        }
                                        runThreads = false;
                                        break;
                                    case "closing":
                                        closeButton.setEnabled(false);
                                        openButton.setEnabled(true);
                                        statusTextView.setText("CLOSING...");
                                        statusTextView.setVisibility(View.VISIBLE);
                                        loadingProgressBar.setVisibility(View.VISIBLE);
                                        shouldShowFinished = true;
                                        break;
                                    case "closed":
                                        closeButton.setEnabled(false);
                                        openButton.setEnabled(true);
                                        statusTextView.setVisibility(View.INVISIBLE);
                                        loadingProgressBar.setVisibility(View.INVISIBLE);
                                        if(shouldShowFinished) {
                                            shouldShowFinished = false;
                                            Toast.makeText(getContext(), "FINISHED CLOSING!", Toast.LENGTH_SHORT).show();
                                        }
                                        runThreads = false;
                                        break;

                                }
                                currentLevelProgressBar.setProgress(currentLevel);
                            } else {
                                ErrorHandler.handleError(response, requireView(), "MENSAJE");
// todo: falta poner mensaje amigable de error y PASARSELO a HandleError
                            }
                        } else {
                            ErrorHandler.handleError(response, requireView(), "MENSAJE");
// todo: falta poner mensaje amigable de error y PASARSELO a HandleError
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<Result<BlindsState>> call, @NonNull Throwable t) {
                        ErrorHandler.handleUnexpectedError(t, requireView(), BlindsControllerFragment.this);
                        // todo: aca no va mensaje amigable, ya que la misma funcion ya lanza un Snackbar
                    }
                });
                try {
                    Thread.sleep(2000);
                } catch (Exception e) {
                    System.out.println("ERROR SLEEPING WHEN UPDATING SEEKBAR: " + e.getMessage());
                }
            }
        }).start();
    }
}



