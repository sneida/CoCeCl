package it.fmd.cocecl;

import android.Manifest;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTabHost;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import static android.graphics.Color.BLUE;
import static android.graphics.Color.GREEN;
import static android.graphics.Color.YELLOW;


public class MainActivity extends FragmentActivity {

    private FragmentTabHost mTabHost;

    public static Location loc;
    private static double longitude;
    private static double latitude;
    private static String lngString = String.valueOf(longitude);
    private static String latString = String.valueOf(latitude);

    // json object response url
    private String urlJsonObj = "http://api.androidhive.info/volley/person_object.json";

    // json array response url
    private String urlJsonArry = "http://api.androidhive.info/volley/person_array.json";

    private static String TAG = MainActivity.class.getSimpleName();
    private Button btnMakeObjectRequest, btnMakeArrayRequest;

    // Progress dialog
    private ProgressDialog pDialog;

    private TextView txtResponse;

    // temporary string to show the parsed response
    private String jsonResponse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Actionbar custom view //

        {
            ActionBar mActionBar = getActionBar();
            //mActionBar.setDisplayShowHomeEnabled(false);
            //mActionBar.setDisplayShowTitleEnabled(false);
            LayoutInflater mInflater = LayoutInflater.from(this);

            View mCustomView = mInflater.inflate(R.layout.custom_actionbar, null);

            ImageButton imageButton = (ImageButton) mCustomView.findViewById(R.id.imageButton);
            imageButton.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    Toast.makeText(getApplicationContext(), "Refresh Clicked!",
                            Toast.LENGTH_SHORT).show();
                }
            });

            assert mActionBar != null;
            mActionBar.setCustomView(mCustomView);
            mActionBar.setDisplayShowCustomEnabled(true);
        }

        // GPS Coordinates // send continuous updates //

        //new

        gpsmanager gps = new gpsmanager(MainActivity.this);
        double latitude = gps.getLatitude();
        double longitude = gps.getLongitude();

        Handler h = new Handler();
        h.postDelayed(new

                              Runnable() {
                                  @Override
                                  public void run() {
                                      //send coordinates every 10 sec
                                  }
                              }

                , 10000);
/*
        gpsmanager.LocationResult locationResult = new gpsmanager.LocationResult(){
            @Override
            public void gotLocation(Location location){
                loc = location;
                latitude = loc.getLatitude();
                longitude = loc.getLongitude();
            }
        };

        gpsmanager mylocation = new gpsmanager();
        mylocation.getLocation(MainActivity.this, locationResult);

        */

            // FRAGMENT MANAGER //
        {
            //TODO: Fragmentmanager does not crash anymore; tablet mode still does not work
            if ((getResources().getConfiguration().screenLayout &
                    Configuration.SCREENLAYOUT_SIZE_MASK) ==
                    Configuration.SCREENLAYOUT_SIZE_LARGE) {
                // on a large screen device ...
/*
                Fragment mainstatusfrag = new mainstatusFragment();
                Fragment incidentFrag = new incidentFragment();
                Fragment fielddataFrag = new fielddataFragment();
                Fragment mapFrag = new mapFragment();
*/
                FragmentManager fm = getSupportFragmentManager();

                //getSupportFragmentManager().findFragmentById(R.id.fragment_status);


                // Transaction start
                FragmentTransaction ft = fm.beginTransaction();

                ft.add(R.id.framelayout_1, new mainstatusFragment());

                if (findViewById(R.id.framelayout_2) != null) {

                    ft.add(R.id.framelayout_2, new incidentFragment());
                }

                if (findViewById(R.id.framelayout_3) != null) {

                    ft.add(R.id.framelayout_3, new mapFragment());
                }

                ft.addToBackStack(null);
                // Transaction commit
                ft.commit();
            }
        }

        // OPTIONS MENU //
/*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = new MenuInflater(this);
        inflater.inflate(R.menu.search, menu);
        return true;
    }
*/

        // TABHOST CONTROLLER //

        if ((getResources().getConfiguration().screenLayout &
                Configuration.SCREENLAYOUT_SIZE_MASK) ==
                Configuration.SCREENLAYOUT_SIZE_NORMAL) {
            // on a normal screen device ...

            mTabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
            mTabHost.setup(this, getSupportFragmentManager(), android.R.id.tabcontent);

            // TABS

            mTabHost.addTab(
                    mTabHost.newTabSpec("tab1").setIndicator("Status", null),
                    mainstatusFragment.class, null);
            mTabHost.addTab(
                    mTabHost.newTabSpec("tab2").setIndicator("Einsatzdaten", null),
                    incidentFragment.class, null); /*
            mTabHost.addTab(
                    mTabHost.newTabSpec("tab1").setIndicator("Status", null),
                    statusFragment.class, null);
            mTabHost.addTab(
                    mTabHost.newTabSpec("tab2").setIndicator("Einsatzdaten", null),
                    fielddataFragment.class, null); */
            mTabHost.addTab(
                    mTabHost.newTabSpec("tab3").setIndicator("Abgabeort", null),
                    deliverylocFragment.class, null);
            mTabHost.addTab(
                    mTabHost.newTabSpec("tab4").setIndicator("Karte", null),
                    mapFragment.class, null);
            mTabHost.addTab(
                    mTabHost.newTabSpec("tab5").setIndicator("Komm", null),
                    communicationFragment.class, null);
        }
    }


