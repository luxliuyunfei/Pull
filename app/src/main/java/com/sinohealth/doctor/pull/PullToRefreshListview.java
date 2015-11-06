package com.sinohealth.doctor.pull;

import android.content.Context;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;

import in.srain.cube.views.loadmore.LoadMoreContainer;
import in.srain.cube.views.loadmore.LoadMoreHandler;
import in.srain.cube.views.loadmore.LoadMoreListViewContainer;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;
import in.srain.cube.views.ptr.header.MaterialHeader;

/**
 * Created by JJfly on 2015/11/2.
 */
public class PullToRefreshListview extends LinearLayout {

    private Context mContext;
    private PtrFrameLayout mPtrFrameLayout;
    private LoadMoreListViewContainer loadMoreContainer;
    private ListView mListview;
    private BaseAdapter adapter;
    private PullListener pl;

    public PullToRefreshListview(Context context) {
        super(context);
        this.mContext=context;
        init();
    }

    public PullToRefreshListview(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext=context;
        init();
    }

    public PullToRefreshListview(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext=context;
        init();
    }
    public  int dp2px( float dpVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dpVal, mContext.getResources().getDisplayMetrics());
    }
    private void init(){
        View view = LayoutInflater.from(mContext).inflate(R.layout.pull_listview, null);
        findId(view);
        this.addView(view);
    }

    private void findId(View view){
        mPtrFrameLayout = (PtrFrameLayout) view.findViewById(R.id.load_more_list_view_ptr_frame);
        mListview = (ListView) view.findViewById(R.id.load_more_small_image_list_view);
        loadMoreContainer = (LoadMoreListViewContainer) view.findViewById(R.id.load_more_list_view_container);
    }

    private void loadMore(){
        final LoadMoreListViewContainer loadMoreListViewContainer = (LoadMoreListViewContainer) this.findViewById(R.id.load_more_list_view_container);
        loadMoreListViewContainer.useDefaultHeader();
        //loadMoreListViewContainer.setAutoLoadMore(false)
        loadMoreListViewContainer.setLoadMoreHandler(new LoadMoreHandler() {
            @Override
            public void onLoadMore(LoadMoreContainer loadMoreContainer) {
                mPtrFrameLayout.refreshComplete();
                pl.onLoadMore(loadMoreContainer,adapter);
            }
        });


        // auto load data
        mPtrFrameLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                mPtrFrameLayout.autoRefresh(false);
            }
        }, 150);
    }

    private void pullList(){
        final MaterialHeader header = new MaterialHeader(mContext);
        int[] colors = getResources().getIntArray(R.array.google_colors);
        header.setColorSchemeColors(colors);
        header.setLayoutParams(new PtrFrameLayout.LayoutParams(-1, -2));
        header.setPadding(0, dp2px(25), 0, dp2px(20));
        header.setPtrFrameLayout(mPtrFrameLayout);

        mPtrFrameLayout.setLoadingMinTime(1000);
        mPtrFrameLayout.setDurationToCloseHeader(1500);
        mPtrFrameLayout.setHeaderView(header);
        mPtrFrameLayout.addPtrUIHandler(header);
        mPtrFrameLayout.setPinContent(true);

        mPtrFrameLayout.setPtrHandler(new PtrHandler() {
            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
                //检测mListview是否有内容
                return PtrDefaultHandler.checkContentCanBePulledDown(frame, mListview, header);
            }

            @Override
            public void onRefreshBegin(final PtrFrameLayout frame) {
                pl.onRefresh(frame, adapter);

            }
        });
    }

    public ListView getListview(){
        return  mListview;
    }

    /**
     *
     * @param auto2Load 是否自动加载 setAdapter(**)之前调
     */
    private void autoLoadMore(boolean auto2Load){
        mPtrFrameLayout.autoRefresh(auto2Load);

    }

    private void autoRefresh(boolean autoRefresh){

    }


    public void refreshComplete(){
        mPtrFrameLayout.refreshComplete();
        mPtrFrameLayout.scrollTo(0, 0);
        adapter.notifyDataSetChanged();
    }

    public void loadMoreFinish(boolean emptyResult, boolean hasMore){
        loadMoreContainer.loadMoreFinish(emptyResult, hasMore);
        adapter.notifyDataSetChanged();
    }


    public void setAdapter(BaseAdapter adapter,PullListener pl){
        this.adapter=adapter;
        this.pl=pl;
        pullList();
        loadMore();
        mListview.setAdapter(adapter);
    }

    public interface PullListener{
        void onRefresh(PtrFrameLayout frame, BaseAdapter adapter);
        void onLoadMore(LoadMoreContainer loadMoreContainer, BaseAdapter adapter);
    }
}
