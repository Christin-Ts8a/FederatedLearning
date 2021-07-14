package com.bcp.general.crypto;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

public class PP implements Serializable {
	
	private static final long serialVersionUID = -8304824410486139727L;
	
	@JsonSerialize(using = ToStringSerializer.class)
	private BigInteger n;
	
	@JsonSerialize(using = ToStringSerializer.class)
	private BigInteger k;
	
	@JsonSerialize(using = ToStringSerializer.class)
	private BigInteger g;

	public PP() {
	}
	
	public PP(BigInteger n, BigInteger k, BigInteger g) {
		this.n = n;
		this.k = k;
		this.g = g;
	}

	public BigInteger getN() {
		return n;
	}

	public BigInteger getK() {
		return k;
	}

	public BigInteger getG() {
		return g;
	}
	
	public void setN(BigInteger n) {
		this.n = n;
	}

	public void setK(BigInteger k) {
		this.k = k;
	}

	public void setG(BigInteger g) {
		this.g = g;
	}

	@JsonIgnore
	public BigInteger[] getAll() {
		return new BigInteger[] { n, k, g };
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof PP) {
			PP pp2 = (PP) obj;
			return Objects.equals(n, pp2.n) && Objects.equals(k, pp2.k) && Objects.equals(g, pp2.g);
		} else {
			return false;
		}
	}
}
