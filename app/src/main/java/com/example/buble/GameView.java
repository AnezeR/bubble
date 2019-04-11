package com.example.buble;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;


@SuppressLint("ViewConstructor")
public class GameView extends SurfaceView implements SurfaceHolder.Callback {

    SurfaceHolder surfaceHolder;
    GameThread gameThread;
    Bitmap map;
    GameAnimation gameAnimation;
    ToGameThread toGameThread;
    MediaPlayer mediaPlayer;

    float screenSizeX, screenSizeY;
    double midX, midY;


    @SuppressLint("ClickableViewAccessibility")
    public GameView(Context context, float screenSizeX, float screenSizeY) {
        super(context);

        this.screenSizeX = screenSizeX;
        this.screenSizeY = screenSizeY;

        surfaceHolder = getHolder();
        surfaceHolder.addCallback(this);

        map = BitmapFactory.decodeResource(getResources(), R.drawable.fl);

        int mapWidth = map.getWidth(), mapHeight = map.getHeight();

        map = Bitmap.createScaledBitmap(map, mapWidth, mapHeight, true);

        ArrayList<Bitmap> bitmaps = new ArrayList<>();

        Bitmap wall = BitmapFactory.decodeResource(getResources(), R.drawable.hero);
        wall = Bitmap.createScaledBitmap(wall, wall.getWidth(), wall.getHeight(), true);

        Bitmap oneMoreWall = BitmapFactory.decodeResource(getResources(), R.drawable.fill);
        mapWidth = oneMoreWall.getWidth() / 4;
        mapHeight = oneMoreWall.getHeight() / 4;
        oneMoreWall = Bitmap.createScaledBitmap(oneMoreWall, mapWidth, mapHeight, true);

        bitmaps.add(map);
        bitmaps.add(wall);
        bitmaps.add(oneMoreWall);

        gameThread = new GameThread(surfaceHolder, screenSizeX, screenSizeY, bitmaps);

        gameAnimation = new GameAnimation();
        toGameThread = new ToGameThread();


        mediaPlayer = MediaPlayer.create(context, R.raw.untitled5);

        mediaPlayer.setLooping(true);

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mp.release();
            }
        });

        mediaPlayer.start();
    }


    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {

        double x = event.getX(), y = event.getY();
        int action = event.getAction();

        if (action == MotionEvent.ACTION_DOWN && x <= screenSizeX / 2) {
            midX = x;
            midY = y;

            toGameThread.setMidXY(midX, midY);
            toGameThread.setJoystickDraw(true);

            toGameThread.setUpdating(true);
            toGameThread.setUpdate(true);

            gameAnimation.setUpdating(false);

        } else if (action == MotionEvent.ACTION_MOVE) {
            toGameThread.setXY(x, y);

        } else if (action == MotionEvent.ACTION_UP) {

            toGameThread.setJoystickDraw(false);
            toGameThread.setPMtoFalse();

            toGameThread.setUpdating(false);
            toGameThread.setUpdate(false);

            gameAnimation.setUpdating(true);
        } else {
            toGameThread.setJoystickDraw(false);
            toGameThread.setPMtoFalse();

            toGameThread.setUpdating(false);
            toGameThread.setUpdate(false);

            gameAnimation.setUpdating(true);
        }

        return true;
    }

    private class ToGameThread extends Thread {

        private volatile boolean running, updating;

        private volatile boolean xP, yP, xM, yM;
        private volatile boolean update;
        private volatile double midX, midY, x, y;


        @Override
        public void run() {

            while (running) {
                while (updating) {

                    gameThread.setUpdate(update, xP, yP, xM, yM);

                    gameThread.setJoystickParams(x, y, midX, midY);
                }
            }
        }

        void setRunning(boolean running) {
            this.running = running;
        }

        void setUpdate(boolean update) {
            this.update = update;
        }

        void setPMtoFalse() {
            this.xM = false;
            this.yM = false;
            this.xP = false;
            this.yP = false;
        }

        void setUpdating(boolean updating) {
            this.updating = updating;
        }

        void setMidXY(double midX, double midY) {
            this.midX = midX;
            this.midY = midY;
        }

        void setXY(double x, double y) {
            this.x = x;
            this.y = y;

            setPMtoFalse();

            double ex = x - midX, ey = y - midY;

            double c = Math.hypot(ex, ey);


            double sinJ = ey / c;
            double cosJ = ex / c;

            if (sinJ > Math.sin(Math.PI / 6))
                yM = true;
            else if (sinJ < Math.sin(-Math.PI / 6))
                yP = true;


            if (cosJ > Math.cos(Math.PI / 3))
                xM = true;
            else if (cosJ < Math.cos(2 * Math.PI / 3))
                xP = true;
        }

        void setJoystickDraw(boolean joystickDraw) {
            gameThread.setJoystickDraw(joystickDraw);
        }
    }

    private class GameAnimation extends Thread {
        private boolean running, updating, jumping = false;

        @Override
        public void run() {

            while (running) {

                if (!jumping) while (updating) {
                    gameThread.setColor(Color.BLACK);

                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    gameThread.setColor(Color.DKGRAY);

                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                else {
                    gameThread.setColor(Color.WHITE);
                }
            }
        }

        void setRunning(boolean running) {
            this.running = running;
        }

        void setUpdating(boolean updating) {
            this.updating = updating;
        }

        void setJumping(boolean jumping) {
            this.jumping = jumping;
        }
    }


    @Override
    public void surfaceCreated(SurfaceHolder holder) {

        gameAnimation.setRunning(true);
        gameAnimation.setUpdating(true);

        gameThread.setRunning(true);
        toGameThread.setRunning(true);

        wait4Holder();

        gameThread.start();
        gameAnimation.start();
        toGameThread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

        mediaPlayer.stop();

        gameThread.setRunning(false);
        gameAnimation.setRunning(false);
        toGameThread.setRunning(false);

        while (gameAnimation.isAlive()) {
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        while (toGameThread.isAlive()) {
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        while (gameThread.isAlive()) {
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void wait4Holder() {
        while (!surfaceHolder.isCreating()) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void setDrawRedCircle(boolean b) {
        gameThread.setDrawRedCircle(b);
    }

    double getScreenX(int i) {
        return gameThread.getScreenX(i);
    }

    double getScreenY(int i) {
        return gameThread.getScreenY(i);
    }

    void setJumping(boolean jumping) {
        gameAnimation.setJumping(jumping);
        gameThread.setJumping(jumping);
    }

    void setDrawingRoom(int room) {
        gameThread.setDrawingRoom(room);
    }
}