package jukebox;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.SourceDataLine;

public abstract class ThreadedPlayTrack implements Playable, Runnable {
    protected boolean playing;
    protected boolean paused;
    protected double start;
    protected double end;
    protected String source;
    protected Thread thread;
    
    protected abstract AudioFormat outputFormat();
    protected abstract byte[] readBytes();
    protected abstract void close();

    public ThreadedPlayTrack(String source) {
        this.source = source;
        this.playing = false;
        this.paused = false;
        this.start = 0.0;
        this.end = 3600.0; // hopefully no tracks over an hour long...
    }

    public void play() {
    	this.paused = false;
        
        if (thread == null) {
            this.thread = new Thread(this);
            this.playing = true;
            thread.start();            
        }
    }

    public void playSnippet(double start, double end) {
    	this.start = start;
    	this.end = end;
    	        
        if (this.start > 0.0) {
        	seek(this.start);
        }

    	play();
    }
    
    public void run() {
    	playTrack();
    }
    
    public void pause() {
        this.paused = true;
    }
    
    public void stop() {
    	this.playing = false;
    }
    
    public boolean isFinished() {
    	if (thread != null && thread.isAlive()) return false;
    	return true;
    }
    
    protected void playTrack() {
        SourceDataLine line = null;
        try {
            line = AudioSystem.getSourceDataLine(outputFormat());
            line.open();

            while (true) {
            	if (!playing) break;
            	if (paused) {
            		if (line.isRunning()) line.stop();
            		Thread.sleep(100);
            		continue;
            	}
            	if (!line.isRunning()) line.start();

            	byte[] data = readBytes();
            	if (data.length == 0) break;
            	line.write(data, 0, data.length);
            }

            if (playing && !paused) line.drain();
        } catch(Exception e) {
            throw new RuntimeException(e.toString());
        } finally {
            if (line != null) {
                line.stop();
                line.close();
            }
            close();
        }
    }
}
