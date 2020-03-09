import com.opencsv.CSVWriter;
import org.json.CDL;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TestCSVWriter {
    private static final String CSV_FILE = "./CSV_FILE.csv";

    private void writeToCSV(List<String[]> data) throws IOException{
        File file = new File(CSV_FILE);
        FileWriter outputfile = new FileWriter(file);

        CSVWriter writer = new CSVWriter(outputfile, '|',
                CSVWriter.NO_QUOTE_CHARACTER,
                CSVWriter.DEFAULT_ESCAPE_CHARACTER,
                CSVWriter.DEFAULT_LINE_END);

        writer.writeAll(data);
        writer.close();
    }

    public static void main(String[] args) throws IOException {

        String jsonArrayString = "{\"key\": [{\"a\": \"1\",\"b\": \"2\",\"c\": \"3\"}]}";

        JSONObject output;
        output = new JSONObject(jsonArrayString);
        JSONArray docs = output.getJSONArray("key");
        String csv = CDL.toString(docs);

        //создаем JSON
        JSONObject obj = new JSONObject();
        obj.put("Name", "Ivan");
        obj.put("SecondName", "Ivanov");

        String stringJSON = obj.toString();
        System.out.println(stringJSON);

        List<String[]> data = new ArrayList<String[]>();
        data.add(new String[] { stringJSON});
        data.add(new String[] { csv});
        data.add(new String[] { "Aman", "10", "620" });
        data.add(new String[] { "Suraj", "10", "630" });

        TestCSVWriter testCSVWriter = new TestCSVWriter();
        testCSVWriter.writeToCSV(data);
    }
}
