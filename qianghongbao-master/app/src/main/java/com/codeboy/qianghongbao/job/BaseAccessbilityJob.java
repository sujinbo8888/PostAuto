package com.codeboy.qianghongbao.job;

import android.accessibilityservice.AccessibilityService;
import android.content.Context;
import android.view.accessibility.AccessibilityEvent;

import com.codeboy.qianghongbao.Config;
import com.codeboy.qianghongbao.QiangHongBaoService;

/**
 * <p>Created 16/1/16 上午12:38.</p>
 * <p><a href="mailto:codeboy2013@gmail.com">Email:codeboy2013@gmail.com</a></p>
 * <p><a href="http://www.happycodeboy.com">LeonLee Blog</a></p>
 *
 * @author LeonLee
 */
public abstract class BaseAccessbilityJob implements AccessbilityJob,IStateContext {

    private QiangHongBaoService service;

    @Override
    public void onCreateJob(QiangHongBaoService service) {
        this.service = service;
    }

    public Context getContext() {
        return service.getApplicationContext();
    }

    public Config getConfig() {
        return service.getConfig();
    }

    @Override
    public AccessibilityService getService() {
        return service;
    }


    //下方是用来做状态转换的

    protected State state=null;

    public void SetState(State state)
    {
        this.state=state;
        //this.state.doJob();
    }

    @Override
    public void onReceiveJob(AccessibilityEvent event){
        state.handleEvent(event);
    };
}
