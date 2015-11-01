package com.github.nwillc.simplecache;

import java.util.Optional;

public class Configuration<K,V> {
	private Optional<Lookup<K,V>> readThrough = Optional.empty();

	public void setReadThroughLookup(Lookup<K, V> readThrough) {
		this.readThrough = Optional.ofNullable(readThrough);
	}

	public Optional<Lookup<K, V>> getReadThrough() {
		return readThrough;
	}
}
