package jp.ac.jec.ws.p2_traintimetable;

import org.json.JSONArray;

public class StationItems {

    // TODO: 2021-07-24 JSONArrayとStringを一緒にしたい 

    private String stationName;
    private String railWayName;
    private String departureTime;
    private JSONArray timetables;
    private String timetable;



    public StationItems() {

    }

    public StationItems(String stationName, String railWayName, String timetable) {
        this.stationName = stationName;
        this.railWayName = railWayName;
        this.timetable = timetable;
    }

    public String getTimetable() {
        return timetable;
    }

    public void setTimetable(String timetable) {
        this.timetable = timetable;
    }

    public String getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(String deparcherTime) {
        this.departureTime = deparcherTime;
    }

    public JSONArray getTimetables() {
        return timetables;
    }

    public void setTimetables(JSONArray timetables) {
        this.timetables = timetables;
    }

    public String getStationName() {
        return stationName;
    }

    public void setStationName(String stationName) {
        this.stationName = stationName;
    }

    public String getRailWayName() {
        return railWayName;
    }

    public void setRailWayName(String railWayName) {
        this.railWayName = railWayName;
    }

}
