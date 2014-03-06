package com.luisdelarosa.glassvisualizer;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;
import android.widget.Button;
import ca.uol.aig.fftpack.RealDoubleFFT;

public class RenderThread extends Thread {
    private LiveCardRenderer liveCardRenderer;
	
    int frequency = 8000;
    int channelConfiguration = AudioFormat.CHANNEL_IN_MONO;
    int audioEncoding = AudioFormat.ENCODING_PCM_16BIT;
    private RealDoubleFFT transformer;
    int blockSize = 256;

    Button startStopButton;
    boolean started = false;

    Bitmap bitmap;
    Canvas canvas;

    /**
     * Initializes the background rendering thread.
     * @param liveCardRenderer 
     */
    public RenderThread(LiveCardRenderer liveCardRenderer) {
        this.liveCardRenderer = liveCardRenderer;
		bitmap = Bitmap.createBitmap((int) 256, (int) 100,
                Bitmap.Config.ARGB_8888);
        canvas = new Canvas(bitmap);
        
        transformer = new RealDoubleFFT(blockSize);
    }

    /**
     * Requests that the rendering thread exit at the next opportunity.
     */
    public synchronized void quit() {
    }

    @Override
    public void run() {
    	startAudioRecording();
    }

	private void repaint(double[] toTransform) {
		liveCardRenderer.repaint(toTransform);		
	}
	
	// Audio recording
	public void startAudioRecording() {
		started = true;
		
		try {
			// int bufferSize = AudioRecord.getMinBufferSize(frequency,
			// AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);
			int bufferSize = AudioRecord.getMinBufferSize(frequency,
					channelConfiguration, audioEncoding);

			AudioRecord audioRecord = new AudioRecord(
					MediaRecorder.AudioSource.MIC, frequency,
					channelConfiguration, audioEncoding, bufferSize);

			short[] buffer = new short[blockSize];
			double[] toTransform = new double[blockSize];

			audioRecord.startRecording();

			// started = true; hopes this should true before calling
			// following while loop

			while (started) {
				int bufferReadResult = audioRecord.read(buffer, 0, blockSize);

				for (int i = 0; i < blockSize && i < bufferReadResult; i++) {
					toTransform[i] = (double) buffer[i] / 32768.0; // signed
																	// 16
				} // bit
				transformer.ft(toTransform);

				// ready to show waveform here
				repaint(toTransform);
			}

			audioRecord.stop();

		} catch (Throwable t) {
			t.printStackTrace();
			Log.e("AudioRecord", "Recording Failed");
		}
	}
}
