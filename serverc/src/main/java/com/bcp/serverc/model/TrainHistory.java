package com.bcp.serverc.model;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Timestamp;

@Table(name="train_history")
public class TrainHistory {
    @Id
    private Long id;

    @Column(name = "train_name")
    private String trainName;

    @Column(name = "model_name")
    private String modelName;

    @Column(name = "org_list")
    private String orgList;

    @Column(name = "finish_time")
    private Timestamp finishTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTrainName() {
        return trainName;
    }

    public void setTrainName(String trainName) {
        this.trainName = trainName;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public String getOrgList() {
        return orgList;
    }

    public void setOrgList(String orgList) {
        this.orgList = orgList;
    }

    public Timestamp getFinishTime() {
        return finishTime;
    }

    public void setFinishTime(Timestamp finishTime) {
        this.finishTime = finishTime;
    }

    @Override
    public String toString() {
        return "TrainHistory{" +
                "id=" + id +
                ", trainName='" + trainName + '\'' +
                ", modelName='" + modelName + '\'' +
                ", orgList='" + orgList + '\'' +
                ", finishTime=" + finishTime +
                '}';
    }
}
