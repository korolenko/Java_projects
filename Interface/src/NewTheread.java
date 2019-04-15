import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class NewTheread extends Thread {
    Thread GUIThread;
    NewTheread(String name){
        GUIThread = new Thread(this,name);
        GUIThread.start();
    }

    public void run(Table table, JTable jTable, DefaultTableModel defaultTableModel, JTextArea jTextArea, PostgreConnector postgreConnector){
        //new MyFrame("Framework viewer 1.0");
        postgreConnector.getDataFromDB(table, jTable, defaultTableModel, jTextArea);
    }
}
