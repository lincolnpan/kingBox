package com.kingbox.service.presenter;


/**
 * Created by Administrator on 2017/7/11.
 */

public class CinemaPresenter /*implements Presenter*/ {
    /*private DataManager manager;
    private CompositeSubscription mCompositeSubscription;
    private Context mContext;
    private CinemaView mCinemaView;
    private Cinema mCinema;
    public CinemaPresenter (Context mContext){
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
        mCinemaView = (CinemaView)view;
    }

    @Override
    public void attachIncomingIntent(Intent intetn) {
    }
    public void getCinemaList(){
        mCompositeSubscription.add(manager.getCinemaList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Cinema>() {
                    @Override
                    public void onCompleted() {
                        if (mCinema != null){
                            mCinemaView.onSuccess(mCinema);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        mCinemaView.onError("请求失败！！");
                    }

                    @Override
                    public void onNext(Cinema book) {
                        mCinema = book;
                    }
                })
        );
    }*/
}
