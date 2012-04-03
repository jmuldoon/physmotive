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
import android.widget.Button;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

import edu.usf.cse.physmotive.db.ActivityDBM;
import edu.usf.cse.physmotive.db.LocationDBM;

public class ActiveActivity extends MapActivity implements LocationListener
{
    public static final int OUT_OF_SERVICE = 0;
    public static final int TEMPORARILY_UNAVAILABLE = 1;
    public static final int AVAILABLE = 2;
    public static final String USERID = "userId";
    public static final String STARTTYPE_MAN = "manual";
    public static final String STARTTYPE_AUTO = "automatic";

    protected MapView mapView;
    protected MapController mapController;
    protected MapItemizedOverlay itemizedOverlay;
    protected GeoPoint point;
    protected LocationManager locationManager;
    protected List<Overlay> mapOverlays;
    private LocationDBM dblManager;
    private ActivityDBM dbaManager;
    protected Button manualStartButton;
    
    private int userID, raceID;
    private String startType;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.active_activity);

        // pulling in bundle information
        Bundle b = getIntent().getExtras();
        userID = b.getInt(USERID);
        startType = b.getString("startType");

        dblManager = new LocationDBM(this);
        dbaManager = new ActivityDBM(this);

        dbaManager.open();
        raceID = dbaManager.insert(userID);
        dbaManager.close();

        mapView = (MapView) findViewById(R.id.mapview);
        mapView.setBuiltInZoomControls(true);
        mapView.setStreetView(true);

        // TODO: also give me time info etc!!
        // TODO: on update put the totalTime and totalDistance with the activity.

        //Checks from previous screen if it starts manually or automatically.
        //if automatic it will just call for location services, otherwise
        //a dialog box will pop-up and wait till ready before starting them.
        if (startType.equals(STARTTYPE_MAN)){
        	showDialog(0);
        } 
        else {
        	initiateLocationServices();
        }

        mapOverlays = mapView.getOverlays();
        Drawable drawable = this.getResources().getDrawable(R.drawable.androidmarker);
        itemizedOverlay = new MapItemizedOverlay(drawable, this);
    }
    
    @Override
    protected Dialog onCreateDialog(int id){
    	return new AlertDialog.Builder(this).setTitle("Manual Start")
                .setPositiveButton("Ready!", new DialogButtonClickHandler()).create();
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
    
    private void initiateLocationServices(){
    	// Use the LocationManager class to obtain GPS locations.
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 3, this);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 2000, 3, this);
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
        dblManager.open();
        dblManager.delete(raceID, userID);
        dblManager.close();
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

    @Override
    public void onLocationChanged(Location loc)
    {
        mapController = mapView.getController();

        point = new GeoPoint((int) (loc.getLatitude() * 1E6), (int) (loc.getLongitude() * 1E6));

        mapController.animateTo(point);
        mapController.setZoom(20);
        mapView.invalidate();

        addGeoPoint(point, "Current Location", loc.getLatitude() + " : " + loc.getLongitude());
        Log.d("lat:long", loc.getLatitude() + ":" + loc.getLongitude());

        dblManager.open();
        dblManager.insert(raceID, String.valueOf(loc.getLatitude()), String.valueOf(loc.getLongitude()),
                (int) loc.getTime(), userID);
        dblManager.close();
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
        // TODO: Something
    }
}