package com.bcp.general.model;

import java.math.BigInteger;
import java.util.List;

import com.bcp.general.crypto.BcpCiphertext;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

public class BcpUserModel {

	/**
	 * 标识用户id
	 */
	private Long userId;
	
	/**
	 * 标识这是属于哪个任务的
	 */
	private Long taskId;
	
	/**
	 * 标识当前轮数
	 */
	private Integer round;
	
	/**
	 * 当任意客户端在一轮的开始时将stop置为了true，则结束当前循环
	 */
	private boolean stop;
	
	/**
	 * 用户的公钥
	 */
	@JsonSerialize(using = ToStringSerializer.class)
	private BigInteger h;
	
	/**
	 * 用户的密文，防止多条用list存，全部都用h加密
	 */
	private List<? extends BcpCiphertext> ciphertextList;
	
	/**
	 * 用户数量，用于客户端自己对相加后的参数进行算术平均
	 */
	private Integer userCount;

	private String struct;

	public String getStruct() {
		return struct;
	}

	public void setStruct(String struct) {
		this.struct = struct;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public BigInteger getH() {
		return h;
	}

	public void setH(BigInteger h) {
		this.h = h;
	}

	public List<? extends BcpCiphertext> getCiphertextList() {
		return ciphertextList;
	}

	public void setCiphertextList(List<? extends BcpCiphertext> ciphertextList) {
		this.ciphertextList = ciphertextList;
	}

	public Integer getUserCount() {
		return userCount;
	}

	public void setUserCount(Integer userCount) {
		this.userCount = userCount;
	}

	public Long getTaskId() {
		return taskId;
	}

	public void setTaskId(Long taskId) {
		this.taskId = taskId;
	}

	public Integer getRound() {
		return round;
	}

	public void setRound(Integer round) {
		this.round = round;
	}

	public boolean isStop() {
		return stop;
	}

	public void setStop(boolean stop) {
		this.stop = stop;
	}

	@Override
	public String toString() {
		return "BcpUserModel{" +
				"userId='" + userId + '\'' +
				", taskId=" + taskId +
				", round=" + round +
				", stop=" + stop +
				", h=" + h +
				", ciphertextList=" + ciphertextList +
				", userCount=" + userCount +
				", struct='" + struct + '\'' +
				'}';
	}
}
