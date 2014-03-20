package yakushimalife.yakushimanextbus;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.yakushimalife.yakushimanextbus.R;

import android.app.Activity;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

public class MainActivityStripped extends Activity {

	private TextView tvDisplayBus;
	private TextView tvTip;
	private TextView tvDisplayTime;
	private Button btnChangeTime;
	private Button btnGo;
	private Button btnListAll;
	private int hour;
	private int minute;
	static final int TIME_DIALOG_ID = 999;
	private int startpoint;
	private int endpoint;
	private int stopnumber = 1000;
	private int[] all_stops_integers;
	private String AnswerString = "";
	private String Tip = "";
	//This is the list of clockwise and counterclockwise bus runs. Longer numbers are two buses with a transfer.
	//For yakushimalife, 056 is the first half of Bus Run #5 pasted to the second half of Bus Run #6.
	private String[] RunList = { "1", "2", "3", "4", "5", "105", "6", "056", "7", "107", "8",
			"078", "9", "10", "110", "11", "01011", "12", "112", "13", "01213", "14", "15",
			"16", "01416", "17", "18", "1718", 
			"1137", "1138", "11410", "11411", "11513", "11616", "200", "2056",
			"210", "211011", "22911", "2213", "30", "3000", "301", "32", "33",
			"401516", "100200" };

	private String[] RunList2 = { "1", "2", "3", "3000", "33000", "4", "5", "45", "6", "7",
			"67", "8", "9", "10", "910", "11", "12", "13", "14", "1314", "15",
			"16", "1516", "17", "18", "300", "301", "302", "303", "304", "305", "30517",
			"306", "307", "220", "230", "220910", "2301516", "30223016",
			"300220910", "3206", "11211", "1135", "11410", "11514", "11616",
			"3105", "6107", "9110", "13112" };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		setCurrentTimeOnView();
		addListenerOnButton();

