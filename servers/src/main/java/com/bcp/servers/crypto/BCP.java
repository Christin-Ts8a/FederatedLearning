package com.bcp.servers.crypto;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import javax.validation.Valid;
import com.bcp.general.crypto.BcpBlindCiphertext;
import com.bcp.general.crypto.BcpCiphertext;
import com.bcp.general.crypto.BcpKeyPair;
import com.bcp.general.crypto.PP;
import com.bcp.general.constant.BCPConstant;
import org.springframework.util.CollectionUtils;

public class BCP implements BCPConstant {

	int kappa;
	int certainty;

	private PP pp;
	private MK mk;

	private LinkedHashMap<String, String> members;

	// --------------get set----------------------------------
	public PP getPP() {
		return pp;
	}

	public MK getMK() {
		return mk;
	}

	@SuppressWarnings("unchecked")
	public LinkedHashMap<String, String> getMembers() {
		return (LinkedHashMap<String, String>) members.clone();
	}

	public int getKappa() {
		return kappa;
	}

	public int getCertainty() {
		return certainty;
	}

	// ----------------------------------------------------------------

	private static class GenPQ implements Runnable {
		BigInteger m;
		int kappa;
		int certainty;

		public GenPQ(int kappa, int certainty) {
			// TODO Auto-generated constructor stub
			this.kappa = kappa;
			this.certainty = certainty;
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			this.m = BCP.genSecMPrime(kappa, certainty);
		}

	}

	// private static class RunnableMDec implements Runnable {
	//
	// private BigInteger N;
	// private BigInteger k;
	// private BigInteger g;
	// private BigInteger h;
	// private BigInteger mp;
	// private BigInteger mq;
	// private BigInteger[] c;
	// private BigInteger m;
	//
	// public RunnableMDec(BigInteger N, BigInteger k, BigInteger g, BigInteger h,
	// BigInteger mp, BigInteger mq,
	// BigInteger[] c) {
	// super();
	// this.N = N;
	// this.k = k;
	// this.g = g;
	// this.h = h;
	// this.mp = mp;
	// this.mq = mq;
	// this.c = c;
	// }
	//
	// @Override
	// public void run() {
	// // TODO Auto-generated method stub
	// this.m = BCP.mDec(this.N, this.k, this.g, this.h, this.mp, this.mq, this.c);
	// }
	//
	// }

	// private static class ParallelEncTask extends RecursiveTask<BigInteger[][]> {
	//
	// private static final long serialVersionUID = 1L;
	//
	// private BigInteger N;
	// private BigInteger g;
	// private BigInteger h;
	// private BigInteger[] m;
	// private BigInteger min;
	// private boolean first;
	//
	// public ParallelEncTask(BigInteger N, BigInteger g, BigInteger h, BigInteger[]
	// m) {
	// // TODO Auto-generated constructor stub
	// this.N = N;
	// this.g = g;
	// this.h = h;
	// this.m = m;
	// this.first = true;
	// }
	//
	// private ParallelEncTask(BigInteger N, BigInteger g, BigInteger h, BigInteger
	// min) {
	// // TODO Auto-generated constructor stub
	// this.N = N;
	// this.g = g;
	// this.h = h;
	// this.min = min;
	// this.first = false;
	// }
	//
	// @SuppressWarnings("unchecked")
	// @Override
	// protected BigInteger[][] compute() {
	// // TODO Auto-generated method stub
	// if (this.first) {
	// ForkJoinTask<BigInteger[][]>[] task = (ForkJoinTask<BigInteger[][]>[]) new
	// ForkJoinTask[m.length];
	// for (int i = 0; i < task.length; i++) {
	// task[i] = new ParallelEncTask(this.N, this.g, this.h, this.m[i]).fork();
	// }
	// BigInteger[][] cipher = new BigInteger[this.m.length][2];
	// for (int i = 0; i < cipher.length; i++) {
	// cipher[i] = task[i].join()[0];
	// }
	// return cipher;
	// } else {
	// BigInteger[][] result = new BigInteger[1][2];
	// BigInteger[] enc = BCP.enc(this.N, this.g, this.h, this.min);
	// result[0] = enc;
	// return result;
	// }
	// }
	// }

