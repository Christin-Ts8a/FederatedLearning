package com.bcp.general.model;

public class RetModel {

	private Integer retCode;
	
	private String retMess;
	
	private Object retValue;

	public RetModel() {
	}
	
	public RetModel(Integer retCode, String retMess, Object retValue) {
		super();
		this.retCode = retCode;
		this.retMess = retMess;
		this.retValue = retValue;
	}

	public Integer getRetCode() {
		return retCode;
	}

	public void setRetCode(Integer retCode) {
		this.retCode = retCode;
	}

	public String getRetMess() {
		return retMess;
	}

	public void setRetMess(String retMess) {
		this.retMess = retMess;
	}

	public Object getRetValue() {
		return retValue;
	}

	public void setRetValue(Object retValue) {
		this.retValue = retValue;
	}
	
}
