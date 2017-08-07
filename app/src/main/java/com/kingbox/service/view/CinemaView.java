package com.kingbox.service.view;

import com.kingbox.service.entity.Cinema;

/**
 * Created by Administrator on 2017/7/11.
 */
public interface CinemaView extends View {
    void onSuccess(Cinema mCinema);

    void onError(String result);
}
