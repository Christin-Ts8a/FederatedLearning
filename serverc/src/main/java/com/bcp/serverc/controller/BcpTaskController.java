package com.bcp.serverc.controller;

import java.util.Arrays;

import com.bcp.general.util.JsonResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bcp.serverc.config.ServerProperties;
import com.bcp.serverc.model.BcpTask;
import com.bcp.general.model.BcpUserModel;
import com.bcp.serverc.service.impl.BcpTaskServiceImpl;

/**
 * 修改计算任务参数，开始任务等接口
 *
 */
@RestController
@RequestMapping("/bcpTask")
public class BcpTaskController {

	@Autowired
	BcpTaskServiceImpl bcpTaskSrv;

	@Autowired
	ServerProperties prop;

	@Autowired
	WebSocketServer ws;

	/**
	 * 查询全部任务列表 默认可查询完成任务
	 * 
	 * @return
	 */
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public Object getTaskList(String username) {
		Object ret = bcpTaskSrv.getTaskList(username, false);

		return ret;
	}

	@RequestMapping(value = "/create", method = RequestMethod.POST)
	public JsonResult createTask(@RequestBody BcpTask bcpTask) {
		Object ret = bcpTaskSrv.createBcpTask(bcpTask);
		return JsonResult.ok(ret);
	}

	@RequestMapping(value = "/update", method = RequestMethod.PATCH)
	public Object updateTask(@RequestBody BcpTask bcpTask, @RequestParam boolean setPP) {
		Object ret = bcpTaskSrv.updateBcpTask(bcpTask, setPP);

		return ret;
	}

	@RequestMapping(value = "/start", method = RequestMethod.POST)
	public Object startTask(@RequestBody BcpTask taskArg) {
		Object ret = bcpTaskSrv.startBcpTask(taskArg);

		return ret;
	}

	@RequestMapping(value = "/submitData", method = RequestMethod.POST)
	public Object submitBcpTask(@RequestBody BcpUserModel userModel) {
		bcpTaskSrv.submitBcpTask(userModel);
		return "submit success";
	}

	@RequestMapping(value = "/getResult", method = RequestMethod.GET)
	public Object getResult(@RequestParam Long taskId, @RequestParam String username, @RequestParam Integer round,
			@RequestParam boolean isLatest) {
		Object ret = bcpTaskSrv.getDesignatedOrLatestResult(Arrays.asList(taskId), username, round, isLatest);
		return ret;
	}
}
