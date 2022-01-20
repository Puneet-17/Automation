package com.huseby.framework.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

/**
 * THis class functions are used only TDD //com.setup.installation.InstallationSetup only
 * @author user
 *
 */
public class InstallationUtils {

	public static String runCommand(Session session, String command, boolean print) throws JSchException, IOException {
		ChannelExec channel = (ChannelExec) session.openChannel("exec");
		StringBuilder output = new StringBuilder();
		channel.setCommand(command);

		try (InputStream stdOut = channel.getInputStream(); InputStream stdErr = channel.getErrStream()) {
			channel.connect();
			// read from stdOut and stdErr
			BufferedReader reader = new BufferedReader(new InputStreamReader(stdOut));
			String line;

			while ((line = reader.readLine()) != null) {
				if (print) {
					System.out.println(line);
				}
				output.append(line);
			}
		}
		channel.disconnect();
		return output.toString();
	}

	public static String preStartCrosscode(Session session) throws JSchException, IOException {
		ChannelExec channel = (ChannelExec) session.openChannel("exec");
		StringBuilder output = new StringBuilder();
		channel.setCommand("sudo /opt/crosscode/pre_start_crosscode.sh");
		OutputStream out = channel.getOutputStream();

		try (InputStream stdOut = channel.getInputStream(); InputStream stdErr = channel.getErrStream()) {
			channel.connect();
			out.write(("https://onpath.crosscode.io \n .\n \n \n \n \n \n").getBytes());
			out.flush();
			// read from stdOut and stdErr
			BufferedReader reader = new BufferedReader(new InputStreamReader(stdOut));
			String line;

			while ((line = reader.readLine()) != null) {
				System.out.println(line);
				output.append(line);
			}
		}
		channel.disconnect();
		return output.toString();
	}
}
