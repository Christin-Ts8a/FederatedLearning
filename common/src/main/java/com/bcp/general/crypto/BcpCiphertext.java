package com.bcp.general.crypto;

import java.math.BigInteger;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

/**
 * 之前因为明文是形如[A,G,T]的一维数组，所以密文是二维数组，这里不再用数组，改封装密文类
 *
 */
public class BcpCiphertext {

	@JsonSerialize(using = ToStringSerializer.class)
	private BigInteger a;

	@JsonSerialize(using = ToStringSerializer.class)
	private BigInteger b;

	public BcpCiphertext() {
	}

	public BcpCiphertext(BigInteger a, BigInteger b) {
		this.a = a;
		this.b = b;
	}

	public BigInteger getA() {
		return a;
	}

	public void setA(BigInteger a) {
		this.a = a;
	}

	public BigInteger getB() {
		return b;
	}

	public void setB(BigInteger b) {
		this.b = b;
	}

	/**
	 * 
	 */
	@Override
	public String toString() {
		return "A:(" + a + ")\nB:(" + b + ")";
	}

}
