package com.teddy.data;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.drawable.GradientDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView.OnItemSelectedListener;
public class Stats extends Activity {
	ConnectionDetector cd;
	int year=0,month=0,day=0, display=0, first=0;
	static String textSize ="Medium";
	static String notif="On";
	static int textSizeInt=16;
	static String textColor="White";
	static String textBkcolor ="Grey";

	// Remember list of buildings and rooms associated
	Map<String, ArrayList<String>> buildingRoomDict;
	
	String buildingSelected, roomSelected, timeSelected2, requestUrl;
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        Bundle extras = getIntent().getExtras(); 
        if(extras !=null)
        {
        	notif = extras.getString("notifs");
        	textSize = extras.getString("text");
        	textColor = extras.getString("color");
        	textBkcolor = extras.getString("bkcolor");
        	if(textSize.equals("Small"))textSizeInt=14;
            else if(textSize.equals("Medium"))textSizeInt=16;
            else if(textSize.equals("Big"))textSizeInt=20;
            else if(textSize.equals("Extra Big"))textSizeInt=30;
        	
        }   
        
        // creating connection detector class instance
        cd = new ConnectionDetector(getApplicationContext());
        
        setContentView(R.layout.stats);

        // Hide input keyboard
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        //Buttons
                
        Button usage =(Button) findViewById(R.id.usagebutton);
        usage.setText("Available");
        
        final Button stats =(Button) findViewById(R.id.statsbutton);
        stats.setText("Statistics");
        
        Button power =(Button) findViewById(R.id.powerbutton);
        power.setText("Power Cost");
             
        ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////     
        //Spinners
        final Spinner build = (Spinner) findViewById(R.id.building_spinner);
        final Spinner room = (Spinner) findViewById(R.id.room_spinner);
        final Spinner time = (Spinner) findViewById(R.id.time_spinner);
        
        // Set resources
        ArrayAdapter<CharSequence> adapterbuild = new ArrayAdapter<CharSequence>(this, android.R.layout.simple_spinner_item);
        adapterbuild.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
 
        final ArrayAdapter<CharSequence> adapterroom = new ArrayAdapter<CharSequence>(this, android.R.layout.simple_spinner_item);
        adapterroom.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		final ArrayAdapter<CharSequence> adaptertime = ArrayAdapter.createFromResource(this, R.array.period_array, android.R.layout.simple_spinner_item);
        adaptertime.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
          
        
        if (cd.isConnectingToInternet()) {
        	
        	
        	buildingRoomDict = RequestHelper.getRoomsAndBuildings(Stats.this);

	        String[] buildings = new String[buildingRoomDict.keySet().size()];
	        buildingRoomDict.keySet().toArray(buildings);
	        
	        for(int i = 0; i < buildings.length; ++i) {
	        	
				String building = buildings[i];
				ArrayList<String> listOfRooms = buildingRoomDict.get(building);
				
				// Skip if no rooms
				if(listOfRooms == null || listOfRooms.size() <= 0)
					continue;
				
				// Otherwise add them to spinner
	            for(int j = 0; j < listOfRooms.size(); ++j) {
	            	String roomName = listOfRooms.get(j).toString();
	            	adapterroom.add(roomName);
	            }
			   
	            // Add the building too
	            adapterbuild.add(building);
	        }
	        
	        //Now use dictionary to populate the UI
	        build.setAdapter(adapterbuild);
	        buildingSelected =(String) (build.getItemAtPosition(0));
	        
	        // Room spinner adapter
	        room.setAdapter(adapterroom);
	        roomSelected =(String) (room.getItemAtPosition(0));
	        
	        // Room spinner adapter
	        time.setAdapter(adaptertime);
	        timeSelected2 =(String) (time.getItemAtPosition(0));
	         
	        // Add event handler to handle room selection
	        build.setOnItemSelectedListener(new OnItemSelectedListener() {
	        
	        	
	        	@Override
	        public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
	        	//TextView text2 = (TextView ) findViewById(R.id.usagetext);
	        		buildingSelected =(String) (build.getItemAtPosition(position));
	        	
	        	List<String> listOfRooms = buildingRoomDict.get(buildingSelected);
	        	
	        	// Update room spinner when building changes
	        	adapterroom.clear();
	        	for(String r : listOfRooms){
	        		adapterroom.add(r);
	        	}
	        	room.setAdapter(adapterroom);
	        	roomSelected =(String) (room.getItemAtPosition(0));
	        	time.setAdapter(adaptertime);
	        	timeSelected2 =(String) (time.getItemAtPosition(0));
	        }
	
	            @Override
	            public void onNothingSelected(AdapterView<?> parentView) {
	                // your code here
	            }
	
	        });

