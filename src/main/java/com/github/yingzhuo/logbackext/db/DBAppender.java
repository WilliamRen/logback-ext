package com.github.yingzhuo.logbackext.db;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import ch.qos.logback.classic.db.DBHelper;
import ch.qos.logback.classic.spi.CallerData;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.IThrowableProxy;
import ch.qos.logback.classic.spi.StackTraceElementProxy;
import ch.qos.logback.classic.spi.ThrowableProxyUtil;
import ch.qos.logback.core.CoreConstants;
import ch.qos.logback.core.db.DBAppenderBase;
import ch.qos.logback.core.db.dialect.SQLDialectCode;

import com.github.yingzhuo.logbackext.names.DefaultTableAndColumnNameResolver;
import com.github.yingzhuo.logbackext.names.TableAndColumnNameResolver;

/**
 * 参考ch.qos.logback.classic.db.DBAppender
 * 
 * @author yingzhuo
 * 
 */
@SuppressWarnings("rawtypes")
public class DBAppender extends DBAppenderBase<ILoggingEvent> {
	
	private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS");
	
	private boolean printStackTrace = true;
	protected String insertPropertiesSQL;
	protected String insertExceptionSQL;
	protected String insertSQL;
	protected static final Method GET_GENERATED_KEYS_METHOD;

	private TableAndColumnNameResolver nameResolver = new DefaultTableAndColumnNameResolver();

	static final int TIMESTMP_INDEX = 1;
	static final int FORMATTED_MESSAGE_INDEX = 2;
	static final int LOGGER_NAME_INDEX = 3;
	static final int LEVEL_STRING_INDEX = 4;
	static final int THREAD_NAME_INDEX = 5;
	static final int REFERENCE_FLAG_INDEX = 6;
	static final int ARG0_INDEX = 7;
	static final int ARG1_INDEX = 8;
	static final int ARG2_INDEX = 9;
	static final int ARG3_INDEX = 10;
	static final int ARG4_INDEX = 11;
	static final int ARG5_INDEX = 12;
	static final int ARG6_INDEX = 13;
	static final int ARG7_INDEX = 14;
	static final int ARG8_INDEX = 15;
	static final int ARG9_INDEX = 16;
	static final int ARG10_INDEX = 17;
	static final int ARG11_INDEX = 18;
	static final int ARG12_INDEX = 19;
	static final int ARG13_INDEX = 20;
	static final int ARG14_INDEX = 21;
	static final int ARG15_INDEX = 22;
	static final int ARG16_INDEX = 23;
	static final int ARG17_INDEX = 24;
	static final int ARG18_INDEX = 25;
	static final int ARG19_INDEX = 26;
	static final int ARG20_INDEX = 27;
	static final int ARG21_INDEX = 28;
	static final int ARG22_INDEX = 29;
	static final int ARG23_INDEX = 30;
	static final int ARG24_INDEX = 31;
	static final int ARG25_INDEX = 32;
	static final int ARG26_INDEX = 33;
	static final int ARG27_INDEX = 34;
	static final int ARG28_INDEX = 35;
	static final int ARG29_INDEX = 36;
	static final int ARG30_INDEX = 37;
	static final int ARG31_INDEX = 38;

	static final int CALLER_FILENAME_INDEX = 39;
	static final int CALLER_CLASS_INDEX = 40;
	static final int CALLER_METHOD_INDEX = 41;
	static final int CALLER_LINE_INDEX = 42;
	static final int EVENT_ID_INDEX = 43;

	static final StackTraceElement EMPTY_CALLER_DATA = CallerData.naInstance();

	static {
		Method getGeneratedKeysMethod;
		try {
			getGeneratedKeysMethod = PreparedStatement.class.getMethod("getGeneratedKeys", (Class[]) null);
		} catch (Exception ex) {
			getGeneratedKeysMethod = null;
		}
		GET_GENERATED_KEYS_METHOD = getGeneratedKeysMethod;
	}

	@Override
	public void start() {
		insertExceptionSQL = SQLBuilder.buildInsertExceptionSQL(nameResolver);
		insertPropertiesSQL = SQLBuilder.buildInsertPropertiesSQL(nameResolver);
		insertSQL = SQLBuilder.buildInsertSQL(nameResolver);
		super.start();
	}