	// private static class ParallelDecTask extends RecursiveTask<BigInteger[]> {
	//
	// private static final long serialVersionUID = 1L;
	//
	// private BigInteger N;
	// private BigInteger a;
	// private BigInteger[][] c;
	// private BigInteger[] cin;
	// private boolean first;
	//
	// public ParallelDecTask(BigInteger N, BigInteger a, BigInteger[][] c) {
	// // TODO Auto-generated constructor stub
	// this.N = N;
	// this.a = a;
	// this.c = c;
	// this.first = true;
	// }
	//
	// private ParallelDecTask(BigInteger N, BigInteger a, BigInteger[] cin) {
	// // TODO Auto-generated constructor stub
	// this.N = N;
	// this.a = a;
	// this.cin = cin;
	// this.first = false;
	// }
	//
	// @SuppressWarnings("unchecked")
	// @Override
	// protected BigInteger[] compute() {
	// // TODO Auto-generated method stub
	// if (this.first) {
	// ForkJoinTask<BigInteger[]>[] task = (ForkJoinTask<BigInteger[]>[]) new
	// ForkJoinTask[c.length];
	// for (int i = 0; i < task.length; i++) {
	// task[i] = new ParallelDecTask(this.N, this.a, this.c[i]).fork();
	// }
	// BigInteger[] plain = new BigInteger[this.c.length];
	// for (int i = 0; i < plain.length; i++) {
	// plain[i] = task[i].join()[0];
	// }
	// return plain;
	// } else {
	// BigInteger[] result = new BigInteger[1];
	// BigInteger dec = BCP.dec(this.N, this.a, this.cin);
	// result[0] = dec;
	// return result;
	// }
	// }
	// }

	/*
	 * private BCP(BigInteger mq, BigInteger mp, BigInteger g, BigInteger k, int
	 * certainty) { // 原始构造器 this.certainty = certainty; this.mk = new MK(mq, mp);
	 * BigInteger q = mq.shiftLeft(1).add(BigInteger.ONE); BigInteger p =
	 * mp.shiftLeft(1).add(BigInteger.ONE); this.pp = new PP(q, p, q.multiply(p), k,
	 * g); this.kappa = p.multiply(q).kappa(); }
	 */

	public BCP(int kappa, int certainty, BigInteger N, BigInteger k, BigInteger g, BigInteger mp, BigInteger mq) {
		this.kappa = kappa;
		this.certainty = certainty;
		this.pp = new PP(N, k, g);
		this.mk = new MK(mp, mq);
		genMembers();
	}

	public BCP(int kappa, int certainty) {
		this.kappa = kappa;
		this.certainty = certainty;

		BigInteger[] result = BCP.setUp(kappa, certainty);

		BigInteger mp = result[0], mq = result[1];
		BigInteger N = mp.shiftLeft(1).add(BigInteger.ONE).multiply(mq.shiftLeft(1).add(BigInteger.ONE));
		BigInteger k = result[2], g = result[3];

		this.pp = new PP(N, k, g);
		this.mk = new MK(mp, mq);
		genMembers();
	}

	public BCP(int kappa) {
		// TODO Auto-generated constructor stub
		this(kappa, BCP.DEFAULTCERTAINTY);
	}

	public BCP() {
		// TODO Auto-generated constructor stub
		this(BCP.DEFAULTKAPPA, BCP.DEFAULTCERTAINTY);
	}

