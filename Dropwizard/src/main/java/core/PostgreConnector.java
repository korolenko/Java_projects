package core;

import java.sql.*;


//класс-коннектор к СУБД
public class PostgreConnector {
    private final String DB_URL = "jdbc:postgresql:XXX";
    private final String USER = "USER";
    private final String PASS = "XXX";
    private Connection connection = null;

    public void makeConnection() {
        Logger.setLog("Trying to make connection to PostgreSQL JDBC");
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            Logger.setLog("PostgreSQL JDBC Driver is not found. Include it in your library path");
            e.printStackTrace();
        }

        Logger.setLog("PostgreSQL JDBC Driver successfully connected");

        try {
            connection = DriverManager.getConnection(DB_URL, USER, PASS);
        } catch (SQLException e) {
            Logger.setLog("Connection Failed");
            e.printStackTrace();
        }

        if (connection != null) {
            Logger.setLog("You successfully connected to database");
        } else {
            Logger.setLog("Failed to make connection to database");
        }
    }

    public void makeSQLQuery(Table table, String sql) throws SQLException {
        // Execute SQL query
        Statement statement = null;
        ResultSet resultSet = null;
        table.getColumnNames().clear();
        try {
            statement = connection.createStatement();
            resultSet = statement.executeQuery(sql);
            if (resultSet != null) {
                ResultSetMetaData columns = resultSet.getMetaData();
                int i = 0;
                while (i < columns.getColumnCount()) {
                    i++;
                    table.getColumnNames().add(columns.getColumnName(i));
                }
                table.fillDataList(resultSet);
            }
        } catch (NullPointerException e) {
            Logger.setLog("Unnable to make SQL query. Check view efw.v_aig_table_list.");
        }
        Logger.setLog("Selection has finished sucsessfully");
    }

    public void closeConnection(){
        try {
            connection.close();
            Logger.setLog("Connection successfully closed");
        } catch (SQLException e){
            Logger.setLog("Failed to close connection");
            e.printStackTrace();
        }
    }
}
