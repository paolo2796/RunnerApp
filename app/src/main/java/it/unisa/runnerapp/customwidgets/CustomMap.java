package it.unisa.runnerapp.customwidgets;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;

/**
 * Created by Paolo on 04/02/2018.
 */

public class CustomMap extends com.google.android.gms.maps.MapView {

    RectF rectf = new RectF();
    private int cornerradiusx = 50;
    private int corneradiusy = 50;

    private static final String MESSAGE_CUSTOMAP="MessageCustomMap";

    public CustomMap(Context context) {

        super(context);

    }

    public CustomMap(Context context, AttributeSet attrs) {

        super(context, attrs);

    }
    public CustomMap(Context context, AttributeSet attrs, int styleattr) {
        super(context, attrs, styleattr);
    }

    @Override
    public void draw(Canvas canvas) {

        super.draw(canvas);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        rectf.set(0, 0, getMinimumWidth(), getMinimumHeight());
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        Path path = new Path();
        int count = canvas.save();
        path.addRoundRect(rectf, cornerradiusx, corneradiusy, Path.Direction.CW);
        canvas.clipPath(path);
        super.dispatchDraw(canvas);
        canvas.restoreToCount(count);
    }

}