	@Override
	protected void subAppend(ILoggingEvent event, Connection connection, PreparedStatement insertStatement) throws Throwable {

		bindLoggingEventWithInsertStatement(insertStatement, event);
		bindLoggingEventArgumentsWithPreparedStatement(insertStatement, event.getArgumentArray());

		bindCallerDataWithPreparedStatement(insertStatement, event.getCallerData());

		int updateCount = 0;
		try {
			updateCount = insertStatement.executeUpdate();
		} catch (Exception e) {
			if (this.printStackTrace) {
				e.printStackTrace();
			}
			throw e;
		}
		if (updateCount != 1) {
			addWarn("Failed to insert loggingEvent");
		}
	}

	protected void secondarySubAppend(ILoggingEvent event, Connection connection, long eventId) throws Throwable {
		Map<String, String> mergedMap = mergePropertyMaps(event);
		insertProperties(mergedMap, connection, eventId);

		if (event.getThrowableProxy() != null) {
			insertThrowable(event.getThrowableProxy(), connection, eventId);
		}
	}

	void bindLoggingEventWithInsertStatement(PreparedStatement stmt, ILoggingEvent event) throws SQLException {
		stmt.setString(TIMESTMP_INDEX, DATE_FORMAT.format(new Date(event.getTimeStamp())));
		stmt.setString(FORMATTED_MESSAGE_INDEX, event.getFormattedMessage());
		stmt.setString(LOGGER_NAME_INDEX, event.getLoggerName());
		stmt.setString(LEVEL_STRING_INDEX, event.getLevel().toString());
		stmt.setString(THREAD_NAME_INDEX, event.getThreadName());
		stmt.setShort(REFERENCE_FLAG_INDEX, DBHelper.computeReferenceMask(event));
	}

	void bindLoggingEventArgumentsWithPreparedStatement(PreparedStatement stmt, Object[] argArray) throws SQLException {
		int arrayLen = argArray != null ? argArray.length : 0;
		for (int i = 0; i < arrayLen && i < 32; i++) {
			stmt.setString(ARG0_INDEX + i, asStringTruncatedTo254(argArray[i]));
		}
		if (arrayLen < 32) {
			for (int i = arrayLen; i < 32; i++) {
				stmt.setString(ARG0_INDEX + i, null);
			}
		}
	}

	String asStringTruncatedTo254(Object o) {
		String s = null;
		if (o != null) {
			s = o.toString();
		}

		if (s == null) {
			return null;
		}
		if (s.length() <= 254) {
			return s;
		} else {
			return s.substring(0, 254);
		}
	}

	void bindCallerDataWithPreparedStatement(PreparedStatement stmt, StackTraceElement[] callerDataArray) throws SQLException {

		StackTraceElement caller = extractFirstCaller(callerDataArray);

		stmt.setString(CALLER_FILENAME_INDEX, caller.getFileName());
		stmt.setString(CALLER_CLASS_INDEX, caller.getClassName());
		stmt.setString(CALLER_METHOD_INDEX, caller.getMethodName());
		stmt.setString(CALLER_LINE_INDEX, Integer.toString(caller.getLineNumber()));
	}

	private StackTraceElement extractFirstCaller(StackTraceElement[] callerDataArray) {
		StackTraceElement caller = EMPTY_CALLER_DATA;
		if (hasAtLeastOneNonNullElement(callerDataArray))
			caller = callerDataArray[0];
		return caller;
	}

	private boolean hasAtLeastOneNonNullElement(StackTraceElement[] callerDataArray) {
		return callerDataArray != null && callerDataArray.length > 0
				&& callerDataArray[0] != null;
	}

	Map<String, String> mergePropertyMaps(ILoggingEvent event) {
		Map<String, String> mergedMap = new HashMap<String, String>();
		Map<String, String> loggerContextMap = event.getLoggerContextVO().getPropertyMap();
		Map<String, String> mdcMap = event.getMDCPropertyMap();
		if (loggerContextMap != null) {
			mergedMap.putAll(loggerContextMap);
		}
		if (mdcMap != null) {
			mergedMap.putAll(mdcMap);
		}

		return mergedMap;
	}

	@Override
	protected Method getGeneratedKeysMethod() {
		return GET_GENERATED_KEYS_METHOD;
	}

	@Override
	protected String getInsertSQL() {
		return insertSQL;
	}

