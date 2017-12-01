package org.inspirecenter.amazechallenge.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Movie;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public class GIFView extends View {

    private static final int DEFAULT_MOVIE_DURATION = 1000;
    private int movieResourceID;
    private Movie movie;
    private long movieStart = 0;
    private int currentAnimationTime = 0;
    private boolean isPlaying = false;
    private float lesserGradientScale = 1;

    public GIFView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
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
        movieWidth = movie.width();
        movieHeight = movie.height();
        requestLayout();
    }//end setImageResource()

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//Log.d("aMaze", "widthMeasureSpec: " + widthMeasureSpec + ", heightMeasureSpec: " + heightMeasureSpec);
        int parentWidth = MeasureSpec.getSize(widthMeasureSpec);
        int parentHeight = MeasureSpec.getSize(heightMeasureSpec);
//Log.d("aMaze", "parentWidth: " + parentWidth+ ", parentHeight: " + parentHeight);
//        this.setMeasuredDimension(parentWidth, parentHeight);
        setMeasuredDimension(movieWidth, movieHeight);
    }//end onMeasure()

    @Override
    protected void onDraw(Canvas canvas) {
        float scaleX = (float) this.getWidth() / (float) movie.width();
        float scaleY = (float) this.getHeight() / (float) movie.height();
Log.d("aMaze", "w/h: " + getWidth() + "," + getHeight() + "  || movie: " + movie.width() + "," + movie.height());
Log.d("aMaze", "scaleX: " + scaleX + ", scaleY: " + scaleY);
//        lesserGradientScale = Math.min(scaleX, scaleY);
//        canvas.scale(lesserGradientScale, lesserGradientScale);

        int posX = (getWidth() - movie.width()) / 2;
        int posY = 0;
        drawGif(canvas, posX, posY);
        if (movie != null){
            if (isPlaying) {            //Important Note: Play the GIF only when it is visible, otherwise the application will slow down significantly.
                updateAnimationTime();
                drawGif(canvas, posX, posY);
                invalidate();
            }//end if isPlaying
        }//end if
        else         drawGif(canvas, posX, posY);

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
        //System.out.println((canvas.getWidth() - movie.width()) / 2);
        //canvas.restore();
    }//end drawGif()

    public void play() { isPlaying = true; }

    public void stop() { isPlaying = false; }

}//end class GIFView