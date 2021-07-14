package com.bcp.serverc.model;

import java.util.Map;

import com.bcp.general.crypto.BcpCiphertext;
import com.bcp.general.model.BcpUserModel;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class BcpUserModelExt extends BcpUserModel {

	/**
	 * 不返回给前台，仅用于转换数据库查出的BcpTaskResult密文时保证密文顺序
	 */
	@JsonIgnore
	private Map<Integer, BcpCiphertext> order2CiphertextMap;

	public Map<Integer, BcpCiphertext> getOrder2CiphertextMap() {
		return order2CiphertextMap;
	}

	public void setOrder2CiphertextMap(Map<Integer, BcpCiphertext> order2CiphertextMap) {
		this.order2CiphertextMap = order2CiphertextMap;
	}
	
}
