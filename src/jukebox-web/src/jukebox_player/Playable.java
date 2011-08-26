package jukebox;

public interface Playable {
    public void play();
    public void playSnippet(double start, double end);
    public void pause();
    public void seek(double time);
    public void stop();
    public boolean isFinished();
}
