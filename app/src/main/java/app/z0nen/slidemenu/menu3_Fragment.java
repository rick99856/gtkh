package app.z0nen.slidemenu;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
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
public class menu3_Fragment extends Fragment {
    private ProgressDialog pDialog;
    private static String url = "http://data.kaohsiung.gov.tw/Opendata/DownLoad.aspx?Type=2&CaseNo1=AV&CaseNo2=2&FileType=1&Lang=C&FolderType=";

    // JSON Node names

    private static final String TAG_Name = "Name";
    private static final String TAG_Description = "Description";
    private static final String TAG_Tel = "Tel";
    private static final String TAG_Add = "Add";
    private static final String TAG_Opentime = "Opentime";
    private static final String TAG_Px = "Px";
    private static final String TAG_Py = "Py";
    private static final String TAG_pict = "Picture1";
    // contacts JSONArray
    JSONObject contacts = null;
    ListView lv;

    // Hashmap for ListView
    ArrayList<HashMap<String, String>> contactList;


    View rootview;
    @Nullable

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootview = inflater.inflate(R.layout.menu3_layout, container, false);
        contactList = new ArrayList<HashMap<String, String>>();



        lv =(ListView)rootview.findViewById(R.id.list3);


        new GetContacts().execute();
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                String description = null;
                String opt = null;
                String url = null;
                AlertDialog.Builder dialog=new AlertDialog.Builder(getActivity());
                dialog.setTitle("詳細內容");
                JSONObject oo = new JSONObject(contactList.get(position));
                try {
                    description = oo.getString("Description");
                    opt = oo.getString("Opentime");
                    url = oo.getString("Picture1");
                    Log.v("照片的網址",url);
                } catch (JSONException e) {
                    e.printStackTrace();
                }


                dialog.setMessage("簡介:\n        "+description+"\n開放時間:\n        "+opt);
                final String finalurl = url;
                dialog.setNegativeButton("景點圖片", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
//                        Intent intent = new Intent();
//                        intent.putExtra("Px", finalPx);
//                        intent.putExtra("Py", finalPy);
//                        intent.putExtra("info", finalName);
//                        intent.setClass(getActivity(), Maplayout.class);
//                        startActivity(intent);
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        // Get the layout inflater
                        LayoutInflater inflater = getActivity().getLayoutInflater();
                        final View alert_view = inflater.inflate(R.layout.dialogout, null);

                        // Inflate and set the layout for the dialog
                        // Pass null as the parent view because its going in the dialog layout
                        builder.setView(alert_view)
                                // Add action buttons
                                .setPositiveButton("yaya", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int id) {

                                    }
                                })
                                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {

                                    }
                                });

                        new Thread(){
                            @Override
                            public void run(){

                                final String pict = finalurl;
                                final WebView webv = (WebView)alert_view.findViewById(R.id.webv);



                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        webv.setWebViewClient(mWebViewClient);
                                        webv.loadUrl(pict);
                                        webv.getSettings().setBuiltInZoomControls(true);
                                    }
                                });
                            }
                        }.start();


                        Log.v("final url",finalurl.toString());



                        builder.show();
                    }


                });


                dialog.setNeutralButton("離開", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        //設定按鈕

                    }
                });
                dialog.show();
                Log.v("Json", dialog.toString());
                // getting values from selected ListItem

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

                        String add = c.getString(TAG_Add);
                        String name = c.getString(TAG_Name);
                        String Tel = c.getString(TAG_Tel);
                        String description = c.getString(TAG_Description);
                        String opentime = c.getString(TAG_Opentime);
                        String px = c.getString(TAG_Px);
                        String py = c.getString(TAG_Py);
                        String pict = c.getString(TAG_pict);
//                        String address = c.getString(TAG_ADDRESS);
//                        String gender = c.getString(TAG_GENDER);

                        HashMap<String, String> contact = new HashMap<String, String>();


                        contact.put(TAG_Name, name);
                        contact.put(TAG_Tel, Tel);
                        contact.put(TAG_Description,description);
                        contact.put(TAG_Opentime,opentime);
                        contact.put(TAG_Add,add);
                        contact.put(TAG_Px,px);
                        contact.put(TAG_Py,py);
                        contact.put(TAG_pict,pict);


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
                    R.layout.list_item2, new String[] { TAG_Name, TAG_Add,
                    TAG_Tel }, new int[] { R.id.id,
                    R.id.name, R.id.tel });

            lv.setAdapter(adapter);
        }


    }



}

