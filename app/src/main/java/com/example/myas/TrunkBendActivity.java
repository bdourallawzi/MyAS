package com.example.myas;


import android.bluetooth.BluetoothDevice;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.mbientlab.metawear.MetaWearBoard;
import com.mbientlab.metawear.Route;
import com.mbientlab.metawear.Subscriber;
import com.mbientlab.metawear.android.BtleService;
import com.mbientlab.metawear.builder.filter.Comparison;
import com.mbientlab.metawear.builder.function.Function2;
import com.mbientlab.metawear.module.Accelerometer;
import com.mbientlab.metawear.module.Debug;
import com.mbientlab.metawear.module.Led;
import com.mbientlab.metawear.module.Logging;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import bolts.Continuation;


public class TrunkBendActivity extends AppCompatActivity implements ServiceConnection {
    public final static String EXTRA_BT_DEVICE = "com.mbientlab.metawear.starter.TrunkBendActivity.EXTRA_BT_DEVICE";
    private BluetoothDevice btDevice;
    private MetaWearBoard metawear;
    private static final String LOG_TAG = "trunkbend";
    private Accelerometer accelerometer;
    private Debug debug;
    private Logging logging;
    private Led led;


    private TextView countdownText;
    private TextView repCountText;
    private TextView sideText;

    Button startMeasuringButton;
    Button pauseresumeButton;
    Button stopButton;
    Button backhomeButton;
    Button tryanotherButton;
    Button showCountButton;

    private Integer repCount = 3;
    private boolean rightside = true;
    private boolean isExercising;

    private int playlistPosition;
    Timer playNextTimer;
    private CountDownTimer timer;

    private Route trunkRightRoute;

    MediaPlayer mp;
    ArrayList<Integer> feedbackPlaylist; //2

