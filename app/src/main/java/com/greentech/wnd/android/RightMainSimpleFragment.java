package com.greentech.wnd.android;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

/**
 * 根据构造函数中传入的需要填充的Layout资源Id，将相应的Layout填充到指定的容器中。
 * @author wlj
 *
 */
public class RightMainSimpleFragment extends Fragment{
	private int resourceId;
	private ImageView mImageView;
	private Handler mHandler = new Handler();
	//-----------------------------------
	
	public RightMainSimpleFragment() {
		super();
	}
	
	public RightMainSimpleFragment(int resourceId) {
		super();
		this.resourceId = resourceId;
	}

	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main_right, container, false);
    
      
//        mImageView = (ImageView)view.findViewById(R.id.img);
//        mImageView.setImageResource(R.drawable.header);
        
//        //嵌套的Fragment
//        Manager= getChildFragmentManager();
//        if(mImageView != null) {
//        	//测试加载网络图片
//            new Thread(new Runnable() {
//    			
//    			@Override
//    			public void run() {
//    				final Bitmap bitmap = BitmapFactory.decodeStream(NetUtil.post("http://www.agri114.cn/images/xt_green/login.png"));
//    		        mHandler.post(new Runnable() {
//    					
//    					@Override
//    					public void run() {
//    						mImageView.setImageBitmap(bitmap);
//    					}
//    				});
//    			}
//    		}).start();
//        }
		return view;
	}
	
}
