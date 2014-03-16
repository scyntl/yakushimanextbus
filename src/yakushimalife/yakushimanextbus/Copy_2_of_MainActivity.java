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

public class Copy_2_of_MainActivity extends Activity {

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
			Intent mainintent = new Intent(this, Copy_2_of_MainActivity.class);
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
							//Flag definitions: 30 is seasonal. 20 is irregular. 0 is regular. 35 is a seasonal run on a regular route.
							//-1 means the start/stop points and time were not found in the run being checked.
							if (ok !=-1)
								AnswerString = AnswerString
								+ anewrunner.subAnswerString;
							//Stop if you find a regular (or seasonal) bus.
							if ((ok == 0)|(ok==30)) 
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
						AnswerString = AnswerString	+ "There are no more xbuses.";

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
			try {

				JSONObject obj = new JSONObject(loadJSONFromAsset(anasset));
				JSONObject jObjectResult = obj.getJSONObject("run" + runnumber);
				JSONArray jArray = jObjectResult.getJSONArray("stops");

				flags = jObjectResult.getInt("flags");
				//Flags: 20=not regular; 30=March-November;0=regular.
				
				String stopid = "0";
				String hourhand = "0";
				String minutehand = "0";
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
								case 1: notes=getString(R.string.run1result);break;
								case 2: notes=getString(R.string.run2result);break;
								case 3: notes=getString(R.string.run3result);break;
								case 4:	notes=getString(R.string.run4result);break;
								case 5: notes=getString(R.string.run5result);break;
								case 6: notes=getString(R.string.run6result);break;
								case 56: notes=getString(R.string.run56result);
								if ((pointb<128)|(pointa>19)) foundstop=0;break;
								case 7: notes=getString(R.string.run7result);break;

								case 78: notes=getString(R.string.run78result);
										if ((pointb<100)|(pointa>19)) foundstop=0;break;


								case 11: notes=getString(R.string.run11result);break;
								case 1011: notes=getString(R.string.run1011result);
								if ((pointb<100)|(pointa>19)) foundstop=0;break;

								case 1213: notes=getString(R.string.run1213result);
								if ((pointb<100)|(pointa>19)) foundstop=0;break;

								case 15: notes=getString(R.string.run15result);break;

								case 1416: notes=getString(R.string.run1416result);
								if ((pointb<100)|(pointa>19)) foundstop=0;break;


								case 1718: notes=getString(R.string.run1718result);
								if ((pointb<100)|(pointa>19)) foundstop=0;break;
								case 105: 
									if (pointa>19)
										notes=getString(R.string.run100result);
									else
										notes=getString(R.string.run105result);
									if (pointb<21) foundstop=0;break;
								case 107: 
									if (pointa>19)
										notes=getString(R.string.run100result);
									else
										notes=getString(R.string.run107result);
									if (pointb<21) foundstop=0;break;
								case 110: 
									if (pointa>19)
										notes=getString(R.string.run100result);
									else
										notes=getString(R.string.run110result);
									if (pointb<21) foundstop=0;break;
								case 112: 
									if (pointa>19)
										notes=getString(R.string.run100result);
									else
										notes=getString(R.string.run112result);
									if (pointb<21) foundstop=0;break;
								case 1137: notes=getString(R.string.run1137result);
								if ((pointb<30)|(pointa>29)) foundstop=0;break;
								case 1138: notes=getString(R.string.run1138result);
								if ((pointb<30)|(pointa>29)) foundstop=0;break;
								case 11410: notes=getString(R.string.run11410result);
								if ((pointb<30)|(pointa>29)) foundstop=0;break;
								case 11411: notes=getString(R.string.run11411result);
								if ((pointb<30)|(pointa>29)) foundstop=0;break;
								case 11513: notes=getString(R.string.run11513result);
								if ((pointb<30)|(pointa>29)) foundstop=0;break;
								case 11616: notes=getString(R.string.run11616result);
								if ((pointb<30)|(pointa>29)) foundstop=0;break;
								case 200: notes=getString(R.string.run200result);break;
								case 2056: notes=getString(R.string.run2056result);
								if ((pointb<68)|(pointa>61)) foundstop=0;break;

								case 211011: notes=getString(R.string.run211011result);
								if ((pointb<68)|(pointa>61)) foundstop=0;break;
								case 22911: notes=getString(R.string.run22911result);
								if (pointb>127) notes=getString(R.string.run22911bresult);
								if((68>pointa)|(pointa>72)|(pointb<73)) foundstop=0; break;
								case 2213: notes=getString(R.string.run2213result);
								if((68>pointa)|(pointa>72)|(pointb<73)) foundstop=0; break;
								case 30: notes=getString(R.string.run30result);break;
								case 301: notes=getString(R.string.run301result);
								if (pointb<69) foundstop=0;break;


//								case 401516:
//									if (pointb>112)
//										notes=getString(R.string.run401516bresult);
//									else
//										notes=getString(R.string.run401516result);
//									if ((pointa!=71)|(pointb<69)) foundstop=0;
//									break;
								case 100200: notes=getString(R.string.run100200result);
								if ((pointa<26)|(pointa>29)|(pointb<128)) foundstop=0;break;
								}
							}
							else if (anasset.equals("backruns.json"))
							{
								switch (run) {
								//Counterclockwise ("back") runs
								case -1: notes=getString(R.string.backrun1result);break;
								case -2: notes=getString(R.string.backrun2result);break;
								case -3: notes=getString(R.string.backrun3result);break;
								case -33000: notes=getString(R.string.backrun33000result);break;
								case -4: notes=getString(R.string.backrun4result);break;
								case -45: notes=getString(R.string.backrun45result);
								if ((pointb>19)|(pointa<20)) foundstop=0;break;
								case -67: notes=getString(R.string.backrun67result);
								if ((pointb>19)|(pointa<35)) foundstop=0;break;
								case -910: notes=getString(R.string.backrun910result);
								if ((pointb>19)|(pointa<20)) foundstop=0;break;
								case -1314: notes=getString(R.string.backrun1314result);
								if ((pointb>19)|(pointa<100)) foundstop=0;break;
								case -1516: notes=getString(R.string.backrun1516result);
								if ((pointb>19)|(pointa<100)) foundstop=0;break;
								case -17: notes=getString(R.string.backrun17result);break;
								case -300: notes=getString(R.string.backrun300result);break;
								case -30517:
									if (pointb<20)
										notes=getString(R.string.backrun30517bresult);
									else
										notes=getString(R.string.backrun30517result);
									if ((pointb>67)|(pointa<69)) foundstop=0;break;
								case -220: notes=getString(R.string.backrun220result);break;
								case -220910:
									if (pointb<20)
										notes=getString(R.string.backrun220910bresult);
									else
										notes=getString(R.string.backrun220910result);
									if ((pointb>67)|(pointa<68)) foundstop=0; break;
								case -2301516: 
									if (pointb<20)
										notes=getString(R.string.backrun2301516bresult);
									else
										notes=getString(R.string.backrun2301516result);
										if ((pointb>61)|(pointa<68)) foundstop=0;break;
								case -30223016: 
									if (pointb<20)
										notes=getString(R.string.backrun30223016cresult);
									else{
										if (pointb<62)
											notes=getString(R.string.backrun30223016bresult);
										else
											notes=getString(R.string.backrun30223016result);
									}
										if ((pointb>67)|(pointa<70)) foundstop=0;break;
								case -3206: notes=getString(R.string.backrun3206result);
									if ((pointb>72)|(pointb<68)|(pointa>62&&(pointa<73)))  foundstop=0; break;
								case -11211: notes=getString(R.string.backrun11211result);
								if (pointa>99)
									notes=getString(R.string.backrun11211bresult);
								if ((pointb>72)|(pointb<68)|(pointa>62&&(pointa<73)))   foundstop=0; break;
								case -1135: 
									if (pointb<20)
										notes=getString(R.string.backrun1135bresult);
									else
										notes=getString(R.string.backrun1135result);break;
								case -11410: 
									if (pointb<20)
										notes=getString(R.string.backrun11410bresult);
									else
										notes=getString(R.string.backrun11410result);break;
								case -11514: 
									if (pointb<20)
										notes=getString(R.string.backrun11514bresult);
									else
										notes=getString(R.string.backrun11514result);break;
								case -11616: 
									if (pointb<20)
										notes=getString(R.string.backrun11616bresult);
									else
										notes=getString(R.string.backrun11616result);break;
								case -3105: notes=getString(R.string.backrun3105result);
								if ((pointb>29)|(pointa<30))  foundstop=0; break;
								case -6107: notes=getString(R.string.backrun6107result);
								if ((pointb>29)|(pointa<30))  foundstop=0; break;
								case -9110: 
									if (pointa>99)
										notes=getString(R.string.backrun9110bresult);
									else
										notes=getString(R.string.backrun9110result);
								if ((pointb>29)|(pointa<30))  foundstop=0; break;
								case -13112: notes=getString(R.string.backrun13112result);
								if ((pointb>29)|(pointa<30))  foundstop=0; break;
								}
								break;
							}
						}
					}
				}

			} catch (JSONException e) {
				subAnswerString="JSONException Error. Please report this error to yakushimalife@gmail.com";
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
				Integer.parseInt(stophour);
				Integer.parseInt(stopminute);

				subAnswerString ="Depart-Arrive </dt><br>" + departhour + ":"
						+ departminute + " - " + stophour + ":" + stopminute
						+ "<br>&nbsp&nbsp<i> " + notes + "</i><br><br>";

				ok = 1;
			} else {
				ok = -1;
			}
			this.subAnswerString = subAnswerString;
			
			//flags: 20/35 for irregular buses; 30 for seasonal buses, 0 for regular buses, -1 for no bus.
			if (ok == -1) {
				this.flags = -1;
			} else {
				this.flags = flags;
			}
		}
	}



}
