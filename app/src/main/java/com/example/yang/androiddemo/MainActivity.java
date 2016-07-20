package com.example.yang.androiddemo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends Activity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.test).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        Intent intent = null;
        switch (view.getId()) {
            case R.id.test:
//                intent = new Intent(this, PictureCropActivity.class);
////                intent.putExtra("cropedName", "editHeaderIcon.jpg");
//                intent.putExtra("cropedName", PUBLIC_CLASS_IMAGE);
//                intent.putExtra("isCapture", false);
//                intent.putExtra(PictureCropActivity.KEY_IS_HEIGHT_FIX, true);//new add
//                startActivityForResult(intent, GET_PICTURE_RROM_LIBRARY);
                break;

        }
    }
}
