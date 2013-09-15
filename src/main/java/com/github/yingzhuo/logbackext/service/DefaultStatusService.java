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
package com.github.yingzhuo.logbackext.service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.core.CoreConstants;

public class DefaultStatusService extends StatusService {

	public static final StatusService INSTANCE = new DefaultStatusService();
	
	public static final DateFormat DEFAULT_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	private DefaultStatusService() {
		super();
	}
	
	@Override
	public List<Status> getStatusList() {
		LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
		List<ch.qos.logback.core.status.Status> list = lc.getStatusManager().getCopyOfStatusList();
		
		List<Status> result = new ArrayList<Status>(list.size());
		
		for (ch.qos.logback.core.status.Status s : list) {
			Status status = new Status();
			status.setDate(DEFAULT_DATE_FORMAT.format(new Date(s.getDate())));
			status.setLevel(statusLevelAsString(s));
			status.setOrigin(abbreviatedOrigin(s));
			status.setMessage(s.getMessage());
			result.add(status);
		}
		
		return result;
	}

	private String statusLevelAsString(ch.qos.logback.core.status.Status s) {
		int l = s.getEffectiveLevel();
		switch(l) {
		case 0 : return "INFO";
		case 1 : return "WARN";
		case 2 : return "ERROR";
		default :
			return null;
		}
	}
	
	private String abbreviatedOrigin(ch.qos.logback.core.status.Status s) {
		Object o = s.getOrigin();
		if (o == null) {
			return null;
		}
		String fqClassName = o.getClass().getName();
		int lastIndex = fqClassName.lastIndexOf(CoreConstants.DOT);
		if (lastIndex != -1) {
			return fqClassName.substring(lastIndex + 1, fqClassName.length());
		} else {
			return fqClassName;
		}
	}

}
