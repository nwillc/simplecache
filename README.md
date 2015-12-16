# simplecache
This is an incomplete JSR 107 implementation. It's just backed by a ConcurrentHashMap, and does'nt offer
management features, and has a few other short cuts.

Why did I bother? It was mostly as a learning experience. But also I did want something
simpler and cleaner then the reference implementation, with JDK8 improvements.
Basically this is only useful to front things like slow out of process requests.

Currently the package includes about a dozen classes, depends only on the javax.cache-api artifact,
and builds to under 50K.

The annotations, should you want them, are in a separate package
called [cache-annotations](https://github.com/nwillc/cache-annotations).

More back story about this code is found [in this blog post.](https://nwillc.wordpress.com/2015/11/22/jcache-jsr-107-under-the-hood/)


-----
[![ISC License](http://shields-nwillc.rhcloud.com/shield/tldrlegal?package=ISC)](http://shields-nwillc.rhcloud.com/homepage/tldrlegal?package=ISC)
[![Build Status](http://shields-nwillc.rhcloud.com/shield/travis-ci?path=nwillc&package=simplecache)](http://shields-nwillc.rhcloud.com/homepage/travis-ci?path=nwillc&package=simplecache)
[![Download](http://shields-nwillc.rhcloud.com/shield/jcenter?path=nwillc&package=simplecache)](http://shields-nwillc.rhcloud.com/homepage/jcenter?group=com.github.nwillc&package=simplecache&path=nwillc/maven/simplecache)
[![Coverage Status](http://shields-nwillc.rhcloud.com/shield/codecov?path=github/nwillc&package=simplecache)](http://shields-nwillc.rhcloud.com/homepage/codecov?path=github/nwillc&package=simplecache)