	// generate a prime mq that 2*mq+1 is also a prime
	private static BigInteger genSecMPrime(int kappa, int certainty) {
		while (true) {
			BigInteger tempmq = BigInteger.ZERO, tempq = BigInteger.ZERO;
			int tempqlength = new Random().nextInt(kappa / 2) + kappa / 2;// 生成二分之一到一之间的素数，保证N的位数不小于kappa
			tempq = new BigInteger(tempqlength, certainty, new Random());// 无需素性检测，因为检测和生成方法的算法类似
			if (!(tempmq = tempq.subtract(BigInteger.ONE).shiftRight(1)).isProbablePrime(certainty)) {
				// check out if mq is a prime,if not,then continue
				continue;
			}
			return tempmq;
		}
	}

	// generate k and g
	private static BigInteger[] generateKG(BigInteger mp, BigInteger mq, int certainty) {
		final BigInteger p = mp.shiftLeft(1).add(BigInteger.ONE), q = mq.shiftLeft(1).add(BigInteger.ONE);
		final BigInteger N = p.multiply(q), N2 = N.pow(2);
		final BigInteger ppqq = q.multiply(mq).multiply(p).multiply(mp);
		final int N2bitlength = N2.bitLength();

		while (true) {
			int Gbitlength = new Random().nextInt(N2bitlength);
			if (Gbitlength < 2) {
				// 用小于2的长度构造素数BigInteger会异常，素数bitlength至少是2(2和3，长度1时只有0,1两种可能)
				continue;
			}
			BigInteger g = new BigInteger(Gbitlength, certainty, new Random());// Zn2*
			if (N2.compareTo(g) != 1) {
				// 防止N2不如g1大，超出Zn2的群
				continue;
			}
			if (g.bitLength() < N2bitlength / 4)
				// 2018.3.21增加条件，g的长度必须大于等于N2/4，方法也追加了一个kappa参数，为了兼容后来对k的限制
				// g的空间是Zn2，既然如此就不限制g的最大长度，只限制最小长度
				continue;

			if (g.modPow(ppqq, N2).compareTo(BigInteger.ONE) != 0) {
				// 若g1的阶不为ppqq，则重新开始
				continue;
			}
			BigInteger GG = g.modPow(mp.multiply(mq), N2);
			BigInteger k = GG.subtract(BigInteger.ONE).divide(N);
			// if (k.bitLength() < kappa / 4 || k.bitLength() > (kappa * 3 / 4))
			// 2018.3.21是否增加条件，k的长度必须大于等于1/4N的长度
			// 经过测试，不知道为什么每次k的长度都比N小仅仅1位或几位，而限制k的最大长度又会对性能造成严重影响
			// continue;

			if (k.compareTo(BigInteger.ONE) == -1 || k.compareTo(N.subtract(BigInteger.ONE)) == 1) {
				// 可以直接用公式算出来
				continue;
			}

			BigInteger[] kg = { k, g };
			return kg;
		}
	}

	private static BigInteger[] generateMPQ(int kappa, int certainty) {
		// 开两个线程生成mq，mp
		BigInteger mq, mp;
		GenPQ genmq = new GenPQ(kappa, certainty);
		GenPQ genmp = new GenPQ(kappa, certainty);
		ExecutorService executor = Executors.newCachedThreadPool();
		executor.execute(genmq);
		executor.execute(genmp);
		executor.shutdown();
		while (!executor.isTerminated())
			;// 等待结束
		mq = genmq.m;
		mp = genmp.m;
		BigInteger[] mpq = { mp, mq };
		return mpq;
	}

	// start two threads to run genSecMPrime() method and then run generateG()
	private static BigInteger[] setUp(int kappa, int certainty) {
		BigInteger[] mpq = BCP.generateMPQ(kappa, certainty);
		BigInteger[] kg = BCP.generateKG(mpq[0], mpq[1], certainty);
		BigInteger[] result = { mpq[0], mpq[1], kg[0], kg[1] };
		return result;
	}

