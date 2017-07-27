package com.codeboy.qianghongbao;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.util.Log;

import java.io.*;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class SocketService extends IntentService {

    public static final String BROADCAST_RECEIVE_MSG="SOCKET_REC_ACTION";
    public static final String RECEIVE_MSG_TAG="com.codeboy.auto.extra.RECEIVE";

    // TODO: Rename parameters
    private static final String EXTRA_HOST = "com.codeboy.auto.extra.REMOTEHOST";
    private static final String EXTRA_PORT = "com.codeboy.auto.extra.PORT";
    private static final String EXTRA_MSG = "com.codeboy.auto.extra.Msg";

    private static boolean isgoing=true;
    private Socket client=null;

    public SocketService() {
        super("SocketService");
    }

    public static void startActionSocketClient(Context context, String host, int port,String msg) {
        Intent intent = new Intent(context, SocketService.class);
        intent.putExtra(EXTRA_HOST, host);
        intent.putExtra(EXTRA_PORT, port);
        intent.putExtra(EXTRA_MSG, msg);
        context.startService(intent);
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String host = intent.getStringExtra(EXTRA_HOST);
            final int port = intent.getIntExtra(EXTRA_PORT,8000);
            final String msg = intent.getStringExtra(EXTRA_MSG);
            handleActionSocketClient(host, port,msg);
        }

    }
/*
    private void handleActionSocketClient(String host, int port) {
        try
        {
            client = new Socket(host,port);
            String ret="";
            while(isgoing){
                ret="";
                try {
                    BufferedInputStream bis = new BufferedInputStream(client.getInputStream());
                    byte[] b = new byte[1024];
                    int len = 0;

                    while((len=bis.read(b))!=-1){
                        ret+=new String(b,0,len).trim();
                    }

                    Intent broadcastIntent = new Intent();
                    broadcastIntent.setAction("SOCKET_REC_ACTION");
                    broadcastIntent.putExtra("receive",ret);
                    getBaseContext().sendBroadcast(broadcastIntent);


                } catch (IOException e) {
                    Log.e("SocketService"," 连接服务器失败");
                }
            }
        }catch (Exception e)
        {

        }
        finally {
            try {
                if (client!=null)
                    client.close();
            } catch (IOException e) {
                Log.e("SocketService",e.toString());
            }
        }
    }

*/

    private void handleActionSocketClient(String host, int port,String msg) {
        try
        {
            Log.e("SocketService","create socket");
            final Socket client = new Socket(host,port);
            PrintWriter out = new PrintWriter(new OutputStreamWriter(client.getOutputStream(),"utf-8"),true);
            Log.e("SocketService","ready to send:"+msg);
            out.write(msg);
            out.flush();

           // BufferedInputStream bis = new BufferedInputStream(client.getInputStream());
            InputStreamReader bis = new InputStreamReader(client.getInputStream(), "UTF-8");

            char[] b = new char[1024];
            int len = 0;
            String ret="";
            Log.d("SocketService","ready to receive:");
            while((len=bis.read(b))!=-1){
                ret+=new String(b,0,len).trim();
            }
            Log.d("SocketService","received:"+ret);
            sendRecMsgBrocad(ret);
            client.close();
/*

            new Thread(new Runnable(){
                    @SuppressWarnings("static-access")
                    public void run(){
                        try {
                            BufferedInputStream bis = new BufferedInputStream(client.getInputStream());
                            byte[] b = new byte[1024];
                            int len = 0;
                            String ret="";
                            Log.d("SocketService","ready to receive:");
                            while((len=bis.read(b))!=-1){
                                ret+=new String(b,0,len).trim();
                            }
                            sendRecMsgBrocad(ret);
                            client.close();

                        } catch (IOException e) {
                            System.err.println("连接服务器失败!");
                        }
                    }
            }).start();
*/

        }catch (Exception e){
            Log.e("SocketService",e.toString());
        };
    }

    private void sendRecMsgBrocad(String msg)
    {
        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction(BROADCAST_RECEIVE_MSG);
        broadcastIntent.putExtra(RECEIVE_MSG_TAG,msg);
        getBaseContext().sendBroadcast(broadcastIntent);
    }

}
