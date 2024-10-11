package com.example.player;
import androidx.appcompat.app.AppCompatActivity;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class MusicPlayerActivity extends AppCompatActivity {

    TextView titleTv, currentTimeTv, totalTimeTv;
    SeekBar seekBar;
    ImageView pausePlay, nextBtn, previousBtn, musicIcon;
    ArrayList<AudioModel> songsList;
    AudioModel currentSong;
    MediaPlayer mediaPlayer = MyMediaPlayer.getinstance();
    int x = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_player);

        // Initialize UI components
        initializeUI();

        // Get songs list from the intent
        songsList = (ArrayList<AudioModel>) getIntent().getSerializableExtra("LIST");

        // Set up the player with the current song
        setResourcesWithMusic();

        // Start updating the seek bar and time display
        updateSeekBar();
    }

    // Initialize UI components
    void initializeUI() {
        titleTv = findViewById(R.id.song_title);
        currentTimeTv = findViewById(R.id.current_time);
        totalTimeTv = findViewById(R.id.total_time);
        seekBar = findViewById(R.id.seek_bar);
        pausePlay = findViewById(R.id.pause_play);
        nextBtn = findViewById(R.id.next);
        previousBtn = findViewById(R.id.previous);
        musicIcon = findViewById(R.id.music_icon_big);

        // Enable marquee scrolling for the song title
        titleTv.setSelected(true);

        // Set event listeners
        setEventListeners();
    }

    // Set up event listeners for buttons
    void setEventListeners() {
        pausePlay.setOnClickListener(v -> pausePlay());
        nextBtn.setOnClickListener(v -> playNextSong());
        previousBtn.setOnClickListener(v -> playPreviousSong());

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (mediaPlayer != null && fromUser) {
                    mediaPlayer.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }

    // Set the resources for the current song and update UI
    void setResourcesWithMusic() {
        currentSong = songsList.get(MyMediaPlayer.currentIndex);

        titleTv.setText(currentSong.getTitle());
        totalTimeTv.setText(convertToMMSS(currentSong.getDuration()));

        playMusic();
    }

    // Play the current song
    void playMusic() {
        mediaPlayer.reset();
        try {
            mediaPlayer.setDataSource(currentSong.getPath());
            mediaPlayer.prepare();
            mediaPlayer.start();
            seekBar.setMax(mediaPlayer.getDuration());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Pause or play the music
    void pausePlay() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            pausePlay.setImageResource(R.drawable.baseline_play_circle_outline_24);
        } else {
            mediaPlayer.start();
            pausePlay.setImageResource(R.drawable.baseline_pause_circle_outline_24);
        }
    }

    // Play the next song in the list
    void playNextSong() {
        if (MyMediaPlayer.currentIndex < songsList.size() - 1) {
            MyMediaPlayer.currentIndex++;
            setResourcesWithMusic();
        }
    }

    // Play the previous song in the list
    void playPreviousSong() {
        if (MyMediaPlayer.currentIndex > 0) {
            MyMediaPlayer.currentIndex--;
            setResourcesWithMusic();
        }
    }

    // Update the seek bar and current time as the song plays
    void updateSeekBar() {
        MusicPlayerActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mediaPlayer != null) {
                    seekBar.setProgress(mediaPlayer.getCurrentPosition());
                    currentTimeTv.setText(convertToMMSS(String.valueOf(mediaPlayer.getCurrentPosition())));

                    // Rotate the music icon if playing
                    if (mediaPlayer.isPlaying()) {
                        musicIcon.setRotation(x++);
                        pausePlay.setImageResource(R.drawable.baseline_pause_circle_outline_24);
                    } else {
                        musicIcon.setRotation(0);
                        pausePlay.setImageResource(R.drawable.baseline_play_circle_outline_24);
                    }
                }
                new Handler().postDelayed(this, 100);
            }
        });
    }

    // Convert milliseconds to MM:SS format
    public static String convertToMMSS(String duration) {
        Long millis = Long.parseLong(duration);
        return String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(millis) % TimeUnit.HOURS.toMinutes(1),
                TimeUnit.MILLISECONDS.toSeconds(millis) % TimeUnit.MINUTES.toSeconds(1)
        );
    }
}
