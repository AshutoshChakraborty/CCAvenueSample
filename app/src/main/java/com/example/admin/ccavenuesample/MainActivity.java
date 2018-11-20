package com.example.admin.ccavenuesample;

import android.content.Intent;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.admin.ccavenuesample.sdk.InterceptorHTTPClientCreator;
import com.example.admin.ccavenuesample.utility.AvenuesParams;
import com.example.admin.ccavenuesample.utility.ServiceUtility;

public class MainActivity extends AppCompatActivity {

    private TextInputEditText price;
    private TextInputEditText orderId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        InterceptorHTTPClientCreator.createInterceptorHTTPClient(getApplicationContext());
        init();
        Integer randomNum = ServiceUtility.randInt(0, 9999999);
        orderId.setText(randomNum.toString());
    }

    private void init() {
        price = findViewById(R.id.price);
        Button button = findViewById(R.id.button);
        orderId = findViewById(R.id.orderId);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,WebActivity.class);
                intent.putExtra(AvenuesParams.ACCESS_CODE,"put access code of your account");
                intent.putExtra(AvenuesParams.MERCHANT_ID, "put marchant Id");
                intent.putExtra(AvenuesParams.ORDER_ID, ServiceUtility.chkNull(orderId.getText()).toString().trim());
                intent.putExtra(AvenuesParams.CURRENCY, "INR");
                intent.putExtra(AvenuesParams.AMOUNT, ServiceUtility.chkNull(price.getText()).toString().trim());
                intent.putExtra(AvenuesParams.REDIRECT_URL, "put the redirect url");
                intent.putExtra(AvenuesParams.CANCEL_URL, "put the cancel url");
                intent.putExtra(AvenuesParams.RSA_KEY_URL,"http://52.66.123.118/omlApp/api/v1/test");
                startActivity(intent);
            }
        });

    }

}
