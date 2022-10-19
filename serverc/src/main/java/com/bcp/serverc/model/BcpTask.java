package com.bcp.serverc.model;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.persistence.*;

import io.swagger.annotations.ApiModelProperty;
import tk.mybatis.mapper.annotation.KeySql;

@Table(name = "bcp_task")
public class BcpTask {
    /**
     * 任务id
     */
    @Id
    @Column(name = "task_id")
    @KeySql(useGeneratedKeys = true)
    @ApiModelProperty(hidden = true)
    private Long taskId;

    /**
     * 任务名称
     */
    @Column(name = "task_name")
    private String taskName;

    /**
     * 任务状态(0:未开始;1:已开始;2:已完成;3:已退回)
     */
    @Column(name = "task_state")
    @ApiModelProperty(hidden = true)
    private BigDecimal taskState;

    /**
     * 最大交互轮数,若为null则无限轮,无限的情况下,有一个客户端发出收敛信号,即代表全体收敛
     */
    @Column(name = "compute_rounds")
    private Integer computeRounds;

    /**
     * 该任务当前交互轮数
     */
    @Column(name = "current_round")
    @ApiModelProperty(hidden = true)
    private Integer currentRound;

    /**
     * 客户端浮点参数支持的最大精度,计算时客户端将作为参数的浮点张量中的所有元素统一乘以10的最大精度次方变为整数后才能加密
     */
    @Column(name = "param_precision")
    private BigDecimal paramPrecision;

    /**
     * 创建人username
     */
    @Column(name = "create_user")
    @ApiModelProperty(hidden = true)
    private Long createUser;

    /**
     * 最后更新人username
     */
    @Column(name = "update_user")
    @ApiModelProperty(hidden = true)
    private Long updateUser;

    /**
     * 开始任务人username
     */
    @Column(name = "start_user")
    @ApiModelProperty(hidden = true)
    private Long startUser;

    /**
     * 结束任务人id
     */
    @Column(name = "finish_user")
    @ApiModelProperty(hidden = true)
    private Long finishUser;

    /**
     * 创建时间
     */
    @Column(name = "create_time")
    @ApiModelProperty(hidden = true)
    private Date createTime;

    /**
     * 最后修改时间
     */
    @Column(name = "update_time")
    @ApiModelProperty(hidden = true)
    private Date updateTime;

    /**
     * 开始时间
     */
    @Column(name = "start_time")
    @ApiModelProperty(hidden = true)
    private Date startTime;

    /**
     * 完成时间
     */
    @Column(name = "finish_time")
    @ApiModelProperty(hidden = true)
    private Date finishTime;

    /**
     * 结束原因
     */
    @Column(name = "finish_reason")
    @ApiModelProperty(hidden = true)
    private String finishReason;

    /**
     * 本次任务使用的kappa
     */
    @Column(name = "task_kappa")
    private BigDecimal taskKappa;

    /**
     * 本次任务使用的certainty
     */
    @Column(name = "task_certainty")
    private BigDecimal taskCertainty;

    /**
     * 本次任务使用的N,在任务开始后才能获得
     */
    @Column(name = "task_n")
    @ApiModelProperty(hidden = true)
    private String taskN;

    /**
     * 本次任务使用的k,在任务开始后才能获得
     */
    @Column(name = "task_k")
    @ApiModelProperty(hidden = true)
    private String taskK;

    /**
     * 本次任务使用的g,在任务开始后才能获得
     */
    @Column(name = "task_g")
    @ApiModelProperty(hidden = true)
    private String taskG;

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    /**
     * 参与者列表
     */
    @Transient
    private List<BcpTaskUser> taskUserList;

    @Column(name = "model_name")
    private String modelName;

    /**
     * 获取任务id
     *
     * @return task_id - 任务id
     */
    public Long getTaskId() {
        return taskId;
    }

    /**
     * 设置任务id
     *
     * @param taskId 任务id
     */
    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    /**
     * 获取任务名称
     *
     * @return task_name - 任务名称
     */
    public String getTaskName() {
        return taskName;
    }

    /**
     * 设置任务名称
     *
     * @param taskName 任务名称
     */
    public void setTaskName(String taskName) {
        this.taskName = taskName == null ? null : taskName.trim();
    }

