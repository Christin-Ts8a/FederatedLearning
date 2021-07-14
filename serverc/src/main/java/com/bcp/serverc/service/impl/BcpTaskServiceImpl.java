package com.bcp.serverc.service.impl;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.SetUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import com.bcp.serverc.config.ServerProperties;
import com.bcp.serverc.constant.CryptoConstant;
import com.bcp.serverc.controller.WebSocketServer;
import com.bcp.serverc.crypto.BCP4C;
import com.bcp.general.crypto.BcpBlindCiphertext;
import com.bcp.general.crypto.BcpCiphertext;
import com.bcp.general.crypto.PP;

import com.bcp.serverc.mapper.BcpTaskCiphertextMapper;
import com.bcp.serverc.mapper.BcpTaskMapper;
import com.bcp.serverc.mapper.BcpTaskResultMapper;
import com.bcp.serverc.mapper.BcpTaskUserMapper;
import com.bcp.general.model.BcpCommunicateModel;
import com.bcp.serverc.model.BcpTask;
import com.bcp.serverc.model.BcpTaskCiphertext;
import com.bcp.serverc.model.BcpTaskResult;
import com.bcp.serverc.model.BcpTaskUser;
import com.bcp.serverc.model.BcpUserModelExt;
import com.bcp.general.model.BcpUserModel;
import com.bcp.general.model.RetModel;

import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.entity.Example.Criteria;

import javax.annotation.Resource;

@Transactional(readOnly = false, rollbackFor = Exception.class)
@Service
public class BcpTaskServiceImpl {

	@Resource
	BcpTaskMapper bcpTaskMapper;

	@Resource
	BcpTaskUserMapper bcpTaskUserMapper;

	@Resource
	BcpTaskCiphertextMapper bcpTaskCiphertextMapper;

	@Resource
	BcpTaskResultMapper bcpTaskResultMapper;

	@Resource
	WebSocketServer webSocketServer;

	@Resource
	ServerProperties sProperties;

	/**
	 * 开启任务，需要检测所有参与者都已连接webSocket才可以
	 * 
	 * @param taskArg
	 *            只需要传taskId
	 * @return
	 */
	public Object startBcpTask(BcpTask taskArg) {
		// 返回结构
		RetModel retModel = new RetModel();

		// 查询该任务是否开启
		Long taskId = taskArg.getTaskId();// 要开启的任务id
		// 查询出任务基本信息
		BcpTask task = bcpTaskMapper.selectByPrimaryKey(taskId);
		if (task == null || !BigDecimal.ZERO.equals(task.getTaskState())) {
			// 若任务不存在或状态不为未开启
			retModel.setRetMess("任务不存在或已开始，请检查任务");
			retModel.setRetValue(task);
			retModel.setRetCode(-1);
			return retModel;
		}

		// 不用全部在线也能开启任务
//		// 查询该任务的参与者
//		BcpTaskUser selTaskUserModel = new BcpTaskUser();
//		selTaskUserModel.setTaskId(taskId);
//		List<BcpTaskUser> taskUserList = bcpTaskUserMapper.select(selTaskUserModel);// 查询参与者
//		List<String> taskUserIdList = taskUserList.stream().map(bcpUser -> bcpUser.getTaskUserId())
//				.collect(Collectors.toList());// 参与者id列表
//
//		// 当前在线用户，跟任务无关所以变量名没有task
//		Map<String, Session> pool = WebSocketServer.getSessionPool();
//		Set<String> onlineUserIdSet = pool.keySet();
//
//		// 获取本次任务参与用户中未上线的用户列表
//		List<String> offlineTaskUserIdList = taskUserIdList.stream()
//				.filter((taskUserId) -> !onlineUserIdSet.contains(taskUserId)).collect(Collectors.toList());
//
//		if (!offlineTaskUserIdList.isEmpty()) {
//			// 若存在未上线用户
//			retModel.setRetMess("存在用户未上线，无法开启任务");
//			retModel.setRetValue(offlineTaskUserIdList);
//			retModel.setRetCode(-1);
//			return retModel;
//		}

		// 1.从s处获取bcp参数
		PP pp = null;
		if (task.getTaskN() == null || task.getTaskK() == null || task.getTaskG() == null) {
			// 只有开始前未设置的PP中任意一项的才重新设置PP，不重复设置PP
			// 发送请求获取pp参数
			pp = createBcpParam(task);
		} else {
			// 若开始前已设置PP，则用已有的
			pp = new PP(new BigInteger(task.getTaskN()), new BigInteger(task.getTaskK()),
					new BigInteger(task.getTaskG()));
		}

		// 批量更新本次任务状态
		task.setStartTime(new Date());
		task.setStartUser(UserServiceImpl.getCurrentLoginUserId());
		task.setTaskState(BigDecimal.ONE);
		if (BCP4C.isValidPP(pp)) {
			task.setTaskN(pp.getN().toString());
			task.setTaskK(pp.getK().toString());
			task.setTaskG(pp.getG().toString());
		}
		bcpTaskMapper.updateByPrimaryKeySelective(task);

		// 2.将pp广播给在线客户端
		webSocketServer.broadCast(pp);

		return retModel;
	}

