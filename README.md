# simplecache
This is an incomplete JSR 107 implementation. It's backed by a store-by-reference ConcurrentHashMap, doesn't offer 
management, and doesn't implement the annotations. Why did I bother? It was mostly an exercise.
I wanted something even simpler and cleaner then the reference implementation. Read-through and write-through are
implemented synchronously. Basically this is only useful to front things like slow out of process requests.

Currently this package includes under a dozen classes, depends only on the javax.cache-api artifact,
and builds to under 30K.

-----
[![ISC License](http://shields-nwillc.rhcloud.com/shield/tldrlegal?package=ISC)](http://shields-nwillc.rhcloud.com/homepage/tldrlegal?package=ISC)
[![Build Status](http://shields-nwillc.rhcloud.com/shield/travis-ci?path=nwillc&package=simplecache)](http://shields-nwillc.rhcloud.com/homepage/travis-ci?path=nwillc&package=simplecache)
[![Download](http://shields-nwillc.rhcloud.com/shield/jcenter?path=nwillc&package=simplecache)](http://shields-nwillc.rhcloud.com/homepage/jcenter?group=com.github.nwillc&package=simplecache&path=nwillc/maven/simplecache)
[![Coverage Status](http://shields-nwillc.rhcloud.com/shield/codecov?path=github/nwillc&package=simplecache)](http://shields-nwillc.rhcloud.com/homepage/codecov?path=github/nwillc&package=simplecache)






