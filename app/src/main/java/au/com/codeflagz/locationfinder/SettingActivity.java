package au.com.codeflagz.locationfinder;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import au.com.codeflagz.locationfinder.model.GpsSettings;

public class SettingActivity extends AppCompatActivity {

    private Button btn_Close;
    private GpsSettings gpsSettings;
    SharedPreferences mPref;
    SharedPreferences.Editor medit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        Intent i = getIntent();
        gpsSettings = (GpsSettings) i.getSerializableExtra("GpsSettings");

        mPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        medit = mPref.edit();

        UpdateText(gpsSettings.FastSog, R.id.textFastSogValue);
        UpdateText(gpsSettings.SlowSog, R.id.textSlowSogValue);
        UpdateText(gpsSettings.FastCog, R.id.textFastCogValue);
        UpdateText(gpsSettings.SlowCog, R.id.textSlowCogValue);
        UpdateText(gpsSettings.NumSeconds / 60, R.id.textMinutesValue);
        SetupSeekBars();

        btn_Close = findViewById(R.id.btn_Close);
        btn_Close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void UpdateText(int value, int textBarId) {
        TextView txtFastSog = findViewById(textBarId);
        txtFastSog.setText(Integer.toString(value));
    }

    private void UpdatePreferences(int value, String key) {
        medit.putInt(key, value);
        medit.commit();
    }

    private void SetupSeekBars() {
        SeekBar sogSlow = findViewById(R.id.seekBarSlowSog);
        sogSlow.setProgress(gpsSettings.SlowSog);
        sogSlow.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {
                UpdateText(progress, R.id.textSlowSogValue);
                UpdatePreferences(progress, "SlowSog");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        SeekBar fastSog = findViewById(R.id.seekBarFastSog);
        fastSog.setProgress(gpsSettings.FastSog);
        fastSog.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {
                UpdateText(progress, R.id.textFastSogValue);
                UpdatePreferences(progress, "FastSog");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        SeekBar slowCog = findViewById(R.id.seekBarSlowCog);
        slowCog.setProgress(gpsSettings.SlowCog);
        slowCog.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {

                UpdateText(progress, R.id.textSlowCogValue);
                UpdatePreferences(progress, "SlowCog");

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        SeekBar fastCog = findViewById(R.id.seekBarFastCog);
        fastCog.setProgress(gpsSettings.FastCog);
        fastCog.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {
                UpdateText(progress, R.id.textFastCogValue);
                UpdatePreferences(progress, "FastCog");

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });


        SeekBar numMinutes = findViewById(R.id.seekBarMinutes);
        numMinutes.setProgress(gpsSettings.NumSeconds / 60);
        numMinutes.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {

                UpdateText(progress, R.id.textMinutesValue);
                UpdatePreferences(progress * 60, "NumSeconds");

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
    }


}
