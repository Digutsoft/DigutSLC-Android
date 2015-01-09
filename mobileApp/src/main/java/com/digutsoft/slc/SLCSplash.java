package com.digutsoft.slc;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;

public class SLCSplash extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);
        getSupportActionBar().hide();
        Handler handler = new Handler() {
            public void handleMessage(Message msg) {
                Intent intSplash = new Intent(SLCSplash.this, SLCMain.class);
                startActivity(intSplash);
                finish();
            }
        };
        handler.sendEmptyMessageDelayed(0, 1500);
    }
}
