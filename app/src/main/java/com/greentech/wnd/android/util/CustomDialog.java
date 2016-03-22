package com.greentech.wnd.android.util;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.greentech.wnd.android.R;

/**
 * 自定义dialog
 * 
 * @author Administrator
 * 
 */
public class CustomDialog {

	public static Dialog createLoadingDialog(Context context, String message) {
		LayoutInflater inflater = LayoutInflater.from(context);
		View v = inflater.inflate(R.layout.progressdialog, null);
		LinearLayout ll = (LinearLayout) v.findViewById(R.id.dialog_view);
		ImageView spaceshipImage = (ImageView) v.findViewById(R.id.img);
		TextView tipTextView = (TextView) v.findViewById(R.id.tipTextView);
		tipTextView.setText(message);
		Animation animation = AnimationUtils.loadAnimation(context,
				R.anim.loading_animation);
		spaceshipImage.setAnimation(animation);

		Dialog loadingDialog = new Dialog(context, R.style.loading_dialog);
		loadingDialog.setCancelable(true);
		loadingDialog.setContentView(ll);
		return loadingDialog;

	}

	public static Dialog createShowNoDataDialog(Context context, String message) {
		LayoutInflater inflater = LayoutInflater.from(context);
		View v = inflater.inflate(R.layout.nodatadialog, null);
		LinearLayout ll = (LinearLayout) v.findViewById(R.id.nodatadialog);
		TextView tipTextView = (TextView) v.findViewById(R.id.noDataText);
		tipTextView.setText(message);
		Dialog noDataDialog = new Dialog(context, R.style.loading_dialog);
		noDataDialog.setContentView(ll);
		return noDataDialog;

	}
}
