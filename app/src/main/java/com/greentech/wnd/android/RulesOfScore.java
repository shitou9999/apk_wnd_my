package com.greentech.wnd.android;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

/**
 * 介绍积分规则
 * @author zhoufazhan
 *
 */
public class RulesOfScore extends BaseActivity{
	private ImageView back;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	setContentView(R.layout.rule_score);
    	back=(ImageView)findViewById(R.id.back);
    	back.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				RulesOfScore.this.finish();
				
			}
		});
    }
}
