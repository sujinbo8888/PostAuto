package com.codeboy.qianghongbao.wy;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import com.codeboy.qianghongbao.job.State;
import com.codeboy.qianghongbao.util.AccessibilityHelper;

/**
 * Created by sjb on 2017/7/26.
 */

public class LoginState extends State {

    private final String TAG=LoginState.class.getName();


    private static final Class[] NEXT_STATES= {
            CommentState.class,LoginState.class
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

        targetNode = AccessibilityHelper.findNodeInfosById(nodeInfo, "com.netease.newsreader.activity:id/login_username");
        if(targetNode != null) {
            Log.w(TAG, "is login page");
            return true;

        }
        return false;
    }



    @Override
    public boolean doJob(AccessibilityEvent event) {
        Log.d(TAG,"do job");
        return postLogin();
    }

    public boolean postLogin() {
        AccessibilityNodeInfo nodeInfo = getStateContext().getService().getRootInActiveWindow();
        if(nodeInfo == null) {
            Log.w(TAG, "rootWindow为空");
            return false;
        }

        AccessibilityNodeInfo idNode = AccessibilityHelper.findNodeInfosById(nodeInfo, "com.netease.newsreader.activity:id/login_username");

        if(idNode != null) {

            Log.w(TAG, "set login info");

            //nodeInfo.performAction(AccessibilityNodeInfo.ACTION_FOCUS);
            idNode.performAction(AccessibilityNodeInfo.ACTION_CLICK);
            idNode.performAction(AccessibilityNodeInfo.ACTION_SET_SELECTION);
            try {
                Thread.sleep(5000);
            }catch (Exception e){}
            AccessibilityNodeInfo clearUid = AccessibilityHelper.findNodeInfosById(nodeInfo, "com.netease.newsreader.activity:id/clear_username");


            AccessibilityNodeInfo pswNode = AccessibilityHelper.findNodeInfosById(nodeInfo, "com.netease.newsreader.activity:id/login_password");
            AccessibilityNodeInfo loginButtonNode = AccessibilityHelper.findNodeInfosById(nodeInfo, "com.netease.newsreader.activity:id/do_login_button");





            AccessibilityHelper.performClick(clearUid);
            try {
                Thread.sleep(5000);
            }catch (Exception e){}
            AccessibilityHelper.performSetTextPast(this.getStateContext().getService(),idNode,"sujinbo8888@163.com");
            AccessibilityHelper.performSetTextPast(this.getStateContext().getService(),pswNode,"sjbeffort8888");
            //AccessibilityHelper.performSetText(idNode,"sujinbo8888@163.com");
            //AccessibilityHelper.performSetText(pswNode,"sjbeffort8888");

            AccessibilityHelper.performClick(loginButtonNode);
            return true;

        }
        return false;
    }

    @Override
    public boolean checkStatus(AccessibilityEvent event)
    {
        Log.d(TAG, "checkStatus");
        if(AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED==event.getEventType() /*&& event.getText().contains("登录失败")*/) {
            Log.e(TAG, "登陆失败"+event.getText());
            return false;
        }

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
        if (index>=NEXT_STATES.length-1)
        {
            return false;
        }

        Log.e(TAG, "准备切换到:"+index);

        return true;
    }

}
