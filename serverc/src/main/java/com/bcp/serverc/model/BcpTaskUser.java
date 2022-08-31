package com.bcp.serverc.model;

import javax.persistence.*;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Table(name = "bcp_task_user")
public class BcpTaskUser {
    /**
     * 任务id
     */
    @Id
    @Column(name = "task_id")
    @ApiModelProperty(hidden = true)
    private Long taskId;

    /**
     * userid
     */
    @Id
    @Column(name = "task_user_name")
    private String taskUserName;

    /**
     * 本次任务该用户的公钥
     */
    @ApiModelProperty(hidden = true)
    private String h;

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
     * 获取userid
     *
     * @return task_user_id - userid
     */
    public String getTaskUserName() {
        return taskUserName;
    }

    /**
     * 设置userid
     *
     * @param taskUserName userid
     */
    public void setTaskUserName(String taskUserName) {
        this.taskUserName = taskUserName;
    }

    /**
     * 获取本次任务该用户的公钥
     *
     * @return h - 本次任务该用户的公钥
     */
    public String getH() {
        return h;
    }

    /**
     * 设置本次任务该用户的公钥
     *
     * @param h 本次任务该用户的公钥
     */
    public void setH(String h) {
        this.h = h;
    }

    @Override
    public String toString() {
        return "BcpTaskUser{" +
                "taskId=" + taskId +
                ", taskUserName='" + taskUserName + '\'' +
                ", h='" + h + '\'' +
                '}';
    }
}