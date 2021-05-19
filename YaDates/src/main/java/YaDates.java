import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.*;

import static java.time.DayOfWeek.MONDAY;
import static java.time.DayOfWeek.SUNDAY;
import static java.time.temporal.TemporalAdjusters.*;

public class YaDates {
    public static void main(String[] args) throws IOException {
        BufferedReader r = new BufferedReader(new InputStreamReader(System.in));
        String str;
        String[] x;
        String period;
        System.out.println("inter period type");
        period = r.readLine();

        System.out.println("inter days");
        str = r.readLine();
        x = str.split(" ");

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate start = LocalDate.parse(x[0], formatter);
        LocalDate end = LocalDate.parse(x[1], formatter);

        Map<LocalDate,LocalDate> dateMap = new LinkedHashMap<>();
        switch (period.toUpperCase()){
            case "WEEK": {
                LocalDate sunday;
                do {
                    LocalDate monday = start.with(previousOrSame(MONDAY));
                    if (monday.isBefore(start)) {
                        monday = start;
                    }
                    sunday = start.with(nextOrSame(SUNDAY));
                    if (sunday.isAfter(end)) {
                        sunday = end;
                    }
                    dateMap.put(monday, sunday);
                    start = sunday.plusDays(1);
                } while (end.isAfter(sunday));
                break;
            }
            case "MONTH":{
                LocalDate sunday;
                do {
                    LocalDate monday = start.withDayOfMonth(1);
                    if (monday.isBefore(start)) {
                        monday = start;
                    }
                    sunday = start.plusMonths(1).withDayOfMonth(1).minusDays(1);
                    if (sunday.isAfter(end)) {
                        sunday = end;
                    }
                    dateMap.put(monday, sunday);
                    start = sunday.plusDays(1);
                } while (end.isAfter(sunday));
                break;
            }
            case "QUARTER":{
                LocalDate sunday;
                do {
                    LocalDate monday = start.with(start.getMonth().firstMonthOfQuarter())
                            .with(TemporalAdjusters.firstDayOfMonth());
                    sunday = monday.plusMonths(2)
                            .with(TemporalAdjusters.lastDayOfMonth());
                    if (monday.isBefore(start)) {
                        monday = start;
                    }
                    if (sunday.isAfter(start.with(lastDayOfYear()))) {
                        sunday = start.with(lastDayOfYear());
                    }
                    if (sunday.isAfter(end)) {
                        sunday = end;
                    }
                    dateMap.put(monday, sunday);
                    start = sunday.plusDays(1);
                } while (end.isAfter(sunday));
                break;
            }
            case "REVIEW":{
                LocalDate sunday;
                do {
                    int month = start.getMonth().getValue();
                    LocalDate monday;
                    if(month <= 9){
                        monday = start.with(firstDayOfYear()).with(start.withMonth(4).withDayOfMonth(1));
                        if (monday.isBefore(start)) {
                            monday = start;
                        }
                        sunday = monday.plusMonths(7).withDayOfMonth(1).minusDays(1);
                        LocalDate limitEnd = start.with(firstDayOfYear()).withMonth(10).withDayOfMonth(1).minusDays(1);
                        if (sunday.isAfter(limitEnd)) {
                                  sunday = limitEnd;
                        }
                        if (sunday.isAfter(end)) {
                            sunday = end;
                        }
                        dateMap.put(monday, sunday);
                        start = sunday.plusDays(1);
                    }else{
                        System.out.println(6);
                        monday = start.with(firstDayOfYear()).withMonth(10).withDayOfMonth(1).minusDays(1);
                        if (monday.isBefore(start)) {
                            monday = start;
                        }
                        sunday = monday.plusMonths(7).withDayOfMonth(1).minusDays(1);
                        LocalDate sundayLimit = monday.plusYears(1).with(firstDayOfYear()).with(monday.plusYears(1).withMonth(4).withDayOfMonth(1)).minusDays(1);
                        if (sunday.isAfter(sundayLimit)) {
                            sunday = sundayLimit;
                        }
                        if (sunday.isAfter(end)) {
                            sunday = end;
                        }
                        dateMap.put(monday, sunday);
                        start = sunday;
                    }
                } while (end.isAfter(sunday));
                break;
            }
            case "YEAR":{
                LocalDate sunday;
                do {
                    LocalDate monday = start.with(firstDayOfYear());
                    if (monday.isBefore(start)) {
                        monday = start;
                    }
                    sunday = start.with(lastDayOfYear());
                    if (sunday.isAfter(end)) {
                        sunday = end;
                    }
                    dateMap.put(monday, sunday);
                    start = sunday.plusDays(1);
                } while (end.isAfter(sunday));
                break;
            }
        }

        System.out.println(dateMap.size());
        dateMap.forEach((s,e) ->{
            System.out.println( s + " " + e);
        });

    }
}
