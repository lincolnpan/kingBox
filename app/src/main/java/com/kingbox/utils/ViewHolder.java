package com.kingbox.utils;

import android.util.SparseArray;
import android.view.View;

/**
 *  通用viewHolder，减少重复造轮子(每个adapter里有个viewHolder)
 * Created by Administrator on 2016/12/7.
 */
public class ViewHolder {

    // I added a generic return type to reduce the casting noise in client code
    @SuppressWarnings("unchecked")
    public static <T extends View> T getViewById(View view, int id) {
        SparseArray<View> viewHolder = (SparseArray<View>) view.getTag();
        if (viewHolder == null) {
            viewHolder = new SparseArray<>();
            view.setTag(viewHolder);
        }
        View childView = viewHolder.get(id);
        if (childView == null) {
            childView = view.findViewById(id);
            viewHolder.put(id, childView);
        }
        return (T) childView;
    }
}
