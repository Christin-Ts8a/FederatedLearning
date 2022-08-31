package com.bcp.serverc.model;

import java.math.BigDecimal;
import javax.persistence.*;

@Table(name = "bcp_task_ciphertext")
public class BcpTaskCiphertext {
    /**
     * 任务id
     */
    @Id
    @Column(name = "task_id")
    private Long taskId;

    /**
     * 用户id
     */
    @Id
    @Column(name = "task_user_name")
    private String taskUserName;

    /**
     * 当前轮数
     */
    @Id
    @Column(name = "task_round")
    private Integer taskRound;

    /**
     * 当前序号
     */
    @Id
    @Column(name = "ciphertext_order")
    private BigDecimal ciphertextOrder;

    /**
     * 密文A
     */
    @Column(name = "ciphertext_a")
    private String ciphertextA;

    /**
     * 密文B
     */
    @Column(name = "ciphertext_b")
    private String ciphertextB;

    /**
     * 加密使用的公钥
     */
    @Column(name = "ciphertext_h")
    private String ciphertextH;

    @Column(name ="ciphertext_struct")
    private String ciphertextStruct;

    public String getCiphertextStruct() {
        return ciphertextStruct;
    }

    public void setCiphertextStruct(String ciphertextStruct) {
        this.ciphertextStruct = ciphertextStruct;
    }

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
     * 获取用户id
     *
     * @return task_user_id - 用户id
     */
    public String getTaskUserName() {
        return taskUserName;
    }

    /**
     * 设置用户id
     *
     * @param taskUserName 用户 username
     */
    public void setTaskUserName(String taskUserName) {
        this.taskUserName = taskUserName;
    }

    /**
     * 获取当前轮数
     *
     * @return task_round - 当前轮数
     */
    public Integer getTaskRound() {
        return taskRound;
    }

    /**
     * 设置当前轮数
     *
     * @param taskRound 当前轮数
     */
    public void setTaskRound(Integer taskRound) {
        this.taskRound = taskRound;
    }

    /**
     * 获取当前序号
     *
     * @return ciphertext_order - 当前序号
     */
    public BigDecimal getCiphertextOrder() {
        return ciphertextOrder;
    }

    /**
     * 设置当前序号
     *
     * @param ciphertextOrder 当前序号
     */
    public void setCiphertextOrder(BigDecimal ciphertextOrder) {
        this.ciphertextOrder = ciphertextOrder;
    }

    /**
     * 获取密文A
     *
     * @return ciphertext_a - 密文A
     */
    public String getCiphertextA() {
        return ciphertextA;
    }

    /**
     * 设置密文A
     *
     * @param ciphertextA 密文A
     */
    public void setCiphertextA(String ciphertextA) {
        this.ciphertextA = ciphertextA;
    }

    /**
     * 获取密文B
     *
     * @return ciphertext_b - 密文B
     */
    public String getCiphertextB() {
        return ciphertextB;
    }

    /**
     * 设置密文B
     *
     * @param ciphertextB 密文B
     */
    public void setCiphertextB(String ciphertextB) {
        this.ciphertextB = ciphertextB;
    }

    /**
     * 获取加密使用的公钥
     *
     * @return ciphertext_h - 加密使用的公钥
     */
    public String getCiphertextH() {
        return ciphertextH;
    }

    @Override
    public String toString() {
        return "BcpTaskCiphertext{" +
                "taskId=" + taskId +
                ", taskUserName='" + taskUserName + '\'' +
                ", taskRound=" + taskRound +
                ", ciphertextOrder=" + ciphertextOrder +
                ", ciphertextA='" + ciphertextA + '\'' +
                ", ciphertextB='" + ciphertextB + '\'' +
                ", ciphertextH='" + ciphertextH + '\'' +
                ", ciphertextStruct='" + ciphertextStruct + '\'' +
                '}';
    }

    /**
     * 设置加密使用的公钥
     *
     * @param ciphertextH 加密使用的公钥
     */
    public void setCiphertextH(String ciphertextH) {

        this.ciphertextH = ciphertextH;
    }
}