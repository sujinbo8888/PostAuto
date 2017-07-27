package com.codeboy.qianghongbao.wy;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Parcelable;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import com.codeboy.qianghongbao.BuildConfig;
import com.codeboy.qianghongbao.Config;
import com.codeboy.qianghongbao.IStatusBarNotification;
import com.codeboy.qianghongbao.QiangHongBaoService;
import com.codeboy.qianghongbao.job.BaseAccessbilityJob;
import com.codeboy.qianghongbao.util.AccessibilityHelper;
import com.codeboy.qianghongbao.util.NotifyHelper;

import java.util.List;

/**
 * <p>Created 17/7/16 上午12:40.</p>
 * <p><a href="mailto:codeboy2013@gmail.com">Email:codeboy2013@gmail.com</a></p>
 * <p><a href="http://www.happycodeboy.com">LeonLee Blog</a></p>
 *
 * @author sujinbo
 */
public class WYAccessbilityJob extends BaseAccessbilityJob {

    private static final String TAG = "WYAccessbilityJob";

    /** app的包名*/
    public static final String PACKAGENAME = "com.netease.newsreader.activity";


    private static final String BUTTON_CLASS_NAME = "android.widget.Button";


    /** 不能再使用文字匹配的最小版本号 */
    private static final int USE_ID_MIN_VERSION = 700;// 6.3.8 对应code为680,6.3.9对应code为700



