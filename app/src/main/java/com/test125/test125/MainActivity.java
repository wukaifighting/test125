package com.test125.test125;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 轮播图
 * <p>
 * 1//将轮播图片和文本放在集合中
 * 2//应用启动的时候默认一组图片和文本
 * 3//需要定时器，目的；每隔一段时间替换文本和图片
 * 4//同时实现手指滑动图片达到轮播效果
 */
public class MainActivity extends AppCompatActivity {

    ViewPager mViewpager;
    ViewpagerAdapter mAdapter;
    List<ImageView> mImg;
    List<View> mDots;
    TextView mTxt;
    //图片信息
    String[] titles = new String[]{
            "妹妹", "哥哥", "弟弟", "姐姐"
    };
    //存放图片id
    int imgid[] = new int[]{
            R.mipmap.a, R.mipmap.aa, R.mipmap.aaaaa, R.mipmap.bbbbbbbbb
    };
    //更新小点
    private int oldPosition = 0;
    private int currentItem;

    //线程池，用来定时轮播
    private ScheduledExecutorService mSchedule;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }
    @Override
    protected void onStart() {
        super.onStart();
        //开启一个单个后台线程
      mSchedule=Executors.newSingleThreadScheduledExecutor();
        //给线程添加一个定时的调度任务
        //任务，时间（延迟多少时间后执行任务），时间（按照这个时间周期性重复执行任务）, TimeUnit.SECONDS
       mSchedule.scheduleWithFixedDelay(
               new ViewPagerTask(),5,4, TimeUnit.SECONDS
        );
    }

    private class ViewPagerTask implements Runnable{
        @Override
        public void run() {
            //用取余的方式来确认currentItem
            currentItem = (currentItem + 1) % imgid.length;
            mhandler.sendEmptyMessage(0);//就是为了调动下handler，为了更新UI
        }
    }

    private Handler mhandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            //更新viewpager当前显示的pager
            mViewpager.setCurrentItem(currentItem);
        }
    };


    private void init() {
        mViewpager = (ViewPager) findViewById(R.id.vpg_main);
        //显示图片的集合
        mImg = new ArrayList<>();
        for (int i = 0; i < imgid.length; i++) {
            ImageView imagview = new ImageView(this);
            imagview.setBackgroundResource(imgid[i]);
            mImg.add(imagview);
        }
        //显示小点
        mDots = new ArrayList<>();
        mDots.add(findViewById(R.id.view_normal0));
        mDots.add(findViewById(R.id.view_normal1));
        mDots.add(findViewById(R.id.view_normal2));
        mDots.add(findViewById(R.id.view_normal3));
        //显示图片标题
        mTxt = (TextView) findViewById(R.id.txt_main);
        mTxt.setText(titles[0]);
        mAdapter = new ViewpagerAdapter();
        mViewpager.setAdapter(mAdapter);
        mViewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                //标题的改变
                mTxt.setText(titles[position]);
                //小点的改变
                mDots.get(position).setBackgroundResource(R.drawable.dot_normal);
                mDots.get(oldPosition).setBackgroundResource(R.drawable.dot_press);

                oldPosition = position;
                currentItem = position;//做轮播的时候会用到
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private class ViewpagerAdapter extends PagerAdapter {
        //获取当前界面数量
        @Override
        public int getCount() {
            return mImg.size();
        }

        //用于判断界面是否有对象生成
        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        //return--一个对象，这的对象表明了PAGERADAPTER选择哪个对象当如当前的viewpager
        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(mImg.get(position));
            return mImg.get(position);
        }

        //从viewgroup中移除当前的view
        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(mImg.get(position));
        }
    }
}
