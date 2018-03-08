package com.example.ntipp.abstracttime;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
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
    Paint paint;
    boolean initialized = false;

    int minSecondRedLevel = 227;
    int minSecondGreenLevel = 229;
    int minSecondBlueLevel = 0;
    int maxSecondRedLevel = 240;
    int maxSecondGreenLevel = 242;
    int maxSecondBlueLevel = 150;

    int secondRedAnimationInterval = 1;
    int secondGreenAnimationInterval = 1;
    int secondBlueAnimationInterval = 4;

    int secondRed = 0;
    int secondBlue = 0;
    int secondGreen = 0;
    boolean secondAnimationForward;

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
    boolean alphaColorAnimation = false;

    int hourRed = 0;
    int hourBlue = 0;
    int hourGreen = 0;
    boolean hourAnimateForward = true;
    boolean sunColorAnimation = false;

    int sunRedAnimationInterval = 20;
    int sunGreenAnimationInterval = 20;
    int sunBlueAnimationInterval = 10;

    int previousMinute = 0;
    int minute = 0;
    int hour = 0;

    Calendar calendar;
    Path path;

    int betelgeuseX;
    int betelgeuseY;
    int betelgeuseRadius;

    int alphaCentX;
    int alphaCentY;
    int alphaCentRadius;

    int sunX;
    int sunY;

    int sunRadius = 200;

    boolean alphaPathAnimating = false;
    float alphaPathAnimationInterval = 3.5f;
    float alphaLineLength = 100f;
    int alphaPathAnimationColor = Color.YELLOW;
    Point alphaPathEnd;
    Point alphaPathStart;
    Point alphaPathCurrent;

    public ClockView(Context context) {
        super(context);
    }

    private void init(Canvas canvas) {
        paint = new Paint();
        path = new Path();
        calendar = Calendar.getInstance();
        int am_pm = calendar.get(Calendar.AM_PM);

        hour = calendar.get(Calendar.HOUR);
        minute = calendar.get(Calendar.MINUTE);
        previousMinute = minute;

        if(am_pm == 1) {
            hourAnimateForward = false;
            hourRed = hour * sunRedAnimationInterval;
            hourGreen = hour * sunBlueAnimationInterval;
            hourBlue = hour * sunGreenAnimationInterval;
        }

        if(minute != 0) {
            minuteRed = maxFirstLevelRed - (minute * 2);
            minuteBlue = 255;
            minuteGreen = maxFirstLevelGreen - minute;
        }

        Log.d("CLOCKVIEW", String.format("minuteRed = %d minuteBlue = %d minuteGreen = %d", minuteRed, minuteBlue, minuteGreen));

        secondRed = minSecondRedLevel;
        secondBlue = maxSecondBlueLevel;
        secondGreen = minSecondGreenLevel;

        secondAnimationForward = false;

        alphaCentX = (getWidth() / 2);
        alphaCentY = (getHeight() / 2);
        alphaCentRadius = 125;

        betelgeuseX = (getWidth() / 2);
        betelgeuseY = (getHeight() - (getHeight() / 8));
        betelgeuseRadius = 75;

        alphaPathEnd = new Point(alphaCentX, alphaCentY + alphaCentRadius);
        alphaPathStart = new Point(betelgeuseX, betelgeuseY - betelgeuseRadius);
        alphaPathCurrent = alphaPathStart;

        initialized = true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if(!initialized)
            init(canvas);

        paint.setStyle(Paint.Style.FILL);
        canvas.drawColor(Color.BLACK);

        calendar = Calendar.getInstance();
        hour = calendar.get(Calendar.HOUR);
        minute = calendar.get(Calendar.MINUTE);

        drawBetelgeuse(canvas);
        if(minute != previousMinute)
            animatePathToAlpha(canvas);
        if(!alphaPathAnimating)
            previousMinute = minute;

        drawAlphaCentauri(canvas);
        drawSun(canvas);
        invalidate();
    }

    private void drawSun(Canvas canvas) {
        if(sunColorAnimation) {
            if(hourAnimateForward) {

            }
        }
    }

    private void drawAlphaCentauri(Canvas canvas) {
        if(alphaColorAnimation) {
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
            alphaColorAnimation = false;
            Log.d("CLOCKVIEW", String.format("minuteRed = %d minuteBlue = %d minuteGreen = %d", minuteRed, minuteBlue, minuteGreen));
        }


        paint.setColor(rgb(minuteRed, minuteGreen, minuteBlue));
        canvas.drawCircle(alphaCentX, alphaCentY , alphaCentRadius, paint);
    }

    private void drawBetelgeuse(Canvas canvas) {
        if(secondAnimationForward) {
            secondBlue = min(secondBlue + secondBlueAnimationInterval, maxSecondBlueLevel);
            if(secondBlue >= maxSecondBlueLevel)
                secondAnimationForward = !secondAnimationForward;
        }

        else {
            secondBlue = max(secondBlue - secondBlueAnimationInterval, minSecondBlueLevel);
            if(secondBlue <= minSecondBlueLevel)
                secondAnimationForward = !secondAnimationForward;
        }

        paint.setColor(rgb(secondRed, secondGreen, secondBlue));
        canvas.drawCircle(betelgeuseX, betelgeuseY , betelgeuseRadius , paint);
    }

    private void animatePathToAlpha(Canvas canvas) {
        alphaPathAnimating = true;
        paint.setColor(rgb(minuteRed, minuteGreen, minuteBlue));
        paint.setStrokeWidth(12f);
        paint.setStyle(Paint.Style.STROKE);
        canvas.drawLine(alphaPathCurrent.x, alphaPathCurrent.y, alphaPathCurrent.x, alphaPathCurrent.y - alphaLineLength, paint);
        alphaPathCurrent.y -= alphaPathAnimationInterval;
        Log.d("CLOCKVIEW", String.format("alphaPathCurrent.y = %d alphaPathEnd.y = %d", alphaPathCurrent.y, alphaPathEnd.y));
        paint.reset();
        if (alphaPathCurrent.y <= alphaPathEnd.y + alphaLineLength) {
            alphaPathAnimating = false;
            alphaColorAnimation = true;
            alphaPathCurrent = alphaPathStart;
        }
    }
}
