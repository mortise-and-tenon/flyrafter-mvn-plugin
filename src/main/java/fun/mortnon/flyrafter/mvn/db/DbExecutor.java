package fun.mortnon.flyrafter.mvn.db;

import fun.mortnon.flyrafter.mvn.utils.Utils;
import fun.mortnon.flyrafter.resolver.Constants;

import javax.sql.DataSource;
import java.io.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;

/**
 * @author Moon Wu
 * @date 2021/5/19
 */
public class DbExecutor {
    private DataSource dataSource;

    public DbExecutor(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * 执行 sql
     *
     * @param folder
     */
    public void executeSql(File folder) {
        if (!folder.exists() || !folder.isDirectory()) {
            Utils.LOGGER.info("sql folder parameter is wrong,couldn't execute sql files.");
            return;
        }

        try {
            Connection connection = dataSource.getConnection();
            Statement statement = connection.createStatement();
            Arrays.stream(folder.listFiles()).forEach(file -> {
                String content = readFile(file);
                try {
                    statement.execute(content);
                } catch (SQLException e) {
                    Utils.LOGGER.error("execute sql fail for " + content);
                    return;
                }
            });
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private String readFile(File file) {
        StringBuffer strBuffer = new StringBuffer();
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), Constants.UTF8));
            String tmpStr = null;

            while (null != (tmpStr = reader.readLine())) {
                strBuffer.append(tmpStr);
            }
        } catch (Exception e) {
            Utils.LOGGER.error("read content from sql file fail:", e);
        }
        return strBuffer.toString();
    }
}
