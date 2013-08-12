
BEGIN;
DROP TABLE IF EXISTS logging_event_property;
DROP TABLE IF EXISTS logging_event_exception;
DROP TABLE IF EXISTS logging_event;
COMMIT;

BEGIN;
CREATE TABLE logging_event 
  (
  	event_id          BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    timestmp         VARCHAR(24) NOT NULL,
    formatted_message  TEXT NOT NULL,
    logger_name       VARCHAR(254) NOT NULL,
    level_string      VARCHAR(254) NOT NULL,
    thread_name       VARCHAR(254),
    reference_flag    SMALLINT,
    caller_filename   VARCHAR(254) NOT NULL,
    caller_class      VARCHAR(254) NOT NULL,
    caller_method     VARCHAR(254) NOT NULL,
    caller_line       CHAR(4) NOT NULL,
    arg0              VARCHAR(254),
    arg1              VARCHAR(254),
    arg2              VARCHAR(254),
    arg3              VARCHAR(254),
    arg4              VARCHAR(254),
    arg5              VARCHAR(254),
    arg6              VARCHAR(254),
    arg7              VARCHAR(254),
    arg8              VARCHAR(254),
    arg9              VARCHAR(254),
    arg10              VARCHAR(254),
    arg11              VARCHAR(254),
    arg12              VARCHAR(254),
    arg13              VARCHAR(254),
    arg14              VARCHAR(254),
    arg15              VARCHAR(254),
    arg16              VARCHAR(254),
    arg17              VARCHAR(254),
    arg18              VARCHAR(254),
    arg19              VARCHAR(254),
    arg20              VARCHAR(254),
    arg21              VARCHAR(254),
    arg22              VARCHAR(254),
    arg23              VARCHAR(254),
    arg24              VARCHAR(254),
    arg25              VARCHAR(254),
    arg26              VARCHAR(254),
    arg27              VARCHAR(254),
    arg28              VARCHAR(254),
    arg29              VARCHAR(254),
    arg30              VARCHAR(254),
    arg31              VARCHAR(254)
  );
COMMIT;
BEGIN;
CREATE TABLE logging_event_property
  (
    event_id	      BIGINT NOT NULL,
    mapped_key        VARCHAR(254) NOT NULL,
    mapped_value      TEXT,
    PRIMARY KEY(event_id, mapped_key),
    FOREIGN KEY (event_id) REFERENCES logging_event(event_id)
  );
COMMIT;
BEGIN;
CREATE TABLE logging_event_exception
  (
    event_id         BIGINT NOT NULL,
    i                SMALLINT NOT NULL,
    trace_line       VARCHAR(254) NOT NULL,
    PRIMARY KEY(event_id, i),
    FOREIGN KEY (event_id) REFERENCES logging_event(event_id)
  );
COMMIT;