package edu.usf.cse.physmotive.ui;

import edu.usf.cse.physmotive.R;
import android.content.Context;
import android.content.res.TypedArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;

public class ImageAdapter extends BaseAdapter {
    int mGalleryItemBackground;
    private Context mContext;
    
    // TODO: get this to work. This is the string that would be used to have the array set dynamically 
    // 		 for the images to be loaded.
//    String[] galleryPictures = getResources().getStringArray(R.array.Activity_Gallery);
    
    private Integer[] mImageIds = {    		
    		R.drawable.sample_0,
    		R.drawable.sample_1,
            R.drawable.sample_2,
            R.drawable.sample_3,
            R.drawable.sample_4,
            R.drawable.sample_5,
            R.drawable.sample_6,
            R.drawable.sample_7
    };

    public ImageAdapter(Context c) {
        mContext = c;
        TypedArray attr = mContext.obtainStyledAttributes(R.styleable.PictureGallery);
        mGalleryItemBackground = attr.getResourceId(R.styleable.PictureGallery_android_galleryItemBackground, 0);
        attr.recycle();
    }

    public int getCount() {
        return mImageIds.length;
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView = new ImageView(mContext);

        imageView.setImageResource(mImageIds[position]);
        imageView.setLayoutParams(new Gallery.LayoutParams(150, 100));
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        imageView.setBackgroundResource(mGalleryItemBackground);

        return imageView;
    }
}