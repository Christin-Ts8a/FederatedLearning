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
	private String username;
	
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

	public String getUserName() {
		return username;
	}

	public void setUserName(String username) {
		this.username = username;
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
	
}