	/**
	 * generate pk and sk
	 * 
	 * @param N
	 * @param g
	 * @param a
	 * @return [h,a]
	 */
	private static BcpKeyPair keyGen(BigInteger N, BigInteger g, BigInteger a) {
		// 原始KeyGen，所有参数都已知
		BigInteger h = g.modPow(a, N.pow(2));
		return new BcpKeyPair(h, a);
	}

	/**
	 * generate pk and sk
	 * 
	 * @param N
	 * @param g
	 * @return
	 */
	public static BcpKeyPair keyGen(BigInteger N, BigInteger g) {
		// 指定N的比特长度，随机生成a的keyGE
		BigInteger N2 = N.multiply(N);
		BigInteger a = new BigInteger(N2.bitLength(), new Random());
		while (a.compareTo(N2) > 0 || a.compareTo(BigInteger.ZERO) < 0) {
			// 确保a大于0小于N2
			a = new BigInteger(N2.bitLength(), new Random());
		}
		return keyGen(N, g, a);
	}

	/**
	 * 
	 * @param N
	 * @param g
	 * @param h
	 *            公钥
	 * @param m
	 *            明文
	 * @return
	 */
	public static BcpCiphertext enc(BigInteger N, BigInteger g, BigInteger h, BigInteger m) {
		BigInteger N2 = N.pow(2);

		BigInteger r = new BigInteger(N2.bitLength(), new Random());
		while (r.compareTo(N2) != -1 || r.compareTo(BigInteger.ZERO) == -1) {
			r = new BigInteger(N2.bitLength(), new Random());
		}

		BigInteger A = g.modPow(r, N2);

		BigInteger B1 = h.modPow(r, N2);
		BigInteger B2 = m.multiply(N).add(BigInteger.ONE).mod(N2);
		BigInteger B = B1.multiply(B2).mod(N2);

		return new BcpCiphertext(A, B);
	}

	// public static BcpCiphertext enc(PP pp, BigInteger h, BigInteger m) {
	// return enc(pp.getN(), pp.getG(), h, m);
	// }

	/**
	 * 
	 * @param N
	 * @param a
	 *            私钥
	 * @param c
	 *            密文
	 * @return
	 */
	public static BigInteger dec(BigInteger N, BigInteger a, @Valid BcpCiphertext c) {
		if (c == null || c.getA() == null || c.getB() == null) {
			throw new IllegalArgumentException("Ciphertext couldn't be null");
		}
		BigInteger N2 = N.multiply(N), A = c.getA(), B = c.getB();
		BigInteger InverseA = A.modInverse(N2);// 是否是A模N2的逆元论文中并未说明

		BigInteger tempA = InverseA.modPow(a, N2);
		BigInteger tempB = B.mod(N2);
		BigInteger tempC = tempA.multiply(tempB).mod(N2);
		BigInteger tempD = tempC.subtract(BigInteger.ONE.mod(N2)).mod(N2);
		return tempD.divide(N);
	}

	/**
	 * 
	 * @param N
	 * @param k
	 * @param g
	 * @param h
	 *            公钥
	 * @param mp
	 * @param mq
	 * @param c
	 *            密文
	 * @return
	 */
	public static BigInteger mDec(BigInteger N, BigInteger k, BigInteger g, BigInteger h, BigInteger mp, BigInteger mq,
			BcpCiphertext c) {
		if (c == null || c.getA() == null || c.getB() == null) {
			throw new IllegalArgumentException("Ciphertext couldn't be null");
		}
		BigInteger N2 = N.multiply(N), A = c.getA(), B = c.getB();
		BigInteger Inversek = k.modInverse(N);
		BigInteger mN = mq.multiply(mp);

		BigInteger tempa = h.modPow(mN, N2).subtract(BigInteger.ONE.mod(N2)).mod(N2);
		BigInteger amodN = tempa.multiply(Inversek).divide(N).mod(N);

		BigInteger tempr = A.modPow(mN, N2).subtract(BigInteger.ONE.mod(N2)).mod(N2);
		BigInteger rmodN = tempr.multiply(Inversek).divide(N).mod(N);

		BigInteger delta = mN.modInverse(N);
		BigInteger gamma = amodN.multiply(rmodN).mod(N);

		BigInteger tempm = B.modPow(mN, N2).multiply(g.modInverse(N2).modPow(gamma.multiply(mN), N2)).mod(N2)
				.subtract(BigInteger.ONE.mod(N2)).mod(N2);
		return tempm.multiply(delta).divide(N).mod(N);
	}

