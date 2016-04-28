package org.roderick.source.lang;

import java.io.ObjectStreamField;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.Arrays;

/**
 * final�࣬���ɼ̳�
 */
public final class StringMe implements Serializable, Comparable<String>, CharSequence {
	private static final long serialVersionUID = -2141535242724633987L;
	/**
	 * final������ֻ�ɸ�ֵһ�Σ��һ���JVM�ı����ڷ��볣���أ�value����ֱ��ָ��õ�ַ
	 */
	private final char value[];
	private int hash;
	/**
	 * ��ȡ���л��е�Fields
	 */
	private static final ObjectStreamField[] serialPersistentFields = 
			new ObjectStreamField[0];
	
	public StringMe() {
		this.value = new char[0]; //��ʱhash����Ĭ��ֵ0
	}
	
	/**
	 * ����һ��original��ֵ��hashֵ��һ�������ޱ�Ҫ������ʹ�øù��캯�����ַ����ǳ�����ֵ��ͬ���Թ���
	 */
	public StringMe(StringMe original) {
		this.value = original.value; //value��˽�еģ�������ͬ����ɼ���
		this.hash = original.hash;
	}
	
	/**
	 * ���ݴ����char��������String������Ϊ��copy�����ģ����Ը����޸Ĳ���Ӱ��
	 */
	public StringMe(char value[]) {
		this.value = Arrays.copyOf(value, value.length);
	}
	
	public StringMe(char value[], int offset, int count) {
		if (offset < 0)
			throw new StringIndexOutOfBoundsException(offset);
		if (count < 0)
			throw new StringIndexOutOfBoundsException(count);
		if (offset > value.length - count) //�ܳ��Ȳ�����ƫ��
			throw new StringIndexOutOfBoundsException(offset + count);
		
		this.value = Arrays.copyOfRange(value, offset, count);
	}
	
	/**
	 * ����Unicode�ַ�����(codePoints)����String���󣬿ɽ�ȡ���в���ֵ
	 */
	public StringMe(int[] codePoints, int offset, int count) {
		if (offset < 0)
			throw new StringIndexOutOfBoundsException(offset);
		if (count < 0)
			throw new StringIndexOutOfBoundsException(count);
		if (offset > codePoints.length - count)
			throw new StringIndexOutOfBoundsException(offset + count);

		final int end = offset + count;

		// Pass 1: Compute precise size of char[]
		int n = count;
		for (int i = offset; i < end; i++) {
			int c = codePoints[i];
			if (Character.isBmpCodePoint(c)) //�Ƿ�ΪBMP�����ַ������о��Ŀ��Ժ��������
				continue;
			else if (Character.isValidCodePoint(c))
				n++;
			else
				throw new IllegalArgumentException(Integer.toString(c));
		}

		// Pass 2: Allocate and fill in char[]
		final char[] v = new char[n];

		for (int i = offset, j = 0; i < end; i++, j++) {
			int c = codePoints[i];
			if (Character.isBmpCodePoint(c))
				v[j] = (char) c;
			/*else //���������protectedȨ�ޣ����޷����ʵ������ע��
				Character.toSurrogates(c, v, j++);*/
		}

		this.value = v;
	}
	
	private static void checkBounds(byte[] bytes, int offset, int length) {
		if (length < 0)
			throw new StringIndexOutOfBoundsException(length);
		if (offset < 0)
			throw new StringIndexOutOfBoundsException(offset);
		if (offset > bytes.length - length)
			throw new StringIndexOutOfBoundsException(offset + length);
	}
	
	/**
	 * �����ַ���(utf-8��ISO-8859-1��)����byte���飬�˴�charsetName����Ϊ��
	 */
	public StringMe(byte bytes[], int offset, int length, String charsetName) {
		if (charsetName == null)
			throw new NullPointerException("charsetName");
		checkBounds(bytes, offset, length);
		//StringCoding�࣬��java.lang�����ɼ�
		//this.value = StringCoding.decode(charsetName, bytes, offset, length);
		this.value = new char[0];
	}
	
	public StringMe(byte bytes[], int offset, int length, Charset charset) {
		if (charset == null)
			throw new NullPointerException("charset");
		checkBounds(bytes, offset, length);
		// StringCoding�࣬��java.lang�����ɼ�
		// this.value = StringCoding.decode(charsetName, bytes, offset, length);
		this.value = new char[0];
	}
	
	public StringMe(byte bytes[], String charsetName)
            throws UnsupportedEncodingException {
        this(bytes, 0, bytes.length, charsetName);
    }
	
	public StringMe(byte bytes[], Charset charset) {
        this(bytes, 0, bytes.length, charset);
    }
	
	/*public StringMe(StringBuffer buffer) {
		synchronized (buffer) { //buffer.getValue()���ɼ�
			this.value = Arrays.copyOf(buffer.getValue(), buffer.length());
		}
	}*/
	
