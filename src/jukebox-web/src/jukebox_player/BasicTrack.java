package jukebox;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class BasicTrack extends ThreadedPlayTrack {
	protected final int MAX_READ_BYTES = 4096;
	protected AudioInputStream inputStream;
	protected long position;
	protected int bytesPerSample;

	public BasicTrack(String source) {
		super(source);
		initializeStream();
	}

	protected synchronized void initializeStream() {
		AudioInputStream in;
		try {
			in = AudioSystem.getAudioInputStream(new FileInputStream(new File(
					this.source)));
		} catch (Exception e) {
			throw new RuntimeException(e.toString());
		}

		AudioFormat sourceFormat = in.getFormat();
		AudioFormat decodedFormat = new AudioFormat(
				AudioFormat.Encoding.PCM_SIGNED, // Encoding to use
				sourceFormat.getSampleRate(), // sample rate (same as base
												// format)
				16, // sample size in bits (thx to Javazoom)
				sourceFormat.getChannels(), // # of Channels
				sourceFormat.getChannels() * 2, // Frame Size
				sourceFormat.getSampleRate(), // Frame Rate
				false // Big Endian
		);

		this.position = 0;
		this.bytesPerSample = sourceFormat.getChannels() * 2;
		this.inputStream = AudioSystem.getAudioInputStream(decodedFormat, in);
	}

	@Override
	protected AudioFormat outputFormat() {
		return inputStream.getFormat();
	}

	protected synchronized float currentTime() {
		return ((float) this.position / (float) (outputFormat().getFrameRate() * outputFormat()
				.getFrameSize()));
	}

	@Override
	protected synchronized void close() {
		try {
			if (inputStream != null)
				inputStream.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected synchronized byte[] readBytes() {
		long bytesLeft = byteIndexForTime(this.end) - this.position;
		if (bytesLeft <= 0) {
			return new byte[0];
		}

		int bytesToRead = (int) ((bytesLeft > MAX_READ_BYTES) ? MAX_READ_BYTES
				: bytesLeft);
		byte[] rawData = new byte[bytesToRead];
		int bytesRead;

		try {
			bytesRead = inputStream.read(rawData, 0, rawData.length);
			this.position += bytesRead;
		} catch (Exception e) {
			throw new RuntimeException(e.toString());
		}

		if (bytesRead == -1)
			return new byte[0];
		if (bytesRead < rawData.length) {
			byte[] data = new byte[bytesRead];
			System.arraycopy(rawData, 0, data, 0, bytesRead);
			return data;
		}
		return rawData;
	}

	protected synchronized long byteIndexForTime(double time) {
		return (long) (time * outputFormat().getFrameRate() * outputFormat()
				.getFrameSize());
	}

	public synchronized void seek(double time) {
		long byteIndex = byteIndexForTime(time);
		if (byteIndex < this.position) {
			initializeStream();
		}

		long bytesLeft = byteIndex - this.position;
		while (bytesLeft > 0) {
			int bytesToRead = (int) ((bytesLeft > MAX_READ_BYTES) ? MAX_READ_BYTES
					: bytesLeft);
			try {
				bytesLeft -= inputStream.read(new byte[bytesToRead], 0, bytesToRead);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
		this.position = byteIndex;
	}
}
