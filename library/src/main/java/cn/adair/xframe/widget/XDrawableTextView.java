package cn.adair.xframe.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import androidx.annotation.IntDef;
import androidx.appcompat.widget.AppCompatTextView;
import android.util.AttributeSet;
import android.view.Gravity;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import cn.adair.xframe.R;

/**
 * 解决TextView中Drawable不与文字居中的问题
 */
public class XDrawableTextView extends AppCompatTextView {

    private Drawable[] drawables;
    private int[] widths;
    private int[] heights;

    public static final int LEFT = 0;
    public static final int TOP = 1;
    public static final int RIGHT = 2;
    public static final int BOTTOM = 3;

    @IntDef({
            LEFT,
            TOP,
            RIGHT,
            BOTTOM
    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface DrawGravity {
    }

    public XDrawableTextView(Context context) {
        this(context, null);
    }

    public XDrawableTextView(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.textViewStyle);
    }

    public XDrawableTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        drawables = new Drawable[4];
        widths = new int[4];
        heights = new int[4];
        setGravity(Gravity.CENTER);

        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.XDrawableTextView);
        drawables[0] = array.getDrawable(R.styleable.XDrawableTextView_leftDrawable);
        drawables[1] = array.getDrawable(R.styleable.XDrawableTextView_topDrawable);
        drawables[2] = array.getDrawable(R.styleable.XDrawableTextView_rightDrawable);
        drawables[3] = array.getDrawable(R.styleable.XDrawableTextView_bottomDrawable);

        widths[0] = array.getDimensionPixelSize(R.styleable.XDrawableTextView_leftDrawableWidth, 0);
        widths[1] = array.getDimensionPixelSize(R.styleable.XDrawableTextView_topDrawableWidth, 0);
        widths[2] = array.getDimensionPixelSize(R.styleable.XDrawableTextView_rightDrawableWidth, 0);
        widths[3] = array.getDimensionPixelSize(R.styleable.XDrawableTextView_bottomDrawableWidth, 0);

        heights[0] = array.getDimensionPixelSize(R.styleable.XDrawableTextView_leftDrawableHeight, 0);
        heights[1] = array.getDimensionPixelSize(R.styleable.XDrawableTextView_topDrawableHeight, 0);
        heights[2] = array.getDimensionPixelSize(R.styleable.XDrawableTextView_rightDrawableHeight, 0);
        heights[3] = array.getDimensionPixelSize(R.styleable.XDrawableTextView_bottomDrawableHeight, 0);

        array.recycle();
    }

    public void setDrawable(@DrawGravity int gravity, Drawable drawable, int width, int height) {
        drawables[gravity] = drawable;
        widths[gravity] = width;
        heights[gravity] = height;
        postInvalidate();
    }

    public void setDrawables(Drawable[] drawables, int[] widths, int[] heights) {
        if (drawables != null && drawables.length >= 4 && widths != null && widths.length >= 4 && heights != null && heights.length >= 4) {
            this.drawables = drawables;
            this.widths = widths;
            this.heights = heights;
            postInvalidate();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int drawablePadding = getCompoundDrawablePadding();
        translateText(canvas, drawablePadding);
        super.onDraw(canvas);

        float centerX = (getWidth() + getPaddingLeft() - getPaddingRight()) / 2;
        float centerY = (getHeight() + getPaddingTop() - getPaddingBottom()) / 2;

        float halfTextWidth = getPaint().measureText(getText().toString()) / 2;
        Paint.FontMetrics fontMetrics = getPaint().getFontMetrics();
        float halfTextHeight = (fontMetrics.descent - fontMetrics.ascent) / 2;

        if (drawables[0] != null) {
            int left = (int) (centerX - drawablePadding - halfTextWidth - widths[0]);
            int top = (int) (centerY - heights[0] / 2);
            drawables[0].setBounds(left, top, left + widths[0], top + heights[0]);
            canvas.save();
            drawables[0].draw(canvas);
            canvas.restore();
        }

        if (drawables[2] != null) {
            int left = (int) (centerX + halfTextWidth + drawablePadding);
            int top = (int) (centerY - heights[2] / 2);
            drawables[2].setBounds(left, top, left + widths[2], top + heights[2]);
            canvas.save();
            drawables[2].draw(canvas);
            canvas.restore();
        }

        if (drawables[1] != null) {
            int left = (int) (centerX - widths[1] / 2);
            int bottom = (int) (centerY - halfTextHeight - drawablePadding);
            drawables[1].setBounds(left, bottom - heights[1], left + widths[1], bottom);
            canvas.save();
            drawables[1].draw(canvas);
            canvas.restore();
        }


        if (drawables[3] != null) {
            int left = (int) (centerX - widths[3] / 2);
            int top = (int) (centerY + halfTextHeight + drawablePadding);
            drawables[3].setBounds(left, top, left + widths[3], top + heights[3]);
            canvas.save();
            drawables[3].draw(canvas);
            canvas.restore();
        }
    }

    private void translateText(Canvas canvas, int drawablePadding) {
        int translateWidth = 0;
        if (drawables[0] != null && drawables[2] != null) {
            translateWidth = (widths[0] - widths[2]) / 2;
        } else if (drawables[0] != null) {
            translateWidth = (widths[0] + drawablePadding) / 2;
        } else if (drawables[2] != null) {
            translateWidth = -(widths[2] + drawablePadding) / 2;
        }

        int translateHeight = 0;
        if (drawables[1] != null && drawables[3] != null) {
            translateHeight = (heights[1] - heights[3]) / 2;
        } else if (drawables[1] != null) {
            translateHeight = (heights[1] + drawablePadding) / 2;
        } else if (drawables[3] != null) {
            translateHeight = -(heights[3] - drawablePadding) / 2;
        }

        canvas.translate(translateWidth, translateHeight);
    }

}
