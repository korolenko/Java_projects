package View;

import io.dropwizard.views.View;

//класс-связь с шаблоном mustache
// нужен для описания отображаемых на шаблоне элементов
public class MainView extends View {
    private String stringDatalistMainPage;
    private String columnNamesMainPageString;
    private String failedOnExtract;
    private String failedOnApply;
    private String distinctStatuses;

    public MainView(String stringDatalistMainPage, String columnNamesMainPageString, String failedOnExtract, String failedOnApply, String distinctStatuses){
        super("/views/main.mustache");
        this.stringDatalistMainPage = stringDatalistMainPage;
        this.columnNamesMainPageString = columnNamesMainPageString;
        this.failedOnExtract = failedOnExtract;
        this.failedOnApply = failedOnApply;
        this.distinctStatuses = distinctStatuses;
    }

    public String getStringDatalistMainPage() {
        return stringDatalistMainPage;
    }
    public String getColumnNamesMainPageString(){
        return columnNamesMainPageString;
    }

    public String getFailedOnExtract() {
        return failedOnExtract;
    }

    public String getFailedOnApply() {
        return failedOnApply;
    }

    public String getDistinctStatuses() {
        return distinctStatuses;
    }
}
