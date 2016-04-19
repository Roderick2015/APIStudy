package org.roderick.source.io;

import java.io.IOException;

import org.roderick.source.lang.AutoCloseableMe;

public interface CloseableMe extends AutoCloseableMe {

	@Override
	public void close() throws IOException;
	
}
