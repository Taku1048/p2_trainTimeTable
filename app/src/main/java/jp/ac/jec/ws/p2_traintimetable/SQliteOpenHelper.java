package jp.ac.jec.ws.p2_traintimetable;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class SQliteOpenHelper extends SQLiteOpenHelper {

    public SQliteOpenHelper(Context context) {
        super(context, "FavoriteStationDB", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String sql = "CREATE TABLE FAVORITES (_id INTEGER PRIMARY KEY AUTOINCREMENT, station_name Text, railway_name Text, railway_timetables Text)";

        db.execSQL(sql);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public ArrayList<StationItems> findAll() {
        ArrayList<StationItems> arrayList = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        if (db == null) {
            return null;
        }
        try {

            Cursor cursor = db.query("FAVORITES", new String[]{"station_name", "railway_name", "railway_timetables"}, null, null, null, null, null);
            while (cursor.moveToNext()) {
                StationItems items = new StationItems();
                items.setStationName(cursor.getString(0));
                items.setRailWayName(cursor.getString(1));
                items.setTimetable(cursor.getString(2));

                arrayList.add(items);
            }

        } catch (SQLiteException e) {
            e.printStackTrace();
        } finally {
            db.close();
        }
        return arrayList;
    }

    public boolean insertDB(StationItems item){
        ContentValues values = new ContentValues();
        values.put("station_name", item.getStationName());
        values.put("railway_name", item.getRailWayName());
        values.put("railway_timetables", item.getTimetable());

        SQLiteDatabase db = getWritableDatabase();
        long ret = -1;
        try {
            ret = db.insert("FAVORITES","",values);
        } catch (SQLiteException e){
            e.printStackTrace();
        } finally {
            db.close();
        }
        return ret > 0;
    }

    public boolean deleteDB(String deleteItem){
        SQLiteDatabase db = getWritableDatabase();
        long ret = -1;
        try {
            ret = db.delete("FAVORITES","station_name = ?",new String[]{deleteItem});
        } catch (SQLiteException e){
            e.printStackTrace();
        } finally {
            db.close();
        }
        return ret > 0;
    }

}
