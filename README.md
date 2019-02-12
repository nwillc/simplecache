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
[![Coverage](https://codecov.io/gh/nwillc/simplecache/branch/master/graphs/badge.svg?branch=master)](https://codecov.io/gh/nwillc/simplecache)
[![license](https://img.shields.io/github/license/nwillc/simplecache.svg)](https://tldrlegal.com/license/-isc-license)
[![Travis](https://img.shields.io/travis/nwillc/simplecache.svg)](https://travis-ci.org/nwillc/simplecache)
[![Download](https://api.bintray.com/packages/nwillc/maven/simplecache/images/download.svg)](https://bintray.com/nwillc/maven/simplecache/_latestVersion)
