package com.codeboy.qianghongbao.wy;

import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import com.codeboy.qianghongbao.job.IStateContext;
import com.codeboy.qianghongbao.job.State;
import com.codeboy.qianghongbao.util.AccessibilityHelper;

/**
 * Created by sjb on 2017/7/26.
 */

public class NewsMainState extends State {

    private final String TAG=NewsMainState.class.getName();


    private static final Class[] NEXT_STATES= {
            CommentState.class,
    };


    @Override
    public Class[] getNextStates()
    {
        return NEXT_STATES;
    }


    @Override
    public  boolean inCurrentState() {
        Log.w(TAG, "check is current state");

        AccessibilityNodeInfo nodeInfo = getStateContext().getService().getRootInActiveWindow();
        if(nodeInfo == null) {
            Log.w(TAG, "rootWindow为空");
            return false;
        }

        AccessibilityNodeInfo targetNode = null;

        targetNode = AccessibilityHelper.findNodeInfosByText(nodeInfo, "发送");
        if(targetNode != null) {
            Log.w(TAG, "is current state");
            return true;

        }
        return false;
    }
/*
    @Override
    public void handleEvent(AccessibilityEvent event) {
        Log.d(TAG,"handle evlent");

        if (isIntialized)return;

        if("com.netease.nr.biz.news.detailpage.NewsPageActivity".equals(event.getClassName()) && event.getText().contains("网易新闻")) {
            Log.w("NewsMainState", "网易新闻");
        }

        if (inCurrentState())
        {
            isIntialized=true;
            doJob();
        }

    }
*/
    @Override
    public boolean doJob(AccessibilityEvent event) {
        Log.d(TAG,"do job");

        AccessibilityNodeInfo nodeInfo = getStateContext().getService().getRootInActiveWindow();
        if(nodeInfo == null) {
            Log.w(TAG, "rootWindow为空");
            return false;
        }

        AccessibilityNodeInfo targetNode = null;

        targetNode = AccessibilityHelper.findNodeInfosById(nodeInfo, "com.netease.newsreader.activity:id/text");
        if(targetNode != null) {
            final AccessibilityNodeInfo n = targetNode;
            Log.w(TAG, "click more comment"+n.getText());
            AccessibilityHelper.performClick(n);
            return true;
        }

        return false;
    }

    @Override
    public boolean checkStatus(AccessibilityEvent event)
    {
        return isSwitchToOtherState();
    }

    public boolean isSwitchToOtherState()
    {
        int index=0;
        for(Class clazz : NEXT_STATES) {
            try {
                Object object = clazz.newInstance();
                if(object instanceof State) {
                    State ns = (State) object;
                    ns.setStateContext(this.getStateContext());
                    if (ns.inCurrentState())
                    {
                        break;
                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            index++;
        }
        if (index>=NEXT_STATES.length)
        {
            return false;
        }

        Log.e(TAG, "准备切换到:"+index);

        return true;
    }
}
