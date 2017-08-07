package com.kingbox.ui.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.kingbox.R;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by Administrator on 2017/7/11.
 */

public class BoxExplainActivity extends BaseActivity {

    @BindView(R.id.back_img)
    ImageView backImg;
    @BindView(R.id.center_title_tv)
    TextView centerTitleTv;
    @BindView(R.id.user_img)
    ImageView userImg;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_box_explain);
        centerTitleTv.setText("宝盒说明");
        userImg.setVisibility(View.GONE);
    }

    @OnClick(R.id.back_img)
    public void onClick(){
        finish();
    }
}
