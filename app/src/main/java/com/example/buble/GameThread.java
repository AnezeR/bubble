package com.example.buble;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.SurfaceHolder;

import java.util.ArrayList;

public class GameThread extends Thread {


    private static double jR = Params.JR, joystickR = Params.JoystickR;
    private static int speed = Params.speed;
    private static int r = Params.R, bgColor = Params.bgColor;


    private final SurfaceHolder surfaceHolder;
    private Map map;
    private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private int color;
    private int heroFloor = Params.startFloor;

    private boolean isJoystickDraw = false;
    private boolean isDrawRedCircle = false;
    private boolean running;
    private boolean updating;
    private boolean jumping = false;
    private boolean xP = false, yP = false, xM = false, yM = false;

    private double joystickX, joystickY;
    private double jMX, jMY;

    private float screenX, screenY;


    GameThread(SurfaceHolder surfaceHolder,
               float screenSizeX, float screenSizeY, ArrayList<Bitmap> bitmaps) {

        this.surfaceHolder = surfaceHolder;

        this.screenX = screenSizeX / 2f;
        this.screenY = screenSizeY / 2f;

        this.map = new Map(bitmaps, screenSizeX, screenSizeY);
    }

    @Override
    public void run() {

        while (running) {

            checkGo();
            if (!jumping)
                checkFloor();

            synchronized (surfaceHolder) {
                try {

                    Canvas canvas = surfaceHolder.lockCanvas();

                    canvas.drawColor(bgColor);

                    map.draw(canvas);

                    if (updating && !jumping) switch (map.getDist()) {

                        case 40:
                            color = Color.LTGRAY;
                            break;
                        case 80:
                            color = Color.RED;
                            break;
                        case 120:
                            color = Color.GREEN;
                            break;
                    }

                    paint.setColor(color);
                    paint.setStyle(Paint.Style.FILL);

                    canvas.drawCircle(screenX, screenY, r, paint);

                    paint.setColor(Color.argb(255 / 2, 255, 255, 255));

                    if (isJoystickDraw)
                        drawJoystick(canvas, paint);

                    drawRedCircle(canvas, paint);

                    surfaceHolder.unlockCanvasAndPost(canvas);
                } catch (Exception ignored) {

                }
            }
        }
    }

    private void checkGo() {

        boolean notX = ((map.getWidth() + map.getX() <= screenX + r && xM)
                || (map.getX() >= screenX - r && xP));
        boolean notY = ((map.getHeight() + map.getY() <= screenY + r && yM)
                || (map.getY() >= screenY - r && yP));

        if (notX) {
            map.setLastScreenX();
            map.setUpdateX(false);
        } else
            map.setUpdateX(updating);

        if (notY) {
            map.setLastScreenY();
            map.setUpdateY(false);
        } else
            map.setUpdateY(updating);

    }

    private void checkFloor() {

        ArrayList<MapStruct> assets = map.getDrawingMapStructs();
        boolean ableX = true, ableY = true;
        map.setSetLsX(true);
        map.setSetLsY(true);

        for (int i = 0; i < assets.size(); i++) {

            MapStruct struct = assets.get(i);

            if (struct.getFloor() != heroFloor) {

                boolean right = (struct.getScreenX() + struct.getWidth() <= screenX - r);
                boolean left = (struct.getScreenX() >= screenX + r);
                boolean bottom = (struct.getScreenY() + struct.getHeight() <= screenY - r);
                boolean top = (struct.getScreenY() >= screenY + r);

                if (!right && !left) {
                    ableX = false;
                }

                if (!top && !bottom) {
                    ableY = false;
                }

                if (!ableX && !ableY) {

                    map.setSetLsX(false);
                    map.setSetLsY(false);

                    right = (struct.getLastScreenX() + struct.getWidth() <= screenX - r);
                    left = (struct.getLastScreenX() >= screenX + r);
                    bottom = (struct.getLastScreenY() + struct.getHeight() <= screenY - r);
                    top = (struct.getLastScreenY() >= screenY + r);

                    if ((right && xP) || (left && xM)) {

                        map.setLastScreenX();
                        map.setUpdateX(false);
                    } else {
                        map.setUpdateX(updating);
                    }

                    if ((top && yM) || (bottom && yP)) {
                        map.setLastScreenY();
                        map.setUpdateY(false);
                    } else {
                        map.setUpdateY(true);
                    }

                } else {
                    ableX = true;
                    ableY = true;
                }
            }
        }

    }

    private void drawJoystick(Canvas canvas, Paint paint) {

        paint.setColor(Color.argb(200, 255, 255, 255));
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(10);

        canvas.drawCircle((float) jMX, (float) jMY, (float) joystickR, paint);

        paint.setStyle(Paint.Style.FILL);
        paint.setAlpha(100);

        canvas.drawCircle((float) jMX, (float) jMY, (float) joystickR, paint);

        paint.setAlpha(200);

        canvas.drawCircle((float) joystickX, (float) joystickY, (float) jR, paint);

        paint.setAlpha(240);

        canvas.drawCircle((float) joystickX, (float) joystickY, (float) jR, paint);
    }

    private void drawRedCircle(Canvas canvas, Paint paint) {

        int color = paint.getColor();

        paint.setColor(Color.RED);

        if (isDrawRedCircle)
            canvas.drawCircle(screenX, screenY, 100, paint);

        paint.setColor(color);
    }


    void setJoystickDraw(boolean isJoystickDraw) {
        this.isJoystickDraw = isJoystickDraw;
    }

    void setJoystickParams(double x, double y, double midX, double midY) {

        jMX = midX;
        jMY = midY;

        double ex = x - midX, ey = y - midY;

        double c = Math.hypot(ex, ey);

        if (c > joystickR) {
            x = joystickR / c * ex + midX;
            y = joystickR / c * ey + midY;

            map.setSpeed(speed);
        } else {
            map.setSpeed((int) (c / joystickR * speed));
        }

        joystickX = x;
        joystickY = y;
    }

    void setColor(int color) {
        this.color = color;
    }

    void setUpdate(boolean update, boolean xP, boolean yP, boolean xM, boolean yM) {

        this.xP = xP;
        this.xM = xM;
        this.yP = yP;
        this.yM = yM;

        map.setPM(xP, yP, xM, yM);

        updating = update;
    }

    void setRunning(boolean running) {
        this.running = running;
    }

    void setDrawRedCircle(boolean isDrawRedCircle) {
        this.isDrawRedCircle = isDrawRedCircle;
    }

    void setJumping(boolean jumping) {
        this.jumping = jumping;
    }

    double getScreenX(int i) {
        return map.getScreenX(i);
    }

    double getScreenY(int i) {
        return map.getScreenY(i);
    }

    void setDrawingRoom(int room) {
        map.changeDrawingRoom(room);
    }
}
