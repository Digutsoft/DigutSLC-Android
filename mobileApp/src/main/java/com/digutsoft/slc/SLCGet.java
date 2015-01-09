package com.digutsoft.slc;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;

public class SLCGet extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String text = getIntent().getStringExtra(Intent.EXTRA_TEXT);
        Intent intOpen = new Intent(SLCGet.this, SLCMain.class);
        intOpen.putExtra("slcLoadedText", text);
        startActivity(intOpen);
        finish();
    }
}
