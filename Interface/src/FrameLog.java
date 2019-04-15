import javax.swing.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FrameLog {
    static public void setLog(JTextArea jTextArea, String string){
        DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss yyyy/MM/dd ");
        Date date = new Date();
        String currentDate = dateFormat.format(date);
        jTextArea.append(currentDate + "  " + string + "\n") ;
    }
}
