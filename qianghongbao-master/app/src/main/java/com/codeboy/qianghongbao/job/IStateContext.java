package com.codeboy.qianghongbao.job;

import android.accessibilityservice.AccessibilityService;
import android.view.accessibility.AccessibilityEvent;

/**
 * Created by sjb on 2017/7/26.
 */

public interface IStateContext {
    public void SetState(State state);
    public AccessibilityService getService();

}
