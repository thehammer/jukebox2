require 'java'
java_package 'jukebox'

class RubyPlayer
  def main(*args)
    PlayableTrackFactory.build("/Users/Hammer/Documents/6-11 In My Life.m4a").play
  end
end
