package com.example.ultrahome.ui.devices.controllers;

import android.os.Bundle;
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
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.lifecycle.ProcessLifecycleOwner;

import com.example.ultrahome.R;
import com.example.ultrahome.apiConnection.ApiClient;
import com.example.ultrahome.apiConnection.ErrorHandler;
import com.example.ultrahome.apiConnection.entities.Result;
import com.example.ultrahome.apiConnection.entities.deviceEntities.blinds.BlindsState;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BlindsControllerFragment extends Fragment implements LifecycleObserver {

    private String deviceId;

    private NotificationManagerCompat notificationManager;
    NotificationCompat.Builder openedBlindsBuilder, closedBlindsBuilder;

    private boolean foreground = true;

    private Button openButton, closeButton;
    private SeekBar levelSeekBar;
    private ProgressBar currentLevelProgressBar, loadingProgressBar;
    private TextView levelTextView, statusTextView;

    private int currentLevel;
    private int level;
    private String status;
    private boolean runThreads = true, shouldShowFinished = false;

    private ApiClient api;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_blinds_controller, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        readBundle(getArguments());

        init(view);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        runThreads = false;
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    public void onAppBackgrounded() {
        foreground = false;
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    public void onAppForegrounded() {
        foreground = true;
    }

    private void updateState() {
        new Thread(() -> {
            while(runThreads) {
                api.getBlindsState(deviceId, new Callback<Result<BlindsState>>() {
                    @Override
                    public void onResponse(@NonNull Call<Result<BlindsState>> call, @NonNull Response<Result<BlindsState>> response) {
                        if (response.isSuccessful()) {
                            Result<BlindsState> result = response.body();
                            if (result != null && runThreads) {
                                BlindsState blindsState = result.getResult();
                                status = blindsState.getStatus();
                                currentLevel = blindsState.getCurrentLevel();
                                status = blindsState.getStatus();
                                switch (status) {
                                    case "opening":
                                        closeButton.setEnabled(true);
                                        openButton.setEnabled(false);
                                        statusTextView.setText(getString(R.string.opening_string));
                                        statusTextView.setVisibility(View.VISIBLE);
                                        loadingProgressBar.setVisibility(View.VISIBLE);
                                        levelSeekBar.setEnabled(false);
                                        shouldShowFinished = true;
                                        break;
                                    case "opened":
                                        closeButton.setEnabled(true);
                                        openButton.setEnabled(false);
                                        statusTextView.setVisibility(View.INVISIBLE);
                                        loadingProgressBar.setVisibility(View.INVISIBLE);
                                        levelSeekBar.setEnabled(true);
                                        if(shouldShowFinished) {
                                            shouldShowFinished = false;
                                            if(foreground)
                                                Toast.makeText(getContext(), getString(R.string.finished_opening_string), Toast.LENGTH_SHORT).show();
                                            else
                                                notificationManager.notify(123, openedBlindsBuilder.build());
                                        }
                                        break;
                                    case "closing":
                                        closeButton.setEnabled(false);
                                        openButton.setEnabled(true);
                                        statusTextView.setText(getString(R.string.closing_string));
                                        statusTextView.setVisibility(View.VISIBLE);
                                        loadingProgressBar.setVisibility(View.VISIBLE);
                                        levelSeekBar.setEnabled(false);
                                        shouldShowFinished = true;
                                        break;
                                    case "closed":
                                        closeButton.setEnabled(false);
                                        openButton.setEnabled(true);
                                        statusTextView.setVisibility(View.INVISIBLE);
                                        loadingProgressBar.setVisibility(View.INVISIBLE);
                                        levelSeekBar.setEnabled(true);
                                        if(shouldShowFinished) {
                                            shouldShowFinished = false;
                                            if(foreground)
                                                Toast.makeText(getContext(), getString(R.string.finished_closing_string), Toast.LENGTH_SHORT).show();
                                            else
                                                notificationManager.notify(123, closedBlindsBuilder.build());
                                        }
                                        break;

                                }
                                currentLevelProgressBar.setProgress(currentLevel);
                                level = blindsState.getLevel();
                                levelTextView.setText(level + "%");
                                levelSeekBar.setMax(100);
                                levelSeekBar.setProgress(level);
                            } else {
                                ErrorHandler.handleError(response, requireView(), getString(R.string.error_1_string));
                            }
                        } else {
                            ErrorHandler.handleError(response, requireView(), getString(R.string.error_1_string));
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<Result<BlindsState>> call, @NonNull Throwable t) {
                        ErrorHandler.handleUnexpectedError(t, requireView(), BlindsControllerFragment.this);
                    }
                });
                try {
                    Thread.sleep(2000);
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }
        }).start();
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

        api = ApiClient.getInstance();

        updateState();

        closedBlindsBuilder = new NotificationCompat.Builder(getContext(), "123")
                .setSmallIcon(R.drawable.blinds_icon_foreground)
                .setContentTitle(getString(R.string.blinds_closed_title_string))
                .setContentText(getString(R.string.blinds_closed_text_string))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        openedBlindsBuilder = new NotificationCompat.Builder(getContext(), "123")
                .setSmallIcon(R.drawable.blinds_icon_foreground)
                .setContentTitle(getString(R.string.blinds_opened_title_string))
                .setContentText(getString(R.string.blinds_opened_text_string))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        notificationManager = NotificationManagerCompat.from(getContext());

        ProcessLifecycleOwner.get().getLifecycle().addObserver(this);
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
            Toast.makeText(getContext(), getString(R.string.already_fully_open_string), Toast.LENGTH_SHORT).show();
            return;
        }

        api.openBlinds(deviceId, new Callback<Result<Boolean>>() {
            @Override
            public void onResponse(@NonNull Call<Result<Boolean>> call, @NonNull Response<Result<Boolean>> response) {
                if (response.isSuccessful()) {
                    Result<Boolean> result = response.body();
                    if (result != null) {
                        Toast.makeText(getContext(), getString(R.string.opening_string), Toast.LENGTH_SHORT).show();
                        closeButton.setEnabled(true);
                        openButton.setEnabled(false);
                        statusTextView.setText(getString(R.string.opening_string));
                        statusTextView.setVisibility(View.VISIBLE);
                        loadingProgressBar.setVisibility(View.VISIBLE);
                        levelSeekBar.setEnabled(false);
                        shouldShowFinished = true;
                    } else {
                        ErrorHandler.handleError(response, requireView(), getString(R.string.error_1_string));
                    }
                } else {
                    ErrorHandler.handleError(response, requireView(), getString(R.string.error_1_string));
                }
            }

            @Override
            public void onFailure(@NonNull Call<Result<Boolean>> call, @NonNull Throwable t) {
                ErrorHandler.handleUnexpectedError(t, requireView(), BlindsControllerFragment.this);
            }
        });
    }

    private void closeBlinds(View view) {
        if (currentLevel == level) {
            Toast.makeText(getContext(), getString(R.string.already_fully_closed_string), Toast.LENGTH_SHORT).show();
            return;
        }

        api.closeBlinds(deviceId, new Callback<Result<Boolean>>() {
            @Override
            public void onResponse(@NonNull Call<Result<Boolean>> call, @NonNull Response<Result<Boolean>> response) {
                if (response.isSuccessful()) {
                    Result<Boolean> result = response.body();
                    if (result != null) {
                        Toast.makeText(getContext(), getString(R.string.closing_string), Toast.LENGTH_SHORT).show();
                        closeButton.setEnabled(false);
                        openButton.setEnabled(true);
                        statusTextView.setText(getString(R.string.closing_string));
                        statusTextView.setVisibility(View.VISIBLE);
                        loadingProgressBar.setVisibility(View.VISIBLE);
                        levelSeekBar.setEnabled(false);
                        shouldShowFinished = true;
                    } else {
                        ErrorHandler.handleError(response, requireView(), getString(R.string.error_1_string));
                    }
                } else {
                    ErrorHandler.handleError(response, requireView(), getString(R.string.error_1_string));
                }
            }

            @Override
            public void onFailure(@NonNull Call<Result<Boolean>> call, @NonNull Throwable t) {
                ErrorHandler.handleUnexpectedError(t, requireView(), BlindsControllerFragment.this);
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
                        Toast.makeText(getContext(), getString(R.string.new_max_level_set_at_string) + " " + newLevel + "%", Toast.LENGTH_SHORT).show();
                        level = newLevel;
                    } else {
                        ErrorHandler.handleError(response, requireView(), getString(R.string.error_1_string));
                    }
                } else {
                    ErrorHandler.handleError(response, requireView(), getString(R.string.error_1_string));
                }
            }

            @Override
            public void onFailure(@NonNull Call<Result<Integer>> call, @NonNull Throwable t) {
                ErrorHandler.handleUnexpectedError(t, requireView(), BlindsControllerFragment.this);
            }
        });
    }
}



