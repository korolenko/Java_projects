package core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Vector;

public class Table {
    private Vector<String> columnNames = new Vector<>();
    private ArrayList DataList = new ArrayList();

    //контейнеры данных для основной страницы
    private String stringDatalistMainPage;
    private String columnNamesMainPageString;

    // контейнеры данных для детальной информации
    private String stringColumnListDetailed;
    private String stringDatalistDetailed;
    private String stringGrepDataExtract;
    private String stringGrepDataApply;

    private String String_source_id;
    private String String_config_id;
    private String stringTableName;
    private String stringDatalist;

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

    //заполнение контейнера заголовка на главной странице
    //входящий параметр status нужен для того, чтобы знать, в рамках какого статуса выполнять сортировку
    //т.к. сортировка по длительности должна работать на всех вкладках
    //для этого реализовано динамическое изменение данных заголовка
    // при каждом клике на вкладки со статусами
    public void setColumnNamesMainPageString(String status){
        StringBuilder builder = new StringBuilder();
        builder.append("<th class='align-middle' scope='col'>№</th>");
        int i = 0;
        while (i < columnNames.size()) {
            if (!columnNames.get(i).equals("table_nm") && !columnNames.get(i).equals("source_id") && !columnNames.get(i).equals("config_id"))
            {
                // логика заполнения кнопок для сортировки, расположенных в заголовке таблицы
                if (columnNames.get(i).equals("extract_duration")){
                    builder.append("<th scope='col'><button type=\"button\" class=\"btn btn-link\" " +
                            "data-button='../getJobsDuration/?type=extract_duration&status="+status+"'title='Нажмите для сортировки по длительности'>"
                            + columnNames.get(i) + "</button></th>");
                }
                else if (columnNames.get(i).equals("apply_duration")){
                    builder.append("<th scope='col'><button type=\"button\" class=\"btn btn-link\" " +
                            "data-button='../getJobsDuration/?type=apply_duration&status="+status+"'title='Нажмите для сортировки по длительности'>"
                            + columnNames.get(i) + "</button></th>");
                }
                else {
                    builder.append("<th class='align-middle' scope='col'>" + columnNames.get(i) + "</th>");
                }
            }
            i++;
        }
        columnNamesMainPageString = builder.toString();
    }

    public String getColumnNamesMainPageString(String status){
        setColumnNamesMainPageString(status);
        return columnNamesMainPageString;
    }

    //вывод результата SQL-запроса без добавления HTML данных
    public String getCount(){
        String count = "";
        for (int i = 0;i<DataList.size(); i++) {
            count = ((Object[])DataList.get(i))[i].toString();
        }
        return count;
    }

    public String getStatuses(String failedExtractJobsSQL,String failedApplyJobsSQL){
        StringBuilder builder = new StringBuilder();
        String active;
        for (int i = 0;i<DataList.size(); i++) {
            int j = 0;
            while (j < ((Object[]) DataList.get(i)).length) {
                //программно делаем нужную вкладку активной
                if(i==1){
                    active = "active";
                }
                else{
                    active = "";
                }
                if (((Object[]) DataList.get(i))[j].toString().equals("FAIL")){
                    builder.append("<li><a class=\"nav-link " + active + "\" data-toggle=\"tab\" href=\"#" + String.valueOf(i + 1)
                            + "i\" role=\"tab\" title='Нажмите для сортировки по статусу'>"
                            + "ALL_FAILS" + " </a></li>\n");
                }
                else if (((Object[]) DataList.get(i))[j].toString().equals("FAIL_ON_APPLY")){
                    builder.append("<li><a class=\"nav-link "+active+"\" data-toggle=\"tab\" href=\"#"+String.valueOf(i+1)
                            + "i\" role=\"tab\" title='Нажмите для сортировки по статусу'>"
                            + ((Object[]) DataList.get(i))[j].toString() +
                            " <span class=\"badge badge-danger\">"+failedApplyJobsSQL+"</span></a></li>\n");
                }
                else if (((Object[]) DataList.get(i))[j].toString().equals("FAIL_ON_EXTRACT")){
                    builder.append("<li><a class=\"nav-link "+active+"\" data-toggle=\"tab\" href=\"#"+String.valueOf(i+1)
                            + "i\" role=\"tab\" title='Нажмите для сортировки по статусу'>"
                            + ((Object[]) DataList.get(i))[j].toString() +
                            " <span class=\"badge badge-danger\">"+failedExtractJobsSQL+"</span></a></li>\n");
                }
                else {
                    builder.append("<li><a class=\"nav-link " + active + "\" data-toggle=\"tab\" href=\"#" + String.valueOf(i + 1)
                            + "i\" role=\"tab\" title='Нажмите для сортировки по статусу'>"
                            + ((Object[]) DataList.get(i))[j].toString() +
                            " </a></li>\n");
                }
                j++;
            }
        }
        return builder.toString();
    }

