package edu.usf.cse.physmotive;

import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

import edu.usf.cse.physmotive.db.ActivityDBM;
import edu.usf.cse.physmotive.db.LocationDBM;
import edu.usf.cse.physmotive.logic.Statistics;
import edu.usf.cse.physmotive.logic.StopWatch;

public class ActiveActivity extends MapActivity implements LocationListener
{
    public static final int OUT_OF_SERVICE = 0;
    public static final int TEMPORARILY_UNAVAILABLE = 1;
    public static final int AVAILABLE = 2;
    public static final String USERID = "userId";
    public static final String STARTTYPE_MAN = "manual";
    public static final String STARTTYPE_AUTO = "automatic";
    public static final int UNITTYPE_TIME = 0;
    public static final int UNITYPE_DIST = 1;

    protected MapView mapView;
    protected MapController mapController;
    protected MapItemizedOverlay itemizedOverlay;
    protected GeoPoint point, curr, prev;
    protected LocationManager locationManager;
    protected List<Overlay> mapOverlays;
    protected Button endActivityButton;
    protected TextView currentDistanceTextView;
    protected TextView currentSpeedTextView;
    protected ProgressBar activityProgressBar;
    protected static TextView time_tv;
    
    private LocationDBM dblManager;
    private ActivityDBM dbaManager;
    private int userId, raceId, unitType, unitValue, progressStatus = 0, endFlag = 0;
    private long tTime = 0, tDistance = 0;
    private String startType;
    private double  ptime = 0, ftime = 0;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.active_activity);

        // pulling in bundle information
        Bundle b = getIntent().getExtras();
        userId = b.getInt(USERID);
        raceId = b.getInt("activityId");
        startType = b.getString("startType");
        unitType = b.getInt("unitType");
        unitValue = b.getInt("unitValue");

        dblManager = new LocationDBM(this);
        dbaManager = new ActivityDBM(this);

        mapView = (MapView) findViewById(R.id.mapview);
        mapView.setBuiltInZoomControls(true);
        mapView.setStreetView(true);

        currentSpeedTextView = (TextView) findViewById(R.id.currentSpeedTextView);
        currentDistanceTextView = (TextView) findViewById(R.id.currentDistanceTextView);
        endActivityButton = (Button) findViewById(R.id.endActivityButton);
        activityProgressBar = (ProgressBar) findViewById(R.id.acitvityProgressBar);
        activityProgressBar.setMax(100);

        // Sets up Timer
        afterInit();

        // On Clicks
        setOnClickListeners();

        // Checks from previous screen if it starts manually or automatically.
        // if automatic it will just call for location services, otherwise
        // a dialog box will pop-up and wait till ready before starting them.
        if (startType.equals(STARTTYPE_MAN))
        {
            showDialog(0);
        } else
        {
            initiateLocationServices();
        }

        mapOverlays = mapView.getOverlays();
        Drawable drawable = this.getResources().getDrawable(R.drawable.androidmarker);
        itemizedOverlay = new MapItemizedOverlay(drawable, this);
    }

    private void afterInit()
    {
        time_tv = (TextView) findViewById(R.id.timeTextView);
        setLabelText("00:00:00", time_tv);
        StopWatch.setStartTimePoint(Long.valueOf(0));
        StopWatch.stopCounting();
    }

    public void setLabelText(String string, TextView label)
    {
    	label.setText(string);
    }
    
    public static TextView getTimeLabel(){
    	return time_tv;
    }
    
    private void updateProgressBar()
    {
        progressStatus = progressWork();
        activityProgressBar.setProgress(progressStatus);
    }

    private int progressWork()
    {
        float progress = 0;
        // unitType == 0 is time in sec, unitType == 1 is distance in meters
        if (unitType == 0)
        {
            progress = (int) (((double) tTime / (double) unitValue) * 100);
        } else
        {
            progress = (int) (((double) tDistance / (double) unitValue) * 100);
        }
        return Math.round(progress);
    }

    private boolean activityComplete()
    {
        if (activityProgressBar.getProgress() >= 100)
            return true;
        else if (progressStatus >= 100) { return true; }

        return false;
    }

    @Override
    protected Dialog onCreateDialog(int id)
    {
        return new AlertDialog.Builder(this).setTitle("Manual Start")
                .setPositiveButton("Ready!", new DialogButtonClickHandler()).create();
    }

    private void onFinish()
    {
    	StopWatch.stopCounting();
        // If not complete
        // TODO: delete race.
        // Only delete(raceId) needs be called.
        if (!activityComplete() && endFlag == 0)
        {
            dbaManager.open();
            dbaManager.delete(raceId);
            dbaManager.close();
        }

        // if Complete:
        // TODO: Get Final GPS pull save with Finished note.
        // dblManager.insert(raceId, lat, lng, spd, lts, "finished", userId);
        // TODO: get tTime to insert int seconds for total time.
        else if (activityComplete()){
            dbaManager.open();
            dbaManager.update(raceId, userId, (int) tTime, (int) tDistance);
            dbaManager.close();
        }

        // If ended early
        // TODO: make sure it saves current info
        else if (!activityComplete() && endFlag == 1)
        {
        	dbaManager.open();
            dbaManager.update(raceId, userId, (int) tTime, (int) tDistance);
            dbaManager.close();
        }
    }

    private void setOnClickListeners()
    {
        endActivityButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v)
            {
                onButtonClickEndActivityButton(v);
            }
        });
    }

    private void onButtonClickEndActivityButton(View v)
    {
        endFlag = 1;
        finish();
    }

    public class DialogButtonClickHandler implements DialogInterface.OnClickListener
    {
        public void onClick(DialogInterface dialog, int clicked)
        {
            switch (clicked) {
            case DialogInterface.BUTTON_POSITIVE:
                initiateLocationServices();
                break;
            }
        }
    }

    private void initiateLocationServices()
    {
        // Use the LocationManager class to obtain GPS locations.
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 3, this);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 2000, 3, this);
        dblManager.open();
        dblManager.insert(raceId, 0, 0, 0, 0, "start", userId);
        dblManager.close();

        StopWatch.startCounting();
    }

    public void addGeoPoint(GeoPoint p, String greeting, String message)
    {
        OverlayItem overlayitem = new OverlayItem(p, greeting, message);

        itemizedOverlay.addOverlay(overlayitem);
        mapOverlays.add(itemizedOverlay);
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        onFinish();
    }

    @Override
    public void onStop()
    {
        super.onStop();
        locationManager.removeUpdates(this);
    }

    @Override
    public void onRestart()
    {
        super.onRestart();
        initiateLocationServices();
    }

    @Override
    protected boolean isRouteDisplayed()
    {
        return false;
    }

    private void updateGeoPoints(GeoPoint p)
    {
        prev = curr;
        curr = p;
    }

    private void updateStatistics(double spd)
    {
        currentSpeedTextView.setText(String.valueOf(Statistics.roundTwoDecimals(spd)));
        currentDistanceTextView.setText(String.valueOf(tDistance));
    }
   
    public void updateTimings(double t){
    	tTime = (long)t;
    	ptime = ftime;
    	ftime = t-ptime;
    	Log.e("time:ptime:ftime", t+":"+ptime+":"+ftime);
    }

    @Override
    public void onLocationChanged(Location loc)
    {
        float[] result = new float[3];
        double speed = 0, timeTaken = 0;
        mapController = mapView.getController();

        point = new GeoPoint((int) (loc.getLatitude() * 1E6), (int) (loc.getLongitude() * 1E6));

        // Update GeoPoints to keep current Stats
        updateGeoPoints(point);
        updateTimings(StopWatch.currentTime());

        // Update the distance for total Distance
        if (prev != null)
        {
            Location.distanceBetween(prev.getLatitudeE6() / 1E6, prev.getLongitudeE6() / 1E6, curr.getLatitudeE6() / 1E6,
                    curr.getLongitudeE6() / 1E6, result);
            tDistance += result[0];

            timeTaken = ftime - ptime;
            speed = Statistics.getSpeed(result[0], timeTaken);
        }

        // Update Stats on Page
        updateStatistics(speed);

        mapController.animateTo(point);
        mapController.setZoom(20);
        mapView.invalidate();

        addGeoPoint(point, "Current Location", loc.getLatitude() + " : " + loc.getLongitude());
        Log.d("lat:long:time", loc.getLatitude() + ":" + loc.getLongitude() + ":" + Statistics.roundTwoDecimals(speed));

        dblManager.open();
        dblManager.insert(raceId, (int) (loc.getLatitude() * 1E6), (int) (loc.getLongitude() * 1E6), (int) speed,
                (int) loc.getTime(), "", userId);
        dblManager.close();

        // Checks to see if the activity is done. if so will call onFinish();
        updateProgressBar();

        if (activityComplete())
            onFinish();
    }

    @Override
    public void onProviderDisabled(String provider)
    {
        Log.d("onProviderDisabled", "GPS Disabled");
    }

    @Override
    public void onProviderEnabled(String provider)
    {
        Log.d("onProviderEnabled", "GPS Enabled");
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras)
    {
        // DO Nothing
    }
}