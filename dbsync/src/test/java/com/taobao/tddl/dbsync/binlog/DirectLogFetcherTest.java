package com.taobao.tddl.dbsync.binlog;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

import junit.framework.Assert;

import org.junit.Test;

public class DirectLogFetcherTest extends BaseLogFetcherTest {

    @Test
    public void testSimple() {
        DirectLogFetcher fecther = new DirectLogFetcher();
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection connection = DriverManager.getConnection("jdbc:mysql://10.20.144.29:3306",
                "ottermysql",
                "ottermysql");
            Statement statement = connection.createStatement();
            statement.execute("SET @master_binlog_checksum='@@global.binlog_checksum'");

            fecther.open(connection, "mysql-bin.001016", 4L, 2);

            LogDecoder decoder = new LogDecoder(LogEvent.UNKNOWN_EVENT, LogEvent.ENUM_END_EVENT);
            LogContext context = new LogContext();
            while (fecther.fetch()) {
                LogEvent event = null;
                event = decoder.decode(fecther, context);

                if (event == null) {
                    throw new RuntimeException("parse failed");
                }

                int eventType = event.getHeader().getType();
                switch (eventType) {
                    case LogEvent.ROTATE_EVENT:
                        // binlogFileName = ((RotateLogEvent)
                        // event).getFilename();
                        break;
                    case LogEvent.WRITE_ROWS_EVENT_V1:
                    case LogEvent.WRITE_ROWS_EVENT:
                        // parseRowsEvent((WriteRowsLogEvent) event);
                        break;
                    case LogEvent.UPDATE_ROWS_EVENT_V1:
                    case LogEvent.UPDATE_ROWS_EVENT:
                        // parseRowsEvent((UpdateRowsLogEvent) event);
                        break;
                    case LogEvent.DELETE_ROWS_EVENT_V1:
                    case LogEvent.DELETE_ROWS_EVENT:
                        // parseRowsEvent((DeleteRowsLogEvent) event);
                        break;
                    case LogEvent.QUERY_EVENT:
                        // parseQueryEvent((QueryLogEvent) event);
                        break;
                    case LogEvent.ROWS_QUERY_LOG_EVENT:
                        // parseRowsQueryEvent((RowsQueryLogEvent) event);
                        break;
                    default:
                        break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        } finally {
            try {
                fecther.close();
            } catch (IOException e) {
                Assert.fail(e.getMessage());
            }
        }

    }
}
