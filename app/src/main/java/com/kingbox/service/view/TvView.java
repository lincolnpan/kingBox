package com.kingbox.service.view;

import com.kingbox.service.entity.Tv;

/**
 * Created by Administrator on 2017/7/11.
 */
public interface TvView extends View {
    void onSuccess(Tv mTv);

    void onError(String result);
}
