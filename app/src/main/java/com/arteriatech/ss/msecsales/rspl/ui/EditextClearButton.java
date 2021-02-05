package com.arteriatech.ss.msecsales.rspl.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import androidx.appcompat.widget.AppCompatEditText;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.arteriatech.ss.msecsales.rspl.R;


/**
 * Created by opprime on 16-7-21.
 */
public class EditextClearButton extends AppCompatEditText {
    private Context mContext;
    private Bitmap mClearButton;
    private Paint mPaint;
    private boolean mClearStatus;
    private ClearButtonMode mClearButtonMode;
    private int mInitPaddingRight;
    private int mInitPadding=dp2px(10);
    private int mButtonPadding = dp2px(0);
    private boolean dontClearText=false;


    public EditextClearButton(Context context) {
        super(context);
        init(context, null);
    }


    public EditextClearButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public EditextClearButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attributeSet) {
        this.mContext = context;
        TypedArray typedArray = context.obtainStyledAttributes(attributeSet, R.styleable.EditTextClearButton);

        switch (typedArray.getInteger(R.styleable.EditTextClearButton_clearButtonMode, 0)) {
            case 1:
                mClearButtonMode = ClearButtonMode.ALWAYS;
                break;
            case 2:
                mClearButtonMode = ClearButtonMode.WHILEEDITING;
                break;
            case 3:
                mClearButtonMode = ClearButtonMode.UNLESSEDITING;
                break;
            default:
                mClearButtonMode = ClearButtonMode.NEVER;
                break;
        }

        int clearButton = typedArray.getResourceId(R.styleable.EditTextClearButton_clearButtonDrawable, android.R.drawable.ic_delete);
        dontClearText = typedArray.getBoolean(R.styleable.EditTextClearButton_clearButtonDontClearText, false);
        typedArray.recycle();
        mClearButton = ((BitmapDrawable) getDrawableCompat(clearButton)).getBitmap();

        mPaint = new Paint();
        mPaint.setAntiAlias(true);

        mInitPaddingRight = getPaddingRight();
    }

    private void buttonManager(Canvas canvas) {
        switch (mClearButtonMode) {
            case ALWAYS:
                drawBitmap(canvas, getRect(true));
                break;
            case WHILEEDITING:
                drawBitmap(canvas, getRect(hasFocus() && getText().length() > 0));
                break;
            case UNLESSEDITING:
                break;
            default:
                drawBitmap(canvas, getRect(false));
                break;
        }
    }

    private void setPadding(boolean isShow) {
        int paddingRight = mInitPaddingRight + (isShow ? mClearButton.getWidth() + mInitPadding + mButtonPadding : 0);
        setPadding(getPaddingLeft(), getPaddingTop(), paddingRight, getPaddingBottom());
    }

    private Rect getRect(boolean isShow) {
        int left, top, right, bottom;

        right = isShow ? getMeasuredWidth() + getScrollX() - mButtonPadding - mButtonPadding : 0;
        left = isShow ? right - mClearButton.getWidth() : 0;
        top = isShow ? (getMeasuredHeight() - mClearButton.getHeight()) / 2 : 0;
        bottom = isShow ? top + mClearButton.getHeight() : 0;

        setPadding(isShow);


        return new Rect(left, top, right, bottom);
    }

    private void drawBitmap(Canvas canvas, Rect rect) {
        if (rect != null) {
            canvas.drawBitmap(mClearButton, null, rect, mPaint);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.save();

        buttonManager(canvas);

        canvas.restore();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_UP:
                if (event.getX() - (getMeasuredWidth() - getPaddingRight()) >= 0) {
                    if (!dontClearText) {
                        setError(null);
                        this.setText("");
                    }
                }
                break;
        }

        return super.onTouchEvent(event);
    }

    private Drawable getDrawableCompat(int resourseId) {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            return getResources().getDrawable(resourseId, mContext.getTheme());
        } else {
            return getResources().getDrawable(resourseId);
        }
    }

    public void setButtonPadding(int buttonPadding) {
        this.mButtonPadding = dp2px(buttonPadding);
    }

    public void setClearButtonMode(ClearButtonMode clearButtonMode) {
        this.mClearButtonMode = clearButtonMode;
    }

    public boolean isShowing() {
        return mClearStatus;
    }

    public int dp2px(float dipValue) {
        final float scale = getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    public enum ClearButtonMode {
        NEVER, ALWAYS, WHILEEDITING, UNLESSEDITING
    }
}