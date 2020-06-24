package com.example.ultrahome.ui.devices.controllers;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.ultrahome.R;
import com.example.ultrahome.apiConnection.ApiClient;
import com.example.ultrahome.apiConnection.ErrorHandler;
import com.example.ultrahome.apiConnection.entities.Result;
import com.example.ultrahome.apiConnection.entities.deviceEntities.speaker.SpeakerState;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SpeakerControllerFragment extends Fragment {

    private String [] genres = {"classical",
            "country",
            "dance",
            "latina",
            "pop",
            "rock"};
    private int currentGenreIndex = 0;

    private String deviceId;

    private ImageButton prevButton, nextButton, pauseButton, playButton, stopButton, volDownButton, volUpButton;
    private Button nextGenreButton;
    private ProgressBar progressProgressBar;
    private TextView genreTextView, titleTextView, durationTextView, progressTextView, artistTextView, albumTextView;
    private SeekBar volumeSeekBar;

    private String status;

    private boolean runThreads = true;

    private int volume, progressInt, durationInt;

    private ApiClient api;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_speaker_controller, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        readBundle(getArguments());

        init(getView());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        runThreads = false;
    }

    private void readBundle(Bundle bundle) {
        if (bundle != null) {
            deviceId = bundle.getString("deviceId");
        }
    }

    private void init(@NonNull View view) {
        prevButton = view.findViewById(R.id.prev_button);
        nextButton = view.findViewById(R.id.next_button);
        pauseButton = view.findViewById(R.id.pause_button);
        playButton = view.findViewById(R.id.play_button);
        stopButton = view.findViewById(R.id.stop_button);
        volDownButton = view.findViewById(R.id.vol_down_button);
        volUpButton = view.findViewById(R.id.vol_up_button);
        volumeSeekBar = view.findViewById(R.id.volume_seekBar);
        nextGenreButton = view.findViewById(R.id.next_genre_button);

        titleTextView = view.findViewById(R.id.title_textView);
        artistTextView = view.findViewById(R.id.artist_textView);
        albumTextView = view.findViewById(R.id.album_textView);
        durationTextView = view.findViewById(R.id.duration_textView);
        progressTextView = view.findViewById(R.id.progress_textView);
        genreTextView = view.findViewById(R.id.genre_textView);

        progressProgressBar = view.findViewById(R.id.progress_progressBar);



        prevButton.setOnClickListener(this::prevSong);
        nextButton.setOnClickListener(this::nextSong);
        pauseButton.setOnClickListener(this::pause);
        playButton.setOnClickListener(this::play);
        stopButton.setOnClickListener(this::stop);
        volDownButton.setOnClickListener(this::volDown);
        volUpButton.setOnClickListener(this::volUp);
        nextGenreButton.setOnClickListener(this::nextGenre);

        volumeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                setVol(volumeSeekBar.getProgress());
            }
        });

        api = ApiClient.getInstance();

        updateState();
    }

    @NonNull
    public static SpeakerControllerFragment newInstance(String deviceId) {
        Bundle bundle = new Bundle();
        bundle.putString("deviceId", deviceId);

        SpeakerControllerFragment fragment = new SpeakerControllerFragment();
        fragment.setArguments(bundle);

        return fragment;
    }

    private void play(View view) {
        if(status.equals("stopped"))
            api.play(deviceId, new Callback<Result<Boolean>>() {
                @Override
                public void onResponse(@NonNull Call<Result<Boolean>> call, @NonNull Response<Result<Boolean>> response) {
                    if (response.isSuccessful()) {
                        Result<Boolean> result = response.body();
                        if (result != null) {
                            updateState();

                        } else {
                            ErrorHandler.handleError(response, requireView(), getString(R.string.error_1_string));
                        }
                    } else {
                        ErrorHandler.handleError(response, requireView(), getString(R.string.error_1_string));
                    }
                }

                @Override
                public void onFailure(@NonNull Call<Result<Boolean>> call, @NonNull Throwable t) {
                    ErrorHandler.handleUnexpectedError(t, requireView(), SpeakerControllerFragment.this);
                }
            });
        else
            api.resume(deviceId, new Callback<Result<Boolean>>() {
                @Override
                public void onResponse(@NonNull Call<Result<Boolean>> call, @NonNull Response<Result<Boolean>> response) {
                    if (response.isSuccessful()) {
                        Result<Boolean> result = response.body();
                        if (result != null) {
                            updateState();

                        } else {
                            ErrorHandler.handleError(response, requireView(), getString(R.string.error_1_string));
                        }
                    } else {
                        ErrorHandler.handleError(response, requireView(), getString(R.string.error_1_string));
                    }
                }

                @Override
                public void onFailure(@NonNull Call<Result<Boolean>> call, @NonNull Throwable t) {
                    ErrorHandler.handleUnexpectedError(t, requireView(), SpeakerControllerFragment.this);
                }
            });

    }

    private void pause(View view) {
        api.pause(deviceId, new Callback<Result<Boolean>>() {
            @Override
            public void onResponse(@NonNull Call<Result<Boolean>> call, @NonNull Response<Result<Boolean>> response) {
                if (response.isSuccessful()) {
                    Result<Boolean> result = response.body();
                    if (result != null) {
                        updateState();

                    } else {
                        ErrorHandler.handleError(response, requireView(), getString(R.string.error_1_string));
                    }
                } else {
                    ErrorHandler.handleError(response, requireView(), getString(R.string.error_1_string));
                }
            }

            @Override
            public void onFailure(@NonNull Call<Result<Boolean>> call, @NonNull Throwable t) {
                ErrorHandler.handleUnexpectedError(t, requireView(), SpeakerControllerFragment.this);
            }
        });
    }

    private void stop(View view) {
        api.stop(deviceId, new Callback<Result<Boolean>>() {
            @Override
            public void onResponse(@NonNull Call<Result<Boolean>> call, @NonNull Response<Result<Boolean>> response) {
                if (response.isSuccessful()) {
                    Result<Boolean> result = response.body();
                    if (result != null) {
                        updateState();


                    } else {
                        ErrorHandler.handleError(response, requireView(), getString(R.string.error_1_string));
                    }
                } else {
                    ErrorHandler.handleError(response, requireView(), getString(R.string.error_1_string));
                }
            }

            @Override
            public void onFailure(@NonNull Call<Result<Boolean>> call, @NonNull Throwable t) {
                ErrorHandler.handleUnexpectedError(t, requireView(), SpeakerControllerFragment.this);
            }
        });
    }

    private void prevSong(View view) {
        api.previousSong(deviceId, new Callback<Result<Boolean>>() {
            @Override
            public void onResponse(@NonNull Call<Result<Boolean>> call, @NonNull Response<Result<Boolean>> response) {
                if (response.isSuccessful()) {
                    Result<Boolean> result = response.body();
                    if (result != null) {
                        updateState();

                    } else {
                        ErrorHandler.handleError(response, requireView(), getString(R.string.error_1_string));
                    }
                } else {
                    ErrorHandler.handleError(response, requireView(), getString(R.string.error_1_string));
                }
            }

            @Override
            public void onFailure(@NonNull Call<Result<Boolean>> call, @NonNull Throwable t) {
                ErrorHandler.handleUnexpectedError(t, requireView(), SpeakerControllerFragment.this);
            }
        });
    }

    private void nextSong(View view) {
        api.nextSong(deviceId, new Callback<Result<Boolean>>() {
            @Override
            public void onResponse(@NonNull Call<Result<Boolean>> call, @NonNull Response<Result<Boolean>> response) {
                if (response.isSuccessful()) {
                    Result<Boolean> result = response.body();
                    if (result != null) {
                        updateState();

                    } else {
                        ErrorHandler.handleError(response, requireView(), getString(R.string.error_1_string));
                    }
                } else {
                    ErrorHandler.handleError(response, requireView(), getString(R.string.error_1_string));
                }
            }

            @Override
            public void onFailure(@NonNull Call<Result<Boolean>> call, @NonNull Throwable t) {
                ErrorHandler.handleUnexpectedError(t, requireView(), SpeakerControllerFragment.this);
            }
        });
    }

    private void volDown(View view) {
        if(volume == 0)
            return;

        setVol(volume - 1);
    }

    private void volUp(View view) {
        if(volume == 10)
            return;

        setVol(volume + 1);
    }

    private void setVol(int newVol) {
        if(newVol == volume)
            return;

        volume = newVol;

        volUpButton.setEnabled(true);
        volDownButton.setEnabled(true);

        if(volume == 10)
            volUpButton.setEnabled(false);

        if(volume == 0)
            volDownButton.setEnabled(false);

        volumeSeekBar.setProgress(volume);

        api.setSpeakerVolume(deviceId, volume , new Callback<Result<Integer>>() {
            @Override
            public void onResponse(@NonNull Call<Result<Integer>> call, @NonNull Response<Result<Integer>> response) {
                if (response.isSuccessful()) {
                    Result<Integer> result = response.body();
                    if (result != null) {
                        //
                    } else {
                        ErrorHandler.handleError(response, requireView(), getString(R.string.error_1_string));
                    }
                } else {
                    ErrorHandler.handleError(response, requireView(), getString(R.string.error_1_string));
                }
            }

            @Override
            public void onFailure(@NonNull Call<Result<Integer>> call, @NonNull Throwable t) {
                ErrorHandler.handleUnexpectedError(t, requireView(), SpeakerControllerFragment.this);
            }
        });
    }

    private int parseTime(String time) {
        int minutes, seconds;

        minutes = Integer.parseInt(time.substring(0, 1));
        seconds = Integer.parseInt(time.substring(2));
        seconds = seconds + minutes * 60;

        return seconds;
    }



    private void updateState() {
        new Thread(() -> {
            while(runThreads) {
                api.getSpeakerState(deviceId, new Callback<Result<SpeakerState>>() {
                    @Override
                    public void onResponse(@NonNull Call<Result<SpeakerState>> call, @NonNull Response<Result<SpeakerState>> response) {
                        if (response.isSuccessful()) {
                            Result<SpeakerState> result = response.body();
                            if (result != null && runThreads) {
                                SpeakerState speakerState = result.getResult();
                                status = speakerState.getStatus();
                                volume = speakerState.getVolume();
                                genreTextView.setText(speakerState.getGenre());
                                getCurrentGenreIndex();
                                volumeSeekBar.setMax(10);
                                volumeSeekBar.setProgress(volume);

                                switch (status) {
                                    case "stopped":
                                        prevButton.setVisibility(View.INVISIBLE);
                                        nextButton.setVisibility(View.INVISIBLE);
                                        playButton.setVisibility(View.VISIBLE);
                                        pauseButton.setVisibility(View.INVISIBLE);
                                        stopButton.setVisibility(View.INVISIBLE);
                                        nextGenreButton.setVisibility(View.VISIBLE);
                                        progressProgressBar.setProgress(0);
                                        titleTextView.setText(" - ");
                                        artistTextView.setText(" - ");
                                        albumTextView.setText(" - ");
                                        durationTextView.setText("-:--");
                                        progressTextView.setText("-:--");
                                        break;
                                    case "playing":
                                        playButton.setVisibility(View.INVISIBLE);
                                        pauseButton.setVisibility(View.VISIBLE);
                                        prevButton.setVisibility(View.VISIBLE);
                                        nextButton.setVisibility(View.VISIBLE);
                                        break;
                                    case "paused":
                                        playButton.setVisibility(View.VISIBLE);
                                        pauseButton.setVisibility(View.INVISIBLE);
                                        prevButton.setVisibility(View.INVISIBLE);
                                        nextButton.setVisibility(View.INVISIBLE);
                                        break;
                                }
                                if(status.equals("playing") || status.equals("paused")) {
                                    progressInt = parseTime(speakerState.getProgress());
                                    durationInt = parseTime(speakerState.getDuration());
                                    progressProgressBar.setMax(durationInt);
                                    progressProgressBar.setProgress(progressInt);
                                    stopButton.setVisibility(View.VISIBLE);
                                    nextGenreButton.setVisibility(View.INVISIBLE);
                                    titleTextView.setText(speakerState.getTitle());
                                    artistTextView.setText(speakerState.getArtist());
                                    albumTextView.setText(speakerState.getAlbum());
                                    durationTextView.setText(speakerState.getDuration());
                                    progressTextView.setText(speakerState.getProgress());
                                }

                            } else {
                                ErrorHandler.handleError(response, requireView(), getString(R.string.error_1_string));
                            }
                        } else {
                            ErrorHandler.handleError(response, requireView(), getString(R.string.error_1_string));
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<Result<SpeakerState>> call, @NonNull Throwable t) {
                        ErrorHandler.handleUnexpectedError(t, requireView(), SpeakerControllerFragment.this);
                    }
                });
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void getCurrentGenreIndex() {
        for(int i = 0; i < 6; i++)
            if(genres[i].equals(genreTextView.getText()))
                currentGenreIndex = i;
    }

    private void nextGenre(View view) {
        currentGenreIndex++;
        if(currentGenreIndex == 6)
            currentGenreIndex = 0;
        api.setSpeakerGenre(deviceId, genres[currentGenreIndex], new Callback<Result<String>>() {
            @Override
            public void onResponse(@NonNull Call<Result<String>> call, @NonNull Response<Result<String>> response) {
                if (response.isSuccessful()) {
                    Result<String> result = response.body();
                    if (result != null) {
                        genreTextView.setText(genres[currentGenreIndex]);
                    } else {
                        ErrorHandler.handleError(response, requireView(), getString(R.string.error_1_string));
                    }
                } else {
                    ErrorHandler.handleError(response, requireView(), getString(R.string.error_1_string));
                }
            }

            @Override
            public void onFailure(@NonNull Call<Result<String>> call, @NonNull Throwable t) {
                ErrorHandler.handleUnexpectedError(t, requireView(), SpeakerControllerFragment.this);
            }
        });
    }
}
