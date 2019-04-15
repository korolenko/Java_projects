import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.*;

public class PostgreConnector {
    private String DB_URL = "XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX";
    private String USER = "XXXXX";
    private String PASS = "XXXXX";
    private String sql = "XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX";
    private Connection connection = null;

    public void setUSER(JTextArea jTextLogin) {
        USER = jTextLogin.getText();
    }

    public void setPASS(JPasswordField jTextPassword) {
        PASS = String.valueOf(jTextPassword.getPassword());
    }

    public String getUSER() {
        return USER;
    }

    public String getPASS() {
        return PASS;
    }

    public void makeConnection(JTextArea jTextArea) {
        FrameLog.setLog(jTextArea, "Trying to make connection to PostgreSQL JDBC");
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            FrameLog.setLog(jTextArea, "PostgreSQL JDBC Driver is not found. Include it in your library path");
            e.printStackTrace();
            return;
        }

        FrameLog.setLog(jTextArea, "PostgreSQL JDBC Driver successfully connected");

        try {
            connection = DriverManager.getConnection(DB_URL, USER, PASS);
        } catch (SQLException e) {
            FrameLog.setLog(jTextArea, "Connection Failed");
            e.printStackTrace();
            return;
        }

        if (connection != null) {
            FrameLog.setLog(jTextArea, "You successfully connected to database");
        } else {
            FrameLog.setLog(jTextArea, "Failed to make connection to database");
        }
    }

    public void makeSQLQuery(JTextArea jTextArea, Table table) throws SQLException {
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
            FrameLog.setLog(jTextArea, "Unnable to make SQL query. Check view efw.v_aig_table_list.");
        }
    }

    public void closeConnection(JTextArea jTextArea){
        try {
            connection.close();
            FrameLog.setLog(jTextArea,"Connection successfully closed");
        } catch (SQLException e){
            FrameLog.setLog(jTextArea,"Failed to close connection");
            e.printStackTrace();
            return;
        }
    }

    public void getDataFromDB(Table table, JTable jTable, DefaultTableModel defaultTableModel, JTextArea jTextArea){

        try {
            table.clearTable(jTable,defaultTableModel);
            makeConnection(jTextArea);
            makeSQLQuery(jTextArea,table);
            closeConnection(jTextArea);
        } catch (SQLException e) {
            FrameLog.setLog(jTextArea, String.valueOf(e.getStackTrace()));
        }
        defaultTableModel.setColumnIdentifiers(table.getColumnNames());
        table.fillTable(defaultTableModel);
    }
}
