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
     * ���ص��Ƕ�ȡ���ֽ�ֵ��8��������λ=1byte����ȡ���Ƕ����������������ݶ��룬��ĩβ���쳣ǰ�����������һֱ����
     *
     * <p> A subclass must provide an implementation of this method.
     *
     * @return     the next byte of data, or <code>-1</code> if the end of the
     *             stream is reached.
     * @exception  IOException  if an I/O error occurs.
     */
	public abstract int read() throws IOException;
	
	/**
	 * �Ѷ�ȡ���ֽڷ���b�У���Щ����ֻ�Ǽ�����ȡ�Ľ��������������{@link #read}������ʵ������
	 */
	public int read(byte b[]) throws IOException {
		return read(b, 0, b.length);
	}
	
	/**
	 * �Ѷ�ȡ���ֽ�ֵ����b[]������
	 * @param b �����ݵ�����
	 * @param off ƫ����
	 * @param len �����ȡ���ٸ��ֽ�
	 * @return ʵ�ʶ�ȡ���ֽڸ��������ֽڿɶ����쳣������-1
	 * @throws IOException
	 */
	public int read(byte b[], int off, int len) throws IOException {
		if (b == null) {
			throw new NullPointerException();
		} else if (off < 0 || len < 0 || len > b.length - off) { //b�ĳ��ȣ�����off+len����Ҫ��ȡ���ֽ�������Խ��
			throw new IndexOutOfBoundsException();
		} else if (len == 0) {
			return 0;
		}
		
		int c = read(); //���һ��ʼ�Ͷ�ȡ�쳣����ֱ���׳�
		if (c == -1) //������
			return -1;
		
		b[off] = (byte) c;
		int i = 1;
		
		try { //�˴���׽�쳣����ʹ�������쳣Ҳ�᷵���Ѿ���ȡ���ֽ���
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
	 * �����������������е�n���ֽڣ�����skipBuffer�С�
	 * nΪ����������������0
	 * @return �������ֽ���
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
	 * ��reset��������ʹ��
	 */
	public synchronized void mark(int readlimit) {};
	
	public synchronized void reset() throws IOException {
		throw new IOException("mark/reset not supported");
	}
	
	/**
	 * ���Ը����Ƿ�֧��mark��reset
	 */
	public boolean markSupported() {
		return false;
	}
	
}
