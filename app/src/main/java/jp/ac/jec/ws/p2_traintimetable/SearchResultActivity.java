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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONException;
import org.w3c.dom.Text;

import java.util.ArrayList;

import static jp.ac.jec.ws.p2_traintimetable.R.*;
import static jp.ac.jec.ws.p2_traintimetable.R.layout.*;

public class SearchResultActivity extends AppCompatActivity {

    // TODO: 2021-07-24 RowModelAdapterを外部クラス化したい

    public static final String YOUR_CONSUMER_KEY = "";

    private ListView listSearchResult;
    private Intent getIntent;
    private String userSearchStationName;
    public RowModelAdapter arrayAdapter;
    SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(activity_search_result);

        preferences = getSharedPreferences("saveData",Context.MODE_PRIVATE);

        TextView textViewUserSearchStationName = findViewById(id.textViewUserSearchStationName);
        listSearchResult = findViewById(R.id.listSearchResult);

        getIntent = getIntent();
        userSearchStationName = getIntent.getStringExtra("userSearchStationName");

        textViewUserSearchStationName.setText("駅：" + userSearchStationName);

        arrayAdapter = new RowModelAdapter(this);
        listSearchResult.setAdapter(arrayAdapter);

        getHttpsData();
        saveUserSearchStationName(userSearchStationName,"StationNameSave");

    }

    /**
     * ユーザーが検索した駅名と選択した路線名を
     * @param userSelect : 検索駅名 or 選択路線名
     * @param saveName : SharedPreferenceの保存名
     */
    private void saveUserSearchStationName(String userSelect, String saveName) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(saveName, userSelect);
        editor.apply();
    }

    private void getHttpsData() {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("https");
        builder.authority("api-tokyochallenge.odpt.org");
        builder.path("api/v4/odpt:Station");
        /*
         .appendQueryParameter:キーと値を引数に、各々エンコードしQuery文字列に追加する
         */
        builder.appendQueryParameter("dc:title", userSearchStationName);
        builder.appendQueryParameter("acl:consumerKey", YOUR_CONSUMER_KEY);

        MyAsync async = new MyAsync(this);
        async.execute(builder);
    }


    class RowModelAdapter extends ArrayAdapter<StationItems> {

//        private String TAG = "###";

        public RowModelAdapter(Context context) {
            super(context, row_item);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            final StationItems items = getItem(position);

            if (convertView == null) {

                LayoutInflater inflater = getLayoutInflater();
                convertView = inflater.inflate(row_item, null);

            }

            if (items != null) {
                final TextView textViewRailway = convertView.findViewById(R.id.textViewResult);

                if (textViewRailway != null) {
                    textViewRailway.setText(items.getRailWayName());

                    textViewRailway.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(SearchResultActivity.this, TrainTimeTableActivity.class);

                            intent.putExtra("userSelectRailWayTimetables", items.getTimetables().toString());

                            saveUserSearchStationName(textViewRailway.getText().toString(),"RailwayNameSave");

                            startActivity(intent);
                        }
                    });
                }
            }
            return convertView;
        }
    }
}
