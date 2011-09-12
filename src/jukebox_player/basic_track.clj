(ns jukebox-player.basic-track
  (:use [jukebox-player.playable])
  (:import [javax.sound.sampled AudioFormat AudioFormat$Encoding AudioSystem DataLine$Info SourceDataLine]
           [java.io File]))

(def sample-size-in-bits 16)
(def big-endian false)

(defrecord BasicTrack [audio-stream])

(defn load-basic-track [file]
  (BasicTrack. (AudioSystem/getAudioInputStream (File. file))))

(defn- compatible? [track]
  (let [info (DataLine$Info. SourceDataLine (.getFormat track) AudioSystem/NOT_SPECIFIED)]
    (AudioSystem/isLineSupported info)))

(defn- create-format [track]
   (let [source-format (.getFormat track)
         channels (.getChannels source-format)
         sample-rate (.getSampleRate source-format)]
     (AudioFormat. AudioFormat$Encoding/PCM_SIGNED
                   sample-rate
                   sample-size-in-bits
                   channels
                   (* channels (/ sample-size-in-bits 8))
                   sample-rate
                   big-endian)))

(defn- convert [track]
  (AudioSystem/getAudioInputStream (create-format track) track))

(extend-type BasicTrack
  Playable
   (in-stream [basic-track]
     (let [audio-stream (:audio-stream basic-track)]
       (if (compatible? audio-stream)
         audio-stream
         (convert audio-stream))))

   (out-format [basic-track]
     (let [audio-stream (:audio-stream basic-track)]
       (if (compatible? audio-stream)
         (.getFormat audio-stream)
         (create-format audio-stream)))))
