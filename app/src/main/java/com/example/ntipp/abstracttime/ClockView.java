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

    int minSecondBlueLevel = 0;
    int maxSecondBlueLevel = 150;

    int secondBlueAnimationInterval = 4;

    int secondRed = 0;
    int secondBlue = 0;
    int secondGreen = 0;
    boolean secondAnimationForward;

    int minFirstLevelRed = 5;
    int minFirstLevelGreen = 10;
    int minFirstLevelBlue = 255;
    int minuteRedInterval = 2;
    int minuteGreenInterval = 4;
    int minuteBlueInterval = 125;

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

    int sunRedAnimationInterval = 11;
    int sunGreenAnimationInterval = 11;
    int sunBrightStart = 80;

    int previousMinute = 0;
    int previousHour = 0;
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
    int sunRadius;

    boolean alphaPathAnimating = false;
    boolean sunPathAnimating = false;

    float pathAnimationInterval = 3.0f;
    float pathLineLength = 100f;
    Point alphaPathEnd;
    Point alphaPathStart;
    Point alphaPathCurrent;
    Point sunPathEnd;
    Point sunPathStart;
    Point sunPathCurrent;

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
        previousHour = hour;

        if(am_pm == 1) {
            hourAnimateForward = false;
            if(hour == 12) {
                hourRed = 255;
                hourGreen = 255;
            }

            else {

                hourRed = 255 - (sunRedAnimationInterval * hour);
                hourGreen = 255 - (sunGreenAnimationInterval * hour);
            }
        }

        else {
            hourAnimateForward = true;
            if(hour == 12) {
                hourRed = 0;
                hourGreen= 0;
            }
            else {
                hourRed = sunBrightStart + (sunRedAnimationInterval * hour);
                hourGreen = sunBrightStart + (sunGreenAnimationInterval * hour);
            }
        }

        if(minute != 0) {
            minuteRed = minFirstLevelRed + (minute * minuteRedInterval);
            minuteBlue = 255;
            minuteGreen = minFirstLevelGreen + (minute * minuteGreenInterval);
        }

        Log.d("CLOCKVIEW", String.format("minuteRed = %d minuteBlue = %d minuteGreen = %d", minuteRed, minuteBlue, minuteGreen));
        Log.d("CLOCKVIEW", String.format("hourRed = %d hourBlue = %d hourGreen = %d", hourRed, hourBlue, hourGreen));

        secondBlue = maxSecondBlueLevel;
        secondRed = 255;
        secondGreen = 255;

        secondAnimationForward = false;

        alphaCentX = (getWidth() / 2);
        alphaCentY = ((getHeight() / 2) + 100);
        alphaCentRadius = 100;

        betelgeuseX = (getWidth() / 2);
        betelgeuseY = (getHeight() - (getHeight() / 8));
        betelgeuseRadius = 75;

        sunX = (getWidth() / 2);
        sunY = (getHeight() / 5);
        sunRadius = 175;

        alphaPathEnd = new Point(alphaCentX, alphaCentY + alphaCentRadius);
        alphaPathStart = new Point(betelgeuseX, betelgeuseY - betelgeuseRadius);
        alphaPathCurrent = alphaPathStart;

        sunPathEnd = new Point(sunX, sunY + sunRadius);
        sunPathStart = new Point(alphaCentX, alphaCentY - alphaCentRadius);
        sunPathCurrent = sunPathStart;

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

        if(previousHour != hour) {
            int am_pm = calendar.get(Calendar.AM_PM);
            if(am_pm == 0)
                animatePathToSun(canvas);
        }
        if(!sunPathAnimating)
            previousHour = hour;

        drawSun(canvas);
        invalidate();
    }

    private void drawSun(Canvas canvas) {
        if(sunColorAnimation) {
            if(hourAnimateForward) {
                if(hour != 1) {
                    hourRed = min(hourRed + sunRedAnimationInterval, 12 * sunRedAnimationInterval);
                    hourGreen = min(hourGreen + sunGreenAnimationInterval, 12 * sunGreenAnimationInterval);
                }

                else {
                    hourRed = sunBrightStart;
                    hourGreen = sunBrightStart;
                }
            }

            else {
                if(hour == 12) {
                    hourRed = 0;
                    hourRed = 0;
                }

                else {
                    hourRed = max(hourRed - sunRedAnimationInterval, 0);
                    hourGreen = max(hourGreen - sunGreenAnimationInterval, 0);
                }
            }

            sunColorAnimation = false;
        }

        paint.setColor(rgb(hourRed, hourGreen, hourBlue));
        canvas.drawCircle(sunX, sunY, sunRadius, paint);
    }

    private void drawAlphaCentauri(Canvas canvas) {
        if(alphaColorAnimation) {
            if (previousMinute == 0) {
                minuteAnimateForward = true;
                minuteBlue = 255;
            }
            else
                minuteAnimateForward = false;

            if (minuteAnimateForward) {
                minuteRed = min(minuteRed + minuteRedInterval, minFirstLevelRed);
                minuteGreen = min(minuteGreen + minuteGreenInterval, minFirstLevelGreen);
                minuteBlue = min(minuteBlue + minuteBlueInterval, minFirstLevelBlue);
                if (minuteGreen == minFirstLevelGreen && minuteRed == minFirstLevelRed) {
                    minuteAnimateForward = false;
                }

            } else if (minute == 0) {
                minuteBlue = 0;
                minuteGreen = 0;
                minuteBlue = 0;
            } else {
                minuteRed = min(minuteRed + minuteRedInterval, 255);
                minuteGreen = min(minuteGreen + minuteGreenInterval, 255);
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
        canvas.drawLine(alphaPathCurrent.x, alphaPathCurrent.y, alphaPathCurrent.x, alphaPathCurrent.y - pathLineLength, paint);
        alphaPathCurrent.y -= pathAnimationInterval;
        Log.d("CLOCKVIEW", String.format("alphaPathCurrent.y = %d alphaPathEnd.y = %d", alphaPathCurrent.y, alphaPathEnd.y));
        paint.reset();
        if (alphaPathCurrent.y <= alphaPathEnd.y + pathLineLength) {
            alphaPathAnimating = false;
            alphaColorAnimation = true;
            alphaPathCurrent = alphaPathStart;
        }
    }

    private void animatePathToSun(Canvas canvas) {
        sunPathAnimating = true;
        paint.setColor(rgb(minuteRed, minuteGreen, minuteBlue));
        paint.setStrokeWidth(12f);
        paint.setStyle(Paint.Style.STROKE);
        canvas.drawLine(sunPathCurrent.x, sunPathCurrent.y, sunPathCurrent.x, sunPathCurrent.y - pathLineLength, paint);
        sunPathCurrent.y -= pathAnimationInterval;
        Log.d("CLOCKVIEW", String.format("alphaPathCurrent.y = %d alphaPathEnd.y = %d", alphaPathCurrent.y, alphaPathEnd.y));
        paint.reset();
        if (sunPathCurrent.y <= sunPathEnd.y + pathLineLength) {
            sunPathAnimating = false;
            sunColorAnimation = true;
            sunPathCurrent = sunPathStart;
        }
    }
}
