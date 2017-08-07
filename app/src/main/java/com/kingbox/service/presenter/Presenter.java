package com.kingbox.service.presenter;

import android.content.Intent;
import com.kingbox.service.view.View;

/**
 * 业务逻辑处理基础接口
 */
public interface Presenter {
    void onCreate();

    void onStart();

    void onStop();

    void pause();

    void attachView(View view);

    void attachIncomingIntent(Intent intent);
}