	/*public StringMe(StringBuilder builder) {
        this.value = Arrays.copyOf(builder.getValue(), builder.length());
    }*/
	
	/*
	 * Package private constructor which shares value array for speed. this
	 * constructor is always expected to be called with share==true. a separate
	 * constructor is needed because we already have a public String(char[])
	 * constructor that makes a copy of the given char[].
	 */
	StringMe(char[] value, boolean share) {
	    // assert share : "unshared not supported";
	    this.value = value;
	}
	
	@Override
	public int length() {
		return value.length;
	}
	
	public boolean isEmpty() {
		return value.length == 0;
	}

	@Override
	public char charAt(int index) {
		if ((index < 0) || (index >= value.length)) {
            throw new StringIndexOutOfBoundsException(index);
        }
		return value[index];
	}

	//�����ַ���ָ��λ�õ�Unicode���룬��'A'����65
	/*public int codePointAt(int index) {
        if ((index < 0) || (index >= value.length)) {
            throw new StringIndexOutOfBoundsException(index);
        }
        return Character.codePointAtImpl(value, index, value.length);
    }*/
	
	//����codePointAt����һ��λ��
	/*public int codePointBefore(int index) {
        int i = index - 1;
        if ((i < 0) || (i >= value.length)) {
            throw new StringIndexOutOfBoundsException(index);
        }
        return Character.codePointBeforeImpl(value, index, 0);
    }*/
	
	//���������Χ֮���Unicode����
	/*public int codePointCount(int beginIndex, int endIndex) {
        if (beginIndex < 0 || endIndex > value.length || beginIndex > endIndex) {
            throw new IndexOutOfBoundsException();
        }
        return Character.codePointCountImpl(value, beginIndex, endIndex - beginIndex);
    }*/
	
	/*public int offsetByCodePoints(int index, int codePointOffset) {
        if (index < 0 || index > value.length) {
            throw new IndexOutOfBoundsException();
        }
        return Character.offsetByCodePointsImpl(value, 0, value.length,
                index, codePointOffset);
    }*/
	
	/**
	 * �����ݸ��Ƶ�dst�ַ�������
	 * @param  dstBegin  dst�е���ʼƫ����
	 */
	void getChars(char dst[], int dstBegin) {
		System.arraycopy(value, 0, dst, dstBegin, value.length);
	}
	
	public void getChars(int srcBegin, int srcEnd, char dst[], int dstBegin) {
        if (srcBegin < 0) {
            throw new StringIndexOutOfBoundsException(srcBegin);
        }
        if (srcEnd > value.length) {
            throw new StringIndexOutOfBoundsException(srcEnd);
        }
        if (srcBegin > srcEnd) {
            throw new StringIndexOutOfBoundsException(srcEnd - srcBegin);
        }
        System.arraycopy(value, srcBegin, dst, dstBegin, srcEnd - srcBegin);
    }
	
	//��ָ�����ַ�����valueֵתΪbyte����
	/*public byte[] getBytes(String charsetName) throws UnsupportedEncodingException {
		if (charsetName == null)
			throw new NullPointerException();
		return StringCoding.encode(charsetName, value, 0, value.length);
	}
	
	/*public byte[] getBytes(Charset charset) {
        if (charset == null) throw new NullPointerException();
        return StringCoding.encode(charset, value, 0, value.length);
    }
	
	public byte[] getBytes() {
        return StringCoding.encode(value, 0, value.length);
    }*/
	
	@Override
	public boolean equals(Object anObject) {
		if (this == anObject) {
            return true;
        }
		
		if (anObject instanceof StringMe) {
			StringMe anotherString = (StringMe) anObject;
			int n = value.length;
			if (n == anotherString.value.length) { //�ȱȳ��ȣ���������ַ�
				char v1[] = value;
				char v2[] = anotherString.value;
				int i = 0;
				while (n-- != 0) {
					if (v1[i] != v2[i]) //����ֱ����n���Ӻ������
						return false;
					i++;
				}
				return true;
			}
		}
		return false;
	}
	
	/**
	 * String��StringBuffer���ݱȽϣ��˷�����ͬ����
	 */
	/*public boolean contentEquals(StringBuffer sb) {
		return contentEquals((CharSequence)sb);
	}
	
	private boolean nonSyncContentEquals(AbstractStringBuilder sb) {
		char v1[] = value;
		char v2[] = sb.getValue();
		int n = v1.length;
		if (n != sb.length()) {
			return false;
		}
		for (int i = 0; i < n; i++) {
			if (v1[i] != v2[i]) {
				return false;
			}
		}
		return true;
	}
	
	public boolean contentEquals(CharSequence cs) {
		// Argument is a StringBuffer, StringBuilder
        if (cs instanceof AbstractStringBuilder) {
            if (cs instanceof StringBuffer) {
                synchronized(cs) {
                   return nonSyncContentEquals((AbstractStringBuilder)cs);
                }
            } else {
                return nonSyncContentEquals((AbstractStringBuilder)cs);
            }
        }
        // Argument is a String
        if (cs instanceof String) {
            return equals(cs);
        }
        // Argument is a generic CharSequence
        char v1[] = value;
        int n = v1.length;
        if (n != cs.length()) {
            return false;
        }
        for (int i = 0; i < n; i++) {
            if (v1[i] != cs.charAt(i)) {
                return false;
            }
        }
        return true;
	}*/
	
