package com.bcp.serverc.model;

import lombok.Data;
import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.sql.Timestamp;

@Data
@Table(name = "train_model")
public class TrainModel {
    @Id
    private Long id;

    @NotNull
    @Column(name = "model_name")
    private String modelName;

    @NotNull
    @Column(name = "org_code")
    private Long orgCode;

    @NotNull
    @Column(name = "create_time")
    private Timestamp createTime;

    @Column(name = "update_time")
    private Timestamp updateTime;

    @NotNull
    @Column(name = "create_user")
    private Long createUser;

    @NotNull
    @Column(name = "status")
    private Integer status;
}
