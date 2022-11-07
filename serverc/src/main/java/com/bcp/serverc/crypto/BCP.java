package com.bcp.serverc.crypto;

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
		BigInteger temp = m.multiply(N).add(BigInteger.ONE);
		BigInteger B2 = temp.subtract(temp.divideAndRemainder(N2)[0].multiply(N2));
		BigInteger B;
		if (m.compareTo(BigInteger.ZERO) < 0) {
			B = B1.multiply(B2).mod(N2).subtract(N2);
		} else {
			B = B1.multiply(B2).mod(N2);
		}

		return new BcpCiphertext(A, B);
	}

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
		BigInteger tempB;
		BigInteger tempC;
		BigInteger tempD;
		if (B.compareTo(BigInteger.ZERO) < 0) {
			tempB = B.mod(N2).subtract(N2);
			tempC = tempA.multiply(tempB).mod(N2).subtract(N2);
			tempD = tempC.subtract(BigInteger.ONE.mod(N2)).mod(N2).subtract(N2);
		}
		else {
			tempB = B.mod(N2);
			tempC = tempA.multiply(tempB).mod(N2);
			tempD = tempC.subtract(BigInteger.ONE.mod(N2)).mod(N2);
		}
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

		BigInteger result;
		if (B.compareTo(BigInteger.ZERO) < 0) {
			BigInteger bModPowMN = B.modPow(mN, N2).subtract(N2);
			BigInteger denominator = bModPowMN
					.multiply(g.modInverse(N2).modPow(gamma.multiply(mN), N2))
					.subtract(BigInteger.ONE.mod(N2))
					.mod(N2)
					.subtract(N2);
			result = denominator
					.multiply(delta)
					.divide(N)
					.mod(N)
					.subtract(N);
		} else {
			BigInteger tempm = B.modPow(mN, N2)
					.multiply(g.modInverse(N2).modPow(gamma.multiply(mN), N2))
					.mod(N2)
					.subtract(BigInteger.ONE.mod(N2))
					.mod(N2);
			result = tempm
					.multiply(delta)
					.divide(N)
					.mod(N);
		}

		return result;
	}

	public static BigInteger mDec(PP pp, BigInteger h, MK mk, BcpCiphertext c) {
		return mDec(pp.getN(), pp.getK(), pp.getG(), h, mk.getMp(), mk.getMq(), c);
	}

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
		BigInteger newB;
		if (a.getB().compareTo(BigInteger.ZERO) < 0 ||b.getB().compareTo(BigInteger.ZERO) < 0) {
			newB = a.getB().multiply(b.getB()).mod(N2).subtract(N2);
		} else {
			newB = a.getB().multiply(b.getB()).mod(N2);
		}
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
//		// 密文
//		BcpCiphertext ciphertext = new BcpBlindCiphertext();
//		ciphertext.setA(new BigInteger("3380002628831292291771134077437562380347264913825424861348978999006586061055803471218041860927913660261029544748986818542174703039002813495170770397406413965939057272752648579653889703145780743943633142907920822944845293115976687965485008987412131801700449247844070674234955261858885766536427486698590621974096613620936482971501508628913464638927922994560754140276972339658436152151813992162558236693032560064640741095579294637073356959878612846610852760779030346084250350682784410352092772304710312696052862221097996061768391210811230142907700274423863184315783419269280635237741714836749562833671169022676584686088017883448698470898239101973153884260762796483959874094617678078671772663327576761224849990254755424434714540352830773350125077245321853274482018232568455962015464707323759659269801564395539363334166759699148575297206864536640"));
//		ciphertext.setB(new BigInteger("-1422792562833341657336527090627027917063339844020652352593672972659539161346081199920974757567516634797343011621663076512887752569655964710119534597737345639535716466322196797429762435832283940552845882643720075762208417335802408905132805456829274599093661282944372608065348916025003729464291251543670332950163761114097515514765696340014930754410059071444353570366654919799553201736774819430508004076490840079850274102527422489003132806082546539070147166588174318460810495269346749226432589114331796602416405952536593929157111733818843694146506390878658011740786881437999503618706067307923462531784220813724109125700671696519831361762897925268311975046388664293274397057881263736183043465761606082510219776152256050214007548057191990566355665418815890414412079131712669382161840996340974739554543228356533368915368567410763578030185846560697"));

		// bcp参数
		BCP bcp = new BCP(256, 100);
		PP pp = bcp.getPP();
		BigInteger n = pp.getN();
//		BigInteger n = new BigInteger("1998020857175272540286015489115639250921162534406450841568234940423199720305111862380112065024408555421543552116677858785689575260349597815610966482322504769383128476601520173147567955828168890274951593873794758645090250733189822245342140383925782352778175173752193838540226381517185481919021956924842142069383809939206295519246231922756641158069000864370784356146150799122236234129501712438078797853397850928284026414589");
		BigInteger g = pp.getG();
//		BigInteger g = new BigInteger("1382383445975905178331747392552203234246042770010428999471755028399948986110031816107940355959127565009440533437213142625215901097519556734696196638860219917174592099395233086534809155438803861592928046710378726254587474149394696990796095912084093696271");
		BcpKeyPair ha = keyGen(n, g);
		BigInteger h = ha.getH();
		BigInteger a = ha.getA();

		BcpCiphertext enc = BCP.enc(n, g, h, new BigInteger("-10101"));
		BcpCiphertext enc1 = BCP.enc(n, g, h, new BigInteger("101"));

		MK mk = bcp.getMK();
		BigInteger blindness = BCP.generateBlindness(n);
		BcpBlindCiphertext bcpBlindCiphertext = BCP.blind(n, g, h, blindness, enc);
		BcpCiphertext ciphertext = BCP.removeKeyProdBlind(n, g, h, blindness, bcpBlindCiphertext);
		System.out.println("==============================================");
		System.out.println(BCP.dec(n, a, BCP.add(n, enc, enc1)));
		System.out.println("==============================================");
	}

}
