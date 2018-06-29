package com.example.meimeng.mywebsocket;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;

import com.example.meimeng.APP;
import com.example.meimeng.mywebsocket.mynotification.NotifyListenerManager;
import com.example.meimeng.mywebsocket.request.ChildResponse;
import com.example.meimeng.mywebsocket.request.Codec;
import com.example.meimeng.mywebsocket.request.Response;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;
import com.neovisionaries.ws.client.WebSocketException;
import com.neovisionaries.ws.client.WebSocketFactory;
import com.neovisionaries.ws.client.WebSocketFrame;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;


/**
 * 作者：凌涛 on 2018/6/29 09:42
 * 邮箱：771548229@qq..com
 */
public class WsManager {
    private static WsManager mInstance;
    private final String TAG = this.getClass().getSimpleName();

    /**
     * WebSocket config
     */
    private static final int FRAME_QUEUE_SIZE = 5;
    private static final int CONNECT_TIMEOUT = 5000;
    private static final String DEF_TEST_URL = "ws://47.106.164.34:80/memen/websocket";//测试服默认地址
    private static final String DEF_RELEASE_URL = "ws://47.106.164.34:80/memen/websocket";//正式服默认地址
    private static final String DEF_URL = BuildConfig.DEBUG ? DEF_TEST_URL : DEF_RELEASE_URL;
    private String url;

    private WsStatus mStatus;
    private WebSocket ws;
    private WsListener mListener;

    private String address = "ws://47.106.164.34:80/memen/websocket";
    private String PORT = "80";

    private WsManager() {
    }

    private static final int REQUEST_TIMEOUT = 10000;//请求超时时间
    private AtomicLong seqId = new AtomicLong(SystemClock.uptimeMillis());//每个请求的唯一标识

    public void sendReq(Action action, Object req, ICallback callback) {
        sendReq(action, req, callback, REQUEST_TIMEOUT);
    }


    public void sendReq(Action action, Object req, ICallback callback, long timeout) {
        sendReq(action, req, callback, timeout, 1);
    }


    private final int SUCCESS_HANDLE = 0x01;
    private final int ERROR_HANDLE = 0x02;

    //超时后的处理有了,接下来我们实现添加超时任务代码.
    private ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
    private Map<Long, CallbackWrapper> callbacks = new HashMap<>();

    /**
     * @param action   Action
     * @param req      请求参数
     * @param callback 回调
     * @param timeout  超时时间
     * @param reqCount 请求次数
     */
    @SuppressWarnings("unchecked")
    private <T> void sendReq(Action action, T req, final ICallback callback, final long timeout, int reqCount) {
        if (!isNetConnect()) {
            callback.onFail("网络不可用");
            return;
        }

        Request request = new Request.Builder<T>()
                .action(action.getAction())
                .reqEvent(action.getReqEvent())
                .seqId(seqId.getAndIncrement())
                .reqCount(reqCount)
                .req(req)
                .build();

        ScheduledFuture timeoutTask = enqueueTimeout(request.getSeqId(), timeout);//添加超时任务

        IWsCallback tempCallback = new IWsCallback() {

            @Override
            public void onSuccess(Object o) {
                mHandler.obtainMessage(SUCCESS_HANDLE, new CallbackDataWrapper(callback, o)).sendToTarget();
            }


            @Override
            public void onError(String msg, Request request, Action action) {
                mHandler.obtainMessage(ERROR_HANDLE, new CallbackDataWrapper(callback, msg)).sendToTarget();
            }


            @Override
            public void onTimeout(Request request, Action action) {
                timeoutHandle(request, action, callback, timeout);
            }
        };

        callbacks.put(request.getSeqId(), new CallbackWrapper(tempCallback, timeoutTask, action, request));


        Log.i(TAG, "send text : " + new Gson().toJson(request));
        ws.sendText(new Gson().toJson(request));
    }

    /**
     * 添加超时任务
     */
    private ScheduledFuture enqueueTimeout(final long seqId, long timeout) {
        return executor.schedule(new Runnable() {
            @Override
            public void run() {
                CallbackWrapper wrapper = callbacks.remove(seqId);
                if (wrapper != null) {
                    Log.i(TAG, "run: (action:" + wrapper.getAction().getAction() + ")第"
                            + wrapper.getRequest().getReqCount() + "次请求超时");
                    wrapper.getTempCallback().onTimeout(wrapper.getRequest(), wrapper.getAction());
                }
            }
        }, timeout, TimeUnit.MILLISECONDS);
    }

