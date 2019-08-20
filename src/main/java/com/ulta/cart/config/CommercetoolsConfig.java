/*
 * Copyright (C) 2019 ULTA
 * http://www.ulta.com
 * BrijendraK@ulta.com
 * All rights reserved
 */
package com.ulta.cart.config;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import io.sphere.sdk.client.SphereClient;
import io.sphere.sdk.client.SphereClientConfig;
import io.sphere.sdk.client.SphereClientFactory;

@Configuration
public class CommercetoolsConfig {

	/**
	 * 
	 * @return
	 * @throws IOException
	 */
	@Autowired
	Environment env;

	@Bean(destroyMethod = "close")
	public SphereClient sphereClient() throws IOException {
		SphereClientConfig config = SphereClientConfig.of(env.getProperty("ctprojectKey"),
				env.getProperty("ctclientId"), env.getProperty("ctclientSecret"), env.getProperty("ctauthUrl"),
				env.getProperty("ctapiUrl"));
		final SphereClient asyncClient = SphereClientFactory.of().createClient(config);
		return asyncClient;
		// BlockingSphereClient.of(asyncClient, 20, TimeUnit.SECONDS);
	}
}