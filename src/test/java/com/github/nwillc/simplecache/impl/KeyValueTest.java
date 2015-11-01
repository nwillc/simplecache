package com.github.nwillc.simplecache.impl;

import org.junit.Test;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

public class KeyValueTest {
	@Test
	public void shouldStore() throws Exception {


		final Long key = 42L;
		final String value = "The answer.";

		KeyValue<Long,String> kv = new KeyValue<>(key, value);
		assertThat(kv.getKey()).isEqualTo(key);
		assertThat(kv.getValue()).isEqualTo(value);
	}
}