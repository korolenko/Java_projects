package resource;

import View.DetailedView;
import View.MainView;
import core.*;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.sql.SQLException;
import java.util.Optional;

//API-сервисы и логика компоновки и отображения web-данных

@Path("/")
@Produces({MediaType.TEXT_HTML,MediaType.APPLICATION_JSON})
public class UIResource {
    private String stringDatalistMainPage;
    private String columnNamesMainPageString;
    private String stringColumnListDetailed;
    private String stringDatalistDetailed;
    private String stringGrepDataExtractResult;
    private String stringGrepDataApplyResult;
    private Table table;
    //private PostgreConnector postgreConnector;

    //private String md_table_ddl_sql;
    //private String tableName;
    private String stringColumnListSql2;
    private String stringDatalistSql2;

    private String string_source_id;
    private String string_config_id;
    private String stringTableName;

    private String stringColumnList_md_table_config;
    private String stringDataList_md_table_config;
    //private String md_table_config_SQL;

    //private String md_table_list_sql;

    //конструктор
    public UIResource() {
        super();
        stringDatalistMainPage = "";
        stringColumnListDetailed = "";
        stringDatalistDetailed = "";
        stringGrepDataExtractResult = "";
        stringGrepDataApplyResult = "";
        columnNamesMainPageString = "";

        string_source_id = "";
        string_config_id = "";
        stringTableName = "";

        stringColumnListSql2 = "";
        stringDatalistSql2 = "";

        stringColumnList_md_table_config = "";
        stringDataList_md_table_config = "";
        //tableName = "";
        //postgreConnector = new PostgreConnector();
    }
//выполняем коннект, затем запрос, сохраняем данные
    private Table getSQLResult(String sqlQuery){
        Table table = new Table();
        PostgreConnector postgreConnector = new PostgreConnector();
        try {
            postgreConnector.makeConnection();
            Logger.setLog("Выполняем запрос: " + sqlQuery);
            postgreConnector.makeSQLQuery(table,sqlQuery);
            postgreConnector.closeConnection();
        } catch (SQLException e) {
            Logger.setLog("Ошибка: " + e.toString());
        }
        return table;
    }

