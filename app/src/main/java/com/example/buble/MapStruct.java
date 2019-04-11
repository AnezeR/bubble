package com.example.buble;

import android.graphics.Bitmap;
import android.graphics.Canvas;

class MapStruct {

    private double screenX;
    private double screenY;
    private double lastScreenX, lastScreenY;
    private boolean isSetLsX = true, isSetLsY = true;
    private int floor;
    private Bitmap sprite;

    MapStruct(double screenX, double screenY, Bitmap bmp, int floor) {

        sprite = bmp;
        sprite.prepareToDraw();

        lastScreenX = screenX;
        lastScreenY = screenY;

        this.screenX = screenX;
        this.screenY = screenY;

        this.floor = floor;
    }


    void draw(Canvas canvas) {
        canvas.drawBitmap(sprite, (float) screenX, (float) screenY, null);
    }

    void updateX(double cos, int speed) {
        if (isSetLsX)
            lastScreenX = screenX;
        screenX += cos * speed;
    }

    void updateY(double sin, int speed) {
        if (isSetLsY)
            lastScreenY = screenY;
        screenY += sin * speed;
    }

    void setLastScreenX() {
        screenX = lastScreenX;
    }

    void setLastScreenY() {
        screenY = lastScreenY;
    }

    double getScreenX() {
        return screenX;
    }

    double getScreenY() {
        return screenY;
    }

    int getFloor() {
        return floor;
    }

    double getWidth() {
        return sprite.getWidth();
    }

    double getHeight() {
        return sprite.getHeight();
    }

    double getLastScreenX() {
        return lastScreenX;
    }

    double getLastScreenY() {
        return lastScreenY;
    }

    void setSetLsX(boolean setLsX) {
        isSetLsX = setLsX;
    }

    void setSetLsY(boolean setLsY) {
        isSetLsY = setLsY;
    }
}