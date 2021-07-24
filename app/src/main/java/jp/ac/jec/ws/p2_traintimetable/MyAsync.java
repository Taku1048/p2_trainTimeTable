package jp.ac.jec.ws.p2_traintimetable;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ListView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class MyAsync extends AsyncTask<Uri.Builder, Void, ArrayList<StationItems>> {

    private final SearchResultActivity activitySearchResult;

    private final TrainTimeTableActivity activityTrainTimeTable;



    public MyAsync(SearchResultActivity activity) { activitySearchResult = activity;
        activityTrainTimeTable = null;
    }

    public MyAsync(TrainTimeTableActivity activity) {
        activityTrainTimeTable = activity;
        activitySearchResult = null;
    }

    @Override
    protected ArrayList<StationItems> doInBackground(Uri.Builder... builders) {

        String resString = "失敗";
        HttpURLConnection connection = null;
        ArrayList<StationItems> ary = null;

        if (activitySearchResult != null){
            try{
                URL url = new URL(builders[0].toString());
                connection = (HttpURLConnection) url.openConnection();
                resString = inputStreamToString(connection.getInputStream());

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (connection != null){
                    connection.disconnect();
                }
            }
            ary = JsonHelper.parseJsonRailway(resString);

        } else {

            try{
                URL url = new URL(builders[0].toString());
                connection = (HttpURLConnection) url.openConnection();
                resString = inputStreamToString(connection.getInputStream());

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (connection != null){
                    connection.disconnect();
                }
            }
            ary = JsonHelper.parseJsonTimeTable(resString);

        }
        return ary;
    }


    /**
     * doInBackgroundの後にcall
     * @param stationItems　Jsonの中身
     */
    @Override
    protected void onPostExecute(ArrayList<StationItems> stationItems) {

        if (activitySearchResult != null){

            for (StationItems tmp : stationItems){
                activitySearchResult.arrayAdapter.add(tmp);
            }
            ListView listViewSearchResult = activitySearchResult.findViewById(R.id.listSearchResult);
            listViewSearchResult.setAdapter(activitySearchResult.arrayAdapter);
            activitySearchResult.arrayAdapter.notifyDataSetChanged();

        } else if (activityTrainTimeTable != null){

            for (StationItems tmp : stationItems){
                activityTrainTimeTable.modelAdapterTrainTimetable.add(tmp);
            }
            ListView listViewTrainTime = activityTrainTimeTable.findViewById(R.id.listTrainTime);
            listViewTrainTime.setAdapter(activityTrainTimeTable.modelAdapterTrainTimetable);
            activityTrainTimeTable.modelAdapterTrainTimetable.notifyDataSetChanged();
        }
    }

    /**
     * InputStreamから文字列としてデータを読み込み文字列データを返す
     * @param inputStream : HTTP通信で取得したInputStream
     * @return
     * @throws IOException
     */
    private String inputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder builder = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null){
            builder.append(line);
        }
        br.close();
        return builder.toString();
    }
}
