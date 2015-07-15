package app.z0nen.slidemenu;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Maplayout extends Activity {
	LocationManager mLocationManager;
	LocationManager lms;
	Location mylocation;
	Criteria criteria;
	LatLng target;
	private String bestProvider = LocationManager.GPS_PROVIDER;
	String targetInfo;
	double dLat;
	double dLng;
	GoogleMap m_map;
	static LatLng MyLocation;

	ArrayList<LatLng> markerPoints;
	private final double EARTH_RADIUS = 6378137.0;
	PolylineOptions lineOptions = null;
	Marker MyMarker;
	boolean FIRST=true;

	private void locationServiceInitial() {
		lms = (LocationManager) getSystemService(LOCATION_SERVICE);
		String bestProvider = lms.getBestProvider(new Criteria(), true);
		// lms.requestLocationUpdates(bestProvider, 1000, 1, new
		// MyLocationListener());

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.maplayout);
		locationServiceInitial();
		Bundle bundle = getIntent().getExtras();
		// double site_x =
		// Double.valueOf(bundle.getString("site_x")).doubleValue();
		// double site_y =
		// Double.valueOf(bundle.getString("site_y")).doubleValue();
		double data_x = Double.valueOf(bundle.getString("location_x"))
				.doubleValue();
		double data_y = Double.valueOf(bundle.getString("location_y"))
				.doubleValue();
		targetInfo = bundle.getString("info");
		System.out.println(data_x + " " + data_y);
		target = new LatLng(data_y, data_x);
		// 初始化
		markerPoints = new ArrayList<LatLng>();

		mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		criteria = new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_FINE);
		String best = mLocationManager.getBestProvider(criteria, true);
		Location location = mLocationManager.getLastKnownLocation(best);// 取得上次定位位置
		mLocationManager.requestLocationUpdates(best, 1000, 1,
				new MyLocationListener2());

		m_map = ((MapFragment) getFragmentManager()
				.findFragmentById(R.id.map)).getMap();

		m_map.setMyLocationEnabled(true);
		if(location!=null)
		{
			LatLng latlng1=new LatLng(location.getLatitude(), location.getLongitude());
			
			if(MyMarker==null)
			{
				MyMarker=m_map.addMarker(new MarkerOptions().position(latlng1).title("現在位置"));
				
			}else
			{
				MyMarker.setPosition(latlng1);
			}
			m_map.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng1, 15.0f));
			if(FIRST)
			{
				String url = getDirectionsUrl(latlng1, target);
				DownloadTask downloadTask = new DownloadTask();
				downloadTask.execute(url);
				FIRST=false;
			}
		}
//		dLat = location.getLatitude(); // 取得緯度
//		double lng = location.getLongitude();
//		dLng = location.getLongitude();// 取得經度
//		Log.v("location", dLat + "+" + dLng);
		
//		// LatLng WhereNow = new LatLng(22.763511, 120.375725);
//		LatLng WhereNow = new LatLng(dLat, dLng);

		lineOptions = new PolylineOptions();
