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

public class MainActivity extends Activity {

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
	private String[] RunList = { "300", "99901", "1", "301", "99902", "302", "303", "2", "3", "4", "5", "200", "999100", "100", 
			"6", "99903", "7", "101", "99904", "8", "9", "999905", "304", "10", "102", "99906",
			"11", "201", "999101", "12", "99907", "13", "103", "99908", "14", "99909", "16", "15", "99910", "17", "18", "99911" };

	private String[] RunList2 = { "1", "2", "99901", "3", "100", "300", "999100", "4", "5", "6", "99902", "7", "8", "99903",
			"200", "9",  "99904","10", "11", "12", "99905", "101", "13", "99906", "301", "14", "15", "999101", "102",
			"201", "99907", "302", "16", "99908", "303", "17", "18", "304", "99909", "99910", "305", "19",
			"306", "307" };

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
					int ok = 0;
					//Check clockwise runs for the next bus.
					if (startpoint < endpoint) {
						for (int i = 0; i < RunList.length; i++) {
							checkrun anewrunner = new checkrun(RunList[i],
									startpoint, endpoint, hour, minute,
									"runs.json");
							ok = anewrunner.flags;
							//Flag definitions: 30 is seasonal. 20 is irregular. 0 is regular. 35 is a seasonal run on a regular route. 99 is Matsubanda.
							//-1 means the start/stop points and time were not found in the run being checked.
							if (ok !=-1)
								AnswerString = AnswerString
								+ anewrunner.subAnswerString;
							//Stop if you find a regular  bus.
							if (ok == 0) 
								break;
						}
					} else {
						//Check counterclockwise runs for the next bus.
						for (int i = 0; i < RunList2.length; i++) {
							checkrun anewrunner = new checkrun(RunList2[i],
									startpoint, endpoint, hour, minute,
									"backruns.json");
							ok = anewrunner.flags;
							//Flag definitions: 30 is seasonal. 20 is irregular. 0 is regular. 35 is a seasonal run on a regular route.
							if (ok !=-1)
								AnswerString = AnswerString
								+ anewrunner.subAnswerString;
							//Stop if you find a regular (or seasonal) bus.
							if ((ok == 0)|(ok==30)) 
								break;
						}
					}

					//If the start/stop points and times were not found in the last run checked:
					if (ok == -1) 
						AnswerString = AnswerString	+ "There are no more buses.";

					tvDisplayBus = (TextView) findViewById(R.id.tvBus);
					tvDisplayBus.setText(Html.fromHtml(AnswerString));
					AnswerString = "";

