/**
 * Example of using libmuse library on android.
 * Interaxon, Inc. 2016
 */

package com.choosemuse.example.libmuse;

import java.lang.ref.WeakReference;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import com.choosemuse.libmuse.ConnectionState;
import com.choosemuse.libmuse.Eeg;
import com.choosemuse.libmuse.LibmuseVersion;
import com.choosemuse.libmuse.Muse;
import com.choosemuse.libmuse.MuseArtifactPacket;
import com.choosemuse.libmuse.MuseConnectionListener;
import com.choosemuse.libmuse.MuseConnectionPacket;
import com.choosemuse.libmuse.MuseDataListener;
import com.choosemuse.libmuse.MuseDataPacket;
import com.choosemuse.libmuse.MuseDataPacketType;
import com.choosemuse.libmuse.MuseListener;
import com.choosemuse.libmuse.MuseManagerAndroid;
import com.choosemuse.libmuse.MuseVersion;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.bluetooth.BluetoothAdapter;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;


public class ConnectMuseActivity extends Activity implements OnClickListener {
    private final String TAG = "TestLibMuseAndroid";
    private MuseManagerAndroid manager;
    private Muse muse;
    private ConnectionListener connectionListener;
    private DataListener dataListener;
    private final double[] eegBuffer = new double[6];
    private boolean eegStale;
    private final double[] alphaBuffer = new double[6];
    private boolean alphaStale;
    private long timestamp;
    private final Handler handler = new Handler();
    private ArrayAdapter<String> spinnerAdapter;
    private boolean dataTransmission = true;
    private String reference;
    private final AtomicReference<Handler> fileHandler = new AtomicReference<>();
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference eegDataRef;
    private TextView alpha1, alpha2, alpha3, alpha4, eegTxt, alphaTxt, eeg1, eeg2, eeg3, eeg4;
    private ImageView lamaL, lamaR;
    private String userName;
    private int gap;

    private Button wynikiBtn, youtubeBtn;
    private int i = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        database.getInstance().setPersistenceEnabled(true);

        manager = MuseManagerAndroid.getInstance();
        manager.setContext(this);

        Log.i(TAG, "LibMuse version=" + LibmuseVersion.instance().getString());
        lamaL = (ImageView) findViewById(R.id.lamaL);
        lamaR = (ImageView) findViewById(R.id.lamaR);


        WeakReference<ConnectMuseActivity> weakActivity =
                new WeakReference<ConnectMuseActivity>(this);

        connectionListener = new ConnectionListener(weakActivity);

        dataListener = new DataListener(weakActivity);

        manager.setMuseListener(new MuseL(weakActivity));

        userName = getIntent().getExtras().getString("NAME");

        ensurePermissions();

        initUI();

        fileThread.start();

        handler.post(tickUi);

        alpha1 = (TextView) findViewById(R.id.alpha1);
        alpha2 = (TextView) findViewById(R.id.alpha2);
        alpha3 = (TextView) findViewById(R.id.alpha3);
        alpha4 = (TextView) findViewById(R.id.alpha4);
        eegTxt = (TextView) findViewById(R.id.eegTxt);
        alphaTxt = (TextView) findViewById(R.id.alphaTxt);
        eeg1 = (TextView) findViewById(R.id.eeg_af7);
        eeg2 = (TextView) findViewById(R.id.eeg_af8);
        eeg3 = (TextView) findViewById(R.id.eeg_tp9);
        eeg4 = (TextView) findViewById(R.id.eeg_tp10);

        alpha1.setVisibility(findViewById(R.id.alpha1).GONE);
        alpha2.setVisibility(findViewById(R.id.alpha2).GONE);
        alpha3.setVisibility(findViewById(R.id.alpha3).GONE);
        alpha4.setVisibility(findViewById(R.id.alpha4).GONE);
        eegTxt.setVisibility(findViewById(R.id.eegTxt).GONE);
        alphaTxt.setVisibility(findViewById(R.id.alphaTxt).GONE);
        eeg1.setVisibility(findViewById(R.id.eeg_af7).GONE);
        eeg2.setVisibility(findViewById(R.id.eeg_af8).GONE);
        eeg3.setVisibility(findViewById(R.id.eeg_tp9).GONE);
        eeg4.setVisibility(findViewById(R.id.eeg_tp10).GONE);