	/**
	 * 检测是否结束，若不结束则将用户数据入库，并检测是否开启当前轮，若开启当前轮则进行一轮新的计算。 多操作的整合接口
	 * 
	 * @param userModel
	 */
	public void submitBcpTask(BcpUserModel userModel) {
		// 需要获取的信息
		String userId = userModel.getUserId();
		if (userId == null) {
			userId = UserServiceImpl.getCurrentLoginUserId();
			userModel.setUserId(userId);
		}
		Long taskId = userModel.getTaskId();
		BigInteger h = userModel.getH();

		// 1.检测是否结束
		if (userModel.isStop()) {
			// 若有用户上传数据时决定这一轮结束，则结束任务
			BcpTask finishTask = new BcpTask();
			finishTask.setTaskId(taskId);
			finishBcpTask(finishTask);
			return;
		}

		// 2.若不结束，则新增数据
		BcpTask bcpTask = bcpTaskMapper.selectByPrimaryKey(taskId.toString());
		BigDecimal currentRound = bcpTask.getCurrentRound();

		insertBcpTaskCiphertext(bcpTask, userModel);
		// 更新h到用户列表
		BcpTaskUser updTaskUser = new BcpTaskUser();
		updTaskUser.setTaskId(taskId);
		updTaskUser.setTaskUserId(userId);
		updTaskUser.setH(h.toString());
		bcpTaskUserMapper.updateByPrimaryKeySelective(updTaskUser);

		// 3.新增完毕，检测是否满足开启下一轮的条件，即使用户提交了空删掉所有提交数据也检测
		BcpTaskUser selTaskUser = new BcpTaskUser();
		selTaskUser.setTaskId(taskId);
		List<BcpTaskUser> bcpTaskUserList = bcpTaskUserMapper.select(selTaskUser);// 当前任务所有参与者
		// 当前任务所有参与者id
		List<String> bcpTaskUserIdList = bcpTaskUserList.stream().map(BcpTaskUser::getTaskUserId)
				.collect(Collectors.toList());

		// 查询当前任务所有参与者当前轮的密文提交情况，顺序一定要对
		Example example = new Example(BcpTaskCiphertext.class);
		Criteria criteria = example.createCriteria();
		criteria.andEqualTo("taskId", taskId);
		criteria.andEqualTo("taskRound", currentRound);
		example.setOrderByClause("task_id, task_user_id, ciphertext_order");
		List<BcpTaskCiphertext> bcpTaskCiphertextList = bcpTaskCiphertextMapper.selectByExample(example);

		// userId->user当前轮密文，用userId聚合，查看用户是否齐全
		HashMap<String, List<BcpTaskCiphertext>> bcpTaskCiphertextMap = new HashMap<>();
		for (BcpTaskCiphertext bcpTaskCiphertext : bcpTaskCiphertextList) {
			String taskUserId = bcpTaskCiphertext.getTaskUserId();
			List<BcpTaskCiphertext> curCiphertextList = bcpTaskCiphertextMap.get(taskUserId);
			if (curCiphertextList == null) {
				curCiphertextList = new ArrayList<>();
				bcpTaskCiphertextMap.put(taskUserId, curCiphertextList);
			}
			curCiphertextList.add(bcpTaskCiphertext);
		}
		// 已提交的用户
		Set<String> submittedUserIdSet = bcpTaskCiphertextMap.keySet();

		if (!SetUtils.isEqualSet(submittedUserIdSet, new HashSet<>(bcpTaskUserIdList))) {
			// 若两个集合不同，则还存在未提交用户，不开始这一轮计算
			return;
		}

		// 4.开始这一轮计算，将开启单轮交互的操作放于一次提交中
		ArrayList<BcpUserModel> userModelList = new ArrayList<BcpUserModel>();
		for (BcpTaskUser bcpTaskUser : bcpTaskUserList) {
			BcpUserModel bcpUserModel = new BcpUserModel();
			String taskUserId = bcpTaskUser.getTaskUserId();
			List<BcpTaskCiphertext> curBcpTaskCiphertextList = bcpTaskCiphertextMap.get(taskUserId);
			List<? extends BcpCiphertext> convertCiphertext = convertCiphertext(curBcpTaskCiphertextList);

			// 设置基本信息
			bcpUserModel.setH(new BigInteger(bcpTaskUser.getH()));
			bcpUserModel.setUserId(taskUserId);
			bcpUserModel.setTaskId(taskId);
			bcpUserModel.setRound(currentRound.intValue());
			bcpUserModel.setCiphertextList(convertCiphertext);
			userModelList.add(bcpUserModel);
		}
		// 开始计算
		singleRound(bcpTask, userModelList, userId);
	}

	/**
	 * 转换密文格式
	 * 
	 * @param ciphertext
	 * @return
	 */
	public static List<? extends BcpCiphertext> convertCiphertext(List<BcpTaskCiphertext> ciphertext) {
		List<BcpCiphertext> collect = ciphertext.stream().map(BcpTaskServiceImpl::convertCiphertext)
				.collect(Collectors.toList());
		return collect;
	}

