package com.kingbox.ui.fragment;

import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.kingbox.R;
import com.kingbox.ui.activity.MainActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * 直播广场
 */
public class LiveSquareFragment1 extends LazyLoadFragment {

    @BindView(R.id.vp)
    ViewPager vp;

    private List<Fragment> fragmentList = new ArrayList<>();

    private MyPagerAdapter mAdapter;

    public LiveSquareFragment1() {
    }

    @Override
    public int getLayout() {
        return R.layout.live_square_layout1;
    }

    @Override
    public void initViews(View view) {
        fragmentList.clear();
        fragmentList.add(new SquareFragment());
        fragmentList.add(new ForeignFragment());
        fragmentList.add(new ChannelFragment());
        //fragmentList.add(new LiveSquareFragment());

        mAdapter = new MyPagerAdapter(getChildFragmentManager());
        vp.setOffscreenPageLimit(3);   // 切换不重复加载
        vp.setAdapter(mAdapter);

        vp.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(final int position) {
                new Handler().post(new Runnable() {
                    @Override
                    public void run() {
                        ((MainActivity) getActivity()).setVpTitle(position);
                    }
                });
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        vp.setCurrentItem(0);
    }


    @Override
    public void loadData() {
    }

    public void setVP(int index) {
        vp.setCurrentItem(index);
    }

    private class MyPagerAdapter extends FragmentStatePagerAdapter {
        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return fragmentList.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return "";
        }

        @Override
        public Fragment getItem(int position) {
            return fragmentList.get(position);
        }
    }
}
