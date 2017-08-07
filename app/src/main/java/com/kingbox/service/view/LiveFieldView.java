package com.kingbox.service.view;

import com.kingbox.service.entity.LiveField;

/**
 * Created by Administrator on 2017/7/11.
 */
public interface LiveFieldView extends View {
    void onSuccess(LiveField mLiveField);

    void onError(String result);
}
