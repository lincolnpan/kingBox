package com.kingbox.ui.fragment;

import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.ProgressBar;

import com.google.gson.Gson;
import com.kingbox.R;
import com.kingbox.adapter.MultiTypeAdapter;
import com.kingbox.service.entity.Banner;
import com.kingbox.service.entity.LiveField;
import com.kingbox.service.entity.Title;
import com.kingbox.service.entity.Visitable;
import com.kingbox.view.FullLinearLayoutManager;
import com.kingbox.view.PullBaseView;
import com.kingbox.view.PullRecyclerView;
import com.kingbox.view.RecycleViewDivider;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.FileCallBack;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import okhttp3.Call;
import okhttp3.Request;

/**
 * Created by Administrator on 2017/7/14.
 */
public class SquareFragment extends LazyLoadFragment implements PullBaseView.OnRefreshListener {

    @BindView(R.id.live_square)
    PullRecyclerView recyclerView;

    @BindView(R.id.progress)
    ProgressBar progressBar;

    private MultiTypeAdapter adapter = null;

    private List<Visitable> list = new ArrayList<>();

    private LiveField tempLiveField = new LiveField(1);

    private List<LiveField> tempList;


    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 3333:    // 处理banner
                    List<Banner> banners = (List<Banner>) msg.obj;
                    Title t = new Title("国内直播秀场", 1);
                    t.setBannerList(banners);
                    if (list.size() > 0 && list.get(0) instanceof Title) {
                        list.remove(0);
                    }
                    list.add(t);

                    adapter.notifyItemChanged(0);

                case 4444:
                    getData();
                    break;
                case 1111:    // 处理直播
                    getPageLives();
                    break;
                case 5555:
                    List<LiveField> tempLives = (List<LiveField>) msg.obj;
                    if (tempLives.size() < LIMIT) {
                        recyclerView.setCanPullUp(false);
                    }else {
                        recyclerView.setCanPullUp(true);
                    }

                    List<LiveField> lfs = tempLiveField.getLiveFieldList();
                    if (null == lfs) {
                        lfs = new ArrayList<>();
                    }

