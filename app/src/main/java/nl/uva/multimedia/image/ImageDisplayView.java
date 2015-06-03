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
import android.widget.SeekBar;

/*
 * This is a View that displays incoming images.
 */
public class ImageDisplayView extends View implements ImageListener {

    public static SeekBar seekBar = null;

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
        // Dat hebben we net gedaan


        /* If there is an image to be drawn: */
        if (this.currentImage != null) {
            /* r=16, g=8, b=0*/
            int color = 16;

            int color_sum = 0;
            int[] color_values = new int[256];

            int color_max = 0;
            int mode = 0;


            /* Build an array of the green values */
            for(int i = 0; i < this.currentImage.length - 1; i++) {
                color_sum += 0xFF & (this.currentImage[i] >> color);

                if(++color_values[0xFF & (this.currentImage[i] >> color)] > color_max) {
                    color_max = color_values[0xFF & (this.currentImage[i] >> color)];
                    mode = 0xFF & (this.currentImage[i] >> color);
                }
            }

            int mean = color_sum / this.currentImage.length;

            int median = 0;
            int color_counter = 0;

            Paint paint = new Paint();
            switch (color) {
                case 0:
                    paint.setColor(Color.BLUE);
                    break;
                case 8:
                    paint.setColor(Color.GREEN);
                    break;
                case 16:
                    paint.setColor(Color.RED);
                    break;
            }


            int bins = (int) Math.pow(2, seekBar.getProgress());
            int binvalue = 0;

            int binHeight;
            int binWidth = 256 / bins;

            int drawWidth = this.imageWidth/bins;
            float drawHeight = color_values[mode]/this.imageHeight;

            for(int j = 0; j <= 255; j++) {
                color_counter += color_values[j];
                if(color_counter > Math.floor(this.currentImage.length / 2) && median ==0) {
                    median = j;
                }

                /*imHeight is the height of the histogram*/
                binvalue += color_values[j];
                if ((j+1) % binWidth == 0 ) {
                    binvalue/=binWidth;
                    binHeight = this.imageHeight-(int)(binvalue/drawHeight);
                    canvas.drawRect((float)((j+1)/binWidth-1)*drawWidth,(float)binHeight,(float)(j+1)/binWidth*drawWidth,(float)this.imageHeight,paint);
                    binvalue=0;
                }

            }


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
