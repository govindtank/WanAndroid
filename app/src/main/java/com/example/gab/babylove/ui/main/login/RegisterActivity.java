package com.example.gab.babylove.ui.main.login;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.AccelerateInterpolator;
import android.widget.Button;

import com.example.gab.babylove.MainActivity;
import com.example.gab.babylove.R;
import com.example.gab.babylove.api.ApiService;
import com.example.gab.babylove.entity.LoginBean;
import com.fy.baselibrary.application.BaseApp;
import com.fy.baselibrary.application.IBaseActivity;
import com.fy.baselibrary.retrofit.NetCallBack;
import com.fy.baselibrary.retrofit.RequestUtils;
import com.fy.baselibrary.retrofit.RxHelper;
import com.fy.baselibrary.retrofit.dialog.IProgressDialog;
import com.fy.baselibrary.statusbar.MdStatusBar;
import com.fy.baselibrary.utils.ConstantUtils;
import com.fy.baselibrary.utils.JumpUtils;
import com.fy.baselibrary.utils.LogUtils;
import com.fy.baselibrary.utils.SpfUtils;
import com.fy.baselibrary.utils.cache.ACache;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by 初夏小溪 on 2018/4/18 0018.
 * 注册界面
 */

public class RegisterActivity extends AppCompatActivity implements IBaseActivity {

    @BindView(R.id.et_username)
    TextInputEditText editRegisterName;

    @BindView(R.id.tilRegisterPass)
    TextInputLayout tilRegisterPass;
    @BindView(R.id.editRegisterPass)
    TextInputEditText editRegisterPass;

    @BindView(R.id.tilRegisterPass2)
    TextInputLayout tilRegisterPass2;
    @BindView(R.id.editRegisterPass2)
    TextInputEditText editRegisterPass2;

    @BindView(R.id.btn_Register)
    Button btn_Register;
    @BindView(R.id.fab)
    FloatingActionButton mFloatingActionButton;
    @BindView(R.id.cv_add)
    CardView mCardView;

    @Override
    public boolean isShowHeadView() {
        return true;
    }

    @Override
    public int setView() {
        return R.layout.activity_register_material;
    }

    @Override
    public void setStatusBar(Activity activity) {
        MdStatusBar.setColorBar(activity, R.color.statusBar, R.color.statusBar);
    }

    @Override
    public void initData(Activity activity, Bundle savedInstanceState) {
        ShowEnterAnimation();
    }

    @OnClick({R.id.btn_Register,R.id.fab})
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_Register:
                register();
                break;
            case R.id.fab:
                animateRevealClose();
                break;
        }
    }

    @Override
    public void reTry() {

    }

    private void ShowEnterAnimation() {
        Transition transition = TransitionInflater.from(this).inflateTransition(R.transition.fabtransition);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setSharedElementEnterTransition(transition);
            transition.addListener(new Transition.TransitionListener() {
                @Override
                public void onTransitionStart(Transition transition) {
                    mCardView.setVisibility(View.GONE);
                }

                @Override
                public void onTransitionEnd(Transition transition) {
                    transition.removeListener(this);
                    animateRevealShow();
                }

                @Override
                public void onTransitionCancel(Transition transition) {

                }

                @Override
                public void onTransitionPause(Transition transition) {

                }

                @Override
                public void onTransitionResume(Transition transition) {

                }
            });
        }
    }

    public void animateRevealShow() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            Animator mAnimator = ViewAnimationUtils.createCircularReveal(mCardView, mCardView.getWidth()/2,0, mFloatingActionButton.getWidth() / 2, mCardView.getHeight());
            mAnimator.setDuration(500);
            mAnimator.setInterpolator(new AccelerateInterpolator());
            mAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                }

                @Override
                public void onAnimationStart(Animator animation) {
                    mCardView.setVisibility(View.VISIBLE);
                    super.onAnimationStart(animation);
                }
            });
            mAnimator.start();
        }
    }

    public void animateRevealClose() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            Animator mAnimator = ViewAnimationUtils.createCircularReveal(mCardView,mCardView.getWidth()/2,0, mCardView.getHeight(), mFloatingActionButton.getWidth() / 2);
            mAnimator.setDuration(500);
            mAnimator.setInterpolator(new AccelerateInterpolator());
            mAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mCardView.setVisibility(View.INVISIBLE);
                    super.onAnimationEnd(animation);
                    mFloatingActionButton.setImageResource(R.mipmap.plus);
                    RegisterActivity.super.onBackPressed();
                }

                @Override
                public void onAnimationStart(Animator animation) {
                    super.onAnimationStart(animation);
                }
            });
            mAnimator.start();
        }

    }
    private void register() {
        IProgressDialog progressDialog = new IProgressDialog().init(this)
                .setDialogMsg(R.string.register_loading);

        String mUserName = editRegisterName.getText().toString().trim();
        String mPassWord = editRegisterPass.getText().toString().trim();

        Map<String, Object> param = new HashMap<>();
        param.put("username", mUserName);
        param.put("password", mPassWord);
        param.put("repassword", mPassWord);
        RequestUtils.create(ApiService.class)
        .getRegister(param)
                .compose(RxHelper.handleResult())
//                .doOnSubscribe(RequestUtils::addDispos)
                .subscribe(new NetCallBack<LoginBean>(progressDialog) {
                    @Override
                    protected void onSuccess(LoginBean login) {
                        ACache mCache = ACache.get(BaseApp.getAppCtx());
                        mCache.put(ConstantUtils.userName, login);

                        SpfUtils.saveBooleanToSpf(ConstantUtils.isLogin, true);
                        SpfUtils.saveStrToSpf(ConstantUtils.userName, login.getUsername());

                        Bundle bundle = new Bundle();
                        bundle.putString("LoginBean", mCache.getAsString("User_Name"));
                        JumpUtils.jump(RegisterActivity.this, MainActivity.class, bundle);
                    }

                    @Override
                    protected void updataLayout(int flag) {
                        LogUtils.e("net updataLayout", flag + "-----");
                    }
                });
    }
}