	/**
	 * 转换密文格式
	 * 
	 * @param ciphertext
	 * @return
	 */
	public static BcpCiphertext convertCiphertext(BcpTaskCiphertext ciphertext) {
		BcpCiphertext bcpCiphertext = new BcpCiphertext();
		bcpCiphertext.setA(new BigInteger(ciphertext.getCiphertextA()));
		bcpCiphertext.setB(new BigInteger(ciphertext.getCiphertextB()));
		return bcpCiphertext;
	}

	/**
	 * 单轮交互流程，每个客户端
	 * 
	 * @param bcpTask
	 * @param userModelList
	 * @param userId
	 *            当前登录用户id
	 */
	public void singleRound(BcpTask bcpTask, List<BcpUserModel> userModelList, String userId) {
		// 1计算出PK
		BigInteger N = new BigInteger(bcpTask.getTaskN());
		List<BigInteger> pkLst = userModelList.stream().map(BcpUserModel::getH).collect(Collectors.toList());
		BigInteger PK = BCP4C.genPK(N, pkLst);
		Long taskId = bcpTask.getTaskId();
		BigDecimal computeRounds = bcpTask.getComputeRounds();
		PP pp = new PP(N, null, null);

		// 2 keyProd 把用户用各自公钥加密的密文转换为用公共公钥PK加密
		BcpCommunicateModel keyProdArg = new BcpCommunicateModel();
		keyProdArg.setPK(PK);
		keyProdArg.setUserModelList(userModelList);
		keyProdArg.setPp(pp);

		// 密文已经全部换底为PK加密
		List<BcpUserModel> userModelOnPKList = keyProd(bcpTask, keyProdArg);

		// 3 将客户端密文相加得出结果 不知道为啥通配泛型经过map后会丢掉受限通配<List<? extends
		// BcpCiphertext>>只保留非受限通配<?>
		// 因此只能循环来代替map操作
		List<List<? extends BcpCiphertext>> ciphertextMultiList = new ArrayList<>();
		for (BcpUserModel bcpUserModel : userModelOnPKList) {
			List<? extends BcpCiphertext> ciphertextList = bcpUserModel.getCiphertextList();
			ciphertextMultiList.add(ciphertextList);
		}

		// 加法结果
		List<? extends BcpCiphertext> addResult = ciphertextMultiList.stream().reduce((p1, p2) -> {
			return BCP4C.add(N, p1, p2);
		}).get();

		// 4 transDec将加法结果由PK加密转换为用户各自公钥加密
		for (BcpUserModel bcpUserModel : userModelList) {
			// 发送前将密文置空，不需要
			bcpUserModel.setCiphertextList(null);
		}
		BcpCommunicateModel transDecArg = new BcpCommunicateModel();
		transDecArg.setResult(addResult);
		transDecArg.setPK(PK);
		transDecArg.setPp(pp);
		transDecArg.setUserModelList(userModelList);
		// 用户各自公钥加密的用户结果
		List<BcpUserModel> transDecResult = transDec(bcpTask, transDecArg);

		// 5 计算完成结果入库
		insertBcpTaskResult(bcpTask, transDecResult);

		// 6 当前轮数加一
		BigDecimal nextRound = BigDecimal.ONE.add(bcpTask.getCurrentRound());
		if (nextRound.compareTo(computeRounds) > 0) {
			// 加一后若大于了最大轮数，则结束任务
			bcpTask.setFinishUser(userId);
			finishBcpTask(bcpTask);
		}
		BcpTask nextRoundTask = new BcpTask();
		nextRoundTask.setCurrentRound(nextRound);
		nextRoundTask.setTaskId(taskId);
		bcpTaskMapper.updateByPrimaryKeySelective(nextRoundTask);

		// 7 将结果发送给各个客户端
		transDecResult.forEach((userModel) -> {
			webSocketServer.sendMessage(userModel.getUserId(), userModel);
		});
	}

