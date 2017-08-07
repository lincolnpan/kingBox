package com.kingbox.service.presenter;

/**
 *  直播秀场业务处理
 * Created by Administrator on 2017/7/11.
 */
public class LiveFieldPresenter /*implements Presenter*/ {
    /*private DataManager manager;
    private CompositeSubscription mCompositeSubscription;
    private Context mContext;
    private LiveFieldView mLiveFieldView;
    private LiveField mLiveField;
    public LiveFieldPresenter (Context mContext){
        this.mContext = mContext;
    }
    @Override
    public void onCreate() {
        manager = new DataManager(mContext);
        mCompositeSubscription = new CompositeSubscription();
    }

    @Override
    public void onStart() {

    }

    @Override
    public void onStop() {
        if (mCompositeSubscription.hasSubscriptions()){
            mCompositeSubscription.unsubscribe();
        }
    }

    @Override
    public void pause() {

    }

    @Override
    public void attachView(View view) {
        mLiveFieldView = (LiveFieldView)view;
    }

    @Override
    public void attachIncomingIntent(Intent intetn) {
    }
    public void getLiveFields(){
        mCompositeSubscription.add(manager.getLiveFields()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<LiveField>() {
                    @Override
                    public void onCompleted() {
                        if (mLiveField != null){
                            mLiveFieldView.onSuccess(mLiveField);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        mLiveFieldView.onError("请求失败！！");
                    }

                    @Override
                    public void onNext(LiveField book) {
                        mLiveField = book;
                    }
                })
        );
    }

    public void getYunCaiDan() {

        manager.getYunCaiDan().enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                mLiveFieldView.onError("下载失败");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                boolean isSuccess = saveFile(response.body().byteStream());
                if (isSuccess) {
                    mLiveFieldView.onSuccess(null);
                }
            }
        });
    }

    private boolean saveFile(InputStream is) {
        try {
            String fn = Environment.getExternalStorageDirectory() + "/yuncaidan.txt";
            FileOutputStream fos = new FileOutputStream(fn);
            byte[] buf = new byte[1024];
            int len;
            while ((len = is.read(buf)) != -1) {
                fos.write(buf, 0, len);
            }
            is.close();
            fos.close();
            return  true;
        } catch (Exception ex) {
            return false;
        }
    }*/
}
