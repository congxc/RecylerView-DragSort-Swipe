package com.rainnshieh.recycler.veiws;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by xc on 2016/11/14.
 */

public abstract class RecyclerItemLongClickListener implements RecyclerView.OnItemTouchListener {
    public abstract void onItemLongClick(RecyclerView.ViewHolder viewHolder,View view, int position);

    private GestureDetector mGestureDetector;
    public RecyclerItemLongClickListener(final Context context, final RecyclerView recyclerView) {

        mGestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return true;
            }

            @Override
            public void onLongPress(MotionEvent e) {
                View childView = recyclerView.findChildViewUnder(e.getX(), e.getY());
                if (childView != null ) {
                    onItemLongClick(recyclerView.getChildViewHolder(childView),childView, recyclerView.getChildAdapterPosition(childView));
                }
            }

        });
    }
    @Override
    public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
        mGestureDetector.onTouchEvent(e);
        return false;
    }

    @Override
    public void onTouchEvent(RecyclerView rv, MotionEvent e) {

    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

    }
}
