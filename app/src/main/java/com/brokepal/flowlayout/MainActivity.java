package com.brokepal.flowlayout;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.TextView;

import com.brokepal.flowlayout.view.FlowLayout;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initData();
    }

    public void initData() {
        String[] mVals = new String[]
                {
                        "Hello", "Android", "Weclome Hi ", "Button", "TextView", "Hello",
                        "Android", "Weclome", "Button ImageView", "TextView", "Helloworld",
                        "Android", "Weclome Hello", "Button Text", "TextView"
                };
        LayoutInflater mInflater = LayoutInflater.from(this);
        FlowLayout mFlowLayout = (FlowLayout) findViewById(R.id.id_flowlayout);
        for (int i = 0; i < mVals.length; i++)
        {
            TextView tv = (TextView) mInflater.inflate(R.layout.tv, mFlowLayout, false);
            tv.setText(mVals[i]);
            mFlowLayout.addView(tv);
        }
    }
}
