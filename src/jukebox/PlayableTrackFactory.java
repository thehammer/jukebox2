package jukebox;

public class PlayableTrackFactory {
	public static Playable build(String source) {
		if (source.endsWith("m4a")) {
			return new MPEG4Track(source);
		} else {
			return new BasicTrack(source);
		}
	}
}
