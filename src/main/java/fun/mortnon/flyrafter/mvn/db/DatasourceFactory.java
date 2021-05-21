package fun.mortnon.flyrafter.mvn.db;

import org.apache.commons.dbcp2.BasicDataSource;

import javax.sql.DataSource;

/**
 * @author Moon Wu
 * @date 2021/5/19
 */
public class DatasourceFactory {

    public static DataSource create(String url, String driver, String username, String password) throws Exception {
        BasicDataSource dataSource = new BasicDataSource();
        dataSource.setUrl(url);
        dataSource.setDriverClassName(driver);
        dataSource.setUsername(username);
        dataSource.setPassword(password);

        return dataSource;
    }
}