	/**
	 * 密文转换
	 * 
	 * @param bcpTask
	 * @param keyProdArg
	 * @return
	 */
	public List<BcpUserModel> keyProd(BcpTask bcpTask, BcpCommunicateModel keyProdArg) {
		// 获取基本信息
		BigInteger PK = keyProdArg.getPK();
		BigInteger N = new BigInteger(bcpTask.getTaskN());
		BigInteger g = new BigInteger(bcpTask.getTaskG());

		String sHost = sProperties.getsHost();
		String sPort = sProperties.getsPort();
		String sOrigin = "http://" + sHost + ":" + sPort;

		// 设置必需参数
		PP pp = new PP(N, null, null);
		keyProdArg.setPp(pp);

		// 1.把用户信息加盲，顺序在c处不会打乱，保存之前的密文和加盲密文
		List<List<BcpBlindCiphertext>> blindCiphertextList = new ArrayList<>();
		List<List<? extends BcpCiphertext>> originalCiphertextList = new ArrayList<>();

		List<BcpUserModel> bcpUserModelList = keyProdArg.getUserModelList();
		for (BcpUserModel bcpUserModel : bcpUserModelList) {
			// 基本信息
			BigInteger h = bcpUserModel.getH();
			List<? extends BcpCiphertext> ciphertextList = bcpUserModel.getCiphertextList();

			// 加盲
			List<BcpBlindCiphertext> keyProdBlindCiphertextList = BCP4C.keyProdBlind(N, g, h, ciphertextList);

			// 覆盖原有密文
			bcpUserModel.setCiphertextList(keyProdBlindCiphertextList);

			// 保存加盲密文和原有密文
			originalCiphertextList.add(ciphertextList);
			blindCiphertextList.add(keyProdBlindCiphertextList);
		}

		// 2.发送加盲密文
		RestTemplate rest = new RestTemplate();
		// 取回转换成PK加密的用户密文
		BcpCommunicateModel retOnPK = rest.postForObject(sOrigin + "/bcp/keyProd", keyProdArg,
				BcpCommunicateModel.class);
		List<BcpUserModel> retUserModelList = retOnPK.getUserModelList();

		// 3.去盲
		for (int i = 0; i < retUserModelList.size(); i++) {
			BcpUserModel bcpUserModelOnPK = retUserModelList.get(i);
			// 用PK加密的加盲密文
			List<? extends BcpCiphertext> blindCiphertextOnPKList = bcpUserModelOnPK.getCiphertextList();

			// 加盲后的，用于取盲
			List<BcpBlindCiphertext> blindCiphertextOnHList = blindCiphertextList.get(i);

			// 循环为每个密文去盲
			ArrayList<BcpCiphertext> ciphertextOnPKList = new ArrayList<BcpCiphertext>();
			for (int j = 0; j < blindCiphertextOnPKList.size(); j++) {
				// 用PK加密的加盲密文
				BcpCiphertext blindCiphertextOnPK = blindCiphertextOnPKList.get(j);

				// 用户各自公钥加密的加盲密文
				BcpBlindCiphertext blindCiphertextOnH = blindCiphertextOnHList.get(j);
				BigInteger blindness = blindCiphertextOnH.getBlindness();// 盲

				// 解盲后的用PK加密的用户密文
				BcpCiphertext ciphertextOnPK = BCP4C.removeKeyProdBlind(N, g, PK, blindness, blindCiphertextOnPK);

				ciphertextOnPKList.add(ciphertextOnPK);
			}

			// 去盲完毕
			bcpUserModelOnPK.setCiphertextList(ciphertextOnPKList);
		}

		return retUserModelList;
	}

	/**
	 * 结果密文转换，加盲去盲方法还是用s的
	 * 
	 * @param bcpTask
	 * @return
	 */
	public List<BcpUserModel> transDec(BcpTask bcpTask, BcpCommunicateModel transDecModel) {
		// 获取基本信息
		BigInteger PK = transDecModel.getPK();
		BigInteger N = new BigInteger(bcpTask.getTaskN());
		BigInteger g = new BigInteger(bcpTask.getTaskG());
		List<? extends BcpCiphertext> result = transDecModel.getResult();

		String sHost = sProperties.getsHost();
		String sPort = sProperties.getsPort();
		String sOrigin = "http://" + sHost + ":" + sPort;

		// 设置必需参数
		PP pp = new PP(N, null, null);
		transDecModel.setPp(pp);

		// 1.把计算结果加盲
		List<BcpBlindCiphertext> blindResult = BCP4C.keyProdBlind(N, g, PK, result);
		transDecModel.setResult(blindResult);// 设置为加盲后的结果

		// 2.发送加盲计算结果
		RestTemplate rest = new RestTemplate();
		// 取回转换成PK加密的用户密文
		BcpCommunicateModel resultModel = rest.postForObject(sOrigin + "/bcp/transDec", transDecModel,
				BcpCommunicateModel.class);
		List<BcpUserModel> resultUserModelList = resultModel.getUserModelList();

		// 3.计算结果去盲
		// 3.去盲
		for (int i = 0; i < resultUserModelList.size(); i++) {
			BcpUserModel resultUserModel = resultUserModelList.get(i);
			// 用用户各自公钥加密的计算结果
			List<? extends BcpCiphertext> blindResultOnHList = resultUserModel.getCiphertextList();
			BigInteger h = resultUserModel.getH();

			// 循环为每个密文去盲
			ArrayList<BcpCiphertext> resultOnHList = new ArrayList<BcpCiphertext>();
			for (int j = 0; j < blindResultOnHList.size(); j++) {
				// 用H加密的加盲结果
				BcpCiphertext blindResultOnH = blindResultOnHList.get(j);

				// 用户各自公钥加密的加盲密文
				BcpBlindCiphertext blindResultOnPK = blindResult.get(j);// 别弄成i
				BigInteger blindness = blindResultOnPK.getBlindness();// 盲

				// 解盲后的用PK加密的用户密文
				BcpCiphertext resultOnH = BCP4C.removeKeyProdBlind(N, g, h, blindness, blindResultOnH);

				resultOnHList.add(resultOnH);
			}

			// 设置用户数量
			resultUserModel.setUserCount(resultUserModelList.size());

			// 去盲完毕
			resultUserModel.setCiphertextList(resultOnHList);
		}

		return resultUserModelList;
	}

