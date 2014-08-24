package com.snaprix.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;

/**
 * ImageView that keeps aspect ratio when scaled
 */
public class ScaleImageView extends ImageView {
    private static boolean DEBUG = false;
    public static void setDebug(boolean debug){
        DEBUG = debug;
    }

    private static final String TAG = "ScaleImageView";

    private int mWidthSize;
    private int mHeightSize;
    private Matrix mMatrix;

    public ScaleImageView(Context context) {
        super(context);
        init();
    }

    public ScaleImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ScaleImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        mMatrix = new Matrix();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // take dimensions provided by parent for this view:
        int measuredWidth = MeasureSpec.getSize(widthMeasureSpec);
        int measuredHeight = MeasureSpec.getSize(heightMeasureSpec);

        // subtract padding to get dimensions available for this view
        final int vwidth = measuredWidth - getPaddingLeft() - getPaddingRight();
        final int vheight = measuredHeight - getPaddingTop() - getPaddingBottom();

        // get drawable size
        Drawable drawable = getDrawable();
        final int dwidth = drawable.getIntrinsicWidth();
        final int dheight = drawable.getIntrinsicHeight();

        /*
         * When view height is smaller than image height and view width
         * is larger than image width calc target width, height and
         * apply matrix transformation, otherwise just take view width
         * and height.
         */
        if (vheight < dheight && dwidth < vwidth) {
            // fill entire view width
            mWidthSize = vwidth;
            // calculate target height to maintain aspect ratio
            mHeightSize = (dheight * vwidth) / dwidth;

            if (DEBUG) Log.v(TAG, String.format("onMeasure v w=%d h=%d d w=%d h=%d",
                    vwidth, vheight, dwidth, dheight));

            // move image to have “fit bottom” effect
            int dy = vheight - mHeightSize;
            mMatrix.setTranslate(0, dy);

            // apply matrix transformation
            setImageMatrix(mMatrix);
        } else {
            mWidthSize = vwidth;
            mHeightSize = vheight;
        }

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (DEBUG) Log.v(TAG, String.format("onDraw mWidthSize=%d mHeightSize=%d",
                mWidthSize, mHeightSize));


        Drawable drawable = getDrawable();
        // set bounds to scale drawable using 9-patch params
        drawable.setBounds(0, 0, mWidthSize, mHeightSize);

        super.onDraw(canvas);
    }
}