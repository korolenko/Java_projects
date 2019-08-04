package core;

import com.fasterxml.jackson.annotation.JsonProperty;

//шаблон формата вывода Json-файла
public class getJobsData {
    private String jobsColumns;
    private String jobsRows;

    public getJobsData(String jobsColumns, String jobsRows){
        this.jobsColumns = jobsColumns;
        this.jobsRows = jobsRows;
    }
    @JsonProperty
    public String getJobsColumns() {
        return jobsColumns;
    }

    @JsonProperty
    public String getJobsRows() {
        return jobsRows;
    }
}
