package com.bcp.serverc.model;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Timestamp;

@Table(name = "predict_data")
public class PredictData {
    @Id
    private Long id;

    @Column(name = "predict_data_name")
    private String predictDataName;

    @Column(name = "create_time")
    private Timestamp createTime;

    @Column(name = "create_user")
    private String createUser;

    @Column(name = "description")
    private String description;

    @Override
    public String toString() {
        return "PredictData{" +
                "id=" + id +
                ", predictDataName='" + predictDataName + '\'' +
                ", createTime=" + createTime +
                ", createUser='" + createUser + '\'' +
                ", description='" + description + '\'' +
                '}';
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPredictDataName() {
        return predictDataName;
    }

    public void setPredictDataName(String predictDataName) {
        this.predictDataName = predictDataName;
    }

    public Timestamp getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Timestamp createTime) {
        this.createTime = createTime;
    }

    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
