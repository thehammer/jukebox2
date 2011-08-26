package jukebox;

public class JavaPlayer {

    public static void main(String[] args) throws InterruptedException {
    	Playable inMyLife = PlayableTrackFactory.build("/Users/Hammer/Documents/6-11 In My Life.m4a");
//    	Playable moby = PlayableTrackFactory.build("/Users/Hammer/Documents/workspace/02 All That I Need Is to Be Loved [L.mp3");
        inMyLife.playSnippet(83.9, 88.1);
//		inMyLife.play();

//    	List<Playable> tracks = new ArrayList<Playable>();
//    	tracks.add(PlayableTrackFactory.build("/Users/hammer/workspace/02 All That I Need Is to Be Loved [L.mp3"));
//    	tracks.add(PlayableTrackFactory.build("/Users/hammer/workspace/6-11 In My Life.m4a"));

//    	for (Playable track : tracks) {
//    		track.play();

//    		while (!track.isFinished()) {
//				Thread.sleep(100);
//			}
//		}
    }
}
