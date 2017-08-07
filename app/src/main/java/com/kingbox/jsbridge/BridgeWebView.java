package com.kingbox.jsbridge;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import com.kingbox.view.VideoEnabledWebChromeClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressLint({ "SetJavaScriptEnabled", "NewApi" })
public class BridgeWebView extends WebView implements JSBridge {

    private final String TAG = "BridgeWebView";

    public static final String toLoadJs = "JSBridge.js";
    Map<String, CallBackFunction> responseCallbacks = new HashMap<>();
    Map<String, BridgeHandler> messageHandlers = new HashMap<>();
    BridgeHandler defaultHandler = new DefaultHandler();

    private BridgeWebViewClient bridgeWebViewClient;

    private List<Message> startupMessage = new ArrayList<>();

    public List<Message> getStartupMessage() {
        return startupMessage;
    }

    public void setStartupMessage(List<Message> startupMessage) {
        this.startupMessage = startupMessage;
    }

    private long uniqueId = 0;

    public BridgeWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        addedJavascriptInterface = false;
        init();
    }

    public BridgeWebView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        addedJavascriptInterface = false;
        init();
    }

    public BridgeWebView(Context context) {
        super(context);
        addedJavascriptInterface = false;
        init();
    }

    /**
     *
     * @param handler
     *            default handler,handle messages send by js without assigned handler name,
     *            if js message has handler name, it will be handled by named handlers registered by native
     */
    public void setDefaultHandler(BridgeHandler handler) {
        this.defaultHandler = handler;
    }

    private void init() {
        this.setVerticalScrollBarEnabled(false);
        this.setHorizontalScrollBarEnabled(false);
        this.getSettings().setJavaScriptEnabled(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(true);
        }
        this.setWebViewClient(generateBridgeWebViewClient());
    }

    protected BridgeWebViewClient generateBridgeWebViewClient() {
        bridgeWebViewClient = new BridgeWebViewClient(this);
        return bridgeWebViewClient;
    }

    public void handlerReturnData(String url) {
        String functionName = BridgeUtil.getFunctionFromReturnUrl(url);
        CallBackFunction f = responseCallbacks.get(functionName);
        String data = BridgeUtil.getDataFromReturnUrl(url);
        if (f != null) {
            f.onCallBack(data);
            responseCallbacks.remove(functionName);
            return;
        }
    }

    @Override
    public void send(String data) {
        send(data, null);
    }

    @Override
    public void send(String data, CallBackFunction responseCallback) {
        doSend(null, data, responseCallback);
    }

    private void doSend(String handlerName, String data, CallBackFunction responseCallback) {
        Message m = new Message();
        if (!TextUtils.isEmpty(data)) {
            m.setData(data);
        }
        if (responseCallback != null) {
            String callbackStr = String.format(BridgeUtil.CALLBACK_ID_FORMAT, ++uniqueId + (BridgeUtil.UNDERLINE_STR + SystemClock.currentThreadTimeMillis()));
            responseCallbacks.put(callbackStr, responseCallback);
            m.setCallbackId(callbackStr);
        }
        if (!TextUtils.isEmpty(handlerName)) {
            m.setHandlerName(handlerName);
        }
        queueMessage(m);
    }

    private void queueMessage(Message m) {
        if (startupMessage != null) {
            startupMessage.add(m);
        } else {
            dispatchMessage(m);
        }
    }

    void dispatchMessage(Message m) {
        String messageJson = m.toJson();
        //escape special characters for json string
        messageJson = messageJson.replaceAll("(\\\\)([^utrn])", "\\\\\\\\$1$2");
        messageJson = messageJson.replaceAll("(?<=[^\\\\])(\")", "\\\\\"");
        String javascriptCommand = String.format(BridgeUtil.JS_HANDLE_MESSAGE_FROM_JAVA, messageJson);
        if (Thread.currentThread() == Looper.getMainLooper().getThread()) {
            this.loadUrl(javascriptCommand);
        }
    }

    public void flushMessageQueue() {
        if (Thread.currentThread() == Looper.getMainLooper().getThread()) {
            loadUrl(BridgeUtil.JS_FETCH_QUEUE_FROM_JAVA, new CallBackFunction() {

                @Override
                public void onCallBack(String data) {
                    // deserializeMessage
                    List<Message> list = null;
                    try {
                        list = Message.toArrayList(data);
                    } catch (Exception e) {
                        e.printStackTrace();
                        return;
                    }
                    if (list == null || list.size() == 0) {
                        return;
                    }
                    for (int i = 0; i < list.size(); i++) {
                        Message m = list.get(i);
                        String responseId = m.getResponseId();
                        // 是否是response
                        if (!TextUtils.isEmpty(responseId)) {
                            CallBackFunction function = responseCallbacks.get(responseId);
                            String responseData = m.getResponseData();
                            function.onCallBack(responseData);
                            responseCallbacks.remove(responseId);
                        } else {
                            CallBackFunction responseFunction = null;
                            // if had callbackId
                            final String callbackId = m.getCallbackId();
                            if (!TextUtils.isEmpty(callbackId)) {
                                responseFunction = new CallBackFunction() {
                                    @Override
                                    public void onCallBack(String data) {
                                        Message responseMsg = new Message();
                                        responseMsg.setResponseId(callbackId);
                                        responseMsg.setResponseData(data);
                                        queueMessage(responseMsg);
                                    }
                                };
                            } else {
                                responseFunction = new CallBackFunction() {
                                    @Override
                                    public void onCallBack(String data) {
                                        // do nothing
                                    }
                                };
                            }
                            BridgeHandler handler;
                            if (!TextUtils.isEmpty(m.getHandlerName())) {
                                handler = messageHandlers.get(m.getHandlerName());
                            } else {
                                handler = defaultHandler;
                            }
                            if (handler != null) {
                                handler.handler(m.getData(), responseFunction);
                            }
                        }
                    }
                }
            });
        }
    }

    public void loadUrl(String jsUrl, CallBackFunction returnCallback) {
        this.loadUrl(jsUrl);
        responseCallbacks.put(BridgeUtil.parseFunctionName(jsUrl), returnCallback);
    }

    /**
     * register handler,so that javascript can call it
     *
     * @param handlerName
     * @param handler
     */
    public void registerHandler(String handlerName, BridgeHandler handler) {
        if (handler != null) {
            messageHandlers.put(handlerName, handler);
        }
    }

    /**
     * call javascript registered handler
     *
     * @param handlerName
     * @param data
     * @param callBack
     */
    public void callAPI(String handlerName, String data, CallBackFunction callBack) {
        doSend(handlerName, data, callBack);
    }

    public void setMainHandler(Handler handler) {
        if (null != bridgeWebViewClient) {
            bridgeWebViewClient.setHandler(handler);
        }
    }


    //以下是兼容视频使用
    public interface ToggledFullscreenCallback
    {
        void toggledFullscreen(boolean fullscreen);
    }
    private VideoEnabledWebChromeClient videoEnabledWebChromeClient = null;

    private boolean addedJavascriptInterface;

    /**
     * Pass only a VideoEnabledWebChromeClient instance.
     */
    @Override
    @SuppressLint ("SetJavaScriptEnabled")
    public void setWebChromeClient(WebChromeClient client)
    {
        getSettings().setJavaScriptEnabled(true);

        if (client instanceof VideoEnabledWebChromeClient)
        {
            this.videoEnabledWebChromeClient = (VideoEnabledWebChromeClient) client;
        }

        super.setWebChromeClient(client);
    }
    @Override
    public void loadData(String data, String mimeType, String encoding)
    {
        addJavascriptInterface();
        super.loadData(data, mimeType, encoding);
    }
    @Override
    public void loadDataWithBaseURL(String baseUrl, String data,
                                    String mimeType, String encoding,
                                    String historyUrl)
    {
        addJavascriptInterface();
        super.loadDataWithBaseURL(baseUrl, data, mimeType, encoding, historyUrl);
    }
    @Override
    public void loadUrl(String url)
    {
        addJavascriptInterface();
        super.loadUrl(url);
    }
    @Override
    public void loadUrl(String url, Map<String, String> additionalHttpHeaders)
    {
        addJavascriptInterface();
        super.loadUrl(url, additionalHttpHeaders);
    }
    private void addJavascriptInterface()
    {
        System.out.println(addedJavascriptInterface);
        if (!addedJavascriptInterface)
        {
            // Add javascript interface to be called when the video ends (must be done before page load)
            addJavascriptInterface(new Object()
            {
            }, "_VideoEnabledWebView"); // Must match Javascript interface name of VideoEnabledWebChromeClient
            addedJavascriptInterface = true;
        }
    }
}