	/**
	 * 将用户数据入库，支持更新
	 * 
	 * @param userModel
	 * @return
	 */
	public void insertBcpTaskCiphertext(BcpTask bcpTask, BcpUserModel userModel) {
		// 需要获取的信息
		List<? extends BcpCiphertext> ciphertextList = userModel.getCiphertextList();
		Long taskId = userModel.getTaskId();
		BigInteger h = userModel.getH();
		String userId = userModel.getUserId();
		if (StringUtils.isBlank(userId)) {
			// 非空判断
			userId = UserServiceImpl.getCurrentLoginUserId();
		}

		// 获取当前任务信息
		if (bcpTask == null) {
			bcpTask = bcpTaskMapper.selectByPrimaryKey(taskId.toString());
		}
		BigDecimal currentRound = bcpTask.getCurrentRound();

		// 1.先删掉该用户之前提交数据
		BcpTaskCiphertext delCiphertextModel = new BcpTaskCiphertext();
		delCiphertextModel.setTaskId(taskId);
		delCiphertextModel.setTaskUserId(userId);
		delCiphertextModel.setTaskRound(currentRound);
		bcpTaskCiphertextMapper.delete(delCiphertextModel);

		// 2.新增本次数据
		if (CollectionUtils.isNotEmpty(ciphertextList)) {
			for (int i = 0; i < ciphertextList.size(); i++) {
				BcpCiphertext bcpCiphertext = ciphertextList.get(i);

				// 设置本次密文内容
				BcpTaskCiphertext bcpTaskCiphertext = new BcpTaskCiphertext();
				bcpTaskCiphertext.setTaskId(taskId);
				bcpTaskCiphertext.setTaskUserId(userId);
				bcpTaskCiphertext.setTaskRound(currentRound);
				bcpTaskCiphertext.setCiphertextOrder(new BigDecimal(i + 1));
				// 密文信息
				bcpTaskCiphertext.setCiphertextA(bcpCiphertext.getA().toString());
				bcpTaskCiphertext.setCiphertextB(bcpCiphertext.getB().toString());
				bcpTaskCiphertext.setCiphertextH(h.toString());

				// 新增
				bcpTaskCiphertextMapper.insertSelective(bcpTaskCiphertext);
			}
		}
	}

	/**
	 * 结果入库
	 * 
	 * @param userModel
	 */
	public void insertBcpTaskResult(BcpTask bcpTask, BcpUserModel userModel) {
		// 需要获取的信息
		List<? extends BcpCiphertext> resultList = userModel.getCiphertextList();
		Long taskId = userModel.getTaskId();
		BigInteger h = userModel.getH();
		String userId = userModel.getUserId();
		Integer userCount = userModel.getUserCount();
		if (StringUtils.isBlank(userId)) {
			// 非空判断
			userId = UserServiceImpl.getCurrentLoginUserId();
		}
		if (bcpTask == null) {
			bcpTask = bcpTaskMapper.selectByPrimaryKey(taskId.toString());
		}
		BigDecimal currentRound = bcpTask.getCurrentRound();
		if (taskId == null) {
			taskId = bcpTask.getTaskId();
		}

		// 1.先删掉该用户本轮之前已有的数据
		BcpTaskResult delTaskResult = new BcpTaskResult();
		delTaskResult.setTaskId(taskId);
		delTaskResult.setTaskUserId(userId);
		delTaskResult.setTaskRound(currentRound);
		bcpTaskResultMapper.delete(delTaskResult);

		// 2.新增本次数据
		if (CollectionUtils.isNotEmpty(resultList)) {
			for (int i = 0; i < resultList.size(); i++) {
				BcpCiphertext result = resultList.get(i);

				// 设置本次密文内容
				BcpTaskResult insTaskResult = new BcpTaskResult();
				insTaskResult.setTaskId(taskId);
				insTaskResult.setTaskUserId(userId);
				insTaskResult.setTaskRound(currentRound);
				insTaskResult.setResultOrder(new BigDecimal(i + 1));
				// 结果信息
				insTaskResult.setResultA(result.getA().toString());
				insTaskResult.setResultB(result.getB().toString());
				insTaskResult.setResultH(h.toString());
				// 用户数量
				insTaskResult.setTaskUserCount(new BigDecimal(userCount));

				// 新增
				bcpTaskResultMapper.insertSelective(insTaskResult);
			}
		}
	}

	/**
	 * 批量插入
	 * 
	 * @param bcpTask
	 * @param userModelList
	 */
	public void insertBcpTaskResult(BcpTask bcpTask, List<BcpUserModel> userModelList) {
		for (BcpUserModel userModel : userModelList) {
			insertBcpTaskResult(bcpTask, userModel);
		}
	}

