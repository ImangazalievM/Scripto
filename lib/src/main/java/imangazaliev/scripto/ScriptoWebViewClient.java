package imangazaliev.scripto;

import android.webkit.WebView;
import android.webkit.WebViewClient;

public class ScriptoWebViewClient extends WebViewClient{

    protected interface OnPageLoadedListener {
        void onPageLoaded();
    }

    private OnPageLoadedListener onPageLoadedListener;

    void setOnPageLoadedListener(OnPageLoadedListener onPageLoadedListener) {
        this.onPageLoadedListener = onPageLoadedListener;
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        super.onPageFinished(view, url);

        if (onPageLoadedListener != null) {
            onPageLoadedListener.onPageLoaded();
        }
    }

}
