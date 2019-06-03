package View;

import io.dropwizard.views.View;

public class DetailedView extends View {
    private String taskName;
    private String stringColumnListDetailed;
    private String stringDatalistDetailed;
    private String stringGrepDataResult;

    public DetailedView(String taskName, String stringColumnListDetailed, String stringDatalistDetailed, String stringGrepDataResult){
        super("/views/detailed.mustache");
        this.taskName = taskName;
        this.stringColumnListDetailed = stringColumnListDetailed;
        this.stringDatalistDetailed = stringDatalistDetailed;
        this.stringGrepDataResult = stringGrepDataResult;
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

    public String getStringGrepDataResult() {
        return stringGrepDataResult;
    }
}
