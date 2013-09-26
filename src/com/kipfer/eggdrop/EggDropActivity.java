package com.kipfer.eggdrop;

import com.google.ads.AdRequest;
import com.google.ads.AdSize;
import com.google.ads.AdView;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.ViewGroup.LayoutParams;
import android.widget.RelativeLayout;


public class EggDropActivity extends Activity {
	private AudioManager audio;
	private AdView add;
	private Panel panel;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if(Integer.valueOf(android.os.Build.VERSION.SDK)
				> 8){
		  StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		    StrictMode.setThreadPolicy(policy);
		}
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		audio = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		setContentView(R.layout.main);

		RelativeLayout addspot = new RelativeLayout(this);
		addspot.setHorizontalGravity(Gravity.CENTER_HORIZONTAL);
		add = new AdView(this, AdSize.BANNER, "a14f00a2fca0c24"); 
		add.setId(33333);
		RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
		lp.addRule(RelativeLayout.CENTER_IN_PARENT);
		lp.addRule(RelativeLayout.ALIGN_PARENT_TOP);
		panel = new Panel(this);
		addspot.addView(panel);
		addspot.addView(add, lp);
		AdRequest adrequest = new AdRequest();
		add.loadAd(adrequest);
		setContentView(addspot);
		switchadds();
		
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		float density = dm.densityDpi;
		Log.i("screen",""+density);
		
	}

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			add.setVisibility(msg.what);
		}
	};

	public void switchadds() {

		new Thread() {

			boolean show = false;

			@Override
			public void run() {
				while (true) {
					if(show){
						handler.sendEmptyMessage(View.VISIBLE);
					} else {
						handler.sendEmptyMessage(View.GONE);
					}
					show = !show;
					try{
						Thread.sleep(30000);
					}catch(InterruptedException e){
						
					}
				}
			}
		}.start();
	}

	@Override
	protected void onDestroy() {
		add.destroy();

		super.onDestroy();
	}

	@Override
	protected void onPause() {
		super.onPause();
		
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_BACK:
			finish();
			return true;
		case KeyEvent.KEYCODE_VOLUME_UP:
			audio.adjustStreamVolume(AudioManager.STREAM_MUSIC,
					AudioManager.ADJUST_RAISE, AudioManager.FLAG_SHOW_UI);
			return true;
		case KeyEvent.KEYCODE_VOLUME_DOWN:
			audio.adjustStreamVolume(AudioManager.STREAM_MUSIC,
					AudioManager.ADJUST_LOWER, AudioManager.FLAG_SHOW_UI);
			return true;
		default:
			return false;
		}
	}

}