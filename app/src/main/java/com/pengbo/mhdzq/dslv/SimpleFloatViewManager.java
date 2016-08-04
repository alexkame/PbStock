package com.pengbo.mhdzq.dslv;

import com.pengbo.mhdzq.R;

import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Color;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ImageView;
import android.widget.TextView;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
/**
 * Simple implementation of the FloatViewManager class. Uses list
 * items as they appear in the ListView to create the floating View.
 */
public class SimpleFloatViewManager implements DragSortListView.FloatViewManager {

    private Bitmap mFloatBitmap;

    private ImageView mImageView;
    private FrameLayout mFrameLayout;
    private LinearLayout mlLayout;
    private TextView mTextView;

    private int mFloatBGColor = Color.BLACK;

    private ListView mListView;

    public SimpleFloatViewManager(ListView lv) {
        mListView = lv;
    }

    public void setBackgroundColor(int color) {
        mFloatBGColor = color;
    }

    /**
     * This simple implementation creates a Bitmap copy of the
     * list item currently shown at ListView <code>position</code>.
     */
    @Override
    public View onCreateFloatView(int position) {
        // Guaranteed that this will not be null? I think so. Nope, got
        // a NullPointerException once...
        View v = mListView.getChildAt(position + mListView.getHeaderViewsCount() - mListView.getFirstVisiblePosition());

        if (v == null) {
            return null;
        }

        v.setPressed(false);
        TextView tv = (TextView) v.findViewById(R.id.headEditActivity_text);
        //String text = tv.getText().toString();
        //tv.setText(text);
        // Create a copy of the drawing cache so that it does not get
        // recycled by the framework when the list tries to clean up memory
        //v.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        v.setDrawingCacheEnabled(true);
        mFloatBitmap = Bitmap.createBitmap(v.getDrawingCache());
        v.setDrawingCacheEnabled(false);

        if (mFrameLayout == null)
        {
        	mFrameLayout = new FrameLayout(mListView.getContext());//定义框架布局器  
        }
        
        if (mlLayout == null)
        {
        	mlLayout = new LinearLayout(mListView.getContext());
        }
        if (mTextView == null)
        {
        	mTextView = new TextView(mListView.getContext());//定义组件  
        }
       // mTextView.setText(text);

        mlLayout.setLayoutParams(new LinearLayout.LayoutParams(v.getWidth(), v.getHeight()));
        mlLayout.setGravity(Gravity.CENTER|Gravity.CENTER_VERTICAL);
        mlLayout.addView(mTextView);

        if (mImageView == null) {
            mImageView = new ImageView(mListView.getContext());
        }
        mImageView.setBackgroundColor(mFloatBGColor);
        mImageView.setPadding(0, 0, 0, 0);
        mImageView.setImageBitmap(mFloatBitmap);
        mImageView.setLayoutParams(new ViewGroup.LayoutParams(v.getWidth(), v.getHeight()));

        mFrameLayout.addView(mImageView);//添加组件  
        mFrameLayout.addView(mlLayout);//添加组件 
        return mFrameLayout;
    }

    /**
     * This does nothing
     */
    @Override
    public void onDragFloatView(View floatView, Point position, Point touch) {
        // do nothing
    }

    /**
     * Removes the Bitmap from the ImageView created in
     * onCreateFloatView() and tells the system to recycle it.
     */
    @Override
    public void onDestroyFloatView(View floatView) {
    	mImageView.setImageDrawable(null);
    	
        mFloatBitmap.recycle();
        mFloatBitmap = null;
        if (mlLayout != null)
        	mlLayout.removeAllViews();
        ((FrameLayout)floatView).removeAllViews();
    }

}

