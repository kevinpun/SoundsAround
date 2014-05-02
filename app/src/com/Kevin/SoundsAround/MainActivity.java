package com.Kevin.SoundsAround;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends FragmentActivity implements View.OnClickListener {
    // Audio variables
    private AudioManager audioManager;
    private SeekBar volumeSeekBar;

    // Environment media player
    private MediaPlayer environmentPlayer;
    private int[] environment;
    private int currentEnvironment;
    private boolean playSong;

    // Sound media player
    private MediaPlayer soundPlayer;
    private int[][] sounds;
    private int currentSound;

    // Image variables
    private ImageView imageView;
    private int[] images;

    // Button variables
    private Button prev;
    private Button next;
    private Button play;

    // Page Adapter
    ViewPager pager;
    ImageAdapter pageAdapter;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        // @TODO: Must set the images/sounds for the app to run
        // Environment images
        images = new int[] {};

        // Environment sounds
        environment = new int[] {};
        currentEnvironment = 0;

        // Sounds in the environment
        sounds = new int[][] {
                {},
                {},
                {}
        };

        List<Fragment> fragments = getFragments();
        pageAdapter = new ImageAdapter(getSupportFragmentManager(), fragments);
        pager = (ViewPager)findViewById(R.id.viewpager);
        pager.setAdapter(pageAdapter);
        pager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i2) {}

            @Override
            public void onPageSelected(int i) {
                playCurrentEnvironmentSound(i);
            }

            @Override
            public void onPageScrollStateChanged(int i) {}
        });

        setViews();
        initializeVolumeSeekBar();
        initializeSounds();
        generateRandomizedSounds();
    }

    /**
     * Creates the list of fragments.
     */
    private List<Fragment> getFragments() {
        List<Fragment> fragmentList = new ArrayList<Fragment>();
        for (int i : images) {
            fragmentList.add(ImageFragment.newInstance(i));
        }
        return fragmentList;
    }

    /**
     *  ImageAdapter class
     */
    private class ImageAdapter extends FragmentPagerAdapter {
        private List<Fragment> fragments;

        public ImageAdapter(FragmentManager fragmentManager, List<Fragment> fragments) {
            super(fragmentManager);
            this.fragments = fragments;
        }

        @Override
        public Fragment getItem(int i) {
            return fragments.get(i);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }
    }

    /**
     *  Set the views
     */
    private void setViews() {
//        imageView = (ImageView) findViewById(R.id.imageView);
        prev = (Button) findViewById(R.id.btnPrev);
        next = (Button) findViewById(R.id.btnNext);
        play = (Button) findViewById(R.id.btnPlay);
        prev.setOnClickListener(this);
        next.setOnClickListener(this);
        play.setOnClickListener(this);
    }

    /**
     *  Initializes the sound settings
     */
    private void initializeVolumeSeekBar() {
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        try
        {
            volumeSeekBar = (SeekBar)findViewById(R.id.seekBar);
            audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            volumeSeekBar.setMax(audioManager
                    .getStreamMaxVolume(AudioManager.STREAM_MUSIC));
            volumeSeekBar.setProgress(audioManager
                    .getStreamVolume(AudioManager.STREAM_MUSIC));

            volumeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onStopTrackingTouch(SeekBar arg0) {}

                @Override
                public void onStartTrackingTouch(SeekBar arg0) {}

                @Override
                public void onProgressChanged(SeekBar arg0, int progress, boolean arg2) {
                    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     *  Initializes the sound
     */
    private void initializeSounds() {
        playSong = false;
        currentSound = 0;

        environmentPlayer = MediaPlayer.create(this, environment[currentEnvironment]);
        environmentPlayer.stop();
    }

    /**
     *  Generates the random sounds in the environment
     */
    private void generateRandomizedSounds() {
        // Randomized sound every x seconds
        final Handler handler = new Handler();
        final Runnable r = new Runnable() {
            public void run() {
                if (environmentPlayer != null && environmentPlayer.isPlaying()) {
                    soundPlayer = MediaPlayer.create(getApplicationContext(), sounds[currentEnvironment][currentSound]);
                    soundPlayer.start();

                    if (currentSound + 1 > sounds[currentEnvironment].length - 1)
                        currentSound = 0;
                    else
                        currentSound++;
                }
                handler.postDelayed(this, 10000);
            }
        };
        handler.postDelayed(r, 10000);
    }

    /**
     *  Called when the app is paused
     */
    @Override
    protected void onPause() {
        super.onPause();
        if (environmentPlayer != null)
            environmentPlayer.pause();
        if (soundPlayer != null)
            soundPlayer.pause();
    }

    /**
     *  Called when the app is resumed
     */
    @Override
    protected void onResume() {
        super.onResume();
        if (environmentPlayer != null)
            environmentPlayer.start();
        if (soundPlayer != null)
            soundPlayer.pause();
    }

    /**
     *  Called when the activity is first created.
     *	@param menu menu of action bar.
     *	@return created status.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /**
     *  Called when the activity is first created.
     *	@param item the menu for the action bar.
     *	@return created status.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Take appropriate action for each action item click
        switch (item.getItemId()) {
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     *  Called when the next or previous button is pressed
     *	@param view the view that was clicked
     */
    @Override
    public void onClick(View view) {
        if (view == prev) {
            changeEnvironmentSound(-1);
        }
        else if (view == next) {
            changeEnvironmentSound(1);
        }
        else if (view == play) {
            // Change play
            if (playSong) {
                play.setText("Play");
                environmentPlayer.pause();
                if (soundPlayer != null)
                    soundPlayer.pause();
            }
            else {
                play.setText("Pause");
                environmentPlayer = MediaPlayer.create(this, environment[currentEnvironment]);
                environmentPlayer.setLooping(true);
                environmentPlayer.start();
            }
            playSong = !playSong;
        }
    }

    /**
     *  Called when the next or previous button is clicked
     */
    private void switchEnvironmentSound() {
        if (soundPlayer != null)
            soundPlayer.stop();

        if (playSong) {
            environmentPlayer.stop();
            environmentPlayer.release();
            environmentPlayer = MediaPlayer.create(this, environment[currentEnvironment]);
            environmentPlayer.setLooping(true);
            environmentPlayer.start();
        }
    }

    /**
     * Change the environment sound
     */
    private void changeEnvironmentSound(int offset) {
        if (offset == -1) {
            if (currentEnvironment == 0) {
                currentEnvironment = environment.length - 1;
            } else {
                currentEnvironment -= 1;
            }
        }
        else if (offset == 1) {
            if (currentEnvironment == environment.length - 1) {
                currentEnvironment = 0;
            } else {
                currentEnvironment += 1;
            }
        }
        pager.setCurrentItem(currentEnvironment, true);
        switchEnvironmentSound();
    }

    /**
     * Play the current environment sound
     */
    private void playCurrentEnvironmentSound(int i) {
        currentEnvironment = i;
        switchEnvironmentSound();
    }
}
