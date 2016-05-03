package org.roderick.source.lang;

import java.io.ObjectStreamField;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.Arrays;

/**
 * final类，不可继承
 */
public final class StringMe implements Serializable, Comparable<String>, CharSequence {
	private static final long serialVersionUID = -2141535242724633987L;
	/**
	 * final声明，只可赋值一次，且会在JVM的编译期放入常量池，value引用直接指向该地址
	 */
	private final char value[];
	private int hash;
	/**
	 * 获取序列化中的Fields
	 */
	private static final ObjectStreamField[] serialPersistentFields = 
			new ObjectStreamField[0];
	
	public StringMe() {
		this.value = new char[0]; //此时hash还是默认值0
	}
	
	/**
	 * 复制一份original，值和hash值都一样，如无必要不建议使用该构造函数，字符串是常量，值相同可以共用
	 */
	public StringMe(StringMe original) {
		this.value = original.value; //value是私有的，但是相同的类可见？
		this.hash = original.hash;
	}
	
	/**
	 * 根据传入的char数组生成String对象，因为是copy过来的，所以各自修改不受影响
	 */
	public StringMe(char value[]) {
		this.value = Arrays.copyOf(value, value.length);
	}
	
	public StringMe(char value[], int offset, int count) {
		if (offset < 0)
			throw new StringIndexOutOfBoundsException(offset);
		if (count < 0)
			throw new StringIndexOutOfBoundsException(count);
		if (offset > value.length - count) //总长度不够你偏移
			throw new StringIndexOutOfBoundsException(offset + count);
		
		this.value = Arrays.copyOfRange(value, offset, count);
	}
	
	/**
	 * 根据Unicode字符数组(codePoints)生成String对象，可截取其中部分值
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
			if (Character.isBmpCodePoint(c)) //是否为BMP，对字符集有研究的可以很容易理解
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
			/*else //这个方法是protected权限，我无法访问到，而且是final类无法继承，因此注释
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
	 * 根据字符集(utf-8、ISO-8859-1等)解码byte数组，此处charsetName不能为空
	 */
	public StringMe(byte bytes[], int offset, int length, String charsetName) {
		if (charsetName == null)
			throw new NullPointerException("charsetName");
		checkBounds(bytes, offset, length);
		//StringCoding是protected类，非java.lang包不可见
		//this.value = StringCoding.decode(charsetName, bytes, offset, length);
		this.value = new char[0];
	}
	