	// the method to process the divided long plaintext
	// it return a BigInteger[m.length][2]
	// public static BigInteger[][] enc(BigInteger N, BigInteger g, BigInteger h,
	// BigInteger[] m) {
	// return new ForkJoinPool().invoke(new ParallelEncTask(N, g, h, m));
	// }

	// Input String m that length<2.pow(31)-1,and then convert m to String[]
	// and convert String[] to BigInteger[] m
	// with the arguement m,this method will invoke enc(m)
	// public static BigInteger[][] randomEnc(BigInteger N, BigInteger g, BigInteger
	// h, String m, int threshold,
	// String charset, int padding, int kappa, String prefix) throws
	// UnsupportedEncodingException {
	// return BCP.enc(N, g, h,
	// ProcessingText.preRandomDivideConvertText(m, threshold, charset, padding,
	// kappa, prefix));
	// }

	// public static BigInteger[][] aptoticEnc(BigInteger N, BigInteger g,
	// BigInteger h, String m, int threshold,
	// String charset, int padding, int kappa, String prefix) throws
	// UnsupportedEncodingException {
	// return BCP.enc(N, g, h,
	// ProcessingText.preAptoticDivideConvertText(m, threshold, charset, padding,
	// kappa, prefix));
	// }

	// public static BigInteger[] dec(BigInteger N, BigInteger a, BigInteger[][] c)
	// {
	// return new ForkJoinPool().invoke(new ParallelDecTask(N, a, c));
	// }

	// public static String dec(BigInteger N, BigInteger a, BigInteger[][] c, String
	// charset, String prefix)
	// throws UnsupportedEncodingException {
	// BigInteger[] dec = BCP.dec(N, a, c);
	// return ProcessingText.preConvertMergeText(dec, charset, prefix);
	// }

	// public static String mDec(BigInteger N, BigInteger k, BigInteger g,
	// BigInteger h, BigInteger mp, BigInteger mq,
	// BigInteger[][] c, String charset, String prefix) throws
	// UnsupportedEncodingException {
	// return ProcessingText.preConvertMergeText(BCP.mDec(N, k, g, h, mp, mq, c),
	// charset, prefix);
	// }

	// public static BigInteger[] mDec(BigInteger N, BigInteger k, BigInteger g,
	// BigInteger h, BigInteger mp,
	// BigInteger mq, BigInteger[][] c) {
	// ExecutorService pool = Executors.newCachedThreadPool();
	// RunnableMDec[] task = new RunnableMDec[c.length];
	// for (int i = 0; i < task.length; i++) {
	// pool.execute(task[i] = new RunnableMDec(N, k, g, h, mp, mq, c[i]));
	// }
	// pool.shutdown();
	// while (!pool.isTerminated())
	// ;
	// BigInteger[] m = new BigInteger[c.length];
	// for (int i = 0; i < m.length; i++) {
	// m[i] = task[i].m;
	// }
	// return m;
	// }

	public static BigInteger mDec(PP pp, BigInteger h, MK mk, BcpCiphertext c) {
		return mDec(pp.getN(), pp.getK(), pp.getG(), h, mk.getMp(), mk.getMq(), c);
	}

