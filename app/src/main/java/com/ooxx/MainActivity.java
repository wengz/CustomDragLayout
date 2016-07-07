package com.ooxx;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.menu.ExpandedMenuView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AnimationSet;

public class MainActivity extends AppCompatActivity {

    View menu1;
    View menu2;
    View menu3;

    Handler handler;

    CanvasView canvasView;

    boolean isAnimation = false;

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        menu1 = findViewById(R.id.menu_1);
        menu2 = findViewById(R.id.menu_2);
        menu3 = findViewById(R.id.menu_3);

        handler = new Handler();

        menu1.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Log.d("xxx", "menu1 onTouch");

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                    case MotionEvent.ACTION_MOVE:
                        menu1.setVisibility(View.INVISIBLE);
                        canvasView.setVisibility(View.VISIBLE);
                        Bitmap bitmap4Draw = converViewToBitMap(menu1);
                        canvasView.bitmap = bitmap4Draw;
                        float touchX = event.getRawX();
                        float touchY = event.getRawY();

                        int drawLeft = (int)(touchX-(bitmap4Draw.getWidth()/2));
                        int drawTop = (int)(touchY-(bitmap4Draw.getHeight()/2));
                        canvasView.bitmapLeft = drawLeft;
                        canvasView.bitmapTop = drawTop;
                        canvasView.invalidate();

                        if (!isAnimation){
                            if (closeTo((int)touchX, (int)touchY, menu2)){
                                exchange(menu1, menu2);
                            }
                        }

                        if (!isAnimation){
                            if (closeTo((int)touchX, (int)touchY, menu3)){
                                exchange(menu1, menu3);
                            }
                        }

                        break;

                    case MotionEvent.ACTION_UP:
                        canvasView.setVisibility(View.GONE);
                        menu1.setVisibility(View.VISIBLE);
                }

                return true;
            }
        });

//        menu1.setOnLongClickListener(new View.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View v) {
//                Log.d("xxx", "menu1 long click");
//                return false;
//            }
//        });


//        menu1.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                exchangePositions();
//            }
//        });
//
//        menu2.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                exchangePositions();
//            }
//        });
//
//        menu3.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                exchangePositions();
//            }
//        });

//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                int left = menu1.getLeft();
//                int top = menu1.getTop();
//                int right = menu1.getRight();
//                int botton = menu1.getBottom();
//
//                int upDistance = 200;
//
//                menu1.setTop(top-upDistance);
//                menu1.setBottom(botton-upDistance);
//            }
//        }, 3000);

        ViewGroup rootView = (ViewGroup) getWindow().getDecorView();
        canvasView = new CanvasView(this);
        ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        canvasView.setLayoutParams(lp);
        canvasView.setVisibility(View.GONE);
        rootView.addView(canvasView);
    }

    private boolean closeTo (int touchX, int touchY, View view){
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        int viewCentX = location[0] + view.getWidth()/2;
        int viewCentY = location[1] + view.getHeight()/2;
        if ( Math.hypot( touchX - viewCentX, touchY - viewCentY) <= 100){
            return true;
        }
        return false;
    }

    private void exchange (View view1, View view2){
        int menu1Left = view1.getLeft();
        int menu2Left = view2.getLeft();

        int menu1Width = view1.getWidth();
        int menu2Width = view2.getWidth();


        int menu1NewLeft = menu2Left;
        int menu2NewLeft = menu1Left;

        ObjectAnimator objectAnimator1 = ObjectAnimator.ofInt(view1, "left", menu1Left, menu1NewLeft);
        ObjectAnimator objectAnimator1Right = ObjectAnimator.ofInt(view1, "right", menu1Left + menu1Width, menu1NewLeft + menu1Width);

        ObjectAnimator objectAnimator2 = ObjectAnimator.ofInt(view2, "left", menu2Left, menu2NewLeft);
        ObjectAnimator objectAnimator2Right = ObjectAnimator.ofInt(view2, "right", menu2Left + menu2Width, menu2NewLeft + menu2Width);


        AnimatorSet animatorSet1 = new AnimatorSet();
        animatorSet1.playTogether(objectAnimator1, objectAnimator1Right);

        AnimatorSet animatorSet2 = new AnimatorSet();
        animatorSet2.playTogether(objectAnimator2, objectAnimator2Right);

        animatorSet1.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                isAnimation = true;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                isAnimation = false;
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                isAnimation = false;
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
                isAnimation = true;
            }
        });
        animatorSet1.setDuration(500);
        animatorSet1.start();

        animatorSet2.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                isAnimation = true;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                isAnimation = false;
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                isAnimation = false;
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
                isAnimation = true;
            }
        });
        animatorSet2.setDuration(500);
        animatorSet2.start();
    }

    private void exchangePositions() {

        int menu1Left = menu1.getLeft();
        int menu2Left = menu2.getLeft();
        int menu3Left = menu3.getLeft();

        int menu1Width = menu1.getWidth();
        int menu2Width = menu2.getWidth();
        int menu3Width = menu3.getWidth();


        int menu1NewLeft = menu3Left;
        int menu2NewLeft = menu1Left;
        int menu3NewLeft = menu2Left;

        ObjectAnimator objectAnimator1 = ObjectAnimator.ofInt(menu1, "left", menu1Left, menu1NewLeft);
        ObjectAnimator objectAnimator1Right = ObjectAnimator.ofInt(menu1, "right", menu1Left + menu1Width, menu1NewLeft + menu1Width);

        ObjectAnimator objectAnimator2 = ObjectAnimator.ofInt(menu2, "left", menu2Left, menu2NewLeft);
        ObjectAnimator objectAnimator2Right = ObjectAnimator.ofInt(menu2, "right", menu2Left + menu2Width, menu2NewLeft + menu2Width);

        ObjectAnimator objectAnimator3 = ObjectAnimator.ofInt(menu3, "left", menu3Left, menu3NewLeft);
        ObjectAnimator objectAnimator3Right = ObjectAnimator.ofInt(menu3, "right", menu3Left + menu3Width, menu3NewLeft + menu3Width);

        AnimatorSet animatorSet1 = new AnimatorSet();
        animatorSet1.playTogether(objectAnimator1, objectAnimator1Right);

        AnimatorSet animatorSet2 = new AnimatorSet();
        animatorSet2.playTogether(objectAnimator2, objectAnimator2Right);

        AnimatorSet animatorSet3 = new AnimatorSet();
        animatorSet1.playTogether(objectAnimator3, objectAnimator3Right);

        animatorSet1.setDuration(500);
        animatorSet1.start();

        animatorSet2.setDuration(500);
        animatorSet2.start();

        animatorSet3.setDuration(500);
        animatorSet3.start();
    }

    public static Bitmap converViewToBitMap(View view) {
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache(true);
        return Bitmap.createBitmap(view.getDrawingCache());
    }
}
