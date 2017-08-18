package com.codeboy.qianghongbao.job;

import android.view.accessibility.AccessibilityEvent;
import android.util.Log;

import com.codeboy.qianghongbao.job.IStateContext;
import com.codeboy.qianghongbao.wy.CommentState;

/**
 * Created by sjb on 2017/7/26.
 */

public abstract class State {

    private static final int STATUS_DOJOB=0;
    private static final int STATUS_CHECKSTATUS=1;
    private static final int STATUS_CHANGESTATE=2;
    private static final int STATUS_NONE=3;

    private IStateContext stateContext;

    private int currentStatus=STATUS_DOJOB;

    public IStateContext getStateContext(){return stateContext;}

    /*
    public State(IStateContext ctx)
    {
        this.stateContext=ctx;
    }
    public State(){}
   */
    public void setStateContext(IStateContext ctx)
    {
        this.stateContext=ctx;
    }

    public void changeToNextState()
    {
        Log.d(getClassName(),"准备切换下一个状态 ");
        boolean success=false;
        Class[] nexts=getNextStates();
        for(Class clazz : nexts) {
            try {
                Object object = clazz.newInstance();
                if(object instanceof State) {
                    State ns = (State) object;
                    ns.setStateContext(this.getStateContext());
                    if (ns.inCurrentState())
                    {
                        this.getStateContext().SetState(ns);
                        success=true;
                        break;
                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
            }


        }
        if (!success)
        {
            Log.w(getClassName(),"找不到下一个状态 ");
        }


    }

    public void handleEvent(AccessibilityEvent event)
    {
        if (currentStatus==STATUS_DOJOB)
        {
            if (doJob(event)) {
                currentStatus = STATUS_CHECKSTATUS;
            }
        }
        else if (currentStatus==STATUS_CHECKSTATUS)
        {
            if (checkStatus(event)) {
                changeToNextState();
                currentStatus = STATUS_NONE;
            }
        }
        else if(currentStatus==STATUS_CHANGESTATE)
        {
            changeToNextState();
        }

    }

    protected String getClassName(){
        return getClass().getSimpleName();
    }

    public abstract boolean doJob(AccessibilityEvent event);

    public abstract boolean checkStatus(AccessibilityEvent event);

    public abstract  boolean inCurrentState();

    public abstract Class[] getNextStates();


}
