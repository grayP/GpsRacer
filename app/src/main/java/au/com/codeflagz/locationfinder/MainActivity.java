package au.com.codeflagz.locationfinder;


import android.app.Activity;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GestureDetectorCompat;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import android.widget.TextView;
import android.widget.Toast;

import com.jjoe64.graphview.GraphView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import au.com.codeflagz.locationfinder.model.GpsGraph;
import au.com.codeflagz.locationfinder.model.GpsSettings;


public class MainActivity extends Activity implements
        GestureDetector.OnGestureListener,
        GestureDetector.OnDoubleTapListener {
    private final Handler mHandler = new Handler();
    private long BackPressed;
    private Toast BackToast;
    Button btn_start, btn_setting, btn_exit;
    boolean boolean_permission;
    TextView tv_version;
    private static final int REQUEST_PERMISSIONS = 100;
    SharedPreferences mPref;
    SharedPreferences.Editor medit;
    Double latitude, longitude, speed, bearing;
    Date tor;
    private GpsDataService gpsDataService = new GpsDataService();
    private GpsGraph gpsGraph = new GpsGraph();
    private GpsSettings gpsSettings = new GpsSettings();
    private GestureDetectorCompat mDetector;
    private static final String DEBUG_TAG = "Gestures";

    @Override
    public void onBackPressed() {
        if (BackPressed + 2000 > System.currentTimeMillis()) {
            BackToast.cancel();
            mPref.edit().remove("service").commit();
            super.onBackPressed();
        } else {
            BackToast = Toast.makeText(getBaseContext(), "Press back again to exit", Toast.LENGTH_SHORT);
            BackToast.show();
        }
        BackPressed = System.currentTimeMillis();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gpsGraph.sogGraph = (GraphView) findViewById(R.id.sogGraph);
        gpsGraph.cogGraph = (GraphView) findViewById(R.id.cogGraph);
        gpsGraph.SetupGraph(this.getResources().getDisplayMetrics());

        mDetector = new GestureDetectorCompat(this, this);
        mDetector.setOnDoubleTapListener(this);

        btn_start = (Button) findViewById(R.id.btn_start);
        btn_setting = (Button) findViewById(R.id.btn_settings);
        btn_exit =(Button) findViewById(R.id.btn_Exit);
        tv_version = (TextView) findViewById(R.id.headerText);
        tv_version.setText("Race Tactics :" + String.valueOf(getPackageInfo().versionCode));
        mPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        medit = mPref.edit();

        SetupPreferences(mPref);

        btn_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SettingActivity.class);
                intent.putExtra("GpsSettings", gpsSettings);
                startActivity(intent);
            }
        });
        btn_exit.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                mPref.edit().remove("service").commit();
                finish();
               // System.exit(0);
            }
        });
        btn_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (boolean_permission) {
                    if (mPref.getString("service", "").matches("")) {
                        medit.putString("service", "service").commit();

                    } else {
                        if (isMyServiceRunning(GoogleService.class)) {
                            Toast.makeText(getApplicationContext(), "Service is already running", Toast.LENGTH_SHORT).show();

                        } else {
                            Intent intent = new Intent(getApplicationContext(), GoogleService.class);
                            startService(intent);
                        }
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Please enable the gps", Toast.LENGTH_SHORT).show();
                }
            }

        });
        fn_permission();
    }

    private void SetupPreferences(SharedPreferences sharedPref) {
        gpsSettings.FastSog = sharedPref.getInt(getString(R.string.fastSog), gpsSettings.FastSog);
        gpsSettings.SlowSog = sharedPref.getInt(getString(R.string.slowSog), gpsSettings.SlowSog);
        gpsSettings.FastCog = sharedPref.getInt(getString(R.string.fastCog), gpsSettings.FastCog);
        gpsSettings.SlowCog = sharedPref.getInt(getString(R.string.slowCog), gpsSettings.SlowCog);
        gpsSettings.NumSeconds=sharedPref.getInt(getString(R.string.numSeconds), gpsSettings.NumSeconds);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (this.mDetector.onTouchEvent(event)) {
            return true;
        }
        return super.onTouchEvent(event);
    }

    @Override
    public boolean onDown(MotionEvent event) {
        Log.d(DEBUG_TAG, "onDown: " + event.toString());
        return true;
    }

    @Override
    public boolean onFling(MotionEvent event1, MotionEvent event2,
                           float velocityX, float velocityY) {
        Log.d(DEBUG_TAG, "onFling: " + event1.toString() + event2.toString());
        SwapGraphs();
        return true;
    }

    @Override
    public void onLongPress(MotionEvent event) {
        Log.d(DEBUG_TAG, "onLongPress: " + event.toString());
    }

    @Override
    public boolean onScroll(MotionEvent event1, MotionEvent event2, float distanceX,
                            float distanceY) {
        Log.d(DEBUG_TAG, "onScroll: " + event1.toString() + event2.toString());
        return true;
    }

    @Override
    public void onShowPress(MotionEvent event) {
        Log.d(DEBUG_TAG, "onShowPress: " + event.toString());
    }

    @Override
    public boolean onSingleTapUp(MotionEvent event) {
        Log.d(DEBUG_TAG, "onSingleTapUp: " + event.toString());
        return true;
    }

    @Override
    public boolean onDoubleTap(MotionEvent event) {
        Log.d(DEBUG_TAG, "onDoubleTap: " + event.toString());
        SwapGraphs();
        return true;
    }

    @Override
    public boolean onDoubleTapEvent(MotionEvent event) {
        Log.d(DEBUG_TAG, "onDoubleTapEvent: " + event.toString());
        return true;
    }


    private void SwapGraphs() {
        if (gpsGraph.sogGraph.getVisibility() == View.VISIBLE) {
            //gpsGraph.sogGraph.animate().alpha(0.0f);
            gpsGraph.sogGraph.setVisibility(View.GONE);
            //gpsGraph.cogGraph.animate().alpha(1.0f);
            gpsGraph.cogGraph.setVisibility(View.VISIBLE);
        } else {
            //gpsGraph.cogGraph.animate().alpha(0.0f);
            gpsGraph.cogGraph.setVisibility(View.GONE);
            // gpsGraph.sogGraph.animate().alpha(0.1f);
            gpsGraph.sogGraph.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent event) {
        Log.d(DEBUG_TAG, "onSingleTapConfirmed: " + event.toString());
        return true;
    }

    private void fn_permission() {
        if ((ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {

            if ((ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION))) {
            } else {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION
                        },
                        REQUEST_PERMISSIONS);
            }
        } else {
            boolean_permission = true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case REQUEST_PERMISSIONS: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    boolean_permission = true;
                } else {
                    Toast.makeText(getApplicationContext(), "Please allow the permission", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    private BroadcastReceiver broadcastReceiver;

    {
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                latitude = Double.valueOf(intent.getStringExtra("latitude"));
                longitude = Double.valueOf(intent.getStringExtra("longitude"));
                speed = Double.valueOf(intent.getStringExtra("sog"));
                bearing = Double.valueOf(intent.getStringExtra("cog"));
                String sTor = intent.getStringExtra("tor");

                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
                try {
                    tor = format.parse(sTor);
                } catch (ParseException e) {
                    e.printStackTrace();
                    Calendar calendar = Calendar.getInstance();
                    tor = calendar.getTime();
                }
                gpsDataService.UpdateSpeed(speed, gpsSettings.SlowSog, gpsSettings.FastSog);
                // gpsDataService.UpdateBearing(bearing, gpsSettings.SlowCog, gpsSettings.FastCog);
                gpsDataService.UpdateBearing(latitude, longitude, gpsSettings.SlowCog, gpsSettings.FastCog);
                // tv_sog.setText(String.format("%.2f", gpsDataService.dFastSog));
                Log.e("Fast", gpsDataService.dFastCog + "");
                Log.e("Slow", gpsDataService.dSlowCog + "");
                gpsGraph.AddDataAndSetYAxis(gpsDataService.dSlowSog, gpsDataService.dFastSog, tor, gpsSettings.NumSeconds);
                gpsGraph.AddCogDataAndSetYAxis(gpsDataService.dSlowCog, gpsDataService.dFastCog, tor, gpsSettings.NumSeconds);
                UpdateText(gpsDataService);
            }
        };
    }
    private void UpdateText(GpsDataService gpsDataService){
       try{
           String output="Lat: " + String.format("%.4f",gpsDataService.dLat) + "";
           TextView lat = findViewById(R.id.textLatitude);
           lat.setText(output);
           output="Long: " + String.format("%.4f",gpsDataService.dLong) + "";
           TextView llong = findViewById(R.id.textLongitude);
           llong.setText(output);
           output="Fast: " + String.format("%.1f",gpsDataService.dFastCog) + "";
           TextView fastCog = findViewById(R.id.textCogFast);
           fastCog.setText(output);
           output="Slow: " + String.format("%.1f",gpsDataService.dSlowCog) + "";
           TextView slowCog = findViewById(R.id.textCogSlow);
           slowCog.setText(output);
       }
       catch(Exception ex){

       }


    }
    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(broadcastReceiver, new IntentFilter(GoogleService.str_receiver));

    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(broadcastReceiver);
    }


    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                String var1 = serviceClass.getName();
                String var2 = service.service.getClassName();
                Log.e("serviceClass.getName()", var1);
                Log.e("service.getClassName()", var2);
                return true;
            }
        }
        return false;
    }

    private PackageInfo getPackageInfo() {
        PackageInfo pi = null;
        try {
            pi = this.getPackageManager().getPackageInfo(
                    this.getPackageName(), PackageManager.GET_ACTIVITIES);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            Log.e("yourTagHere", e.getMessage());
        }
        return pi;
    }

}