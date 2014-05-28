package com.luisdelarosa.glassvisualizer;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.google.android.glass.timeline.LiveCard;
import com.google.android.glass.timeline.LiveCard.PublishMode;

public class LiveCardService extends Service {

	private static final String LIVE_CARD_ID = "com.luisdelarosa.glassvisualizer.VISUALIZER_LIVE_CARD";
	
	private LiveCard mLiveCard;

	private LiveCardRenderer mLiveCardRenderer;
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

    @Override
    public void onCreate() {
        super.onCreate();
    }
    
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (mLiveCard == null) {
            mLiveCard = new LiveCard(this, LIVE_CARD_ID);

            mLiveCard.setDirectRenderingEnabled(true);
            mLiveCardRenderer = new LiveCardRenderer();
            mLiveCard.getSurfaceHolder().addCallback(mLiveCardRenderer);

            Intent menuIntent = new Intent(this, MenuActivity.class);
            mLiveCard.setAction(PendingIntent.getActivity(this, 0, menuIntent, 0));

            mLiveCard.publish(PublishMode.REVEAL);
        } else {
        	// TODO use publish trick to jump to live card
        }

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        if (mLiveCard != null && mLiveCard.isPublished()) {
            mLiveCard.getSurfaceHolder().removeCallback(mLiveCardRenderer);
            mLiveCard.unpublish();
            mLiveCard = null;
        }
        super.onDestroy();
    }

}
