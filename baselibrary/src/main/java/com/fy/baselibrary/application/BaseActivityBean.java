package com.fy.baselibrary.application;

import java.io.Serializable;

import butterknife.Unbinder;

/**
 * 使用 ActivityLifecycleCallbacks 实现给所有 Activity 执行 ButterKnife.bind
 * Created by fangs on 2017/5/18.
 */
public class BaseActivityBean implements Serializable {

    private Unbinder unbinder;

    public Unbinder getUnbinder() {
        return unbinder;
    }

    public void setUnbinder(Unbinder unbinder) {
        this.unbinder = unbinder;
    }



    private BaseOrientoinListener orientoinListener;

    public BaseOrientoinListener getOrientoinListener() {
        return orientoinListener;
    }

    public void setOrientoinListener(BaseOrientoinListener orientoinListener) {
        this.orientoinListener = orientoinListener;
    }
}