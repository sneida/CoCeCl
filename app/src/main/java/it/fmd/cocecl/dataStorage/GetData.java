package it.fmd.cocecl.dataStorage;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import it.fmd.cocecl.APPConstants;
import it.fmd.cocecl.contentviews.AssignedUnits;
import it.fmd.cocecl.contentviews.AssignedUnitsAdapter;

public class GetData extends AsyncTask<String, String, String> {

    HttpURLConnection urlConnection;

    // Hashmap for ListView
    ArrayList<HashMap<String, String>> assunitList;


    @Override
    protected String doInBackground(String... args) {

        StringBuilder result = new StringBuilder();

        try {

            URL url = new URL(APPConstants.COCESO_API);

            urlConnection = (HttpURLConnection) url.openConnection();
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());

            BufferedReader reader = new BufferedReader(new InputStreamReader(in));

            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            urlConnection.disconnect();
        }

        return result.toString();
    }

    @Override
    protected void onPostExecute(String result) {

        assunitList = ParseJSON(result);

        String data = "";
        try {
            JSONObject jsonO = new JSONObject(result);

            String tasktype = jsonO.optString("tasktype");
            String bo = jsonO.optString("boaddress");
            String boinfo = jsonO.optString("boinfo");
            String bogrund = jsonO.optString("bogrund");
            String caller = jsonO.optString("caller");
            Boolean emergency = jsonO.optBoolean("emergency");
/*
            //Get the instance of JSONArray that contains JSONObjects
            JSONArray jsonArray = jsonO.optJSONArray("Employee");

            //Iterate the jsonArray and print the info of JSONObjects
            for(int i=0; i < jsonArray.length(); i++){
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                int id = Integer.parseInt(jsonObject.optString("id").toString());
                String name = jsonObject.optString("name").toString();
                float salary = Float.parseFloat(jsonObject.optString("salary").toString());

                data += "Node"+i+" : \n id= "+ id +" \n Name= "+ name +" \n Salary= "+ salary +" \n ";
            }
*/
            IncidentData.getInstance().setTasktype(tasktype);
            IncidentData.getInstance().setBogrund(bogrund);
            IncidentData.getInstance().setBoaddress(bo);
            IncidentData.getInstance().setBoinfo(boinfo);
            IncidentData.getInstance().setCaller(caller);
            IncidentData.getInstance().setEmergency(emergency);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            JSONObject jsonO = new JSONObject(result);

            String amb = jsonO.optString("amb");
            String ambtype = jsonO.optString("ambtype");

            MainData.getInstance().setAmb(amb);
            MainData.getInstance().setAmbtype(ambtype);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            JSONObject jsonO = new JSONObject(result);

            String unitnames = jsonO.optString("unitname");
            String unitkennz = jsonO.optString("kennz");
            String unitfkenn = jsonO.optString("fkenn");

            Boolean mdunitc = jsonO.optBoolean("mdunit");
            Boolean mobileunitc = jsonO.optBoolean("mobileunit");

            UnitData.getInstance().setUnitname(unitnames);
            UnitData.getInstance().setVehicleplate(unitkennz);
            UnitData.getInstance().setUnitnumber(unitfkenn);

            UnitData.getInstance().setMdunit(mdunitc);
            UnitData.getInstance().setMobileunit(mobileunitc);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            JSONObject jsonO = new JSONObject(result);

            String ustatus = jsonO.optString("ustatus");
            String ustaddition = jsonO.optString("ustaddition");

            UnitStatus.getInstance().setUstatus(ustatus);
            UnitStatus.getInstance().setUstaddition(ustaddition);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            JSONObject jsonO = new JSONObject(result);

            String incistatus = jsonO.optString("incistatus");

            IncidentData.getInstance().setIncistatus(incistatus);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            JSONObject jsonO = new JSONObject(result);

            String madnr = jsonO.optString("userdnr");
            String mafname = jsonO.optString("userfname");
            String masname = jsonO.optString("usersname");

            PersonnelData.getInstance().setMADnr(madnr);
            PersonnelData.getInstance().setMAFamilyname(mafname);
            PersonnelData.getInstance().setMAName(masname);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    // Get JSON Arrays

    private static final String TAG_ASSUNIT_NAME = "unitname";
    private static final String TAG_ASSUNIT_STATUS = "status";
    private static final String TAG_ASSUNIT_ARRAY = "assunits";


    private ArrayList<HashMap<String, String>> ParseJSON(String result) {

        if (result != null) {

            try {

                // Hashmap for ListView
                ArrayList<HashMap<String, String>> assunitList = new ArrayList<HashMap<String, String>>();

                JSONObject jsonObj = new JSONObject(result);

                // Getting JSON Array node
                JSONArray assunitsarray = jsonObj.getJSONArray(TAG_ASSUNIT_ARRAY);

                // looping through All Students
                for (int i = 0; i < assunitsarray.length(); i++) {
                    JSONObject c = assunitsarray.getJSONObject(i);

                    String assunitname = c.optString("unitname");
                    String assstatus = c.optString("status");

                    //HashMap for every assunit
                    HashMap<String, String> assunits = new HashMap<String, String>();

                    // adding every child node to HashMap key => value
                    assunits.put(TAG_ASSUNIT_NAME, assunitname);
                    assunits.put(TAG_ASSUNIT_STATUS, assstatus);

                    AssignedUnits.getInstance().setAunit(assunitname);
                    AssignedUnits.getInstance().setStatusaunit(assstatus);

                    // adding unit to units list
                    assunitList.add(assunits);

                }
                return assunitList;
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
        } else {
            Log.e("ServiceHandler", "No data received from HTTP Request");
            return null;
        }
    }
}
