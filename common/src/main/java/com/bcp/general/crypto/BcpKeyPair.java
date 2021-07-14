package com.bcp.general.crypto;

import java.math.BigInteger;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

public class BcpKeyPair {

	/**
	 * 公钥
	 */
	@JsonSerialize(using = ToStringSerializer.class)
	private BigInteger h;

	/**
	 * 私钥
	 */
	@JsonSerialize(using = ToStringSerializer.class)
	private BigInteger a;

	public BcpKeyPair() {
	}
	
	public BcpKeyPair(BigInteger h, BigInteger a) {
		this.h = h;
		this.a = a;
	}

	public BigInteger getH() {
		return h;
	}

	public void setH(BigInteger h) {
		this.h = h;
	}

	public BigInteger getA() {
		return a;
	}

	public void setA(BigInteger a) {
		this.a = a;
	}

}