    @GET
    @Path("main")
    //обработчик логики основной страницы
    public MainView showViewTemplate(){
        //формируем данные для отображения на основной странице
        //(написал аналог запроса к представлению efw.v_aig_table_list, так как оно меленно работает)
        //StringBuilderы для ускорения
        StringBuilder v_aig_table_list_sql = new StringBuilder();
        v_aig_table_list_sql.append("select xxx.task_name,xxx.last_status, to_char(xxx.last_extract_attempt_dt,'yyyy:mm:dd HH24:MI:SS') \"last_extract_attempt_dt\", \n" +
                "    xxx.extract_duration,to_char(xxx.last_apply_attempt_dt,'yyyy:mm:dd HH24:MI:SS') \"last_apply_attempt_dt\",\n" +
                "     xxx.apply_duration, xxx.source_id, xxx.table_nm, xxx.config_id\n" +
                "                from \n" +
                "                 ( \n" +
                "                        select \n" +
                "                         md_journal.table_nm,\n" +
                "                        row_number() over(partition by md_journal.table_nm order by extract_task_start_datetime desc) rn, \n" +
                "                         database_nm ::text || '.' ::text ||  md_journal.table_nm \"task_name\",\n" +
                "                         CASE \n" +
                "                                    WHEN ((apply_task_end_datetime IS NOT NULL) AND (finished_cd = '-1'::integer)) THEN 'FAIL_ON_APPLY'::text\n" +
                "                                    WHEN ((extract_task_end_datetime IS NOT NULL) AND (finished_cd = '-1'::integer)) THEN 'FAIL_ON_EXTRACT'::text\n" +
                "                                    WHEN ((apply_task_end_datetime IS  NULL) AND extract_task_end_datetime is null) THEN 'FAIL'::text\n" +
                "                                    WHEN ((apply_task_end_datetime IS NOT NULL) AND (finished_cd = 0)) THEN 'OK'::text\n" +
                "                                    WHEN ((apply_task_start_datetime IS NOT NULL) AND (apply_task_end_datetime IS NULL)) THEN 'ON_APPLY'::text \n" +
                "                                    WHEN ((extract_task_end_datetime IS NOT NULL) AND (finished_cd = 0)) THEN 'WAIT_FOR_APPLY'::text\n" +
                "                                    WHEN (extract_task_end_datetime IS NULL) THEN 'ON_EXTRACT'::text\n" +
                "                                    ELSE NULL::text\n" +
                "                         END AS last_status,\n" +
                "                         finished_cd,\n" +
                "                         mtl.config_id,\n" +
                "                        extract_task_start_datetime \"last_extract_attempt_dt\", \n" +
                "                        to_char(extract_task_end_datetime - extract_task_start_datetime,'HH24:MI:SS') \"extract_duration\", \n" +
                "                        apply_task_start_datetime \"last_apply_attempt_dt\",\n" +
                "                        COALESCE(to_char(apply_task_end_datetime - apply_task_start_datetime,'HH24:MI:SS'),to_char(to_timestamp(-10800),'HH24:MI:SS')) \"apply_duration\", \n" +
                "                        md_journal.source_id\n" +
                "                        from  efw.md_journal \n" +
                "                        join efw.md_table_list mtl on mtl.source_id = md_journal.source_id and mtl.table_nm = md_journal.table_nm and mtl.t_active_flg = 1\n" +
                "                        where 1=1\n" +
                "                        order by extract_task_start_datetime desc \n" +
                "                  )xxx \n" +
                "                  where rn=1\n" +
                "  and last_status like '%FAIL%'");

        table = new Table();
        table = getSQLResult(v_aig_table_list_sql.toString());

        //заполненяем заголовок таблицы на главной странице
        columnNamesMainPageString = table.getColumnNamesMainPageString("ALL_FAILS");
        //заполняем данные таблицы
        stringDatalistMainPage = table.getStringDatalistMainPage();

        // определяем количество упавших джобов
        //FAIL_ON_EXTRACT
        StringBuilder failedExtractJobsSQL = new StringBuilder();
        failedExtractJobsSQL.append("select count(*)\n" +
                "                from \n" +
                "                 ( \n" +
                "                        select \n" +
                "                         md_journal.table_nm,\n" +
                "                        row_number() over(partition by md_journal.table_nm order by extract_task_start_datetime desc) rn, \n" +
                "                         database_nm ::text || '.' ::text ||  md_journal.table_nm \"task_name\",\n" +
                "                         CASE \n" +
                "                                    WHEN ((apply_task_end_datetime IS NOT NULL) AND (finished_cd = '-1'::integer)) THEN 'FAIL_ON_APPLY'::text\n" +
                "                                    WHEN ((extract_task_end_datetime IS NOT NULL) AND (finished_cd = '-1'::integer)) THEN 'FAIL_ON_EXTRACT'::text\n" +
                "                                    WHEN ((apply_task_end_datetime IS  NULL) AND extract_task_end_datetime is null) THEN 'FAIL'::text\n" +
                "                                    WHEN ((apply_task_end_datetime IS NOT NULL) AND (finished_cd = 0)) THEN 'OK'::text\n" +
                "                                    WHEN ((apply_task_start_datetime IS NOT NULL) AND (apply_task_end_datetime IS NULL)) THEN 'ON_APPLY'::text \n" +
                "                                    WHEN ((extract_task_end_datetime IS NOT NULL) AND (finished_cd = 0)) THEN 'WAIT_FOR_APPLY'::text\n" +
                "                                    WHEN (extract_task_end_datetime IS NULL) THEN 'ON_EXTRACT'::text\n" +
                "                                    ELSE NULL::text\n" +
                "                         END AS last_status,\n" +
                "                         finished_cd,\n" +
                "                         mtl.config_id,\n" +
                "                        extract_task_start_datetime \"last_extract_attempt_dt\", \n" +
                "                        to_char(extract_task_end_datetime - extract_task_start_datetime,'HH24:MI:SS') \"extract_duration\", \n" +
                "                        apply_task_start_datetime \"last_apply_attempt_dt\",\n" +
                "                        COALESCE(to_char(apply_task_end_datetime - apply_task_start_datetime,'HH24:MI:SS'),to_char(to_timestamp(-10800),'HH24:MI:SS')) \"apply_duration\", \n" +
                "                        md_journal.source_id\n" +
                "                        from  efw.md_journal \n" +
                "                        join efw.md_table_list mtl on mtl.source_id = md_journal.source_id and mtl.table_nm = md_journal.table_nm and mtl.t_active_flg = 1\n" +
                "                        where 1=1\n" +
                "                        order by extract_task_start_datetime desc \n" +
                "                  )xxx \n" +
                "                  where rn=1 \n" +
                "                  and last_status = 'FAIL_ON_EXTRACT'");
        //FAIL_ON_APPLY
        StringBuilder failedApplyJobsSQL = new StringBuilder();
        failedApplyJobsSQL.append("select count(*)\n" +
                "                from \n" +
                "                 ( \n" +
                "                        select \n" +
                "                         md_journal.table_nm,\n" +
                "                        row_number() over(partition by md_journal.table_nm order by extract_task_start_datetime desc) rn, \n" +
                "                         database_nm ::text || '.' ::text ||  md_journal.table_nm \"task_name\",\n" +
                "                         CASE \n" +
                "                                    WHEN ((apply_task_end_datetime IS NOT NULL) AND (finished_cd = '-1'::integer)) THEN 'FAIL_ON_APPLY'::text\n" +
                "                                    WHEN ((extract_task_end_datetime IS NOT NULL) AND (finished_cd = '-1'::integer)) THEN 'FAIL_ON_EXTRACT'::text\n" +
                "                                    WHEN ((apply_task_end_datetime IS  NULL) AND extract_task_end_datetime is null) THEN 'FAIL'::text\n" +
                "                                    WHEN ((apply_task_end_datetime IS NOT NULL) AND (finished_cd = 0)) THEN 'OK'::text\n" +
                "                                    WHEN ((apply_task_start_datetime IS NOT NULL) AND (apply_task_end_datetime IS NULL)) THEN 'ON_APPLY'::text \n" +
                "                                    WHEN ((extract_task_end_datetime IS NOT NULL) AND (finished_cd = 0)) THEN 'WAIT_FOR_APPLY'::text\n" +
                "                                    WHEN (extract_task_end_datetime IS NULL) THEN 'ON_EXTRACT'::text\n" +
                "                                    ELSE NULL::text\n" +
                "                         END AS last_status,\n" +
                "                         finished_cd,\n" +
                "                         mtl.config_id,\n" +
                "                        extract_task_start_datetime \"last_extract_attempt_dt\", \n" +
                "                        to_char(extract_task_end_datetime - extract_task_start_datetime,'HH24:MI:SS') \"extract_duration\", \n" +
                "                        apply_task_start_datetime \"last_apply_attempt_dt\",\n" +
                "                        COALESCE(to_char(apply_task_end_datetime - apply_task_start_datetime,'HH24:MI:SS'),to_char(to_timestamp(-10800),'HH24:MI:SS')) \"apply_duration\", \n" +
                "                        md_journal.source_id\n" +
                "                        from  efw.md_journal \n" +
                "                        join efw.md_table_list mtl on mtl.source_id = md_journal.source_id and mtl.table_nm = md_journal.table_nm and mtl.t_active_flg = 1\n" +
                "                        where 1=1\n" +
                "                        order by extract_task_start_datetime desc \n" +
                "                  )xxx \n" +
                "                  where rn=1 \n" +
                "                  and last_status = 'FAIL_ON_APPLY'");

        //определяем количество вкладок с статусами
        final String distinctStatusesSQL = "select distinct last_status from efw.v_aig_table_list \n" +
                "union \n" +
                "select 'ALL_STATUSES' \"last_status\"";

        //формируем данные, заполняем параметры для отображения на html-странице
        Table failedExtractJobsSQLTable = getSQLResult(failedExtractJobsSQL.toString());
        String failedOnExtract =failedExtractJobsSQLTable.getCount();

        Table failedApplyJobsSQLTable = getSQLResult(failedApplyJobsSQL.toString());
        String failedOnApply =failedApplyJobsSQLTable.getCount();

        //формируем вкладки со статусами
        //отображаем на активной вкладке все упавшие задания
        //на вход передаем количество упавших джобов для отображения в span
        Table distinctStatusesSQLTable = getSQLResult(distinctStatusesSQL);
        String distinctStatuses = distinctStatusesSQLTable.getStatuses(failedOnExtract,failedOnApply);

        //передаем полученные данные в макет отображения
        return new MainView(stringDatalistMainPage,columnNamesMainPageString,failedOnExtract,failedOnApply,distinctStatuses);
    }

