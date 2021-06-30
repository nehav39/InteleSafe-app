package org.intelehealth.swasthyasamparkp.activities.HealthFacility;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.google.gson.JsonObject;

import org.intelehealth.swasthyasamparkp.R;
import org.intelehealth.swasthyasamparkp.utilities.DateAndTimeUtils;
import org.intelehealth.swasthyasamparkp.utilities.FileUtils;
import org.intelehealth.swasthyasamparkp.utilities.SessionManager;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MedicalFacility extends AppCompatActivity {
private static final String TAG = MedicalFacility.class.getName();
RecyclerView recyclerView;
MedicalFacility_Adapter adapter;
Context context;
ArrayList<MedicalFacility_DataModel> arrayList;
SessionManager sessionManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medical_facility);

        context = MedicalFacility.this;
        sessionManager = new SessionManager(context);
        recyclerView = findViewById(R.id.medical_recyclerview);
        arrayList = new ArrayList<>();


        //TODO: Write logic for fetching data from json of assets than get that data
        // and store in a Medical_DataModel object and add it to the Arraylist and pass it to the Adapter...
        //create jsonobject class and fetch the json objects....
        try {
//            JSONObject jsonObject = new JSONObject(FileUtils.readFileRoot
//                    ("health_facility.json", context));
            JSONObject jsonObject = new JSONObject();
            jsonObject = FileUtils.encodeJSON(this, "health_facility.json");

            JSONArray medical_list =  jsonObject.getJSONArray("medical_list");
           if(medical_list != null && medical_list.length() > 0) {

               for (int i = 0; i < medical_list.length(); i++) {

                   if(medical_list.getJSONObject(i).has("state") && medical_list.getJSONObject(i).getString("state")
                           .equalsIgnoreCase("Jharkhand")) {

                       //check district array is empty or not...
                       JSONArray district = medical_list.getJSONObject(i).getJSONArray("district");

                       if(medical_list.getJSONObject(i).has("district") && district != null && district.length() > 0) {

                           for (int j = 0; j < district.length(); j++) {
                               //checking for disitrit is empty or not...

                               if(district.getJSONObject(j).has("covid_centre") && district.getJSONObject(j).getJSONArray("covid_centre") != null &&
                              district.getJSONObject(j).getString("covid_centre").length() > 0) {
                                  //checking for covid_centre exists or not...
                                  //this means that the hospital exists...

                                  JSONArray covid_centre = district.getJSONObject(j).getJSONArray("covid_centre");
                                  if(covid_centre != null && covid_centre.length() > 0) {

                                      String mtitle = "", maddress = "", mphone= "", MmapUrl = "";
                                      for (int k = 0; k < covid_centre.length(); k++) {
                                          //traversing through covid-centre's...
                                          mtitle = covid_centre.getJSONObject(k).getString("c_name");
                                          maddress = covid_centre.getJSONObject(k).getString("address");
                                          mphone = covid_centre.getJSONObject(k).getString("phone_no");
                                          MmapUrl = covid_centre.getJSONObject(k).getString("map_url");

                                          arrayList.add(new MedicalFacility_DataModel(mtitle, maddress, mphone, MmapUrl));
                                      }

                                  }

                              }
                           }

                       }
                   }
                   String s = sessionManager.getState();
                   Log.d(TAG, "state: "+ s);
               }
           }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        adapter = new MedicalFacility_Adapter(arrayList, context);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);



    }
}