package jp.ac.jec.ws.p2_traintimetable;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;

import static jp.ac.jec.ws.p2_traintimetable.R.layout.row_item;


public class TrainTimeTableActivity extends AppCompatActivity {

    public static final String YOUR_CONSUMER_KEY = "";

    private ListView listTrainTime;
    private Button buttonInbound;
    private Button buttonOutbound;
    private ImageButton imgButtonAddFavorite;

    private Boolean isButtonFavorite = true;
    private String trainTimetable;
    private String[] trainTimetables;

    private String stationName;
    private String railwayName;

    public RowModelAdapter modelAdapterTrainTimetable;

    SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_train_time_table);

        TextView textViewUserSelectRailway = findViewById(R.id.textViewUserSelectLine);
        listTrainTime = findViewById(R.id.listTrainTime);
        imgButtonAddFavorite = findViewById(R.id.imageButtonStar);
        buttonInbound = findViewById(R.id.buttonInbound);
        buttonOutbound = findViewById(R.id.buttonOutbound);

        Intent intent = getIntent();
        if (intent.getBooleanExtra("isIntent",false)){
            stationName = intent.getStringExtra("favoriteStationName");
            railwayName = intent.getStringExtra("favoriteRailWay");
            trainTimetable = intent.getStringExtra("favoriteTimetable");

            changeStringToArray(trainTimetable);

            isButtonFavorite = false;
            imgButtonAddFavorite.setImageResource(R.drawable.ic_star_black_24dp);

        } else {
            preferences = getSharedPreferences("saveData", Context.MODE_PRIVATE);
            stationName = preferences.getString("StationNameSave", "");
            railwayName = preferences.getString("RailwayNameSave", "");

            trainTimetable = intent.getStringExtra("userSelectRailWayTimetables");

            changeStringToArray(trainTimetable);
        }

        textViewUserSelectRailway.setText( stationName + "-" + railwayName);

        buttonInbound.setEnabled(false);
        buttonOutbound.setEnabled(true);

        buttonInbound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonInbound.setEnabled(false);
                buttonOutbound.setEnabled(true);
                modelAdapterTrainTimetable.clear();
                getHttpsData();
            }
        });

        buttonOutbound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonInbound.setEnabled(true);
                buttonOutbound.setEnabled(false);
                modelAdapterTrainTimetable.clear();
                getHttpsData();
            }
        });

        imgButtonAddFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isButtonFavorite) {
                    isButtonFavorite = false;
                    imgButtonAddFavorite.setImageResource(R.drawable.ic_star_black_24dp);

                    SQliteOpenHelper helper = new SQliteOpenHelper(TrainTimeTableActivity.this);
                    StationItems items = new StationItems();
                    items.setStationName(stationName);
                    items.setRailWayName(railwayName );
                    items.setTimetable(trainTimetable);
                    if (helper.insertDB(items)){
                        Toast.makeText(TrainTimeTableActivity.this, "??????????????????", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(TrainTimeTableActivity.this, "???????????????????????????", Toast.LENGTH_SHORT).show();
                    }

                } else {

                    isButtonFavorite = true;
                    imgButtonAddFavorite.setImageResource(R.drawable.ic_star_border_black_24dp);
                    SQliteOpenHelper helper = new SQliteOpenHelper(TrainTimeTableActivity.this);
                    if (helper.deleteDB(stationName)){
                        Toast.makeText(TrainTimeTableActivity.this, "????????????", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(TrainTimeTableActivity.this, "????????????", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        getHttpsData();
        modelAdapterTrainTimetable = new RowModelAdapter(this);
        listTrainTime.setAdapter(modelAdapterTrainTimetable);

    }

    /**
     * ??????????????????????????????trainTimetables???????????????
     *
     * @param trainTimetable : ?????????????????????Timetable???????????????
     */
    private void changeStringToArray(String trainTimetable) {
        String str = trainTimetable.substring(1, trainTimetable.length() - 1);
        String nextStr = str.replace("\",\"", " ");
        String secondNextStr = nextStr.replace("\"", "");
        trainTimetables = secondNextStr.split(" ");
    }


    // FIXME: 2021-07-24 java.lang.OutOfMemoryError????????? ??????????????????????????????
    // TODO: 2021-07-24 ??????????????????????????????????????????????????????

    private void getHttpsData() {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("https");
        builder.authority("api-tokyochallenge.odpt.org");
        builder.path("api/v4/odpt:StationTimetable");
        /*
         .appendQueryParameter:???????????????????????????????????????????????????Query????????????????????????
         */
        builder.appendQueryParameter("acl:consumerKey", YOUR_CONSUMER_KEY);

        /*
        Button???????????????????????????????????????Inbound?????????????????????
         */
        if (buttonInbound.isEnabled()) {
            //???????????????
            if (trainTimetables.length > 2){
                if (isWeekday()) {
                    builder.appendQueryParameter("owl:sameAs", trainTimetables[2]);
                } else {
                    builder.appendQueryParameter("owl:sameAs", trainTimetables[3]);
                }
            }
        } else {
            //???????????????
            Log.i("###", "getHttpsData:" + isWeekday());
            if (isWeekday()) {
                builder.appendQueryParameter("owl:sameAs", trainTimetables[0]);
            } else {
                builder.appendQueryParameter("owl:sameAs", trainTimetables[1]);
            }

        }

        MyAsync async = new MyAsync(this);
        async.execute(builder);
    }

    /**
     * ???????????????????????????
     * @return
     */
    private boolean isWeekday() {

        boolean isWeekday = true;
        Calendar calendar = Calendar.getInstance();

        switch (calendar.get(Calendar.DAY_OF_WEEK)) {
            case Calendar.SUNDAY:
            case Calendar.SATURDAY:
                isWeekday = false;
                break;
            case Calendar.MONDAY:
            case Calendar.TUESDAY:
            case Calendar.WEDNESDAY:
            case Calendar.THURSDAY:
            case Calendar.FRIDAY:
                isWeekday = true;
                break;
        }
        return isWeekday;
    }


    class RowModelAdapter extends ArrayAdapter<StationItems> {

        public RowModelAdapter(Context context) {
            super(context, row_item);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            StationItems items = getItem(position);

            if (convertView == null) {
                LayoutInflater inflater = getLayoutInflater();
                convertView = inflater.inflate(row_item, null);
            }

            if (items != null) {
                TextView textViewDepartureTime = convertView.findViewById(R.id.textViewResult);
                if (textViewDepartureTime != null) {
                    textViewDepartureTime.setText(items.getDepartureTime());
                }
            }
            return convertView;
        }
    }
}
