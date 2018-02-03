package com.freetek.rxjavademo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.freetek.rxjavademo.dagger2.DaggerSampleComponent;

import javax.inject.Inject;

import io.reactivex.BackpressureStrategy;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Inject
    public FlowableSamples flowableSamples;

    private Button mBtn_Start;
    private Button mBtn_Request;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        DaggerSampleComponent.create().inject(this);
        initView();
    }

    private void initView() {
        mBtn_Start = (Button)findViewById(R.id.btn_start);
        mBtn_Request = (Button)findViewById(R.id.btn_request);
        mBtn_Start.setOnClickListener(this);
        mBtn_Request.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_start:
                flowableSamples.flowableASyncTimes(130);
                break;

            case R.id.btn_request:
                flowableSamples.request(129);
                break;

            default:

                break;
        }
    }
}