    @GET
    @Path("detailed")
    // обработчик логики страницы с детальной информацией
    public DetailedView showDetail(@QueryParam("name") Optional<String> name){
        //получаем из get-запроса название задания
        final String value = name.get();
        table.getStringDatalistDetailed(value);

        stringTableName = "";
        string_source_id = "";
        string_config_id = "";

        stringTableName = table.getStringTableName();
        string_source_id = table.getString_source_id();
        string_config_id = table.getString_config_id();

        //формируем данные таблиц на странице с детальной информацией
        String stringColumnList_md_table_list = "";
        String stringDataList_md_table_list = "";
        String stringColumnList_md_table_state = "";
        String stringDataList_md_table_state = "";
        String md_table_ddl_sql;
        String md_table_config_SQL;
        String md_table_list_sql;
        if(stringTableName != null && string_source_id != null){
            Table md_table_list_Table;
            StringBuilder builder = new StringBuilder();
            builder.append("SELECT to_char(mmm.extract_task_start_datetime,'yyyy:mm:dd HH24:MI:SS')::text || ' (' ::text || to_char(mmm.extract_task_end_datetime - mmm.extract_task_start_datetime,'HH24:MI:SS') ::text || ')' \"extract start time (duration)\",\n" +
                    "case when mmm.apply_task_start_datetime is not null then 0 \n" +
                    "    else mmm.journal_id  end AS last_extract_log_file,\n" +
                    "to_char(mmm.apply_task_start_datetime,'yyyy:mm:dd HH24:MI:SS')::text || ' (' ::text || to_char(mmm.apply_task_end_datetime - mmm.apply_task_start_datetime,'HH24:MI:SS') ::text || ')' \"apply start time (duration)\",\n" +
                    "case when mmm.apply_task_start_datetime is null then 0\n" +
                        "when mmm.finished_cd = 0 then -1\n" +
                    "    else mmm.journal_id end AS last_apply_log_file,\n" +
                    "mmm.finished_cd,mmm.source_id,mmm.rows_count,mmm.incr_rows_count\n" +
                    "from efw.md_journal mmm WHERE mmm.table_nm = '"+stringTableName+"'\n" +
                    "  and mmm.source_id = '"+string_source_id+"'\n" +
                    "ORDER BY mmm.journal_id DESC");
            md_table_list_sql = builder.toString();
            md_table_list_Table = getSQLResult(md_table_list_sql);
            stringColumnListDetailed = md_table_list_Table.getStringColumnListDetailed();
            stringDatalistDetailed = md_table_list_Table.getStringDatalist("class=\"table-info\"");

            //выполнение запроса к efw.md_table_list
            Table md_table_list_T;
            String md_table_list_SQL = "select * from efw.md_table_list where source_id = '"+string_source_id+"' and table_nm = '"+stringTableName+"'";
            md_table_list_T = getSQLResult(md_table_list_SQL);
            stringColumnList_md_table_list = md_table_list_T.getStringColumnListDetailed();
            stringDataList_md_table_list = md_table_list_T.getStringDatalist("class=\"table-info\"");

            //выполнение запроса к efw.md_table_state
            Table md_table_state_T;
            String md_table_state_SQL = "select * from efw.md_table_state where source_id = '"+string_source_id+"' and table_nm = '"+stringTableName+"'";
            md_table_state_T = getSQLResult(md_table_state_SQL);
            stringColumnList_md_table_state = md_table_state_T.getStringColumnListDetailed();
            stringDataList_md_table_state = md_table_state_T.getStringDatalist("class=\"table-info\"");
        }
        //выполнение запроса к efw.md_table_ddl
        if (stringTableName != null){
            Table md_table_ddl_Table;
            md_table_ddl_sql = "SELECT column_nm,src_data_type,hdp_data_type FROM efw.md_table_ddl WHERE table_nm ='"+stringTableName+"'";
            md_table_ddl_Table = getSQLResult(md_table_ddl_sql);
            stringColumnListSql2 = md_table_ddl_Table.getStringColumnListDetailed();
            stringDatalistSql2  = md_table_ddl_Table.getStringDatalist("class=\"table-info\"");

        //выполнение запроса к efw.md_table_config
            Table md_table_config_Table;
            md_table_config_SQL = "SELECT * FROM efw.md_table_config WHERE config_id = '"+string_config_id+"'";
            md_table_config_Table = getSQLResult(md_table_config_SQL);
            stringColumnList_md_table_config = md_table_config_Table.getStringColumnListDetailed();
            stringDataList_md_table_config  = md_table_config_Table.getStringDatalist("class=\"table-info\"");
        }

        //передаем данные в шаблон для отображения на html странице
        return new DetailedView(value,stringColumnListDetailed,stringDatalistDetailed,stringGrepDataExtractResult,stringGrepDataApplyResult,
                stringColumnListSql2,stringDatalistSql2,stringColumnList_md_table_config,stringDataList_md_table_config,
                stringColumnList_md_table_list,stringDataList_md_table_list, stringColumnList_md_table_state,stringDataList_md_table_state);
    }

