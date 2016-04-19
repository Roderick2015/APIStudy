package org.roderick.source.io;

import java.io.IOException;

public abstract class InputStreamMe implements CloseableMe {

	public static final int MAX_SKIP_BUFFER_SIZE = 2048;
	
	public abstract int read() throws IOException;
	
	@Override
	public void close() throws IOException {
		
	}

}