	        room.setOnItemSelectedListener(new OnItemSelectedListener() {
	            @Override
	            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
	            	
	            	// Get new selection
	            	roomSelected =(String) (room.getItemAtPosition(position));
	            	time.setAdapter(adaptertime);
		        	timeSelected2 =(String) (time.getItemAtPosition(0));
	            	
	            }

	            @Override
	            public void onNothingSelected(AdapterView<?> parentView) {
	                // your code here
	            }

	        });
	        
	        
	        time.setOnItemSelectedListener(new OnItemSelectedListener() {
	        
	        	
	        	@Override
	        	public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
	        	
	        		// Get new selection
	            	timeSelected2 =(String) (time.getItemAtPosition(position));
	            	
	            	//if(first==0){first=1;}           ///////calendar here  ************
	            	//else if(first==1)
	            	{
	            		//first=2;
	            		Calendar cal = Calendar.getInstance();
	            	   	year = cal.get(Calendar.YEAR);  
	            	   	month = cal.get(Calendar.MONTH)+1;
	            	   	day = cal.get(Calendar.DAY_OF_MONTH);
	            	}
	            	/*else if(first==2)
	            	{
	            		showDatePickerDialog(stats);
	            		first=1;
	            	    year=DatePickerFragment.selectedyear;
	            	    month=DatePickerFragment.selectedmonth+1;
	            	    day=DatePickerFragment.selectedday;
	            	}
	            	*/
	            	// Display available computers in this room:    ***************
	            //requestUrl="http://service-teddy2012.rhcloud.com/log/" + buildingSelected + "/" + roomSelected + "/" + year + "/" + month + "/" + day;
	            //display=1;
					if(timeSelected2.equals("Last Month")) {
						display=3;
	            		requestUrl = "http://service-teddy2012.rhcloud.com/log/" + buildingSelected + "/" + roomSelected + "/" + year + "/" + month;
					}
	            	else if(timeSelected2.equals("Last Year")) {
	            		display=4;
	            		requestUrl = "http://service-teddy2012.rhcloud.com/log/" + buildingSelected + "/" + roomSelected + "/" + year;
	            	}
	            	else if(timeSelected2.equals("Last Day")) {
	            		display=1;
	            		requestUrl = "http://service-teddy2012.rhcloud.com/log/" + buildingSelected + "/" + roomSelected + "/" + year + "/" + month + "/" + day;
	            	}
	            	else if(timeSelected2.equals("Last Week")) {
	            		display=2;
	            		requestUrl = "http://service-teddy2012.rhcloud.com/log/" + buildingSelected + "/" + roomSelected + "/" + year + "/" + month + "/" + day + "/w";
	            	}
					
	            	new GetStatsTask().execute(requestUrl);
	        	}
	
	            @Override
	            public void onNothingSelected(AdapterView<?> parentView) {
	                // your code here
	            }
	
	        });
	        
	        usage.setOnClickListener(new Button.OnClickListener(){
	        	public void onClick(View v){
		        	Intent i = new Intent(getApplicationContext(),  UsageScreen.class);
		        	i.putExtra("notifs", notif);
					i.putExtra("text", textSize);
					i.putExtra("color", textColor);
			    	i.putExtra("bkcolor",textBkcolor );
		        	finish();
		        	startActivity(i);
		        	overridePendingTransition(R.anim.push_right_in,R.anim.push_right_out);
	        	}
	        });
	              
	        power.setOnClickListener(new Button.OnClickListener(){
		        public void onClick(View v){
	            	Intent i = new Intent(getApplicationContext(), Power.class);
	            	i.putExtra("notifs", notif);
	    			i.putExtra("text", textSize);
					i.putExtra("color", textColor);
			    	i.putExtra("bkcolor",textBkcolor );
					finish();
	            	startActivity(i);	
		        	overridePendingTransition(R.anim.push_right_in,R.anim.push_right_out);
	
		    	}
	        });
	        
	        stats.setOnClickListener(new Button.OnClickListener(){
	        	public void onClick(View v){}
	        });
        
        }
        else {
        	System.out.println("No Internet Connection");
        }  
	}


	public void onClick(View v) {
		Intent i = new Intent(getApplicationContext(),  Info.class);
     	i.putExtra("URL", requestUrl);
     	i.putExtra("period", timeSelected2);
     	i.putExtra("room",roomSelected);
     	i.putExtra("building",buildingSelected);
     	startActivity(i);
    	overridePendingTransition(R.anim.fadein,R.anim.fadeout);
    } 
	
	@Override
    public void onBackPressed() {
     finish();
    }
	
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.usagemenu, menu);
	    return true;
	}
	
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent i;
		switch (item.getItemId()) {
			case R.id.video:
			i = new Intent(getApplicationContext(), About.class);
			startActivity(i);
        	overridePendingTransition(R.anim.fadein,R.anim.fadeout);
			return true;
			case R.id.settings:
			i = new Intent(getApplicationContext(), Settings.class);
			i.putExtra("notifs", notif);
			i.putExtra("text", textSize);
			i.putExtra("color", textColor);
	    	i.putExtra("bkcolor",textBkcolor );
            finish();
            startActivity(i);
	        overridePendingTransition(R.anim.fadein,R.anim.fadeout);
			return true;
		    case R.id.instructions:
		    i = new Intent(getApplicationContext(), Instr.class);
		    i.putExtra("text", textSize);
			i.putExtra("id", "1");
			i.putExtra("color", textColor);
	    	i.putExtra("bkcolor",textBkcolor );
			startActivity(i);
        	overridePendingTransition(R.anim.fadein,R.anim.fadeout);
		    return true;
		    case R.id.support:
		    i = new Intent(getApplicationContext(), Support.class);
			i.putExtra("text", textSize);
			i.putExtra("color", textColor);
	    	i.putExtra("bkcolor",textBkcolor );
			startActivity(i);
		    finish();
        	overridePendingTransition(R.anim.fadein,R.anim.fadeout);
		    return true;
		    default:
		    return super.onOptionsItemSelected(item);
		}
	}
	
	/*public void showDatePickerDialog(View v) {
	    DialogFragment newFragment = new DatePickerFragment();
	    newFragment.show(getFragmentManager(), "datePicker");
	}*/
	
	// Get power stats json asynchronously
	public class GetStatsTask extends AsyncTask<String, Void, JSONObject> {
		
				
		public double powerCost = 0, idleCost = 0;
		// Progress dialog
		ProgressDialog prog;
		    
		protected void onPreExecute() {
			prog = new ProgressDialog(Stats.this);
		    prog.setTitle("Waiting...");
		    prog.setMessage("Retrieving data from the service.");       
		    prog.setIndeterminate(false);
		    prog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		    prog.show();
		    
		}
		
		protected JSONObject doInBackground(String... urls) {
			JsonParser parser = new JsonParser();
			
			JSONObject jObject = null;
			try {
				jObject = parser.getJSONFromUrl(urls[0]);
			} catch (JSONException e) {
				// TODO DONE1 Auto-generated catch block
				
				e.printStackTrace();
				android.os.Process.killProcess(android.os.Process.myPid());

			}
			
			return jObject;
			
		}
		
	
	     protected void onPostExecute(JSONObject result) {
    	 	prog.hide();
 
			powerCost = 0; idleCost = 0;
			String start_date="null",end_date="null";
			String[] start={"null","null"}, end={"null","null"},start_format={"null","null","null"},end_format={"null","null","null"};
			
			
			try { 
//{"Total_Power_Cost": "302.13", "watts": "10358900", "watts_alive": "1726290", "end_date": "2013-03-18T00:00:00",
				//"power_cost_no_idle": "258.97", "start_date": "2013-03-17T00:00:00"}
				powerCost = result.getDouble("Total_Power_Cost");
				idleCost = result.getDouble("power_cost_no_idle");
				start_date = result.getString("start_date");
        		end_date = result.getString("end_date");
        		
        		start =start_date.split("T");
        		end = end_date.split("T");
        		start_format =start[0].split("-");
        		end_format =end[0].split("-");
				} catch (JSONException e) {
					// TODO DONE1 Catch exception here if JSON is not formulated well
					e.printStackTrace();
					android.os.Process.killProcess(android.os.Process.myPid());

				}
				catch (NullPointerException e) {
					e.printStackTrace();
					android.os.Process.killProcess(android.os.Process.myPid()); 

				}	
			
			TextView powerTextView = (TextView) findViewById(R.id.powertext);
			if(display==1)powerTextView.setText("Total power consumption costs on "+day+"/"+month+"/"+year+" from "+start[1]+" to "+end[1]+String.format(": %.2f GBP",powerCost));
			else if (display==2) powerTextView.setText("In week "+ (day+7)%7 +" of "+month+"/"+year+" from "+start_format[2]+"/"+start_format[1]+"/"+start_format[0]+", "+start[1]+" to "+end_format[2]+"/"+end_format[1]+"/"+end_format[0]+", "+end[1]+String.format(" the cost for total power consumption : %.2f GBP.",powerCost));
			else if (display==3) powerTextView.setText("In "+month+"/"+year+" from "+start_format[2]+"/"+start_format[1]+"/"+start_format[0]+", "+start[1]+" to "+end_format[2]+"/"+end_format[1]+"/"+end_format[0]+", "+end[1]+String.format(" the cost for total power consumption : %.2f GBP.",powerCost));
			else if (display==4) powerTextView.setText("In "+year+" from "+start_format[2]+"/"+start_format[1]+"/"+start_format[0]+", "+start[1]+" to "+end_format[2]+"/"+end_format[1]+"/"+end_format[0]+", "+end[1]+String.format(" the cost for total power consumption : %.2f GBP.",powerCost));
			powerTextView.setTextSize(textSizeInt);
			if(textColor.equals("White"))powerTextView.setTextColor(getResources().getColor(R.color.white));
            else if(textColor.equals("Black"))powerTextView.setTextColor(getResources().getColor(R.color.black));
            else if(textColor.equals("Red"))powerTextView.setTextColor(getResources().getColor(R.color.red));
            else if(textColor.equals("Blue"))powerTextView.setTextColor(getResources().getColor(R.color.blue));
            else if(textColor.equals("Green"))powerTextView.setTextColor(getResources().getColor(R.color.green));
            else if(textColor.equals("Yellow"))powerTextView.setTextColor(getResources().getColor(R.color.yellow));
            else if(textColor.equals("Orange"))powerTextView.setTextColor(getResources().getColor(R.color.orange));
            else if(textColor.equals("Grey"))powerTextView.setTextColor(getResources().getColor(R.color.grey));
			
			TextView idleTextView = (TextView) findViewById(R.id.idletext);
			idleTextView.setText(String.format("Possible savings: %.2f GBP",idleCost));
			idleTextView.setTextSize(textSizeInt);
			if(textColor.equals("White"))idleTextView.setTextColor(getResources().getColor(R.color.white));
            else if(textColor.equals("Black"))idleTextView.setTextColor(getResources().getColor(R.color.black));
            else if(textColor.equals("Red"))idleTextView.setTextColor(getResources().getColor(R.color.red));
            else if(textColor.equals("Blue"))idleTextView.setTextColor(getResources().getColor(R.color.blue));
            else if(textColor.equals("Green"))idleTextView.setTextColor(getResources().getColor(R.color.green));
            else if(textColor.equals("Yellow"))idleTextView.setTextColor(getResources().getColor(R.color.yellow));
            else if(textColor.equals("Orange"))idleTextView.setTextColor(getResources().getColor(R.color.orange));
            else if(textColor.equals("Grey"))idleTextView.setTextColor(getResources().getColor(R.color.grey));
			
			TextView infoTextView = (TextView) findViewById(R.id.infoText);
			infoTextView.setText("Did you know? You can click on the text to see the graph.");
			infoTextView.setTextSize(textSizeInt);
			if(textColor.equals("White"))infoTextView.setTextColor(getResources().getColor(R.color.white));
            else if(textColor.equals("Black"))infoTextView.setTextColor(getResources().getColor(R.color.black));
            else if(textColor.equals("Red"))infoTextView.setTextColor(getResources().getColor(R.color.red));
            else if(textColor.equals("Blue"))infoTextView.setTextColor(getResources().getColor(R.color.blue));
            else if(textColor.equals("Green"))infoTextView.setTextColor(getResources().getColor(R.color.green));
            else if(textColor.equals("Yellow"))infoTextView.setTextColor(getResources().getColor(R.color.yellow));
            else if(textColor.equals("Orange"))infoTextView.setTextColor(getResources().getColor(R.color.orange));
            else if(textColor.equals("Grey"))infoTextView.setTextColor(getResources().getColor(R.color.grey));
			
			
			int colors[] = null;
         	View mlayout= findViewById(R.id.mainlayout);
         	
        	if(textBkcolor.equals("White"))colors = new int[]{Color.rgb(255,255,255),Color.rgb(255,250,240)};
            else if(textBkcolor.equals("Black"))colors = new int[]{Color.rgb(69,69,69),Color.rgb(0,0,0)};
            else if(textBkcolor.equals("Red")) colors = new int[]{Color.rgb(238,48,48),Color.rgb(205,0,0)};
            else if(textBkcolor.equals("Blue")) colors = new int[]{Color.rgb(72,118,255),Color.rgb(39,64,139)};
            else if(textBkcolor.equals("Green")) colors = new int[]{Color.rgb(154,255,154),Color.rgb(0,139,69)};
            else if(textBkcolor.equals("Yellow")) colors = new int[]{Color.rgb(255,236,139),Color.rgb(238,173,14)};
            else if(textBkcolor.equals("Orange")) colors = new int[]{Color.rgb(255,165,75),Color.rgb(255,127,0)};
            else if(textBkcolor.equals("Grey"))  colors = new int[]{Color.rgb(123,123,123),Color.rgb(50,50,50)};
            
        	GradientDrawable grDr =new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, colors);
            grDr.setCornerRadius(5);
            grDr.setGradientCenter((float) 0.5,0.5f);
            grDr.setGradientRadius(300);
            grDr.setGradientType(GradientDrawable.RADIAL_GRADIENT);
            //grDr.setStroke(1,Color.WHITE);
            mlayout.setBackgroundDrawable(grDr);
            Window window = getWindow();
            window.setFormat(PixelFormat.RGBA_8888);
        
	     }
	     
	     
	}
}
