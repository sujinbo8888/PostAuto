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

    private boolean isIntialized=false;

    public NewsMainState(IStateContext ctx)
    {
        super(ctx);
    }

    public boolean isCurrentState(AccessibilityEvent event) {
        Log.w("NewsMainState", "check is current state");

        AccessibilityNodeInfo nodeInfo = getStateContext().getService().getRootInActiveWindow();
        if(nodeInfo == null) {
            Log.w("NewsMainState", "rootWindow为空");
            return false;
        }

        AccessibilityNodeInfo targetNode = null;

        targetNode = AccessibilityHelper.findNodeInfosByText(nodeInfo, "发送");
        if(targetNode != null) {
            Log.w("NewsMainState", "is current state");
            return true;

        }
        return false;
    }

    @Override
    public void handleEvent(AccessibilityEvent event) {
        Log.d("NewsMainState","handle evlent");

        if("com.netease.nr.biz.news.detailpage.NewsPageActivity".equals(event.getClassName()) && event.getText().contains("网易新闻")) {
            Log.w("NewsMainState", "网易新闻");
        }

        if (isCurrentState(event))
        {
            isIntialized=true;
            doJob();
        }


/*
        if("android.widget.TextView".equals(event.getClassName())) {
            AccessibilityNodeInfo nodeInfo = getStateContext().getService().getRootInActiveWindow();
            if(nodeInfo == null) {
                Log.w("NewsMainState", "rootWindow为空");
                return;
            }

            AccessibilityNodeInfo targetNode = null;

            targetNode = AccessibilityHelper.findNodeInfosByText(nodeInfo, "热门跟帖");
            if(targetNode != null) {

                this.getStateContext().SetState(null);

            }

        }
        */
    }

    @Override
    public void doJob() {
        Log.d("NewsMainState","do job");

        if (!isIntialized) return ;

        AccessibilityNodeInfo nodeInfo = getStateContext().getService().getRootInActiveWindow();
        if(nodeInfo == null) {
            Log.w("NewsMainState", "rootWindow为空");
            return;
        }

        AccessibilityNodeInfo targetNode = null;

        targetNode = AccessibilityHelper.findNodeInfosById(nodeInfo, "com.netease.newsreader.activity:id/text");
        if(targetNode != null) {
            final AccessibilityNodeInfo n = targetNode;
            Log.w("NewsMainState", "click more comment"+n.getText());
            AccessibilityHelper.performClick(n);
            this.getStateContext().SetState(new CommentState(this.getStateContext()));

        }
    }
}
