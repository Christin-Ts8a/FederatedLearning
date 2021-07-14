package com.bcp.general.util;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;


public class ProcessingText {
	// Step 1:String->String[]
	// Step 2:String[]->byte[][]->BigInteger[]
	// Step 3:BigInteger[]->byte[][]->String[]
	// Step 4:String[]->String

	/*
	 * Step 1 Step 1 has two methods named random and aptotic ,they are writed
	 * to divide the String to String[],these two ways are both parallel
	 * recursive methods
	 * 
	 * Method list: public static String[] aptoticDivide(String m, String
	 * charset, int padding, int kappa); public static String[]
	 * randomDivide(String m, String charset, int padding, int kappa); private
	 * static String[] commonDivide(String m, String charset, int padding, int
	 * kappa, int each); public static String[] randomDivideText(String m, int
	 * threshold, String charset, int padding, int kappa); public static
	 * String[] aptoticDivideText(String m, int threshold, String charset, int
	 * padding, int kappa);
	 * 
	 * Helper method list: public static int[] arrayMount(int len, int maxlen);
	 * public static <T> T[] combineArrays(T[] first, T[] second); public static
	 * int fuzzyCountEach(String m, String charset, int padding); public static
	 * int countEach(String m, String charset, int padding); public static int
	 * fuzzyCountMaxLen(String m, String charset, int padding, int kappa);
	 * public static int countMaxLen(String m, String charset, int padding, int
	 * kappa); public static int maxLen(int each, int kappa);
	 * 
	 */

	/*
	 * Step 2 In the stage 1 of step 2,you needs to determine a character set
	 * ,or use the method getEncoding(Str:String)to get the charset.
	 * 
	 * Some Chinese characters or special characters may be transferred to
	 * negative values in the byte array, and if the first value of the
	 * array(byte[0])is a negative value,that array in stage 2 will be converted
	 * to a negative BigInteger.Some asymmetric cryptographic algorithms'
	 * plaintext don't allow the negative values(Their space of plaintext are
	 * like Zn), so if we want to encrypt the input String,we have to de some
	 * special treatments on it. My method is to add a "z" at the top of the
	 * String which can be converted to a byte[] that first value is
	 * negative.This way can ensure that the transferred BigInteger has a
	 * positive value. That method are BCPRandomConvertText() and
	 * BCPAptoticConvertText(),which can finsh step 2.
	 * 
	 * There's nothing to pay attention to the stage 2 of step 2,beacuse there
	 * is only one constructor we can use
	 * 
	 * Method list: public static BigInteger encodingText(String m, String
	 * charset); public static BigInteger[] convert(String[] m, String charset);
	 * public static BigInteger[] preConvert(String[] m, String charset, String
	 * prefix)
	 * 
	 * 
	 */

	/*
	 * Methods that finish 1 and 2: public static BigInteger[]
	 * randomConvertText(String m, int threshold, String charset, int padding,
	 * int kappa); public static BigInteger[] aptoticConvertText(String m, int
	 * threshold, String charset, int padding, int kappa); public static
	 * BigInteger[] preAptoticConvertText(String m, int threshold, String
	 * charset, int padding, int kappa,String prefix); public static
	 * BigInteger[] preRandomConvertText(String m, int threshold, String
	 * charset, int padding, int kappa,String prefix);
	 * 
	 * 
	 */

	/*
	 * Step 3 In the stage 2,we should specify an encoding method
	 * 
	 * Method list: public static String decodingText(BigInteger M, String
	 * charset); public static String[] convert(BigInteger[] m, String charset);
	 * public static String[] preConvert(BigInteger m[], String charset, String
	 * prefix)
	 * 
	 */
	/*
	 * Step 4
	 * 
	 * Method list: public static String mergeStrings(String[] m);
	 * 
	 */
	/*
	 * Methods that finish 3 and 4: public static String
	 * convertMergeText(BigInteger[] m, String charset); public static String
	 * preConvertMergeText(BigInteger[] m, String charset, String prefix);
	 */

