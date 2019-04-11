package com.example.buble;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;

public class MainActivity extends AppCompatActivity {

    ImageButton b1;
    GameView gameView;
    ChangeButtons changeButtons;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        Point size = new Point();

        getWindowManager().getDefaultDisplay().getSize(size);

        gameView = new GameView(this, size.x, size.y);
        final ConstraintLayout game = new ConstraintLayout(this);
        ImageButton b2 = new ImageButton(this);
        b1 = new ImageButton(this);
        ImageButton b3 = new ImageButton(this);

        Bitmap b = BitmapFactory.decodeResource(getResources(), R.drawable.fill);
        int w = b.getWidth() / 4;
        int h = b.getHeight() / 4;
        b = Bitmap.createScaledBitmap(b, w, h, true);
        b1.setImageBitmap(b);
        b1.setBackground(null);
        b1.setAlpha((float) 0);

        b = BitmapFactory.decodeResource(getResources(), R.drawable.up);
        w = b.getWidth() / 8;
        h = b.getHeight() / 8;
        b = Bitmap.createScaledBitmap(b, w, h, true);
        b2.setImageBitmap(b);
        b2.setBackground(null);
        b2.setX(size.x - 200 - b.getWidth());
        b2.setY(size.y - 200 - b.getHeight());

        b = BitmapFactory.decodeResource(getResources(), R.drawable.up);
        w = b.getWidth() / 8;
        h = b.getHeight() / 8;
        b = Bitmap.createScaledBitmap(b, w, h, true);
        b3.setImageBitmap(b);
        b3.setBackground(null);
        b3.setX(size.x - 600 - b.getWidth());
        b3.setY(size.y - 500 - b.getHeight());

        game.addView(gameView);
        game.addView(b1);
        game.addView(b2);
        game.addView(b3);

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gameView.setDrawRedCircle(true);
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                gameView.setDrawRedCircle(false);

            }
        });

        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gameView.setJumping(true);
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                gameView.setJumping(false);
            }
        });

        b3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gameView.setDrawingRoom(1);
            }
        });

        setContentView(R.layout.activity_main);

        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setContentView(game);
            }
        });

        changeButtons = new ChangeButtons();

        changeButtons.start();
    }

    class ChangeButtons extends Thread {

        boolean running = true;

        @Override
        public void run() {
            while (running) {
                try {
                    b1.setX((float) gameView.getScreenX(1) - 15);
                    b1.setY((float) gameView.getScreenY(1) - 15);
                } catch (NullPointerException e) {
                    Log.e("tagThere", "run: ", e);
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        changeButtons.running = false;
        super.onDestroy();
    }
}