package jp.ac.jec.ws.p2_traintimetable;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class JsonHelper {

    private static final String TAG = "###";

    public static ArrayList<StationItems> parseJsonRailway(String resString) {
        ArrayList<StationItems> list = new ArrayList<>();
        try {
            JSONArray json = new JSONArray(resString);
            for (int i = 0; i < json.length(); i++) {
                JSONObject items = json.getJSONObject(i);
                list.add(parseToItem(items, "railwayName"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static ArrayList<StationItems> parseJsonTimeTable(String resString) {
        ArrayList<StationItems> listTimetables = new ArrayList<>();
        try {
            JSONArray json = new JSONArray(resString);

            for (int i = 0; i < json.length(); i++) {
                JSONObject items = json.getJSONObject(i);
                JSONArray stationTimetableObject = items.getJSONArray("odpt:stationTimetableObject");

                for (int j = 0; j < stationTimetableObject.length(); j++) {

                    JSONObject stationTimetableObjectItem = stationTimetableObject.getJSONObject(j);
                    listTimetables.add(parseToItem(stationTimetableObjectItem, "timetable"));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return listTimetables;
    }

    private static StationItems parseToItem(JSONObject items, String praseItemName) throws JSONException {

        StationItems stationItems = new StationItems();

        if (praseItemName.equals("railwayName")){

            stationItems.setRailWayName(items.getString("odpt:railway"));
            stationItems.setTimetables(items.getJSONArray("odpt:stationTimetable"));

        } else if (praseItemName.equals("timetable")){

            stationItems.setDepartureTime(items.getString("odpt:departureTime"));

        }

        return stationItems;
    }
}
