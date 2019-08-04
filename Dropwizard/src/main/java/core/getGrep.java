package core;

import com.fasterxml.jackson.annotation.JsonProperty;

//шаблон формата вывода Json-файла
public class getGrep{

    private String grepLog;

    public getGrep(String grepLog) {
        this.grepLog = grepLog;
    }

    @JsonProperty
    public String getGrepLog() {
        return grepLog;
    }
}