	// public static void main(String[] args) throws Exception {
	// String m = "一二三四五六七八九十九八七六五四三二一1234567890abcdefghijklmnopqrstuvwxyz";
	// String charset = "UTF-16LE";
	// int padding = 10;
	// int threshold = 50;
	// String prefix = "z";
	//
	// Date a1 = new Date();
	// long t1 = a1.getTime();
	//
	// BCP testbcp = new BCP(2048, DEFAULTCERTAINTY);
	//
	// Date a2 = new Date();
	// long t2 = a2.getTime();
	// long ms = t2 - t1;
	// double s = (double) ms / 1000;// 若不进行强制转换，则会丢失一位以上的小数位
	// String[] hms = countHM(s);
	//
	// BigInteger N = testbcp.pp.getN(), k = testbcp.pp.getK(), g =
	// testbcp.pp.getG();
	// BigInteger mp = testbcp.mk.getMp(), mq = testbcp.mk.getMq();
	// int kappa = testbcp.kappa, certainty = testbcp.certainty;
	//
	// BigInteger[] key = testbcp.keyGen(testbcp.pp.getN(), testbcp.pp.getG());//
	// 生成密钥对
	// BigInteger h = key[0], a = key[1];
	//
	// BigInteger[] plaintext = ProcessingText.preAptoticDivideConvertText(m,
	// threshold, charset, padding, kappa,
	// prefix);
	//
	// BigInteger[][] c = BCP.enc(testbcp.pp.getN(), testbcp.pp.getG(), h,
	// plaintext);
	//
	// String dec = BCP.dec(N, a, c, charset, prefix);
	// String mDec = BCP.mDec(N, k, g, h, mp, mq, c, charset, prefix);
	//
	// String filename = "BCP" + kappa + "test.txt";
	// PrintWriter pr = new PrintWriter(filename);
	// pr.println("kappa=" + kappa);
	// pr.println("certainty=" + certainty);
	// pr.println("N=" + N);
	// pr.println("N.length=" + N.bitLength());
	// pr.println("k=" + k);
	// pr.println("k.length=" + k.bitLength());
	// pr.println("g=" + g);
	// pr.println("g.length=" + g.bitLength());
	// pr.println("mp=" + mp);
	// pr.println("mp.length=" + mp.bitLength());
	// pr.println("mq=" + mq);
	// pr.println("mq.length=" + mq.bitLength());
	// pr.println("h=" + h);
	// pr.println("h.length=" + h.bitLength());
	// pr.println("a=" + a);
	// pr.println("a.length=" + a.bitLength());
	// pr.println();
	// for (int i = 0; i < plaintext.length; i++) {
	// pr.println("plaintext[" + i + "]=" + plaintext[i]);
	// }
	// pr.println("Input=" + m);
	// pr.println("dec =" + dec);
	// pr.println("mDec =" + mDec);
	// pr.println("Time consuming:" + hms[0] + "h" + hms[1] + "m" + hms[2] + "s");
	//
	// pr.close();
	// }

	public static String[] countHM(double s) {
		long m = 0, h = 0;
		while (s >= 60.0) {
			m++;
			s -= 60.0;
		}
		while (m >= 60) {
			h++;
			m -= 60;
		}
		String[] time = { String.valueOf(h), String.valueOf(m), String.valueOf(s) };
		return time;
	}

	// -------------------------------------------------------------------------
	private void genMembers() {
		members = new LinkedHashMap<>();
		members.put("kappa", String.valueOf(kappa));
		members.put("certainty", String.valueOf(certainty));
		members.put("N", pp.getN().toString());
		members.put("k", pp.getK().toString());
		members.put("g", pp.getG().toString());
		members.put("mp", mk.getMp().toString());
		members.put("mq", mk.getMq().toString());
		members.put("p", mk.getP().toString());
		members.put("q", mk.getQ().toString());
	}

	/**
	 * 
	 * @param m
	 * @param N
	 * @return m模N的加法逆元
	 */
	public static BigInteger additiveInverse(BigInteger m, BigInteger N) {
		return N.subtract(m);
	}

