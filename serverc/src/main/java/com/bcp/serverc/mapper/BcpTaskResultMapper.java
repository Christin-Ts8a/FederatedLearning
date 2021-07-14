package com.bcp.serverc.mapper;

import java.util.Collection;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.bcp.serverc.model.BcpTaskResult;
import tk.mybatis.mapper.common.Mapper;

public interface BcpTaskResultMapper extends Mapper<BcpTaskResult> {

	List<BcpTaskResult> getDesignatedOrLatestResult(@Param("taskIdCollection") Collection<Long> taskIdCollection,
			@Param("userId") String userId, @Param("round") Integer round, @Param("isLatest") boolean isLatest);
}