    //API получения описания extract ошибки
    @GET
    @Path("getExtractGrep")
    public getGrep showExtractGrepResult(@QueryParam("extractGrep") Optional<String> grepCommand){
        final String grepResult = grepCommand.get();
        Table grepTable;
        StringBuilder grepResultSQL = new StringBuilder();
        grepResultSQL.append("SELECT ((((('grep ERROR '::text || (mmm.extract_log_file)::text) || '*|grep '''::text) || '\\.'::text) || (mmm.table_nm)::text) || ' '''::text) AS last_extract_log_file\n" +
                "from efw.md_journal mmm\n" +
                "WHERE \n" +
                "  mmm.finished_cd <>0 \n" +
                "  and mmm.journal_id = '"+grepResult+"'");
        grepTable = getSQLResult(grepResultSQL.toString());
        String finalGrepResult = grepTable.shellExecutor(grepTable.getCount());
        return new getGrep(finalGrepResult);
    }

    //API получения описания apply ошибки
    @GET
    @Path("getApplyGrep")
    public getGrep showApplyGrepResult(@QueryParam("applyGrep") Optional<String> grepCommand){
         String grepResult = "";
         if (grepCommand.isPresent()) {
             grepResult = grepCommand.get();
         }
        Table grepTable;
        String grepResultSQL = "SELECT ((((('grep ERROR /u01/opt/efw/log/apply/_INFO.log'::text || '*|grep '' '::text) || (mtl.hdp_database_nm)::text) || '\\.'::text) || (mmm.table_nm)::text) || ' '''::text) AS last_apply_log_file\n" +
                "from efw.md_journal mmm\n" +
                "join efw.md_table_list mtl on mmm.table_nm = mtl.table_nm and mmm.source_id = mtl.source_id\n" +
                "WHERE mmm.finished_cd <>0 " +
                "  and mmm.journal_id = '"+grepResult+"'";
        grepTable = getSQLResult(grepResultSQL);
        String finalGrepResult = grepTable.shellExecutor(grepTable.getCount());
        return new getGrep(finalGrepResult);
    }

