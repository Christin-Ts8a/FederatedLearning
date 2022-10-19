package com.bcp.servers.service.impl;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bcp.servers.crypto.BCP;
import com.bcp.general.crypto.BcpCiphertext;
import com.bcp.servers.crypto.MK;
import com.bcp.general.crypto.PP;
import com.bcp.servers.mapper.BcpParamMapper;
import com.bcp.general.model.BcpCommunicateModel;
import com.bcp.general.model.BcpUserModel;
import com.bcp.servers.model.BcpParam;

@Service
@Transactional(readOnly = false, rollbackFor = Exception.class)
public class BcpServiceImpl {

	@Autowired
	BcpParamMapper bcpParamMapper;

	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public Object createBcpParam(Integer kappa, Integer certainty) {
		if (kappa == null) {
			kappa = 512;
		}
		if (certainty == null) {
			certainty = 100;
		}

		BCP bcp = new BCP(kappa, certainty);

		PP pp = bcp.getPP();
		MK mk = bcp.getMK();

		BcpParam bcpParam = new BcpParam();
		bcpParam.setN(pp.getN().toString());
		bcpParam.setK(pp.getK().toString());
		bcpParam.setG(pp.getG().toString());
		bcpParam.setMp(mk.getMp().toString());
		bcpParam.setMq(mk.getMq().toString());
		bcpParam.setKappa(kappa);
		bcpParam.setCertainty(certainty);

		bcpParamMapper.insert(bcpParam);

		return bcpParam;
	}

	public Object keyProd(BcpCommunicateModel keyProdArg) {
		List<BcpUserModel> modelLst = keyProdArg.getUserModelList();
		PP pp = keyProdArg.getPp();
		BigInteger N = pp.getN();

		// 查出mk和pp其余参数
		// 主键必须转换成字符串，否则大整数超出数据库整数类型无法查出准确数据
		BcpParam ppMk = bcpParamMapper.selectByPrimaryKey(N.toString());
		BigInteger k = new BigInteger(ppMk.getK());
		BigInteger g = new BigInteger(ppMk.getG());
		BigInteger mp = new BigInteger(ppMk.getMp());
		BigInteger mq = new BigInteger(ppMk.getMq());// 别弄成mp

		// 1.生成PK
		List<BigInteger> pkLst = modelLst.stream().map((model) -> model.getH()).collect(Collectors.toList());
		BigInteger PK = BCP.genPK(N, pkLst);

		// 2.将由h加密的加盲密文转换为由PK加密
		List<BcpUserModel> retLst = new ArrayList<>();
		for (BcpUserModel model : modelLst) {
			BcpUserModel ret = new BcpUserModel();// 返回结构
			ArrayList<BcpCiphertext> ciphertextPKLst = new ArrayList<>();

			BigInteger h = model.getH();// 公钥
			List<? extends BcpCiphertext> ciphertextLst = model.getCiphertextList();
			for (BcpCiphertext CiDi : ciphertextLst) {
				// 使用mdec解密
				BigInteger zi = BCP.mDec(N, k, g, h, mp, mq, CiDi);
				// 使用PK再次加密
				BcpCiphertext WiZi = BCP.enc(N, g, PK, zi);
				// 存入返回list
				ciphertextPKLst.add(WiZi);
			}

			// 设置返回结构
			ret.setH(PK);
			ret.setUserId(model.getUserId());
			ret.setCiphertextList(ciphertextPKLst);
			retLst.add(ret);
		}
		
		// 返回信息
		BcpCommunicateModel ret = new BcpCommunicateModel();
		ret.setPK(PK);
		ret.setPp(pp);
		ret.setUserModelList(retLst);

		return ret;
	}