    /**
     * 获取任务状态(0:未开始;1:已开始;2:已完成;3:已退回)
     *
     * @return task_state - 任务状态(0:未开始;1:已开始;2:已完成;3:已退回)
     */
    public BigDecimal getTaskState() {
        return taskState;
    }

    /**
     * 设置任务状态(0:未开始;1:已开始;2:已完成;3:已退回)
     *
     * @param taskState 任务状态(0:未开始;1:已开始;2:已完成;3:已退回)
     */
    public void setTaskState(BigDecimal taskState) {
        this.taskState = taskState;
    }

    /**
     * 获取最大交互轮数,若为null则无限轮,无限的情况下,有一个客户端发出收敛信号,即代表全体收敛
     *
     * @return compute_rounds - 最大交互轮数,若为null则无限轮,无限的情况下,有一个客户端发出收敛信号,即代表全体收敛
     */
    public Integer getComputeRounds() {
        return computeRounds;
    }

    /**
     * 设置最大交互轮数,若为null则无限轮,无限的情况下,有一个客户端发出收敛信号,即代表全体收敛
     *
     * @param computeRounds 最大交互轮数,若为null则无限轮,无限的情况下,有一个客户端发出收敛信号,即代表全体收敛
     */
    public void setComputeRounds(Integer computeRounds) {
        this.computeRounds = computeRounds;
    }

    /**
     * 获取该任务当前交互轮数
     *
     * @return current_round - 该任务当前交互轮数
     */
    public Integer getCurrentRound() {
        return currentRound;
    }

    /**
     * 设置该任务当前交互轮数
     *
     * @param currentRound 该任务当前交互轮数
     */
    public void setCurrentRound(Integer currentRound) {
        this.currentRound = currentRound;
    }

    /**
     * 获取客户端浮点参数支持的最大精度,计算时客户端将作为参数的浮点张量中的所有元素统一乘以10的最大精度次方变为整数后才能加密
     *
     * @return param_precision - 客户端浮点参数支持的最大精度,计算时客户端将作为参数的浮点张量中的所有元素统一乘以10的最大精度次方变为整数后才能加密
     */
    public BigDecimal getParamPrecision() {
        return paramPrecision;
    }

    /**
     * 设置客户端浮点参数支持的最大精度,计算时客户端将作为参数的浮点张量中的所有元素统一乘以10的最大精度次方变为整数后才能加密
     *
     * @param paramPrecision 客户端浮点参数支持的最大精度,计算时客户端将作为参数的浮点张量中的所有元素统一乘以10的最大精度次方变为整数后才能加密
     */
    public void setParamPrecision(BigDecimal paramPrecision) {
        this.paramPrecision = paramPrecision;
    }

    /**
     * 获取创建人id
     *
     * @return create_user - 创建人id
     */
    public Long getCreateUser() {
        return createUser;
    }

    /**
     * 设置创建人id
     *
     * @param createUser 创建人id
     */
    public void setCreateUser(Long createUser) {
        this.createUser = createUser;
    }

    /**
     * 获取最后更新人id
     *
     * @return update_user - 最后更新人id
     */
    public Long getUpdateUser() {
        return updateUser;
    }

    /**
     * 设置最后更新人id
     *
     * @param updateUser 最后更新人id
     */
    public void setUpdateUser(Long updateUser) {
        this.updateUser = updateUser;
    }

    /**
     * 获取开始任务人id
     *
     * @return start_user - 开始任务人id
     */
    public Long getStartUser() {
        return startUser;
    }

    /**
     * 设置开始任务人id
     *
     * @param startUser 开始任务人id
     */
    public void setStartUser(Long startUser) {
        this.startUser = startUser;
    }

    /**
     * 获取结束任务人id
     *
     * @return finish_user - 结束任务人id
     */
    public Long getFinishUser() {
        return finishUser;
    }

    /**
     * 设置结束任务人id
     *
     * @param finishUser 结束任务人id
     */
    public void setFinishUser(Long finishUser) {
        this.finishUser = finishUser;
    }

    /**
     * 获取创建时间
     *
     * @return create_time - 创建时间
     */
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * 设置创建时间
     *
     * @param createTime 创建时间
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    /**
     * 获取最后修改时间
     *
     * @return update_time - 最后修改时间
     */
    public Date getUpdateTime() {
        return updateTime;
    }