	protected void insertProperties(Map<String, String> mergedMap, Connection connection, long eventId) throws SQLException {
		Set propertiesKeys = mergedMap.keySet();
		if (propertiesKeys.size() > 0) {
			PreparedStatement insertPropertiesStatement = connection.prepareStatement(insertPropertiesSQL);

			for (Iterator i = propertiesKeys.iterator(); i.hasNext();) {
				String key = (String) i.next();
				String value = (String) mergedMap.get(key);

				insertPropertiesStatement.setLong(1, eventId);
				insertPropertiesStatement.setString(2, key);
				insertPropertiesStatement.setString(3, value);

				if (cnxSupportsBatchUpdates) {
					insertPropertiesStatement.addBatch();
				} else {
					insertPropertiesStatement.execute();
				}
			}

			if (cnxSupportsBatchUpdates) {
				insertPropertiesStatement.executeBatch();
			}

			insertPropertiesStatement.close();
		}
	}

	void updateExceptionStatement(PreparedStatement exceptionStatement, String txt, short i, long eventId) throws SQLException {
		exceptionStatement.setLong(1, eventId);
		exceptionStatement.setShort(2, i);
		exceptionStatement.setString(3, txt);
		if (cnxSupportsBatchUpdates) {
			exceptionStatement.addBatch();
		} else {
			exceptionStatement.execute();
		}
	}

	short buildExceptionStatement(IThrowableProxy tp, short baseIndex,
			PreparedStatement insertExceptionStatement, long eventId)
			throws SQLException {

		StringBuilder buf = new StringBuilder();
		ThrowableProxyUtil.subjoinFirstLine(buf, tp);
		updateExceptionStatement(insertExceptionStatement, buf.toString(), baseIndex++, eventId);

		int commonFrames = tp.getCommonFrames();
		StackTraceElementProxy[] stepArray = tp.getStackTraceElementProxyArray();
		for (int i = 0; i < stepArray.length - commonFrames; i++) {
			StringBuilder sb = new StringBuilder();
			sb.append(CoreConstants.TAB);
			ThrowableProxyUtil.subjoinSTEP(sb, stepArray[i]);
			updateExceptionStatement(insertExceptionStatement, sb.toString(), baseIndex++, eventId);
		}

		if (commonFrames > 0) {
			StringBuilder sb = new StringBuilder();
			sb.append(CoreConstants.TAB).append("... ").append(commonFrames)
					.append(" common frames omitted");
			updateExceptionStatement(insertExceptionStatement, sb.toString(),
					baseIndex++, eventId);
		}

		return baseIndex;
	}

	protected void insertThrowable(IThrowableProxy tp, Connection connection, long eventId) throws SQLException {

		PreparedStatement exceptionStatement = connection.prepareStatement(insertExceptionSQL);

		short baseIndex = 0;
		while (tp != null) {
			baseIndex = buildExceptionStatement(tp, baseIndex, exceptionStatement, eventId);
			tp = tp.getCause();
		}

		if (cnxSupportsBatchUpdates) {
			exceptionStatement.executeBatch();
		}
		exceptionStatement.close();
	}

	public boolean isPrintStackTrace() {
		return printStackTrace;
	}

	public void setPrintStackTrace(boolean printStackTrace) {
		this.printStackTrace = printStackTrace;
	}

	public TableAndColumnNameResolver getNameResolver() {
		return nameResolver;
	}

	public void setNameResolver(TableAndColumnNameResolver nameResolver) {
		this.nameResolver = nameResolver;
	}

	@Override
	public void append(ILoggingEvent eventObject) {
		Connection connection = null;
		try {
			connection = connectionSource.getConnection();
			connection.setAutoCommit(false);
			PreparedStatement insertStatement;

			if (cnxSupportsGetGeneratedKeys) {
				String EVENT_ID_COL_NAME = "EVENT_ID";
				if (connectionSource.getSQLDialectCode() == SQLDialectCode.POSTGRES_DIALECT) {
					EVENT_ID_COL_NAME = EVENT_ID_COL_NAME.toLowerCase();
				}
				insertStatement = connection.prepareStatement(getInsertSQL(), new String[] { EVENT_ID_COL_NAME });
			} else {
				insertStatement = connection.prepareStatement(getInsertSQL());
			}

			long eventId;
			synchronized (this) {
				subAppend(eventObject, connection, insertStatement);
				eventId = 
						selectEventId(insertStatement, connection);
			}
			secondarySubAppend(eventObject, connection, eventId);
			insertStatement.close();
			connection.commit();
		} catch (Throwable sqle) {
			if (this.printStackTrace) {
				sqle.printStackTrace();
			}
			addError("problem appending event", sqle);
		} finally {
			try { connection.close();} catch (SQLException e) {}
		}
	}
}
