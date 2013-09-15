/*
 * Copyright 2002-2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.yingzhuo.logbackext.names;

public class DefaultTableAndColumnNameResolver implements TableAndColumnNameResolver {

	private String loggingEventTableName = "logging_event";
	private String loggingEventExceptionTableName = "logging_event_exception";
	private String loggingEventPropertyTableName = "logging_event_property";
	
	public String getLoggingEventTableName() {
		return this.loggingEventTableName;
	}

	public String getLoggingEventExceptionTableName() {
		return this.loggingEventExceptionTableName;
	}

	public String getLoggingEventPropertyTableName() {
		return this.loggingEventPropertyTableName;
	}

	public String getColumnName(Object object) {
		return object.toString().toLowerCase();
	}
	
	// setter
	// -------------------------------------------------------------------------------------------------------------------

	public void setLoggingEventTableName(String loggingEventTableName) {
		this.loggingEventTableName = loggingEventTableName;
	}

	public void setLoggingEventExceptionTableName(String loggingEventExceptionTableName) {
		this.loggingEventExceptionTableName = loggingEventExceptionTableName;
	}

	public void setLoggingEventPropertyTableName(String loggingEventPropertyTableName) {
		this.loggingEventPropertyTableName = loggingEventPropertyTableName;
	}
	
}
