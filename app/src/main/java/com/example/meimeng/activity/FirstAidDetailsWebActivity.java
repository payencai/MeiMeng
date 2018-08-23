package com.example.meimeng.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.meimeng.R;
import com.example.meimeng.base.BaseActivity;
import com.example.meimeng.custom.KyLoadingBuilder;
import com.example.meimeng.util.LogUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class FirstAidDetailsWebActivity extends BaseActivity implements View.OnClickListener {


    private TextView title;
    private TextView aidTitle;
    private FrameLayout webViewFrameLayout;

    private int mId;
    private int mType;
    private String mTitle;
    private String mArticle;
    private String mArticleKey;
    private String mUrl = "http://www.baidu.com";
    private WebView webView;
    private KyLoadingBuilder mLoadView;
    private String url="http://www.1sos.cn/meiMeng_webui/medicine_detail.html?id=";

    @Override
    protected void initView() {
        Intent intent = getIntent();
        mId = intent.getIntExtra("id", -1);
        mType = intent.getIntExtra("type", -1);
        mTitle = intent.getStringExtra("title");
        mArticle = intent.getStringExtra("article");
        mArticleKey = intent.getStringExtra("articleKey");

        findViewById(R.id.back).setOnClickListener(this);
        this.title = (TextView) findViewById(R.id.title);
        aidTitle = (TextView) findViewById(R.id.aidTitle);
        webViewFrameLayout = (FrameLayout) findViewById(R.id.webViewFrameLayout);
        this.title.setText(mTitle);
        aidTitle.setText(mTitle);

        if (!TextUtils.isEmpty(mArticle)) {
            mUrl = url+mId;
        }
        Log.e("aaa",mArticle);
        Log.e("bbb",mUrl);
        webView = new WebView(getApplicationContext());
        webViewFrameLayout.addView(webView);
        initSetting();

    }


    @Override
    protected int getContentId() {
        return R.layout.activity_first_aid_details_web;
    }
    private void imgReset() {
        webView.loadUrl("javascript:(function(){"
                + "var objs = document.getElementsByTagName('img'); "
                + "for(var i=0;i<objs.length;i++)  " + "{"
                + "var img = objs[i];   "
                + "    img.style.width = '100%';   "
                + "    img.style.height = 'auto';   "
                + "}" + "})()");
    }


    private void initSetting() {
        WebSettings webSetting = webView.getSettings();
        //允许javascript执行
        webSetting.setJavaScriptEnabled(true);
        webSetting.setBuiltInZoomControls(true);
        webSetting.setJavaScriptCanOpenWindowsAutomatically(true);
        webSetting.setDomStorageEnabled(true);
        webSetting.setAllowFileAccess(true);
        webSetting.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        webSetting.setSupportZoom(true);
        webSetting.setUseWideViewPort(true);
        webSetting.setSupportMultipleWindows(true);
        webSetting.setAppCacheEnabled(true);
        webSetting.setGeolocationEnabled(true);
        webSetting.setAppCacheMaxSize(Long.MAX_VALUE);
        webSetting.setPluginState(WebSettings.PluginState.ON_DEMAND);
        webSetting.setRenderPriority(WebSettings.RenderPriority.HIGH);
        webSetting.setSupportMultipleWindows(false);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Log.e("url",url);
                view.loadUrl(url);
                return true;
            }

            //开始加载网页时回调
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
            }

            //网页加载结束时回调
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                imgReset();
            }
        });
        webView.setWebChromeClient(new WebChromeClient() {
            //获取网页标题
            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
            }

        });
        webView.loadUrl(mUrl);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                finish();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        //如果网页可以后退，则网页后退
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        webView.removeAllViews();
        webView.destroy();
    }

    private void upImage(String url, String filePath, final int id) {
        OkHttpClient mOkHttpClent = new OkHttpClient();
        File file = new File(filePath);
        MultipartBody.Builder builder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("image", "image",
                        RequestBody.create(MediaType.parse("image/png"), file));
        RequestBody requestBody = builder.build();
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
        Call call = mOkHttpClent.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (mLoadView != null) {
                            mLoadView.dismiss();
                        }
                        Toast.makeText(FirstAidDetailsWebActivity.this, "上传失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String string = response.body().string();
                Log.d("lingtaoshiwo", "onResponse: " + string);
                try {
                    JSONObject object = new JSONObject(string);
                    int resultCode = object.getInt("resultCode");
                    final String data = object.getString("data");
                    if (resultCode == 0) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (mLoadView != null) {
                                    mLoadView.dismiss();
                                }
                                Map<String, Object> params = new HashMap<>();
                                params.put("id", id);
                                params.put("avatar", data);
                                Toast.makeText(FirstAidDetailsWebActivity.this, "上传成功", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (mLoadView != null) {
                                    mLoadView.dismiss();
                                }
                                Toast.makeText(FirstAidDetailsWebActivity.this, "上传失败", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                } catch (JSONException e) {
                    if (mLoadView != null) {
                        mLoadView.dismiss();
                    }
                    e.printStackTrace();
                }

            }
        });


    }
}
