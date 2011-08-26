package jukebox;

import net.sourceforge.jaad.aac.Decoder;
import net.sourceforge.jaad.aac.SampleBuffer;
import net.sourceforge.jaad.mp4.MP4Container;
import net.sourceforge.jaad.mp4.api.AudioTrack;
import net.sourceforge.jaad.mp4.api.Frame;
import net.sourceforge.jaad.mp4.api.Movie;
import net.sourceforge.jaad.mp4.api.Track;

import javax.sound.sampled.AudioFormat;
import java.io.RandomAccessFile;
import java.util.List;

public class MPEG4Track extends ThreadedPlayTrack {
	protected AudioTrack trackData;
	protected Decoder decoder;

	public MPEG4Track(String source) {
		super(source);
		try {
			final MP4Container container = new MP4Container(new RandomAccessFile(source, "r"));
			final Movie movie = container.getMovie();
			final List<Track> tracks = movie.getTracks(AudioTrack.AudioCodec.AAC);
			this.trackData = (AudioTrack) tracks.get(0);
			this.decoder = new Decoder(this.trackData.getDecoderSpecificInfo());
        } catch(Exception e) {
            throw new RuntimeException(e.toString());
        }
	}
	
	@Override
    protected AudioFormat outputFormat() {
        return new AudioFormat(trackData.getSampleRate(), trackData.getSampleSize(), trackData.getChannelCount(), true, true);
    }

	@Override
	protected void close() {
	}

	@Override
	protected byte[] readBytes() {
        final SampleBuffer buffer = new SampleBuffer();
        
        try {
        	Frame frame = trackData.readNextFrame();
        	if (frame == null) return new byte[0];
        	if (frame.getTime() > this.end) return new byte[0];
        	
        	this.decoder.decodeFrame(frame.getData(), buffer);
        } catch(Exception e) {
            throw new RuntimeException(e.toString());
        }
        
        return buffer.getData();
	}
	
    public void seek(double time) {
    	trackData.seek(time);
    }
}
