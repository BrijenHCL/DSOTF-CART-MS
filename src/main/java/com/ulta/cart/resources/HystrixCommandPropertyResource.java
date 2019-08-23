package com.ulta.cart.resources;

import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import com.netflix.config.ConfigurationManager;
@Component
public class HystrixCommandPropertyResource {

	private final static String THREAD_POOL_IDENTIFIER = "ThreadPool.";
	private final static String COMMAND_KEY_IDENTIFIER = "Command.";
	private final static String COLLAPSER = "Collapser";
	static Logger log = LoggerFactory.getLogger(HystrixCommandPropertyResource.class);

	public HystrixCommandPropertyResource() {

		try {
			PropertyResourceManager hystrixProperties = new PropertyResourceManager("/HystrixCommand.properties");
			Set<Object> keys = hystrixProperties.getAllKeys();
			for (Object k : keys) {
				String key = (String) k;
				String value = hystrixProperties.getPropertyValue(key);
				if (value != null) {
					if (key.contains(COMMAND_KEY_IDENTIFIER)) {
						if (value.matches("((-|\\+)?[0-9]+(\\.[0-9]+)?)+")) {
							ConfigurationManager.getConfigInstance().setProperty(key, Long.valueOf(value));

						} else if ("true".equals(value) || "false".equals(value)) {
							ConfigurationManager.getConfigInstance().setProperty(key, Boolean.valueOf(value));

						} else {
							ConfigurationManager.getConfigInstance().setProperty(key, value);

						}
					} else if (key.contains(THREAD_POOL_IDENTIFIER)) {
						if (value.matches("((-|\\+)?[0-9]+(\\.[0-9]+)?)+")) {
							ConfigurationManager.getConfigInstance().setProperty(key, Long.valueOf(value));

						} else if ("true".equals(value) || "false".equals(value)) {
							ConfigurationManager.getConfigInstance().setProperty(key, Boolean.valueOf(value));

						} else {
							ConfigurationManager.getConfigInstance().setProperty(key, value);

						}
					} else if (key.contains(COLLAPSER)) {
						if (value.matches("((-|\\+)?[0-9]+(\\.[0-9]+)?)+")) {
							ConfigurationManager.getConfigInstance().setProperty(key, Long.valueOf(value));

						} else if ("true".equals(value) || "false".equals(value)) {
							ConfigurationManager.getConfigInstance().setProperty(key, Boolean.valueOf(value));

						} else {
							ConfigurationManager.getConfigInstance().setProperty(key, value);

						}
					}
				}
			}
		} catch (Exception e) {
			log.debug("getting error when we are fetching/orchestrating the hystrix properties file", e.getMessage());

		}
	}
}