	public static final int DEFAULTEACH = 40;
	// each only depends on the input String
	// maxlen is equals to kappa/each
	// 默认情况下一个字符编码后会让BigInteger增加的bit位数
	public static final int DEFAULTEACHPADDING = 10;
	// eachpadding
	public static final int DEFAULTTHRESHOLD = 1000;
	// the threshold to start recursive

	private static class RandomDivideTask extends RecursiveTask<String[]> {

		private static final long serialVersionUID = 1L;
		
		private String m;
		private int threshold;
		private String charset;
		private int padding;
		private int kappa;

		public RandomDivideTask(String m, int threshold, String charset, int padding, int kappa) {
			// TODO Auto-generated constructor stub
			this.m = m;
			this.threshold = threshold;
			this.charset = charset;
			this.padding = padding;
			this.kappa = kappa;
		}

		@Override
		protected String[] compute() {
			// TODO Auto-generated method stub
			if (this.m.length() < this.threshold) {
				return ProcessingText.randomDivide(this.m, this.charset, this.padding, this.kappa);
			} else {
				int len = new Random().nextInt(this.m.length());
				String subleft = this.m.substring(0, len);
				String subright = this.m.substring(len);
				RandomDivideTask taskleft = new RandomDivideTask(subleft, this.threshold, this.charset, this.padding,
						this.kappa);
				RandomDivideTask taskright = new RandomDivideTask(subright, this.threshold, this.charset, this.padding,
						this.kappa);
				ForkJoinTask<String[]> left = taskleft.fork();// return this
				ForkJoinTask<String[]> right = taskright.fork();

				for (int i = 0; i < taskleft.join().length; i++) {
					System.out.println("taskleft=" + taskleft.join()[i]);
				}
				// 查看源代码也是fork()返回的就是this对象本身，这样就可以用数组保存引用变量循环调用fork
				for (int i = 0; i < left.join().length; i++) {
					System.out.println("left=" + left.join()[i]);
				}

				return ProcessingText.<String>combineArrays(left.join(), right.join());
			}
		}
	}

	private static class AptoticDivideTask extends RecursiveTask<String[]> {

		private static final long serialVersionUID = 1L;
		
		private String m;
		private int threshold;
		private String charset;
		private int padding;
		private int kappa;

		public AptoticDivideTask(String m, int threshold, String charset, int padding, int kappa) {
			// TODO Auto-generated constructor stub
			this.m = m;
			this.threshold = threshold;
			this.charset = charset;
			this.padding = padding;
			this.kappa = kappa;
		}

		@Override
		protected String[] compute() {
			// TODO Auto-generated method stub
			if (this.m.length() < this.threshold) {
				return ProcessingText.aptoticDivide(this.m, this.charset, this.padding, this.kappa);
			} else {
				int len = new Random().nextInt(this.m.length());
				String subleft = this.m.substring(0, len);
				String subright = this.m.substring(len);
				AptoticDivideTask taskleft = new AptoticDivideTask(subleft, this.threshold, this.charset, this.padding,
						this.kappa);
				AptoticDivideTask taskright = new AptoticDivideTask(subright, this.threshold, this.charset,
						this.padding, this.kappa);
				taskleft.fork();
				taskright.fork();
				return ProcessingText.<String>combineArrays(taskleft.join(), taskright.join());
			}
		}
	}

	public static int[] arrayMount(int len, int maxlen) {
		int counts = len / maxlen;
		int remainder = len % maxlen;
		int[] result = { counts, remainder };
		return result;
	}

	public static <T> T[] combineArrays(T[] first, T[] second) {
		T[] result = Arrays.copyOf(first, first.length + second.length);
		System.arraycopy(second, 0, result, first.length, second.length);
		return result;
	}

	/*
	 * public static <T> T[] combineArrays(T[] a1, T[] a2) { T[] A = (T[]) new
	 * String[a1.length + a2.length];//无论Object数组和String数组，合并都会会炸 for (int i =
	 * 0; i < A.length; i++) { if (i < a1.length) { A[i] = a1[i]; } else { A[i]
	 * = a2[i - a1.length]; } } return A; }
	 */

