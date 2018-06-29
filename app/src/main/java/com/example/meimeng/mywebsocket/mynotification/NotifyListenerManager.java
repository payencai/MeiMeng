package com.example.meimeng.mywebsocket.mynotification;

import android.util.Log;

import com.example.meimeng.mywebsocket.request.Response;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.util.HashMap;
import java.util.Map;

/**
 * 作者：凌涛 on 2018/6/29 10:54
 * 邮箱：771548229@qq..com
 */
public class NotifyListenerManager {
    private final String TAG = this.getClass().getSimpleName();
    private volatile static NotifyListenerManager manager;
    private Map<String, INotifyListener> map = new HashMap<>();

    private NotifyListenerManager() {
        regist();
    }

    public static NotifyListenerManager getInstance() {
        if (manager == null) {
            synchronized (NotifyListenerManager.class) {
                if (manager == null) {
                    manager = new NotifyListenerManager();
                }
            }
        }
        return manager;
    }

    private void regist() {
        map.put("notifyAnnounceMsg", new AnnounceMsgListener());
    }

    public void fire(Response response) {
        String action = response.getAction();
        String resp = response.getResp();
        INotifyListener listener = map.get(action);
        if (listener == null) {
            Log.i(TAG, "no found notify listener");
            return;
        }

        NotifyClass notifyClass = listener.getClass().getAnnotation(NotifyClass.class);
        Class<?> clazz = notifyClass.value();
        Object result = null;
        try {
            result = new Gson().fromJson(resp, clazz);
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        }
        Log.i(TAG, result.toString());
        listener.fire(result);
    }

}
