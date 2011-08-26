package jukebox

object ScalaPlayer {
  def main(args:Array[String]) = {
    PlayableTrackFactory.build("/Users/Hammer/Documents/6-11 In My Life.m4a").play
  }
}