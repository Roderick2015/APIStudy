package org.roderick.source.io;

import java.io.IOException;

import org.roderick.source.lang.AutoCloseable;

public interface Closeable extends AutoCloseable {

	@Override
	public void close() throws IOException;
	
}
