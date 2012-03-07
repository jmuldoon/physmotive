package edu.usf.cse.physmotive;

//This will be put in later

import java.util.ArrayList;

import android.graphics.drawable.Drawable;

import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.OverlayItem;

public class MapItemizedOverlay extends ItemizedOverlay
{

    private ArrayList<OverlayItem> mOverlays = new ArrayList<OverlayItem>();

    public MapItemizedOverlay(Drawable defaultMarker) {
	super(boundCenterBottom(defaultMarker));
    }

    @Override
    protected OverlayItem createItem(int i)
    {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public int size()
    {
	// TODO Auto-generated method stub
	return 0;
    }

    public void addOverlay(OverlayItem overlay)
    {
	mOverlays.add(overlay);
	populate();
    }

}
