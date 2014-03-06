package com.luisdelarosa.glassvisualizer;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;

public class LiveCardRenderer implements Callback {

	private static final String TAG = "LiveCardRenderer";

	private int mSurfaceWidth;
	private int mSurfaceHeight;
	private SurfaceHolder mHolder;
	private com.luisdelarosa.glassvisualizer.RenderThread mRenderThread;

	private Paint paint;

	public LiveCardRenderer() {
		super();
        paint = new Paint();
        paint.setColor(Color.GREEN);
	}

	// Surface callbacks
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		mSurfaceWidth = width;
		mSurfaceHeight = height;
		doLayout();
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		mHolder = holder;

		mRenderThread = new RenderThread(this);
		mRenderThread.start();
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		mRenderThread.quit();
	}

	// Layout
	private void doLayout() {
		// TODO Auto-generated method stub

	}

	// Painting
	/**
	 * Repaints the compass.
	 * @param toTransform 
	 */
	synchronized void repaint(double[] toTransform) {
		Canvas canvas = null;

		try {
			canvas = mHolder.lockCanvas();
		} catch (RuntimeException e) {
			Log.d(TAG, "lockCanvas failed", e);
		}

		if (canvas != null) {
			drawWaveform(canvas, toTransform);

			try {
				mHolder.unlockCanvasAndPost(canvas);
			} catch (RuntimeException e) {
				Log.d(TAG, "unlockCanvasAndPost failed", e);
			}
		}
	}

	private void drawWaveform(Canvas canvas, double[] toTransform) {
		canvas.drawColor(Color.BLACK);

		for (int i = 0; i < toTransform.length; i++) {
			int x = i;
			int downy = (int) (100 - (toTransform[i] * 10));

			//canvas.drawLine(x, downy, x, upy, paint);
			
			int scaledX = (int) (((double) x / (double) toTransform.length) * mSurfaceWidth);
			int scaledY = (int) (((double) downy / 100.0) * mSurfaceHeight);
			canvas.drawLine(scaledX, scaledY, scaledX, mSurfaceHeight, paint);
		}
	}

}