	/**
	 * 同态相加
	 * 
	 * @param N
	 * @param a
	 *            密文[A,B]
	 * @param b
	 *            密文[A`,B`]
	 * @return a+b之和
	 */
	public static BcpCiphertext add(BigInteger N, BcpCiphertext a, BcpCiphertext b) {
		BigInteger N2 = N.multiply(N);
		BigInteger newA = a.getA().multiply(b.getA()).mod(N2);
		BigInteger newB = a.getB().multiply(b.getB()).mod(N2);
		return new BcpCiphertext(newA, newB);
	}

	/**
	 * 将密文a乘以明文multiple，即密文a循环加法multiple-1次
	 * 
	 * @param N
	 * @param multiple
	 *            必须大于0
	 * @param a
	 *            密文
	 * @return
	 */
	public static BcpCiphertext plaintextMult(BigInteger N, BigInteger multiple, BcpCiphertext a) {
		BcpCiphertext multResult = a;
		for (BigInteger i = BigInteger.ONE; i.compareTo(multiple) < 0; i = i.add(BigInteger.ONE)) {
			multResult = add(N, multResult, a);
		}
		return multResult;
	}

	/**
	 * 同态乘法
	 * 
	 * @param N
	 * @param k
	 * @param g
	 * @param PK
	 * @param mp
	 * @param mq
	 * @param c1
	 * @param c2
	 * @return
	 */
	public static BcpCiphertext mult(BigInteger N, BigInteger k, BigInteger g, BigInteger PK, BigInteger mp,
			BigInteger mq, BcpCiphertext c1, BcpCiphertext c2) {
		BigInteger client1 = BCP.mDec(N, k, g, PK, mp, mq, c1);
		BigInteger client2 = BCP.mDec(N, k, g, PK, mp, mq, c2);
		return BCP.enc(N, g, PK, client1.multiply(client2).mod(N));// (Z1,Z2)
	}

	public static BcpCiphertext mult(PP pp, BigInteger PK, MK mk, BcpCiphertext c1, BcpCiphertext c2) {
		return mult(pp.getN(), pp.getK(), pp.getG(), PK, mk.getMp(), mk.getMq(), c1, c2);
	}

	/**
	 * 生成公共公钥PK,改为reduce
	 * 
	 * @param N
	 * @param pkLst
	 *            子公钥列表
	 * @return PK
	 */
	public static BigInteger genPK(BigInteger N, Collection<BigInteger> pkLst) {
		if (CollectionUtils.isEmpty(pkLst)) {
			return null;
		}
		BigInteger product = pkLst.stream().reduce(BigInteger::multiply).get();
		return product.mod(N.multiply(N));
	}

	/**
	 * 生成盲
	 * 
	 * @param N
	 * @return
	 */
	public static BigInteger generateBlindness(BigInteger N) {// multToS会调用该方法
		BigInteger blind;
		do {
			blind = new BigInteger(N.bitLength(), new Random());
		} while (blind.compareTo(BigInteger.ZERO) < 0 || blind.compareTo(N) >= 0);
		return blind;
	}

	/**
	 * 基础盲化c
	 * 
	 * @param N
	 * @param g
	 * @param h
	 * @param blindness
	 * @param c
	 * @return
	 */
	public static BcpBlindCiphertext blind(BigInteger N, BigInteger g, BigInteger h, BigInteger blindness,
			BcpCiphertext c) {
		BcpCiphertext encblind = enc(N, g, h, blindness);
		BcpCiphertext blindcipher = add(N, c, encblind);

		BcpBlindCiphertext result = new BcpBlindCiphertext(blindcipher, blindness);
		return result;
	}

	/**
	 * keyProd时的盲化c，直接用blindness作为盲，乘法操作时用blindness的加法逆元作为盲
	 * 
	 * @param N
	 * @param g
	 * @param h
	 * @param c
	 * @return
	 */
	public static BcpBlindCiphertext keyProdBlind(BigInteger N, BigInteger g, BigInteger h, BcpCiphertext c) {
		return blind(N, g, h, generateBlindness(N), c);
	}

