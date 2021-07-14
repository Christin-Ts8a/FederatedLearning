package com.bcp.serverc.crypto;

import java.math.BigInteger;

public class MK {

	private final BigInteger mp;
	private final BigInteger p;
	private final BigInteger mq;
	private final BigInteger q;

	/**
	 * mp*2+1=p
	 * @param mp
	 * @param mq
	 */
	public MK(BigInteger mp, BigInteger mq) {
		this.mp = mp;
		this.mq = mq;
		this.p = mp.shiftLeft(1).add(BigInteger.ONE);
		this.q = mq.shiftLeft(1).add(BigInteger.ONE);
	}

	public BigInteger getMp() {
		return mp;
	}

	public BigInteger getP() {
		return p;
	}

	public BigInteger getMq() {
		return mq;
	}

	public BigInteger getQ() {
		return q;
	}

	public BigInteger[] getAll() {
		return new BigInteger[] { mp, mq, p, q };
	}

}