    /**
     * 超时处理
     */
    private void timeoutHandle(Request request, Action action, ICallback callback, long timeout) {
        if (request.getReqCount() > 3) {
            Log.i(TAG, "timeoutHandle: action:" + action.getAction() + "连续3次请求超时 执行http请求");
            //走http请求
        } else {
            sendReq(action, request.getReq(), callback, timeout, request.getReqCount() + 1);
            Log.i(TAG, "timeoutHandle: action:" + action.getAction() + "发起第" + request.getReqCount() + "次请求");
        }
    }


    public static WsManager getInstance() {
        if (mInstance == null) {
            synchronized (WsManager.class) {
                if (mInstance == null) {
                    mInstance = new WsManager();
                }
            }
        }
        return mInstance;
    }

    public void init() {
        try {
            /**
             * configUrl其实是缓存在本地的连接地址
             * 这个缓存本地连接地址是app启动的时候通过http请求去服务端获取的,
             * 每次app启动的时候会拿当前时间与缓存时间比较,超过6小时就再次去服务端获取新的连接地址更新本地缓存
             */
            String configUrl = "";
            url = TextUtils.isEmpty(configUrl) ? DEF_URL : configUrl;
            ws = new WebSocketFactory().createSocket(url, CONNECT_TIMEOUT)
                    .setFrameQueueSize(FRAME_QUEUE_SIZE)//设置帧队列最大值为5
                    .setMissingCloseFrameAllowed(false)//设置不允许服务端关闭连接却未发送关闭帧
                    .addListener(mListener = new WsListener())//添加回调监听
                    .connectAsynchronously();//异步连接
            setStatus(WsStatus.CONNECTING);
            Log.i(TAG, "init: 第一次连接");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * 继承默认的监听空实现WebSocketAdapter,重写我们需要的方法
     * onTextMessage 收到文字信息
     * onConnected 连接成功
     * onConnectError 连接失败
     * onDisconnected 连接关闭
     */
    class WsListener extends WebSocketAdapter {
        @Override
        public void onTextMessage(WebSocket websocket, String text) throws Exception {
            super.onTextMessage(websocket, text);
            Log.i(TAG, "onTextMessage: " + text);
            Response response = Codec.decoder(text);//解析出第一层bean
            if (response.getRespEvent() == 10) {//响应
                CallbackWrapper wrapper = callbacks.remove(Long.parseLong(response.getSeqId()));//找到对应callback
                if (wrapper == null) {
                    Log.i(TAG, "onTextMessage: (action:" + response.getAction() + ") not found callback");
                    return;
                }

                try {
                    wrapper.getTimeoutTask().cancel(true);//取消超时任务
                    ChildResponse childResponse = Codec.decoderChildResp(response.getResp());//解析第二层bean
                    if (childResponse.isOK()) {
                        Object o = new Gson().fromJson(childResponse.getData(),
                                wrapper.getAction().getRespClazz());
                        wrapper.getTempCallback().onSuccess(o);
                    } else {
                        wrapper.getTempCallback()
                                .onError(ErrorCode.BUSINESS_EXCEPTION.getMsg(), wrapper.getRequest(),
                                        wrapper.getAction());
                    }
                } catch (JsonSyntaxException e) {
                    e.printStackTrace();
                    wrapper.getTempCallback()
                            .onError(ErrorCode.PARSE_EXCEPTION.getMsg(), wrapper.getRequest(),
                                    wrapper.getAction());
                }

            } else if (response.getRespEvent() == 20) {//通知,服务器主动通知
                NotifyListenerManager.getInstance().fire(response);
            }

        }


        @Override
        public void onConnected(WebSocket websocket, Map<String, List<String>> headers)
                throws Exception {
            super.onConnected(websocket, headers);
            Log.i(TAG, "onConnected: 连接成功");
            setStatus(WsStatus.CONNECT_SUCCESS);
            cancelReconnect();//连接成功的时候取消重连,初始化连接次数
//            doAuth();//测试连接
        }


        @Override
        public void onConnectError(WebSocket websocket, WebSocketException exception)
                throws Exception {
            super.onConnectError(websocket, exception);
            Log.i(TAG, "onConnectError: 连接错误");
            setStatus(WsStatus.CONNECT_FAIL);
            reconnect();//连接错误的时候调用重连方法

        }

        @Override
        public void onDisconnected(WebSocket websocket, WebSocketFrame serverCloseFrame, WebSocketFrame clientCloseFrame, boolean closedByServer)
                throws Exception {
            super.onDisconnected(websocket, serverCloseFrame, clientCloseFrame, closedByServer);
            Log.i(TAG, "onDisconnected: 断开连接");
            setStatus(WsStatus.CONNECT_FAIL);
            reconnect();//连接断开的时候调用重连方法

        }
    }

    private void setStatus(WsStatus status) {
        this.mStatus = status;
    }

    private WsStatus getStatus() {
        return mStatus;
    }

    public void disconnect() {
        if (ws != null)
            ws.disconnect();
    }

    private static final long HEARTBEAT_INTERVAL = 30000;//心跳间隔

    private Handler mHandler = new Handler(Looper.myLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SUCCESS_HANDLE:
                    CallbackDataWrapper successWrapper = (CallbackDataWrapper) msg.obj;
                    successWrapper.getCallback().onSuccess(successWrapper.getData());
                    break;
                case ERROR_HANDLE:
                    CallbackDataWrapper errorWrapper = (CallbackDataWrapper) msg.obj;
                    errorWrapper.getCallback().onFail((String) errorWrapper.getData());
                    break;
            }
        }

    };

