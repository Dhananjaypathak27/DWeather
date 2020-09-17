package com.xparticle.dweather;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.Network;
import android.os.Bundle;
import android.telephony.AvailableNetworkInfo;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;

public class MainActivity2 extends AppCompatActivity {

    SwipeRefreshLayout swipeRefreshLayout;
    ConstraintLayout constraintLayout;
    TextView cityName,temp,weather1;
    DatabaseHelper myDB;
    private RequestQueue requestQueue;
    DecimalFormat df = new DecimalFormat("#.##");
    FloatingActionButton floatingActionButton;

    String baseURL = "https://api.openweathermap.org/data/2.5/weather?q=";
    String endURL =  "&appid=bac0f8e4d37a2f1e4ecaadf2c62aefe7";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        constraintLayout = findViewById(R.id.layout);
        cityName = findViewById(R.id.cityName);
        temp = findViewById(R.id.temp);
        weather1 = findViewById(R.id.weather);
        myDB = new DatabaseHelper(this);
        swipeRefreshLayout = findViewById(R.id.swipeLayout);
        floatingActionButton = findViewById(R.id.floatingActionButton);
        requestQueue = VollySinglenton.getInstance(this).getRequestQueue();

        DatabaseHelper dbHelper = new DatabaseHelper(MainActivity2.this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String query = "SELECT * FROM myWeather_table";
        Cursor mcursor = db.rawQuery(query, null);
        int count = mcursor.getCount();
        mcursor.close();

        if(count>0)
            defaultDataSet();

        Cursor cursor = myDB.readData();
        String mCityName = null;
        while (cursor.moveToNext()) {
            mCityName = cursor.getString(1);
        }
        weatherNameFun(mCityName);

        if(!amIConnected()){
            Toast.makeText(MainActivity2.this,"No Internet Access",Toast.LENGTH_SHORT).show();
        }

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Toast.makeText(MainActivity2.this,"Refresh Successful",Toast.LENGTH_SHORT).show();


                Cursor cursor = myDB.readData();
                String mCityName = null,mWeather=null,mTemp=null;
                while (cursor.moveToNext()) {
                    //list.add(new TodoList(cursor.getString(1)));
                    mCityName = cursor.getString(1);
//                    mWeather = cursor.getString(2);
//                    mTemp = cursor.getString(3);
                }
                weatherNameFun(mCityName);

                swipeRefreshLayout.setRefreshing(false);
            }
        });
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText editText = new EditText(MainActivity2.this);
                AlertDialog builder = new AlertDialog.Builder(MainActivity2.this)
                        .setTitle("Enter Your City")
                        .setView(editText)
                        .setPositiveButton("add", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String weather = editText.getText().toString();
                                if (weather.equals("")) {
                                    Toast.makeText(MainActivity2.this, "enter city", Toast.LENGTH_SHORT).show();
                                    return;
                                }

                                weatherNameFun(weather);
                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .create();
                builder.show();
            }
        });
    }

    private boolean amIConnected(){
        ConnectivityManager connectivityManager= (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        Network activeNetworkInfo = connectivityManager.getActiveNetwork();
        return activeNetworkInfo != null;
    }

    private void defaultDataSet(){
        Cursor cursor = myDB.readData();
        String mCityName = null,mWeather=null,mTemp=null;
        while (cursor.moveToNext()) {
            //list.add(new TodoList(cursor.getString(1)));
            mCityName = cursor.getString(1);
            mWeather = cursor.getString(2);
            mTemp = cursor.getString(3);
        }

        cityName.setText(mCityName);
        weather1.setText(mWeather);
        temp.setText(mTemp);
        resource(mWeather);

    }
    private void weatherNameFun(String weatherName){
        final String[] myWeather = new String[1];
        final String[] cityN = new String[1];
        final double[] reTemp = new double[1];
        String myURL = baseURL + weatherName + endURL;
        JsonObjectRequest jsonObjectRequest =new JsonObjectRequest(Request.Method.GET, myURL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String weather = response.getString("weather");
                    JSONArray ar = new JSONArray(weather);

                    for(int i=0;i<ar.length();i++){
                        JSONObject parObj = ar.getJSONObject(i);
                        myWeather[0] = parObj.getString("main");
                        weather1.setText(myWeather[0]);
                    }
                    cityN[0] = response.getString("name");
                    cityName.setText(cityN[0]);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    String  temperature = response.getString("main");
                    JSONObject tempObj = new JSONObject(temperature);
                    String myTemp = tempObj.getString("temp");
                    reTemp[0] = Float.parseFloat(myTemp)-275.15;


                    temp.setText( df.format(reTemp[0]));
                } catch (JSONException e) {
                    e.printStackTrace();
                }


                DatabaseHelper dbHelper = new DatabaseHelper(MainActivity2.this);
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                String query = "SELECT * FROM myWeather_table";
                Cursor cursor = db.rawQuery(query, null);
                int count = cursor.getCount();
                cursor.close();


                if(count>1){
                    myDB.updateData(String.valueOf(1),cityN[0],myWeather[0],Double.toString(reTemp[0]));
                }
                else{
                    myDB.addData(cityN[0],myWeather[0],Double.toString(reTemp[0]));
                }
                resource(myWeather[0]);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        requestQueue.add(jsonObjectRequest);
    }
    void resource(String mWeather){
        switch (mWeather){
            case "Clouds":
                constraintLayout.setBackground(getDrawable(R.drawable.cloudy));
                break;
            case "Cloudy":
                constraintLayout.setBackground(getDrawable(R.drawable.cloudy));
                break;
            case "Partly cloudy":
                constraintLayout.setBackground(getDrawable(R.drawable.cloudy));
                break;
            case "Rain":
                constraintLayout.setBackground(getDrawable(R.drawable.rainy));
                break;
            case "Snow":
                constraintLayout.setBackground(getDrawable(R.drawable.snow));
                break;
            case "Thunderstorm":
                constraintLayout.setBackground(getDrawable(R.drawable.thunderstorm));
                break;
            case "Foggy":
                constraintLayout.setBackground(getDrawable(R.drawable.foggy));
                break;
            case "Haze":
                constraintLayout.setBackground(getDrawable(R.drawable.foggy));
                break;
            case "Clear":
                constraintLayout.setBackground(getDrawable(R.drawable.sunny));
                break;
            case "Drizzle":
                constraintLayout.setBackground(getDrawable(R.drawable.rainy));
                break;
            default:
                constraintLayout.setBackground(getDrawable(R.drawable.cloudy));
        }

    }
}