	public static String getEncoding(String str) throws UnsupportedEncodingException {
		String encode[] = new String[] { "UTF-8", "UTF-32LE", "UTF-16LE", "GB18030", "Big5", "UTF-16BE", "ISO-8859-1" };
		for (int i = 0; i < encode.length; i++) {
			if (str.equals(new String(str.getBytes(encode[i]), encode[i]))) {
				return encode[i];
			}
		}
		throw new UnsupportedEncodingException("Unknown Encoding");
	}

	// 若编码方式仅使用UTF-16，不指定BELE，则视为UTF-16BE

	public static BigInteger encodingText(String m, String charset) throws UnsupportedEncodingException {
		// String--(指定编码方案)byte[]，byte[]--BigInteger
		// 结论是编码成BigInteger时一定要使用小端编码
		return new BigInteger(m.getBytes(charset));
	}

	public static BigInteger encodingText(String m) throws UnsupportedEncodingException {
		return new BigInteger(m.getBytes(getEncoding(m)));
	}

	// BigInteger--byte[]，byte[]--(指定编码方案)String
	// getBytes()是可以指定String转化为大端排列还是小端排列的byte数组(大端用BE，小端用LE即可)
	// 但toByteArray()方法无论何时都将BigInteger转化为小端字节序排列的byte[],可能是x86CPU的缘故,可惜JDK不提供大端排序的方法
	// 因此为了兼容只转化小端数组的toByteArray()，getBytes时一定要选择小端编码的方案，如UTF-8,UTF-16LE,UTF-32LE编码
	// 即使UTF-32BE,UTF-16BE等大端编码后，再使用其对应的LE作为参数解码构造String也会有乱码,不过乱码数量会比使用原生编码产生的数量少
	public static String decodingText(BigInteger M, String charset) throws UnsupportedEncodingException {
		return new String(M.toByteArray(), charset);
	}

	// padding is aim to increase the final each to avoid overflowing the
	// plaintext space
	public static int fuzzyCountEach(String m, String charset, int padding) throws UnsupportedEncodingException {
		// 假定m的长度在一个int可以表示的范围内
		int len = new Random().nextInt(m.length() - 1) + 1;// 生成子字符串的长度,最少是1
		String substr = m.substring(0, len);// 截取子字符
		BigInteger b = encodingText(substr, charset);
		int bitlen = b.bitLength();// BigInteger的长度
		return (bitlen / len) + padding;// Big长度/子长度为一个字符对应的Big长度
	}

	public static int fuzzyCountEach(String m) throws UnsupportedEncodingException {
		return fuzzyCountEach(m, getEncoding(m), ProcessingText.DEFAULTEACHPADDING);
	}

	public static int countEach(String m, String charset, int padding) throws UnsupportedEncodingException {
		BigInteger b = encodingText(m, charset);
		int bitlen = b.bitLength();
		return bitlen / m.length() + padding;
	}

	public static int countEach(String m) throws UnsupportedEncodingException {
		return countEach(m, getEncoding(m), ProcessingText.DEFAULTEACHPADDING);
	}

	public static int fuzzyCountMaxLen(String m, String charset, int padding, int kappa)
			throws UnsupportedEncodingException {
		return kappa / fuzzyCountEach(m, charset, padding);
	}

	public static int countMaxLen(String m, String charset, int padding, int kappa)
			throws UnsupportedEncodingException {
		return kappa / countEach(m, charset, padding);
	}

	public static int maxLen(int each, int kappa) {
		return kappa / each;
	}

