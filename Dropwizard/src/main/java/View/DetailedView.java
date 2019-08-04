package View;

import io.dropwizard.views.View;

//класс-связь с шаблоном mustache
// нужен для описания отображаемых на шаблоне элементов
public class DetailedView extends View {
    private String taskName;
    private String stringColumnListDetailed;
    private String stringDatalistDetailed;
    private String stringGrepDataExtractResult;
    private String stringGrepDataApplyResult;
    private String stringColumnListSql2;
    private String stringDatalistSql2;
    private String stringColumnList_md_table_config;
    private String stringDataList_md_table_config;
    private String stringColumnList_md_table_list;
    private String stringDataList_md_table_list;
    private String stringColumnList_md_table_state;
    private String stringDataList_md_table_state;

    public DetailedView(String taskName, String stringColumnListDetailed, String stringDatalistDetailed, String stringGrepDataExtractResult,String stringGrepDataApplyResult,
                        String stringColumnListSql2,String stringDatalistSql2, String stringColumnList_md_table_config, String stringDataList_md_table_config,
                        String stringColumnList_md_table_list, String stringDataList_md_table_list, String stringColumnList_md_table_state,String stringDataList_md_table_state){
        super("/views/detailed.mustache");
        this.taskName = taskName;
        this.stringColumnListDetailed = stringColumnListDetailed;
        this.stringDatalistDetailed = stringDatalistDetailed;
        this.stringGrepDataExtractResult = stringGrepDataExtractResult;
        this.stringGrepDataApplyResult = stringGrepDataApplyResult;
        this.stringColumnListSql2 = stringColumnListSql2;
        this.stringDatalistSql2 = stringDatalistSql2;
        this.stringColumnList_md_table_config = stringColumnList_md_table_config;
        this.stringDataList_md_table_config = stringDataList_md_table_config;
        this.stringColumnList_md_table_list = stringColumnList_md_table_list;
        this.stringDataList_md_table_list = stringDataList_md_table_list;
        this.stringColumnList_md_table_state = stringColumnList_md_table_state;
        this.stringDataList_md_table_state = stringDataList_md_table_state;
    }
    public String getTaskName() {
        return taskName;
    }

    public String getStringColumnListDetailed() {
        return stringColumnListDetailed;
    }

    public String getStringDatalistDetailed() {
        return stringDatalistDetailed;
    }

    public String getStringGrepDataExtractResult() {
        return stringGrepDataExtractResult;
    }
    public String getStringGrepDataApplyResult() {
        return stringGrepDataApplyResult;
    }
    public String getStringColumnListSql2() {
        return stringColumnListSql2;
    }
    public String getStringDatalistSql2() {
        return stringDatalistSql2;
    }

    public String getStringColumnList_md_table_config() {
        return stringColumnList_md_table_config;
    }

    public String getStringDataList_md_table_config() {
        return stringDataList_md_table_config;
    }

    public String getStringColumnList_md_table_list() {
        return stringColumnList_md_table_list;
    }

    public String getStringDataList_md_table_list() {
        return stringDataList_md_table_list;
    }

    public String getStringColumnList_md_table_state() {
        return stringColumnList_md_table_state;
    }

    public String getStringDataList_md_table_state() {
        return stringDataList_md_table_state;
    }
}
