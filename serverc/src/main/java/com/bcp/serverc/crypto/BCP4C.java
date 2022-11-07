package com.bcp.serverc.crypto;

import java.math.BigInteger;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.validation.Valid;


import com.bcp.general.crypto.BcpBlindCiphertext;
import com.bcp.general.crypto.BcpCiphertext;
import com.bcp.general.crypto.PP;
import com.bcp.general.constant.BCPConstant;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class BCP4C implements BCPConstant {

	private PP pp;

	// --------------get set----------------------------------
	public PP getPp() {
		return pp;
	}

	public void setPp(PP pp) {
		this.pp = pp;
	}
	// ----------------------------------------------------------------

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
	 * 批量加法
	 * 
	 * @param N
	 * @param aList
	 * @param bList
	 * @return
	 */
	public static List<? extends BcpCiphertext> add(BigInteger N, List<? extends BcpCiphertext> aList,
			List<? extends BcpCiphertext> bList) {
		List<BcpCiphertext> resultList = new ArrayList<>();
		if (!CollectionUtils.isEmpty(aList) && !CollectionUtils.isEmpty(bList)) {
			if (aList.size() != bList.size()) {
				throw new IllegalArgumentException(
						"size of a:" + aList.size() + " is not equals to size of b:" + bList.size());
			}
			for (int i = 0; i < aList.size(); i++) {
				BcpCiphertext a = aList.get(i);
				BcpCiphertext b = bList.get(i);
				BcpCiphertext result = add(N, a, b);
				resultList.add(result);
			}
		}
		return resultList;
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
		BigInteger client1 = BCP4C.mDec(N, k, g, PK, mp, mq, c1);
		BigInteger client2 = BCP4C.mDec(N, k, g, PK, mp, mq, c2);
		return BCP4C.enc(N, g, PK, client1.multiply(client2).mod(N));// (Z1,Z2)
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

	/**
	 * 检测给定PP是否合法
	 * 
	 * @param pp
	 * @return
	 */
	public static boolean isValidPP(PP pp) {
		return pp != null && pp.getN() != null && pp.getK() != null & pp.getG() != null;
	}

	public static void main(String[] args) {
		BigInteger[] ba = { new BigInteger("123"), new BigInteger("456"), new BigInteger("789") };
		BigInteger bigInteger = Stream.of(ba).reduce(BigInteger::multiply).get();
		System.out.println(bigInteger);
	}
}
