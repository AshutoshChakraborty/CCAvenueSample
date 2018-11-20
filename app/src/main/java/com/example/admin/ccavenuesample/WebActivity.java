package com.example.admin.ccavenuesample;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.example.admin.ccavenuesample.sdk.Sdk;
import com.example.admin.ccavenuesample.sdk.Service;
import com.example.admin.ccavenuesample.utility.AvenuesParams;
import com.example.admin.ccavenuesample.utility.Constants;
import com.example.admin.ccavenuesample.utility.RSAUtility;
import com.example.admin.ccavenuesample.utility.ServiceUtility;

import org.apache.http.util.EncodingUtils;

import java.net.URLEncoder;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WebActivity extends AppCompatActivity {
    private ProgressDialog dialog;
    Intent mainIntent;
    String html, encVal;
    private String accessCode;
    private String orderId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);
        mainIntent = getIntent();
        renderView();

    }

    private void renderView() {
        dialog = new ProgressDialog(this);
        dialog.setMessage("Please wait...");
        dialog.setCancelable(false);
        dialog.show();
        Service service = new Sdk.Builder().build(this).getService();
        accessCode = mainIntent.getStringExtra(AvenuesParams.ACCESS_CODE);
        orderId = mainIntent.getStringExtra(AvenuesParams.MERCHANT_ID);
        HashMap<String, String> map = new HashMap<>();
        map.put("order_id", orderId);
        map.put("access_code", accessCode);
        map.put("billing_name", "Test");
        map.put("billing_address", "Kolkata");
        map.put("billing_state", "WB");
        map.put("billing_country", "India");
        map.put("billing_city", "Kolkata");
        map.put("billing_zip", "700001");
        map.put("billing_tel", "0000000009");
        map.put("billing_email", "test@test.com");


        service.getRsaKey("test", map).enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                if (response.isSuccessful()) {
//                    String s=decrypt(response.body());
//                    Log.e("onResponse: ", s);
                    dialog.dismiss();
                    StringBuffer vEncVal = new StringBuffer("");
                    vEncVal.append(ServiceUtility.addToPostParams(AvenuesParams.AMOUNT, mainIntent.getStringExtra(AvenuesParams.AMOUNT)));
                    vEncVal.append(ServiceUtility.addToPostParams(AvenuesParams.CURRENCY, mainIntent.getStringExtra(AvenuesParams.CURRENCY)));
                    vEncVal.append(ServiceUtility.addToPostParams("billing_name", "Test"));
                    vEncVal.append(ServiceUtility.addToPostParams("billing_address", "Kolkata"));
                    vEncVal.append(ServiceUtility.addToPostParams("billing_state", "WB"));
                    vEncVal.append(ServiceUtility.addToPostParams("billing_country", "India"));
                    vEncVal.append(ServiceUtility.addToPostParams("billing_city", "Kolkata"));
                    vEncVal.append(ServiceUtility.addToPostParams("billing_zip", "700001"));
                    vEncVal.append(ServiceUtility.addToPostParams("billing_tel", "0000000009"));
                    vEncVal.append(ServiceUtility.addToPostParams("billing_email", "test@test.com"));
                    encVal = RSAUtility.encrypt(vEncVal.substring(0, vEncVal.length() - 1), response.body());
                    Log.e("encVal ", encVal);
                    openWebView();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                dialog.dismiss();
                Log.e("onFailure: ", t.getMessage());

            }
        });
    }


    private void openWebView() {
        if (dialog.isShowing())
            dialog.dismiss();

        @SuppressWarnings("unused")
        class MyJavaScriptInterface {
            @JavascriptInterface
            public void processHTML(String html)
            {
                // process the html as needed by the app
                String status = null;
                if(html.contains("Failure")){
                    status = "Transaction Declined!";
                }else if(html.contains("Success")){
                    status = "Transaction Successful!";
                }else if(html.contains("Aborted")){
                    status = "Transaction Cancelled!";
                }else{
                    status = "Status Not Known!";
                }
                Toast.makeText(getApplicationContext(), status, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(),Status.class);
                intent.putExtra("transStatus", status);
                startActivity(intent);
            }
        }

        final WebView webview = (WebView) findViewById(R.id.webview);
        webview.getSettings().setJavaScriptEnabled(true);
        webview.addJavascriptInterface(new MyJavaScriptInterface(), "HTMLOUT");
        webview.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(webview, url);
                if(url.indexOf("/ccavResponseHandler.php")!=-1){
                    webview.loadUrl("javascript:window.HTMLOUT.processHTML('<head>'+document.getElementsByTagName('html')[0].innerHTML+'</head>');");
                }
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                Toast.makeText(getApplicationContext(), "Oh no! " + description, Toast.LENGTH_SHORT).show();
            }
        });
        /* An instance of this class will be registered as a JavaScript interface */
        StringBuffer params = new StringBuffer();
        params.append(ServiceUtility.addToPostParams(AvenuesParams.ACCESS_CODE,accessCode));
        params.append(ServiceUtility.addToPostParams(AvenuesParams.MERCHANT_ID,mainIntent.getStringExtra(AvenuesParams.MERCHANT_ID)));
        params.append(ServiceUtility.addToPostParams(AvenuesParams.ORDER_ID,orderId));
        params.append(ServiceUtility.addToPostParams(AvenuesParams.REDIRECT_URL,mainIntent.getStringExtra(AvenuesParams.REDIRECT_URL)));
        params.append(ServiceUtility.addToPostParams(AvenuesParams.CANCEL_URL,mainIntent.getStringExtra(AvenuesParams.CANCEL_URL)));
        params.append(ServiceUtility.addToPostParams(AvenuesParams.ENC_VAL,URLEncoder.encode(encVal)));

        String vPostParams = params.substring(0,params.length()-1);
        Log.e("vPostParams ", vPostParams);
        try {
            webview.postUrl(Constants.TRANS_URL, EncodingUtils.getBytes(vPostParams, "UTF-8"));
//				webview.postUrl(Constants.TRANS_URL, URLEncoder.encode(vPostParams, "UTF-8").getBytes());
            Log.e("EncodingUtils ", EncodingUtils.getBytes(vPostParams, "UTF-8").toString());
        } catch (Exception e) {
            showToast("Exception occured while opening webview.");
        }


    }
    public void showToast(String msg) {
        Toast.makeText(this, "Toast: " + msg, Toast.LENGTH_LONG).show();
    }


}
