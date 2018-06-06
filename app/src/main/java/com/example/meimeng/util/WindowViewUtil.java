package com.example.meimeng.util;

/**
 * 作者：凌涛 on 2018/6/1 14:29
 * 邮箱：771548229@qq..com
 */
public class WindowViewUtil {

//
//    /**
//     * 4.4-5.0的处理：
//     * @param activity
//     * @param statusColor
//     */
//    public static void setStatusBarColor(Activity activity, int statusColor) {
//        Window window = activity.getWindow(); //设置Window为全透明
//        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//        ViewGroup mContentView = (ViewGroup) window.findViewById(Window.ID_ANDROID_CONTENT);
//        //获取父布局
//        View mContentChild = mContentView.getChildAt(0);
//        //获取状态栏高度
//        int statusBarHeight = getStatusBarHeight(activity);
//        //如果已经存在假状态栏则移除，防止重复添加
//        removeFakeStatusBarViewIfExist(activity);
//        //添加一个View来作为状态栏的填充
//        addFakeStatusBarView(activity, statusColor, statusBarHeight);
//        // 设置子控件到状态栏的间距
//        addMarginTopToContentChild(mContentChild, statusBarHeight);
//        //不预留系统栏位置
//        if (mContentChild != null) {
//            ViewCompat.setFitsSystemWindows(mContentChild, false);
//        }
//        //如果在Activity中使用了ActionBar则需要再将布局与状态栏的高度跳高一个ActionBar的高度，否则内容会被ActionBar遮挡
//        int action_bar_id = activity.getResources().getIdentifier("action_bar", "id", activity.getPackageName());
//        View view = activity.findViewById(action_bar_id);
//        if (view != null) {
//            TypedValue typedValue = new TypedValue();
//            if (activity.getTheme().resolveAttribute(R.attr.actionBarSize, typedValue, true)) {
//                int actionBarHeight = TypedValue.complexToDimensionPixelSize(typedValue.data, activity.getResources().getDisplayMetrics());
//                setContentTopPadding(activity, actionBarHeight);
//            }
//        }
//    }
//
//    private static void removeFakeStatusBarViewIfExist(Activity activity) {
//        Window window = activity.getWindow();
//        ViewGroup mDecorView = (ViewGroup) window.getDecorView();
//        View fakeView = mDecorView.findViewWithTag(TAG_FAKE_STATUS_BAR_VIEW);
//        if (fakeView != null) {
//            mDecorView.removeView(fakeView);
//        }
//    }
//
//    private static View addFakeStatusBarView(Activity activity, int statusBarColor, int statusBarHeight) {
//        Window window = activity.getWindow();
//        ViewGroup mDecorView = (ViewGroup) window.getDecorView();
//        View mStatusBarView = new View(activity);
//        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, statusBarHeight);
//        layoutParams.gravity = Gravity.TOP;
//        mStatusBarView.setLayoutParams(layoutParams);
//        mStatusBarView.setBackgroundColor(statusBarColor);
//        mStatusBarView.setTag(TAG_FAKE_STATUS_BAR_VIEW);
//        mDecorView.addView(mStatusBarView);
//        return mStatusBarView;
//    }
//
//    private static void addMarginTopToContentChild(View mContentChild, int statusBarHeight) {
//        if (mContentChild == null) {
//            return;
//        }
//        if (!TAG_MARGIN_ADDED.equals(mContentChild.getTag())) {
//            FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) mContentChild.getLayoutParams();
//            lp.topMargin += statusBarHeight;
//            mContentChild.setLayoutParams(lp);
//            mContentChild.setTag(TAG_MARGIN_ADDED);
//        }
//    }
//
//    public static void setContentTopPadding(Activity activity, int padding) {
//        ViewGroup mContentView = (ViewGroup) activity.getWindow().findViewById(Window.ID_ANDROID_CONTENT);
//        mContentView.setPadding(0, padding, 0, 0);
//    }
//
//    //Android5.0以上的处理：
//    public static void setStatusBarColor2(Activity activity, int statusColor) {
//        Window window = activity.getWindow();
//        //取消状态栏透明
//        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//        //添加Flag把状态栏设为可绘制模式
//        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//        //设置状态栏颜色
//        window.setStatusBarColor(statusColor);
//        //设置系统状态栏处于可见状态
//        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
//        //让view不根据系统窗口来调整自己的布局
//        ViewGroup mContentView = (ViewGroup) window.findViewById(Window.ID_ANDROID_CONTENT);
//        View mChildView = mContentView.getChildAt(0);
//        if (mChildView != null) {
//            ViewCompat.setFitsSystemWindows(mChildView, false);
//            ViewCompat.requestApplyInsets(mChildView);
//        }
//    }

}
