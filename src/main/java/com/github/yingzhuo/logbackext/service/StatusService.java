package com.github.yingzhuo.logbackext.service;

import java.util.List;


public abstract class StatusService {

	public static StatusService getDefaultService() {
		return DefaultStatusService.INSTANCE;
	}
	
	public abstract List<Status> getStatusList();

}