//		m_map.moveCamera(CameraUpdateFactory.newLatLngZoom(WhereNow, 15.0f));
		
		
		
		m_map.addMarker(new MarkerOptions().position(target).title(targetInfo));

		// Drawing polyline in the Google Map for the i-th route
		m_map.addPolyline(lineOptions);
		
		// Enable MyLocation Button in the Map
		m_map.setMyLocationEnabled(true);

		

		// Start downloading json data from Google Directions
		// API
		

		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction().commit();
		}

	}

	private String getDirectionsUrl(LatLng yoursite, LatLng goalsite) {
		// TODO Auto-generated method stub
		// Origin of route
		String str_yoursite = "origin=" + yoursite.latitude + ","
				+ yoursite.longitude;

		// Destination of route
		String str_goalsite = "destination=" + goalsite.latitude + ","
				+ goalsite.longitude;

		// Sensor enabled
		String sensor = "sensor=false";

		// Building the parameters to the web service
		String parameters = str_yoursite + "&" + str_goalsite + "&" + sensor;

		// Output format
		String output = "json";

		// Building the url to the web service
		String url = "https://maps.googleapis.com/maps/api/directions/"
				+ output + "?" + parameters;
		Log.d("url", url);

		return url;

	}

	private String downloadUrl(String strUrl) throws IOException {
		String data = "";
		InputStream iStream = null;
		HttpURLConnection urlConnection = null;
		try {
			URL url = new URL(strUrl);

			// Creating an http connection to communicate with url
			urlConnection = (HttpURLConnection) url.openConnection();

			// Connecting to url
			urlConnection.connect();

			// Reading data from url
			iStream = urlConnection.getInputStream();

			BufferedReader br = new BufferedReader(new InputStreamReader(
					iStream));

			StringBuffer sb = new StringBuffer();

			String line = "";
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}

			data = sb.toString();

			br.close();

		} catch (Exception e) {
//			Log.d("Exception while downloading url", e.toString());
		} finally {
			iStream.close();
			urlConnection.disconnect();
		}
		return data;
	}

	private class DownloadTask extends AsyncTask<String, Void, String> {

		// Downloading data in non-ui thread
		@Override
		protected String doInBackground(String... url) {

			// For storing data from web service
			String data = "";

			try {
				// Fetching the data from web service
				data = downloadUrl(url[0]);
			} catch (Exception e) {
				Log.d("Background Task", e.toString());
			}
			return data;
		}

		// Executes in UI thread, after the execution of
		// doInBackground()
		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);

			ParserTask parserTask = new ParserTask();

			// Invokes the thread for parsing the JSON data
			parserTask.execute(result);

		}
	}

	private class ParserTask extends
			AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

		// Parsing the data in non-ui thread
		@Override
		protected List<List<HashMap<String, String>>> doInBackground(
				String... jsonData) {

			JSONObject jObject;
			List<List<HashMap<String, String>>> routes = null;

			try {
				jObject = new JSONObject(jsonData[0]);
				DirectionsJSONParser parser = new DirectionsJSONParser();

				// Starts parsing data
				routes = parser.parse(jObject);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return routes;
		}

		// Executes in UI thread, after the parsing process
		@Override
		protected void onPostExecute(List<List<HashMap<String, String>>> result) {
			ArrayList<LatLng> points = null;
			PolylineOptions lineOptions = null;
			MarkerOptions markerOptions = new MarkerOptions();

			// Traversing through all the routes
			for (int i = 0; i < result.size(); i++) {
				points = new ArrayList<LatLng>();
				lineOptions = new PolylineOptions();

				// Fetching i-th route
				List<HashMap<String, String>> path = result.get(i);

				// Fetching all the points in i-th route
				for (int j = 0; j < path.size(); j++) {
					HashMap<String, String> point = path.get(j);

					double lat = Double.parseDouble(point.get("lat"));
					double lng = Double.parseDouble(point.get("lng"));
					LatLng position = new LatLng(lat, lng);

					points.add(position);
				}

				// Adding all the points in the route to LineOptions
				lineOptions.addAll(points);
				lineOptions.width(16); // 導航路徑寬度
				lineOptions.color(Color.RED); // 導航路徑顏色

			}

			// Drawing polyline in the Google Map for the i-th route
			m_map.addPolyline(lineOptions);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.my, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.maplayout,
					container, false);
			return rootView;
		}
	}
	public class MyLocationListener2 implements LocationListener {
		
		
		
		@Override
		public void onLocationChanged(Location location) {
			// TODO Auto-generated method stub
			// String x_site = ""+location.getLatitude();
			// String y_site = ""+location.getLongitude();
//			MyLocation = new LatLng(location.getLatitude(), location.getLongitude());
			Log.d("sasda", location.getLatitude()+"+"+location.getLongitude());
			LatLng latlng1=new LatLng(location.getLatitude(), location.getLongitude());
			if(MyMarker==null)
			{
				MyMarker=m_map.addMarker(new MarkerOptions().position(latlng1).title("現在位置"));
				
			}else
			{
				MyMarker.setPosition(latlng1);
			}
			m_map.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng1, 15.0f));
			if(FIRST)
			{
				String url = getDirectionsUrl(latlng1, target);
				DownloadTask downloadTask = new DownloadTask();
				downloadTask.execute(url);
				FIRST=false;
			}
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onProviderEnabled(String provider) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onProviderDisabled(String provider) {
			// TODO Auto-generated method stub

		}

	}
}
