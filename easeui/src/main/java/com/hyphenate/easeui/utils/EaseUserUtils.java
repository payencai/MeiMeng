package com.hyphenate.easeui.utils;

import android.content.Context;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.hyphenate.easeui.R;
import com.hyphenate.easeui.EaseUI;
import com.hyphenate.easeui.EaseUI.EaseUserProfileProvider;
import com.hyphenate.easeui.domain.EaseUser;
import com.squareup.picasso.Picasso;


public class EaseUserUtils {

    static EaseUserProfileProvider userProvider;
    private static String headUrl;

    static {
        userProvider = EaseUI.getInstance().getUserProfileProvider();
    }

    /**
     * get EaseUser according username
     *
     * @param username
     * @return
     */
    public static EaseUser getUserInfo(String username) {
        if (userProvider != null) {
            return userProvider.getUser(username);
        }
        return null;
    }

    public static String getMeUserHeadUrl() {
        return headUrl;
    }

    public static void saveMeUserHeadUrl(String url) {
        headUrl = url;
    }

    /**
     * set user avatar
     *
     * @param username
     */
    public static void setUserAvatar(Context context, String username, ImageView imageView) {
        EaseUser user = getUserInfo(username);
        if (user != null && user.getAvatar() != null) {
            try {
                int avatarResId = Integer.parseInt(user.getAvatar());
                Glide.with(context).load(avatarResId).into(imageView);
            } catch (Exception e) {
                //use default avatar
                Glide.with(context).load(user.getAvatar()).diskCacheStrategy(DiskCacheStrategy.ALL)
                        .placeholder(R.drawable.ease_default_avatar).into(imageView);
            }
        } else {
//            Glide.with(context).load(R.drawable.ease_default_avatar).into(imageView);
            Picasso.with(context).load(R.drawable.ease_default_avatar).into(imageView);
        }
    }

    public static void setUserAvatar2(Context context, String imgUrl, ImageView imageView) {
        Picasso.with(context).load(imgUrl)
                .placeholder(R.drawable.ease_default_avatar)
                .into(imageView);
    }

    /**
     * set user's nickname
     */
    public static void setUserNick(String username, TextView textView) {
        if (textView != null) {
            EaseUser user = getUserInfo(username);
            if (user != null && user.getNick() != null) {
                textView.setText(user.getNick());
            } else {
                textView.setText(username);
            }
        }
    }

    public static void setNickName(String username, TextView textView) {
        if (textView != null)
            textView.setText(username);

    }
}

