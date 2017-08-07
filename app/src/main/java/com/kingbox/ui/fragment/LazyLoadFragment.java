package com.kingbox.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;

/**
 * 懒加载fragment基类
 * （执行顺序 PS: setUserVisibleHint(false) -> onAttach -> onCreate -> setUserVisibleHint(true)  -> onCreateView -> onActivityCreated ->.... -> onDetach）
 */
public abstract class LazyLoadFragment extends Fragment {

    /**
     * 控件是否初始化完成
     */
    private boolean isViewCreated;
    /**
     * 数据是否已加载完毕
     */
    private boolean isLoadDataCompleted;

    private View view = null;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (view != null) {
            ViewGroup viewGroup = (ViewGroup) view.getParent();
            if (viewGroup != null) {
                viewGroup.removeView(view);
            }
        } else {
            view = inflater.inflate(getLayout(),
                    container, false);
        }
        ButterKnife.bind(this, view);
        initViews(view);
        isViewCreated = true;
        return view;
    }

    public abstract int getLayout();

    public abstract void initViews(View view);

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && isViewCreated && !isLoadDataCompleted) {
            isLoadDataCompleted = true;
            loadData();
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (getUserVisibleHint()) {
            isLoadDataCompleted = true;
            loadData();
        }
    }

    /**
     * 子类实现加载数据的方法
     */
    public abstract void loadData();


}