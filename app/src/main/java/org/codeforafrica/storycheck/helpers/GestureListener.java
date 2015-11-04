package org.codeforafrica.storycheck.helpers;

import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.ListView;

public final class GestureListener extends GestureDetector.SimpleOnGestureListener {

    private static final int SWIPE_THRESHOLD = 100;
    private static final int SWIPE_VELOCITY_THRESHOLD = 100;
    private ListView list;

    public GestureListener(ListView _list){
        list = _list;
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return true;
    }

    private int getPostion(MotionEvent e1) {
        return list.pointToPosition((int) e1.getX(), (int) e1.getY());
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        float distanceX = e2.getX() - e1.getX();
        float distanceY = e2.getY() - e1.getY();
        if (Math.abs(distanceX) > Math.abs(distanceY) && Math.abs(distanceX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
            if (distanceX > 0)
                onSwipeRight(getPostion(e1));
            else
                onSwipeLeft();
            return true;
        }
        return false;
    }

    public void onSwipeRight(int pos) {

    }

    public void onSwipeLeft() {

    }

}