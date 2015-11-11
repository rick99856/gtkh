package app.z0nen.slidemenu;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Z0NEN on 10/22/2014.
 */
public class menu2_Fragment extends Fragment {
    private ProgressDialog pDialog;
    private static String url = "http://192.168.137.178/project_das/disease_all.php";

    // JSON Node names

    private static final String TAG_Name = "name";
    private static final String TAG_Danger = "danger";
    private static final String TAG_Infection = "infection";
    private static final String TAG_Info = "info";
    private static final String TAG_Population = "population";
    private static final String TAG_Symptom = "symptom";
     private static final String TAG_Prevention = "prevention";
    private static final String TAG_Treatment = "treatment";

    // contacts JSONArray
    JSONObject contacts = null;
    ListView lv;

    // Hashmap for ListView
    ArrayList<HashMap<String, String>> contactList;


    View rootview;
    @Nullable

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootview = inflater.inflate(R.layout.menu2_layout, container, false);
        if (android.os.Build.VERSION.SDK_INT > Build.VERSION_CODES.GINGERBREAD)
        {
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                    .detectDiskReads()
                    .detectDiskWrites()
                    .detectNetwork()   // or .detectAll() for all detectable problems
                    .penaltyLog()
                    .build());
        }
        contactList = new ArrayList<HashMap<String, String>>();



        lv =(ListView)rootview.findViewById(R.id.list2);


        new GetContacts().execute();
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parsnt, View view,
                                    int position, long id) {

                String info = null;
                String population = null;
                String symptom = null;
                String prevention = null;
                String treatment = null;
//                String url = null;

                AlertDialog.Builder dialog=new AlertDialog.Builder(getActivity());
                dialog.setTitle("detail");
                JSONObject oo = new JSONObject(contactList.get(position));
                Log.v("cont",contactList.get(position).toString());
                try {
                    info = oo.getString("info");
                    population = oo.getString("population");
                    symptom = oo.getString("symptom");
                    prevention = oo.getString("prevention");
                    treatment = oo.getString("treatment");
//                    url = oo.getString("Picture1");
//                    Px = oo.getDouble();

//                    Log.v("照片的網址",url);
                } catch (JSONException e) {
                    e.printStackTrace();
                }


                dialog.setMessage("詳細資訊：\n        " + info + "\n感染年齡層：\n        " + population
                                    + "\n感染症狀：\n        " + symptom+"\n預防方式：\n        "+prevention
                                    + "\n治療方式：\n        " + treatment);
//                final String finalPx = Px;
//                final String finalPy = Py;
//                final String finalName = Name;
//                final String finalurl = url;
//                dialog.setNegativeButton("景點圖片", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//
//                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//                        // Get the layout inflater
//                        LayoutInflater inflater = getActivity().getLayoutInflater();
//                        final View alert_view = inflater.inflate(R.layout.dialogout, null);
//
//                        // Inflate and set the layout for the dialog
//                        // Pass null as the parent view because its going in the dialog layout
//                        builder.setView(alert_view)
//                                // Add action buttons
//                                .setPositiveButton("yaya", new DialogInterface.OnClickListener() {
//                                    @Override
//                                    public void onClick(DialogInterface dialog, int id) {
//
//                                    }
//                                })
//                                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
//                                    public void onClick(DialogInterface dialog, int id) {
//
//                                    }
//                                });
//
//                        new Thread(){
//                            @Override
//                            public void run(){
//
//                                final String pict = finalurl;
//                                final WebView webv = (WebView)alert_view.findViewById(R.id.webv);
//
//
//
//                                getActivity().runOnUiThread(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        webv.setWebViewClient(mWebViewClient);
//                                        webv.loadUrl(pict);
//                                        webv.getSettings().setBuiltInZoomControls(true);
//                                    }
//                                });
//                            }
//                        }.start();


//                        Log.v("final url",finalurl.toString());



//                        builder.show();
//                    }
//
//
//                });


                dialog.setNeutralButton("離開", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        //設定按鈕

                    }
                });
                dialog.show();
                Log.v("Json",dialog.toString());
                // getting values from selected ListItem




            }


        });
        return rootview;

    }
    WebViewClient mWebViewClient = new WebViewClient() {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    };





    private class GetContacts extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(getActivity());
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected Void doInBackground(Void... arg0) {
            // Creating service handler class instance
            // Making a request to url and getting response

            ServiceHandler sh = new ServiceHandler();

            String jsonStr = sh.makeServiceCall(url, ServiceHandler.GET);

            Log.d("Response: ", "> " + jsonStr);

            if (jsonStr != null) {
                try {
                    JSONArray jsonOba = new JSONArray(jsonStr);

                    for(int i =0;i<jsonOba.length();i++){
                        JSONObject c = jsonOba.getJSONObject(i);

                        String name = c.getString(TAG_Name);
                        String danger = c.getString(TAG_Danger);
                        String infection = c.getString(TAG_Infection);
                        String info = c.getString(TAG_Info);
                        String population = c.getString(TAG_Population);
                        String symptom = c.getString(TAG_Symptom);
                        String prevention = c.getString(TAG_Prevention);
                        String treatment = c.getString(TAG_Treatment);

                        HashMap<String, String> contact = new HashMap<String, String>();


                        contact.put(TAG_Name, name);
                        contact.put(TAG_Danger, danger);
                        contact.put(TAG_Infection,infection);
                        contact.put(TAG_Info,info);
                        contact.put(TAG_Population,population);
                        contact.put(TAG_Symptom,symptom);
                        contact.put(TAG_Prevention,prevention);
                        contact.put(TAG_Treatment,treatment);



                        contactList.add(contact);

                    }


                    Log.v("string",jsonStr);
                } catch (Exception e) {
                    e.printStackTrace();

                }
            } else {
                Log.e("ServiceHandler", "Couldn't get any data from the url");
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog
            if (pDialog.isShowing())
                pDialog.dismiss();
            /**
             * Updating parsed JSON data into ListView
             * */
            ListAdapter adapter = new SimpleAdapter(
                    getActivity(), contactList,
                    R.layout.list_item2, new String[] { TAG_Name, TAG_Danger,
                    TAG_Infection }, new int[] { R.id.id,
                    R.id.name, R.id.tel });

            lv.setAdapter(adapter);
        }


    }



}


