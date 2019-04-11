package com.example.buble;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.Log;

import java.util.ArrayList;

class Map {

    private double cos, sin;
    private boolean updateX, updateY;
    private int dist;
    private double height, width;
    private volatile boolean moving = false;
    private int speed;

    private ArrayList<ArrayList<MapStruct>> mapStructs;
    private ArrayList<MapStruct> drawingMapStructs;

    Map(ArrayList<Bitmap> bitmaps, float screenSizeX, float screenSizeY) {

        Bitmap bitmap = bitmaps.get(0);

        double screenX = -bitmap.getWidth() / 2f + screenSizeX / 2f;
        double screenY = -bitmap.getHeight() / 2f + screenSizeY / 2f;

        mapStructs = new ArrayList<>();
        drawingMapStructs = new ArrayList<>();
        ArrayList<MapStruct> room = new ArrayList<>();

        room.add(new MapStruct(screenX, screenY, bitmap, 1));

        height += bitmap.getHeight();
        width += bitmap.getWidth();

        room.add(new MapStruct(400, 600, bitmaps.get(2), 2));

        mapStructs.add(room);

        Log.d("tagThere", "Map: " + mapStructs.get(0).get(1).getScreenX() + ' ' + mapStructs.get(0).get(1).getScreenY());

        room = new ArrayList<>();

        room.add(new MapStruct(screenX, screenY, bitmap, 1));

        room.add(new MapStruct(-500, -600, bitmaps.get(2), 2));

        mapStructs.add(room);

        Log.d("tagThere", "Map: " + mapStructs.get(0).get(1).getScreenX() + ' ' + mapStructs.get(0).get(1).getScreenY());

        drawingMapStructs.addAll(mapStructs.get(0));
    }

    void draw(Canvas canvas) {

        int N = drawingMapStructs.size();

        for (int i = 0; i < N; i++) {
            drawingMapStructs.get(i).draw(canvas);
        }

        update();
    }

    private void update() {
        if (updateX) {
            int N = drawingMapStructs.size();

            for (int i = 0; i < N; i++) {
                drawingMapStructs.get(i).updateX(cos, speed);
            }
        }
        if (updateY) {
            int N = drawingMapStructs.size();

            for (int i = 0; i < N; i++) {
                drawingMapStructs.get(i).updateY(sin, speed);
            }
        }

        if (moving)
            dist = (dist + 2) % 122;

        if (!updateY && !updateX)
            dist = 0;
    }

    void setPM(boolean xP, boolean yP, boolean xM, boolean yM) {

        if (xP) {
            if (yP) {
                cos = Math.sqrt(2) / 2;
                sin = cos;
            } else if (yM) {
                cos = Math.sqrt(2) / 2;
                sin = -cos;
            } else {
                cos = 1;
                sin = 0;
            }
        } else if (xM) {
            if (yP) {
                cos = -Math.sqrt(2) / 2;
                sin = -cos;
            } else if (yM) {
                cos = -Math.sqrt(2) / 2;
                sin = cos;
            } else {
                cos = -1;
                sin = 0;
            }
        } else if (yP) {
            cos = 0;
            sin = 1;
        } else if (yM) {
            cos = 0;
            sin = -1;
        } else {
            cos = 0;
            sin = 0;
        }

        moving = xP || xM || yP || yM;
    }

    double getX() {
        return drawingMapStructs.get(0).getScreenX();
    }

    double getY() {
        return drawingMapStructs.get(0).getScreenY();
    }

    double getHeight() {
        return height;
    }

    double getWidth() {
        return width;
    }

    void setUpdateX(boolean updateX) {
        this.updateX = updateX;
    }

    void setUpdateY(boolean updateY) {
        this.updateY = updateY;
    }

    int getDist() {
        return dist;
    }

    void setLastScreenX() {
        int size = drawingMapStructs.size();

        for (int i = 0; i < size; i++) {
            drawingMapStructs.get(i).setLastScreenX();
        }
    }

    void setLastScreenY() {
        int size = drawingMapStructs.size();

        for (int i = 0; i < size; i++) {
            drawingMapStructs.get(i).setLastScreenY();
        }
    }

    ArrayList<MapStruct> getDrawingMapStructs() {
        return drawingMapStructs;
    }

    void setSpeed(int speed) {
        this.speed = speed;
    }

    void setSetLsX(boolean setLsX) {

        int size = drawingMapStructs.size();

        for (int i = 0; i < size; i++) {
            drawingMapStructs.get(i).setSetLsX(setLsX);
        }
    }

    void setSetLsY(boolean setLsY) {

        int size = drawingMapStructs.size();

        for (int i = 0; i < size; i++) {
            drawingMapStructs.get(i).setSetLsY(setLsY);
        }
    }

    void changeDrawingRoom(int room) {

        drawingMapStructs.clear();
        drawingMapStructs.addAll(mapStructs.get(room));

        Log.d("tagThere", "changeDrawingRoom: " + room);
    }

    double getScreenX(int i) {
        return drawingMapStructs.get(i).getScreenX();
    }

    double getScreenY(int i) {
        return drawingMapStructs.get(i).getScreenY();
    }
}