    /**
     * 设置最后修改时间
     *
     * @param updateTime 最后修改时间
     */
    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    /**
     * 获取开始时间
     *
     * @return start_time - 开始时间
     */
    public Date getStartTime() {
        return startTime;
    }

    /**
     * 设置开始时间
     *
     * @param startTime 开始时间
     */
    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    /**
     * 获取完成时间
     *
     * @return finish_time - 完成时间
     */
    public Date getFinishTime() {
        return finishTime;
    }

    /**
     * 设置完成时间
     *
     * @param finishTime 完成时间
     */
    public void setFinishTime(Date finishTime) {
        this.finishTime = finishTime;
    }

    /**
     * 获取结束原因
     *
     * @return finish_reason - 结束原因
     */
    public String getFinishReason() {
        return finishReason;
    }

    /**
     * 设置结束原因
     *
     * @param finishReason 结束原因
     */
    public void setFinishReason(String finishReason) {
        this.finishReason = finishReason == null ? null : finishReason.trim();
    }

    /**
     * 获取本次任务使用的kappa
     *
     * @return task_kappa - 本次任务使用的kappa
     */
    public BigDecimal getTaskKappa() {
        return taskKappa;
    }

    /**
     * 设置本次任务使用的kappa
     *
     * @param taskKappa 本次任务使用的kappa
     */
    public void setTaskKappa(BigDecimal taskKappa) {
        this.taskKappa = taskKappa;
    }

    /**
     * 获取本次任务使用的certainty
     *
     * @return task_certainty - 本次任务使用的certainty
     */
    public BigDecimal getTaskCertainty() {
        return taskCertainty;
    }

    /**
     * 设置本次任务使用的certainty
     *
     * @param taskCertainty 本次任务使用的certainty
     */
    public void setTaskCertainty(BigDecimal taskCertainty) {
        this.taskCertainty = taskCertainty;
    }

    /**
     * 获取本次任务使用的N,在任务开始后才能获得
     *
     * @return task_n - 本次任务使用的N,在任务开始后才能获得
     */
    public String getTaskN() {
        return taskN;
    }

    /**
     * 设置本次任务使用的N,在任务开始后才能获得
     *
     * @param taskN 本次任务使用的N,在任务开始后才能获得
     */
    public void setTaskN(String taskN) {
        this.taskN = taskN;
    }

    /**
     * 获取本次任务使用的k,在任务开始后才能获得
     *
     * @return task_k - 本次任务使用的k,在任务开始后才能获得
     */
    public String getTaskK() {
        return taskK;
    }

    /**
     * 设置本次任务使用的k,在任务开始后才能获得
     *
     * @param taskK 本次任务使用的k,在任务开始后才能获得
     */
    public void setTaskK(String taskK) {
        this.taskK = taskK;
    }

    /**
     * 获取本次任务使用的g,在任务开始后才能获得
     *
     * @return task_g - 本次任务使用的g,在任务开始后才能获得
     */
    public String getTaskG() {
        return taskG;
    }

    /**
     * 设置本次任务使用的g,在任务开始后才能获得
     *
     * @param taskG 本次任务使用的g,在任务开始后才能获得
     */
    public void setTaskG(String taskG) {
        this.taskG = taskG;
    }

	public List<BcpTaskUser> getTaskUserList() {
		return taskUserList;
	}

	public void setTaskUserList(List<BcpTaskUser> taskUserList) {
		this.taskUserList = taskUserList;
	}

    @Override
    public String toString() {
        return "BcpTask{" +
                "taskId=" + taskId +
                ", taskName='" + taskName + '\'' +
                ", taskState=" + taskState +
                ", computeRounds=" + computeRounds +
                ", currentRound=" + currentRound +
                ", paramPrecision=" + paramPrecision +
                ", createUser='" + createUser + '\'' +
                ", updateUser='" + updateUser + '\'' +
                ", startUser='" + startUser + '\'' +
                ", finishUser='" + finishUser + '\'' +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                ", startTime=" + startTime +
                ", finishTime=" + finishTime +
                ", finishReason='" + finishReason + '\'' +
                ", taskKappa=" + taskKappa +
                ", taskCertainty=" + taskCertainty +
                ", taskN='" + taskN + '\'' +
                ", taskK='" + taskK + '\'' +
                ", taskG='" + taskG + '\'' +
                ", taskUserList=" + taskUserList +
                '}';
    }
}