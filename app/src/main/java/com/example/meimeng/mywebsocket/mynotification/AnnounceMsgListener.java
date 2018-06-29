package com.example.meimeng.mywebsocket.mynotification;

/**
 * 作者：凌涛 on 2018/6/29 10:58
 * 邮箱：771548229@qq..com
 */
//具体逻辑对应的处理子类
@NotifyClass(AnnounceMsgNotify.class)
public class AnnounceMsgListener implements INotifyListener<AnnounceMsgNotify> {
    @Override
    public void fire(AnnounceMsgNotify announceMsgNotify) {
        //这里处理具体的逻辑
    }
}