                    if (page > 1){
                        recyclerView.onFooterRefreshComplete(lfs.size() - LIMIT);
                    } else {
                        lfs.clear();
                    }
                    lfs.addAll(tempLives);
                case 2222:
                    if (1 == page) {
                        Visitable tempVisitable = null;
                        for (Visitable visitable : list) {
                            if (visitable instanceof LiveField) {
                                tempVisitable = visitable;
                                break;
                            }
                        }
                        if (null != tempVisitable) {
                            list.remove(tempVisitable);
                        }
                        list.add(tempLiveField);
                    }
                    adapter.notifyItemChanged(1);
                    recyclerView.onHeaderRefreshComplete();
                    break;
            }
        }
    };

    private int page = 1;
    private int index = 0;
    private static final int LIMIT = 90;

    private void getPageLives(){
        handler.post(new Runnable() {
            @Override
            public void run() {
                List<LiveField> pageLives = new ArrayList<>();
                int count = page * LIMIT;
                for (int i = index; i < tempList.size(); i++) {
                    if (i == count) {
                        index += LIMIT;
                        break;
                    }
                    pageLives.add(tempList.get(i));
                }
                Message message = new Message();
                message.what = 5555;
                message.obj = pageLives;
                handler.handleMessage(message);
            }
        });
    }

    public SquareFragment(){}

    @Override
    public int getLayout() {
        return R.layout.square_fragment;
    }

    @Override
    public void initViews(View view) {

        // 设置布局管理
        recyclerView.setLayoutManager(new FullLinearLayoutManager(getActivity()));

        // 设置item间隔
        recyclerView.addItemDecoration(new RecycleViewDivider(
                getActivity(), LinearLayoutManager.VERTICAL, 12, getResources().getColor(R.color.transparent)));

        recyclerView.setCanScrollAtRereshing(true);
        recyclerView.setOnRefreshListener(this);   // 刷新监听
        recyclerView.setCanPullDown(true);   // 下拉刷新
        recyclerView.setCanPullUp(false);   // 加载跟多


        if (null == adapter) {
            adapter = new MultiTypeAdapter(list);
        }
        recyclerView.setAdapter(adapter);
        recyclerView.setVisibility(View.VISIBLE);

    }

    @Override
    public void loadData() {
        recyclerView.headerRefreshing();
    }

    private void getBannerData() {

        OkHttpUtils.get().url("http://kingbox.donewe.com/API/GetAds?typeId=1").tag(103)   // 请求Id
                .build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                handler.sendEmptyMessage(4444);
            }

            @Override
            public void onResponse(String response, int id) {

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.has("Success") && jsonObject.getBoolean("Success")) {
                        if (jsonObject.has("Content")) {
                            JSONArray resultsJson = jsonObject.getJSONArray("Content");
                            if (null != resultsJson && resultsJson.length() > 0) {
                                List<Banner> portfolioList = new ArrayList<>();
                                for (int i = 0; i < resultsJson.length(); i++) {
                                    JSONObject resultJson = resultsJson.getJSONObject(i);
                                    Gson gson = new Gson();
                                    Banner banner = gson.fromJson(resultJson.toString(), Banner.class);
                                    portfolioList.add(banner);
                                }
                                Message msg = new Message();
                                msg.what = 3333;
                                msg.obj = portfolioList;
                                handler.handleMessage(msg);
                                return;
                            }
                        }
                    }
                } catch (JSONException e) {
                }
                handler.sendEmptyMessage(4444);
                return;
            }
        });
    }

    private void getData() {
        //http://kingbox.donewe.com/api/Getfilteredcloudmenus
        //http://kingbox.donewe.com/API/GetCloudMenus
        OkHttpUtils.get().url("http://kingbox.donewe.com/api/Getfilteredcloudmenus")
                .tag(104)
                .build().execute(new FileCallBack(Environment.getExternalStorageDirectory().getAbsolutePath(), "yunData.txt")
        {

            @Override
            public void onBefore(Request request, int id) {
            }

            @Override
            public void inProgress(float progress, long total, int id) {
            }

            @Override
            public void onError(Call call, Exception e, int id) {
                handler.sendEmptyMessage(2222);
            }

            @Override
            public void onResponse(File file, int id) {
                try {
                    tempList = new ArrayList<>();
                    InputStreamReader isr = new InputStreamReader(new FileInputStream(file), "UTF-8");
                    BufferedReader br = new BufferedReader(isr);
                    String readline;
                    while ((readline = br.readLine()) != null) {
                        if (!readline.contains("无主播在线") && !readline.contains("更新时间")) {
                            LiveField liveField = new LiveField(1);
                            liveField.setMsg(readline);
                            tempList.add(liveField);
                        }
                    }
                    br.close();
                    handler.sendEmptyMessage(1111);

                } catch (Exception e) {
                    handler.sendEmptyMessage(2222);
                }
            }
        });


        /*OkHttpUtils.get().url("http://bee.donewe.com/yuncaidan.txt")   //http://bee.donewe.com/nijing.txt
                .build().execute(new FileCallBack(Environment.getExternalStorageDirectory().getAbsolutePath(), "yunData.txt")
        {

            @Override
            public void onBefore(Request request, int id) {
            }

            @Override
            public void inProgress(float progress, long total, int id) {
            }

            @Override
            public void onError(Call call, Exception e, int id) {
            }

            @Override
            public void onResponse(File file, int id) {
                try {
                    tempList = new ArrayList<>();
                    InputStreamReader isr = new InputStreamReader(new FileInputStream(file), "GBK");
                    BufferedReader br = new BufferedReader(isr);
                    String readline;
                    while ((readline = br.readLine()) != null) {
                        LiveField liveField = new LiveField(2);
                        liveField.setMsg(readline);
                        tempList.add(liveField);
                    }
                    if (tempList.size() > 0) {
                        tempList.remove(tempList.size() - 1);
                    }
                    br.close();

                    new Handler().post(new Runnable() {
                        @Override
                        public void run() {
                            for(LiveField lf : tempList){
                                String mesg = lf.getMsg();
                                int index = mesg.indexOf("@dz");
                                int a = mesg.indexOf("@mc");
                                int b = mesg.indexOf("|@tp");
                                String img = mesg.substring(b + 4, index-1);
                                String roomName = mesg.substring(a + 3, b);
                                String url = mesg.substring(index + 3, mesg.length() - 1);
                                Log.i("liveUrl", url + "==== 文件名");
                                getItemData(url,img,roomName);
                            }
                        }
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });*/
    }



    //private List<LiveField> allLiveUrl = new ArrayList<>();
    /*
    private int i = 0;
    private synchronized void getItemData(String url, final String img, final String roomName) {
        OkHttpUtils.get().url("http://bee.donewe.com/" + url + ".txt")
                .build().execute(new FileCallBack(Environment.getExternalStorageDirectory().getAbsolutePath(), "y11unData.txt")
        {

            @Override
            public void onBefore(Request request, int id) {
            }

            @Override
            public void inProgress(float progress, long total, int id) {
            }

            @Override
            public void onError(Call call, Exception e, int id) {
            }

            @Override
            public void onResponse(File file, int id) {
                try {
                    i++;

                    List<LiveField> zitempList = new ArrayList<>();
                    InputStreamReader isr = new InputStreamReader(new FileInputStream(file), "GBK");
                    BufferedReader br = new BufferedReader(isr);
                    String readline = "";
                    while ((readline = br.readLine()) != null) {
                        if ("无主播在线".equals(readline))
                            return;
                        LiveField liveField = new LiveField(2);
                        liveField.setMsg(readline);
                        liveField.setImg(img);
                        liveField.setRoomName(roomName);
                        zitempList.add(liveField);
                        Log.i("liveUrl", readline + "==== 直播名");
                    }
                    zitempList.remove(zitempList.size() - 1);
                    br.close();
                    //allLiveUrl.addAll(tempList);

                    if (zitempList.size() > 0) {
                        tempLiveField.getLiveFieldList().addAll(zitempList);

                        adapter.notifyDataSetChanged();

                        if (progressBar.getVisibility() == View.VISIBLE) {
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }*/


    @Override
    public void onDestroy() {
        super.onDestroy();
        //liveFieldPresenter.onStop();
        OkHttpUtils.getInstance().cancelTag(103);
        OkHttpUtils.getInstance().cancelTag(104);
        OkHttpUtils.getInstance().cancelTag(108);
    }

    @Override
    public void onHeaderRefresh(PullBaseView view) {
        page = 1;
        index = 0;
        getBannerData();
    }

    @Override
    public void onFooterRefresh(PullBaseView view) {
        page ++;
        handler.sendEmptyMessage(1111);
    }
}