    private ExerciseFeedback whileExercising;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_setup_trunkbend);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        initialiseVaribles();

        startMeasuringButton.setOnClickListener(v -> {
            isExercising = true;
            pauseresumeButton.setText(getString(R.string.pause));
            controlButtons(View.VISIBLE);

            play(9);

            countDownTextVisbility(View.VISIBLE);
            startMeasuringButton.setVisibility(View.GONE);
            resetCountdownValues();

            exerciseWalkThrough();
            whileExercising = new ExerciseFeedback(this);
            logging.start(false);
            accelerometer.acceleration().start();
            accelerometer.start();

        });
        pauseresumeButton.setOnClickListener(v -> {
            if (isExercising) {
                pauseresumeButton.setText(getString(R.string.resume));
                isExercising = false;
            } else {
                pauseresumeButton.setText(getString(R.string.pause));
                isExercising = true;
            }
        });

        stopButton.setOnClickListener(v -> {
            stop();
            mp.stop();
            playAudio(3, 0);
            timer.cancel();

            showCountButton.setVisibility(View.VISIBLE);
            controlButtons(View.GONE);
            startMeasuringButton.setText(getString(R.string.retry_exercise));
            countDownTextVisbility(View.GONE);
            rightside = true;
            resetCountdownValues();
            startMeasuringButton.setVisibility(View.VISIBLE);

        });

        backhomeButton.setOnClickListener(v -> {
            cleanup();
            metawear.disconnectAsync();
            startActivity(new Intent(TrunkBendActivity.this, MainActivity.class));
        });

        tryanotherButton.setOnClickListener(v -> {
            cleanup();
            metawear.disconnectAsync();
            Intent sensorIntent = new Intent(TrunkBendActivity.this, MetaWearMainActivity.class);
            sensorIntent.putExtra("workoutfragment", "true");
            startActivity(sensorIntent);
        });


        showCountButton.setOnClickListener(v -> {
            Intent popupIntent = new Intent(TrunkBendActivity.this, Pop.class);
            popupIntent.putExtra("name", "TRUNK BEND");
            popupIntent.putExtra("totalcount", whileExercising.getTotal());
            popupIntent.putExtra("wrongcount", whileExercising.getWrongTotal(0));

            startActivity(popupIntent);

        });

        btDevice = getIntent().getParcelableExtra(EXTRA_BT_DEVICE);
        getApplicationContext().bindService(new Intent(this, BtleService.class), this, BIND_AUTO_CREATE);

    }

    public void exerciseWalkThrough() {
        if (repCount > 0) timer = new CountDownTimer(15000, 1000) {

            @Override
            public void onTick(long millisUntilFinished) {
                countdownText.setText(String.format("%d", millisUntilFinished / 1000));
            }

            @Override
            public void onFinish() {
                try {
                    if (repCount > 1) {
                        repCount--;
                        repCountText.setText("" + repCount);
                        playlistRep();
                        timer.start();
                    } else {
                        if (rightside) {
                            resetValuesforLeft();
                        } else {
                            stop();
                            mp.stop();
                            controlButtons(View.GONE);
                            if(mp.isPlaying()) mp.stop();
                            play(4);
                            startMeasuringButton.setText(R.string.retry_exercise);
                            sideText.setText(getString(R.string.goodjob));
                            showCountButton.setVisibility(View.VISIBLE);
                            countDownTextVisbility(View.GONE);
                            rightside = true;
                            startMeasuringButton.setVisibility(View.VISIBLE);
                        }
                    }
                } catch (Exception e) {
                    Log.e("Error", "Error: " + e.toString());
                }
            }
        }.start();
    }

    public void resetValuesforLeft() {
        mp.stop();
        play(10);

        rightside = false;
        trunkRightRoute.remove();
        processingTrunkBendConfigLeft();
        resetCountdownValues();

        new CountDownTimer(5000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                sideText.setText(getString(R.string.startleftin) + millisUntilFinished / 1000);
            }

            @Override
            public void onFinish() {
                sideText.setText(getString(R.string.leftbendnow));
                timer.start();
            }
        }.start();
    }

    public void initialiseVaribles() {
        countdownText = findViewById(R.id.time_countdown);
        repCountText = findViewById(R.id.rep_countdown);
        sideText = findViewById(R.id.textRight);
        startMeasuringButton = findViewById(R.id.start_trunk_button);
        pauseresumeButton = findViewById(R.id.pauseplay_button);
        stopButton = findViewById(R.id.stop_button);
        backhomeButton = findViewById(R.id.done_button);
        tryanotherButton = findViewById(R.id.tryanother_button);
        showCountButton = findViewById(R.id.count_popup_button);

        playNextTimer = new Timer();
        setPlaylists();
    }

    public void controlButtons(int visibility) {
        // first pair
        //gone 8 visibile 0
        if (visibility == View.VISIBLE) {
            pauseresumeButton.setVisibility(visibility);
            stopButton.setVisibility(visibility);
            backhomeButton.setVisibility(View.GONE);
            tryanotherButton.setVisibility(View.GONE);
        } else if (visibility == View.GONE) {
            pauseresumeButton.setVisibility(visibility);
            stopButton.setVisibility(visibility);
            backhomeButton.setVisibility(View.VISIBLE);
            tryanotherButton.setVisibility(View.VISIBLE);
        }
    }


    public void cleanup() {
        debug.resetAsync();
        if (mp != null) {
            if (mp.isPlaying())
                mp.stop();
            timer.cancel();
        }

        stop();
    }

    public void stop() {
        if (accelerometer != null) {
            accelerometer.stop();
            accelerometer.acceleration().stop();
        }
        if (logging != null) {
            logging.stop();
            logging.downloadAsync().continueWith(task -> {
                Log.i(LOG_TAG, "Log download complete");
                return null;
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_device_setup, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_disconnect:
                metawear.disconnectAsync();
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        metawear.disconnectAsync();
        super.onBackPressed();
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        metawear = ((BtleService.LocalBinder) service).getMetaWearBoard(btDevice);
        led = metawear.getModule(Led.class);

        // NOTE: to interact with the underlying MetaWear modules, retrieve reference to the
        // desired interface getModule
        accelerometer = metawear.getModule(Accelerometer.class);
        accelerometer.configure()
                .odr(50f)
                .commit();
        processingTrunkBendConfigRight();

    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        led.stop(true);
    }

    public void processingTrunkBendConfigRight() {
        accelerometer.acceleration().addRouteAsync(source -> source.multicast()
                .to().count().stream((Subscriber) (data, env) -> {
                    whileExercising.streamCountTotal(data.value(Integer.class));

                })
                .to().split().index(2).delay((byte) 1).map(Function2.SUBTRACT, accelerometer.acceleration().yAxisName())
                .filter(Comparison.LT, -0.02).count().stream((Subscriber) (data, env) -> {
                    //tilt forward
                    wrongmoveLED();
                    playAudio(0, 7);
                    whileExercising.tiltForwardFeedback(data.value(Integer.class));

                })
                .to().split().index(0).delay((byte) 1).map(Function2.SUBTRACT, accelerometer.acceleration().zAxisName())
                .filter(Comparison.LT, 0).count().stream((Subscriber) (data, env) -> {
                    //tilt back
                    wrongmoveLED();
                    playAudio(1, 8);
                    whileExercising.tiltBackwardFeedback(data.value(Integer.class));
                }).end())
                .continueWith((Continuation<Route, Void>) task -> {
                    trunkRightRoute = task.getResult();
                    if (task.isFaulted()) {
                        Log.e(LOG_TAG, metawear.isConnected() ? "Error setting up route" : "Error connecting",
                                task.getError());
                    } else {
                        Log.i(LOG_TAG, "Connected");
                        debug = metawear.getModule(Debug.class);
                        logging = metawear.getModule(Logging.class);
                    }

                    return null;
                });

    }

    public void processingTrunkBendConfigLeft() {
        accelerometer.acceleration().addRouteAsync(source ->
                source.multicast()
                        .to().count().stream((Subscriber) (data, env) -> {
                    whileExercising.streamCountTotal(data.value(Integer.class));

                })
                        .to().split().index(2).filter(Comparison.LT, 0).count()
                        .stream((Subscriber) (data, env) -> {
                            wrongmoveLED();
                            playAudio(2, 7);
                            whileExercising.wrongMovementFeedback(data.value(Integer.class));
                        }))
                .continueWith(task -> {
                    if (task.isFaulted()) {
                        Log.e(LOG_TAG, metawear.isConnected() ? "Error setting up route" : "Error connecting",
                                task.getError());
                    } else {
                        Log.i(LOG_TAG, "Connected");
                        led.editPattern(Led.Color.GREEN)
                                .riseTime((short) 0)
                                .pulseDuration((short) 1000)
                                .repeatCount((byte) 3)
                                .highTime((short) 300)
                                .highIntensity((byte) 16)
                                .lowIntensity((byte) 16)
                                .commit();
                        debug = metawear.getModule(Debug.class);
                        logging = metawear.getModule(Logging.class);
                    }

                    return null;
                });

    }

    public void wrongmoveLED() {
        led.editPattern(Led.Color.RED)
                .riseTime((short) 0)
                .pulseDuration((short) 1000)
                .repeatCount((byte) 2)
                .highTime((short) 200)
                .highIntensity((byte) 16)
                .lowIntensity((byte) 16)
                .commit();
        led.play();
    }

    public void setPlaylists() {
        feedbackPlaylist = new ArrayList<>();
        feedbackPlaylist.add(R.raw.bendforward); //0
        feedbackPlaylist.add(R.raw.bendbackward); //1
        feedbackPlaylist.add(R.raw.wrongmovement); //2
        feedbackPlaylist.add(R.raw.stopped); //3
        feedbackPlaylist.add(R.raw.done); // 4


        feedbackPlaylist.add(R.raw.benefit_trunkbend); //5
        feedbackPlaylist.add(R.raw.dizziness); //6
        feedbackPlaylist.add(R.raw.maintainneutralspine); //7
        feedbackPlaylist.add(R.raw.ensureslow); //8


        feedbackPlaylist.add(R.raw.start_trunkbend); //9
        feedbackPlaylist.add(R.raw.leftsidebreak);//10
        feedbackPlaylist.add(R.raw.rep2);//11
        feedbackPlaylist.add(R.raw.rep3right);//12
        feedbackPlaylist.add(R.raw.finalrepleft);//13


    }

    public void playAudio(int playlistInt, int next) {
        if (!mp.isPlaying()) {
            play(playlistInt);
            playNext(next);
        } else if (mp.isPlaying() && (playlistPosition > 4 && playlistPosition < 9)) {
            mp.stop();
            play(playlistInt);
        }

    }

    public void playNext(int playlistInt) {
        playNextTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                mp.reset();
                mp = MediaPlayer.create(TrunkBendActivity.this, feedbackPlaylist.get(playlistInt));
                if (!mp.isPlaying()) {
                    playlistPosition = playlistInt;
                    mp.start();
                }
            }
        }, mp.getDuration() + 100);
    }


    public void playlistRep() {
        if (mp.isPlaying()) {
            mp.stop();
        }

        if (repCount == 2 && rightside) {
            play(11);
            playNext(8); //maintainneutral //8
        } else if (repCount == 1 && rightside) {
            play(12);
            playNext(5); //dizziness//5
        } else if (repCount == 2 && !rightside) {
            play(11);
            playNext(6); //benefit//6
        } else if (repCount == 1 && !rightside) {
            play(13);
            playNext(7); //7
        }
    }

    public void play(int position) {
        playlistPosition = position;

        if (position != 9) {
            mp.reset();
        }
        mp = MediaPlayer.create(this, feedbackPlaylist.get(position));
        mp.start();
    }

    public void countDownTextVisbility(int visibility) {
        countdownText.setVisibility(visibility);
        repCountText.setVisibility(visibility);
        findViewById(R.id.repstogo).setVisibility(visibility);
        findViewById(R.id.secstogo).setVisibility(visibility);
    }

    public void resetCountdownValues() {
        repCount = 3;
        repCountText.setText("" + repCount);
        countdownText.setText("15");
        if (rightside) {
            sideText.setText(getString(R.string.start_bend));
        }
    }


}
