package org.inspirecenter.amazechallenge.ui;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Movie;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;

public class GIFView extends View {

    private static final float SCALE_LOWRES_HEIGHT = 0.5f;
    private static final float SCALE_LOWRES_WIDTH = 0.5f;

    private static final int DEFAULT_MOVIE_DURATION = 1000;
    private Context context;
    private int movieResourceID;
    private Movie movie;
    private long movieStart = 0;
    private int currentAnimationTime = 0;

    private boolean scaleToFitLowerResolutions = false;
    private int viewportHeight;
    private int viewportWidth;

    public GIFView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        viewportWidth = Resources.getSystem().getDisplayMetrics().widthPixels;
        viewportHeight = Resources.getSystem().getDisplayMetrics().heightPixels;
        if (viewportHeight < 960 && viewportWidth < 540) {
            scaleToFitLowerResolutions = true;
        }
    }//end GIFView()

    public GIFView(Context context, AttributeSet attrs, final int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
    }//end GIFView()

    private int movieWidth = 0;
    private int movieHeight = 0;

    public void setImageResource(int mvId){
        this.movieResourceID = mvId;
        movie = Movie.decodeStream(getResources().openRawResource(movieResourceID));
        movieWidth = Math.min(viewportWidth / 2, movie.width());
        movieHeight = Math.min(viewportHeight / 2, movie.height());
        requestLayout();
    }//end setImageResource()

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(movieWidth, movieHeight);
    }//end onMeasure()

    @Override
    protected void onDraw(Canvas canvas) {
//        float scaleX = (float) this.getWidth() / (float) movie.width();
//        float scaleY = (float) this.getHeight() / (float) movie.height();
        int posX = (getWidth() - movie.width()) / 2;
        if (scaleToFitLowerResolutions) posX = (viewportWidth - movie.width()) /2;
        //System.out.println("X:" + posX);
        int posY = 0;
        if (scaleToFitLowerResolutions) canvas.scale(SCALE_LOWRES_WIDTH, SCALE_LOWRES_HEIGHT);
        if (movie != null){
            updateAnimationTime();
            drawGif(canvas, posX, posY);
            invalidate();
        }//end if
        else drawGif(canvas, posX, posY);
    }//end onDraw()

    private void updateAnimationTime() {
        long now = android.os.SystemClock.uptimeMillis();
        if (movieStart == 0) movieStart = now;
        int dur = movie.duration();
        if (dur == 0) dur = DEFAULT_MOVIE_DURATION;
        currentAnimationTime = (int) ((now - movieStart) % dur);
    }//end updateAnimationTime()

    private void drawGif(Canvas canvas, final int posX, final int posY) {
        movie.setTime(currentAnimationTime);
        movie.draw(canvas, posX , posY);
    }//end drawGif()

}//end class GIFView