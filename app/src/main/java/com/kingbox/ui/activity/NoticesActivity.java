package com.kingbox.ui.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.kingbox.R;
import com.kingbox.service.entity.Notice;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.Call;

/**
 * Created by lincolnpan on 2017/7/18.
 */
public class NoticesActivity extends BaseActivity {

    /**
     * 标题
     */
    @BindView(R.id.center_title_tv)
    TextView titleTV = null;

    @BindView(R.id.text)
    TextView text = null;

    @BindView(R.id.user_img)
    ImageView userImg;

    private int type = 1;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1111:
                    List<Notice> list = (List<Notice>) msg.obj;
                    if (null != list && list.size() > 0) {
                        Notice notice = list.get(list.size() - 1);
                        titleTV.setText(notice.getTitle());
                        text.setText(notice.getContent());
                    }
                    break;
                case 2222:

                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notices);

        userImg.setVisibility(View.GONE);
        titleTV.setText("");

        type = getIntent().getIntExtra("type", 1);
        //
        OkHttpUtils
                .get()
                .url("http://kingbox.donewe.com/API/GetNotices?typeId=" + type)
                .id(101)   // 请求Id
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        handler.sendEmptyMessage(2222);
                    }

                    @Override
                    public void onResponse(String response, int id) {

                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.has("Success") && jsonObject.getBoolean("Success")) {
                                if (jsonObject.has("Content")) {
                                    JSONArray resultsJson = jsonObject.getJSONArray("Content");
                                    if (null != resultsJson && resultsJson.length() > 0) {
                                        List<Notice> portfolioList = new ArrayList<>();
                                        for (int i = 0; i < resultsJson.length(); i++) {
                                            JSONObject resultJson = resultsJson.getJSONObject(i);
                                            Gson gson = new Gson();
                                            Notice banner = gson.fromJson(resultJson.toString(), Notice.class);
                                            portfolioList.add(banner);
                                        }
                                        Message msg = new Message();
                                        msg.what = 1111;
                                        msg.obj = portfolioList;
                                        handler.handleMessage(msg);
                                        return;
                                    }
                                }
                            }
                        } catch (JSONException e) {
                        }
                        handler.sendEmptyMessage(2222);
                        return;
                    }
                });
    }

    @OnClick(R.id.back_img)
    public void back(){
        finish();
    }
}