		Spinner startpointspinner = (Spinner) findViewById(R.id.startpointspinner);
		Spinner endpointspinner = (Spinner) findViewById(R.id.endpointspinner);
		// Create an ArrayAdapter using the string array and a default spinner
		// layout
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
				this, R.array.all_stops, android.R.layout.simple_spinner_item);
		// Specify the layout to use when the list of choices appears
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// Apply the adapter to the spinner
		startpointspinner.setAdapter(adapter);
		endpointspinner.setAdapter(adapter);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu items for use in the action bar
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main, menu);
		return super.onCreateOptionsMenu(menu);

	}

	// display current time
	public void setCurrentTimeOnView() {
		tvDisplayTime = (TextView) findViewById(R.id.tvTime);
		final Calendar c = Calendar.getInstance();
		hour = c.get(Calendar.HOUR_OF_DAY);
		minute = c.get(Calendar.MINUTE);
		// set current time into textview
		tvDisplayTime.setText(new StringBuilder().append(pad(hour)).append(":")
				.append(pad(minute)));

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle presses on the action bar items
		switch (item.getItemId()) {
		case R.id.action_search:
			Intent mainintent = new Intent(this, MainActivity.class);
			startActivity(mainintent);
			return true;
		case R.id.action_stoplist:
			Intent stoplistintent = new Intent(this, StopListActivity.class);
			startActivity(stoplistintent);
			return true;
		case R.id.action_numbers:
			Intent numbersintent = new Intent(this, NumbersActivity.class);
			startActivity(numbersintent);
			return true;
		case R.id.action_about:
			Intent aboutintent = new Intent(this, AboutActivity.class);
			startActivity(aboutintent);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}


	public void addListenerOnButton() {
		
		//Allow users to enter a specific time.
		btnChangeTime = (Button) findViewById(R.id.btnChangeTime);
		btnChangeTime.setOnClickListener(new OnClickListener() {
			@SuppressWarnings("deprecation")
			@Override
			public void onClick(View v) {
				showDialog(TIME_DIALOG_ID);
			}
		});

		//Press Go! to search for the next bus.
		btnGo = (Button) findViewById(R.id.btnGo);
		btnGo.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				//TODO Fix this code so it clears current results while searching.
				tvDisplayBus = (TextView) findViewById(R.id.tvBus);
				tvDisplayBus.setText(". . .");

				//Convert the Spinner positions into actual stop numbers used by the bus company.
				Spinner startpointspinner = (Spinner) findViewById(R.id.startpointspinner);
				int a = startpointspinner.getSelectedItemPosition();
				Spinner endpointspinner = (Spinner) findViewById(R.id.endpointspinner);
				int b = endpointspinner.getSelectedItemPosition();
				all_stops_integers = getResources().getIntArray(
						R.array.all_stops_integers);
				startpoint = all_stops_integers[a];
				endpoint = all_stops_integers[b];
				//Check that destination!=starting point.
				if (startpoint == endpoint) {
					Tip = getString(R.string.startequalsstop);
					tvTip = (TextView) findViewById(R.id.tvtip);
					tvTip.setText(new StringBuilder().append(Tip));
				} else {
					
					String json = null;
					try {
						InputStream is = getAssets().open("backruns.json");
						int size = is.available();
						byte[] buffer = new byte[size];
						is.read(buffer);
						is.close();
						json = new String(buffer, "UTF-8");
						AnswerString="ok";
						try {

							JSONObject obj = new JSONObject(json);
						}catch (JSONException e) {
							AnswerString= AnswerString+"JSONException Error.";
							e.printStackTrace();}
					} catch (IOException ex) {
						ex.printStackTrace();
						AnswerString="notok";
					}
					
					
				}
				

				tvDisplayBus = (TextView) findViewById(R.id.tvBus);
				tvDisplayBus.setText(Html.fromHtml(AnswerString));
				AnswerString = "";
			}
		});
		
		//List all runs between the starting point and destination.
		btnListAll = (Button) findViewById(R.id.btnListAll);
		btnListAll.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				//TODO Fix this code so it clears the results while searching.
				tvDisplayBus = (TextView) findViewById(R.id.tvBus);
				tvDisplayBus.setText(". . .");
				
				AnswerString="<i>All Buses:</i><br>";
				
				//Convert the Spinner positions into actual stop numbers used by the bus company.
				Spinner startpointspinner = (Spinner) findViewById(R.id.startpointspinner);
				int a = startpointspinner.getSelectedItemPosition();
				Spinner endpointspinner = (Spinner) findViewById(R.id.endpointspinner);
				int b = endpointspinner.getSelectedItemPosition();
				all_stops_integers = getResources().getIntArray(
						R.array.all_stops_integers);
				startpoint = all_stops_integers[a];
				endpoint = all_stops_integers[b];
				//Check that destination!=starting point.
				if (startpoint == endpoint) {
					Tip = getString(R.string.startequalsstop);
					tvTip = (TextView) findViewById(R.id.tvtip);
					tvTip.setText(new StringBuilder().append(Tip));
				} else {
					
					String json = null;
					try {
						InputStream is = getAssets().open("backruns.json");
						int size = is.available();
						byte[] buffer = new byte[size];
						is.read(buffer);
						is.close();
						json = new String(buffer, "UTF-8");
						AnswerString="okall";
					} catch (IOException ex) {
						ex.printStackTrace();
						AnswerString="notokall";
					}
					}


					tvDisplayBus = (TextView) findViewById(R.id.tvBus);
					tvDisplayBus.setText(Html.fromHtml(AnswerString));
					AnswerString = "";

			}
		});
		
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case TIME_DIALOG_ID:
			// set time picker as current time
			return new TimePickerDialog(this, timePickerListener, hour, minute,
					false);
		}
		return null;
	}

	private TimePickerDialog.OnTimeSetListener timePickerListener = new TimePickerDialog.OnTimeSetListener() {
		public void onTimeSet(TimePicker view, int selectedHour,
				int selectedMinute) {
			hour = selectedHour;
			minute = selectedMinute;

			// set current time into textview
			tvDisplayTime.setText(new StringBuilder().append(pad(hour))
					.append(":").append(pad(minute)));
		}
	};

	private static String pad(int c) {
		if (c >= 10)
			return String.valueOf(c);
		else
			return "0" + String.valueOf(c);
	}

	//load run data for all runs in the file called anasset.
	public String loadJSONFromAsset(String anasset) {
		String json = null;
		try {
			InputStream is = getAssets().open(anasset);
			int size = is.available();
			byte[] buffer = new byte[size];
			is.read(buffer);
			is.close();
			json = new String(buffer, "UTF-8");
		} catch (IOException ex) {
			ex.printStackTrace();
			return null;
		}
		return json;
	}





}
