package com.codeboy.qianghongbao.state;

import android.view.accessibility.AccessibilityEvent;

/**
 * Created by sjb on 2017/7/26.
 */

public abstract class State {

    private Context context;

    public State(Context ctx)
    {
        this.context=ctx;
    }

    public abstract  void handleEvent(AccessibilityEvent event);

    public abstract void doJob();
}
