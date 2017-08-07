package com.kingbox.utils;

import android.content.Context;
import android.view.View;

import com.kingbox.holder.BaseViewHolder;
import com.kingbox.service.entity.LiveField;
import com.kingbox.service.entity.Title;

/**
 * 多类型item工厂
 */
public interface TypeFactory {

    BaseViewHolder createViewHolder(Context context, int type, View itemView);

    int type(LiveField itemRoadShow, int type);   // 秀场

    int type(Title itemTiltle);   // 标题
}