/* TODO: Nullpointer Exception - android.app.Activity.getLayoutInflater
        final LayoutInflater factory = getLayoutInflater();

        final View cusactbar = factory.inflate(R.layout.custom_actionbar, null);

        // Action bar connection state icon //
        ImageView netcon = (ImageView) cusactbar.findViewById(R.id.imageView_con);
        ImageView mlscon = (ImageView) cusactbar.findViewById(R.id.imageView_mlscon);
        {
            // check if you are connected or not
            connectionmanager conman = new connectionmanager();

            if (conman.isOnline()) {
                netcon.setBackgroundColor(0xFF00CC00);
                mlscon.setBackgroundColor(0xFF00CC00);

            } else {

                netcon.setBackgroundColor(0xFFFFCC00);
                mlscon.setBackgroundColor(0xFFFFCC00);
            }
        }
*/

    // JSON //

    public void json(View v) {
        if (v.getId() == R.id.button31) {

            { // TODO: set new button and textview
                btnMakeObjectRequest = (Button) findViewById(R.id.button);
                btnMakeArrayRequest = (Button) findViewById(R.id.button);
                txtResponse = (TextView) findViewById(R.id.textView);

                pDialog = new ProgressDialog(this);
                pDialog.setMessage("Please wait...");
                pDialog.setCancelable(false);

                btnMakeObjectRequest.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        // making json object request
                        makeJsonObjectRequest();
                    }
                });

                btnMakeArrayRequest.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        // making json array request
                        makeJsonArrayRequest();
                    }
                });
            }
        }
    }

    /**
     * Method to make json object request where json response starts wtih {
     * */
    private void makeJsonObjectRequest() {

        showpDialog();

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                urlJsonObj, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, response.toString());

                try {
                    // Parsing json object response
                    // response will be a json object
                    String name = response.getString("name");
                    String email = response.getString("email");
                    JSONObject phone = response.getJSONObject("phone");
                    String home = phone.getString("home");
                    String mobile = phone.getString("mobile");

                    jsonResponse = "";
                    jsonResponse += "Name: " + name + "\n\n";
                    jsonResponse += "Email: " + email + "\n\n";
                    jsonResponse += "Home: " + home + "\n\n";
                    jsonResponse += "Mobile: " + mobile + "\n\n";

                    txtResponse.setText(jsonResponse);

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(),
                            "Error: " + e.getMessage(),
                            Toast.LENGTH_LONG).show();
                }
                hidepDialog();
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_SHORT).show();
                // hide the progress dialog
                hidepDialog();
            }
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq);
    }

    /**
     * Method to make json array request where response starts with [
     * */
    private void makeJsonArrayRequest() {

        showpDialog();

        JsonArrayRequest req = new JsonArrayRequest(urlJsonArry,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d(TAG, response.toString());

                        try {
                            // Parsing json array response
                            // loop through each json object
                            jsonResponse = "";
                            for (int i = 0; i < response.length(); i++) {

                                JSONObject person = (JSONObject) response
                                        .get(i);

                                String name = person.getString("name");
                                String email = person.getString("email");
                                JSONObject phone = person
                                        .getJSONObject("phone");
                                String home = phone.getString("home");
                                String mobile = phone.getString("mobile");

                                jsonResponse += "Name: " + name + "\n\n";
                                jsonResponse += "Email: " + email + "\n\n";
                                jsonResponse += "Home: " + home + "\n\n";
                                jsonResponse += "Mobile: " + mobile + "\n\n\n";

                            }

                            txtResponse.setText(jsonResponse);

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(),
                                    "Error: " + e.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }

                        hidepDialog();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_SHORT).show();
                hidepDialog();
            }
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(req);
    }

    private void showpDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hidepDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

    //TODO: create method to save app/fragment state
    /*
        @Override
        public void onSaveInstanceState(Bundle outState) {
            super.onSaveInstanceState(outState);
            System.out.println("TAG, onSaveInstanceState");

            outState.putString("message", "This is my message to be reloaded");

            final TextView textView83 = (TextView)findViewById(R.id.textView83);
            final Button button41 = (Button)findViewById(R.id.button41);

            CharSequence button41text = button41.getText();
            CharSequence stateText = textView83.getText();
            outState.putCharSequence("savedbuttonText", button41text);
            outState.putCharSequence("savedstateText", stateText);
        }

        protected void onRestoreInstanceState(Bundle savedState) {
            System.out.println("TAG, onRestoreInstanceState");

            final TextView textView83 = (TextView)findViewById(R.id.textView83);
            final Button button41 = (Button) findViewById(R.id.button41);

            CharSequence button41text = savedState.getCharSequence("savedbuttonText");
            CharSequence stateText = savedState.getCharSequence("savedstateText");
            button41.setText(button41text);
            textView83.setText(stateText);
        }
    */
    //TODO: create app life cycle
    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onPause() {
        super.onPause();
        /*gpsmanager mylocation = new gpsmanager();
        mylocation.cancelTimer();*/

    }

    @Override
    public void onStop() {
        super.onStop();


    }

    @Override
    public void onDestroy() {
        super.onDestroy();


    }

    // Menu on the right of ActionBar/TitleBar //
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }


    // Buttons //

    public void ptt(View v) {
        if (v.getId() == R.id.button61) {
            AlertDialog.Builder dlgBuilder = new AlertDialog.Builder(MainActivity.this);
            dlgBuilder.setMessage("ptt app");
            dlgBuilder.setTitle("PTT App");

            dlgBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });

            AlertDialog alert = dlgBuilder.create();
            alert.show();

        }
    }

    //Call LS Button on deliverylocFragment//

    public void lscall(View v) {

        if (v.getId() == R.id.button17) {

            Button button17 = (Button) findViewById(R.id.button17);
            button17.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {

                    Intent callIntent = new Intent(Intent.ACTION_CALL);
                    callIntent.setData(Uri.parse("tel:" + R.string.lsbv));
                    startActivity(callIntent);

                    PackageManager pm = getPackageManager();
                    if (pm.checkPermission(Manifest.permission.CALL_PHONE, getPackageName()) == PackageManager.PERMISSION_GRANTED) {

                    } else {

                    }
                }
            });
        }

        if (v.getId() == R.id.button47) {

            Button button47 = (Button) findViewById(R.id.button47);
            button47.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {

                    Intent callIntent = new Intent(Intent.ACTION_CALL);
                    callIntent.setData(Uri.parse("tel:" + R.string.lsallg));
                    startActivity(callIntent);

                    PackageManager pm = getPackageManager();
                    if (pm.checkPermission(Manifest.permission.CALL_PHONE, getPackageName()) == PackageManager.PERMISSION_GRANTED) {

                    } else {

                    }

                }
            });
        }
    }

    // Alert Push Notification Manager //
    //TODO: later: function for new incident alert !!! check again
    public void alertbtn(View v) {

        final LayoutInflater factory = getLayoutInflater();

        final View incidentView = factory.inflate(R.layout.fragment_incident, null);
        //TODO: remove button
        if (v.getId() == R.id.button) {

            Button b22 = (Button) findViewById(R.id.button);
            b22.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View arg5) {

                    final TextView bofield = (TextView) incidentView.findViewById(R.id.bofield);
                    final TextView brfrfield = (TextView) incidentView.findViewById(R.id.brfrfield);
                    final TextView infofield = (TextView) incidentView.findViewById(R.id.infofield);

                    final Button button41 = (Button) incidentView.findViewById(R.id.button41);
                    final TextView textView83 = (TextView) incidentView.findViewById(R.id.textView83);
                    final TextView textView85 = (TextView) incidentView.findViewById(R.id.textView85);

                    final Calendar cal = Calendar.getInstance(TimeZone.getDefault());
                    final SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");

                    AlertDialog.Builder dlgBuilder = new AlertDialog.Builder(MainActivity.this);
                    dlgBuilder.setCancelable(false);
                    dlgBuilder.setTitle("EINSATZ");
                    dlgBuilder.setMessage("Addresse & Berufungsgrund");

                    dlgBuilder.setPositiveButton("Einsatz übernehmen", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            button41.setText(R.string.zbo);
                            textView83.setText("QU");
                            textView85.setText(sdf.format(cal.getTime()));
                            button41.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_fast_forward_black_18dp, 0, 0, 0);

                            Toast.makeText(MainActivity.this, "Einsatz übernommen", Toast.LENGTH_SHORT).show();

                        }
                    });

                    AlertDialog alert = dlgBuilder.create();
                    alert.show();

