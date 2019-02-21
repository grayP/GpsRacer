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
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class MainActivity extends Activity {
    private final Handler mHandler = new Handler();
    private long BackPressed;
    private Toast BackToast;
    Button btn_start;
    private static final int REQUEST_PERMISSIONS = 100;
    boolean boolean_permission;
   // TextView tv_latitude, tv_longitude, ;
            TextView tv_sog, tv_cog, tv_version;
    SharedPreferences mPref;
    SharedPreferences.Editor medit;
    Double latitude, longitude, speed;
    Date tor;
    // private Runnable mTimer2;
    private LineGraphSeries<DataPoint> mSeriesFastSog, mSeriesSlowSog;
    private SimpleDateFormat sdf;
    private Calendar calendar;
    GpsDataService gpsDataService;

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

        gpsDataService = new GpsDataService();
        btn_start = (Button) findViewById(R.id.btn_start);
        tv_sog = (TextView) findViewById(R.id.sog);
        tv_cog = (TextView) findViewById(R.id.cog);
        tv_version = (TextView) findViewById(R.id.version);
        mPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        medit = mPref.edit();
        //

        tv_version.setText(String.valueOf(getPackageInfo().versionCode));
        calendar = Calendar.getInstance();
        sdf = new SimpleDateFormat("mm:ss");
        SetupGraph();

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

    private LineGraphSeries<DataPoint> SetupSeries (int colour, int size, String title)
    {
        LineGraphSeries<DataPoint> series=  new  LineGraphSeries<>();
        series.setThickness(size);
        series.setDrawDataPoints(false);
        series.setColor(colour);
        series.setTitle(title);

        return series;
    }

    private void SetupGraph() {
        GraphView graph = (GraphView) findViewById(R.id.graph);
        mSeriesSlowSog = SetupSeries(android.R.color.holo_red_dark,2,"Sog-Slow");
        mSeriesFastSog = SetupSeries(android.R.color.black,2,"Sog-Fast");
        graph.addSeries(mSeriesFastSog);
        graph.addSeries(mSeriesSlowSog);
        graph.getViewport().setXAxisBoundsManual(true);
        graph.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter() {
            @Override
            public String formatLabel(double value, boolean isValueX) {
                if (isValueX) {
                    return sdf.format(new Date((long) value));
                } else {
                    return super.formatLabel(value, isValueX);
                }
            }
        });
        graph.getGridLabelRenderer().setNumHorizontalLabels(5); // only 4 because of the space
        Calendar calendar = Calendar.getInstance();
        Date d1 = calendar.getTime();
        calendar.add(Calendar.MINUTE, -1);
        Date d2 = calendar.getTime();
        graph.getViewport().setMaxX(d1.getTime());
        graph.getViewport().setMinX(d2.getTime());
        graph.getGridLabelRenderer().setHumanRounding(false);
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

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            latitude = Double.valueOf(intent.getStringExtra("latitude"));
            longitude = Double.valueOf(intent.getStringExtra("longitude"));
           // speed = Double.valueOf(intent.getStringExtra("sog"));
           // String sTor = intent.getStringExtra("tor");

           // SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
           // try {
           //     tor = format.parse(sTor);
           // } catch (ParseException e) {
           //     e.printStackTrace();
            Calendar calendar = Calendar.getInstance();
                tor=calendar.getTime();
           // }
          //  tv_latitude.setText(latitude + "");
          //  tv_longitude.setText(longitude + "");
            gpsDataService.UpdateSog(latitude, longitude, calendar.getTime());
            mSeriesFastSog.appendData(new DataPoint(tor, gpsDataService.dFastSog), true, 20);
            mSeriesSlowSog.appendData(new DataPoint(tor, gpsDataService.dSlowSog), true, 20);
        }
    };

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