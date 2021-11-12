package com.bcp.serverc.model;

import lombok.Data;
import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Data
@Table(name = "org")
public class Org {
    @Id
    private Long id;

    @NotNull
    @Column(name = "org_name")
    private String orgName;

    @NotNull
    @Column(name = "server_address")
    private String serverAddress;

    @NotNull
    @Column(name = "front_address")
    private String frontAddress;
}
