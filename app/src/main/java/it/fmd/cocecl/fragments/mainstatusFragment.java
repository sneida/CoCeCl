package it.fmd.cocecl.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import it.fmd.cocecl.R;
import it.fmd.cocecl.contentviews.IncidentAdapter;
import it.fmd.cocecl.contentviews.Incidents;
import it.fmd.cocecl.contentviews.ListViewUtil;
import it.fmd.cocecl.unitstatus.SetUnitStatus;

public class mainstatusFragment extends Fragment {

    SetUnitStatus sus = new SetUnitStatus();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_mainstatus2, container, false);

        final Button button38 = (Button) v.findViewById(R.id.button38);
        final Button button39 = (Button) v.findViewById(R.id.button39);
        final Button button40 = (Button) v.findViewById(R.id.button40);

        final Button button5 = (Button) v.findViewById(R.id.button5);
        final Button button12 = (Button) v.findViewById(R.id.button12);

        //return v;

        //Status EB
        button38.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                st1();
            }
        });

        //Status NEB
        button39.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                st6();
            }
        });

        //Status AD
        button40.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                st9();
            }
        });

        //Status 5 Selektivruf
        button5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectivbtn();
            }
        });

        //Status NOTRUF
        button12.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                emergencybtn();
            }
        });


        // ReportIncident PlacesAutoComplete

        return v;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        setIncidentLVData();
    }

    public void setIncidentLVData() {

        // Construct the data source
        ArrayList<Incidents> arrayOfIncidentses = new ArrayList<Incidents>();

        // Create the adapter to convert the array to views
        IncidentAdapter adapter = new IncidentAdapter(getContext(), arrayOfIncidentses);

        // Attach the adapter to a ListView
        final ListView listView = (ListView) getActivity().findViewById(R.id.activeincidentlv);

        // Adapt LV size, LV in ScrollView bug
        ListViewUtil.setListViewHeightBasedOnChildren(listView);

        listView.setAdapter(adapter);

        // Add item to adapter
        // TEST DATA - later from server JSON, stored in shared prefs
        Incidents newIncidents = new Incidents("Sturz", "unklar", "Neubaugasse 64", "QU");

        adapter.add(newIncidents);

        // OnClick Event load Incident Data from Storage (if more than one) to fields in incidentFragment
        listView.setClickable(true);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {

                //Object incidentclick = listView.getItemAtPosition(position);

                arg0.setSelected(true);
            }
        });

    }

    // Button state & color functions START //
    // Status EB NEB AD mainstatusFragment //
    // TODO: set by server ebst(); nebst(); adst(); not by user (for now)

    public void st1() {

        AlertDialog.Builder dlgBuilder = new AlertDialog.Builder(getActivity());
        dlgBuilder.setMessage("Einheit EB melden?");
        dlgBuilder.setCancelable(false);
        dlgBuilder.setPositiveButton("Ja", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                ebst();

                //Toast.makeText(getActivity(), "Im Dienst", Toast.LENGTH_SHORT).show();
            }
        });

        dlgBuilder.setNegativeButton("Nein", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        AlertDialog alert = dlgBuilder.create();
        alert.show();
    }


    public void ebst() {

        Button button38 = (Button) getActivity().findViewById(R.id.button38);
        Button button39 = (Button) getActivity().findViewById(R.id.button39);
        Button button40 = (Button) getActivity().findViewById(R.id.button40);

        CardView cardviewst = (CardView) getActivity().findViewById(R.id.cardviewst);
        LinearLayout statusbtnlinlay = (LinearLayout) getActivity().findViewById(R.id.statusbtnlinlay);

        TextView textView111 = (TextView) getActivity().findViewById(R.id.textView111);

        button38.setEnabled(false);
        button38.setClickable(false);
        //button38.setBackgroundColor(GREEN);
        button38.setBackground(getResources().getDrawable(R.drawable.button_green_pressed));

        button39.setEnabled(true);
        button39.setClickable(false);
        //button39.setBackgroundColor(Color.parseColor("#bdbdbd"));
        //button39.setBackgroundResource(android.R.drawable.btn_default);
        button39.setBackground(getResources().getDrawable(R.drawable.custom_button_normal));

        button40.setEnabled(true);
        button40.setClickable(false);
        //button40.setBackgroundColor(Color.parseColor("#bdbdbd"));
        //button40.setBackgroundResource(android.R.drawable.btn_default);
        button40.setBackground(getResources().getDrawable(R.drawable.custom_button_normal));

        //cardviewst.setCardBackgroundColor(GREEN);
        //statusbtnlinlay.setBackgroundColor(GREEN);
        statusbtnlinlay.setBackgroundColor(getResources().getColor(R.color.btnclr_pdgreen));

        textView111.setVisibility(View.VISIBLE);
        textView111.setText(R.string.eb);

    }

    public void st6() {

        final TextView textView111 = (TextView) getActivity().findViewById(R.id.textView111);

        AlertDialog.Builder dlgbuilder = new AlertDialog.Builder(getActivity());
        dlgbuilder.setTitle("Einheit nicht einsatzbereit melden?");
        dlgbuilder.setItems(new CharSequence[]
                        {"NEB (andere Grund)", "Tanken", "Material nachfassen", "Bereitschaft", "Nein"},

                new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {

                        switch (which) {
                            case 0:
                                nebst();
                                //Toast.makeText(getActivity(), "Nicht EB", Toast.LENGTH_SHORT).show();
                                textView111.setVisibility(View.VISIBLE);
                                textView111.setText("Nicht EB");
                                break;
                            case 1:
                                nebst();
                                //Toast.makeText(getActivity(), "Tanken", Toast.LENGTH_SHORT).show();
                                textView111.setVisibility(View.VISIBLE);
                                textView111.setText("Tanken");
                                break;
                            case 2:
                                nebst();
                                //Toast.makeText(getActivity(), "Mat. nachfassen", Toast.LENGTH_SHORT).show();
                                textView111.setVisibility(View.VISIBLE);
                                textView111.setText("Material nachfassen");
                                break;
                            case 3:
                                nebst();
                                //Toast.makeText(getActivity(), "Bereitschaft", Toast.LENGTH_SHORT).show();
                                textView111.setVisibility(View.VISIBLE);
                                textView111.setText("Bereitschaft");
                                break;
                            case 4:
                                //Toast.makeText(getActivity(), "weiter EB", Toast.LENGTH_SHORT).show();
                                textView111.setVisibility(View.VISIBLE);
                                textView111.setText("weiter EB");
                                break;
                        }
                    }
                });
        dlgbuilder.create().show();
    }


    public void nebst() {

        Button button38 = (Button) getActivity().findViewById(R.id.button38);
        Button button39 = (Button) getActivity().findViewById(R.id.button39);
        Button button40 = (Button) getActivity().findViewById(R.id.button40);

        CardView cardviewst = (CardView) getActivity().findViewById(R.id.cardviewst);
        LinearLayout statusbtnlinlay = (LinearLayout) getActivity().findViewById(R.id.statusbtnlinlay);

        button38.setEnabled(true);
        button38.setClickable(false);
        //button38.setBackgroundColor(Color.parseColor("#bdbdbd"));
        //button38.setBackgroundResource(android.R.drawable.btn_default);
        button38.setBackground(getResources().getDrawable(R.drawable.custom_button_normal));

        button39.setEnabled(false);
        button39.setClickable(false);
        //button39.setBackgroundColor(Color.parseColor("#EF6C00"));
        button39.setBackground(getResources().getDrawable(R.drawable.button_yellow_pressed));

        button40.setEnabled(true);
        button40.setClickable(false);
        //button40.setBackgroundColor(Color.parseColor("#bdbdbd"));
        //button40.setBackgroundResource(android.R.drawable.btn_default);
        button40.setBackground(getResources().getDrawable(R.drawable.custom_button_normal));

        //cardviewst.setCardBackgroundColor(Color.parseColor("#EF6C00"));
        statusbtnlinlay.setBackgroundColor(Color.parseColor("#EF6C00"));
    }

    public void st9() {

        AlertDialog.Builder dlgBuilder = new AlertDialog.Builder(getActivity());
        dlgBuilder.setMessage("Einheit ausser Dienst stellen?");
        dlgBuilder.setCancelable(false);
        dlgBuilder.setPositiveButton("Ja", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                adst();

                Toast.makeText(getActivity(), "Ausser Dienst", Toast.LENGTH_SHORT).show();
            }
        });

        dlgBuilder.setNegativeButton("Nein", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        AlertDialog alert = dlgBuilder.create();
        alert.show();
    }


    public void adst() {

        Button button38 = (Button) getActivity().findViewById(R.id.button38);
        Button button39 = (Button) getActivity().findViewById(R.id.button39);
        Button button40 = (Button) getActivity().findViewById(R.id.button40);

        CardView cardviewst = (CardView) getActivity().findViewById(R.id.cardviewst);
        LinearLayout statusbtnlinlay = (LinearLayout) getActivity().findViewById(R.id.statusbtnlinlay);

        TextView textView111 = (TextView) getActivity().findViewById(R.id.textView111);

        button38.setEnabled(true);
        button38.setClickable(false);
        //button38.setBackgroundColor(Color.parseColor("#bdbdbd"));
        //button38.setBackgroundResource(android.R.drawable.btn_default);
        button38.setBackground(getResources().getDrawable(R.drawable.custom_button_normal));

        button39.setEnabled(true);
        button39.setClickable(false);
        //button39.setBackgroundColor(Color.parseColor("#bdbdbd"));
        //button39.setBackgroundResource(android.R.drawable.btn_default);
        button39.setBackground(getResources().getDrawable(R.drawable.custom_button_normal));

        button40.setEnabled(false);
        button40.setClickable(false);
        //button40.setBackgroundColor(Color.parseColor("#9C27B0"));
        button40.setBackground(getResources().getDrawable(R.drawable.button_purple_pressed));

        textView111.setVisibility(View.VISIBLE);
        textView111.setText("Ausser Dienst");

        //cardviewst.setCardBackgroundColor(Color.parseColor("#9C27B0"));
        statusbtnlinlay.setBackgroundColor(Color.parseColor("#9C27B0"));
    }

    //Radio

    //SelectivRuf
    public void selectivbtn() {
        final AlertDialog.Builder dlgBuilder = new AlertDialog.Builder(getActivity());
        dlgBuilder.setMessage("Selektivruf senden?");
        dlgBuilder.setCancelable(false);
        dlgBuilder.setPositiveButton("Ja", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                sus.selectivmt();
            }
        });
        dlgBuilder.setNegativeButton("Nein", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        dlgBuilder.create().show();
    }

    //NOTRUF
    public void emergencybtn() {
        final AlertDialog.Builder dlgBuilder = new AlertDialog.Builder(getActivity());
        dlgBuilder.setMessage("NOTRUF senden?");
        dlgBuilder.setCancelable(false);
        dlgBuilder.setPositiveButton("Ja", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                sus.emergencymt();
            }
        });

        dlgBuilder.setNegativeButton("Nein", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        dlgBuilder.create().show();
    }
}