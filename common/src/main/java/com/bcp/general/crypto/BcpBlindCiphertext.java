package com.bcp.general.crypto;

import java.math.BigInteger;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class BcpBlindCiphertext extends BcpCiphertext {

	/**
	 * 盲
	 */
	@JsonIgnore
	private BigInteger blindness;

	public BcpBlindCiphertext() {
	}
	
	public BcpBlindCiphertext(BigInteger A, BigInteger B, BigInteger blind) {
		super(A, B);
		this.blindness = blind;
	}

	public BcpBlindCiphertext(BcpCiphertext c, BigInteger blind) {
		this(c.getA(), c.getB(), blind);
	}

	public BigInteger getBlindness() {
		return blindness;
	}

	public void setBlindness(BigInteger blindness) {
		this.blindness = blindness;
	}

	@Override
	public String toString() {
		return "BcpBlindCiphertext{" +
				"blindness=" + blindness +
				'}';
	}
}
