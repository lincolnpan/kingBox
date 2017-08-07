package com.kingbox.view;

import android.content.Context;
import android.os.Parcelable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

/**
 * 多类型item  RecyclerView
 */
public class PullRecyclerView extends PullBaseView<RecyclerView> {

    public PullRecyclerView(Context context) {
        this(context, null);
    }

    public PullRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void addItemDecoration(RecyclerView.ItemDecoration decor) {
        mRecyclerView.addItemDecoration(decor, -1);
    }

    @Override
    protected RecyclerView createRecyclerView(Context context, AttributeSet attrs) {
        return new RecyclerView(context, attrs);
    }

    public void setAdapter(RecyclerView.Adapter adapter) {
        mRecyclerView.setAdapter(adapter);
    }

    public void setLayoutManager(RecyclerView.LayoutManager manager) {
        mRecyclerView.setLayoutManager(manager);
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        try {
            super.onRestoreInstanceState(state);
        } catch (Exception e) {
        }
        state = null;
    }

    @Override
    public boolean canScrollVertically(int direction) {
        // check if scrolling up
        if (direction < 1) {
            boolean original = super.canScrollVertically(direction);
            return !original && getChildAt(0) != null && getChildAt(0).getTop() < 0 || original;
        }
        return super.canScrollVertically(direction);

    }

}