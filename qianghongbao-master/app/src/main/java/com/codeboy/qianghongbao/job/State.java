package com.codeboy.qianghongbao.job;

import android.view.accessibility.AccessibilityEvent;

import com.codeboy.qianghongbao.job.IStateContext;

/**
 * Created by sjb on 2017/7/26.
 */

public abstract class State {

    private IStateContext stateContext;

    public IStateContext getStateContext(){return stateContext;}

    public State(IStateContext ctx)
    {
        this.stateContext=ctx;
    }

    public abstract  void handleEvent(AccessibilityEvent event);

    public abstract void doJob();
}
