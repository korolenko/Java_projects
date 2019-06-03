package View;

import io.dropwizard.views.View;

public class MainView extends View {
    private String stringDatalistMainPage;
    private String columnNamesMainPageString;

    public MainView(String stringDatalistMainPage, String columnNamesMainPageString){
        super("/views/main.mustache");
        this.stringDatalistMainPage = stringDatalistMainPage;
        this.columnNamesMainPageString = columnNamesMainPageString;
    }

    public String getStringDatalistMainPage() {
        return stringDatalistMainPage;
    }
    public String getColumnNamesMainPageString(){
        return columnNamesMainPageString;
    }
}
