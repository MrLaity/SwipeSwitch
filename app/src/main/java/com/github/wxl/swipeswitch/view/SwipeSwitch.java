package com.github.wxl.swipeswitch.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Looper;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

/**
 * Created by wxl on 15/9/23.
 */
public class SwipeSwitch extends View {

    private static final String SWIPE_LOGIN_TEXT = "スワイプしてログイン";

    private static final int BACK_COLOR = Color.parseColor("#ECEDEE");
    private static final int FRONT_COLOR = Color.parseColor("#C3C4C5");

    private int width;
    private int height;

    private Paint paint;
    private Paint textPaint;
    private float text_X;
    private float text_Y;

    private RectF frontCircleRect;
    private RectF middleCircleRect;
    private RectF backCircleRect;

    private boolean isOpen;
    private int alpha;
    private int max_left;
    private int min_left;
    private int frontRect_left;
    private int frontRect_left_begin;

    private int eventStartX;
    private int eventLastX;
    private int diffX;
    private boolean slideable;

    private Bitmap bitmap;

    private StatusListener listener;

    public SwipeSwitch(Context context) {
        super(context);
        initDrawingVal();
    }

    public SwipeSwitch(Context context, AttributeSet attrs) {
        super(context, attrs);
        initDrawingVal();

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        this.width = w;
        this.height = h;
        initDrawingVal();
    }

    private void initDrawingVal() {
        this.slideable = true;

        this.paint = new Paint();
        this.paint.setAntiAlias(true);

        this.textPaint = new Paint();
        this.textPaint.setAntiAlias(true);
        this.textPaint.setTextSize(50);
        this.textPaint.setStyle(Paint.Style.STROKE);
        this.textPaint.setFakeBoldText(true);

        this.frontCircleRect = new RectF();
        this.middleCircleRect = new RectF();
        this.backCircleRect = new RectF(0, 0, width, height);

        this.min_left = 15;
        this.max_left = width - (height - 30) - 15;

        if (this.isOpen) {
            this.frontRect_left = this.max_left;
            this.alpha = 255;
        } else {
            this.frontRect_left = 15;
            this.alpha = 0;
        }

        this.frontRect_left_begin = this.frontRect_left;

        calTextStartPoint(SWIPE_LOGIN_TEXT, textPaint);

    }

    private void calTextStartPoint(String text, Paint paint) {
        if (text == null) return;
        final Paint.FontMetrics fontMetrics = paint.getFontMetrics();
        final float textWidth = paint.measureText(text);
        final float textHeight = fontMetrics.bottom + fontMetrics.top;

        text_X = this.backCircleRect.centerX() - textWidth / 2f;
        text_Y = this.backCircleRect.centerY() - textHeight / 2f;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int radius = (int) this.backCircleRect.height() / 2;

        //draw back circleRect
        this.paint.setColor(BACK_COLOR);
        canvas.drawRoundRect(this.backCircleRect, (float) radius, (float) radius, this.paint);

        //draw black text
        this.textPaint.setColor(Color.BLACK);
        canvas.drawText(SWIPE_LOGIN_TEXT, text_X, text_Y, textPaint);

        //draw middle circleRect
        this.paint.setColor(FRONT_COLOR);
        this.paint.setAlpha(this.alpha);
        float middleCircleRectWidth = this.frontRect_left + this.backCircleRect.height() - 15;
        float middleCircleRectHeight = this.backCircleRect.height();
        middleCircleRect.set(0, 0, middleCircleRectWidth, middleCircleRectHeight);
        canvas.drawRoundRect(this.middleCircleRect, (float) radius, (float) radius, this.paint);

        //draw white text
        this.textPaint.setColor(Color.WHITE);
        String whiteText = subTextWithRect(this.textPaint, SWIPE_LOGIN_TEXT, this.middleCircleRect);
        canvas.drawText(whiteText, text_X, text_Y, textPaint);

        this.frontCircleRect.set(this.frontRect_left, 15, this.frontRect_left + this.backCircleRect.height() - 30, this.backCircleRect.height() - 15);
        this.paint.setColor(Color.YELLOW);
        canvas.drawRoundRect(this.frontCircleRect, (float) radius, (float) radius, this.paint);

        if (this.bitmap != null) {
            drawBitmapCenter(canvas, this.bitmap, this.frontCircleRect.centerX(), this.frontCircleRect.centerY(), 1, this.paint);
        }

    }

