package com.github.nwillc.simplecache.impl;

import org.junit.Test;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

public class KeyValueTest {
	@Test
	public void shouldStore() throws Exception {
		KeyValue<Long,String> kv = new KeyValue<>();

		final Long key = 42L;
		final String value = "The answer.";

		kv.setKey(key);
		kv.setValue(value);

		assertThat(kv.getKey()).isEqualTo(key);
		assertThat(kv.getValue()).isEqualTo(value);
	}
}