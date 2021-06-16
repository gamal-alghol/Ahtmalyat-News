package com.karkor.ahtmalyat.View;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.karkor.ahtmalyat.R;

public class web extends AppCompatActivity {
    WebView myWebView;
    ProgressBar bar;
    private float m_downX;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);
        myWebView =  findViewById(R.id.webview);
        bar=findViewById(R.id.progressBar5);

    //    myWebView.getSettings().setJavaScriptEnabled(true);
      //  myWebView.getSettings().setLoadWithOverviewMode(true);
        //myWebView.getSettings().setUseWideViewPort(true);
       // myWebView.getSettings().setPluginState(WebSettings.PluginState.OFF);
        //  myWebView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT));
        myWebView.getSettings().setJavaScriptEnabled(true);
        if (savedInstanceState == null) {
            myWebView.loadUrl(getIntent().getStringExtra("link"));
            initWebView();
        }


        myWebView.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                bar.setVisibility(View.GONE);
            }
        });
    }
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null) {

            myWebView.restoreState(savedInstanceState);

        }
    }
    private void initWebView() {
        myWebView.setWebChromeClient(new MyWebChromeClient(this));
        myWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                bar.setVisibility(View.VISIBLE);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                myWebView.loadUrl(url);
                bar.setVisibility(View.GONE);
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                bar.setVisibility(View.GONE);
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
            }
        });

        myWebView.clearCache(true);
        myWebView.clearHistory();
        myWebView.getSettings().setJavaScriptEnabled(true);
        myWebView.setHorizontalScrollBarEnabled(false);
myWebView.setOnTouchListener(new View.OnTouchListener() {
    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {

        if (motionEvent.getPointerCount() > 1) {
            //Multi touch detected
            return true;
        }

        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                // save the x
                m_downX = motionEvent.getX();
            }
            break;

            case MotionEvent.ACTION_MOVE:
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP: {
                // set x so that it doesn't move
                motionEvent.setLocation(m_downX, motionEvent.getY());
            }
            break;
        }

        return false;
    }


});

    }
    private class MyWebChromeClient extends WebChromeClient {
        Context context;

        public MyWebChromeClient(Context context) {
            super();
            this.context = context;
        }
    }

}