	/**
	 * 盲化c
	 * 
	 * @param pp
	 * @param h
	 * @param c
	 * @return
	 */
	public static BcpBlindCiphertext keyProdBlind(PP pp, BigInteger h, BcpCiphertext c) {
		return keyProdBlind(pp.getN(), pp.getG(), h, c);
	}

	/**
	 * 批量盲化
	 * 
	 * @param N
	 * @param g
	 * @param h
	 * @param ciphertextList
	 * @return
	 */
	public static List<BcpBlindCiphertext> keyProdBlind(BigInteger N, BigInteger g, BigInteger h,
			Collection<? extends BcpCiphertext> ciphertextList) {
		return ciphertextList.stream().map((ciphertext) -> keyProdBlind(N, g, h, ciphertext))
				.collect(Collectors.toList());
	}

	/**
	 * 使用blindinverse去盲，注意这是blindiness在N下的加法逆元
	 * 
	 * @param N
	 * @param g
	 * @param PK
	 * @param blindness
	 * @param c
	 * @return
	 */
	public static BcpCiphertext removeKeyProdBlind(BigInteger N, BigInteger g, BigInteger PK, BigInteger blindness,
			BcpCiphertext c) {
		BcpCiphertext encBlindness = enc(N, g, PK, additiveInverse(blindness, N));
		return add(N, c, encBlindness);
	}

	/**
	 * 使用blindinverse去盲，注意这是blindiness在N下的加法逆元
	 * 
	 * @param N
	 * @param g
	 * @param PK
	 * @param blindCiphertext
	 * @return
	 */
	public static BcpCiphertext removeKeyProdBlind(BigInteger N, BigInteger g, BigInteger PK,
			BcpBlindCiphertext blindCiphertext) {
		return removeKeyProdBlind(N, g, PK, blindCiphertext.getBlindness(), blindCiphertext);
	}

	/**
	 * 批量解盲
	 * 
	 * @param N
	 * @param g
	 * @param PK
	 * @param blindCiphertextList
	 * @return
	 */
	public static List<BcpCiphertext> removeKeyProdBlind(BigInteger N, BigInteger g, BigInteger PK,
			Collection<? extends BcpBlindCiphertext> blindCiphertextList) {
		return blindCiphertextList.stream().map((blindCiphertext) -> removeKeyProdBlind(N, g, PK, blindCiphertext))
				.collect(Collectors.toList());
	}

	public static void main(String[] args) {
		// 明文
		BigInteger m1 = new BigInteger("22");
		BigInteger m2 = new BigInteger("-23");
		BigInteger m3 = new BigInteger("53");

		// bcp参数
		BCP bcp = new BCP(256, 100);
		PP pp = bcp.getPP();
		BigInteger n = pp.getN();
		BigInteger g = pp.getG();
		BcpKeyPair ha = keyGen(n, g);
		BigInteger h = ha.getH();
		BigInteger a = ha.getA();
		// 密文
		BcpCiphertext c1 = BCP.enc(n, g, h, m1);
		BcpCiphertext c2 = BCP.enc(n, g, h, m2);
		BcpCiphertext c3 = BCP.enc(n, g, h, m3);

		BcpCiphertext c12 = BCP.add(n, c1, c2);
		BcpCiphertext c123 = BCP.add(n, c12, c3);

		BigInteger d12 = dec(n, a, c12);
		BigInteger d123 = dec(n, a, c123);
		System.out.println(d12);
		System.out.println(d123);

		System.out.println(d12.modInverse(n));
		System.out.println(d12.divide(new BigInteger("2")));
		System.out.println(Arrays.toString(d12.divideAndRemainder(new BigInteger("2"))));
	}

}