	// /**
	// * 无数据库的版本
	// *
	// * @param keyProdArg
	// * @param param
	// * @return
	// */
	// public Object mult(BcpKeyProdArg keyProdArg, BcpParam param) {
	// List<BcpKeyProdModel> modelLst = keyProdArg.getModelLst();
	// BigInteger N = param.getN();
	// BigInteger k = param.getK();
	// BigInteger g = param.getG();
	// BigInteger mp = param.getMp();
	// BigInteger mq = param.getMq();
	// BigInteger PK = keyProdArg.getPK();// PK
	//
	// // 1.乘法计算
	// ArrayList<BcpKeyProdModel> retLst = new ArrayList<>();
	// // for (BcpKeyProdModel model : modelLst) {
	// // BcpKeyProdModel keyProdRet = new BcpKeyProdModel();// 返回结构
	// // ArrayList<BcpCiphertext> ciphertextPKLst = new ArrayList<>();
	// //
	// // BigInteger h = model.getH();// 公钥
	// // List<BcpCiphertext> ciphertextLst = model.getCiphertextLst();
	// // for (BcpCiphertext CiDi : ciphertextLst) {
	// // // 使用mdec解密
	// // BigInteger zi = BCP.mDec(N, k, g, h, mp, mq, CiDi);
	// // // 使用PK再次加密
	// // BcpCiphertext WiZi = BCP.enc(N, g, PK, zi);
	// // // 存入返回list
	// // ciphertextPKLst.add(WiZi);
	// // }
	// //
	// // // 设置返回结构
	// // keyProdRet.setH(PK);
	// // keyProdRet.setUserId(model.getUserId());
	// // keyProdRet.setCiphertextLst(ciphertextPKLst);
	// // retLst.add(keyProdRet);
	// // }
	//
	// return retLst;
	// }
	//
	// public Object mult(BcpKeyProdArg keyProdArg) {
	// List<BcpKeyProdModel> modelLst = keyProdArg.getModelLst();
	// PP pp = keyProdArg.getPp();
	// BigInteger N = pp.getN();
	//
	// // 1.查出mk和pp其余参数
	// BcpParam ppMk = bcpParamMapper.selectByPrimaryKey(N.toString());
	//
	// // 2.生成PK
	// ArrayList<BigInteger> pkLst = new ArrayList<>();
	// for (BcpKeyProdModel bcpKeyProdModel : modelLst) {
	// pkLst.add(bcpKeyProdModel.getH());
	// }
	// BigInteger PK = BCP.genPK(N, pkLst);
	// keyProdArg.setPK(PK);
	//
	// // 3.重载
	// return mult(keyProdArg, ppMk);
	// }

	/**
	 * S应该只需要keyProd和transDec两部分，不需要中间的mult
	 * 
	 * @param transDecArg
	 * @return
	 */
	public Object transDec(BcpCommunicateModel transDecArg) {
		// 获取基本信息
		List<BcpUserModel> modelLst = transDecArg.getUserModelList();
		PP pp = transDecArg.getPp();
		BigInteger N = pp.getN();
		List<? extends BcpCiphertext> resultOnPK = transDecArg.getResult();

		// 查出mk和pp其余参数
		BcpParam ppMk = bcpParamMapper.selectByPrimaryKey(N.toString());
		BigInteger k = new BigInteger(ppMk.getK());
		BigInteger g = new BigInteger(ppMk.getG());
		BigInteger mp = new BigInteger(ppMk.getMp());
		BigInteger mq = new BigInteger(ppMk.getMq());// 别弄成mp

		// 1.生成PK
		List<BigInteger> pkLst = modelLst.stream().map((model) -> model.getH()).collect(Collectors.toList());
		BigInteger PK = BCP.genPK(N, pkLst);

		// 2.将由PK加密的加盲计算结果转换为由用户各自公钥h加密
		List<BcpUserModel> retLst = modelLst.stream().map((model) -> {
			BcpUserModel ret = new BcpUserModel();// 返回结构
			BigInteger h = model.getH();// 公钥
			
			// 循环解密计算结果
			List<BcpCiphertext> resultOnHList = resultOnPK.stream().map((CiDi) -> {
				// 使用mdec解密PK加密的结果
				BigInteger zi = BCP.mDec(N, k, g, PK, mp, mq, CiDi);
				// 使用h再次加密
				BcpCiphertext WiZi = BCP.enc(N, g, h, zi);
				// 返回WiZi
				return WiZi;
			}).collect(Collectors.toList());

			// 设置返回结构
			ret.setH(h);
			ret.setUserId(model.getUserId());
			ret.setCiphertextList(resultOnHList);
			return ret;
		}).collect(Collectors.toList());

		// 必须封装返回，单独返回retLst在postForObject中无法解析出List<BigInteger>泛型类型
		BcpCommunicateModel ret = new BcpCommunicateModel();
		ret.setUserModelList(retLst);// 其他参数都没必要返回
		
		return ret;
	}

}
