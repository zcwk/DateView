package com.example.zdzyc.dateviewpage;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * TODO: document your custom view class.
 */
public class DateViewPage extends View {
    OnChangeListener onChangeListener;
    private final String TAG = "DateViewPage";

    private String mDate;//当前日期

    private String mCurrentDateString; //当前日期
    private String mCurrentWeekString; //当前日期
    private String mLeftDateString;//前一天的日期
    private String mLeftWeekString;//前一天的日期
    private String mRightDateString;//后一天的日期
    private String mRightWeekString;//后一天的日期
    private String mLeft2DateString;//大前天的日期
    private String mLeft2WeekString;//大前天的日期
    private String mRight2DateString;//大后天的日期
    private String mRight2WeekString;//大后天的日期

    private int mCurrentDateStringColor = Color.RED; // 当前日期的颜色
    private int mLeftDateStringColor = Color.RED;//前一天的日期的颜色
    private int mRightDateStringColor = Color.RED;//前一天的日期的颜色

    private float mCurrentStringSize = 0;//当前日期字体的大小
    private float mLeftRightStringSize = 0;//前一天和后一天的字体的大小

    private Drawable mLeftDrawable;
    private Drawable mRightDrawable;

    private boolean isDisplayWeek;//是否显示星期数

    private TextPaint mCurrentDateStringPaint;
    private float mCurrentDateStringWidth;
    private float mCurrentDateStringHeight;

    private TextPaint mRightDateStringPaint;
    private float mRightDateStringWidth;
    private float mRightDateStringHeight;

    private TextPaint mLeftDateStringPaint;
    private float mLeftDateStringWidth;
    private float mLeftDateStringHeight;

    private String specidate;
    private int current_x;//当前的坐标
    private int spaceWidth;

    public DateViewPage(Context context) {
        super(context);
        init(null, 0);
    }

