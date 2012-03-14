package edu.usf.cse.physmotive;

import java.util.List;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

public class MapMenu extends MapActivity{	
	protected MapView mapView;
    protected MapController mapController;
    protected MapItemizedOverlay itemizedOverlay;
	protected GeoPoint point;
    protected LocationManager locationManager;
	protected LocationListener locationListener;
	protected List<Overlay> mapOverlays;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_menu);
        mapView = (MapView)findViewById(R.id.mapview);
        mapView.setBuiltInZoomControls(true);
        mapView.setStreetView(true);
        
        // Use the LocationManager class to obtain GPS locations.
        locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);

        locationListener = new MyLocationListener();
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2500, 5, this.locationListener);
        
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
    protected boolean isRouteDisplayed() {
        return false;
    }
    
    // Class My Location Listener.
    public class MyLocationListener implements LocationListener {
    	public static final int OUT_OF_SERVICE = 0;
    	public static final int TEMPORARILY_UNAVAILABLE = 1;
    	public static final int AVAILABLE = 2;
    	    	
    	public void onLocationChanged(Location loc){
	        mapController = mapView.getController();
			
	        point = new GeoPoint((int)(loc.getLatitude() * 1E6),(int)(loc.getLongitude() * 1E6));
	        
	        mapController.animateTo(point);
	        mapController.setZoom(20);
	        mapView.invalidate();
	        
	        addGeoPoint(point, "Sup Gaisz", "current pos");
	        Log.d("lat:long", loc.getLatitude()+":"+loc.getLongitude());
	    }

	    public void onProviderDisabled(String provider){
	    	Log.d("onProviderDisabled", "GPS Disabled");
	    }

	    public void onProviderEnabled(String provider){
	    	Log.d("onProviderEnabled", "GPS Enabled");
	    }

	    public void onStatusChanged(String provider, int status, Bundle extras){
    		// TODO: Something
	    }
    }
}