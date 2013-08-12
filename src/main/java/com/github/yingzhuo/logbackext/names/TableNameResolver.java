package com.github.yingzhuo.logbackext.names;

public interface TableNameResolver {
	
	public String getLoggingEventTableName();

	public String getLoggingEventExceptionTableName();
	
	public String getLoggingEventPropertyTableName();

}
