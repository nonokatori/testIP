package main;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;


public class JSONTickets {

    private ArrayList<Long> flightTime = new ArrayList<>();


    public void readFile() {
        StringBuilder jsonStrBuilder = new StringBuilder();

        try(BufferedReader bufferedReader = new BufferedReader(new InputStreamReader
                (this.getClass().getClassLoader().getResourceAsStream("tickets.json")))) {
            String currentStr = null;
            while ((currentStr = bufferedReader.readLine()) != null) {
                jsonStrBuilder.append(currentStr); //считываем из файла в билдер всю информацию
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        int ind = jsonStrBuilder.indexOf("{");

        String jsonStr = jsonStrBuilder.substring(ind,jsonStrBuilder.length()) // берем подстроку, начинающуюся с {
                //если не использовать, то захватывает невесть откуда взявшийся дефис
                .replaceAll("( )+", " "); //удаляем лишние пробелы
        parseJSON(jsonStr);
    }

    private void parseJSON (String jsonStr) {

        JSONObject jsonObject = new JSONObject(jsonStr);
        JSONArray jsonArray = jsonObject.getJSONArray("tickets");
        StringBuilder departureBuilder = new StringBuilder();
        StringBuilder arrivalBuilder = new StringBuilder();

        for (int i = 0; i < jsonArray.length(); i++) {
            departureBuilder.append(jsonArray.getJSONObject(i).get("departure_date")).append(" ")
                    .append(jsonArray.getJSONObject(i).get("departure_time"));

            arrivalBuilder.append(jsonArray.getJSONObject(i).get("arrival_date")).append(" ")
                    .append(jsonArray.getJSONObject(i).get("arrival_time"));

            flightTime.add(calcFlightTime(departureBuilder.toString(), arrivalBuilder.toString()));
            departureBuilder.delete(0,departureBuilder.length());
            arrivalBuilder.delete(0,arrivalBuilder.length());
        }

    }

    private long calcFlightTime (String departure, String arrival) {

        DateTimeFormatter parseDate = DateTimeFormatter.ofPattern("dd.MM.yy H:mm");

        LocalDateTime parseDeparture = LocalDateTime.parse(departure,parseDate);
        LocalDateTime parseArrival = LocalDateTime.parse(arrival,parseDate);

        return ChronoUnit.MINUTES.between(parseDeparture,parseArrival);
    }

    private LocalTime averageTime() {
        long sumTime = 0L;
        for (int i = 0; i < flightTime.size(); i++) {
            sumTime+=flightTime.get(i);
        }
        long averageTime = sumTime/flightTime.size();

        return LocalTime.MIN.plus(Duration.ofMinutes(averageTime));
    }

    private LocalTime percent90 () {
        Collections.sort(flightTime);
        double n = 0.9*(flightTime.size()-1);
        long perc90 = (long)(flightTime.get((int)n)+(n%(int)n)*(flightTime.get((int)(n+1))-flightTime.get((int)n)));
        //perc90 = Xn + дробная часть Xn * (Xn+1 - Xn);
        return LocalTime.MIN.plus(Duration.ofMinutes(perc90));
    }

    public void outputOfResult () {
        System.out.println("Среднее время полета между городами Владивосток и Тель-Авив: " + averageTime());
        System.out.println("90-й процентиль времени полета между городами Владивосток и Тель-Авив: " + percent90());

    }
}
