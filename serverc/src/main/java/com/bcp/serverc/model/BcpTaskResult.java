package com.bcp.serverc.model;

import java.math.BigDecimal;
import javax.persistence.*;

@Table(name = "bcp_task_result")
public class BcpTaskResult {
    /**
     * 任务id
     */
    @Id
    @Column(name = "task_id")
    private Long taskId;

    /**
     * 用户id,若为默认值则是使用公共公钥PK加密的计算结果
     */
    @Id
    @Column(name = "task_user_name")
    private String taskUserName;

    /**
     * 任务轮数
     */
    @Id
    @Column(name = "task_round")
    private Integer taskRound;

    /**
     * 结果序号
     */
    @Id
    @Column(name = "result_order")
    private BigDecimal resultOrder;

    /**
     * 本次任务参与计算的用户数量,用于将结果平均
     */
    @Column(name = "task_user_count")
    private BigDecimal taskUserCount;

    /**
     * 相加结果A
     */
    @Column(name = "result_a")
    private String resultA;

    /**
     * 相加结果B
     */
    @Column(name = "result_b")
    private String resultB;

    /**
     * 加密使用的公钥
     */
    @Column(name = "result_h")
    private String resultH;

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
     * 获取用户id,若为默认值则是使用公共公钥PK加密的计算结果
     *
     * @return task_user_name - 用户username,若为默认值则是使用公共公钥PK加密的计算结果
     */
    public String getTaskUserName() {
        return taskUserName;
    }

    /**
     * 设置用户username,若为默认值则是使用公共公钥PK加密的计算结果
     *
     * @param taskUserName 用户username,若为默认值则是使用公共公钥PK加密的计算结果
     */
    public void setTaskUserName(String taskUserName) {
        this.taskUserName = taskUserName;
    }

    /**
     * 获取任务轮数
     *
     * @return task_round - 任务轮数
     */
    public Integer getTaskRound() {
        return taskRound;
    }

    /**
     * 设置任务轮数
     *
     * @param taskRound 任务轮数
     */
    public void setTaskRound(Integer taskRound) {
        this.taskRound = taskRound;
    }

    /**
     * 获取结果序号
     *
     * @return result_order - 结果序号
     */
    public BigDecimal getResultOrder() {
        return resultOrder;
    }

    /**
     * 设置结果序号
     *
     * @param resultOrder 结果序号
     */
    public void setResultOrder(BigDecimal resultOrder) {
        this.resultOrder = resultOrder;
    }

    /**
     * 获取本次任务参与计算的用户数量,用于将结果平均
     *
     * @return task_user_count - 本次任务参与计算的用户数量,用于将结果平均
     */
    public BigDecimal getTaskUserCount() {
        return taskUserCount;
    }

    /**
     * 设置本次任务参与计算的用户数量,用于将结果平均
     *
     * @param taskUserCount 本次任务参与计算的用户数量,用于将结果平均
     */
    public void setTaskUserCount(BigDecimal taskUserCount) {
        this.taskUserCount = taskUserCount;
    }

    /**
     * 获取相加结果A
     *
     * @return result_a - 相加结果A
     */
    public String getResultA() {
        return resultA;
    }

    /**
     * 设置相加结果A
     *
     * @param resultA 相加结果A
     */
    public void setResultA(String resultA) {
        this.resultA = resultA;
    }

    /**
     * 获取相加结果B
     *
     * @return result_b - 相加结果B
     */
    public String getResultB() {
        return resultB;
    }

    /**
     * 设置相加结果B
     *
     * @param resultB 相加结果B
     */
    public void setResultB(String resultB) {
        this.resultB = resultB;
    }

    /**
     * 获取加密使用的公钥
     *
     * @return result_h - 加密使用的公钥
     */
    public String getResultH() {
        return resultH;
    }

    /**
     * 设置加密使用的公钥
     *
     * @param resultH 加密使用的公钥
     */
    public void setResultH(String resultH) {
        this.resultH = resultH;
    }
}