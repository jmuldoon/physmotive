package edu.usf.cse.physmotive;

import java.util.List;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

import android.graphics.drawable.Drawable;
import android.os.Bundle;

public class MapMenu extends MapActivity{
    protected MapView mapView;
    protected MapItemizedOverlay itemizedOverlay;
	protected List<Overlay> mapOverlays;
    
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_menu);
        mapView = (MapView)findViewById(R.id.mapview);
        mapView.setBuiltInZoomControls(true);
        
        mapOverlays = mapView.getOverlays();
        Drawable drawable = this.getResources().getDrawable(R.drawable.androidmarker);
        itemizedOverlay = new MapItemizedOverlay(drawable, this);
        
        addGeoPoint(35410000, 139460000, "Sekai, konichiwa!", "I'm in Japan!");
    }
    
	public void addGeoPoint(int x, int y, String greeting, String message){
		GeoPoint point = new GeoPoint(x,y);
        OverlayItem overlayitem = new OverlayItem(point, greeting,  message);
        
        itemizedOverlay.addOverlay(overlayitem);
        mapOverlays.add(itemizedOverlay);
	}
	
    @Override
    protected boolean isRouteDisplayed() {
        return false;
    }
}