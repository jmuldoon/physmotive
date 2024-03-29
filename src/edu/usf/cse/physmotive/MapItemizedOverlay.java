package edu.usf.cse.physmotive;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.drawable.Drawable;

import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.OverlayItem;

public class MapItemizedOverlay extends ItemizedOverlay
{
    private ArrayList<OverlayItem> myOverlays = new ArrayList<OverlayItem>();
    private Context myContext;

    // private GeoPoint prePoint = null, currentPoint = null;

    public MapItemizedOverlay(Drawable defaultMarker, Context context) {
        super(boundCenterBottom(defaultMarker));
        myContext = context;
    }

    public void addOverlay(OverlayItem overlay)
    {
        myOverlays.add(overlay);
        populate();
    }

    // @Override
    // public void draw(Canvas canvas, MapView mapView, boolean shadow) {
    // super.draw(canvas, mapView, shadow);
    //
    // Paint paint = new Paint();
    // Point screenCoordsA = new Point();
    // Point screenCoordsB = new Point();
    //
    // mapView.getProjection().toPixels(prePoint, screenCoordsA);
    // int xA=screenCoordsA.x;
    // int yA=screenCoordsA.y;
    //
    // mapView.getProjection().toPixels(currentPoint, screenCoordsB);
    // int xB=screenCoordsB.x;
    // int yB=screenCoordsB.y;
    //
    // paint.setStrokeWidth(1);
    // canvas.drawLine(xA, yA, xB, yB, paint);
    // }

    @Override
    protected boolean onTap(int index)
    {
        OverlayItem item = myOverlays.get(index);
        AlertDialog.Builder dialog = new AlertDialog.Builder(myContext);
        dialog.setTitle(item.getTitle());
        dialog.setMessage(item.getSnippet());
        dialog.show();
        return true;
    }

    @Override
    protected OverlayItem createItem(int i)
    {
        return myOverlays.get(i);
    }

    @Override
    public int size()
    {
        return myOverlays.size();
    }

}