	/**
	 * 某用户主动结束任务 ws环境应手动传递用户名
	 * 
	 * @param task
	 * 
	 */
	public void finishBcpTask(BcpTask task) {
		Long taskId = task.getTaskId();
		String finishUserId = task.getFinishUser();

		BcpTask finishTask = new BcpTask();
		finishTask.setTaskId(taskId);
		finishTask.setTaskState(new BigDecimal(2));
		finishTask.setFinishTime(new Date());
		finishTask.setFinishUser(finishUserId != null ? finishUserId : UserServiceImpl.getCurrentLoginUserId());
		bcpTaskMapper.updateByPrimaryKeySelective(finishTask);
	}

	/**
	 * 创建任务
	 * 
	 * @param bcpTask
	 * @return
	 */
	// 不需要因发请求网络错误而回滚，不知道为啥私自吃掉异常不抛出事务提交错误了，可能是spring版本升级解决了
	// @Transactional(noRollbackFor = ResourceAccessException.class)
	public Object createBcpTask(BcpTask bcpTask) {
		// 1.设置基础信息，id数据库自动生成创建时不传
		String currentLoginUserId = UserServiceImpl.getCurrentLoginUserId();
		bcpTask.setCurrentRound(BigDecimal.ONE);// 默认第1轮为开始
		bcpTask.setCreateUser(currentLoginUserId);
		bcpTask.setCreateTime(new Date());
		bcpTask.setTaskState(BigDecimal.ZERO);
		bcpTask.setTaskId(null);// 主键要自增，前台传的置为空
		if (bcpTask.getTaskKappa() == null) {
			// 默认kappa和certainty
			bcpTask.setTaskKappa(new BigDecimal(CryptoConstant.DEFAULT_BCP_KAPPA));
		}
		if (bcpTask.getTaskCertainty() == null) {
			bcpTask.setTaskCertainty(new BigDecimal(CryptoConstant.DEFAULT_BCP_CERTAINTY));
		}

		// 2.设置pp参数，若请求错误则不设置
		PP pp = createBcpParam(bcpTask);
		if (BCP4C.isValidPP(pp)) {
			bcpTask.setTaskN(pp.getN().toString());
			bcpTask.setTaskK(pp.getK().toString());
			bcpTask.setTaskG(pp.getG().toString());
		}
		bcpTaskMapper.insertSelective(bcpTask);
		Long taskId = bcpTask.getTaskId();// 获取自增后取回的主键id

		// 3.设置被邀请人信息
		insBcpTaskUser(taskId, currentLoginUserId, bcpTask.getTaskUserList());

		RetModel retModel = new RetModel();
		retModel.setRetCode(0);
		retModel.setRetMess("create success");
		retModel.setRetValue(bcpTask);
		return retModel;
	}

	/**
	 * 修改任务属性
	 * 
	 * @param bcpTask
	 * @param setPP
	 *            若设置则重新设置pp
	 * @return
	 */
	public Object updateBcpTask(BcpTask bcpTask, boolean setPP) {
		RetModel retModel = new RetModel();
		Long taskId = bcpTask.getTaskId();
		if (taskId == null) {
			retModel.setRetCode(-1);
			retModel.setRetMess("Task Id can't be null");
			retModel.setRetValue(bcpTask);
			return retModel;
		}
		BcpTask selTask = bcpTaskMapper.selectByPrimaryKey(taskId.toString());
		if (!BigDecimal.ZERO.equals(selTask.getTaskState())) {
			// 若任务不是未开始，则不能修改
			retModel.setRetCode(-1);
			retModel.setRetMess("The starting task cannot be modified");
			retModel.setRetValue(selTask);
			return retModel;
		}

		// 1.若未开始则可以修改
		String currentLoginUserId = UserServiceImpl.getCurrentLoginUserId();
		bcpTask.setUpdateUser(currentLoginUserId);
		bcpTask.setUpdateTime(new Date());
		bcpTask.setCurrentRound(BigDecimal.ONE);// 默认第1轮为开始

		// 2.根据条件是否设置pp参数
		if (setPP || (selTask.getTaskN() == null || selTask.getTaskK() == null || selTask.getTaskG() == null)) {
			// 1.setPP要求设置2.数据库中pp未设置3.数据库中pp不合法 时会强行设置pp
			PP pp = createBcpParam(bcpTask);
			if (BCP4C.isValidPP(pp)) {
				// 若pp内容有效则更新任务
				bcpTask.setTaskN(pp.getN().toString());
				bcpTask.setTaskK(pp.getK().toString());
				bcpTask.setTaskG(pp.getG().toString());
			}
		} else {
			// 若不重置pp则不修改bcp参数
			bcpTask.setTaskKappa(null);
			bcpTask.setTaskCertainty(null);
		}
		bcpTaskMapper.updateByPrimaryKeySelective(bcpTask);

		// 3.设置被邀请人信息
		insBcpTaskUser(taskId, currentLoginUserId, bcpTask.getTaskUserList());

		retModel.setRetCode(0);
		retModel.setRetMess("update success");
		retModel.setRetValue(bcpTask);
		return retModel;
	}