        wynikiBtn = (Button) findViewById(R.id.wyniki);
        wynikiBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                i = i + 1;
                openMainActivity();
            }
        });

        youtubeBtn = (Button) findViewById(R.id.youtubeBtn);
        youtubeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openYoutubeActivity();
            }

        });

    }

    private void openMainActivity() {

        if (i == 1) {
            alpha1.setVisibility(findViewById(R.id.alpha1).VISIBLE);
            alpha2.setVisibility(findViewById(R.id.alpha2).VISIBLE);
            alpha3.setVisibility(findViewById(R.id.alpha3).VISIBLE);
            alpha4.setVisibility(findViewById(R.id.alpha4).VISIBLE);
            eegTxt.setVisibility(findViewById(R.id.eegTxt).VISIBLE);
            alphaTxt.setVisibility(findViewById(R.id.alphaTxt).VISIBLE);
            eeg1.setVisibility(findViewById(R.id.eeg_af7).VISIBLE);
            eeg2.setVisibility(findViewById(R.id.eeg_af8).VISIBLE);
            eeg3.setVisibility(findViewById(R.id.eeg_tp9).VISIBLE);
            eeg4.setVisibility(findViewById(R.id.eeg_tp10).VISIBLE);
        } else {
            alpha1.setVisibility(findViewById(R.id.alpha1).GONE);
            alpha2.setVisibility(findViewById(R.id.alpha2).GONE);
            alpha3.setVisibility(findViewById(R.id.alpha3).GONE);
            alpha4.setVisibility(findViewById(R.id.alpha4).GONE);
            eegTxt.setVisibility(findViewById(R.id.eegTxt).GONE);
            alphaTxt.setVisibility(findViewById(R.id.alphaTxt).GONE);
            eeg1.setVisibility(findViewById(R.id.eeg_af7).GONE);
            eeg2.setVisibility(findViewById(R.id.eeg_af8).GONE);
            eeg3.setVisibility(findViewById(R.id.eeg_tp9).GONE);
            eeg4.setVisibility(findViewById(R.id.eeg_tp10).GONE);
            i = 0;
        }

    }


    private void openYoutubeActivity() {
        Intent intent = new Intent(this, YoutubeActivity.class);
        startActivity(intent);
    }

    protected void onPause() {
        super.onPause();

        manager.stopListening();
    }

    public boolean isBluetoothEnabled() {
        return BluetoothAdapter.getDefaultAdapter().isEnabled();
    }

    @Override
    public void onClick(View v) {

        alpha1 = (TextView) findViewById(R.id.alpha1);
        alpha2 = (TextView) findViewById(R.id.alpha2);
        alpha3 = (TextView) findViewById(R.id.alpha3);
        alpha4 = (TextView) findViewById(R.id.alpha4);

        if (v.getId() == R.id.refresh) {

            manager.stopListening();
            manager.startListening();

        } else if (v.getId() == R.id.connect) {


            manager.stopListening();

            List<Muse> availableMuses = manager.getMuses();
            Spinner musesSpinner = (Spinner) findViewById(R.id.muses_spinner);


            if (availableMuses.size() < 1 || musesSpinner.getAdapter().getCount() < 1) {
                Log.w(TAG, "There is nothing to connect to");
            } else {
                gap = 0;
                muse = availableMuses.get(musesSpinner.getSelectedItemPosition());
                Date date = new Date();
                timestamp = date.getTime();
                reference = "Device/EEG_data_" + userName + "/" + timestamp;

                muse.enableDataTransmission(true);
                database.goOnline();
                muse.unregisterAllListeners();
                muse.registerConnectionListener(connectionListener);
                muse.registerDataListener(dataListener, MuseDataPacketType.EEG);
                muse.registerDataListener(dataListener, MuseDataPacketType.ALPHA_RELATIVE);

                muse.runAsynchronously();

            }

        } else if (v.getId() == R.id.disconnect) {


            if (muse != null) {
                dataTransmission = !dataTransmission;
                muse.disconnect();
            }


        } else if (v.getId() == R.id.pause) {
            if (muse != null) {
                //dataTransmission = false;
                gap = 0;
                muse.disconnect();
                muse.enableDataTransmission(false);
                database.goOffline();
            }
        }
    }


    private void ensurePermissions() {

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            DialogInterface.OnClickListener buttonListener =
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            ActivityCompat.requestPermissions(ConnectMuseActivity.this,
                                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                                    0);
                        }
                    };


            AlertDialog introDialog = new AlertDialog.Builder(this)
                    .setTitle(R.string.permission_dialog_title)
                    .setMessage(R.string.permission_dialog_description)
                    .setPositiveButton(R.string.permission_dialog_understand, buttonListener)
                    .create();
            introDialog.show();
        }
    }


    public void museListChanged() {
        final List<Muse> list = manager.getMuses();
        spinnerAdapter.clear();
        for (Muse m : list) {
            spinnerAdapter.add(m.getName() + " - " + m.getMacAddress());
        }
    }


    public void receiveMuseConnectionPacket(final MuseConnectionPacket p, final Muse muse) {

        final ConnectionState current = p.getCurrentConnectionState();

        final String status = p.getPreviousConnectionState() + " -> " + current;
        Log.i(TAG, status);


        handler.post(new Runnable() {
            @Override
            public void run() {


                final TextView statusText = (TextView) findViewById(R.id.con_status);
                statusText.setText(status);

                final MuseVersion museVersion = muse.getMuseVersion();

                if (museVersion != null) {
                    final String version = museVersion.getFirmwareType() + " - "
                            + museVersion.getFirmwareVersion() + " - "
                            + museVersion.getProtocolVersion();

                } else {

                }
            }
        });

        if (current == ConnectionState.DISCONNECTED) {
            Log.i(TAG, "Muse disconnected:" + muse.getName());
            this.muse = null;
        }
    }


    public void receiveMuseDataPacket(final MuseDataPacket p, final Muse muse) {

        final long n = p.valuesSize();
        switch (p.packetType()) {
            case EEG:
                assert (eegBuffer.length >= n);
                getEegChannelValues(eegBuffer, p);
                eegStale = true;
                break;
            case ALPHA_RELATIVE:
                assert (alphaBuffer.length >= n);
                getEegChannelValues(alphaBuffer, p);
                alphaStale = true;
                break;
            // case BATTERY:
            default:
                break;
        }
    }


    public void receiveMuseArtifactPacket(final MuseArtifactPacket p, final Muse muse) {
    }


    private void getEegChannelValues(double[] buffer, MuseDataPacket p) {
        buffer[0] = p.getEegChannelValue(Eeg.EEG1);
        buffer[1] = p.getEegChannelValue(Eeg.EEG2);
        buffer[2] = p.getEegChannelValue(Eeg.EEG3);
        buffer[3] = p.getEegChannelValue(Eeg.EEG4);
        buffer[4] = p.getEegChannelValue(Eeg.AUX_LEFT);
        buffer[5] = p.getEegChannelValue(Eeg.AUX_RIGHT);

    }

    private long increaseTime() {
        timestamp += 1000.0/60.0;
        return timestamp;
    }


    private void initUI() {
        setContentView(R.layout.activity_muse_connect);
        Button refreshButton = (Button) findViewById(R.id.refresh);
        refreshButton.setOnClickListener(this);
        Button connectButton = (Button) findViewById(R.id.connect);
        connectButton.setOnClickListener(this);
        Button disconnectButton = (Button) findViewById(R.id.disconnect);
        disconnectButton.setOnClickListener(this);
        Button pauseButton = (Button) findViewById(R.id.pause);
        pauseButton.setOnClickListener(this);

        spinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item);
        Spinner musesSpinner = (Spinner) findViewById(R.id.muses_spinner);
        musesSpinner.setAdapter(spinnerAdapter);
    }


    private final Runnable tickUi = new Runnable() {
        @Override
        public void run() {

            if (eegStale) {
                updateEeg();
            }
            handler.postDelayed(tickUi, 1000/60);
        }
    };


    private void updateEeg() {

        TextView tp9 = (TextView) findViewById(R.id.eeg_tp9);
        TextView fp1 = (TextView) findViewById(R.id.eeg_af7);
        TextView fp2 = (TextView) findViewById(R.id.eeg_af8);
        TextView tp10 = (TextView) findViewById(R.id.eeg_tp10);
//        tp9.setText(String.format("%6.2f", eegBuffer[0]));
//        fp1.setText(String.format("%6.2f", eegBuffer[1]));
//        fp2.setText(String.format("%6.2f", eegBuffer[2]));
//        tp10.setText(String.format("%6.2f", eegBuffer[3]));
        Map<String, Object> user = new HashMap<>();
        if (checkAlpha()) {
            tp9.setText(String.format("%6.2f", eegBuffer[0]));
            fp1.setText(String.format("%6.2f", eegBuffer[1]));
            fp2.setText(String.format("%6.2f", eegBuffer[2]));
            tp10.setText(String.format("%6.2f", eegBuffer[3]));
            user.put("EEG0", eegBuffer[0]);
            user.put("EEG1", eegBuffer[1]);
            user.put("EEG2", eegBuffer[2]);
            user.put("EEG3", eegBuffer[3]);
        } else {
            tp9.setText("0");
            fp1.setText("0");
            fp2.setText("0");
            tp10.setText("0");
            user.put("EEG0", 0);
            user.put("EEG1", 0);
            user.put("EEG2", 0);
            user.put("EEG3", 0);
        }
        user.put("timestamp", increaseTime());
        if (gap >= 2) {
            eegDataRef = database.getReference(reference);
            eegDataRef.keepSynced(true);
            eegDataRef.push().setValue(user);
        }
    }

    private boolean checkAlpha() {

        alpha1 = (TextView) findViewById(R.id.alpha1);
        alpha2 = (TextView) findViewById(R.id.alpha2);
        alpha3 = (TextView) findViewById(R.id.alpha3);
        alpha4 = (TextView) findViewById(R.id.alpha4);

        TextView alpha = (TextView) findViewById(R.id.alphaTxt);

        if (String.valueOf(alphaBuffer[0]).equals("NaN")) {
            alpha1.setBackgroundResource(R.color.red);
        } else {
            alpha1.setBackgroundResource(R.color.green);
        }
        if (String.valueOf(alphaBuffer[1]).equals("NaN")) {
            alpha2.setBackgroundResource(R.color.red);
        } else {
            alpha2.setBackgroundResource(R.color.green);
        }
        if (String.valueOf(alphaBuffer[2]).equals("NaN")) {
            alpha3.setBackgroundResource(R.color.red);
        } else {
            alpha3.setBackgroundResource(R.color.green);
        }
        if (String.valueOf(alphaBuffer[3]).equals("NaN")) {
            alpha4.setBackgroundResource(R.color.red);

        } else {
            alpha4.setBackgroundResource(R.color.green);
        }

        if (String.valueOf(alphaBuffer[0]).equals("NaN") || String.valueOf(alphaBuffer[1]).equals("NaN") || String.valueOf(alphaBuffer[2]).equals("NaN") || String.valueOf(alphaBuffer[3]).equals("NaN")) {
            alpha.setText("Popraw opaskę");
            if (gap < 2)
                gap = 1;
            return false;
        } else {
            alpha.setText("Wspolczynniki alfa:");
            if (gap == 1)
                gap = 2;
            return true;
        }

    }


    private final Thread fileThread = new Thread() {
        @Override
        public void run() {

        }
    };


    class MuseL extends MuseListener {
        final WeakReference<ConnectMuseActivity> activityRef;

        MuseL(final WeakReference<ConnectMuseActivity> activityRef) {
            this.activityRef = activityRef;
        }

        @Override
        public void museListChanged() {
            activityRef.get().museListChanged();
        }
    }

    class ConnectionListener extends MuseConnectionListener {
        final WeakReference<ConnectMuseActivity> activityRef;

        ConnectionListener(final WeakReference<ConnectMuseActivity> activityRef) {
            this.activityRef = activityRef;
        }

        @Override
        public void receiveMuseConnectionPacket(final MuseConnectionPacket p, final Muse muse) {
            activityRef.get().receiveMuseConnectionPacket(p, muse);
        }
    }

    class DataListener extends MuseDataListener {
        final WeakReference<ConnectMuseActivity> activityRef;

        DataListener(final WeakReference<ConnectMuseActivity> activityRef) {
            this.activityRef = activityRef;
        }

        @Override
        public void receiveMuseDataPacket(final MuseDataPacket p, final Muse muse) {
            activityRef.get().receiveMuseDataPacket(p, muse);
        }

        @Override
        public void receiveMuseArtifactPacket(final MuseArtifactPacket p, final Muse muse) {
            activityRef.get().receiveMuseArtifactPacket(p, muse);
        }
    }
}
