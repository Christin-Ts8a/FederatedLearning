package com.bcp.general.model;

import java.math.BigInteger;
import java.util.List;

import com.bcp.general.crypto.BcpCiphertext;
import com.bcp.general.crypto.PP;

public class BcpCommunicateModel {

	/**
	 * 只传N
	 */
	private PP pp;
	
	/**
	 * 公共公钥PK
	 */
	private BigInteger PK;

	/**
	 * 客户端列表
	 */
	private List<BcpUserModel> userModelList;
	
	/**
	 * 用PK加密的计算结果
	 */
	private List<? extends BcpCiphertext> result;

	public PP getPp() {
		return pp;
	}

	public void setPp(PP pp) {
		this.pp = pp;
	}

	public List<BcpUserModel> getUserModelList() {
		return userModelList;
	}

	public void setUserModelList(List<BcpUserModel> userModelList) {
		this.userModelList = userModelList;
	}

	public BigInteger getPK() {
		return PK;
	}

	public void setPK(BigInteger pK) {
		PK = pK;
	}

	public List<? extends BcpCiphertext> getResult() {
		return result;
	}

	public void setResult(List<? extends BcpCiphertext> result) {
		this.result = result;
	}

}
