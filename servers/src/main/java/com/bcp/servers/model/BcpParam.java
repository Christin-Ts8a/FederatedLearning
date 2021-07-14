package com.bcp.servers.model;


import javax.persistence.*;
import javax.validation.constraints.NotBlank;

import org.apache.ibatis.type.JdbcType;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

import tk.mybatis.mapper.annotation.ColumnType;

@Table(name = "bcp_param")
public class BcpParam {
	/**
	 * mysql用(N,k,g)做联合主键超出3072byte最大值。N作为唯一主键一般情况没有问题，虽然N=pq且对于同一个N时k和g可以生成多对，但一般不会这么做。N一般设置为2048，防止超出主键范围还是设置3072byte。
	 */
	@Id
	@NotBlank
	@Column(name = "n")
	@ColumnType(jdbcType = JdbcType.VARCHAR)
	@JsonSerialize(using = ToStringSerializer.class)
	private String n;

	@NotBlank
	@ColumnType(jdbcType = JdbcType.VARCHAR)
	@JsonSerialize(using = ToStringSerializer.class)
	private String k;

	@NotBlank
	@JsonSerialize(using = ToStringSerializer.class)
	@ColumnType(jdbcType = JdbcType.VARCHAR)
	private String g;

	/**
	 * p`
	 */
	@JsonIgnore
	@ColumnType(jdbcType = JdbcType.VARCHAR)
	private String mp;

	/**
	 * q`
	 */
	@JsonIgnore
	@ColumnType(jdbcType = JdbcType.VARCHAR)
	private String mq;
	
	@JsonIgnore
	private Integer kappa;
	
	@JsonIgnore
	private Integer certainty;

	public String getN() {
		return n;
	}

	public void setN(String n) {
		this.n = n;
	}

	public String getK() {
		return k;
	}

	public void setK(String k) {
		this.k = k;
	}

	public String getG() {
		return g;
	}

	public void setG(String g) {
		this.g = g;
	}

	public String getMp() {
		return mp;
	}

	public void setMp(String mp) {
		this.mp = mp;
	}

	public String getMq() {
		return mq;
	}

	public void setMq(String mq) {
		this.mq = mq;
	}

	public Integer getKappa() {
		return kappa;
	}

	public void setKappa(Integer kappa) {
		this.kappa = kappa;
	}

	public Integer getCertainty() {
		return certainty;
	}

	public void setCertainty(Integer certainty) {
		this.certainty = certainty;
	}

}