package com.ooxx;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class MyPagerAdapter extends FragmentPagerAdapter{

    List<Fragment> fs = new ArrayList<>();

    String order;

    FragmentManager fm;

    public MyPagerAdapter(FragmentManager fm, String order) {
        super(fm);
        this.fm = fm;
        if (order == null){
            fs.add(new MessageFragment());
            fs.add(new MailFragment());
            fs.add(new ContactFragment());
            fs.add(new SettingFragment());
        }else{
            this.order = order;
            for ( int i = 0; i < 4; i++ ){
                char c = order.charAt(i);
                if ( c == 'm' ){
                    fs.add(new MessageFragment());
                }else if ( c == 'a' ){
                    fs.add(new MailFragment());
                }else if ( c == 'c' ){
                    fs.add(new ContactFragment());
                }else if ( c == 's' ){
                    fs.add(new SettingFragment());
                }
            }
        }
    }

    public MyPagerAdapter(FragmentManager fm,String order, List<Fragment> fs){
        super(fm);
        this.fm = fm;
        this.fs = fs;
        this.order = order;
    }


    @Override
    public int getItemPosition(Object object) {
        Log.d("xx", "getItemPosition ");
        Fragment tFragment = (Fragment) object;
        for ( int i = 0; i < 4; i++ ){
            Fragment mFragment = fs.get(i);
            if (mFragment == tFragment){
                Log.d("xx", "return my item position:"+i);
                return i;
            }
        }
        return super.getItemPosition(object);
    }

    @Override
    public long getItemId(int position) {
        Log.d("xx", "getItemId ");
        return super.getItemId(position);
    }

    public void updateOrder (String newOrder){
        List<Fragment> newFragments = new ArrayList<>();
        for ( int i = 0; i < 4; i++ ){
            char c = newOrder.charAt(i);
            newFragments.add(getFragmentByChar(c));
        }
        fs = newFragments;
        order = newOrder;
    }

    public MyPagerAdapter newOrder (String newOrder){
        List<Fragment> newFragments = new ArrayList<>();
        for ( int i = 0; i < 4; i++ ){
            char c = newOrder.charAt(i);
           newFragments.add(getFragmentByChar(c));
        }
        MyPagerAdapter newAdapter = new MyPagerAdapter(fm, newOrder, newFragments);
        return newAdapter;
    }

    private Fragment getFragmentByChar (char c){
        for ( int i = 0; i < 4; i++ ){
            char tempc = order.charAt(i);
            if (tempc == c){
                return fs.get(i);
            }
        }
        return null;
    }

    @Override
    public Fragment getItem(int position) {
        Log.d("xxx", "adapter getItem");
        return fs.get(position);
    }

    @Override
    public int getCount() {
        return fs.size();
    }
}