	public static String[] aptoticDivide(String m, String charset, int padding, int kappa) {
		int each = ProcessingText.DEFAULTEACH;
		try {
			each = ProcessingText.countEach(m, charset, padding);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return commonDivide(m, kappa, each);
	}

	public static String[] randomDivide(String m, String charset, int padding, int kappa) {
		int fuzzyeach = ProcessingText.DEFAULTEACH;
		try {
			fuzzyeach = ProcessingText.fuzzyCountEach(m, charset, padding);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return commonDivide(m, kappa, fuzzyeach);
	}

	public static String[] commonDivide(String m, int kappa, int each) {
		final int maxlen = ProcessingText.maxLen(each, kappa);
		final ArrayList<String> list = new ArrayList<String>();
		/*
		 * int total = 0; while (m.length() > total) { int split = new
		 * Random().nextInt(maxlen - 1) + 1; if (split > m.length() - total) {
		 * list.add(m.substring(total)); total += (m.length() - total); } else {
		 * list.add(m.substring(total, total += split)); } } for (int i = 0; i <
		 * list.size(); i++) { System.out.println("list[" + i + "]=" +
		 * list.get(i)); }
		 */

		while (m.length() > maxlen) {
			int split = new Random().nextInt(maxlen - 1) + 1;
			list.add(m.substring(0, split));
			m = m.substring(split);
		}
		list.add(m);

		String[] result = new String[list.size()];

		for (int i = 0; i < list.size(); i++) {
			result[i] = list.get(i);
		}
		return result;
	}

	/*
	 * private static String[] commonDivide(String m, String charset, int
	 * padding, int kappa, int each) {
	 * 
	 * final int maxlen = ProcessingText.maxLen(each, kappa); //
	 * 测试在指定kappa，明文空间下一个大整数最多容许由多长的一个字符串构造，也就是返回的数组中每个字符串元素的最大长度，
	 * 每个元素都要构造一个大整数来进行加密
	 * 
	 * final int[] mount = ProcessingText.arrayMount(m.length(), maxlen);
	 * 
	 * final int counts = mount[0]; //
	 * 通过要分割的字符串长度除以每个字符串元素的最大长度得出最大分组数量，若reminder不为0，则最大分组数+1 final int
	 * remainder = mount[1]; // 若该值不为0，则这是最后一个分组的元素个数 if (remainder == 0) { //
	 * String长度正好整除each，除出来的结果maxlen就是分组数量 String[] result = new String[counts];
	 * for (int i = 0; i < result.length; i++) { result[i] = m.substring(i *
	 * maxlen, (i + 1) * maxlen); } return result; } else { String[] result =
	 * new String[counts + 1]; int i = 0; for (i = 0; i < result.length - 1;
	 * i++) { result[i] = m.substring(i * maxlen, (i + 1) * maxlen);//
	 * fuzzymaxlen可能会导致索引超过原字符串长度的异常发生 } result[i] = m.substring(i * maxlen);
	 * return result; } }
	 */

	public static String[] randomDivideText(String m, int threshold, String charset, int padding, int kappa) {
		ForkJoinPool pool = new ForkJoinPool();
		return pool.invoke(new RandomDivideTask(m, threshold, charset, padding, kappa));
	}

	public static String[] aptoticDivideText(String m, int threshold, String charset, int padding, int kappa) {
		ForkJoinPool pool = new ForkJoinPool();
		return pool.invoke(new AptoticDivideTask(m, threshold, charset, padding, kappa));
	}

	public static BigInteger[] randomDivideConvertText(String m, int threshold, String charset, int padding, int kappa)
			throws UnsupportedEncodingException {
		return ProcessingText.convert(randomDivideText(m, threshold, charset, padding, kappa), charset);
	}

	// 对于一个字符串m，先调用aptoticDivideText()将其分割成String[]，然后调用convert将String[]转换成BigInteger[]
	// 但是有些String在转换的时候会转换为负数BigInteger，我发现这些字符串全都是以汉字或其他字符打头的，它们转换为byte[]之后第一个byte值是负数，所以用该byte[]构造的BigInteger也是负数
	// 如果在这些字符串的头部加一个字母或数字，它们byte[]中第一个字符就会变成正数，而转换后的大整数也因为这个数变成了一个正数
	// 新开一个方法，在把String[]转换为BigInteger[]之后，先检测每个元素是否都大于0，如果是负数的话把对应位置的String前面加一个z，然后再转换看看是否还是负数，如果不是就通过
	// 经过测试，除了UTF-16无论在字符串首加什么数字字母byte[]前几个元素都是-2，-1，0以外，其他的编码在字符串首加一个非负字符的方法都可以通过，决定采用使用频率最低的z
	// 希望不会因为这一个字符的原因就导致转换后的BigInteger大小超过了明文空间
	public static BigInteger[] aptoticDivideConvertText(String m, int threshold, String charset, int padding, int kappa)
			throws UnsupportedEncodingException {
		return ProcessingText.convert(aptoticDivideText(m, threshold, charset, padding, kappa), charset);
	}

	// finish step1 and step2 in BCP way
	public static BigInteger[] preRandomDivideConvertText(String m, int threshold, String charset, int padding,
			int kappa, String prefix) throws UnsupportedEncodingException {
		return preConvert(randomDivideText(m, threshold, charset, padding, kappa), charset, prefix);
	}

	public static BigInteger[] preAptoticDivideConvertText(String m, int threshold, String charset, int padding,
			int kappa, String prefix) throws UnsupportedEncodingException {
		return preConvert(aptoticDivideText(m, threshold, charset, padding, kappa), charset, prefix);
	}

	public static BigInteger[] preConvert(String[] m, String charset, String prefix)
			throws UnsupportedEncodingException {
		for (int i = 0; i < m.length; i++) {
			m[i] = prefix + m[i];
		}
		return convert(m, charset);
	}

	// convert String[] to BigInteger[]
	public static BigInteger[] convert(String[] m, String charset) throws UnsupportedEncodingException {
		BigInteger[] b = new BigInteger[m.length];
		for (int i = 0; i < m.length; i++) {
			b[i] = ProcessingText.encodingText(m[i], charset);
		}
		return b;
	}

	public static String[] preConvert(BigInteger m[], String charset, String prefix)
			throws UnsupportedEncodingException {
		String[] s = ProcessingText.convert(m, charset);
		for (int i = 0; i < s.length; i++) {
			int indexOf = s[i].indexOf(prefix);
			if (indexOf != -1) {
				s[i] = s[i].substring(indexOf + 1);
			}
		}
		return s;
	}

	// convert BigInteger[] to String[]
	public static String[] convert(BigInteger[] m, String charset) throws UnsupportedEncodingException {
		String[] s = new String[m.length];
		for (int i = 0; i < s.length; i++) {
			s[i] = ProcessingText.decodingText(m[i], charset);
		}
		return s;
	}

	public static String mergeText(String[] m) {
		String text = "";
		for (int i = 0; i < m.length; i++) {
			text += m[i];
		}
		return text;
	}

	public static String preConvertMergeText(BigInteger[] m, String charset, String prefix)
			throws UnsupportedEncodingException {
		return ProcessingText.mergeText(preConvert(m, charset, prefix));
	}

	public static String convertMergeText(BigInteger[] m, String charset) throws UnsupportedEncodingException {
		return ProcessingText.mergeText(ProcessingText.convert(m, charset));
	}

}

// UTF-8向下兼容ASCII码，英文，数字等ASCII码中存在的编码方式完全不变，只占一个字节，对于汉字来说大多数情况是一个字符三个字节，最大编码为8字节
// UTF-16分为BE,LE两种，两者互不兼容，每个字符都编码为两字节或四字节，不兼容ASCII码,ASCII字符在内存中的表示都要重新编码，
// UTF-32每个字符都编码为四字节
// 所有字符串在从byte[]变回String之后都是固定长度

// getChars版本，缺点是麻烦和容易出空指针异常，优点是可以处理的场景更多 char[] chars = null; char[]
// 默认使用toCharArray版本，不容易出错，应用场景单一
// 第一个参数是m起始位置，第二个是m结束位置，第三个目的地，第四个是目的地起始位置
// 若不对C进行初始化，C是null，或者初始化C的大小不如m就会空指针异常
// 若C的大小超过了m，转码之后就会在长度超过过m的部分之后出现乱码
// 记住参数选取的左闭右开原则
// char[] chars=null;
// m.getChars(0, m.length(), chars = new char[m.length()],0);
// 不能使用char[]，数组中存储的仍然是中英文字符而非数字
