package com.example.henrik.googlemapsexample;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * Created by Henrik on 2016-05-16.
 */
public class Website_fragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.webpage, container, false);
        createWebView(v);
        return v;
    }

    public void createWebView(View v){
        WebView webView = (WebView) v.findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl(DataStorage.getInstance().getActiveWebLink());
        webView.setWebViewClient(new WebViewClient());

    }
}



