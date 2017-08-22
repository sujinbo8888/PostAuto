package com.codeboy.qianghongbao.wy;

import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.content.*;

import com.codeboy.qianghongbao.job.IStateContext;
import com.codeboy.qianghongbao.job.State;
import com.codeboy.qianghongbao.util.AccessibilityHelper;

/**
 * Created by sjb on 2017/7/26.
 *
 */

public class CommentState extends State {

    private final String TAG=CommentState.class.getName();


    private static final Class[] NEXT_STATES= {
            LoginState.class,
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

        targetNode = AccessibilityHelper.findNodeInfosById(nodeInfo, "com.netease.newsreader.activity:id/refresh_time");
        if(targetNode != null) {
            Log.w(TAG, "is current state");
            return true;

        }
        return false;
    }



    public boolean isLoginPage(AccessibilityEvent event) {
        Log.w(TAG, "check is  state");

        AccessibilityNodeInfo nodeInfo = getStateContext().getService().getRootInActiveWindow();
        if(nodeInfo == null) {
            Log.w(TAG, "rootWindow为空");
            return false;
        }

        AccessibilityNodeInfo targetNode = null;

        targetNode = AccessibilityHelper.findNodeInfosById(nodeInfo, "com.netease.newsreader.activity:id/login_username");
        if(targetNode != null) {
            Log.w(TAG, "is login page");
            return true;

        }
        return false;
    }
    /*

    @Override
    public void handleEvent(AccessibilityEvent event) {
        Log.d(TAG,"handle evlent");

        if (!isIntialized)
        {
            if (isCurrentState(event))
            {
                isIntialized=true;
                doJob();
            }
            else
            {
                return;
            }
        }


        if("TYPE_NOTIFICATION_STATE_CHANGED".equals(event.getAction()) &&
                "android.widget.Toast$TN".equals(event.getClassName()) &&
                event.getText().contains("发表成功")) {
            Log.w(TAG, "发表成功");

        }

        if (!logined && isLoginPage(event))
        {
            postLogin();
            logined=true;
        }


    }
    */

    @Override
    public boolean doJob(AccessibilityEvent event) {
        Log.d(TAG,"do job");
        return postComment();
    }

    public void postLogin() {
        Log.d(TAG,"postlogin");

       // if (!isIntialized) return ;


        AccessibilityNodeInfo nodeInfo = getStateContext().getService().getRootInActiveWindow();
        if(nodeInfo == null) {
            Log.w(TAG, "rootWindow为空");
            return;
        }

        AccessibilityNodeInfo idNode = AccessibilityHelper.findNodeInfosById(nodeInfo, "com.netease.newsreader.activity:id/login_username");

        if(idNode != null) {

            Log.w(TAG, "set login info");

            AccessibilityNodeInfo pswNode = AccessibilityHelper.findNodeInfosById(nodeInfo, "com.netease.newsreader.activity:id/login_password");
            AccessibilityNodeInfo loginButtonNode = AccessibilityHelper.findNodeInfosById(nodeInfo, "com.netease.newsreader.activity:id/do_login_button");

            //AccessibilityHelper.performSetTextPast(this.getStateContext().getService(),idNode,"sujinbo8888@163.com");
            //AccessibilityHelper.performSetTextPast(this.getStateContext().getService(),pswNode,"sjbeffort8888");
            AccessibilityHelper.performSetText(idNode,"sujinbo8888@163.com");
            AccessibilityHelper.performSetText(pswNode,"sjbeffort8888");

            AccessibilityHelper.performClick(loginButtonNode);

        }
    }

    public boolean postComment() {
        Log.d(TAG,"postComment");


        AccessibilityNodeInfo nodeInfo = getStateContext().getService().getRootInActiveWindow();
        if(nodeInfo == null) {
            Log.w(TAG, "rootWindow为空");
            return false;
        }

        AccessibilityNodeInfo targetNode = null;

        targetNode = AccessibilityHelper.findNodeInfosById(nodeInfo, "com.netease.newsreader.activity:id/mock_reply_edit");
        //targetNode = AccessibilityHelper.findNodeInfosById(nodeInfo, "com.netease.newsreader.activity:id/reply_edit");

        //targetNode = AccessibilityHelper.findNodeInfosByText(nodeInfo, "写跟帖");
        if(targetNode != null) {
            final AccessibilityNodeInfo n = targetNode;

            AccessibilityHelper.performClick(n);

            targetNode = AccessibilityHelper.findNodeInfosById(nodeInfo, "com.netease.newsreader.activity:id/reply_edit");
            if(targetNode != null) {
                // AccessibilityHelper.performSetText(targetNode, "我是来看帖的");
                ClipboardManager clipboard = (ClipboardManager)this.getStateContext().getService().getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("text", "我是来看帖的");
                clipboard.setPrimaryClip(clip);

                targetNode.performAction(AccessibilityNodeInfo.ACTION_FOCUS);

                targetNode.performAction(AccessibilityNodeInfo.ACTION_PASTE);


                targetNode = AccessibilityHelper.findNodeInfosById(nodeInfo, "com.netease.newsreader.activity:id/reply");
                if(targetNode != null) {
                    AccessibilityHelper.performClick(targetNode);
                    return true;
                }
                else
                {
                    Log.w(TAG, "发送按钮没找到");

                }
            }

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
