package com.ooxx;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Handler;
import android.preference.Preference;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.menu.ExpandedMenuView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.TouchDelegate;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AnimationSet;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements View.OnLongClickListener{

    View menu1;
    View menu2;
    View menu3;
    View menu4;

    Handler handler;

    CanvasView canvasView;

    boolean isAnimation = false;

    ImageView img1;
    ImageView img2;
    ImageView img3;
    ImageView img4;

    ViewPager viewPager;
    MyPagerAdapter myPagerAdapter;

    String order;

    String newOrder;

    List<View> menus = new ArrayList<>();

    private View getMenu (char c){
        for (int i = 0; i < 4; i++){
            char tempC = order.charAt(i);
            if (tempC == c){
                return menus.get(i);
            }
        }
        return null;
    }

    private void readyForDragHint (){
        footContainer.setBackgroundColor(getResources().getColor(R.color.foot_container_bg_drag));
    }

    private void endDrag (){
        footContainer.setBackgroundColor(getResources().getColor(R.color.foot_container_bg_normal));
    }

    private String loadOrder (){
        SharedPreferences sharedPreferences = getSharedPreferences("order", MODE_PRIVATE);
        String order = sharedPreferences.getString("order", "macs");
        return order;
    }

    private void saveOrder (String order){
        SharedPreferences.Editor editor = getSharedPreferences("order", MODE_PRIVATE).edit();
        editor.putString("order", order);
        editor.commit();
    }

    private String swipeOrder (String oldOrder, char c1, char c2){
        StringBuilder stringBuilder = new StringBuilder();
        for ( int i = 0; i < 4; i++ ){
            char c = oldOrder.charAt(i);
            if (c == c1){
                c = c2;
            }else if ( c == c2 ){
                c = c1;
            }
            stringBuilder.append(c);
        }
        return stringBuilder.toString();
    }

    private void syncViewOrder (){
        List<View> newMenus = new ArrayList<>();
        for ( int i = 0; i < 4; i++ ){
            newMenus.add(getMenu(newOrder.charAt(i)));
        }
        menus = newMenus;
    }

    private int charToIndex (char c){
        for ( int i = 0; i < 4; i++ ){
            char tempc = order.charAt(i);
           if (tempc == c){
            return i;
           }
        }
        return 0;
    }

    boolean firstTouch = true;

    private boolean handleTouchMenu (char c, View v, MotionEvent event){
        if (!readyForDrag){
            return false;
        }else{
            View touchView = getMenu(c);
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                case MotionEvent.ACTION_MOVE:
                    if (firstTouch){
                        newOrder = order;
                        firstTouch = false;
                    }

                    touchView.setVisibility(View.INVISIBLE);
                    canvasView.setVisibility(View.VISIBLE);
                    Bitmap bitmap4Draw = converViewToBitMap(touchView);
                    canvasView.bitmap = bitmap4Draw;
                    float touchX = event.getRawX();
                    float touchY = event.getRawY();

                    int drawLeft = (int)(touchX-(bitmap4Draw.getWidth()/2));
                    int drawTop = (int)(touchY-(bitmap4Draw.getHeight()/2));
                    canvasView.bitmapLeft = drawLeft;
                    canvasView.bitmapTop = drawTop;
                    canvasView.invalidate();

                    if (!isAnimation){
                        for ( int i = 0; i < 4; i++ ){
                            char tempC = order.charAt(i);
                            if (tempC != c){
                                View testMenu = getMenu(tempC);
                                if (closeTo((int)touchX, (int)touchY, testMenu)){
                                    exchange(touchView, testMenu);
                                    newOrder = swipeOrder(newOrder, c, tempC);
                                }
                            }
                        }
                    }
                    break;

                case MotionEvent.ACTION_UP:
                    canvasView.setVisibility(View.GONE);

                    firstTouch = true;
                    readyForDrag = false;
                    endDrag();
                    syncViewOrder();
                    order = newOrder;
                    saveOrder(order);
                    myPagerAdapter.updateOrder(order);
                    int curShowFragment = charToIndex(c);
                    myPagerAdapter.notifyDataSetChanged();
                    viewPager.setCurrentItem(curShowFragment);

                    touchView.setVisibility(View.VISIBLE);
            }
            return true;
        }
    }

    LinearLayout footContainer;

    LayoutInflater layoutInflater;

    boolean readyForDrag = false;

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        footContainer = (LinearLayout) findViewById(R.id.main_footer);

        layoutInflater = LayoutInflater.from(this);

        menu1 = layoutInflater.inflate(R.layout.layout_message, footContainer, false);
        menu2 = layoutInflater.inflate(R.layout.layout_mail, footContainer, false);
        menu3 = layoutInflater.inflate(R.layout.layout_contact, footContainer, false);
        menu4 = layoutInflater.inflate(R.layout.layout_setting, footContainer, false);

        menu1.setOnLongClickListener(this);
        menu2.setOnLongClickListener(this);
        menu3.setOnLongClickListener(this);
        menu4.setOnLongClickListener(this);


        img1 = (ImageView) menu1.findViewById(R.id.img1);
        img2 = (ImageView) menu2.findViewById(R.id.img2);
        img3 = (ImageView) menu3.findViewById(R.id.img3);
        img4 = (ImageView) menu4.findViewById(R.id.img4);

        img1.setOnLongClickListener(this);
        img2.setOnLongClickListener(this);
        img3.setOnLongClickListener(this);
        img4.setOnLongClickListener(this);


        order = loadOrder();

        for ( int i = 0; i < 4; i++ ){
            char c = order.charAt(i);
            if (c == 'm'){
                footContainer.addView(menu1);
                menus.add(menu1);
            }else if (c == 'a'){
                footContainer.addView(menu2);
                menus.add(menu2);
            }else if (c == 'c'){
                footContainer.addView(menu3);
                menus.add(menu3);
            }else if (c == 's'){
                footContainer.addView(menu4);
                menus.add(menu4);
            }
        }

        viewPager = (ViewPager) findViewById(R.id.vp);
        myPagerAdapter = new MyPagerAdapter(getSupportFragmentManager(), order);
        viewPager.setOffscreenPageLimit(4);
        viewPager.setAdapter(myPagerAdapter);

        handler = new Handler();

        //消息
        menu1.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                boolean res = handleTouchMenu('m', v, event);
                return res;
            }
        });

        img1.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                boolean res = handleTouchMenu('m', v, event);
                return res;
            }
        });


        //邮件
        menu2.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                boolean res = handleTouchMenu('a', v, event);
                return res;
            }
        });

        img2.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                boolean res = handleTouchMenu('a', v, event);
                return res;
            }
        });

        //联系人
        menu3.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                boolean res = handleTouchMenu('c', v, event);
                return res;
            }
        });

        img3.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                boolean res = handleTouchMenu('c', v, event);
                return res;
            }
        });

        //设置
        menu4.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                boolean res = handleTouchMenu('s', v, event);
                return res;
            }
        });

        img4.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                boolean res = handleTouchMenu('s', v, event);
                return res;
            }
        });


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

    public static Bitmap converViewToBitMap(View view) {
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache(true);
        return Bitmap.createBitmap(view.getDrawingCache());
    }

    @Override
    public boolean onLongClick(View v) {
        readyForDragHint();
        readyForDrag = true;
        return true;
    }
}
