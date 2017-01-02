package org.tdc.client.cli;

/**
 * 'Main' class for launching primary command-line interface.
 */
public class TDCClient {
	public static void main(String[] args) {
		CLIOperations ops = new CLIOperations();
		ops.execute(args);
	}
}
