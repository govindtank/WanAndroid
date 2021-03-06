package com.example.gab.babylove.ui.news.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.example.gab.babylove.R;
import com.example.gab.babylove.ui.news.activity.SystemActivity;
import com.example.gab.babylove.ui.news.adapter.NewsAdapter;
import com.example.gab.babylove.api.ApiService;
import com.example.gab.babylove.entity.TreeBean;
import com.fy.baselibrary.base.BaseFragment;
import com.fy.baselibrary.retrofit.BeanModule;
import com.fy.baselibrary.retrofit.NetCallBack;
import com.fy.baselibrary.retrofit.RequestUtils;
import com.fy.baselibrary.retrofit.dialog.IProgressDialog;
import com.fy.baselibrary.statusbar.MdStatusBar;
import com.fy.baselibrary.utils.JumpUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Gab on 2017/12/15 0015.
 * 体系 主Fragment
 */

public class NewsFragment extends BaseFragment {

    @BindView(R.id.rv_title)
    RecyclerView mRecyclerView;
    NewsAdapter mAdapter;


    @Override
    protected int setContentLayout() {
        return R.layout.fragment_news;
    }

    @Override
    protected void baseInit() {
        super.baseInit();
        initRecyle();
        getArticleList();
        MdStatusBar.setColorBar(getActivity(), R.color.statusBar, R.color.statusBar);
    }

    /**
     * 列表数据加载
     */
    @SuppressLint("CheckResult")
    private void getArticleList() {
        IProgressDialog progressDialog = new IProgressDialog().init((AppCompatActivity) getActivity()).setDialogMsg(R.string.loading_get);
        RequestUtils.create(ApiService.class)
                .getTreeList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new NetCallBack<BeanModule<List<TreeBean>>>(progressDialog) {
                    @Override
                    protected void onSuccess(BeanModule<List<TreeBean>> listBeanModule) {
                        if (null != listBeanModule && null != listBeanModule.getData()) {
                            mAdapter.setNewData(listBeanModule.getData());
                        }
                    }

                    @Override
                    protected void updataLayout(int flag) {

                    }
                });
    }

    /**
     * recycleview 相关设置
     */
    private void initRecyle() {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mAdapter = new NewsAdapter(R.layout.item_news, new ArrayList<>());
        mAdapter.setOnItemClickListener((adapter, view, position) -> {
            TreeBean bean = mAdapter.getData().get(position);
            Bundle bundle = new Bundle();
            bundle.putSerializable("bean",bean);
            JumpUtils.jump(mContext, SystemActivity.class, bundle);// 详情
        });
        mRecyclerView.setAdapter(mAdapter);
    }

}