    public DateViewPage(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public DateViewPage(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {
        // Load attributes
        final TypedArray a = getContext().obtainStyledAttributes(
                attrs, R.styleable.DateViewPage, defStyle, 0);

        mCurrentDateStringColor = a.getColor(
                R.styleable.DateViewPage_currentStringColor, mCurrentDateStringColor);
        mLeftDateStringColor = a.getColor(
                R.styleable.DateViewPage_leftStringColor,
                mLeftDateStringColor);
        mRightDateStringColor = a.getColor(
                R.styleable.DateViewPage_rightStringColor,
                mRightDateStringColor);
        // Use getDimensionPixelSize or getDimensionPixelOffset when dealing with
        // values that should fall on pixel boundaries.
        mCurrentStringSize = a.getDimension(
                R.styleable.DateViewPage_CurrentStringSize,
                mCurrentStringSize);
        mLeftRightStringSize = a.getDimension(
                R.styleable.DateViewPage_LeftRightStringSize,
                mLeftRightStringSize);

        isDisplayWeek = a.getBoolean(R.styleable.DateViewPage_isDisplayWeek, isDisplayWeek);

        if (a.hasValue(R.styleable.DateViewPage_leftDrawable)) {
            mLeftDrawable = a.getDrawable(
                    R.styleable.DateViewPage_leftDrawable);
            mLeftDrawable.setCallback(this);
        }
        if (a.hasValue(R.styleable.DateViewPage_rightDrawable)) {
            mRightDrawable = a.getDrawable(
                    R.styleable.DateViewPage_rightDrawable);
            mRightDrawable.setCallback(this);
        }

        a.recycle();

        // Set up a default TextPaint object
        mCurrentDateStringPaint = new TextPaint();
        mCurrentDateStringPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        mCurrentDateStringPaint.setTextAlign(Paint.Align.LEFT);

        mRightDateStringPaint = new TextPaint();
        mRightDateStringPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        mRightDateStringPaint.setTextAlign(Paint.Align.LEFT);

        mLeftDateStringPaint = new TextPaint();
        mLeftDateStringPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        mLeftDateStringPaint.setTextAlign(Paint.Align.LEFT);

        initDate();
        // Update TextPaint and text measurements from attributes
        invalidateTextPaintAndMeasurements();

        Log.v(TAG, "初始化完成");

    }

    private void initDate() {
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        specidate = sdf.format(date);
        setDate();
    }

    private void setDate() {
        mDate = getSpecifiedDay(specidate, 0)[0];
        mLeftDateString = getSpecifiedDay(specidate, -1)[1];
        mLeftWeekString = getSpecifiedDay(specidate, -1)[2];
        mRightDateString = getSpecifiedDay(specidate, 1)[1];
        mRightWeekString = getSpecifiedDay(specidate, 1)[2];
        mLeft2DateString = getSpecifiedDay(specidate, -2)[1];
        mLeft2WeekString = getSpecifiedDay(specidate, -2)[2];
        mRight2DateString = getSpecifiedDay(specidate, 2)[1];
        mRight2WeekString = getSpecifiedDay(specidate, 2)[2];
        mCurrentDateString = getSpecifiedDay(specidate, 0)[1];
        mCurrentWeekString = getSpecifiedDay(specidate, 0)[2];
    }

    /**
     * 获得指定日期
     *
     * @param specifiedDay 当前的日期
     * @param spaceDay     获得前几天或者后几天 负数为后几天
     * @return
     */
    public String[] getSpecifiedDay(String specifiedDay, int spaceDay) {//可以用new Date().toLocalString()传递参数
        Calendar c = Calendar.getInstance();
        Date date = null;
        try {
            date = new SimpleDateFormat("yyyy-MM-dd").parse(specifiedDay);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        c.setTime(date);
        int day = c.get(Calendar.DATE);
        c.set(Calendar.DATE, day + spaceDay);
        String dayBefore[] = new String[3];
        dayBefore[0] = new SimpleDateFormat("yyyy-MM-dd").format(c
                .getTime());
        if (!isDisplayWeek) {
            dayBefore[1] = new SimpleDateFormat("MM月dd日").format(c
                    .getTime());
        } else {
            String[] weekOfDays = {"星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};
            int w = c.get(Calendar.DAY_OF_WEEK) - 1;
            if (w < 0) {
                w = 0;
            }
            dayBefore[1] = weekOfDays[w];
        }
        String[] weekOfDays = {"星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};
        int w = c.get(Calendar.DAY_OF_WEEK) - 1;
        if (w < 0) {
            w = 0;
        }
        dayBefore[2] = weekOfDays[w];

        return dayBefore;
    }

    private void invalidateTextPaintAndMeasurements() {
        mCurrentDateStringPaint.setTextSize(mCurrentStringSize);
        mCurrentDateStringPaint.setColor(mCurrentDateStringColor);
        mCurrentDateStringWidth = mCurrentDateStringPaint.measureText(mCurrentDateString);
        Paint.FontMetrics fontMetrics = mCurrentDateStringPaint.getFontMetrics();
        mCurrentDateStringHeight = fontMetrics.bottom;

        mLeftDateStringPaint.setTextSize(mLeftRightStringSize);
        mLeftDateStringPaint.setColor(mLeftDateStringColor);
        mLeftDateStringWidth = mLeftDateStringPaint.measureText(mLeftDateString);
        Paint.FontMetrics fontMetrics1 = mLeftDateStringPaint.getFontMetrics();
        mLeftDateStringHeight = fontMetrics1.bottom;

        mRightDateStringPaint.setTextSize(mLeftRightStringSize);
        mRightDateStringPaint.setColor(mRightDateStringColor);
        mRightDateStringWidth = mRightDateStringPaint.measureText(mRightDateString);
        Paint.FontMetrics fontMetrics2 = mRightDateStringPaint.getFontMetrics();
        mRightDateStringHeight = fontMetrics2.bottom;
    }

    private int paddingLeft;
    private int paddingTop;
    private int paddingRight;
    private int paddingBottom;
    private int leftImagepadding;
    private int leftImageWidth;
    private int leftImageHeight;

    private Rect mLeftDrawableRect;
    private Rect mRightDrawableRect;

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // TODO: consider storing these as member variables to reduce
        // allocations per draw cycle.
        paddingLeft = getPaddingLeft();
        paddingTop = getPaddingTop();
        paddingRight = getPaddingRight();
        paddingBottom = getPaddingBottom();

        int contentWidth = getWidth() - paddingLeft - paddingRight;
        int contentHeight = getHeight() - paddingTop - paddingBottom;

        leftImagepadding = 20;
        leftImageWidth = getHeight() >> 1;
        leftImageHeight = getHeight() - (leftImagepadding << 1);

        spaceWidth = ((contentWidth - (leftImageWidth << 1) - (leftImagepadding << 1)) >> 1) - ((int) mCurrentDateStringWidth >> 1);

        mLeftDrawableRect = new Rect(paddingLeft + leftImagepadding, paddingTop + leftImagepadding,
                paddingLeft + leftImagepadding + leftImageWidth, paddingTop + contentHeight - leftImagepadding);

        mRightDrawableRect = new Rect(contentWidth - leftImageWidth, paddingTop + leftImagepadding,
                contentWidth - leftImagepadding, paddingTop + contentHeight - leftImagepadding);
        // Draw the text.

        canvas.drawText(mLeft2DateString,
                leftImageWidth + leftImagepadding + paddingLeft + current_x - spaceWidth,
                paddingTop + (contentHeight >> 1) + mLeftDateStringHeight,
                mLeftDateStringPaint);
        canvas.drawText(mLeftDateString,
                leftImageWidth + leftImagepadding + paddingLeft + current_x,
                paddingTop + (contentHeight >> 1) + mLeftDateStringHeight,
                mLeftDateStringPaint);
        canvas.drawText(mCurrentDateString,
                paddingLeft + current_x + (contentWidth - mCurrentDateStringWidth) / 2,
                paddingTop + (contentHeight >> 1) + mCurrentDateStringHeight,
                mCurrentDateStringPaint);
        canvas.drawText(mRightDateString,
                -leftImagepadding - leftImageWidth + paddingRight + current_x + (contentWidth - mRightDateStringWidth),
                paddingTop + (contentHeight >> 1) + mRightDateStringHeight,
                mRightDateStringPaint);
        canvas.drawText(mRight2DateString,
                -leftImagepadding - leftImageWidth + paddingRight + current_x + spaceWidth + (contentWidth - mRightDateStringWidth),
                paddingTop + (contentHeight >> 1) + mRightDateStringHeight,
                mRightDateStringPaint);

        // Draw the example drawable on top of the text.
        if (mLeftDrawable != null) {
            mLeftDrawable.setBounds(mLeftDrawableRect);
            mLeftDrawable.draw(canvas);
        }
        if (mRightDrawable != null) {
            mRightDrawable.setBounds(mRightDrawableRect);
            mRightDrawable.draw(canvas);
        }

        if (isLeftMove) {
            current_x += spaceWidth / 10;
            if (current_x > spaceWidth) {
                current_x = 0;
                isLeftMove = false;
                yesterday();
            }
            invalidate();
        } else if (isRighttMove) {
            current_x -= spaceWidth / 10;
            if (current_x < -spaceWidth) {
                current_x = 0;
                isRighttMove = false;
                tomorrow();
            }
            invalidate();
        }


    }

    private int s_x = 0, s_y = 0, e_x = 0, e_y = 0, m_x = 0, m_y = 0;
    boolean isLeftMove;
    boolean isRighttMove;

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        int action = event.getAction();

        if (action == MotionEvent.ACTION_DOWN) {
            s_x = (int) event.getRawX();
            s_y = (int) event.getRawY();
            current_x = 0;
        } else if (action == MotionEvent.ACTION_MOVE) {
            m_x = (int) event.getRawX();
            m_y = (int) event.getRawY();
            if (Math.abs(current_x) < spaceWidth) {
                current_x = m_x - s_x;
            }
        } else if (action == MotionEvent.ACTION_UP) {
            e_x = (int) event.getX();
            e_y = (int) event.getY();
            if (Math.abs(current_x) >= (spaceWidth >> 1)) {
                if (m_x > s_x) {
                    yesterday();
                } else {
                    tomorrow();
                }
            } else if (mLeftDrawableRect.contains(e_x, e_y, e_x, e_y)) {
                isLeftMove = true;
            } else if (mRightDrawableRect.contains(e_x, e_y, e_y, e_y)) {
                isRighttMove = true;
            }
            current_x = 0;
        }
        invalidate();
        return true;
    }

    private void tomorrow() {
        specidate = getSpecifiedDay(specidate, 1)[0];
        setDate();
        Log.v(TAG, "明天");
        this.onChangeListener.tomorrow();
        this.onChangeListener.onChange();
    }

    private void yesterday() {
        specidate = getSpecifiedDay(specidate, -1)[0];
        setDate();
        Log.v(TAG, "昨天");
        this.onChangeListener.yesterday();
        this.onChangeListener.onChange();
    }

    public String getmDate() {
        return mDate;
    }

    public void setmDate(String mDate) {
        this.mDate = mDate;
    }

    public String getmCurrentDateString() {
        return mCurrentDateString;
    }

    public void setmCurrentDateString(String mCurrentDateString) {
        this.mCurrentDateString = mCurrentDateString;
    }

    public String getmCurrentWeekString() {
        return mCurrentWeekString;
    }

    public void setmCurrentWeekString(String mCurrentWeekString) {
        this.mCurrentWeekString = mCurrentWeekString;
    }

    public String getmLeftDateString() {
        return mLeftDateString;
    }

    public void setmLeftDateString(String mLeftDateString) {
        this.mLeftDateString = mLeftDateString;
    }

    public String getmLeftWeekString() {
        return mLeftWeekString;
    }

    public void setmLeftWeekString(String mLeftWeekString) {
        this.mLeftWeekString = mLeftWeekString;
    }

    public String getmRightDateString() {
        return mRightDateString;
    }

    public void setmRightDateString(String mRightDateString) {
        this.mRightDateString = mRightDateString;
    }

    public String getmRightWeekString() {
        return mRightWeekString;
    }

    public void setmRightWeekString(String mRightWeekString) {
        this.mRightWeekString = mRightWeekString;
    }

    public boolean isDisplayWeek() {
        return isDisplayWeek;
    }

    public void setIsDisplayWeek(boolean isDisplayWeek) {
        this.isDisplayWeek = isDisplayWeek;
    }

    public OnChangeListener getOnChangeListener() {
        return onChangeListener;
    }

    public void setOnChangeListener(OnChangeListener onChangeListener) {
        this.onChangeListener = onChangeListener;
    }

    public interface OnChangeListener {
        abstract void onChange();//有日期变化

        abstract void tomorrow();//选择了下一天

        abstract void yesterday();//选择了上一天
    }
}
