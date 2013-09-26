package com.kipfer.eggdrop;

import android.graphics.Canvas;
import android.os.Looper;
import android.view.SurfaceHolder;

class EggDropThread extends Thread {
	private SurfaceHolder _surfaceHolder;
	private Panel _panel;
	private boolean _run = false;
	static final long FPS = 60;

	public EggDropThread(SurfaceHolder surfaceHolder, Panel panel) {
		_surfaceHolder = surfaceHolder;
		_panel = panel;

	}

	public SurfaceHolder getSurfaceHolder() {
		return _surfaceHolder;
	}

	public void setRunning(boolean run) {
		_run = run;
	}

	@Override
	public void run() {
		
		this.setPriority(MAX_PRIORITY);
		long ticksPS = 1000/ FPS;
		long startTime;
		long sleepTime;
		Canvas c;
		Looper.prepare();
		while (_run) {
			c = new Canvas();
			startTime = System.currentTimeMillis();
			try {
				c = _surfaceHolder.lockCanvas(null);
				synchronized (_surfaceHolder) {
					
					_panel.setStuff();
					_panel.makeEgg();
					_panel.updatePhysics();
					_panel.checkForHit();
					_panel.onDraw(c);
				}
			} finally {
				if (c != null) {
					_surfaceHolder.unlockCanvasAndPost(c);
				}
			}
			sleepTime = ticksPS-(System.currentTimeMillis()-startTime);
			try{
				if (sleepTime > 0){
					sleep(sleepTime);
				}else{
					sleep(10);
				}
			}catch(Exception e){}
		}
	}

	@Override
	public void interrupt() {
		super.interrupt();

	}
}