    private String subTextWithRect(Paint paint, String text, RectF rectF) {
        float rectWidth = rectF.width();
        final float textWidth = paint.measureText(text) + text_X;
        if (textWidth < rectWidth) {
            return text;
        } else {
            if (rectWidth < text_X) {
                return "";
            }
            final float singleTextWidth = paint.measureText(text, 0, 1);
            int endIndex = (int) ((rectWidth - text_X) / singleTextWidth);
            if (endIndex > 0) {
                endIndex -= 1;
            }
            return text.substring(0, endIndex);
        }
    }

    public static void drawBitmapCenter(Canvas canvas, Bitmap bitmap, float posX, float posY, float scale, Paint paint) {
        final Matrix matrix = new Matrix();
        if (bitmap != null) {
            matrix.setTranslate(-bitmap.getWidth() / 2, -bitmap.getHeight() / 2);
            matrix.postScale(scale, scale);
            matrix.postTranslate(posX, posY);
            canvas.drawBitmap(bitmap, matrix, paint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!this.slideable) {
            return super.onTouchEvent(event);
        } else {
            int action = MotionEventCompat.getActionMasked(event);
            switch (action) {
                case MotionEvent.ACTION_DOWN:
                    this.eventStartX = (int) event.getRawX();
                    break;
                case MotionEvent.ACTION_UP:
                    int wholeX = (int) (event.getRawX() - (float) this.eventStartX);
                    if (Math.abs(wholeX) < 5) {
                        break;
                    }
                    this.frontRect_left_begin = this.frontRect_left;
                    boolean toRight = this.frontRect_left_begin > this.max_left / 2;
                    this.moveToDest(toRight);
                    break;
                case MotionEvent.ACTION_MOVE:
                    this.eventLastX = (int) event.getRawX();
                    this.diffX = this.eventLastX - this.eventStartX;
                    if (Math.abs(diffX) < 5) {
                        break;
                    }
                    int tempX = this.diffX + this.frontRect_left_begin;
                    tempX = tempX > this.max_left ? this.max_left : tempX;
                    tempX = tempX < this.min_left ? this.min_left : tempX;
                    if (tempX >= this.min_left && tempX <= this.max_left) {
                        this.frontRect_left = tempX;
                        this.alpha = (int) (255.0F * (float) tempX / (float) this.max_left);
                        this.invalidateView();
                    }
            }

            return true;
        }
    }

    public void moveToDest(final boolean toRight) {
        ValueAnimator toDestAnim = ValueAnimator.ofInt(new int[]{this.frontRect_left, toRight ? this.max_left : this.min_left});
        toDestAnim.setDuration(500L);
        toDestAnim.setInterpolator(new AccelerateDecelerateInterpolator());
        toDestAnim.start();
        toDestAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator animation) {
                SwipeSwitch.this.frontRect_left = ((Integer) animation.getAnimatedValue()).intValue();
                SwipeSwitch.this.alpha = (int) (255.0F * (float) SwipeSwitch.this.frontRect_left / (float) SwipeSwitch.this.max_left);
                SwipeSwitch.this.invalidateView();
            }
        });
        toDestAnim.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animation) {
                if (toRight) {
                    SwipeSwitch.this.isOpen = true;
                    if(SwipeSwitch.this.listener != null) {
                        SwipeSwitch.this.listener.statusOpen();
                    }

                    SwipeSwitch.this.frontRect_left_begin = SwipeSwitch.this.max_left;
                } else {
                    SwipeSwitch.this.isOpen = false;
                    if(SwipeSwitch.this.listener != null) {
                        SwipeSwitch.this.listener.statusClose();
                    }

                    SwipeSwitch.this.frontRect_left_begin = SwipeSwitch.this.min_left;
                }

            }
        });
    }

    private void invalidateView() {
        if (Looper.getMainLooper() == Looper.myLooper()) {
            this.invalidate();
        } else {
            this.postInvalidate();
        }

    }

    public void setSlideable(boolean slideable) {
        this.slideable = slideable;
    }

    public void setStatusListener(StatusListener listener) {
        this.listener = listener;
    }

    public void setFrontBitmap(int resId) {
        bitmap = BitmapFactory.decodeResource(getResources(), resId);
    }

    public interface StatusListener {
        void statusOpen();
        void statusClose();
    }
}