    //API сортировка заданий по статусам
    @GET
    @Path("getJobsData")
    public getJobsData showJobsData(@QueryParam("status") Optional<String> status) {
        String jobStatus = "";
        if(status.isPresent()) {
            jobStatus = status.get();
        }
        StringBuilder showJobsDataSQL = new StringBuilder();
        if (jobStatus.trim().equals("ALL_STATUSES")){
            showJobsDataSQL.append("select xxx.task_name,xxx.last_status, to_char(xxx.last_extract_attempt_dt,'yyyy:mm:dd HH24:MI:SS') \"last_extract_attempt_dt\", \n" +
                    "    xxx.extract_duration,to_char(xxx.last_apply_attempt_dt,'yyyy:mm:dd HH24:MI:SS') \"last_apply_attempt_dt\",\n" +
                    "     xxx.apply_duration, xxx.source_id, xxx.table_nm, xxx.config_id\n" +
                    "                from \n" +
                    "                 ( \n" +
                    "                        select \n" +
                    "                         md_journal.table_nm,\n" +
                    "                        row_number() over(partition by md_journal.table_nm order by extract_task_start_datetime desc) rn, \n" +
                    "                         database_nm ::text || '.' ::text ||  md_journal.table_nm \"task_name\",\n" +
                    "                         CASE \n" +
                    "                                    WHEN ((apply_task_end_datetime IS NOT NULL) AND (finished_cd = '-1'::integer)) THEN 'FAIL_ON_APPLY'::text\n" +
                    "                                    WHEN ((extract_task_end_datetime IS NOT NULL) AND (finished_cd = '-1'::integer)) THEN 'FAIL_ON_EXTRACT'::text\n" +
                    "                                    WHEN ((apply_task_end_datetime IS  NULL) AND extract_task_end_datetime is null) THEN 'FAIL'::text\n" +
                    "                                    WHEN ((apply_task_end_datetime IS NOT NULL) AND (finished_cd = 0)) THEN 'OK'::text\n" +
                    "                                    WHEN ((apply_task_start_datetime IS NOT NULL) AND (apply_task_end_datetime IS NULL)) THEN 'ON_APPLY'::text \n" +
                    "                                    WHEN ((extract_task_end_datetime IS NOT NULL) AND (finished_cd = 0)) THEN 'WAIT_FOR_APPLY'::text\n" +
                    "                                    WHEN (extract_task_end_datetime IS NULL) THEN 'ON_EXTRACT'::text\n" +
                    "                                    ELSE NULL::text\n" +
                    "                         END AS last_status,\n" +
                    "                         finished_cd,\n" +
                    "                         mtl.config_id,\n" +
                    "                        extract_task_start_datetime \"last_extract_attempt_dt\", \n" +
                    "                        to_char(extract_task_end_datetime - extract_task_start_datetime,'HH24:MI:SS') \"extract_duration\", \n" +
                    "                        apply_task_start_datetime \"last_apply_attempt_dt\",\n" +
                    "                        COALESCE(to_char(apply_task_end_datetime - apply_task_start_datetime,'HH24:MI:SS'),to_char(to_timestamp(-10800),'HH24:MI:SS')) \"apply_duration\", \n" +
                    "                        md_journal.source_id\n" +
                    "                        from  efw.md_journal \n" +
                    "                        join efw.md_table_list mtl on mtl.source_id = md_journal.source_id and mtl.table_nm = md_journal.table_nm and mtl.t_active_flg = 1\n" +
                    "                        where 1=1\n" +
                    "                        order by extract_task_start_datetime desc \n" +
                    "                  )xxx \n" +
                    "                  where rn=1 ");
        }
        else if (jobStatus.trim().equals("ALL_FAILS")){
            showJobsDataSQL.append("select xxx.task_name,xxx.last_status, to_char(xxx.last_extract_attempt_dt,'yyyy:mm:dd HH24:MI:SS') \"last_extract_attempt_dt\", \n" +
                    "    xxx.extract_duration,to_char(xxx.last_apply_attempt_dt,'yyyy:mm:dd HH24:MI:SS') \"last_apply_attempt_dt\",\n" +
                    "     xxx.apply_duration, xxx.source_id, xxx.table_nm, xxx.config_id\n" +
                    "                from \n" +
                    "                 ( \n" +
                    "                        select \n" +
                    "                         md_journal.table_nm,\n" +
                    "                        row_number() over(partition by md_journal.table_nm order by extract_task_start_datetime desc) rn, \n" +
                    "                         database_nm ::text || '.' ::text ||  md_journal.table_nm \"task_name\",\n" +
                    "                         CASE \n" +
                    "                                    WHEN ((apply_task_end_datetime IS NOT NULL) AND (finished_cd = '-1'::integer)) THEN 'FAIL_ON_APPLY'::text\n" +
                    "                                    WHEN ((extract_task_end_datetime IS NOT NULL) AND (finished_cd = '-1'::integer)) THEN 'FAIL_ON_EXTRACT'::text\n" +
                    "                                    WHEN ((apply_task_end_datetime IS  NULL) AND extract_task_end_datetime is null) THEN 'FAIL'::text\n" +
                    "                                    WHEN ((apply_task_end_datetime IS NOT NULL) AND (finished_cd = 0)) THEN 'OK'::text\n" +
                    "                                    WHEN ((apply_task_start_datetime IS NOT NULL) AND (apply_task_end_datetime IS NULL)) THEN 'ON_APPLY'::text \n" +
                    "                                    WHEN ((extract_task_end_datetime IS NOT NULL) AND (finished_cd = 0)) THEN 'WAIT_FOR_APPLY'::text\n" +
                    "                                    WHEN (extract_task_end_datetime IS NULL) THEN 'ON_EXTRACT'::text\n" +
                    "                                    ELSE NULL::text\n" +
                    "                         END AS last_status,\n" +
                    "                         finished_cd,\n" +
                    "                         mtl.config_id,\n" +
                    "                        extract_task_start_datetime \"last_extract_attempt_dt\", \n" +
                    "                        to_char(extract_task_end_datetime - extract_task_start_datetime,'HH24:MI:SS') \"extract_duration\", \n" +
                    "                        apply_task_start_datetime \"last_apply_attempt_dt\",\n" +
                    "                        COALESCE(to_char(apply_task_end_datetime - apply_task_start_datetime,'HH24:MI:SS'),to_char(to_timestamp(-10800),'HH24:MI:SS')) \"apply_duration\", \n" +
                    "                        md_journal.source_id\n" +
                    "                        from  efw.md_journal \n" +
                    "                        join efw.md_table_list mtl on mtl.source_id = md_journal.source_id and mtl.table_nm = md_journal.table_nm and mtl.t_active_flg = 1\n" +
                    "                        where 1=1\n" +
                    "                        order by extract_task_start_datetime desc \n" +
                    "                  )xxx \n" +
                    "                  where rn=1 and last_status like '%FAIL%'");
        }
        else {
            showJobsDataSQL.append("select xxx.task_name,xxx.last_status, to_char(xxx.last_extract_attempt_dt,'yyyy:mm:dd HH24:MI:SS') \"last_extract_attempt_dt\", \n" +
                    "    xxx.extract_duration,to_char(xxx.last_apply_attempt_dt,'yyyy:mm:dd HH24:MI:SS') \"last_apply_attempt_dt\",\n" +
                    "     xxx.apply_duration, xxx.source_id, xxx.table_nm, xxx.config_id\n" +
                    "                from \n" +
                    "                 ( \n" +
                    "                        select \n" +
                    "                         md_journal.table_nm,\n" +
                    "                        row_number() over(partition by md_journal.table_nm order by extract_task_start_datetime desc) rn, \n" +
                    "                         database_nm ::text || '.' ::text ||  md_journal.table_nm \"task_name\",\n" +
                    "                         CASE \n" +
                    "                                    WHEN ((apply_task_end_datetime IS NOT NULL) AND (finished_cd = '-1'::integer)) THEN 'FAIL_ON_APPLY'::text\n" +
                    "                                    WHEN ((extract_task_end_datetime IS NOT NULL) AND (finished_cd = '-1'::integer)) THEN 'FAIL_ON_EXTRACT'::text\n" +
                    "                                    WHEN ((apply_task_end_datetime IS  NULL) AND extract_task_end_datetime is null) THEN 'FAIL'::text\n" +
                    "                                    WHEN ((apply_task_end_datetime IS NOT NULL) AND (finished_cd = 0)) THEN 'OK'::text\n" +
                    "                                    WHEN ((apply_task_start_datetime IS NOT NULL) AND (apply_task_end_datetime IS NULL)) THEN 'ON_APPLY'::text \n" +
                    "                                    WHEN ((extract_task_end_datetime IS NOT NULL) AND (finished_cd = 0)) THEN 'WAIT_FOR_APPLY'::text\n" +
                    "                                    WHEN (extract_task_end_datetime IS NULL) THEN 'ON_EXTRACT'::text\n" +
                    "                                    ELSE NULL::text\n" +
                    "                         END AS last_status,\n" +
                    "                         finished_cd,\n" +
                    "                         mtl.config_id,\n" +
                    "                        extract_task_start_datetime \"last_extract_attempt_dt\", \n" +
                    "                        to_char(extract_task_end_datetime - extract_task_start_datetime,'HH24:MI:SS') \"extract_duration\", \n" +
                    "                        apply_task_start_datetime \"last_apply_attempt_dt\",\n" +
                    "                        COALESCE(to_char(apply_task_end_datetime - apply_task_start_datetime,'HH24:MI:SS'),to_char(to_timestamp(-10800),'HH24:MI:SS')) \"apply_duration\", \n" +
                    "                        md_journal.source_id\n" +
                    "                        from  efw.md_journal \n" +
                    "                        join efw.md_table_list mtl on mtl.source_id = md_journal.source_id and mtl.table_nm = md_journal.table_nm and mtl.t_active_flg = 1\n" +
                    "                        where 1=1\n" +
                    "                        order by extract_task_start_datetime desc \n" +
                    "                  )xxx \n" +
                    "                  where rn=1\n" +
                    "  and last_status = '" + jobStatus + "'");
        }

        table = new Table();
        table = getSQLResult(showJobsDataSQL.toString());
        //заполняем заголовки
        String finalJobStatusColumns = table.getColumnNamesMainPageString(jobStatus);
        //заполняем данные
        String finalJobStatusData = table.getStringDatalistMainPage();

        return new getJobsData(finalJobStatusColumns,finalJobStatusData);
    }

