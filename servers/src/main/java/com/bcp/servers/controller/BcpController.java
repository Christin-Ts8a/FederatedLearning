package com.bcp.servers.controller;

import javax.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bcp.general.model.BcpCommunicateModel;
import com.bcp.servers.service.impl.BcpServiceImpl;

@RestController
@RequestMapping("/bcp")
@Validated
public class BcpController {

	@Autowired
	BcpServiceImpl bcpSrv;
	
	/**
	 * 生成一组BCP参数，存入数据库并返回
	 * @param kappa
	 * @param certainty
	 * @return
	 */
	@RequestMapping(value = "/create", method = RequestMethod.POST)
	public Object createBcpParam(@NotNull @RequestParam Integer kappa, Integer certainty) {
		Object ret = bcpSrv.createBcpParam(kappa, certainty);
		return ret;
	}
	
	@RequestMapping(value = "/keyProd", method = RequestMethod.POST)
	public Object keyProd(@RequestBody BcpCommunicateModel keyProdArg) {
		Object ret = bcpSrv.keyProd(keyProdArg);
		return ret;
	}
	
	@RequestMapping(value = "/transDec", method = RequestMethod.POST)
	public Object transDec(@RequestBody BcpCommunicateModel keyProdArg) {
		Object ret = bcpSrv.transDec(keyProdArg);
		return ret;
	}

//	/**
//	 * 目标是将包括keyProd在内的一次fedavg的全部操作封装在一个接口中
//	 * @param keyProdArg
//	 * @return
//	 */
//	@RequestMapping(value = "/fedAvg", method = RequestMethod.POST)
//	public Object fedAvg(@RequestBody BcpKeyProdArg keyProdArg) {
//		
//		Object ret = null;
//		
//		return ret;
//	}
	
}
