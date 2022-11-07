package com.bcp.serverc.controller;

import java.io.IOException;
import java.net.http.HttpRequest;
import java.util.Arrays;
import java.util.Map;

import com.bcp.general.util.JsonResult;
import com.bcp.serverc.model.User;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.bcp.serverc.config.ServerProperties;
import com.bcp.serverc.model.BcpTask;
import com.bcp.general.model.BcpUserModel;
import com.bcp.serverc.service.impl.BcpTaskServiceImpl;
import springfox.documentation.spring.web.json.Json;

import javax.servlet.http.HttpServletRequest;

/**
 * 修改计算任务参数，开始任务等接口
 *
 */
@RestController
@RequestMapping("/bcpTask")
public class BcpTaskController {

	public static final Logger logger = LoggerFactory.getLogger(BcpTaskController.class);

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
	@RequestMapping(value = "/list", method = RequestMethod.POST)
	public Object getTaskList(@RequestBody JSONObject param) {
		if (!param.containsKey("pageNum")) {
			param.put("pageNum", 1);
		}
		if (!param.containsKey("pageSize")) {
			param.put("pageSize", 10);
		}
		Object ret = bcpTaskSrv.getTaskList(param);

		return ret;
	}

	@RequestMapping(value = "/create", method = RequestMethod.POST)
	public JsonResult createTask(HttpServletRequest request, @RequestBody BcpTask bcpTask) {
		logger.info("create");
		Object ret = bcpTaskSrv.createBcpTask(request, bcpTask);
		return JsonResult.ok(ret);
	}

	@RequestMapping(value = "/update", method = RequestMethod.PATCH)
	public Object updateTask(HttpServletRequest request, @RequestBody BcpTask bcpTask, @RequestParam boolean setPP) {
		Object ret = bcpTaskSrv.updateBcpTask(request, bcpTask, setPP);

		return ret;
	}

	@RequestMapping(value = "/start", method = RequestMethod.POST)
	public Object startTask(HttpServletRequest request, @RequestBody JSONObject params) {
		logger.info("start");
		params.remove("createTime");
		BcpTask taskArg = (BcpTask) JSONObject.toBean(params, BcpTask.class);
		Object ret = bcpTaskSrv.startBcpTask(request, taskArg);
		return ret;
	}

	@RequestMapping(value = "/submitData", method = RequestMethod.POST)
	public Object submitBcpTask(HttpServletRequest request, @RequestBody BcpUserModel userModel) {
		User loginUser = (User) request.getSession().getAttribute("SYSTEM_USER_SESSION");
		logger.info("submitData loginUser: " + loginUser);
		userModel.setUserId(loginUser.getUserId());
		System.out.println("loginUser.getOrgCode().toString(): "+loginUser.getOrgCode().toString());
		bcpTaskSrv.submitBcpTask(userModel);
		return "submit success";
	}

	@RequestMapping(value = "/getResult", method = RequestMethod.GET)
	public Object getResult(@RequestParam Long taskId, @RequestParam String username, @RequestParam Integer round,
			@RequestParam boolean isLatest) {
		Object ret = bcpTaskSrv.getDesignatedOrLatestResult(Arrays.asList(taskId), username, round, isLatest);
		return ret;
	}

	@PostMapping("/test")
	public JsonResult test(@RequestBody JSONObject param) throws IOException {
		bcpTaskSrv.test(param);
		return JsonResult.ok();
	}
}