    //заполнение контейнера данных web-таблицы на главной странице
    //выводим только то, что нужно на первой странице
    //соответственно заголовкам
    public void  setStringDatalistMainPage() {
        StringBuilder builder = new StringBuilder();
        builder.append("<tr>");
        for (int i = 0;i<DataList.size(); i++){
            builder.append("<th scope='row'>"+String.valueOf(i+1)+"</th>");
            int j = 0;
            for (String k:columnNames)
            {
                if (j == 0)
                    builder.append("<td>" + "<a href = '../detailed/?name=" + ((Object[])DataList.get(i))[columnNames.indexOf(k)].toString() + "' title='Нажмите для просмотра деталей'>"
                            + ((Object[])DataList.get(i))[columnNames.indexOf(k)].toString() + "</a></td>");
                else {
                    //выделяем статус ошибки
                    if (k.equals("last_status")) {
                        if (((Object[]) DataList.get(i))[columnNames.indexOf(k)].toString().equals("OK")) {
                            builder.append("<td><span class=\"badge badge-success\">" + ((Object[]) DataList.get(i))[columnNames.indexOf(k)].toString() + "</span></td>");
                        }
                        else if (((Object[]) DataList.get(i))[columnNames.indexOf(k)].toString().equals("ON_APPLY") ||
                                ((Object[]) DataList.get(i))[columnNames.indexOf(k)].toString().equals("WAIT_FOR_APPLY")) {
                            builder.append("<td><span class=\"badge badge-primary\">" + ((Object[]) DataList.get(i))[columnNames.indexOf(k)].toString() + "</span></td>");
                        } else {
                            builder.append("<td><span class=\"badge badge-danger\">" + ((Object[]) DataList.get(i))[columnNames.indexOf(k)].toString() + "</span></td>");
                        }
                    }
                    else if (!k.equals("table_nm") && !k.equals("source_id") && !k.equals("config_id")) {
                        builder.append("<td>" + ((Object[]) DataList.get(i))[columnNames.indexOf(k)].toString() + "</td>");
                    }
                }
                j++;
            }
            builder.append("</tr>");
            stringDatalistMainPage = builder.toString();
        }
    }

    public String getStringDatalistMainPage(){
        setStringDatalistMainPage();
        return stringDatalistMainPage;
    }

    /*******
     * Описание логики формирования html-страницы с детальной информацией
     ******/
    //заполнение заголовка web-таблицы с детальной информацией
    public void  setStringColumnListDetailed() {
        //колонка с номером
        StringBuilder builder = new StringBuilder();
        builder.append("<th class='align-middle' scope='col'>№</th>");
        int i = 0;
        while (i < columnNames.size()) {
            if (columnNames.get(i).contains("last_extract_log_file"))
            {
                builder.append("<th class='align-middle' scope='col'>extract status description</th>");
            }
            else if (columnNames.get(i).contains("last_apply_log_file")){
                builder.append("<th class='align-middle' scope='col'>apply status description</th>");
            }
            else
            {
                builder.append("<th class='align-middle' scope='col'>" + columnNames.get(i) + "</th>");
            }
            i++;
        }
        stringColumnListDetailed = builder.toString();
    }

    public String getStringColumnListDetailed(){
        setStringColumnListDetailed();
        return stringColumnListDetailed;
    }

    public String getString_source_id() {
        return String_source_id;
    }

    public String getString_config_id() {
        return String_config_id;
    }

