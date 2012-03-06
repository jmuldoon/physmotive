package edu.usf.cse.physmotive;

import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import android.os.Bundle;

public class MapMenu extends MapActivity{
    protected MapView mapView; 
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        mapView = (MapView)findViewById(R.id.mapview);
        mapView.setBuiltInZoomControls(true);
    }
    
    @Override
    protected boolean isRouteDisplayed() {
        return false;
    }
}