# jukebox2

[![Build Status](https://secure.travis-ci.org/thehammer/jukebox2.png)](http://travis-ci.org/thehammer/jukebox2)

Jukebox2 is a communal music player for team environments. Users can upload their own music, and jukebox2 will cycle through it and play a mix. 

<img src="https://raw.github.com/thehammer/jukebox2/master/resources/img/screenshot.png" />

## getting started

* put your music in music/<yourname>

## hacking

* use `lein spec spec/jukebox_web/models/xyz_spec.clj` to run a single test suite
* use `lein ring server-headless` to run the app in development mode