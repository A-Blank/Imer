package com.imer.View;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.imer.Interface.CallBack;
import com.imer.Interface.ListViewCallBack;
import com.imer.R;


/**
 * Created by 丶 on 2017/2/26.
 */

public class MyListView extends ListView implements AbsListView.OnScrollListener {


    private int padding;
    private RelativeLayout relativeLayout;
    private LayoutInflater inflater;
    private View view;
    private Context context;
    private float currentX,currnetY,oldX,oldY;

    private ListViewCallBack callBack;
    private int firstItemIndex;
    private int height;
    private boolean isRefresh=false;

    private RotateAnimation rotateAnimation;

    private Animation animation;
    private TextView textView_updown;
    private AnimationSet animationSet;

    private int CURRENT_STATE=0;

    public MyListView(Context context) {
        super(context);
    }

    public MyListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context=context;
        Init();
    }

    public MyListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void Init(){

//        animationSet=new AnimationSet(true);
//        rotateAnimation = new RotateAnimation(0, -180, RotateAnimation.RELATIVE_TO_SELF, 0.5f, RotateAnimation.RELATIVE_TO_SELF, 0.5f);
//        rotateAnimation.setInterpolator(new LinearInterpolator());
//        rotateAnimation.setDuration(250);
//        rotateAnimation.setFillAfter(true);
//        animationSet.addAnimation(rotateAnimation);
        animation= AnimationUtils.loadAnimation(context, R.anim.rotate);
        animation.setFillAfter(true);
        relativeLayout= (RelativeLayout) findViewById(R.id.head);
        inflater=LayoutInflater.from(context);
        view=inflater.inflate(R.layout.item_listview_head,null);
        textView_updown= (TextView) view.findViewById(R.id.tv_head_updown);
        /**
         * 强制测量
         */
        view.post(new Runnable() {
            @Override
            public void run() {
                height=view.getHeight();
                view.setPadding(0,-1*height,0,0);
                padding=height;
                Log.i("TAG","padding"+height+"");
            }
        });
        addHeaderView(view);
        setOnScrollListener(this);
//        Log.i("TAG", "Init: "+view.getMeasuredHeight());

    }



    @Override
    public boolean onTouchEvent(MotionEvent ev) {


        switch(ev.getAction())
        {
            case MotionEvent.ACTION_DOWN:
                oldX=ev.getX();
                oldY=ev.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                //获取当前坐标
                currentX=ev.getX();
                currnetY=ev.getY();
                int sub=(int)(currnetY-oldY);
                Log.i("TAG",sub+"");
//                view.setAnimation(animation);
                if(firstItemIndex==0&&sub>0&&!isRefresh){
                    view.setPadding(0,sub/3-padding,0,0);
                    padding=padding-sub/3;
                    TextView textView;
                    textView= (TextView) view.findViewById(R.id.tv_head);
                    if(-padding>=0&&CURRENT_STATE==0){
                        textView_updown.startAnimation(animation);
                        textView.setText("释放刷新");
                        CURRENT_STATE=1;
                    }
                    else if(-padding<0&&CURRENT_STATE==1){
                        textView_updown.startAnimation(animation);
                        textView.setText("下拉刷新");
                        textView_updown.startAnimation(animation);
                        CURRENT_STATE=0;
                    }
//                    Log.i("TAG","padding="+padding);
                }
                break;
            case MotionEvent.ACTION_UP:
                //刷新
                if(-padding>=0){
                    CURRENT_STATE=2;
                    //接口回调
                    ((ListViewCallBack)context).Refresh();
                }
                TextView textView= (TextView) view.findViewById(R.id.tv_head);
                textView.setText("下拉刷新");
                view.setPadding(0,-height,0,0);
                padding=height;
                CURRENT_STATE=0;
                break;
        }
        oldX=ev.getX();
        oldY=ev.getY();
        return super.onTouchEvent(ev);
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

        firstItemIndex=firstVisibleItem;

    }
}
