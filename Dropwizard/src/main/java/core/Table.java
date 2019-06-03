package core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class Table {
    private Vector<String> columnNames = new Vector<>();
    private ArrayList DataList = new ArrayList();

    //контейнеры данных для основной страницы
    private String stringDatalistMainPage;
    private String columnNamesMainPageString;
    private String[] columnNamesMainPage= new String[4];

    // контейнеры данных для детальной информации
    private String stringColumnListDetailed;
    private String stringDatalistDetailed;
    private String stringGrepData;
    private String stringGrepDataResult;

    //заполнение массива данных из представления Postgre SQL
    public void fillDataList(ResultSet resultSet) throws SQLException {
        DataList.clear();
        while (resultSet.next()){
            int i = 0;
            Object[] SQLRow = new Object[columnNames.size()];
            while (i<columnNames.size()){
                if (resultSet.getObject(i+1)==null)
                    SQLRow[i] = "";
                else
                    SQLRow[i] = resultSet.getObject(i+1);
                i++;
            }
            DataList.add(SQLRow);
        }
    }

    public Vector<String> getColumnNames(){
        return columnNames;
    }

    //задаем те поля, которые нужны на первой странице
    public void setColumnNamesMainPage() {
        columnNamesMainPage[0] = "name";
        columnNamesMainPage[1] = "last_status";
        columnNamesMainPage[2] = "last_extract_attempt_dt";
        columnNamesMainPage[3] = "last_success_extract_dt";
    }

    //заполнение контейнера заголовка на главной странице
    public void setColumnNamesMainPageString(){
        columnNamesMainPageString = "<th scope='col'>№</th>";
        for (String i:columnNamesMainPage)
        {
            //проверка на наличие в базе-источнике искомых полей
            if (columnNames.contains(i)) columnNamesMainPageString += "<th scope='col'>" + i + "</th>";
            else columnNamesMainPageString += "<th scope='col'>" + "Проверьте правильность поля " + i + "</th>";
        }
    }

    public String getColumnNamesMainPageString(){
        setColumnNamesMainPageString();
        return columnNamesMainPageString;
    }

    //заполнение контейнера данных web-таблицы на главной странице
    //забираем все данные из представления, выводим только то, что нужно на первой странице
    // соответственно заголовкам
    public void  setStringDatalistMainPage() {
        stringDatalistMainPage = "<tr>";
        for (int i = 0;i<DataList.size(); i++){
            stringDatalistMainPage += "<th scope='row'>"+String.valueOf(i+1)+"</th>";
            int j = 0;
            for (String k:columnNamesMainPage)
            {
                if (j == 0)
                    stringDatalistMainPage += "<td>" + "<a href = 'http://hdp-study4:8080/detailed/?name=" + ((Object[])DataList.get(i))[columnNames.indexOf(k)].toString() + "' title='Кликни для детализации'>"
                            + ((Object[])DataList.get(i))[columnNames.indexOf(k)].toString() + "</a></td>";
                else
                    stringDatalistMainPage += "<td>" + ((Object[])DataList.get(i))[columnNames.indexOf(k)].toString() + "</td>";
                j++;
            }
            stringDatalistMainPage += "</tr>";
        }
    }

    public String getStringDatalistMainPage(){
        setStringDatalistMainPage();
        return stringDatalistMainPage;
    }

    /*******
     * Detailed page
     ******/
    //заполнение заголовка web-таблицы с детальной информацией
    public void  setStringColumnListDetailed() {
        stringColumnListDetailed = "<th scope='col'>№</th>";
        int i = 0;
        while (i < columnNames.size()) {
            stringColumnListDetailed += "<th scope='col'>" + columnNames.get(i) + "</th>";
            i++;
        }
    }

    public String getStringColumnListDetailed(){
        setStringColumnListDetailed();
        return stringColumnListDetailed;
    }

    //заполнение данных web-таблицы с детальной информацией о задании
   /*public void  setStringDatalistDetailed1(String name) {
        stringDatalistDetailed = "";
        for (int i = 0;i<DataList.size(); i++){
            if (((Object[])DataList.get(i))[1].equals(name)){
                stringDatalistDetailed += "<tr>" + "<th scope='row'>"+String.valueOf(i+1)+"</th>";
                int j = 0;
                while( j<((Object[]) DataList.get(i)).length){
                        stringDatalistDetailed += "<td>" + ((Object[])DataList.get(i))[j].toString() + "</td>";
                        //заполнение stringGrepData
                        if (j==17) stringGrepData = ((Object[])DataList.get(i))[j].toString();
                    j++;
                }
                stringDatalistDetailed += "</tr>";
                break;
            }
        }
    }*/

    //заполнение данных детальной информации о задании
    public void  setStringDatalistDetailed(String name) {
        stringDatalistDetailed = "";
        for (int i = 0;i<DataList.size(); i++){
            if (((Object[])DataList.get(i))[1].equals(name)) {
                int j = 0;
                while (j < ((Object[]) DataList.get(i)).length) {
                    stringDatalistDetailed += "<tr><td><th>" + columnNames.get(j) + "</th><td>" + ((Object[]) DataList.get(i))[j].toString() + "</td></td></tr>";
                    //заполнение stringGrepData
                    if (j==17) stringGrepData = ((Object[])DataList.get(i))[j].toString();
                    j++;
                }
                break;
            }
        }
    }

    public String getStringDatalistDetailed(String name){
        setStringDatalistDetailed(name);
        return stringDatalistDetailed;
    }

    public String getStringGrepDataResult() throws IOException {
        //shellExecutor();
        return stringGrepDataResult;
    }

    //выполнение shell-команды grep
    public void shellExecutor() throws IOException {
        List<String> sqoopArgsList = new ArrayList<>();
        sqoopArgsList.add(stringGrepData);

        ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.command(sqoopArgsList);

        Process p = processBuilder.start();

        ///перенаправление стандартных потоков
        InputStream stderr = p.getErrorStream();
        InputStream stdout = p.getInputStream();
        try {
            try (BufferedReader brStdOut = new BufferedReader(new InputStreamReader(stdout));
                 BufferedReader brStdErr = new BufferedReader(new InputStreamReader(stderr))) {
                do {
                    while ((stringGrepDataResult = brStdErr.readLine()) != null) {
                        Logger.setLog("Считана команда: " + stringGrepDataResult);
                    }
                    while ((stringGrepDataResult = brStdOut.readLine()) != null) {
                        //System.out.println(line);
                    }
                    Thread.sleep(1000);
                } while (p.isAlive() );
            }
        } catch (Exception e){
            Logger.setLog(e.toString());
        }

    }
}