	/**
	 * 封装和S交互获取pp的操作
	 * 
	 * @param bcpTask
	 * @return
	 */
	public PP createBcpParam(BcpTask bcpTask) {
		// 远程服务器信息
		String sHost = sProperties.getsHost();
		String sPort = sProperties.getsPort();
		String sOrigin = "http://" + sHost + ":" + sPort;

		// 需要的参数
		BigDecimal kappa = bcpTask.getTaskKappa();
		BigDecimal certainty = bcpTask.getTaskCertainty();

		// 获取pp
		PP pp = createBcpParam(sOrigin, kappa.intValue(), certainty.intValue());

		// 暂不回显设置
		// bcpTask.setTaskN(pp.getN().toString());
		// bcpTask.setTaskK(pp.getK().toString());
		// bcpTask.setTaskG(pp.getG().toString());
		return pp;
	}

	/**
	 * 封装获取PP参数
	 * 
	 * @param sOrigin
	 * @param kappa
	 * @param certainty
	 * @return
	 */
	public static PP createBcpParam(String sOrigin, Integer kappa, Integer certainty) {
		// 发请求，若错误则返回null
		PP pp = null;
		try {
			// 发送请求获取pp参数
			RestTemplate rest = new RestTemplate();
			pp = rest.postForObject(sOrigin + "/bcp/create?kappa={1}&certainty={2}", null, PP.class, kappa, certainty);
		} catch (Exception e) {
			// 连接超时是ResourceAccessException
			e.printStackTrace();
		}
		return pp;
	}

	/**
	 * 设置返回用户信息
	 * 
	 * @param bcpTaskList
	 * @return
	 */
	public void setTaskUserList(List<BcpTask> bcpTaskList) {
		bcpTaskList.forEach((bcpTask) -> {
			Long taskId = bcpTask.getTaskId();
			BcpTaskUser selTaskUser = new BcpTaskUser();
			selTaskUser.setTaskId(taskId);
			List<BcpTaskUser> bcpTaskUserList = bcpTaskUserMapper.select(selTaskUser);
			bcpTask.setTaskUserList(bcpTaskUserList);
		});
	}

	/**
	 * 查询指定用户参与的任务 不传则查询全部taskId
	 * 
	 * @param userId
	 *            对应的用户参与的任务
	 * @param unfinished
	 *            在查询指定用户的前提下，是否只查询未完成的任务
	 * @return
	 */
	public List<BcpTask> getTaskList(String userId, boolean unfinished) {
		// 需要返回的任务列表
		List<BcpTask> taskList = new ArrayList<>();

		// 查询全部参与者并映射到每个任务
		List<BcpTaskUser> taskUserList = null;

		if (userId != null) {
			// 若传userid，则只查询该用户参与的任务
			BcpTaskUser selTaskUser = new BcpTaskUser();
			selTaskUser.setTaskUserId(userId);
			List<BcpTaskUser> involvedTaskUserList = bcpTaskUserMapper.select(selTaskUser);
			if (CollectionUtils.isEmpty(involvedTaskUserList)) {
				// 若没有有效任务id
				return taskList;
			}
			// 获得去重后的参与任务id集合
			Set<Long> involvedTaskIdSet = involvedTaskUserList.stream().map(BcpTaskUser::getTaskId)
					.collect(Collectors.toSet());

			// 查询参与的任务明细
			Example taskExample = new Example(BcpTask.class);
			Criteria taskCriteria = taskExample.createCriteria();
			taskCriteria.andIn("taskId", involvedTaskIdSet);
			if (unfinished) {
				// 若查询未完成的，则去掉已完成的
				taskCriteria.andNotEqualTo("taskState", 2);
			}
			taskList = bcpTaskMapper.selectByExample(taskExample);
			if (CollectionUtils.isEmpty(taskList)) {
				// 若没有有效任务
				return taskList;
			}
			if (unfinished) {
				// 若查询未完成任务，则更新任务id列表，去除已完成任务
				involvedTaskIdSet = taskList.stream().map(BcpTask::getTaskId).collect(Collectors.toSet());
			}

			// 查询指定任务的参与者
			Example taskUserExample = new Example(BcpTaskUser.class);
			Criteria taskUserCriteria = taskUserExample.createCriteria();
			taskUserCriteria.andIn("taskId", involvedTaskIdSet);
			taskUserList = bcpTaskUserMapper.selectByExample(taskUserExample);
		} else {
			// 查询全部任务
			taskList = bcpTaskMapper.selectAll();
			// 查询全部参与者
			taskUserList = bcpTaskUserMapper.selectAll();
		}

		// taskId -> 任务参与者
		Map<Long, List<BcpTaskUser>> taskId2TaskUserMap = new HashMap<>();
		taskUserList.forEach((taskUser) -> {
			Long taskId = taskUser.getTaskId();
			List<BcpTaskUser> subTaskUserList = taskId2TaskUserMap.get(taskId);
			if (subTaskUserList == null) {
				subTaskUserList = new ArrayList<>();
				taskId2TaskUserMap.put(taskId, subTaskUserList);
			}
			subTaskUserList.add(taskUser);
		});

		// 为每个任务设置参与者列表
		taskList.forEach((task) -> {
			List<BcpTaskUser> subTaskUserList = taskId2TaskUserMap.get(task.getTaskId());
			task.setTaskUserList(subTaskUserList);
		});

		return taskList;
	}