/*
                String title = bgfield.getText().toString().trim();
                String subject = bofield.getText().toString().trim();
                String body = infofield.getText().toString().trim();
*/
                    Intent intent = new Intent(MainActivity.this, MainActivity.class);
                    PendingIntent contentIntent = PendingIntent.getActivity(MainActivity.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

                    NotificationCompat.Builder mBuilder =
                            new NotificationCompat.Builder(MainActivity.this);
                                /*
                                .setSmallIcon(R.drawable.ic_warning_black_18dp)
                                .setContentTitle(infofield.getText().toString())
                                .setContentText(brfrfield.getText().toString());
                                //.setContentIntent(pendingIntent); // below Gingerbread
*/
                    mBuilder.setAutoCancel(true)
                            .setDefaults(Notification.DEFAULT_ALL)
                            .setWhen(System.currentTimeMillis())
                            .setSmallIcon(R.drawable.ic_warning_black_18dp)
                            .setTicker("Alert new Incident")
                            .setContentTitle("Alert + Code")
                            .setContentText("AddressStreet")
                            .setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_SOUND)
                            .setContentIntent(contentIntent)
                            .setContentInfo("Detail Code");

                    // AlertSound
                    mBuilder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM));

                    NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                    notificationManager.notify(1, mBuilder.build());
                }
            });
        }
    }

