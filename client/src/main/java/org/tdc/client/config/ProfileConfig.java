package org.tdc.client.config;

/**
 * Defines server profile information.
 */
public interface ProfileConfig {
	String getProfileName();
	String getServerHostName();
	int getServerPort();
}