	/**
	 * 提出创建bcp任务时新增邀请人的方法
	 * 
	 * @param taskId
	 * @param currentLoginUserId
	 * @param taskUserList
	 */
	public void insBcpTaskUser(Long taskId, String currentLoginUserId, List<BcpTaskUser> taskUserList) {
		if (CollectionUtils.isNotEmpty(taskUserList)) {
			// 获取用户id集合，若不包含当前登录用户则加上
			Set<String> taskUserIdSet = taskUserList.stream().map(BcpTaskUser::getTaskUserId)
					.collect(Collectors.toSet());
			// 加上会自动去重，直接加
			taskUserIdSet.add(currentLoginUserId);

			// 若设置了新的参与人则用新参与人，若没设置则不修改参与人信息
			BcpTaskUser delTaskUser = new BcpTaskUser();
			delTaskUser.setTaskId(taskId);
			bcpTaskUserMapper.delete(delTaskUser);

			// 用set先删后增
			BcpTaskUser insTaskUser = new BcpTaskUser();
			insTaskUser.setTaskId(taskId);
			taskUserIdSet.forEach((taskUserId) -> {
				insTaskUser.setTaskUserId(taskUserId);
				bcpTaskUserMapper.insert(insTaskUser);
			});
		}
	}

	/**
	 * 
	 * @param taskIdCollection
	 * @param userId
	 * @param round
	 *            若为null，则查询最新轮计算结果
	 * @return
	 */
	public List<BcpUserModel> getDesignatedOrLatestResult(Collection<Long> taskIdCollection, String userId,
			Integer round, boolean isLatest) {
		// 1.查询结果
		List<BcpTaskResult> taskResultList = bcpTaskResultMapper.getDesignatedOrLatestResult(taskIdCollection, userId,
				round, isLatest);

		// 2.转换结果为BcpUserModel类型
		// userId-> (taskId->result)
		Map<String, Map<Long, BcpUserModelExt>> resultMap = new HashMap<>();
		for (BcpTaskResult result : taskResultList) {
			String bcpTaskUserId = result.getTaskUserId();
			Long bcpTaskId = result.getTaskId();
			Integer resultOrder = result.getResultOrder().intValue();// order是主键应该不会空

			Map<Long, BcpUserModelExt> bcpUserTaskMap = resultMap.get(bcpTaskUserId);
			if (bcpUserTaskMap == null) {
				bcpUserTaskMap = new HashMap<>();
				resultMap.put(bcpTaskUserId, bcpUserTaskMap);
			}

			BcpUserModelExt bcpUserModelExt = bcpUserTaskMap.get(bcpTaskId);
			if (bcpUserModelExt == null) {
				// 设置初始化信息，不初始化ciphertextList，设置完order2CiphertextMap后统一设置
				bcpUserModelExt = new BcpUserModelExt();
				bcpUserModelExt.setTaskId(bcpTaskId);
				bcpUserModelExt.setUserId(bcpTaskUserId);
				bcpUserModelExt.setH(result.getResultH() != null ? new BigInteger(result.getResultH()) : null);
				bcpUserModelExt
						.setUserCount(result.getTaskUserCount() != null ? result.getTaskUserCount().intValue() : null);
				bcpUserModelExt.setRound(result.getTaskRound() != null ? result.getTaskRound().intValue() : null);
				// bcpUserModelExt.setOrder2CiphertextMap(new TreeMap<>());
				bcpUserTaskMap.put(bcpTaskId, bcpUserModelExt);
			}

			Map<Integer, BcpCiphertext> order2CiphertextMap = bcpUserModelExt.getOrder2CiphertextMap();
			if (order2CiphertextMap == null) {
				order2CiphertextMap = new TreeMap<>();
				bcpUserModelExt.setOrder2CiphertextMap(order2CiphertextMap);
			}

			// 追加密文
			BcpCiphertext bcpCiphertext = order2CiphertextMap.get(resultOrder);
			if (bcpCiphertext == null) {
				bcpCiphertext = new BcpCiphertext();
				bcpCiphertext.setA(result.getResultA() != null ? new BigInteger(result.getResultA()) : null);
				bcpCiphertext.setB(result.getResultB() != null ? new BigInteger(result.getResultB()) : null);
				order2CiphertextMap.put(resultOrder, bcpCiphertext);
			}
		}

		// 3.最终平铺
		List<BcpUserModel> resultList = new ArrayList<>();
		for (Entry<String, Map<Long, BcpUserModelExt>> userEntry : resultMap.entrySet()) {
			for (Entry<Long, BcpUserModelExt> taskEntry : userEntry.getValue().entrySet()) {
				BcpUserModelExt modelExt = taskEntry.getValue();
				Map<Integer, BcpCiphertext> order2CiphertextMap = modelExt.getOrder2CiphertextMap();
				if (order2CiphertextMap != null) {
					// 转换密文为list
					modelExt.setCiphertextList(new ArrayList<>(order2CiphertextMap.values()));
				}
				resultList.add(modelExt);
			}
		}

		return resultList;
	}

}