					//Get tips for certain stops.
					Tip="";
					//Tip for travelers to Anbo Port.
					if (startpoint == 64 | endpoint == 64)
						Tip = Tip + getString(R.string.noteanboport);
					//Tip for travelers to Oko-no-Taki.
					if (startpoint == 129 | endpoint == 129)
						Tip = Tip + getString(R.string.noteokonotaki);
					tvTip = (TextView) findViewById(R.id.tvtip);
					tvTip.setText(new StringBuilder().append(Tip));

				}
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
					int ok = 0;
					int count=0;
					//Check clockwise runs for the next bus.
					if (startpoint < endpoint) {
						for (int i = 0; i < RunList.length; i++) {
							checkrun anewrunner = new checkrun(RunList[i],
									startpoint, endpoint, 0, 0,
									"runs.json");
							ok = anewrunner.flags;
							//If the run currently being checked contains the start/stop points, add it to the AnswerString, but keep searching.
							if (ok !=-1){
								AnswerString = AnswerString
										+ anewrunner.subAnswerString;
								count++;
							}
						}
					} else {
						//Check counterclockwise runs for the next bus.
						for (int i = 0; i < RunList2.length; i++) {
							checkrun anewrunner = new checkrun(RunList2[i],
									startpoint, endpoint, 0, 0,
									"backruns.json");
							ok = anewrunner.flags;
							//If the run currently being checked contains the start/stop points, add it to the AnswerString, but keep searching.
							if (ok !=-1){
								AnswerString = AnswerString
										+anewrunner.subAnswerString;
								count++;
							}
						}
					}

					//If the start/stop points and times were not found in any runs:
					if (count==0) 
						AnswerString = AnswerString	+ "There are no more buses.";

					tvDisplayBus = (TextView) findViewById(R.id.tvBus);
					tvDisplayBus.setText(Html.fromHtml(AnswerString));
					AnswerString = "";

					//Get tips for certain stops.
					Tip="";
					if (startpoint == 64 | endpoint == 64)
						Tip = Tip + getString(R.string.noteanboport);
					if (startpoint == 129 | endpoint == 129)
						Tip = Tip + getString(R.string.noteokonotaki);
					tvTip = (TextView) findViewById(R.id.tvtip);
					tvTip.setText(new StringBuilder().append(Tip));

				}
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

	public class checkrun {
		private String subAnswerString;
		private int flags;
		public checkrun(String runnumber, int pointa, int pointb,
				int starthour, int startminute, String anasset) {

			int foundstart = 0;
			int foundstop = 0;
			String notes = "";
			int run = 0;
			int flags = 20;
			int ok = 0;
			String subAnswerString = "";
			ArrayList<String[]> startdata = new ArrayList<String[]>();
			ArrayList<String[]> stopdata = new ArrayList<String[]>();
			String hourhand = "0";
			String minutehand = "0";

			try {

				JSONObject obj = new JSONObject(loadJSONFromAsset(anasset));
				JSONObject jObjectResult = obj.getJSONObject("run" + runnumber);
				JSONArray jArray = jObjectResult.getJSONArray("stops");

				flags = jObjectResult.getInt("flags");
				//Flags: 20=not regular; 30=March-November;0=regular.

				String stopid = "0";
				hourhand = "0";
				minutehand = "0";
				// Search the run for the start point.
				for (int i = 0; i < jArray.length(); i++) {
					stopid = jArray.getJSONObject(i).getString("stopid");
					stopnumber = Integer.parseInt(stopid);
					if (stopnumber == pointa) {
						hourhand = jArray.getJSONObject(i)
								.getString("hourhand");
						minutehand = jArray.getJSONObject(i).getString(
								"minutehand");
						// Is the time after requested departure?
						if ((Integer.parseInt(hourhand) > starthour)
								| ((Integer.parseInt(hourhand) == starthour) & (Integer
										.parseInt(minutehand) >= startminute))) {
							startdata.add(new String[] { stopid, hourhand,
									minutehand });
							foundstart = i;
							break;
						} else
							foundstart = 0;
					}
				}
				// Search the run for the endpoint.
				if (foundstart!=0)
				{
					for (int i = foundstart; i < jArray.length(); i++) {
						stopid = jArray.getJSONObject(i).getString("stopid");
						stopnumber = Integer.parseInt(stopid);
						if (stopnumber == pointb) {
							hourhand = jArray.getJSONObject(i)
									.getString("hourhand");
							minutehand = jArray.getJSONObject(i).getString(
									"minutehand");
							stopdata.add(new String[] { stopid, hourhand,
									minutehand });
							foundstop = stopnumber;

							notes = jObjectResult.getString("notes");
							run=jObjectResult.getInt("run");
							//Get notes to display with each run result. This is pretty nasty, but I want to be able
							//to display the notes in multiple languages and include directions for making transfers.
							if (anasset.equals("runs.json"))
							{
								switch (run) {
								case 300: notes=getString(R.string.Tozan); break;
								case 99901:
									notes=getString(R.string.MatsubandaToShizenkan);
									if (pointb==70) notes=notes+getString(R.string.R99901T300);
									break;
								case 1:
									notes=getString(R.string.ToShizenkan);
									if (pointb==70) 
										notes=notes+getString(R.string.R1T300);
									else 
										flags=20;
									break;
									//run 301 ->300
								case 99902:
									notes=getString(R.string.MatsubandaToShizenkan);
									if (pointb==70) {
										notes=notes+getString(R.string.R99902T301);
										flags=0;
									}
									break;
									//run 302 ->300
								case 303: 
									notes=getString(R.string.Tozan303);
									flags=303;
									break;
								case 2: 
									notes=getString(R.string.R2);
									flags=2;
									break;
								case 3:
									notes=getString(R.string.R3);
									flags=3;
									break;
								case 4:
									notes=getString(R.string.R4);
									flags=4;
									break;
								case 5:
									notes=getString(R.string.ToKurioBashi);
									if (pointb>127)
										notes=notes+getString(R.string.R5T6);
									else if (68<=pointb&&pointb<=72) {
										if (pointa<62)
											notes=notes+getString(R.string.R5T200);
										else
											foundstop=0;
									}
									else if (26<=pointb&&pointb<=29) {
										if (pointa<20)
											notes=notes+getString(R.string.R5T999100100);
										else 
											foundstop=0;
									}
									if ((pointa>=127)|(68<=pointa&&pointa<=72)|(26<=pointa&&pointa<=29)| ((pointa>=20)&(68<=pointb&&pointb<=72)) | (pointa>=20&&pointb>127)) 
										foundstop=0; 
									break;
								case 200:
									notes=getString(R.string.ToKigenSugi);
									break;
								case 999100:
									notes=getString(R.string.MatsubandaToShiratani);
									break;
								case 100:
									notes=getString(R.string.ToShiratani);
									break;
								case 6:
									notes=getString(R.string.ToOkonoTaki);
									if (67<pointb&&pointb<73) {
										if (pointa<62)
											notes=notes+getString(R.string.R6T200);
										else
											foundstop=0;
									}
									if (68<=pointa&&pointa<=72)
										foundstop=0;
									break;
								case 99903:
									notes=getString(R.string.MatsubandaToShizenkan);
									if (25<pointa&&pointa<30) 
										notes=getString(R.string.ToMiyanouraKo)+getString(R.string.B101T99903);
									if (25<pointb&&pointb<30) 
										foundstop=0;
									break;
								case 7:
									notes=getString(R.string.ToIwasaki);
									if (25<pointa&&pointa<30){
										if (pointb<=99)
											notes=getString(R.string.ToMiyanouraKo)+getString(R.string.B100T7);
										else
											notes=getString(R.string.ToMiyanouraKo)+getString(R.string.B100T8);
									}
									else if (pointa==68) {
										if (pointb<=99)
											notes=getString(R.string.MatsubandaToMiyanouraKo)+getString(R.string.B99903T7);
										else
											notes=getString(R.string.MatsubandaToMiyanouraKo)+getString(R.string.B99903T8);
									}
									else if (pointb>99) {
										if (pointa>=20)
											foundstop=0;
										else	
											notes=notes+getString(R.string.R7T8);
									}
									else if (pointb==68)  {
										if (pointa>=20)
											foundstop=0;
										else{
											notes=notes+getString(R.string.R7T99904);
											stopdata.set(0, new String[] {stopdata.get(0)[0], "10", "53"});
										}
									}
									if ((25<pointb&&pointb<30)|(pointa>98)) foundstop=0;
									break;
								case 101:
									if (pointa<20)
										notes=getString(R.string.ToIwasaki)+getString(R.string.R7T101);
									else
										notes=getString(R.string.ToShiratani);
									if (pointb<=20)
										foundstop=0;
									break;
								case 99904:
									notes=getString(R.string.MatsubandaToShizenkan);
									break;
								case 8:
									//	if (25<pointa&&pointa<30)these are in run 7
									notes=getString(R.string.ToKurioBashi);
									break;
								case 9:
									if (68<=pointa&&pointa<=72)
										notes=getString(R.string.ToGoChoMae)+getString(R.string.B200T9);
									else
										notes=getString(R.string.ToKurioBashi);
									if (67<pointb&&pointb<73) foundstop=0;
									break;
								case 99905:
									if (pointa<20) {
										if (pointb==68||pointb==70)
											notes=getString(R.string.ToKurioBashi)+getString(R.string.R9T99905);
										else
											foundstop=0;
									}
									else 
										notes=getString(R.string.MatsubandaToShizenkan);
									if (pointb==70) 
										notes=notes+getString(R.string.R99905T304);
									if(pointb<20)
										foundstop=0;
									break;
									//run 304 --> 300
								case 10:
									notes=getString(R.string.ToIwasaki);
									if (25<pointa&&pointa<30){
										notes=getString(R.string.ToMiyanouraKo);
										if (pointb>99)
											notes=notes+getString(R.string.B101T11);
										else
											notes=notes+getString(R.string.B101T10);}
									else if (pointb>99)
										notes=notes+getString(R.string.R10T11);
									switch (pointb){
									case 26:
										if (pointa<20){
											notes=notes+getString(R.string.R10T102);
											stopdata.set(0, new String[] {stopdata.get(0)[0], "14", "02"});
										}
										else 
											foundstop=0;
										break;
									case 29:
										if (pointa<20){
											notes=notes+getString(R.string.R10T102);
											stopdata.set(0, new String[] {stopdata.get(0)[0], "14", "25"});
										}
										else 
											foundstop=0;
										break;
									case 68:
										if (pointa<20){
											notes=notes+getString(R.string.R10T99906);
											stopdata.set(0, new String[] {stopdata.get(0)[0], "13", "33"});
										}
										break;
									}
									if (68<pointb&&pointb<73)
										notes=notes+getString(R.string.R10T201);
									if ((25<pointa&&pointa<30)&(25<pointb&&pointb<30) | (67<pointa&&pointa<73) | (pointa>=20&&(68<=pointb&&pointb<73)))
										foundstop=0;
									break;
									//run 102 --> case 100
								case 99906:
									notes=getString(R.string.MatsubandaToShizenkan);
									if (pointb>68)
										notes=notes+getString(R.string.R99906T201);
									if (pointa>67)
										foundstop=0;
									break;
								case 11:
									notes=getString(R.string.ToOkonoTaki);
									switch (pointb){
									case 68:
										if (pointa<62){
											notes=notes+getString(R.string.R11T201);
											stopdata.set(0, new String[] {stopdata.get(0)[0], "13", "39"});
										}
										else
											foundstop=0;
										break;
									case 69:
										if (pointa<62)
											notes=notes+getString(R.string.R11T201);
										else
											foundstop=0;
										break;
									case 71:
										if (pointa<62)
											notes=notes+getString(R.string.R11T201);
										else
											foundstop=0;
										break;
									case 72:
										if (pointa<62)
											notes=notes+getString(R.string.R11T201);
										else
											foundstop=0;
										break;
									}
									if(69<pointa&&pointa<73)
										foundstop=0;
									if (pointa==68){
										if (pointb>=73)
											notes=getString(R.string.MatsubandaToMiyanouraKo)+getString(R.string.B99905T11);
										else foundstop=0;
									}

									break;
									//run 201 --> 200
									//run 999101 --> 999100
								case 12:
									if (pointa==68){
										if (pointb>68)
											notes=getString(R.string.MatsubandaToMiyanouraKo)+getString(R.string.B99905T12);
										else
											foundstop=0;
									}
									else
										notes=getString(R.string.ToIwasaki);
									if (pointb>99)
										notes=notes+getString(R.string.R12T13);
									else if (25<pointb&&pointb<30)
										notes=notes+getString(R.string.R12T103);
									else if (pointb==68){
										if (pointa==67)
											foundstop=0;
										else{
											notes=notes+getString(R.string.R12T99907);
											stopdata.set(0, new String[] {stopdata.get(0)[0], "15", "33"});
											if (pointa>19)
												flags=12;//TODO set code for this flag.
										}
									}
									if ((pointa>=99) | (26<=pointa&&pointa<=29) | (25<pointb&&pointb<30&&pointa>=20))
										foundstop=0;
									break;
									//run 99907 -->99900
								case 13:
									if (25<pointa&&pointa<30)
										notes=getString(R.string.MatsubandaToMiyanouraKo)+getString(R.string.B999101T13);
									//There is a Kotsu bus that leaves a few minutes earlier.
									else if (69<=pointa&&pointa<=72)
										notes=getString(R.string.ToGoChoMae)+getString(R.string.B201T13);
									else if (pointa==68)
										notes=getString(R.string.ToGoChoMae)+getString(R.string.B99907T13);
									else
										notes=getString(R.string.ToOkonoTaki);
									if ((25<pointb&&pointb<30)|(67<pointb&&pointb<73))
										foundstop=0;
									break;
									//run 103 --> case 100
									//run 99908 --> 99900
								case 14:
									notes=getString(R.string.ToIwasaki);
									if (pointb>99)
										notes=notes+getString(R.string.R14T16);
									else if (pointb==68){
										notes=notes+getString(R.string.R14T99909);
										if (pointa>19)
											flags=14;//TODO set code for this flag.
									}
									if ((pointa>98)|(pointa==68))
										foundstop=0;
									break;
									//run 99909 -->99900
								case 16:
									if (25<pointa&&pointa<30)
										notes=getString(R.string.ToMiyanouraKo)+getString(R.string.B103T16);
									else
										notes=getString(R.string.ToKurioBashi);
									if (25<pointb&&pointb<30)
										foundstop=0;
									break;
								case 15:
									if (pointa==70)
										notes=getString(R.string.BTozan)+getString(R.string.B305T15);
									else
										notes=getString(R.string.ToHirauchi);
									if (pointb>=114){
										if (pointa==68||pointa==70)
											notes=notes+getString(R.string.R15T18);
										else
											foundstop=0;
									}										
									if (pointb==70)
										foundstop=0;
									break;
									//run 99910 -->99900
								case 17:
									notes=getString(R.string.ToIwasaki);
									if (pointb>99)
										notes=notes+getString(R.string.R17T18);
									if (pointa>98)
										foundstop=0;
									if (pointa==68){
										if (pointb<=99)
											notes=getString(R.string.MatsubandaToMiyanouraKo)+getString(R.string.B99909T17);
										else
											foundstop=0;
										}
									if (pointb==68)
										foundstop=0;
									break;
								case 18:
									if (pointa==68)
										notes=getString(R.string.R99910T18);
									else if (pointa==70)
										notes=getString(R.string.BTozan)+getString(R.string.B306T99910)+getString(R.string.R99910T18);
									else 
										notes=getString(R.string.ToKurioBashi);
									if (pointb==68||pointb==70)
										foundstop=0;
									break;
								case 99911:
									notes=getString(R.string.MatsubandaToAirport);
									break;
								}
							}
							else if (anasset.equals("backruns.json"))
							{
								switch (run) {
								//Counterclockwise ("back") runs
								case -1: 
									notes=getString(R.string.ToShizenkan);
									if (pointb==70)
										notes=notes+getString(R.string.B1T303);
									else if (pointa==70)
										foundstop=0;
									break;
								case -2:
									notes=getString(R.string.ToMiyanouraKo);
									flags=-2;
									break;
								case -99901:
									notes=getString(R.string.MatsubandaToMiyanouraKo);
									if (26<=pointb&&pointb<=29)
										notes=notes+getString(R.string.B99901T999100);
									break;	
								case -3:
									notes=getString(R.string.ToMiyanouraKo);
									if (pointb<20)
										notes=notes+getString(R.string.B3TB4);
									if (26<=pointb&&pointb<=29)
										notes=notes+getString(R.string.B3T100);
									if ((pointa<=20)|(26<=pointa&&pointa<=29))
										foundstop=0;
									break;	
								case -100:
									notes=getString(R.string.ToMiyanouraKo);
									break;
								case -300:
									notes=getString(R.string.Tozan);
									break;
								case -999100:
									notes=getString(R.string.MatsubandaToMiyanouraKo);
									break;
								case -4:
									notes=getString(R.string.ToNagata);
									break;

								case -5:
									notes=getString(R.string.ToMiyanouraKo);
									if (pointb<20)
										notes=notes+getString(R.string.B5TB6);
									else if (68<=pointb&&pointb<=72)
										notes=notes+getString(R.string.B5T200);//TODO Did you include Yland on transfers to 200/B200 201/B201?
									if ((pointa<=20)|(68<=pointa&&pointa<=72))
										foundstop=0;
									break;
								case -6:
									if (26<=pointa&&pointa<=29)
										notes=getString(R.string.ToMiyanouraKo)+getString(R.string.B100TB6);
									else
										notes=getString(R.string.ToNagata);
									if (26<=pointb&&pointb<=29)
										foundstop=0;
									break;
								case -99902:
									notes=getString(R.string.MatsubandaToMiyanouraKo);
									if (pointb<20){
										if (pointa>20)
											notes=notes+getString(R.string.B99902TB6);
										else
											foundstop=0;
									}
									if (pointa<=20)
										foundstop=0;
									break;
								case -7:
									notes=getString(R.string.ToMiyanouraKo);
									if (pointb<20)
										notes=notes+getString(R.string.B7TB8);
									else if (26<=pointb&&pointb<=29)
										notes=notes+getString(R.string.B7T101);
									if ((pointa<=37)|(26<=pointa&&pointa<=29))
										foundstop=0;
									break;
								case -8:
									notes=getString(R.string.ToNagata);//SHIRATANI
									break;
								case -99903:
									if (pointa==70)
										notes=getString(R.string.Tozan)+getString(R.string.B300TB99903);
									else
										notes=getString(R.string.MatsubandaToMiyanouraKo);
									break;
								case -200:
									notes=getString(R.string.ToGoChoMae);
									break;
								case -9:
									notes=getString(R.string.ToMiyanouraKo);
									break;
								case -99904:
									notes=getString(R.string.MatsubandaToMiyanouraKo);
									break;
								case -10:
									notes=getString(R.string.ToMiyanouraKo);
									if (pointa==129){
										if (68<=pointb&&pointb<=72)
											switch (pointb) {
											case 72:
												notes=notes+getString(R.string.B10T201);
												stopdata.set(0, new String[] {stopdata.get(0)[0], "14", "32"});
												break;
											case 71:
												notes=notes+getString(R.string.B10T201);
												stopdata.set(0, new String[] {stopdata.get(0)[0], "14", "12"});
												break;
											case 70:
												notes=notes+getString(R.string.B10T304);
												stopdata.set(0, new String[] {stopdata.get(0)[0], "13", "35"});
												break;
											case 69:
												notes=notes+getString(R.string.B10T201);
												stopdata.set(0, new String[] {stopdata.get(0)[0], "14", "06"});
												break;
											case 68:
												notes=notes+getString(R.string.B10T99905);
												break;
											}
										else if (26<=pointb&&pointb<=29)
											notes=notes+getString(R.string.B10T102);
									}
									else if (pointa<=68&&pointa<=72)
										notes=getString(R.string.ToGoChoMae)+getString(R.string.B200T10);										
									if (pointb<20){
										if (pointa>20)
											notes=notes+getString(R.string.B10TB11);
										else 
											foundstop=0;
									}
									if (((69<=pointb&&pointb<=72)|(pointb==68)|(26<=pointb&&pointb<=29))&(pointa!=129) | (pointa<20) | (26<=pointa&&pointa<=29))
										foundstop=0;									
									break;
								case -11:
									notes=getString(R.string.ToNagata);
									if (pointa>20)
										notes=getString(R.string.ToMiyanouraKo)+getString(R.string.B101T11);
									if (pointb>=20)
										foundstop=0;
									break;
								case -12:
									notes=getString(R.string.ToNagata);
									if (pointb==68)
										notes=notes+getString(R.string.B12T99906);
									else if (69<=pointb&&pointb<=72)
										notes=notes+getString(R.string.B12T201);
									else if (26<=pointb&&pointb<=29)
										notes=notes+getString(R.string.B12T102);
									if ((68<=pointa&&pointa<=72)|(26<=pointa&&pointa<=29))
										foundstop=0;
									break;
								case -99905:
									notes=getString(R.string.MatsubandaToMiyanouraKo);
									break;
									//run -101 --> 100
								case -13:
									notes=getString(R.string.ToMiyanouraKo);
									break;
								case -99906:
									notes=getString(R.string.MatsubandaToMiyanouraKo);
									break;
									//run -304 --> 300
								case -14:
									notes=getString(R.string.ToMiyanouraKo);
									if (pointb<20)
										notes=notes+getString(R.string.B14TB15);
									else if (26<=pointb&&pointb<=29){
										if (pointa>99)
											notes=notes+getString(R.string.B14T103);
										else
											foundstop=0;
									}
									if ((26<=pointb&&pointb<=29)|(pointa<=20))
										foundstop=0;
									break;
								case -15:
									if (26<=pointa&&pointa<=29)
										notes=getString(R.string.B103TB15);
									else if (pointa==68){
										if (pointb<20)
											notes=getString(R.string.MatsubandaToMiyanouraKo)+getString(R.string.B99906B15);
										else
											foundstop=0;
									}
									else									
										notes=getString(R.string.ToNagata);
									switch (pointb){
									case 26:
										notes=notes+getString(R.string.B15T103);
										stopdata.set(0, new String[] {stopdata.get(0)[0], "15", "42"});
										break;
									case 29:
										notes=notes+getString(R.string.B15T103);
										stopdata.set(0, new String[] {stopdata.get(0)[0], "16", "05"});
										break;
									case 68:
										notes=notes+getString(R.string.B15T99909);
										stopdata.set(0, new String[] {stopdata.get(0)[0], "15", "33"});
										break;
									}
									break;
								case -999101:
									notes=getString(R.string.MatsubandaToMiyanouraKo);
									break;
									//run -102 -->-100
								case -201:
									notes=getString(R.string.ToGoChoMae);
									break;
								case -99907:
									if (pointa==70){
										if (pointb<20)
											notes=getString(R.string.Tozan)+getString(R.string.B302TB99907)+getString(R.string.B99907TB18);
										else if (pointb<62)
											notes =getString(R.string.Tozan)+getString(R.string.B302TB99907b);
										else 
											notes=getString(R.string.Tozan)+getString(R.string.B302TB201);
									}
									else if (68<pointa&&pointa<=72){
										notes=getString(R.string.ToGoChoMae);//comment for Kotsu
										if (pointb<20)
											notes=notes+getString(R.string.B201TB18);
										else if (pointb<62)
											notes=notes+getString(R.string.B201TB99907);
										else
											foundstop=0;
									}									
									else
										notes=getString(R.string.MatsubandaToMiyanouraKo);
									if (pointb<20){
										if (pointa<68)
											foundstop=0;
										else
											notes=notes+getString(R.string.B99907TB18);
									}
									if ((pointa<20)|(68<pointb&&pointb<=72)|(62<pointb&&pointa!=70))
										foundstop=0;
									break;
									// run -302 --> 300
								case -16:
									notes=getString(R.string.ToMiyanouraKo);
									if (pointb<20){
										if (pointa<=99)
											foundstop=0;
									}
									else if (pointb==68){
										if (pointa>68)
											notes=notes+getString(R.string.B16T99909);
										else 
											foundstop=0;			
									}
									else
										notes=notes+getString(R.string.B16TB18);
									if (pointa<=20)
										foundstop=0;
									break;
								case -99908:
									if (pointa==70)
										notes=getString(R.string.Tozan)+getString(R.string.B303TB99908);
									else
										notes=getString(R.string.MatsubandaToMiyanouraKo);
									if (pointb==70)
										foundstop=0;
									break;
									//run -303 --> 300
								case -17:
									if (pointa==70){
										notes=getString(R.string.Tozan)+getString(R.string.B304TB17);
										flags=0;}
									else{
										notes=getString(R.string.ToSeaSide);
										flags=30;
										flags=0;
									}
									if (pointb<19)
										notes=notes+getString(R.string.B17TB18);
									if (pointb==70||pointa<=20)
										foundstop=0;
									break;
								case -18:
									if (26<=pointa&&pointa<=29)
										notes=getString(R.string.ToMiyanouraKo)+getString(R.string.B103TB18);
									else if (pointa==68){
										if (pointb<20)
											notes=getString(R.string.ToMiyanouraKo)+getString(R.string.B99908TB18);
										else
											foundstop=0;
									}
									else
										notes=getString(R.string.ToNagata);
									if (pointb==68){
										if (pointa>68){
											notes=notes+getString(R.string.B18T99910);
											stopdata.set(0, new String[] {stopdata.get(0)[0], "18", "13"});
										}
										else 
											foundstop=0;
									}
									if (26<=pointb&&pointb<=29)
										foundstop=0;
									break;
									//run -304 -->300
								case -99909:
									if (pointa==70)
										notes=getString(R.string.Tozan)+getString(R.string.B305TB99909);
									else
										notes=getString(R.string.MatsubandaToMiyanouraKo);
									break;
								case -99910:
									if (pointa==70)
										notes=getString(R.string.Tozan)+getString(R.string.B306TB99910);
									else
										notes=getString(R.string.MatsubandaToMiyanouraKo);
									break;
									//run -305 --> 300
								case -19:
									notes=getString(R.string.ToMiyanouraKo);
									break;
									// run -306 --> 300
									// run -307 --> 300								
								}	


								//TODO ADD RUNS BTN ARAKAWA AND KIGEN SUGI AN SHIRATANI.
								break;
							}
						}
					}
				}

			} catch (JSONException e) {
				subAnswerString="JSONException Error. Please report this error to yakushimalife@gmail.com";//TODO make this usable.
				e.printStackTrace();
			}
			if ((foundstop != 0) && (foundstart != 0)) {
				String[] kk = null;
				kk = startdata.get(0);
				String departhour = kk[1];
				String departminute = kk[2];
				kk = stopdata.get(0);
				String stophour = kk[1];
				String stopminute = kk[2];
//				Integer.parseInt(stophour);
//				Integer.parseInt(stopminute);

				subAnswerString ="Depart-Arrive </dt><br>" + departhour + ":"
						+ departminute + " - " + stophour + ":" + stopminute
						+ "<br>&nbsp&nbsp<i> " + notes + "</i><br><br>";

				ok = 1;
			} else {
				ok = -1;
			}
			this.subAnswerString = subAnswerString;

			//flags: 20/35 for irregular buses; 30 for seasonal buses, 0 for regular buses, -1 for no bus. 99 for matsubanda
			if (ok == -1) {
				this.flags = -1;
			} else {
				this.flags = flags;
			}
		}
	}



}
