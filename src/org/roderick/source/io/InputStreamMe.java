package org.roderick.source.io;

import java.io.IOException;

public abstract class InputStreamMe implements CloseableMe {

	public static final int MAX_SKIP_BUFFER_SIZE = 2048;
	
	/**
     * Reads the next byte of data from the input stream. The value byte is
     * returned as an <code>int</code> in the range <code>0</code> to
     * <code>255</code>. If no byte is available because the end of the stream
     * has been reached, the value <code>-1</code> is returned. This method
     * blocks until input data is available, the end of the stream is detected,
     * or an exception is thrown.
     * 返回的是读取的字节值，8个二进制位=1byte，读取的是二进制流。在有数据读入，流末尾，异常前，这个方法会一直阻塞
     *
     * <p> A subclass must provide an implementation of this method.
     *
     * @return     the next byte of data, or <code>-1</code> if the end of the
     *             stream is reached.
     * @exception  IOException  if an I/O error occurs.
     */
	public abstract int read() throws IOException;
	
	/**
	 * 把读取的字节放入b中，这些方法只是监听读取的结果，真正的流在{@link #read}方法的实现类中
	 */
	public int read(byte b[]) throws IOException {
		return read(b, 0, b.length);
	}
	
	/**
	 * 把读取的字节值放入b[]数组中
	 * @param b 放数据的数组
	 * @param off 偏移量
	 * @param len 打算读取多少个字节
	 * @return 实际读取的字节个数，无字节可读或异常，返回-1
	 * @throws IOException
	 */
	public int read(byte b[], int off, int len) throws IOException {
		if (b == null) {
			throw new NullPointerException();
		} else if (off < 0 || len < 0 || len > b.length - off) { //b的长度，不够off+len（需要读取的字节数），越界
			throw new IndexOutOfBoundsException();
		} else if (len == 0) {
			return 0;
		}
		
		int c = read(); //如果一开始就读取异常，则直接抛出
		if (c == -1) //读完了
			return -1;
		
		b[off] = (byte) c;
		int i = 1;
		
		try { //此处捕捉异常，即使发生了异常也会返回已经读取的字节数
			for (; i < len; i++) {
				c = read();
				
				if (c == -1)
					break;
				
				b[off + i] = (byte) c;
			}
		} catch (IOException ee) {
			
		}
		
		return i;
	}
	
	/**
	 * 跳过并丢弃数据流中的n个字节，存入skipBuffer中。
	 * n为负数则不跳过，返回0
	 * @return 跳过的字节数
	 */
	public long skip(long n) throws IOException {
		long remaining = n;
		int nr;
		
		if (n <= 0)
			return 0;
		
		int size = (int) Math.min(MAX_SKIP_BUFFER_SIZE, remaining);
		byte[] skipBuffer = new byte[size];
		while(remaining > 0) {
			nr = read(skipBuffer, 0, (int)Math.min(size, remaining));
			if (nr < 0)
				break;
			remaining -= nr;
		}
		
		return n - remaining;
	}
	
	public int available() throws IOException {
		return 0;
	}
	
	@Override
	public void close() throws IOException {}
	
	/**
	 * 与reset方法配套使用
	 */
	public synchronized void mark(int readlimit) {};
	
	public synchronized void reset() throws IOException {
		throw new IOException("mark/reset not supported");
	}
	
	/**
	 * 测试该流是否支持mark和reset
	 */
	public boolean markSupported() {
		return false;
	}
	
}