	public boolean equalsIgnoreCase(StringMe anotherString) {
		return (this == anotherString) ? true
				: (anotherString != null)
				&& (anotherString.value.length == value.length)
				&& regionMatches(true, 0, anotherString, 0, this.value.length);
	}
	
	public int compareTo(String anotherString) {
		return 0;
	}
	/*@Override
	public int compareTo(StringMe anotherString) {
		int len1 = value.length;
		int len2 = anotherString.value.length;
		int lim = Math.min(len1, len2);
		char v1[] = value;
		char v2[] = anotherString.value;
		
		int k = 0;
        while (k < lim) {
            char c1 = v1[k];
            char c2 = v2[k];
            if (c1 != c2) {
                return c1 - c2;
            }
            k++;
        }
        return len1 - len2; //Ԫ��һ���ȳ���
	}*/
	
	/**
	 * �ֲ��Ƚϲ����Դ�Сд
	 */
	public boolean regionMatches(int toffset, StringMe other, int ooffset,
            int len) {
        char ta[] = value;
        int to = toffset;
        char pa[] = other.value;
        int po = ooffset;
        // Note: toffset, ooffset, or len might be near -1>>>1.
        if ((ooffset < 0) || (toffset < 0)
                || (toffset > (long)value.length - len)
                || (ooffset > (long)other.value.length - len)) {
            return false;
        }
        while (len-- > 0) {
            if (ta[to++] != pa[po++]) {
                return false;
            }
        }
        return true;
    }
	
	/**
	 * �ַ����ľֲ��Ƚ�
	 * 
	 * @param ignoreCase �Ƿ���Դ�Сд
	 * @param toffset ��ǰString��ƫ����
	 * @param other ��һ��String
	 * @param ooffset ��һ��String��ƫ����
	 * @param len �Ƚϵ��ַ�����
	 * @return
	 */
	public boolean regionMatches(boolean ignoreCase, int toffset,
			StringMe other, int ooffset, int len) {
		char ta[] = value;
		int to = toffset;
		char pa[] = other.value;
		int po = ooffset;
		if ((ooffset < 0) || (toffset < 0)
                || (toffset > (long)value.length - len)
                || (ooffset > (long)other.value.length - len)) {
            return false;
        }
		while (len-- > 0) {
			char c1 = ta[to++];
			char c2 = pa[po++];
			if (c1 == c2) {
				continue;
			}
			if (ignoreCase) { //���Դ�Сд
				char u1 = Character.toUpperCase(c1);
				char u2 = Character.toUpperCase(c2);
				if (u1 == u2) {
					continue;
				}
				if (Character.toLowerCase(u1) == Character.toLowerCase(u2)) {
                    continue;
                }
			}
			return false;
		}
		return true;
	}
	
	public boolean startWith(StringMe prefix, int toffset) {
		char ta[] = value;
		int to = toffset;
		char pa[] = prefix.value;
		int po = 0;
		int pc = prefix.value.length;
		if ((toffset < 0) || (toffset > value.length - pc)) {
            return false;
        }
		while (--pc >= 0) {
			if (ta[to++] != pa[po++]) {
				return false;
			}
		}
		return true;
	}
	
	public boolean startWith(StringMe prefix) {
		return startWith(prefix, 0);
	}
	
	public boolean endWith(StringMe suffix) {
		return startWith(suffix, value.length - suffix.value.length);
	}
	
	/**
	 * ��������
	 * ��д��equals�������ر�����������У����븲дhashcode����
	 * 1.Ĭ�ϵ�equals��==�ǵ�Ч�ģ���дequals���ٵ�Ч
	 * 2.���ѿ���equals��ʵ�֣����String�಻��дhashcode���������ַ����������
	 * 3.�Լ���д�����ڲ��漰hash�ṹʱ�����Բ���д�÷���
	 * 4.����java�淶Լ��
	 */
	@Override
	public int hashCode() {
		int h = hash;
		if (h == 0 && value.length > 0) { //�״ε��ã�����hashcode
			char val[] = value;
			
			for (int i = 0; i < value.length; i++) {
                h = 31 * h + val[i];
            }
			hash = h;
		}
		return h;
	}
	
	@Override
	public CharSequence subSequence(int start, int end) {
		// TODO Auto-generated method stub
		return null;
	}

	
	
}
