package com.app.mathpix_sample;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import io.github.kexanie.library.MathView;

public class ResultActivity extends AppCompatActivity {

    public static final String RESULT = "result";

    public static void goResultActivity(Activity activity,String result){
        Intent intent = new Intent(activity,ResultActivity.class).putExtra(RESULT,result);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        MathView mathView = (MathView) findViewById(R.id.tv_result);

        Intent intent = getIntent();
        String result = intent.getStringExtra(RESULT);
        mathView.setText(result);
    }
}
