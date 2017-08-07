package com.kingbox.service.presenter;


/**
 * Created by Administrator on 2017/7/11.
 */
public class TvPresenter /*implements Presenter*/ {
    /*private DataManager manager;
    private CompositeSubscription mCompositeSubscription;
    private Context mContext;
    private TvView mTvView;
    private Tv mTv;
    public TvPresenter (Context mContext){
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
        mTvView = (TvView) view;
    }

    @Override
    public void attachIncomingIntent(Intent intetn) {
    }
    public void getTvType(){
        mCompositeSubscription.add(manager.getTvType()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Tv>() {
                    @Override
                    public void onCompleted() {
                        if (mTv != null){
                            mTvView.onSuccess(mTv);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        mTvView.onError("请求失败！！");
                    }

                    @Override
                    public void onNext(Tv book) {
                        mTv = book;
                    }
                })
        );
    }

    public void getChannelTvList(String tvId){
        mCompositeSubscription.add(manager.getChannelTvList(tvId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Tv>() {
                    @Override
                    public void onCompleted() {
                        if (mTv != null){
                            mTvView.onSuccess(mTv);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        mTvView.onError("请求失败！！");
                    }

                    @Override
                    public void onNext(Tv book) {
                        mTv = book;
                    }
                })
        );
    }*/
}
