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
			/*else //���������protectedȨ�ޣ����޷����ʵ���������final���޷��̳У����ע��
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
		//StringCoding��protected�࣬��java.lang�����ɼ�
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
	
	/**
	 * ���ַ����е��ַ����Ƶ�ָ����char�����У�ע��˴����Ƶ��ַ�������srcEnd - srcBegin��
	 * �紫��1,3�൱��ȡ�ַ���[1,3)λ�õ��ַ�
	 * @param srcBegin Դ�ַ�����ʼλ��
	 * @param srcEnd Դ�ַ�������λ��
	 * @param dst Ŀ��char����
	 * @param dstBegin Ŀ��char������ʼλ�ã��Ӹ�����λ�ÿ�ʼ�滻
	 */
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
	
	/**
	 * 1.�Ƚ��Ƿ�ָ����ͬ�Ķ���Ҳ��ObjectĬ�ϵ�equals�ȽϷ�ʽ<br>
	 * 2.�ж��Ƿ�Ϊ��ǰ���ʵ������<br>
	 * 3.�Ƚϳ��Ⱥ�ÿ��Ԫ���Ƿ����<br>
	 */
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
					if (v1[i] != v2[i])
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
	 * ��д��equals���������鸲дhashcode����<br>
	 * 1.==�Ƚϵ��Ƕ����ڴ��ַ������ָ�����صĵ�ַ��������������ָ�����ͬһ�����󣨳�������Obecj��equals��������==<br>
	 * 2.String��equals�������ȱȽ����õ�ַ��������Ƚ�ÿ���ַ���ֵ�Ƿ���ȣ�����ַ���������һ��ʱ��equalsΪtrue<br>
	 * 3.Object��hashcode��������ֵ���ɸö����ڴ��ַת�������������ġ�
	 * �����ʱ��hashcode��ֵ����ָ��ĵ�ַ�����ģ�����equalsΪtrueʱ�����������hashcode������ȡ�<br>
	 * 4.java se�淶Լ���������дequals��������ҲҪ��дhashCode������ʹequalsΪ���ʱ��hashCode��ֵҲ��ͬ��
	 * ����д��hashcodeֵ�ɶ������ݾ�����<br>
	 * 5.���淶�Ƿ�ǿ�Ƶģ����㲻��Ҫhashcode���߲��漰hash�ṹʱ����hashmap�ȣ������Բ���д�÷�����
	 */
	@Override
	public int hashCode() {
		int h = hash;
		if (h == 0 && value.length > 0) { //�״ε��ã�����hashcode������ǿ��ַ�����Ҳ����Ĭ��ֵ��
			char val[] = value;
			
			for (int i = 0; i < value.length; i++) {
                h = 31 * h + val[i];
            }
			hash = h;
		}
		return hash;
	}
	
	public int indexOf(int ch) {
		return indexOf(ch, 0);
	}
	
	/**
	 * �ҳ����ַ����ַ����е�λ��
	 * @param ch
	 * @param fromIndex ƫ����
	 * @return
	 */
	private int indexOf(int ch, int fromIndex) {
		final int max = value.length;
		if (fromIndex < 0) { //С��0����Ϊ0
			fromIndex = 0;
		} else if (fromIndex >= max) { //�����ַ�������ʱ������-1�����ᱨԽ���쳣
			return -1;
		}
		
		if (ch < Character.MIN_SUPPLEMENTARY_CODE_POINT) { //���������ַ����漰�����ַ������֪ʶ
			// handle most cases here (ch is a BMP code point or a
            // negative value (invalid code point)) ����������ڸ÷����д���
			final char[] value = this.value;
			for (int i = fromIndex; i < max; i++) {
				if (value[i] == ch)
					return i;
			}
			return -1;
		} else {
			return indexOfSupplementary(ch, fromIndex);
		}
	}

	/**
	 * ������utf-16֮��ı���
	 */
	private int indexOfSupplementary(int ch, int fromIndex) {
		if (Character.isValidCodePoint(ch)) {
            final char[] value = this.value;
            final char hi = Character.highSurrogate(ch);
            final char lo = Character.lowSurrogate(ch);
            final int max = value.length - 1;
            for (int i = fromIndex; i < max; i++) {
                if (value[i] == hi && value[i + 1] == lo) { //һ����ռ����λ�ã�
                    return i;
                }
            }
        }
        return -1;
	}
	
	public int lastIndexOf(int ch) {
        return lastIndexOf(ch, value.length - 1);
    }

	private int lastIndexOf(int ch, int fromIndex) {
		if (ch < Character.MIN_SUPPLEMENTARY_CODE_POINT) {
			final char[] value = this.value; //����Ϊfinal���飬�ڱ�����ʱ������һ������������
			int i = Math.min(fromIndex, value.length - 1); //fromIndexС��0ʱ����-1������value���������ֵʱ����value�����ֵƥ��
			for (; i >= 0; i--) {
				if (value[i] == ch) {
					return i;
				}
			}
			return -1;
		} else {
			return lastIndexOfSupplementary(ch, fromIndex);
		}
	}

	private int lastIndexOfSupplementary(int ch, int fromIndex) {
		if (Character.isValidCodePoint(ch)) {
			final char[] value = this.value;
			char hi = Character.highSurrogate(ch);
			char lo = Character.lowSurrogate(ch);
			int i = Math.min(fromIndex, value.length - 1);
			for (; i >= 0; i--) {
				if (value[i] == hi && value[i + 1] == lo)
					return i;
			}
		}
		return -1;
	}
	
	public int indexOf(StringMe str) {
		return indexOf(str, 0);
	}

	private int indexOf(StringMe str, int fromIndex) {
		return indexOf(value, 0, value.length, str.value, 0, str.value.length, fromIndex);
	}
	
	static int indexOf(char[] source, int sourceOffset, int sourceCount,
			StringMe target, int fromIndex) {
		return indexOf(source, sourceOffset, sourceCount,
				target.value, 0, target.value.length, fromIndex);
	}
	
	/**
	 * �������ַ���ƥ�䣬ƥ��ʧ�ܷ���-1
	 */
	static int indexOf(char[] source, int sourceOffset, int sourceCount,
			char[] target, int targetOffset, int targetCount, int fromIndex) {
		if (fromIndex >= sourceCount) //ƫ��������ԭ�ַ������ȣ����ƥ����ַ����ǿմ����򷵻�ԭ�ַ�������ֵ
			return (targetCount == 0 ? sourceCount : -1);
		if (fromIndex < 0)
			fromIndex = 0;
		if (targetCount == 0) //ƥ�䴮�ǿմ��򷵻�ƫ����
			return fromIndex;
		
		char first = target[targetOffset];
		int max = sourceOffset + (sourceCount - targetCount); //����ѭ��������ƥ�䴮����Դ����-1
		for (int i = sourceOffset + fromIndex; i <= max; i++) {
			if (source[i] != first)
				while (++i <= max && source[i] != first); //���ҵ���һ��ƥ����ַ�
			
			if (i <= max) { //�ҵ��˵�һ����ƥ��ʣ���ַ�
				int j = i + 1;
				int end = j + targetCount - 1; //ƥ�����󳤶�
				for (int k = targetOffset + 1; j < end && source[j]
						== target[k]; j++, k++); //ƥ��һ����+1��ȫ��ƥ��ʱ��j�Ż����end
				
				if (j == end)
					return i - sourceOffset;
			}
		}
		return -1;
	}
	
	public int lastIndexOf(StringMe str) {
		return lastIndexOf(str, value.length);
	}
	
	public int lastIndexOf(StringMe str, int fromIndex) {
		return lastIndexOf(value, 0, value.length, str.value, 0, str.value.length, fromIndex);
	}
	
	/**
     * Code shared by String and AbstractStringBuilder to do searches. 
     */
	static int lastIndexOf(char[] source, int sourceOffset, int sourceCount,
			StringMe target, int fromIndex) {
        return lastIndexOf(source, sourceOffset, sourceCount,
                       target.value, 0, target.value.length,
                       fromIndex);
    }

	static int lastIndexOf(char[] source, int sourceOffset, int sourceCount, char[] target, int targetOffset,
			int targetCount, int fromIndex) {
		int rightIndex = sourceCount - targetCount;
		if (fromIndex < 0)
			return -1;
		if (fromIndex > rightIndex) // ��ֹƥ��Խ��
			fromIndex = rightIndex;
		if (targetCount == 0) // �մ�����ƫ��λ��
			return fromIndex;

		int strLastIndex = targetOffset + targetCount - 1;
		char strLastChar = target[strLastIndex];
		int min = sourceOffset + targetCount - 1;
		int i = min + fromIndex;

		startSearchForLastChar: while (true) {
			while (i >= min && source[i] != strLastChar) {
				i--;
			}
			if (i < min) {
				return -1;
			}
			int j = i - 1;
			int start = j - (targetCount - 1);
			int k = strLastIndex - 1;

			while (j > start) {
				if (source[j--] != target[k--]) {
					i--;
					continue startSearchForLastChar;
				}
			}
			return start - sourceOffset + 1;
		}
	}

	@Override
	public CharSequence subSequence(int start, int end) {
		// TODO Auto-generated method stub
		return null;
	}

	
	
}
