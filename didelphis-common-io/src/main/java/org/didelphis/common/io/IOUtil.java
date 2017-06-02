package org.didelphis.common.io;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

/**
 * Created by samantha on 1/21/17.
 */
public final class IOUtil {

	private static final Logger LOG = LoggerFactory.getLogger(IOUtil.class);

	private IOUtil() {}
	
	public static String readPath(String path) {
		File file = new File(path);
		try (InputStream stream = new FileInputStream(file)) {
			return readStream(stream);
		} catch (IOException e) {
			LOG.error("Failed to read from path {}", path, e);
		}
		return null;
	}
	
	public static String readStream(InputStream stream) {
		try (Reader reader = new BufferedReader(new InputStreamReader(stream))) {
			return readString(reader);
		} catch (IOException e) {
			LOG.error("Failed to read from stream", e);
		}
		return null;
	}

	private static String readString(Reader reader) throws IOException {
		StringBuilder sb = new StringBuilder(0x1000);
		int r = reader.read();
		while (r >= 0) {
			sb.append((char) r);
			r = reader.read();
		}
		return sb.toString();
	}
}