    // заполнение данных html-таблицы из SQL запроса
    public void setStringDatalist(String formatClass){
        StringBuilder builder = new StringBuilder();
        for (int i = 0;i<DataList.size(); i++){
            builder.append("<tr "+formatClass+"><th scope='row'>"+String.valueOf(i+1)+"</th>");
            int j = 0;
            while (j < ((Object[]) DataList.get(i)).length) {
                //проверяем есть ли в результате запроса поле last_extract_log_file
                if (columnNames.get(j).equals("last_extract_log_file")) {
                    stringGrepDataExtract = ((Object[])DataList.get(i))[j].toString();
                    //выполняем команду GREP
                    if (stringGrepDataExtract!=null) {
                        //выводим данные на экран
                        if (stringGrepDataExtract.equals("0")){
                            builder.append("<td><font color=\"green\">Extract выполнен успешно</font></td>");
                        }
                        else {
                            builder.append("<td>" +
                                    "<button type=\"button\" class=\"btn btn-danger\" data-button='../getExtractGrep/?extractGrep=" + stringGrepDataExtract + "'>Посмотреть extract ошибку</button>");
                        }
                    }
                }
                else if (columnNames.get(j).equals("last_apply_log_file")){
                    stringGrepDataApply= ((Object[])DataList.get(i))[j].toString();
                    //выполняем команду GREP
                    if (stringGrepDataApply!=null) {
                        if (stringGrepDataApply.equals("0")){
                            builder.append("<td><font color=\"red\">Apply не выполнялся</font></td>");
                        }
                        else if(stringGrepDataApply.equals("-1")){
                            builder.append("<td><font color=\"green\">Apply выполнен успешно</font></td>");
                        }
                        else{
                            builder.append("<td>" +
                                    "<button type=\"button\" class=\"btn btn-danger\" data-button='../getApplyGrep/?applyGrep="+stringGrepDataApply+"'>Посмотреть apply ошибку</button>");
                        }
                    }
                }
                else {
                    builder.append("<td>" + ((Object[]) DataList.get(i))[j].toString()  + "</td>");
                }
                j++;
            }
            builder.append("</tr>");
            stringDatalist = builder.toString();
        }
    }

    public String getStringDatalist(String formatClass){
        setStringDatalist(formatClass);
        return stringDatalist;
    }

    //заполняем тело таблиц для страницы с детальным отображением
    public void  setStringDatalistDetailed(String name) {
        stringDatalistDetailed = "";
        for (int i = 0;i<DataList.size(); i++){
            if (((Object[])DataList.get(i))[0].equals(name)) {
                int j = 0;
                while (j < ((Object[]) DataList.get(i)).length) {
                    if (columnNames.get(j).equals("table_nm")) {
                        stringTableName = ((Object[])DataList.get(i))[j].toString();
                    }
                    if (columnNames.get(j).equals("source_id")) {
                        String_source_id = ((Object[])DataList.get(i))[j].toString();
                    }
                    if (columnNames.get(j).equals("config_id")) {
                        String_config_id = ((Object[])DataList.get(i))[j].toString();
                    }
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

    public String getStringTableName() {
        return stringTableName;
    }

    /******
     * Реализация grep`ера
     *****/

    //выполнение shell-команды grep
    public String shellExecutor(String stringGrepData){
        Logger.setLog("Выполняем bash-комманду: " + stringGrepData);
        Process process;
        int result;
        StringBuilder stringBuilder = new StringBuilder();
        try {
            final ProcessBuilder processBuilder =
                    new ProcessBuilder("bash", "-c",stringGrepData);
            process = processBuilder.start();
            result = process.waitFor();
            Logger.setLog("Результат выполнения: " + result);

            InputStream stderr = process.getErrorStream();
            InputStream stdout = process.getInputStream();
            InputStreamReader isr = new InputStreamReader(stderr);
            InputStreamReader esr = new InputStreamReader(stdout);
            BufferedReader br = new BufferedReader(isr);
            BufferedReader br_er = new BufferedReader(esr);
            String line;
            do {
                while ((line = br_er.readLine()) != null) {
                    //парсим лог грепа
                    String[] parserString = line.split("\\| ");
                    stringBuilder.append(parserString[1] + '\n');
                }
                while ((line = br.readLine()) != null) {
                    stringBuilder.append(line + '\n');
                }
                Thread.sleep(1000);
            }while (process.isAlive() );
            process.destroy();
            br.close();
            br_er.close();
            esr.close();
            isr.close();
        }
        catch (IOException|InterruptedException e) {
            Logger.setLog("Ошибка:" + e.toString());
            e.printStackTrace();

        }
        return stringBuilder.toString();
    }
}
