package resource;

import View.DetailedView;
import View.MainView;
import core.PostgreConnector;
import core.Table;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Optional;

@Path("/")
@Produces({MediaType.TEXT_HTML,MediaType.APPLICATION_JSON})
public class UIResource {
    private String stringDatalistMainPage;
    private String columnNamesMainPageString;
    private String stringColumnListDetailed;
    private String stringDatalistDetailed;
    private String stringGrepDataResult;
    private Table table;
    private PostgreConnector postgreConnector;

    public UIResource() {
        super();
        stringDatalistMainPage = "";
        stringColumnListDetailed = "";
        stringDatalistDetailed = "";
        stringGrepDataResult = "";
        columnNamesMainPageString = "";
        table = new Table();
        postgreConnector = new PostgreConnector();
    }

    @GET
    @Path("main")
    //обработчик логики основной страницы
    public MainView showViewTemplate(){
        try {
            postgreConnector.makeConnection();
            postgreConnector.makeSQLQuery(table);
            postgreConnector.closeConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        table.setColumnNamesMainPage();
        stringDatalistMainPage = table.getStringDatalistMainPage();
        columnNamesMainPageString = table.getColumnNamesMainPageString();

        return new MainView(stringDatalistMainPage,columnNamesMainPageString);
    }

    @GET
    @Path("detailed")
    // обработчик логики страницы с детальной информацией
    public DetailedView showDetail(@QueryParam("name") Optional<String> name) throws IOException {
        final String value = name.get();
        stringColumnListDetailed = table.getStringColumnListDetailed();
        stringDatalistDetailed = table.getStringDatalistDetailed(value);
        stringGrepDataResult = table.getStringGrepDataResult();

        return new DetailedView(value,stringColumnListDetailed,stringDatalistDetailed,stringGrepDataResult);
    }
}

