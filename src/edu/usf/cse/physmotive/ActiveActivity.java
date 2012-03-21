package edu.usf.cse.physmotive;

import java.util.List;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

import edu.usf.cse.physmotive.db.ActivityDBM;
import edu.usf.cse.physmotive.db.LocationDBM;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

public class ActiveActivity extends MapActivity implements LocationListener{
	public static final int OUT_OF_SERVICE = 0;
	public static final int TEMPORARILY_UNAVAILABLE = 1;
	public static final int AVAILABLE = 2;
	
	protected MapView mapView;
    protected MapController mapController;
    protected MapItemizedOverlay itemizedOverlay;
	protected GeoPoint point;
    protected LocationManager locationManager;
	protected List<Overlay> mapOverlays;
	private LocationDBM dblManager;
	private ActivityDBM dbaManager;
	
	private int usrID, raceID;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.active_activity);
        
        Bundle b = getIntent().getExtras();
        usrID = b.getInt("userId");
        
        dblManager = new LocationDBM(this);
        dbaManager = new ActivityDBM(this);
        raceID = (int)dbaManager.insert(usrID);
        
        mapView = (MapView)findViewById(R.id.mapview);
        mapView.setBuiltInZoomControls(true);
        mapView.setStreetView(true);
        
        // TODO:get eddy to pass into bundle button manual / auto
        // 		if manual, force button click to start, else start
        
        // Use the LocationManager class to obtain GPS locations.
        locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 3, this);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 2000, 3, this);
        
        mapOverlays = mapView.getOverlays();
        Drawable drawable = this.getResources().getDrawable(R.drawable.androidmarker);
        itemizedOverlay = new MapItemizedOverlay(drawable, this);
	}
	
	public void addGeoPoint(GeoPoint p, String greeting, String message){
        OverlayItem overlayitem = new OverlayItem(p, greeting,  message);
        
        itemizedOverlay.addOverlay(overlayitem);
        mapOverlays.add(itemizedOverlay);
	}
	
	@Override
	public void onDestroy(){
		super.onDestroy();
		dblManager.delete(raceID, usrID);
	}
	
	@Override
	public void onStop(){
		super.onStop();
		locationManager.removeUpdates(this);
	}
    
	@Override
	public void onRestart(){
		super.onRestart();
		locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 3, this);
		locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 2000, 3, this);
	}
	
    @Override
    protected boolean isRouteDisplayed() {
        return false;
    }
    
    @Override
	public void onLocationChanged(Location loc){
        mapController = mapView.getController();
		
        point = new GeoPoint((int)(loc.getLatitude() * 1E6),(int)(loc.getLongitude() * 1E6));
        
        mapController.animateTo(point);
        mapController.setZoom(20);
        mapView.invalidate();
        
        addGeoPoint(point, "Current Location", loc.getLatitude()+" : "+loc.getLongitude());
        Log.d("lat:long", loc.getLatitude()+":"+loc.getLongitude());
        
        dblManager.insert(raceID, String.valueOf(loc.getLatitude()), String.valueOf(loc.getLongitude()), usrID);
    }

    @Override
    public void onProviderDisabled(String provider){
    	Log.d("onProviderDisabled", "GPS Disabled");
    }

    @Override
    public void onProviderEnabled(String provider){
    	Log.d("onProviderEnabled", "GPS Enabled");
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras){
		// TODO: Something
    }
}