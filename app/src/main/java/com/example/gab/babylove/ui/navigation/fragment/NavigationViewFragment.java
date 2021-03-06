package com.example.gab.babylove.ui.navigation.fragment;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.example.gab.babylove.R;
import com.example.gab.babylove.ui.navigation.adapter.NavigationCidAdapter;
import com.example.gab.babylove.ui.navigation.adapter.NavigationViewAdapter;
import com.example.gab.babylove.api.ApiService;
import com.example.gab.babylove.entity.NavigationBean;
import com.example.gab.babylove.web.AgentWebActivity;
import com.fy.baselibrary.base.BaseFragment;
import com.fy.baselibrary.retrofit.BeanModule;
import com.fy.baselibrary.retrofit.NetCallBack;
import com.fy.baselibrary.retrofit.RequestUtils;
import com.fy.baselibrary.retrofit.dialog.IProgressDialog;
import com.fy.baselibrary.statusbar.MdStatusBar;
import com.fy.baselibrary.utils.JumpUtils;
import com.google.android.flexbox.AlignItems;
import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexWrap;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.flexbox.JustifyContent;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Gab on 2017/12/15 0015.
 * 导航数据
 */

public class NavigationViewFragment extends BaseFragment {

    @BindView(R.id.rv_title)
    RecyclerView mRecyclerView_Title;
    @BindView(R.id.rv_context)
    RecyclerView mRecyclerView_Context;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    NavigationViewAdapter mAdapter;
    NavigationCidAdapter mNavigationCidAdapter;

    private int mSelectedPos = 0;//实现单选 变量保存当前选中的position
    private LinearLayoutManager mLinearLayoutManager;

    @Override
    protected int setContentLayout() {
        return R.layout.fragment_navigation;
    }

    @Override
    protected void baseInit() {
        super.baseInit();
        mToolbar.setTitle("首页");
        MdStatusBar.setColorBar(getActivity(), R.color.statusBar, R.color.statusBar);
        initRecyle();
        initRecyleCid();
        getNaviList();

    }

    /**
     * 列表数据加载
     */
    private void getNaviList() {
        IProgressDialog progressDialog = new IProgressDialog().init((AppCompatActivity) getActivity()).setDialogMsg(R.string.loading_get);
        RequestUtils.create(ApiService.class)
                .getNaviList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new NetCallBack<BeanModule<List<NavigationBean>>>(progressDialog) {
                    @Override
                    protected void onSuccess(BeanModule<List<NavigationBean>> navigationBeanBeanModule) {
                        if (null != navigationBeanBeanModule && null != navigationBeanBeanModule.getData()) {
                            mAdapter.setNewData(navigationBeanBeanModule.getData());
                            if (mSelectedPos == 0) {
                                mAdapter.getData().get(mSelectedPos).setSelected(true);
                                mNavigationCidAdapter.setNewData(navigationBeanBeanModule.getData().get(mSelectedPos).getArticles());
                            }
                        }
                    }

                    @Override
                    protected void updataLayout(int flag) {

                    }
                });
    }


    private void initRecyle() {
        mLinearLayoutManager = new LinearLayoutManager(mContext);
        mRecyclerView_Title.setLayoutManager(mLinearLayoutManager);
        mAdapter = new NavigationViewAdapter(R.layout.item_navigation, new ArrayList<>());
        mAdapter.setOnItemClickListener((adapter, view, position) -> {
            NavigationBean navigationBean = mAdapter.getData().get(position);
            if (mSelectedPos != position) {
                mAdapter.getData().get(mSelectedPos).setSelected(false);
                mAdapter.notifyItemChanged(mSelectedPos);
                mSelectedPos = position;
                mAdapter.getData().get(mSelectedPos).setSelected(true);
                mAdapter.notifyItemChanged(mSelectedPos);

            }
            mNavigationCidAdapter.setNewData(navigationBean.getArticles());

            View childAt = mRecyclerView_Title.getChildAt(position - mLinearLayoutManager.findFirstVisibleItemPosition());
            if (childAt != null) {
                int y = childAt.getTop() - mRecyclerView_Title.getHeight() / 2;
                mRecyclerView_Title.smoothScrollBy(0, y);
            }

        });
        mRecyclerView_Title.setAdapter(mAdapter);
    }

    private void initRecyleCid() {
        FlexboxLayoutManager layoutManager = new FlexboxLayoutManager(getActivity());
        layoutManager.setFlexWrap(FlexWrap.WRAP);
        layoutManager.setFlexDirection(FlexDirection.ROW);
        layoutManager.setAlignItems(AlignItems.STRETCH);
        layoutManager.setJustifyContent(JustifyContent.SPACE_BETWEEN);
        mRecyclerView_Context.scrollToPosition(0);
        mRecyclerView_Context.setLayoutManager(layoutManager);
        mNavigationCidAdapter = new NavigationCidAdapter(R.layout.item_navigation_cid, new ArrayList<>());
        mNavigationCidAdapter.setOnItemClickListener((adapter, view, position) -> {
            NavigationBean.ArticlesBean navigationBean = mNavigationCidAdapter.getData().get(position);
            Bundle bundle = new Bundle();
            bundle.putString("UrlBean", navigationBean.getLink());
            JumpUtils.jump(mContext, AgentWebActivity.class, bundle);// 详情

        });
        mRecyclerView_Context.setAdapter(mNavigationCidAdapter);
    }
}
