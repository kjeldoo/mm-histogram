/*
 * Framework code written for the Multimedia course taught in the first year
 * of the UvA Informatica bachelor.
 *
 * Nardi Lam, 2015 (based on code by I.M.J. Kamps, S.J.R. van Schaik, R. de Vries, 2013)
 */

package nl.uva.multimedia.image;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;
import android.graphics.Paint;
import android.graphics.Color;

/*
 * This is a View that displays incoming images.
 */
public class ImageDisplayView extends View implements ImageListener {

    /*** Constructors ***/

    public ImageDisplayView(Context context) {
        super(context);
    }

    public ImageDisplayView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ImageDisplayView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    /*** Image drawing ***/

    private int[] currentImage = null;
    private int imageWidth, imageHeight;

    @Override
    public void onImage(int[] argb, int width, int height) {
        /* When we recieve an image, simply store it and invalidate the View so it will be
         * redrawn. */
        this.currentImage = argb;
        this.imageWidth = width;
        this.imageHeight = height;
        this.invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // TODO: Hier wordt een afbeelding op het scherm laten zien!
        // Je zou hier dus code kunnen plaatsen om iets anders weer te geven.


        /* If there is an image to be drawn: */
        if (this.currentImage != null) {
            /* Center the image... */
            int left = (this.getWidth() - this.imageWidth) / 2;
            int top = (this.getHeight() - this.imageHeight) / 2;

            /* ...and draw it. */
            canvas.drawBitmap(this.currentImage, 0, this.imageWidth, left, top, this.imageWidth,
                    this.imageHeight, true, null);

            int g_sum = 0;
            int[] g_values = new int[256];

            int g_max = 0;
            int mode = 0;

            for(int i = 0; i < this.currentImage.length - 1; i++) {
                g_sum += 0xFF & (this.currentImage[i] >> 8);

                if(++g_values[0xFF & (this.currentImage[i] >> 8)] > g_max) {
                    g_max = g_values[0xFF & (this.currentImage[i] >> 8)];
                    mode = 0xFF & (this.currentImage[i] >> 8);
                }
            }

            int mean = g_sum / this.currentImage.length;

            int median = 0;
            int g_counter = 0;

            for(int j = 0; j < 255; j++) {
                g_counter += g_values[j];
                if(g_counter > Math.floor(this.currentImage.length / 2)) {
                    median = j;
                    break;
                }
            }

            Paint paint = new Paint();

            paint.setColor(Color.BLACK);
            paint.setTextSize(40);
            canvas.drawText("Mean: " + mean, 10, 45, paint);
            canvas.drawText("Median: " + median, 10, 85, paint);
            canvas.drawText("Mode: " + mode, 10, 125, paint);


        }
    }

    /*** Source selection ***/

    private ImageSource source = null;

    public void setImageSource(ImageSource source) {
        if (this.source != null) {
            this.source.setOnImageListener(null);
        }
        source.setOnImageListener(this);
        this.source = source;
    }

    public ImageSource getImageSource() {
        return this.source;
    }

}
