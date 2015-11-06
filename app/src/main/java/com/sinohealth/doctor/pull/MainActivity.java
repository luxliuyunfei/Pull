package com.sinohealth.doctor.pull;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import in.srain.cube.views.loadmore.LoadMoreContainer;
import in.srain.cube.views.ptr.PtrFrameLayout;

/**
 * Created by JJfly on 2015/11/2.
 */
public class MainActivity extends Activity implements PullToRefreshListview.PullListener{

    private List<String> data;

private String TAG = "MainActivity :    ";
    private PullToRefreshListview pullToRefreshListview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.testlayout);
        initData();
        initListview();
        pullToRefreshListview = (PullToRefreshListview)this.findViewById(R.id.listview);
        pullToRefreshListview.setAdapter(adapter, this);
    }
    private int more;

    private int count;
private BaseAdapter adapter;


    private void initListview(){

        adapter = new BaseAdapter() {
            @Override
            public int getCount() {
                return data.size();
            }

            @Override
            public Object getItem(int position) {
                return data.get(position);
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if (convertView==null){
                    convertView = LayoutInflater.from(MainActivity.this).inflate(R.layout.listitem,parent,false);
                }
                TextView textView = (TextView)convertView.findViewById(R.id.text);
                textView.setText(data.get(position));
                return convertView;
            }
        };

    }

    private void initData(){
        data = new ArrayList<String>();
        for (int i=0;i<50;i++){
            data.add("item"+i);
        }
    }

    @Override
    public void onRefresh(final PtrFrameLayout frame,final BaseAdapter baseAdapter) {
Toast.makeText(MainActivity.this, "onRefresh", Toast.LENGTH_SHORT).show();
        frame.postDelayed(new Runnable() {
            @Override
            public void run() {
                data.add(0, "Refresh" + ++count);
                pullToRefreshListview.refreshComplete();
            }
        }, 1800);
    }

    @Override
    public void onLoadMore(final LoadMoreContainer loadMoreContainer,final BaseAdapter baseAdapter) {
        Toast.makeText(MainActivity.this, "onLoadMore", Toast.LENGTH_SHORT).show();
        new android.os.Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                  data.add(data.size(), "more" + ++more);
                pullToRefreshListview.loadMoreFinish(false,true);

            }
        },1800);

    }
}
