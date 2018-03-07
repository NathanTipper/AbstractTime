package com.example.ntipp.abstracttime;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.Log;
import android.view.View;

import java.util.Calendar;

import static java.lang.Math.min;
import static java.lang.Math.max;
import static android.graphics.Color.rgb;

/**
 * Created by ntipp on 2018-03-06.
 */

public class ClockView extends View {
    int maxSecondColor = Color.WHITE;
    int minSecondColor = Color.BLACK;
    int maxMinuteColor = Color.WHITE;
    int minMinuteColor = Color.BLACK;
    int maxHourColor = Color.WHITE;
    int minHourColor = Color.BLACK;

    Paint paint;
    boolean initialized = false;

    int secondRed = 0;
    int secondBlue = 0;
    int secondGreen = 0;
    boolean secondAnimationForward = true;

    int maxFirstLevelRed = 158;
    int maxFirstLevelGreen = 199;
    int maxFirstLevelBlue = 255;
    int minuteRedInterval = 3;
    int minuteGreenInterval = 4;
    int minuteBlueInterval = 5;

    int minuteRed = 0;
    int minuteBlue = 0;
    int minuteGreen = 0;
    boolean minuteAnimateForward = false;

    int hourRed = 0;
    int hourBlue = 0;
    int hourGreen = 0;
    boolean hourAnimateForward = true;

    int second = 0;
    int previousMinute = 0;
    int minute = 0;
    int hour = 0;

    Calendar calendar;
    Path path;

    public ClockView(Context context) {
        super(context);
    }

    private void init(Canvas canvas) {
        paint = new Paint();
        path = new Path();
        calendar = Calendar.getInstance();
        int am_pm = calendar.get(Calendar.AM_PM);
        if(am_pm == 1)
            hourAnimateForward = false;

        hour = calendar.get(Calendar.HOUR);
        minute = calendar.get(Calendar.MINUTE);
        previousMinute = minute;

        if(minute != 0) {
            minuteRed = maxFirstLevelRed - (minute * 2);
            minuteBlue = 255;
            minuteGreen = maxFirstLevelGreen - minute;
        }


        paint.setStyle(Paint.Style.FILL);
        canvas.drawColor(Color.TRANSPARENT);

        initialized = true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if(!initialized)
            init(canvas);

        calendar = Calendar.getInstance();
        hour = calendar.get(Calendar.HOUR);
        minute = calendar.get(Calendar.MINUTE);

        drawBetelgeuse(canvas);
        drawAlphaCentauri(canvas);
        invalidate();
    }

    private void drawSun(Canvas canvas) {

    }

    private void drawAlphaCentauri(Canvas canvas) {
        float x = getWidth();
        float y = getHeight();
        path.moveTo(x / 2, y / 2);
        path.lineTo(x / 2, y);
        if(minute != previousMinute) {
            if (previousMinute == 0)
                minuteAnimateForward = true;
            else
                minuteAnimateForward = false;

            if (minuteAnimateForward) {
                minuteRed = min(minuteRed + minuteRedInterval, maxFirstLevelRed);
                minuteGreen = min(minuteGreen + minuteGreenInterval, maxFirstLevelGreen);
                minuteBlue = min(minuteBlue + minuteBlueInterval, maxFirstLevelBlue);
                if (minuteBlue == maxFirstLevelBlue && minuteGreen == maxFirstLevelGreen && minuteRed == maxFirstLevelRed) {
                    minuteAnimateForward = false;
                }

            } else if (minute == 0) {
                minuteBlue = 0;
                minuteGreen = 0;
                minuteBlue = 0;
            } else {
                minuteRed = max(minuteRed - minuteRedInterval, 0);
                minuteGreen = max(minuteGreen - minuteGreenInterval, 0);
            }
            Log.d("CLOCKVIEW", String.format("minuteRed = %d minuteBlue = %d minuteGreen = %d", minuteRed, minuteBlue, minuteGreen));
            previousMinute = minute;
        }

        paint.setColor(Color.GREEN);
        canvas.drawPath(path, paint);
        paint.setColor(rgb(minuteRed, minuteGreen, minuteBlue));
        canvas.drawCircle(x / 2, (y / 2) , min(x / 8, y / 8) , paint);

    }

    private void drawBetelgeuse(Canvas canvas) {
        int interval = 10;

        if(secondAnimationForward) {
            secondBlue = min(secondBlue + interval, 255);
            secondGreen = min(secondGreen + interval, 255);
            secondRed = min(secondRed + interval, 255);
            if(secondBlue >= 255)
                secondAnimationForward = !secondAnimationForward;
        }

        else {
            secondBlue = max(secondBlue - interval, 0);
            secondGreen = max(secondGreen - interval, 0);
            secondRed = max(secondRed - interval, 0);
            if(secondBlue <= 0)
                secondAnimationForward = !secondAnimationForward;
        }

        float x = getWidth();
        float y = getHeight();
        paint.setColor(rgb(secondRed, secondGreen, secondBlue));
        canvas.drawCircle(x / 2, (y - 200) , min(x / 16, y / 16) , paint);
    }
}
