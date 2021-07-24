package jp.ac.jec.ws.p2_traintimetable;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ListView listView;
    private ArrayAdapter<StationItems> arrayAdapter;
    private ArrayList<StationItems> stationItems = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final EditText editTextUserSearchStation = findViewById(R.id.editTextUserSearchStation);
        ImageButton imgButtonSearch = findViewById(R.id.imageButtonSearch);

        listView = findViewById(R.id.listFavorite);

        imgButtonSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editTextUserSearchStation.getText().toString().equals("")){

                    Toast.makeText(MainActivity.this, "駅名を入力してください", Toast.LENGTH_SHORT).show();

                } else {
                    Intent intent = new Intent(MainActivity.this,SearchResultActivity.class);
                    intent.putExtra("userSearchStationName", editTextUserSearchStation.getText().toString());
                    startActivity(intent);
                }
            }
        });

        arrayAdapter = new FavoriteListAdapter(this,R.layout.favorite_row_item,stationItems);

    }

    @Override
    protected void onResume() {
        super.onResume();

        arrayAdapter.clear();

        SQliteOpenHelper helper = new SQliteOpenHelper(this);

        for (int i = 0; i < helper.findAll().size(); i++){
            StationItems item = new StationItems(helper.findAll().get(i).getStationName(),helper.findAll().get(i).getRailWayName(),helper.findAll().get(i).getTimetable());
            stationItems.add(item);
        }
        listView.setAdapter(arrayAdapter);
    }

    private class FavoriteListAdapter extends ArrayAdapter<StationItems> {

        private int resource;
        private List<StationItems> items;
        private LayoutInflater inflater;

        public FavoriteListAdapter(Context context, int resource, List<StationItems> objects) {
            super(context, resource, objects);

            this.resource = resource;
            items = objects;
            this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View view;

            if (convertView != null){
                view = convertView;
            } else {
                view = inflater.inflate(resource,null);
            }

            final StationItems stationItems = items.get(position);
            final TextView textViewFavoriteInfo = view.findViewById(R.id.textViewFavoriteName);
            Button buttonDelete = view.findViewById(R.id.buttonDelete);

            textViewFavoriteInfo.setText(stationItems.getStationName() + "-" + stationItems.getRailWayName());

            textViewFavoriteInfo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MainActivity.this,TrainTimeTableActivity.class);

                    intent.putExtra("favoriteStationName",stationItems.getStationName());
                    intent.putExtra("favoriteRailWay", stationItems.getRailWayName());
                    intent.putExtra("favoriteTimetable",stationItems.getTimetable());
                    intent.putExtra("isIntent",true);

                    startActivity(intent);
                }
            });

            buttonDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                   SQliteOpenHelper helper = new SQliteOpenHelper(MainActivity.this);
                   if (helper.deleteDB(stationItems.getStationName())){
                       Toast.makeText(MainActivity.this, "削除成功", Toast.LENGTH_SHORT).show();
                       items.remove(position);
                       arrayAdapter.notifyDataSetChanged();
                   } else {
                       Toast.makeText(MainActivity.this, "削除失敗", Toast.LENGTH_SHORT).show();
                   }
                }
            });

            return view;
        }
    }
}