/* outdated version
                NotificationManager notif = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                Notification notify = new Notification(R.drawable.ic_warning_black_18dp, title, System.currentTimeMillis());
                PendingIntent pending = PendingIntent.getActivity(getApplicationContext(), 0, new Intent(), 0);

                notify.setLatestEventInfo(getApplicationContext(), subject, body, pending);
                notif.notify(0, notify);
            }
        });

        */


    // Button state & color functions START //

    // Status EB NEB AD mainstatusFragment //

    public void ebst(View v) {
        if (v.getId() == R.id.button38) {

            Button button38 = (Button) findViewById(R.id.button38);
            Button button39 = (Button) findViewById(R.id.button39);
            Button button40 = (Button) findViewById(R.id.button40);

            button38.setEnabled(true);
            button38.setClickable(false);
            button38.setBackgroundColor(GREEN);

            button39.setEnabled(false);
            button39.setClickable(false);
            button39.setBackgroundResource(android.R.drawable.btn_default);

            button40.setEnabled(false);
            button40.setClickable(false);
            button40.setBackgroundResource(android.R.drawable.btn_default);

        }
    }

    public void nebst(View v) {
        if (v.getId() == R.id.button39) {

            Button button38 = (Button) findViewById(R.id.button38);
            Button button39 = (Button) findViewById(R.id.button39);
            Button button40 = (Button) findViewById(R.id.button40);

            button38.setEnabled(false);
            button38.setClickable(false);
            button38.setBackgroundResource(android.R.drawable.btn_default);

            button39.setEnabled(false);
            button39.setClickable(false);
            button39.setBackgroundColor(Color.parseColor("#9C27B0"));

            button40.setEnabled(false);
            button40.setClickable(false);
            button40.setBackgroundResource(android.R.drawable.btn_default);

        }
    }

    public void adst(View v) {
        if (v.getId() == R.id.button40) {

            Button button38 = (Button) findViewById(R.id.button38);
            Button button39 = (Button) findViewById(R.id.button39);
            Button button40 = (Button) findViewById(R.id.button40);

            button38.setEnabled(false);
            button38.setClickable(false);
            button38.setBackgroundResource(android.R.drawable.btn_default);

            button39.setEnabled(false);
            button39.setClickable(false);
            button39.setBackgroundResource(android.R.drawable.btn_default);

            button40.setEnabled(false);
            button40.setClickable(false);
            button40.setBackgroundColor(Color.parseColor("#9C27B0"));

        }
    }

    // Status weiterschalten incidentFragment //
    //TODO: set fragments on status change

    public void stbtnClick(View v) {

        final Calendar cal = Calendar.getInstance(TimeZone.getDefault());
        final SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");

        Button button41 = (Button) findViewById(R.id.button41);

        AlertDialog.Builder dlgBuilder = new AlertDialog.Builder(MainActivity.this);
        dlgBuilder.setTitle(R.string.stwe);
        dlgBuilder.setMessage(button41.getText().toString());
        dlgBuilder.setCancelable(false);
        dlgBuilder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {

            final Button button41 = (Button) findViewById(R.id.button41);
            final TextView textView83 = (TextView) findViewById(R.id.textView83);
            final TextView textView85 = (TextView) findViewById(R.id.textView85);
            final TextView aofield = (TextView) findViewById(R.id.aofield);

            Button button10 = (Button) findViewById(R.id.button10);
            Button button11 = (Button) findViewById(R.id.button11);
            Button button13 = (Button) findViewById(R.id.button13);
            Button button46 = (Button) findViewById(R.id.button46);

            @Override
            public void onClick(DialogInterface dialog, int which) {

                if (textView83.getText().equals("") || textView83.getText().equals("EB")) {

                    button41.setEnabled(false);
                    button41.setClickable(false);
                    button41.setBackgroundColor(YELLOW);
                    button41.setText(R.string.zbo);
                    textView83.setText("QU");
                    textView85.setText(sdf.format(cal.getTime()));
                    button41.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_fast_forward_black_18dp, 0, 0, 0);

                    Handler h = new Handler();
                    h.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            button41.setEnabled(true);
                            button41.setClickable(true);
                            button41.setBackgroundResource(android.R.drawable.btn_default);
                        }
                    }, 3000);

                } else if (textView83.getText().equals("QU")) {

                    button41.setEnabled(false);
                    button41.setClickable(false);
                    button41.setBackgroundColor(YELLOW);
                    button41.setText(R.string.abo);
                    textView83.setText("ZBO");
                    textView85.setText(sdf.format(cal.getTime()));
                    button41.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_skip_next_black_18dp, 0, 0, 0);

                    Handler h = new Handler();
                    h.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            button41.setEnabled(true);
                            button41.setClickable(true);
                            button41.setBackgroundResource(android.R.drawable.btn_default);
                        }
                    }, 10000);

                } else if (textView83.getText().equals("ZBO")) {

                    button41.setEnabled(false);
                    button41.setClickable(false);
                    button41.setBackgroundColor(YELLOW);
                    button41.setText(R.string.zao);
                    textView83.setText("ABO");
                    textView85.setText(sdf.format(cal.getTime()));
                    button41.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_call_made_black_18dp, 0, 0, 0);

                    button10.setEnabled(true);
                    button10.setClickable(true);
                    button11.setEnabled(true);
                    button11.setClickable(true);
                    button13.setEnabled(true);
                    button13.setClickable(true);
                    button46.setEnabled(true);
                    button46.setClickable(true);