    private void doAuth() {
        sendReq(Action.LOGIN, null, new ICallback() {
            @Override
            public void onSuccess(Object o) {
                Log.i(TAG, "onSuccess: 授权成功");
                setStatus(WsStatus.AUTH_SUCCESS);
                startHeartbeat();
                delaySyncData();
            }


            @Override
            public void onFail(String msg) {

            }
        });
    }

    //同步数据
    private void delaySyncData() {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                sendReq(Action.SYNC, null, new ICallback() {
                    @Override
                    public void onSuccess(Object o) {

                    }


                    @Override
                    public void onFail(String msg) {

                    }
                });
            }
        }, 300);
    }


    private void startHeartbeat() {
        mHandler.postDelayed(heartbeatTask, HEARTBEAT_INTERVAL);
    }

    private void cancelHeartbeat() {
        heartbeatFailCount = 0;
        mHandler.removeCallbacks(heartbeatTask);
    }


    private int heartbeatFailCount = 0;
    private Runnable heartbeatTask = new Runnable() {
        @Override
        public void run() {
            sendReq(Action.HEARTBEAT, null, new ICallback() {
                @Override
                public void onSuccess(Object o) {
                    heartbeatFailCount = 0;
                }


                @Override
                public void onFail(String msg) {
                    heartbeatFailCount++;
                    if (heartbeatFailCount >= 3) {
                        reconnect();
                    }
                }
            });

            mHandler.postDelayed(this, HEARTBEAT_INTERVAL);
        }
    };



    private int reconnectCount = 0;//重连次数
    private long minInterval = 3000;//重连最小时间间隔
    private long maxInterval = 60000;//重连最大时间间隔


    public void reconnect() {
        if (!isNetConnect()) {
            reconnectCount = 0;
            Log.i(TAG, "reconnect: 重连失败网络不可用");
            return;
        }

        //这里其实应该还有个用户是否登录了的判断
        // 因为当连接成功后我们需要发送用户信息到服务端进行校验
        //由于我们这里是个demo所以省略了
        if (ws != null &&
                !ws.isOpen() &&//当前连接断开了
                getStatus() != WsStatus.CONNECTING) {//不是正在重连状态

            reconnectCount++;
            setStatus(WsStatus.CONNECTING);

            long reconnectTime = minInterval;
            if (reconnectCount > 3) {
                url = DEF_URL;
                long temp = minInterval * (reconnectCount - 2);
                reconnectTime = temp > maxInterval ? maxInterval : temp;
            }
            Log.i(TAG, "reconnect: 准备开始第" + reconnectCount + "次重连,重连间隔" + reconnectTime + "-- url:" + url);
            mHandler.postDelayed(mReconnectTask, reconnectTime);
        }
    }


    private Runnable mReconnectTask = new Runnable() {

        @Override
        public void run() {
            try {
                ws = new WebSocketFactory().createSocket(url, CONNECT_TIMEOUT)
                        .setFrameQueueSize(FRAME_QUEUE_SIZE)//设置帧队列最大值为5
                        .setMissingCloseFrameAllowed(false)//设置不允许服务端关闭连接却未发送关闭帧
                        .addListener(mListener = new WsListener())//添加回调监听
                        .connectAsynchronously();//异步连接
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    };


    private void cancelReconnect() {
        reconnectCount = 0;
        mHandler.removeCallbacks(mReconnectTask);
    }


    private boolean isNetConnect() {
        ConnectivityManager connectivity = (ConnectivityManager) APP.getInstance().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo info = connectivity.getActiveNetworkInfo();
            if (info != null && info.isConnected()) {
                // 当前网络是连接的
                if (info.getState() == NetworkInfo.State.CONNECTED) {
                    // 当前所连接的网络可用
                    return true;
                }
            }
        }
        return false;
    }


}