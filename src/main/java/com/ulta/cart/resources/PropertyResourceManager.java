package com.ulta.cart.resources;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PropertyResourceManager {
	static Logger log = LoggerFactory.getLogger(PropertyResourceManager.class);
	private Properties prop = null;

	public PropertyResourceManager(String fileName) {

		InputStream is = null;
		try {
			this.prop = new Properties();
			is = this.getClass().getResourceAsStream(fileName);
			prop.load(is);
		} catch (FileNotFoundException e) {
			log.error("File not found exception", e);
		} catch (IOException e) {
			log.error("Error occured during property file loading", e);
		}
	}

	public Set<Object> getAllKeys() {
		Set<Object> keys = prop.keySet();
		return keys;
	}

	public String getPropertyValue(String key) {
		return this.prop.getProperty(key);
	}
}