/*
                    mTabHost.getTabWidget().removeView(mTabHost.getTabWidget().getChildTabViewAt(2));

                    mTabHost.addTab(
                            mTabHost.newTabSpec("tab4").setIndicator("Abgabeort", null),
                            deliverylocFragment.class, null);
*/
                    Handler h = new Handler();
                    h.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            button41.setEnabled(true);
                            button41.setClickable(true);
                            button41.setBackgroundResource(android.R.drawable.btn_default);
                        }
                    }, 10000);

                } else if (textView83.getText().equals("ABO") /*&& (aofield.getText() != (""))*/) {

                    button41.setEnabled(true);
                    button41.setClickable(true);
                    button41.setBackgroundColor(YELLOW);
                    button41.setText(R.string.aao);
                    textView83.setText("ZAO");
                    textView85.setText(sdf.format(cal.getTime()));
                    button41.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_local_hospital_black_18dp, 0, 0, 0);

                    button10.setEnabled(false);
                    button10.setClickable(false);
                    button11.setEnabled(false);
                    button11.setClickable(false);
                    button13.setEnabled(false);
                    button13.setClickable(false);
                    button46.setEnabled(false);
                    button46.setClickable(false);
/*
                    mTabHost.getTabWidget().removeView(mTabHost.getTabWidget().getChildTabViewAt(1));
*/
                    Handler h = new Handler();
                    h.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            button41.setEnabled(true);
                            button41.setClickable(true);
                            button41.setBackgroundResource(android.R.drawable.btn_default);
                        }
                    }, 10000);

                } else if (textView83.getText().equals("ZAO")) {

                    button41.setEnabled(false);
                    button41.setClickable(false);
                    button41.setBackgroundColor(YELLOW);
                    button41.setText(R.string.eb);
                    textView83.setText("AAO");
                    textView85.setText(sdf.format(cal.getTime()));
                    button41.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_play_arrow_black_18dp, 0, 0, 0);

                    Handler h = new Handler();
                    h.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            button41.setEnabled(true);
                            button41.setClickable(true);
                            button41.setBackgroundResource(android.R.drawable.btn_default);
                        }
                    }, 10000);

                } else if (textView83.getText().equals("AAO")) {

                    button41.setEnabled(false);
                    button41.setClickable(false);
                    button41.setBackgroundColor(YELLOW);
                    button41.setText("QU");
                    textView83.setText("EB");
                    textView85.setText(sdf.format(cal.getTime()));
/*
                    mTabHost.removeAllViews();

                    mTabHost.addTab(
                            mTabHost.newTabSpec("tab1").setIndicator("Status", null),
                            mainstatusFragment.class, null);
                    mTabHost.addTab(
                            mTabHost.newTabSpec("tab2").setIndicator("Einsatzdaten", null),
                            incidentFragment.class, null);
*/
                    Handler h = new Handler();
                    h.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            button41.setEnabled(true);
                            button41.setClickable(true);
                            button41.setBackgroundResource(android.R.drawable.btn_default);
                        }
                    }, 10000);

                }
            }
        });

        dlgBuilder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        AlertDialog alert = dlgBuilder.create();
        alert.show();
    }

    // Emergency light yes/no //
    public void checkBox(View v) {
        if (v.getId() == R.id.checkBox) {
            CheckBox checkBox = (CheckBox) findViewById(R.id.checkBox);
            checkBox.setEnabled(true);
            checkBox.setClickable(false);
            checkBox.setHintTextColor(BLUE);
        }
    }

    // Button state & color functions END

    // Patienten Management dialog builder //

    public void createpat(View v) {

        final RelativeLayout patmanlayout = (RelativeLayout)getLayoutInflater().inflate(R.layout.patman, null);
        final Button bettbtn = (Button) patmanlayout.findViewById(R.id.bettbtn);
        final TextView textView11 = (TextView) patmanlayout.findViewById(R.id.textView11);

        if (v.getId() == R.id.button46) {

            Button button46 = (Button) findViewById(R.id.button46);
            button46.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {

                    AlertDialog.Builder dlgBuilder = new AlertDialog.Builder(MainActivity.this);
                    //dlgBuilder.setMessage("Patient anlegen");
                    dlgBuilder.setTitle("PATADMIN");

                    //LayoutInflater inflater = (MainActivity.this.getLayoutInflater());

                    dlgBuilder.setView(patmanlayout)

                            .setPositiveButton("Senden", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    //remove layout
                                    View viewToRemove = findViewById(R.id.patmanrelayout);
                                    if (viewToRemove != null && viewToRemove.getParent() != null && viewToRemove instanceof ViewGroup)
                                        ((ViewGroup) viewToRemove.getParent()).removeView(viewToRemove);

                                    //send data

                                    Toast.makeText(MainActivity.this, "Patient angelegt", Toast.LENGTH_SHORT).show();

                                }
                            });

                    dlgBuilder.setNegativeButton("Zurück", new DialogInterface.OnClickListener()

                            {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    //remove layout
                                    View viewToRemove = findViewById(R.id.patmanrelayout);
                                    if (viewToRemove != null && viewToRemove.getParent() != null && viewToRemove instanceof ViewGroup)
                                        ((ViewGroup) viewToRemove.getParent()).removeView(viewToRemove);
                                }
                            }

                    );

                    AlertDialog alert = dlgBuilder.create();
                    alert.show();

                }
            });
        }

        if (v.getId() == R.id.changepatbtn) {

            Button createpatbtn = (Button) findViewById(R.id.changepatbtn);

            createpatbtn.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {

                    AlertDialog.Builder dlgBuilder = new AlertDialog.Builder(MainActivity.this);
                    //dlgBuilder.setMessage("Patient anlegen");
                    dlgBuilder.setTitle("PATADMIN");

                    //LayoutInflater inflater = (MainActivity.this.getLayoutInflater());

                    dlgBuilder.setView(patmanlayout);
                    //bettbtn.setEnabled(false);
                    //bettbtn.setClickable(false);
                    bettbtn.setVisibility(View.GONE);
                    textView11.setVisibility(View.GONE);

                    dlgBuilder.setPositiveButton("Senden", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            //remove layout
                            View viewToRemove = findViewById(R.id.patmanrelayout);
                            if (viewToRemove != null && viewToRemove.getParent() != null && viewToRemove instanceof ViewGroup)
                                ((ViewGroup) viewToRemove.getParent()).removeView(viewToRemove);

                            //send data

                            //Toast.makeText(MainActivity.this, "Pat. Daten geändert", Toast.LENGTH_SHORT).show();

                        }
                    });

                    dlgBuilder.setNegativeButton("Zurück", new DialogInterface.OnClickListener()

                            {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    //remove layout
                                    View viewToRemove = findViewById(R.id.patmanrelayout);
                                    if (viewToRemove != null && viewToRemove.getParent() != null && viewToRemove instanceof ViewGroup)
                                        ((ViewGroup) viewToRemove.getParent()).removeView(viewToRemove);
                                }
                            }

                    );

                    AlertDialog alert = dlgBuilder.create();
                    alert.show();

                }
            });
        }
    }

    // Bett abbuchen btn //

    public void bettbuchen(View v) {

        final RelativeLayout patmanlayout = (RelativeLayout)getLayoutInflater().inflate(R.layout.patman, null);
        /*
        LayoutInflater inflater = getLayoutInflater();
        getWindow().addContentView(inflater.inflate(R.layout.patman, null), new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
*/
        if (v.getId() == R.id.bettbtn) {

            AlertDialog.Builder dlgbuilder = new AlertDialog.Builder(MainActivity.this);
            dlgbuilder.setTitle("Abteilung auswählen");
            dlgbuilder.setItems(new CharSequence[]
                            {"Intern", "Unfall", "Chirurgie", "HNO", "Dermatologie", "Spezialbett", "andere Abteilung"},

                    new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int which) {

                            TextView abtedit = (TextView) patmanlayout.findViewById(R.id.textView11);

                            switch (which) {

                                case 0:
                                    abtedit.setText(R.string.intern);
                                    Toast.makeText(MainActivity.this, R.string.intern, Toast.LENGTH_SHORT).show();
                                    break;

                                case 1:
                                    abtedit.setText(R.string.unfall, TextView.BufferType.EDITABLE);
                                    Toast.makeText(MainActivity.this, R.string.unfall, Toast.LENGTH_SHORT).show();
                                    break;

                                case 2:
                                    abtedit.setText(R.string.chir, TextView.BufferType.EDITABLE);
                                    Toast.makeText(MainActivity.this, R.string.chir, Toast.LENGTH_SHORT).show();
                                    break;

                                case 3:
                                    abtedit.setText(R.string.hno, TextView.BufferType.EDITABLE);
                                    Toast.makeText(MainActivity.this, R.string.hno, Toast.LENGTH_SHORT).show();
                                    break;

                                case 4:
                                    abtedit.setText(R.string.derma, TextView.BufferType.EDITABLE);
                                    Toast.makeText(MainActivity.this, R.string.derma, Toast.LENGTH_SHORT).show();
                                    break;

                                case 5:
                                    abtedit.setText(R.string.spezbett, TextView.BufferType.EDITABLE);
                                    Toast.makeText(MainActivity.this, R.string.lsbvanrufen, Toast.LENGTH_LONG).show();
                                    break;

                                case 6:
                                    abtedit.setText(R.string.andbett, TextView.BufferType.EDITABLE);
                                    Toast.makeText(MainActivity.this, R.string.lsbvanrufen, Toast.LENGTH_LONG).show();
                                    break;
                            }
                        }
                    }
            );

            dlgbuilder.create().

                    show();
        }
    }

    // PatMan start btn //

    public void patmanstart(View v) {
        if (v.getId() == R.id.button21) {

            Button button21 = (Button) findViewById(R.id.button21);
            button21.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    Intent ipatman = new Intent(getApplicationContext(), patmanActivity.class);
                    startActivity(ipatman);
                }
            });

        }
    }

    public void navigate(View v) {

        if (v.getId() == R.id.button18) {
            final TextView text = (TextView) findViewById(R.id.bofield);
            Button button18 = (Button) findViewById(R.id.button18);
            button18.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {

                    if(text != null) {

                        String navadress = "google.navigation:" + text.getText().toString();
                        Intent nav = new Intent(android.content.Intent.ACTION_VIEW);
                        nav.setData(Uri.parse(navadress));
                        startActivity(nav);

                    } else {
                        Toast.makeText(MainActivity.this, "Kein Berufungsort eingetragen!", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }

        if (v.getId() == R.id.button19) {

            final TextView text = (TextView) findViewById(R.id.aofield);
            Button button19 = (Button) findViewById(R.id.button19);
            button19.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {

                    if (text != null) {

                        String navadress = "google.navigation:" + text.getText().toString();
                        Intent nav = new Intent(android.content.Intent.ACTION_VIEW);
                        nav.setData(Uri.parse(navadress));
                        startActivity(nav);

                    } else {
                        Toast.makeText(MainActivity.this, "Kein Abgabeort eingetragen!", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }

        public void showmap(View v) {
            if (v.getId() == R.id.button30) {

                WebView gisView = (WebView) findViewById(R.id.gisView);

                gisView.getSettings().setJavaScriptEnabled(true);
                gisView.getSettings().getAllowFileAccessFromFileURLs();
                gisView.getSettings().setAllowUniversalAccessFromFileURLs(true);
                gisView.getSettings().setGeolocationEnabled(true);

                gisView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
                gisView.getSettings().setBuiltInZoomControls(true);

                gisView.setWebViewClient(new GeoWebViewActivity.GeoWebViewClient());
                gisView.setWebChromeClient(new GeoWebViewActivity.GeoWebChromeClient());

                gisView.loadUrl("file:///android_asset/leaflet.html");
            }
        }

        public void st14(View v) {

        RelativeLayout reportincident = (RelativeLayout)getLayoutInflater().inflate(R.layout.reportincident, null);

            if (v.getId() == R.id.button42) {

                final EditText editText24 = (EditText) reportincident.findViewById(R.id.editText24);
                final TextView textView86 = (TextView) reportincident.findViewById(R.id.textView86);
                final TextView textView93 = (TextView) reportincident.findViewById(R.id.textView93);
                final Button button42 = (Button) findViewById(R.id.button42);

                AlertDialog.Builder dlgBuilder = new AlertDialog.Builder(MainActivity.this);
                dlgBuilder.setMessage("Neuen Einsatz bei derzeitiger Position melden?");
                dlgBuilder.setCancelable(false);

                dlgBuilder.setView(reportincident);

                /*
                gpsmanager.LocationResult locationResult = new gpsmanager.LocationResult() {
                    @Override
                    public void gotLocation(Location location) {
                        loc = location;
                        latitude = loc.getLatitude();
                        longitude = loc.getLongitude();
                    }
                };

                gpsmanager mylocation = new gpsmanager();
                mylocation.getLocation(MainActivity.this, locationResult);
*/
                gpsmanager gps = new gpsmanager(MainActivity.this);
                double latitude = gps.getLatitude();
                double longitude = gps.getLongitude();

                Geocoder geocoder = new Geocoder(this, Locale.getDefault());

                try {
                    List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);

                    if(addresses != null && addresses.size() > 0) {
                        Address returnedAddress = addresses.get(0);
                        StringBuilder strReturnedAddress = new StringBuilder();
                        for(int i=0; i<returnedAddress.getMaxAddressLineIndex(); i++) {
                            strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
                        }
                        strReturnedAddress.append(returnedAddress.getLocality()).append("\n");
                        strReturnedAddress.append(returnedAddress.getPostalCode()).append("\n");
                        strReturnedAddress.append(returnedAddress.getCountryName());
                        editText24.setText(strReturnedAddress.toString());

                    } else {

                        editText24.setText("No Address found!");
                    }
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    editText24.setText("Cannot get Address!");
                }



/*
                Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
                String result = null;
                try {
                    List<Address> addressList = geocoder.getFromLocation(
                            latitude, longitude, 1);
                    if (addressList != null && addressList.size() > 0) {
                        Address address = addressList.get(0);
                        StringBuilder sb = new StringBuilder();
                        for (int i = 0; i < address.getMaxAddressLineIndex(); i++) {
                            sb.append(address.getAddressLine(i)).append("\n");
                        }
                        sb.append(address.getLocality()).append("\n");
                        sb.append(address.getPostalCode()).append("\n");
                        sb.append(address.getCountryName());
                        result = sb.toString();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
*/

                    //editText24.setText(locationAddress);
                    textView86.setText("lat: " + latitude);
                    textView93.setText("lon: " + longitude);

                dlgBuilder.setPositiveButton("Ja", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                button42.setEnabled(false);
                                button42.setClickable(false);
                                button42.setBackgroundColor(YELLOW);

                                Handler h = new Handler();
                                h.postDelayed(new

                                                      Runnable() {
                                                          @Override
                                                          public void run() {
                                                              button42.setEnabled(true);
                                                              button42.setClickable(true);
                                                              button42.setBackgroundResource(android.R.drawable.btn_default);
                                                              editText24.setText("");
                                                              textView86.setText("");
                                                              textView93.setText("");
                                                          }
                                                      }

                                        , 30000);

                                Toast.makeText(MainActivity.this, "Neuen Einsatz an Leitstelle gemeldet", Toast.LENGTH_SHORT).

                                        show();
                            }
                        }

                );

                dlgBuilder.setNegativeButton("Nein", new DialogInterface.OnClickListener()

                        {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        }

                );

                AlertDialog alert = dlgBuilder.create();
                alert.show();

                // remove layout
                View viewToRemove = findViewById(R.id.reportincidentrelayout);
                if (viewToRemove != null && viewToRemove.getParent() != null && viewToRemove instanceof ViewGroup)
                    ((ViewGroup) viewToRemove.getParent()).removeView(viewToRemove);


            }
        }
    }




