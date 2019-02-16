package com.example.meimeng.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.GeolocationPermissions;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.example.meimeng.APP;
import com.example.meimeng.R;
import com.example.meimeng.base.BaseActivity;
import com.example.meimeng.bean.AddressBean;
import com.example.meimeng.bean.WebLocResultEntity;
import com.example.meimeng.util.ContactInformEntity;
import com.example.meimeng.util.PermissionUtils;
import com.example.meimeng.util.WebViewMod;
import com.example.meimeng.util.X5WebView;
import com.google.gson.Gson;
import com.tencent.smtt.export.external.interfaces.SslError;
import com.tencent.smtt.export.external.interfaces.SslErrorHandler;

import java.net.URLDecoder;


/**
 * Created by ckerv on 2018/2/6.
 */

public class ChooseAddressWebActivity extends BaseActivity {


    private X5WebView mWebView;

    private String mUrl = "http://47.106.164.34/map/";
    private String mPoiName;
    private String mPoiAddress;

    private double lat;
    private double lng;

    //setContentView(R.layout.activity_choose_addr_web);

    @Override
    protected int getContentId() {
        return R.layout.activity_choose_addr_web;
    }
    @Override
    public void initView() {
        mWebView = (X5WebView) findViewById(R.id.choose_addr_web_wv);
        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        TextView title=findViewById(R.id.title);
        title.setText("选择地址");
        initWebView();

    }

    private void initWebView() {
        com.tencent.smtt.sdk.WebSettings settings = mWebView.getSettings();
        settings.setJavaScriptEnabled(true);
        //设置WebView是否使用viewport
        settings.setUseWideViewPort(true);
        //设置WebView是否使用预览模式加载界面。
        settings.setLoadWithOverviewMode(true);
        //设置WebView是否支持使用屏幕控件或手势进行缩放
        settings.setSupportZoom(true);
        //设置WebView是否使用其内置的变焦机制，该机制集合屏幕缩放控件使用
        settings.setBuiltInZoomControls(true);
        mWebView.setWebViewClient(new com.tencent.smtt.sdk.WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(com.tencent.smtt.sdk.WebView view, String url) {
                Log.e("url",url);
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onReceivedSslError(com.tencent.smtt.sdk.WebView view, SslErrorHandler handler, SslError error) {
                super.onReceivedSslError(view, handler, error);
                handler.proceed();
            }
            //开始加载网页时回调
            @Override
            public void onPageStarted(com.tencent.smtt.sdk.WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                Log.d("onPageFinished", "------------:" + url);
                if (url.contains("locationPicker")) {
                    int index = url.indexOf("loc");
                    String jsonStr = url.substring(index + "loc=".length(), url.length());
                    Log.e("json",jsonStr);
                    WebLocResultEntity webLocResultEntity = new Gson().fromJson(Uri.decode(jsonStr), WebLocResultEntity.class);
                    lat = webLocResultEntity.getLatlng().getLat();
                    lng = webLocResultEntity.getLatlng().getLng();
                    mPoiName = webLocResultEntity.getPoiname();
                    mPoiAddress = webLocResultEntity.getPoiaddress();
                    AddressBean  locationEntity = new AddressBean();
                    locationEntity.setLat(lat);
                    locationEntity.setLon(lng);
                    locationEntity.setName(mPoiName);
                    locationEntity.setAddress(mPoiAddress);
                    locationEntity.setCity(webLocResultEntity.getCityname());
                    Intent intent = new Intent();
                    intent.putExtra("address", locationEntity);
                    setResult(RESULT_OK, intent);
                    finish();
                    mWebView.stopLoading();
                }

//                if (url.startsWith("http://www.test.com/?loc=")) {
//                    try {
//                        String[] split = url.split("loc=");
//                        String s = split[1];
//                        String strUTF8 = URLDecoder.decode(s, "UTF-8");
//                        Intent intent = new Intent();
//                        intent.putExtra("address", strUTF8);
//                        setResult(3, intent);
//                        finish();
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }
            }

            //网页加载结束时回调
            @Override
            public void onPageFinished(com.tencent.smtt.sdk.WebView view, String url) {
                super.onPageFinished(view, url);
                Log.e("onPageFinished", "onPageFinished: " + url);
            }
        });
        mWebView.setWebChromeClient(new com.tencent.smtt.sdk.WebChromeClient(){

        });
        mWebView.loadUrl("http://120.79.176.228:8080/gaote-web/map/index.html");
    }








    /**
     * 改写物理按键——返回的逻辑
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (mWebView.canGoBack()) {
                mWebView.goBack();//返回上一页面
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }


}
