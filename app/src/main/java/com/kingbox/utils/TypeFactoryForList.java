package com.kingbox.utils;

import android.content.Context;
import android.view.View;

import com.kingbox.R;
import com.kingbox.holder.BaseViewHolder;
import com.kingbox.holder.LiveFieldViewHolder;
import com.kingbox.holder.LiveFieldViewHolder1;
import com.kingbox.holder.TitleViewHolder;
import com.kingbox.service.entity.LiveField;
import com.kingbox.service.entity.Title;


/**
 * 多类型item工厂集合
 */
public class TypeFactoryForList implements TypeFactory {

    private final int TYPE_ITEM_TITLE = R.layout.item_title;  // 标题

    private final int TYPE_ITEM_LIVE_FIELD = R.layout.item_live_field;  // 秀场item

    private final int TYPE_ITEM_LIVE_FIELD1 = R.layout.first_live_field_list;


    @Override
    public int type(Title itemTiltle) {
        return TYPE_ITEM_TITLE;
    }

    @Override
    public int type(LiveField itemRoadShow, int type) {
        if (type == 2) {
            return TYPE_ITEM_LIVE_FIELD;
        } else {
            return TYPE_ITEM_LIVE_FIELD1;
        }
    }

    @Override
    public BaseViewHolder createViewHolder(Context context, int type, View itemView) {

        if (TYPE_ITEM_TITLE == type) {
            return new TitleViewHolder(context, itemView);
        } else if (TYPE_ITEM_LIVE_FIELD == type) {
            return new LiveFieldViewHolder(context, itemView);
        }else if (TYPE_ITEM_LIVE_FIELD1 == type) {
            return new LiveFieldViewHolder1(context, itemView);
        }
        return null;
    }

}
