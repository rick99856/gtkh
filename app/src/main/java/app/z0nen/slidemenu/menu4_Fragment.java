package app.z0nen.slidemenu;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
public class menu4_Fragment extends Fragment {
    private ProgressDialog pDialog;
    private static String url = "http://data.kaohsiung.gov.tw/Opendata/DownLoad.aspx?Type=2&CaseNo1=AV&CaseNo2=5&FileType=2&Lang=C&FolderType=U";

    // JSON Node names

    private static final String TAG_Name = "旅宿名稱";
    private static final String TAG_Country = "縣市";
    private static final String TAG_Tel = "電話";
    private static final String TAG_Add = "地址";
    private static final String TAG_Fax = "傳真";
    private static final String TAG_Town = "鄉鎮";
    private static final String TAG_Web = "網址";
    // contacts JSONArray
    JSONObject contacts = null;
    ListView lv;

    // Hashmap for ListView
    ArrayList<HashMap<String, String>> contactList;


    View rootview;
    @Nullable

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootview = inflater.inflate(R.layout.menu4_layout, container, false);
        contactList = new ArrayList<HashMap<String, String>>();



        lv =(ListView)rootview.findViewById(R.id.list4);


        new GetContacts().execute();
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                String add = null;
                String tel = null;
                String fax = null;
                String web = null;
                AlertDialog.Builder dialog=new AlertDialog.Builder(getActivity());
                dialog.setTitle("詳細內容");
                JSONObject oo = new JSONObject(contactList.get(position));
                try {
                    add = oo.getString("地址");
                    tel = oo.getString("電話");
                    fax = oo.getString("傳真");
                    web = oo.getString("網址");
                } catch (JSONException e) {
                    e.printStackTrace();
                }


                dialog.setMessage("地址:\n"+add+"\n電話:\n        "+tel
                            +"\n傳真:\n"+fax+"\n網站:\n "+
                                web).show();
                Log.v("Json",dialog.toString());
                // getting values from selected ListItem




            }


        });
        return rootview;
    }




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
                        String Fax = c.getString(TAG_Fax);
                        String Country = c.getString(TAG_Country);
                        String Town = c.getString(TAG_Town);
                        String Web = c.getString(TAG_Web);
//                        String address = c.getString(TAG_ADDRESS);
//                        String gender = c.getString(TAG_GENDER);

                        HashMap<String, String> contact = new HashMap<String, String>();


                        contact.put(TAG_Name, name);
                        contact.put(TAG_Tel, Tel);
                        contact.put(TAG_Fax,Fax);
                        contact.put(TAG_Country,Country);
                        contact.put(TAG_Add,add);
                        contact.put(TAG_Web,Web);
                        contact.put(TAG_Town,Town);


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
                    R.layout.list_item2, new String[] { TAG_Name, TAG_Country,
                    TAG_Town }, new int[] { R.id.id,
                    R.id.name, R.id.tel });

            lv.setAdapter(adapter);
        }


    }



}

