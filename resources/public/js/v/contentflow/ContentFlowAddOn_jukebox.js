new ContentFlowAddOn ('jukebox', {
  ContentFlowConf: {
    maxItemHeight: 350,   // 0 == auto, >0 max item height in px
    fixItemSize: true,    // don't scale item size to fit image, crop image if bigger than item
    circularFlow: false,  // should the flow wrap around at begging and end?
    startItem:  "first",  // which item should be shown on startup?
    flowSpeedFactor: 0.2, // how fast should it scroll?
  }
});
