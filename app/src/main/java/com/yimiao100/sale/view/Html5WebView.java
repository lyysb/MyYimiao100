package com.yimiao100.sale.view;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Message;
import android.util.AttributeSet;
import android.webkit.GeolocationPermissions;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.yimiao100.sale.base.ActivityCollector;
import com.yimiao100.sale.utils.LogUtil;
import com.yimiao100.sale.utils.NetStatusUtil;
import com.yimiao100.sale.utils.ProgressDialogUtil;
import com.yimiao100.sale.utils.ToastUtil;

/**
 * 自定义WebView
 */
public class Html5WebView extends WebView {

    private Context mContext;
    private ProgressDialog mLoadingProgress;
    public static boolean shouldOverrideUrlLoading = true;

    public Html5WebView(Context context) {
        this(context, null);
    }

    public Html5WebView(Context context, AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public Html5WebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        init();
    }

    private void init() {
        WebSettings mWebSettings = getSettings();
        mWebSettings.setSupportZoom(true);
        mWebSettings.setLoadWithOverviewMode(true);
        mWebSettings.setUseWideViewPort(true);
        mWebSettings.setDefaultTextEncodingName("utf-8");
        mWebSettings.setLoadsImagesAutomatically(true);

        //调用JS方法.安卓版本大于17,加上注解 @JavascriptInterface
        mWebSettings.setJavaScriptEnabled(true);
        mWebSettings.setSupportMultipleWindows(true);
        //缓存数据
        saveData(mWebSettings);
        newWin(mWebSettings);


        setWebChromeClient(webChromeClient);
        setWebViewClient(webViewClient);
    }

    /**
     * 多窗口的问题
     */
    private void newWin(WebSettings mWebSettings) {
        //html中的_bank标签就是新建窗口打开，有时会打不开，需要加以下
        //然后 复写 WebChromeClient的onCreateWindow方法
        mWebSettings.setSupportMultipleWindows(true);
        mWebSettings.setJavaScriptCanOpenWindowsAutomatically(true);
    }

    /**
     * HTML5数据存储
     */
    private void saveData(WebSettings mWebSettings) {
        //有时候网页需要自己保存一些关键数据,Android WebView 需要自己设置

        if (NetStatusUtil.isConnected(mContext)) {
            mWebSettings.setCacheMode(WebSettings.LOAD_DEFAULT);//根据cache-control决定是否从网络上取数据。
        } else {
            mWebSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);//没网，则从本地获取，即离线加载
        }

        mWebSettings.setDomStorageEnabled(true);
        mWebSettings.setDatabaseEnabled(true);
        mWebSettings.setAppCacheEnabled(true);
        String appCachePath = mContext.getCacheDir().getAbsolutePath();
        mWebSettings.setAppCachePath(appCachePath);
    }

    /**
     * 自定义WebViewClient
     */
    WebViewClient webViewClient = new WebViewClient() {


        // 多页面在同一个WebView中打开，就是不新建activity或者调用系统浏览器打开
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            //如果需要本地加载，则加载url
            if (shouldOverrideUrlLoading) {
                view.loadUrl(url);
            } else {
                Intent intent = new Intent();
                intent.setAction("android.intent.action.VIEW");
                Uri content_url = Uri.parse(url);
                intent.setData(content_url);
                ActivityCollector.getTopActivity().startActivity(intent);
                if (Html5WebView.this.canGoBack()) {
                    Html5WebView.this.goBack();
                }
            }
            LogUtil.Companion.d(url);
            return true;
        }


        // 网页开始加载
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            LogUtil.Companion.d("网页开始加载");
            if (mLoadingProgress == null) {
                mLoadingProgress = ProgressDialogUtil.getLoadingProgress(ActivityCollector.getTopActivity());
            }
            if (!mLoadingProgress.isShowing()) {
                mLoadingProgress.show();
            }
            view.getSettings().setJavaScriptEnabled(true);
            super.onPageStarted(view, url, favicon);
        }

        // 网页加载结束
        @Override
        public void onPageFinished(WebView view, String url) {
            LogUtil.Companion.d("网页加载结束");
            if (mLoadingProgress.isShowing() && mLoadingProgress != null) {
                mLoadingProgress.dismiss();
            }
            view.getSettings().setJavaScriptEnabled(true);
            super.onPageFinished(view, url);
            // 加载完成之后，添加监听图片的点击JS函数
            addImageClickListener(view);
        }

        @Override
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError
                error) {
            // 出现错误界面，隐藏WebView，弹出错误提示
            if (mLoadingProgress.isShowing() && mLoadingProgress != null) {
                mLoadingProgress.dismiss();
            }
            view.setVisibility(view.INVISIBLE);
            ToastUtil.showShort(mContext, "请检查您的网络设置。");
        }

        // 注入js函数监听
        private void addImageClickListener(WebView view) {
            // 这段js函数的功能就是，遍历所有的img几点，并添加onclick函数，函数的功能是在图片点击的时候调用本地java接口并传递url过去
            view.loadUrl("javascript:(function(){" +
                    "var objs = document.getElementsByTagName(\"img\"); " +
                    "for(var i=0;i<objs.length;i++)  " +
                    "{"
                    + "    objs[i].onclick=function()  " +
                    "    {  "
                    + "        window.imagelistner.openImage(this.src);  " +
                    "    }  " +
                    "}" +
                    "})()");
        }
    };

    WebChromeClient webChromeClient = new WebChromeClient() {

        //=========HTML5定位==========================================================
        //需要先加入权限
        //<uses-permission android:name="android.permission.INTERNET"/>
        //<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION"/>
        //<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION"/>
        @Override
        public void onReceivedIcon(WebView view, Bitmap icon) {
            super.onReceivedIcon(view, icon);
        }

        @Override
        public void onGeolocationPermissionsHidePrompt() {
            super.onGeolocationPermissionsHidePrompt();
        }

        @Override
        public void onGeolocationPermissionsShowPrompt(final String origin, final
        GeolocationPermissions.Callback callback) {
            callback.invoke(origin, true, false);//注意个函数，第二个参数就是是否同意定位权限，第三个是是否希望内核记住
            super.onGeolocationPermissionsShowPrompt(origin, callback);
        }
        //=========HTML5定位==========================================================


        //=========多窗口的问题==========================================================
        @Override
        public boolean onCreateWindow(WebView view, boolean isDialog, boolean isUserGesture,
                                      Message resultMsg) {
            HitTestResult result = view.getHitTestResult();
            String data = result.getExtra();
            view.loadUrl(data);
            return true;
        }
        //=========多窗口的问题==========================================================
    };
}