    //API сортировка заданий по длительности
    @GET
    @Path("getJobsDuration")
    public getGrep showJobsDuration(@QueryParam("type") Optional<String> type,@QueryParam("status") Optional<String> status){
        final String operationType = type.get();
        final String jobStatus = status.get();
        Table JobsDurationTable;
        StringBuilder JobsDurationSQL = new StringBuilder();
        if (jobStatus.equals("ALL_FAILS")){
            JobsDurationSQL.append("select xxx.task_name,xxx.last_status, to_char(xxx.last_extract_attempt_dt,'yyyy:mm:dd HH24:MI:SS') \"last_extract_attempt_dt\", \n" +
                    "    xxx.extract_duration,to_char(xxx.last_apply_attempt_dt,'yyyy:mm:dd HH24:MI:SS') \"last_apply_attempt_dt\",\n" +
                    "     xxx.apply_duration, xxx.source_id, xxx.table_nm, xxx.config_id\n" +
                    "                from \n" +
                    "                 ( \n" +
                    "                        select \n" +
                    "                         md_journal.table_nm,\n" +
                    "                        row_number() over(partition by md_journal.table_nm order by extract_task_start_datetime desc) rn, \n" +
                    "                         database_nm ::text || '.' ::text ||  md_journal.table_nm \"task_name\",\n" +
                    "                         CASE \n" +
                    "                                    WHEN ((apply_task_end_datetime IS NOT NULL) AND (finished_cd = '-1'::integer)) THEN 'FAIL_ON_APPLY'::text\n" +
                    "                                    WHEN ((extract_task_end_datetime IS NOT NULL) AND (finished_cd = '-1'::integer)) THEN 'FAIL_ON_EXTRACT'::text\n" +
                    "                                    WHEN ((apply_task_end_datetime IS  NULL) AND extract_task_end_datetime is null) THEN 'FAIL'::text\n" +
                    "                                    WHEN ((apply_task_end_datetime IS NOT NULL) AND (finished_cd = 0)) THEN 'OK'::text\n" +
                    "                                    WHEN ((apply_task_start_datetime IS NOT NULL) AND (apply_task_end_datetime IS NULL)) THEN 'ON_APPLY'::text \n" +
                    "                                    WHEN ((extract_task_end_datetime IS NOT NULL) AND (finished_cd = 0)) THEN 'WAIT_FOR_APPLY'::text\n" +
                    "                                    WHEN (extract_task_end_datetime IS NULL) THEN 'ON_EXTRACT'::text\n" +
                    "                                    ELSE NULL::text\n" +
                    "                         END AS last_status,\n" +
                    "                         finished_cd,\n" +
                    "                         mtl.config_id,\n" +
                    "                        extract_task_start_datetime \"last_extract_attempt_dt\", \n" +
                    "                        to_char(extract_task_end_datetime - extract_task_start_datetime,'HH24:MI:SS') \"extract_duration\", \n" +
                    "                        apply_task_start_datetime \"last_apply_attempt_dt\",\n" +
                    "                        COALESCE(to_char(apply_task_end_datetime - apply_task_start_datetime,'HH24:MI:SS'),to_char(to_timestamp(-10800),'HH24:MI:SS')) \"apply_duration\", \n" +
                    "                        md_journal.source_id\n" +
                    "                        from  efw.md_journal \n" +
                    "                        join efw.md_table_list mtl on mtl.source_id = md_journal.source_id and mtl.table_nm = md_journal.table_nm and mtl.t_active_flg = 1\n" +
                    "                        where 1=1\n" +
                    "                        order by extract_task_start_datetime desc \n" +
                    "                  )xxx \n" +
                    "                  where rn=1 and last_status like '%FAIL%'\n" +
                    "                  order by " + operationType + " desc");
        }
        else if (jobStatus.equals("ALL_STATUSES")){
            JobsDurationSQL.append("select xxx.task_name,xxx.last_status, to_char(xxx.last_extract_attempt_dt,'yyyy:mm:dd HH24:MI:SS') \"last_extract_attempt_dt\", \n" +
                    "    xxx.extract_duration,to_char(xxx.last_apply_attempt_dt,'yyyy:mm:dd HH24:MI:SS') \"last_apply_attempt_dt\",\n" +
                    "     xxx.apply_duration, xxx.source_id, xxx.table_nm, xxx.config_id\n" +
                    "                from \n" +
                    "                 ( \n" +
                    "                        select \n" +
                    "                         md_journal.table_nm,\n" +
                    "                        row_number() over(partition by md_journal.table_nm order by extract_task_start_datetime desc) rn, \n" +
                    "                         database_nm ::text || '.' ::text ||  md_journal.table_nm \"task_name\",\n" +
                    "                         CASE \n" +
                    "                                    WHEN ((apply_task_end_datetime IS NOT NULL) AND (finished_cd = '-1'::integer)) THEN 'FAIL_ON_APPLY'::text\n" +
                    "                                    WHEN ((extract_task_end_datetime IS NOT NULL) AND (finished_cd = '-1'::integer)) THEN 'FAIL_ON_EXTRACT'::text\n" +
                    "                                    WHEN ((apply_task_end_datetime IS  NULL) AND extract_task_end_datetime is null) THEN 'FAIL'::text\n" +
                    "                                    WHEN ((apply_task_end_datetime IS NOT NULL) AND (finished_cd = 0)) THEN 'OK'::text\n" +
                    "                                    WHEN ((apply_task_start_datetime IS NOT NULL) AND (apply_task_end_datetime IS NULL)) THEN 'ON_APPLY'::text \n" +
                    "                                    WHEN ((extract_task_end_datetime IS NOT NULL) AND (finished_cd = 0)) THEN 'WAIT_FOR_APPLY'::text\n" +
                    "                                    WHEN (extract_task_end_datetime IS NULL) THEN 'ON_EXTRACT'::text\n" +
                    "                                    ELSE NULL::text\n" +
                    "                         END AS last_status,\n" +
                    "                         finished_cd,\n" +
                    "                         mtl.config_id,\n" +
                    "                        extract_task_start_datetime \"last_extract_attempt_dt\", \n" +
                    "                        to_char(extract_task_end_datetime - extract_task_start_datetime,'HH24:MI:SS') \"extract_duration\", \n" +
                    "                        apply_task_start_datetime \"last_apply_attempt_dt\",\n" +
                    "                        COALESCE(to_char(apply_task_end_datetime - apply_task_start_datetime,'HH24:MI:SS'),to_char(to_timestamp(-10800),'HH24:MI:SS')) \"apply_duration\", \n" +
                    "                        md_journal.source_id\n" +
                    "                        from  efw.md_journal \n" +
                    "                        join efw.md_table_list mtl on mtl.source_id = md_journal.source_id and mtl.table_nm = md_journal.table_nm and mtl.t_active_flg = 1\n" +
                    "                        where 1=1\n" +
                    "                        order by extract_task_start_datetime desc \n" +
                    "                  )xxx \n" +
                    "                  where rn=1\n" +
                    "                  order by " + operationType + " desc");
        }
        else {
            JobsDurationSQL.append("select xxx.task_name,xxx.last_status, to_char(xxx.last_extract_attempt_dt,'yyyy:mm:dd HH24:MI:SS') \"last_extract_attempt_dt\", \n" +
                    "    xxx.extract_duration,to_char(xxx.last_apply_attempt_dt,'yyyy:mm:dd HH24:MI:SS') \"last_apply_attempt_dt\",\n" +
                    "     xxx.apply_duration, xxx.source_id, xxx.table_nm, xxx.config_id\n" +
                    "                from \n" +
                    "                 ( \n" +
                    "                        select \n" +
                    "                         md_journal.table_nm,\n" +
                    "                        row_number() over(partition by md_journal.table_nm order by extract_task_start_datetime desc) rn, \n" +
                    "                         database_nm ::text || '.' ::text ||  md_journal.table_nm \"task_name\",\n" +
                    "                         CASE \n" +
                    "                                    WHEN ((apply_task_end_datetime IS NOT NULL) AND (finished_cd = '-1'::integer)) THEN 'FAIL_ON_APPLY'::text\n" +
                    "                                    WHEN ((extract_task_end_datetime IS NOT NULL) AND (finished_cd = '-1'::integer)) THEN 'FAIL_ON_EXTRACT'::text\n" +
                    "                                    WHEN ((apply_task_end_datetime IS  NULL) AND extract_task_end_datetime is null) THEN 'FAIL'::text\n" +
                    "                                    WHEN ((apply_task_end_datetime IS NOT NULL) AND (finished_cd = 0)) THEN 'OK'::text\n" +
                    "                                    WHEN ((apply_task_start_datetime IS NOT NULL) AND (apply_task_end_datetime IS NULL)) THEN 'ON_APPLY'::text \n" +
                    "                                    WHEN ((extract_task_end_datetime IS NOT NULL) AND (finished_cd = 0)) THEN 'WAIT_FOR_APPLY'::text\n" +
                    "                                    WHEN (extract_task_end_datetime IS NULL) THEN 'ON_EXTRACT'::text\n" +
                    "                                    ELSE NULL::text\n" +
                    "                         END AS last_status,\n" +
                    "                         finished_cd,\n" +
                    "                         mtl.config_id,\n" +
                    "                        extract_task_start_datetime \"last_extract_attempt_dt\", \n" +
                    "                        to_char(extract_task_end_datetime - extract_task_start_datetime,'HH24:MI:SS') \"extract_duration\", \n" +
                    "                        apply_task_start_datetime \"last_apply_attempt_dt\",\n" +
                    "                        COALESCE(to_char(apply_task_end_datetime - apply_task_start_datetime,'HH24:MI:SS'),to_char(to_timestamp(-10800),'HH24:MI:SS')) \"apply_duration\", \n" +
                    "                        md_journal.source_id\n" +
                    "                        from  efw.md_journal \n" +
                    "                        join efw.md_table_list mtl on mtl.source_id = md_journal.source_id and mtl.table_nm = md_journal.table_nm and mtl.t_active_flg = 1\n" +
                    "                        where 1=1\n" +
                    "                        order by extract_task_start_datetime desc \n" +
                    "                  )xxx \n" +
                    "                  where rn=1\n" +
                    "                  and last_status = '" + jobStatus + "'\n" +
                    "                  order by " + operationType + " desc");
        }
        JobsDurationTable = getSQLResult(JobsDurationSQL.toString());
        String finalJobDuration= JobsDurationTable.getStringDatalistMainPage();
        return new getGrep(finalJobDuration);
    }
}

