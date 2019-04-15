import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Vector;

public class Table {
    private Vector<String> columnNames = new Vector<>();
    private ArrayList DataList = new ArrayList();

    public Vector<String> getColumnNames(){
        return columnNames;
    }

    public void fillDataList(ResultSet resultSet) throws SQLException {
        DataList.clear();
        while (resultSet.next()){
            int i = 0;
            Object[] SQLRow = new Object[columnNames.size()];
            while (i<columnNames.size()){
                SQLRow[i] = resultSet.getObject(i+1);
                i++;
            }
            DataList.add(SQLRow);
        }
    }

    void fillTable(DefaultTableModel defaultTableModel){
        for (int i = 0; i < DataList.size(); i++) {
            defaultTableModel.addRow((Object[]) DataList.get(i));
        }
    }

    void clearTable(JTable jTable,DefaultTableModel defaultTableModel){
        defaultTableModel.setRowCount(0);
        jTable.setModel(defaultTableModel);
    }

}
