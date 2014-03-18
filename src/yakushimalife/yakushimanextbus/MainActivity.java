package yakushimalife.yakushimanextbus;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import com.yakushimalife.yakushimanextbus.R;

public class MainActivity extends NextBusActivity {

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
	private final String[] RunList = { "300", "99901", "1", "301", "99902", "302", "303", "2", "3", "4", "5", "200", "999100", "100", 
			"6", "99903", "7", "101", "99904", "8", "9", "999905", "304", "10", "102", "99906",
			"11", "201", "999101", "12", "99907", "13", "103", "99908", "14", "99909", "16", "15", "99910", "17", "18", "99911" };

	private final String[] RunList2 = { "1", "2", "99901", "3", "3000", "33000", "4", "5", "45", "99902", "6", "7", "99903",
			"67", "8",  "799904","9", "10", "910", "11", "99905", "12", "99906", "13", "14", "1314", "99907", "15", "99908",
			"16", "302301516", "1516", "17", "99909", "99910", "18", "300", "301", "302", "303", "304", "305", "30517",
			"306", "307", "300220910",
			"3206", "11211", "1135", "11410", "11514", "11616",
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

	private final TimePickerDialog.OnTimeSetListener timePickerListener = new TimePickerDialog.OnTimeSetListener() {
		@Override
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
		private final String subAnswerString;
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
								case 300: notes=getString(R.string.Tozan); break;
								case 99901:
									notes=getString(R.string.MatsubandaToShizenkan);
									if (pointb==70) notes=notes+getString(R.string.R99901T300);
									flags=99901;//TODO 
									break;
								case 1:
									notes=getString(R.string.ToShizenkan);
									if (pointb==70) 
										notes=notes+getString(R.string.R1T300);
									else 
										flags=20;//TODO
									break;
									//run 301 ->300
								case 99902:
									notes=getString(R.string.MatsubandaToShizenkan);
									if (pointb==70) notes=notes+getString(R.string.R99902T301);
									flags=99902;//TODO 
									break;
									//run 302 ->300
									//run 303 ->300
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
									else if (68<=pointb&&pointb<=72) 
										notes=notes+getString(R.string.R5T200);
									else if (26<=pointb&&pointb<=29) 
										notes=notes+getString(R.string.R5T999100100);
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
									if (67<pointb&&pointb<73) 
										notes=notes+getString(R.string.R6T200);
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
										else	
											notes=notes+getString(R.string.R7T99904);
									}
									//TODO change finish time for pointb.
									//should say that you could transfer to the Matsubanda bus at any stop from Miyanoura to Makino.
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
									if (pointa<20) 
										notes=getString(R.string.ToKurioBashi)+getString(R.string.R9T99905);
									else notes=getString(R.string.MatsubandaToShizenkan);
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
									else if (25<pointb&&pointb<30)
										notes=notes+getString(R.string.R10T102);
									//TODO change finish times for pointb.
									else if (pointb==68&&pointa<20)
										notes=notes+getString(R.string.R10T99906);
									else if (68<pointb&&pointb<73)
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
									if (68<=pointb&&pointb<=72)//TODO add Matsubanda Shizenkan (13:15) transfer to run11.
										notes=notes+getString(R.string.R11T201);
									if(67<pointa&&pointa<73)
										foundstop=0;
									break;
									//run 201 --> 200
									//run 999101 --> 999100
								case 12:
									notes=getString(R.string.ToIwasaki);
									if (pointb>99)
										notes=notes+getString(R.string.R12T13);
									else if (25<pointb&&pointb<30)
										notes=notes+getString(R.string.R12T103);
									else if (pointb==68){
										notes=notes+getString(R.string.R12T99907);//TODO pointa==68
										if (pointa>19)
											flags=12;//TODO set code for this flag.
									}
									if ((pointa>=99) | (pointa==68) | (26<=pointa&&pointa<=29) | (25<pointb&&pointb<30&&pointa>=20))
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
								case -1: notes=getString(R.string.backrun1result);break;
								case -2: notes=getString(R.string.backrun2result);break;
								case -99901: notes=getString(R.string.backrun999result);
								if ((pointb==26)|(pointb==29)) 
								{notes= notes + getString(R.string.backrun99901999101result);
								}
								if ((pointa==26)|(pointa==29)) foundstop=0;break;
								case -3: notes=getString(R.string.backrun3result);break;
								case -33000: notes=getString(R.string.backrun33000result);break;
								case -4: notes=getString(R.string.backrun4result);break;
								case -45: notes=getString(R.string.backrun45result);
								if ((pointb>19)|(pointa<20)) foundstop=0;
								flags=20;break;//This is the only bus that you might choose over the next one!
								case -99902: notes=getString(R.string.backrun999result);
								if (pointb<20) notes =notes+ getString(R.string.backrun99902cresult);
								if (pointa<21) foundstop=0; break;
								case -67: notes=getString(R.string.backrun67result);
								if ((pointb>19)|(pointa<35)|(pointa==68)) foundstop=0;
								if (pointb==68) notes=getString(R.string.backrun6799903result);break;
								case -99903: notes=getString(R.string.backrun999result); break;
								case -99904: notes=getString(R.string.backrun999result); 
								if (pointb<20) 
								{if (pointa==68) notes =notes+ getString(R.string.backrun99904cresult);
								else foundstop=0;//TODO add the option to transfer twice.
								}
								if (pointa<21) foundstop=0; break;
								case -910: notes=getString(R.string.backrun910result);
								if ((pointb>19)|(pointa<20)|(pointa==68)) foundstop=0;
								if (pointb==68) notes=getString(R.string.backrun91099905result);break;
								case -11: notes=getString(R.string.backrun3result);
								if (pointa==68) foundstop=0;
								if (pointb==68) notes=getString(R.string.backrun1199906result);break;
								case -99905: notes=getString(R.string.backrun999result); 
								if ((pointb==26)|(pointb==29)) {
									notes=getString(R.string.backrun99905999101result);
									if (pointa>19) foundstop=0;
								}
								if ((pointa==26)|(pointa==29)) foundstop=0;break;
								case -99906: notes=getString(R.string.backrun999result);
								if (pointb<20) notes =notes+ getString(R.string.backrun99906cresult);
								if (pointa<21) foundstop=0; break;
								case -14: notes=getString(R.string.backrun3result);
								if (pointa==68) foundstop=0;
								if (pointb==68) notes=getString(R.string.backrun1499907result);break;
								case -1314: notes=getString(R.string.backrun1314result);
								if ((pointb>19)|(pointa<100)) foundstop=0;break;
								case -99907: notes=getString(R.string.backrun999result); break;
								case -15: notes=getString(R.string.backrun4result);
								if (pointa==68) foundstop=0;
								if (pointb==68) notes=getString(R.string.backrun1599909result);break;
								case -99908: notes=getString(R.string.backrun999result); break;
								case -16: notes=getString(R.string.backrun3result);
								if (pointa==68) foundstop=0;
								if (pointb==68) notes=getString(R.string.backrun1699910result);break;
								case -1516: notes=getString(R.string.backrun1516result);
								if ((pointb>19)|(pointa<100)) foundstop=0;break;
								case -17: notes=getString(R.string.backrun17result);break;
								case -99909: notes=getString(R.string.backrun999result); break;
								case -99910: notes=getString(R.string.backrun999result); break;
								case -300: notes=getString(R.string.backrun300result);break;
								case -30517:
									if (pointb<20)
										notes=getString(R.string.backrun30517bresult);
									else
										notes=getString(R.string.backrun30517result);
									if ((pointb>67)|(pointa<69)) foundstop=0;break;
								case -300220910: 
									if (pointa==70)
									{
										notes=getString(R.string.backrun300result)+getString(R.string.backrun300220result);
										if (pointb<62)
										{
											notes=notes+getString(R.string.backrun2209result);
											if (pointb<20)
												notes=notes+getString(R.string.backrun220910result);
										}
									}
									else if (pointa>62)
									{
										notes=getString(R.string.backrun220result);
										if (pointb<62)
										{
											notes=notes+getString(R.string.backrun2209result);
											if (pointb<20)
												notes=notes+getString(R.string.backrun220910result);
										}
									}
									else
										foundstop=0;break;
								case -302301516: //TODO add stop 70 to matsubanda backruns
									if (pointa==70)
									{
										notes=getString(R.string.backrun300result)+getString(R.string.backrun302230result);
										if (pointb<20)
											notes=notes+getString(R.string.backrun23016result);
										else if (pointb<62)
											notes=notes+getString(R.string.backrun23015result);
									}
									else if (pointa>62)
									{
										notes=getString(R.string.backrun220result);
										if (pointb<20)
											notes=notes+getString(R.string.backrun23016result);
										else if (pointb<62)
											notes=notes+getString(R.string.backrun23015result);
									}
									else
										foundstop=0;break;
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
								if ((pointb>29)|(pointa<30)) foundstop=0; break;
								case -6107: notes=getString(R.string.backrun6107result);
								if ((pointb>29)|(pointa<30)) foundstop=0; break;
								case -9110: 
									if (pointa>99)
										notes=getString(R.string.backrun9110bresult);
									else
										notes=getString(R.string.backrun9110result);
									if ((pointb>29)|(pointa<30))  foundstop=0; break;
								case -13112: notes=getString(R.string.backrun13112result);
								if ((pointb>29)|(pointa<30))  foundstop=0; break;
								case -999100:  notes=getString(R.string.backrun999result); break;
								case -99910114:  notes=getString(R.string.backrun999result); 
								if (pointb<21) notes=notes+getString(R.string.backrun99910114result);
								if (pointa<21)foundstop=0;break;	

								}
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
