package com.greentech.wnd.android;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
/**
 * 从主界面进入自诊fragment
 * @author zhoufazhan
 *
 */
public class AutognosisActivity extends FragmentActivity {
     @Override
    protected void onCreate(Bundle arg0) {
    	super.onCreate(arg0);
    	setContentView(R.layout.autognosis);
    	FragmentManager fragmentManager = getSupportFragmentManager();
    	FragmentTransaction fragmentTransaction=fragmentManager.beginTransaction();
    	fragmentTransaction.add(R.id.fragment, new SelectedDiseaseFragment(true)).commit();
    }
}