    private boolean isReceivingHongbao;
    private PackageInfo mAppPackageInfo = null;
    private Handler mHandler = null;

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //更新安装包信息
            updatePackageInfo();
        }
    };

    @Override
    public void onCreateJob(QiangHongBaoService service) {
        super.onCreateJob(service);

        updatePackageInfo();

        IntentFilter filter = new IntentFilter();
        filter.addDataScheme("package");
        filter.addAction("android.intent.action.PACKAGE_ADDED");
        filter.addAction("android.intent.action.PACKAGE_REPLACED");
        filter.addAction("android.intent.action.PACKAGE_REMOVED");

        getContext().registerReceiver(broadcastReceiver, filter);
    }

    @Override
    public void onStopJob() {
        try {
            getContext().unregisterReceiver(broadcastReceiver);
        } catch (Exception e) {}
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    public void onNotificationPosted(IStatusBarNotification sbn) {
        Notification nf = sbn.getNotification();
        String text = String.valueOf(sbn.getNotification().tickerText);
        notificationEvent(text, nf);
    }

    @Override
    public boolean isEnable() {
        return getConfig().isEnableWechat();
    }

    @Override
    public String getTargetPackageName() {
        return PACKAGENAME;
    }

    @Override
    public void onReceiveJob(AccessibilityEvent event) {
        final int eventType = event.getEventType();
        //通知栏事件
        if(eventType == AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED) {
            Parcelable data = event.getParcelableData();
            if(data == null || !(data instanceof Notification)) {
                return;
            }
            if(QiangHongBaoService.isNotificationServiceRunning() && getConfig().isEnableNotificationService()) { //开启快速模式，不处理
                return;
            }
            List<CharSequence> texts = event.getText();
            if(!texts.isEmpty()) {
                String text = String.valueOf(texts.get(0));
                notificationEvent(text, (Notification) data);
            }
        } else if(eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            handleNav(event);
        } else if(eventType == AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED) {
            if(mCurrentWindow != WINDOW_LAUNCHER) { //不在聊天界面或聊天列表，不处理
                return;
            }
            if(isReceivingHongbao) {
                handleChatListHongBao();
            }
        }
    }

    /** 是否为群聊天*/
    private boolean isMemberChatUi(AccessibilityNodeInfo nodeInfo) {
        if(nodeInfo == null) {
            return false;
        }
        String id = "com.tencent.mm:id/ces";
        int wv = getAppVersion();
        if(wv <= 680) {
            id = "com.tencent.mm:id/ew";
        } else if(wv <= 700) {
            id = "com.tencent.mm:id/cbo";
        }
        String title = null;
        AccessibilityNodeInfo target = AccessibilityHelper.findNodeInfosById(nodeInfo, id);
        if(target != null) {
            title = String.valueOf(target.getText());
        }
        List<AccessibilityNodeInfo> list = nodeInfo.findAccessibilityNodeInfosByText("返回");

        if(list != null && !list.isEmpty()) {
            AccessibilityNodeInfo parent = null;
            for(AccessibilityNodeInfo node : list) {
                if(!"android.widget.ImageView".equals(node.getClassName())) {
                    continue;
                }
                String desc = String.valueOf(node.getContentDescription());
                if(!"返回".equals(desc)) {
                    continue;
                }
                parent = node.getParent();
                break;
            }
            if(parent != null) {
                parent = parent.getParent();
            }
            if(parent != null) {
                if( parent.getChildCount() >= 2) {
                    AccessibilityNodeInfo node = parent.getChild(1);
                    if("android.widget.TextView".equals(node.getClassName())) {
                        title = String.valueOf(node.getText());
                    }
                }
            }
        }


        if(title != null && title.endsWith(")")) {
            return true;
        }
        return false;
    }

    /** 通知栏事件*/
    private void notificationEvent(String ticker, Notification nf) {
        String text = ticker;
        int index = text.indexOf(":");
        if(index != -1) {
            text = text.substring(index + 1);
        }
        text = text.trim();
        if(text.contains(HONGBAO_TEXT_KEY)) { //红包消息
            newHongBaoNotification(nf);
        }
    }

    /** 打开通知栏消息*/
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void newHongBaoNotification(Notification notification) {
        isReceivingHongbao = true;
        //以下是精华，将微信的通知栏消息打开
        PendingIntent pendingIntent = notification.contentIntent;
        boolean lock = NotifyHelper.isLockScreen(getContext());

        if(!lock) {
            NotifyHelper.send(pendingIntent);
        } else {
            NotifyHelper.showNotify(getContext(), String.valueOf(notification.tickerText), pendingIntent);
        }

        if(lock || getConfig().getWechatMode() != Config.WX_MODE_0) {
            NotifyHelper.playEffect(getContext(), getConfig());
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void handleNav(AccessibilityEvent event) {
        if("com.netease.nr.phone.main.MainActivity".equals(event.getClassName())) {
            mCurrentWindow = WINDOW_LUCKYMONEY_RECEIVEUI;

            handleClickMe();
        } else if("com.tencent.mm.plugin.luckymoney.ui.LuckyMoneyDetailUI".equals(event.getClassName())) {
            mCurrentWindow = WINDOW_LUCKYMONEY_DETAIL;
            //拆完红包后看详细的纪录界面
            if(getConfig().getWechatAfterGetHongBaoEvent() == Config.WX_AFTER_GET_GOHOME) { //返回主界面，以便收到下一次的红包通知
                AccessibilityHelper.performHome(getService());
            }
        } else if("com.tencent.mm.ui.LauncherUI".equals(event.getClassName())) {
            mCurrentWindow = WINDOW_LAUNCHER;
            //在聊天界面,去点中红包
            handleChatListHongBao();
        } else {
            mCurrentWindow = WINDOW_OTHER;
        }
    }

    /**
     * 点击我去登陆
     * */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void handleClickMe() {
        AccessibilityNodeInfo nodeInfo = getService().getRootInActiveWindow();
        if(nodeInfo == null) {
            Log.w(TAG, "rootWindow为空");
            return;
        }

        AccessibilityNodeInfo targetNode = null;

        targetNode = AccessibilityHelper.findNodeInfosByText(nodeInfo, "我");
        if(targetNode != null) {
            final AccessibilityNodeInfo n = targetNode;

                AccessibilityHelper.performClick(n);

        }

        return;


    }

    /**
     * 收到聊天里的红包
     * */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    private void handleChatListHongBao() {
        int mode = getConfig().getWechatMode();
        if(mode == Config.WX_MODE_3) { //只通知模式
            return;
        }

        AccessibilityNodeInfo nodeInfo = getService().getRootInActiveWindow();
        if(nodeInfo == null) {
            Log.w(TAG, "rootWindow为空");
            return;
        }

        if(mode != Config.WX_MODE_0) {
            boolean isMember = isMemberChatUi(nodeInfo);
            if(mode == Config.WX_MODE_1 && isMember) {//过滤群聊
                return;
            } else if(mode == Config.WX_MODE_2 && !isMember) { //过滤单聊
                return;
            }
        }

        List<AccessibilityNodeInfo> list = nodeInfo.findAccessibilityNodeInfosByText("领取红包");

        if(list != null && list.isEmpty()) {
            // 从消息列表查找红包
            AccessibilityNodeInfo node = AccessibilityHelper.findNodeInfosByText(nodeInfo, "[微信红包]");
            if(node != null) {
                if(BuildConfig.DEBUG) {
                    Log.i(TAG, "-->微信红包:" + node);
                }
                isReceivingHongbao = true;
                AccessibilityHelper.performClick(nodeInfo);
            }
        } else if(list != null) {
            if (isReceivingHongbao){
                //最新的红包领起
                AccessibilityNodeInfo node = list.get(list.size() - 1);
                AccessibilityHelper.performClick(node);
                isReceivingHongbao = false;
            }
        }
    }

    private Handler getHandler() {
        if(mHandler == null) {
            mHandler = new Handler(Looper.getMainLooper());
        }
        return mHandler;
    }

    /** 获取的版本*/
    private int getAppVersion() {
        if(mAppPackageInfo == null) {
            return 0;
        }
        return mAppPackageInfo.versionCode;
    }

    /** 更新包信息*/
    private void updatePackageInfo() {
        try {
            mAppPackageInfo = getContext().getPackageManager().getPackageInfo(PACKAGENAME, 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }
}
