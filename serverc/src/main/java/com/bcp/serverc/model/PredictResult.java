package com.bcp.serverc.model;

import javax.persistence.*;
import java.sql.Timestamp;

@Table(name = "predict_result")
public class PredictResult {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "model_name")
    private String modelName;

    @Column(name = "create_user")
    private String createUser;

    @Column(name = "create_time")
    private Timestamp createTime;

    @Column(name = "finish_time")
    private Timestamp finishTime;

    @Column(name = "status")
    private Integer status;

    @Column(name = "predict_result")
    private String predictResult;

    @Column(name = "data_name")
    private String dataName;

    public String getDataName() {
        return dataName;
    }

    public void setDataName(String dataName) {
        this.dataName = dataName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }

    public Timestamp getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Timestamp createTime) {
        this.createTime = createTime;
    }

    public Timestamp getFinishTime() {
        return finishTime;
    }

    public void setFinishTime(Timestamp finishTime) {
        this.finishTime = finishTime;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getPredictResult() {
        return predictResult;
    }

    public void setPredictResult(String predictResult) {
        this.predictResult = predictResult;
    }

    @Override
    public String toString() {
        return "PredictResult{" +
                "id=" + id +
                ", modelName='" + modelName + '\'' +
                ", createUser='" + createUser + '\'' +
                ", createTime=" + createTime +
                ", finishTime=" + finishTime +
                ", status=" + status +
                ", predictResult='" + predictResult + '\'' +
                ", dataName='" + dataName + '\'' +
                '}';
    }
}
