package org.inspirecenter.amazechallenge.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Movie;
import android.graphics.Paint;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;

public class GIFView extends View {

    private static final int DEFAULT_MOVIEW_DURATION = 1000;
    private int movieResourceID;
    private Movie movie;
    private long movieStart = 0;
    private int currentAnimationTime = 0;
    private boolean isPlaying = false;
    private float lesserGradientScale = 1;

    @SuppressLint("NewApi")
    public GIFView(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) setLayerType(View.LAYER_TYPE_SOFTWARE, null);
    }//end GIFView()

    public void setImageResource(int mvId){
        this.movieResourceID = mvId;
        movie = Movie.decodeStream(getResources().openRawResource(movieResourceID));
        requestLayout();
    }//end setImageResource()

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int parentWidth = MeasureSpec.getSize(widthMeasureSpec);
        int parentHeight = MeasureSpec.getSize(heightMeasureSpec);
        this.setMeasuredDimension(parentWidth, parentHeight);
    }//end onMeasure()

    @Override
    protected void onDraw(Canvas canvas) {
        float scaleX = (float) this.getWidth() / (float) movie.width();
        float scaleY = (float) this.getHeight() / (float) movie.height();
        lesserGradientScale = Math.min(scaleX, scaleY);
        canvas.scale(lesserGradientScale, lesserGradientScale);

        drawGif(canvas);
        if (movie != null){
            if (isPlaying) {            //Important Note: Play the GIF only when it is visible, otherwise the application will slow down significantly.
                updateAnimationTime();
                drawGif(canvas);
                invalidate();
            }//end if isPlaying
        }//end if
        else drawGif(canvas);

    }//end onDraw()

    private void updateAnimationTime() {
        long now = android.os.SystemClock.uptimeMillis();
        if (movieStart == 0) movieStart = now;
        int dur = movie.duration();
        if (dur == 0) dur = DEFAULT_MOVIEW_DURATION;
        currentAnimationTime = (int) ((now - movieStart) % dur);
    }//end updateAnimationTime()

    private void drawGif(Canvas canvas) {
        movie.setTime(currentAnimationTime);
        movie.draw(canvas, 0 , 0);
        //System.out.println((canvas.getWidth() - movie.width()) / 2);
        //canvas.restore();
    }//end drawGif()

    public void play() { isPlaying = true; }

    public void stop() { isPlaying = false; }

}//end class GIFView