	public StringMe(byte bytes[], int offset, int length, Charset charset) {
		if (charset == null)
			throw new NullPointerException("charset");
		checkBounds(bytes, offset, length);
		// StringCoding类，非java.lang包不可见
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
		synchronized (buffer) { //buffer.getValue()不可见
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

	//返回字符串指定位置的Unicode编码，如'A'返回65
	/*public int codePointAt(int index) {
        if ((index < 0) || (index >= value.length)) {
            throw new StringIndexOutOfBoundsException(index);
        }
        return Character.codePointAtImpl(value, index, value.length);
    }*/
	
	//就是codePointAt的上一个位置
	/*public int codePointBefore(int index) {
        int i = index - 1;
        if ((i < 0) || (i >= value.length)) {
            throw new StringIndexOutOfBoundsException(index);
        }
        return Character.codePointBeforeImpl(value, index, 0);
    }*/
	
	//返回这个范围之间的Unicode编码
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
	 * 将内容复制到dst字符数组中
	 * @param  dstBegin  dst中的起始偏移量
	 */
	void getChars(char dst[], int dstBegin) {
		System.arraycopy(value, 0, dst, dstBegin, value.length);
	}
	
	/**
	 * 将字符串中的字符复制到指定的char数组中，注意此处复制的字符个数是srcEnd - srcBegin，
	 * 如传入1,3相当于取字符串[1,3)位置的字符
	 * @param srcBegin 源字符串起始位置
	 * @param srcEnd 源字符串结束位置
	 * @param dst 目标char数组
	 * @param dstBegin 目标char数组起始位置，从该索引位置开始替换
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
	
	//按指定的字符集将value值转为byte数组
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
	 * 1.比较是否指向相同的对象，也是Object默认的equals比较方式<br>
	 * 2.判断是否为当前类的实例对象<br>
	 * 3.比较长度和每个元素是否相等<br>
	 */
	@Override
	public boolean equals(Object anObject) {
		if (this == anObject) {
            return true;
        }
		
		if (anObject instanceof StringMe) {
			StringMe anotherString = (StringMe) anObject;
			int n = value.length;
			if (n == anotherString.value.length) { //先比长度，再逐个比字符
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
	 * String与StringBuffer内容比较，此方法是同步的
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
        return len1 - len2; //元素一样比长度
	}*/
	
	/**
	 * 局部比较不忽略大小写
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
	 * 字符串的局部比较
	 * 
	 * @param ignoreCase 是否忽略大小写
	 * @param toffset 当前String的偏移量
	 * @param other 另一个String
	 * @param ooffset 另一个String的偏移量
	 * @param len 比较的字符个数
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
			if (ignoreCase) { //忽略大小写
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
	 * 覆写了equals方法，建议覆写hashcode方法<br>
	 * 1.==比较的是对象内存地址（包括指向常量池的地址），即两个引用指向的是同一个对象（常量），Obecj的equals方法就是==<br>
	 * 2.String的equals方法会先比较引用地址，再逐个比较每个字符的值是否相等，因此字符串的内容一致时，equals为true<br>
	 * 3.Object的hashcode方法返回值是由该对象内存地址转换成整数而来的。
	 * 因此这时候hashcode的值是由指向的地址决定的，导致equals为true时，两个对象的hashcode不会相等。<br>
	 * 4.java se规范约定，如果重写equals方法，那也要重写hashCode方法，使equals为真的时，hashCode的值也相同。
	 * 即重写后，hashcode值由对象内容决定。<br>
	 * 5.但规范是非强制的，当你不需要hashcode或者不涉及hash结构时（如hashmap等），可以不覆写该方法。
	 */
	@Override
	public int hashCode() {
		int h = hash;
		if (h == 0 && value.length > 0) { //首次调用，生成hashcode，如果是空字符串那也就是默认值了
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
	 * 找出该字符在字符串中的位置
	 * @param ch
	 * @param fromIndex 偏移量
	 * @return
	 */
	private int indexOf(int ch, int fromIndex) {
		final int max = value.length;
		if (fromIndex < 0) { //小于0，置为0
			fromIndex = 0;
		} else if (fromIndex >= max) { //大于字符串长度时，返回-1，不会报越界异常
			return -1;
		}
		
		if (ch < Character.MIN_SUPPLEMENTARY_CODE_POINT) { //不是增补字符，涉及增补字符集相关知识
			// handle most cases here (ch is a BMP code point or a
            // negative value (invalid code point)) 大多出情况都在该方法中处理
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
	 * 处理如utf-16之类的编码
	 */
	private int indexOfSupplementary(int ch, int fromIndex) {
		if (Character.isValidCodePoint(ch)) {
            final char[] value = this.value;
            final char hi = Character.highSurrogate(ch);
            final char lo = Character.lowSurrogate(ch);
            final int max = value.length - 1;
            for (int i = fromIndex; i < max; i++) {
                if (value[i] == hi && value[i + 1] == lo) { //一个人占两个位置？
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
			final char[] value = this.value; //都设为final数组，在编译期时，共用一个常量池引用
			int i = Math.min(fromIndex, value.length - 1); //fromIndex小于0时返回-1，大于value的最大索引值时，以value的最大值匹配
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
	 * 连续的字符串匹配，匹配失败返回-1
	 */
	static int indexOf(char[] source, int sourceOffset, int sourceCount,
			char[] target, int targetOffset, int targetCount, int fromIndex) {
		if (fromIndex >= sourceCount) //偏移量大于原字符串长度，如果匹配的字符串是空串，则返回原字符串长度值
			return (targetCount == 0 ? sourceCount : -1);
		if (fromIndex < 0)
			fromIndex = 0;
		if (targetCount == 0) //匹配串是空串则返回偏移量
			return fromIndex;
		
		char first = target[targetOffset];
		int max = sourceOffset + (sourceCount - targetCount); //计算循环次数，匹配串大于源串则返-1
		for (int i = sourceOffset + fromIndex; i <= max; i++) {
			if (source[i] != first)
				while (++i <= max && source[i] != first); //先找到第一个匹配的字符
			
			if (i <= max) { //找到了第一个，匹配剩余字符
				int j = i + 1;
				int end = j + targetCount - 1; //匹配的最大长度
				for (int k = targetOffset + 1; j < end && source[j]
						== target[k]; j++, k++); //匹配一个就+1，全部匹配时，j才会等于end
				
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
		if (fromIndex > rightIndex) // 防止匹配越界
			fromIndex = rightIndex;
		if (targetCount == 0) // 空串返回偏移位置
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
