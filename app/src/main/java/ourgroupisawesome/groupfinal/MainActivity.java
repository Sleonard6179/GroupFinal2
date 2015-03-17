package ourgroupisawesome.groupfinal;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;

import org.w3c.dom.Text;

import java.net.UnknownHostException;
import java.sql.Time;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends Activity
        implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    protected static final String TAG = "My freaking App";
    protected GoogleApiClient mGoogleApiClient;
    protected Location mLastLocation;
    protected TextView mLatitudeText;
    protected TextView mLongitudeText;
    private TextView textView;
    public TextView textViewHide;
    public String pName3;
    public String pName4;
    public TextView Gstatus;
    public String Answer;
    public String HideTime;
    public double distance;
    public String HideLat;
    public String HideLon;
    public String SeekLat;
    public String SeekLon;
    public String prevSeek;
    public String playerName;
    public static double distanceHotCold;
    public String timeH;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button Seekbtn = (Button) findViewById(R.id.seekButton);
        Seekbtn.setEnabled(true);
        Button Hidebtn = (Button) findViewById(R.id.hideButton);
        Hidebtn.setEnabled(true);
        mLatitudeText = (TextView) findViewById((R.id.mLatitudeText));
        mLongitudeText = (TextView) findViewById((R.id.mLongitudeText));
        textView = (TextView) findViewById(R.id.textView3);
        textViewHide = (TextView) findViewById(R.id.textView4);
        pName3 = PreferenceManager.getDefaultSharedPreferences(getBaseContext()).getString("name", "defaultStringIfNothingFound");
        pName4 = String.valueOf(pName3);
        Gstatus = (TextView) findViewById(R.id.textView2);
        buildGoogleApiClient();
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("   Welcome to: " +
                " \n !...The Game...!");
        alert.setMessage("Enter Player Name");
        // Set an EditText view to get user input
        final EditText input = new EditText(this);
        alert.setView(input);
        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                Editable pName = input.getText();
                playerName = String.valueOf(pName);
                Checkstatus checkstatus = new Checkstatus();
                checkstatus.execute();}});
        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                Checkstatus checkstatus = new Checkstatus();
                checkstatus.execute();}});alert.show(); }

    public class Checkstatus extends AsyncTask<Void, Void, String> {
        public String doInBackground(Void... voids) {
            try {
                MongoClientURI uri = new MongoClientURI("mongodb://TGIS504GroupProj:h!d3andseek@ds039010.mongolab.com:39010/tgis504");
                MongoClient client = new MongoClient(uri);
                DB db = client.getDB(uri.getDatabase());
                DBCollection HideAndSeek = db.getCollection("HideAndSeek");
                long nll = HideAndSeek.count();
                if (nll != 0) {
                    client.close();
                    return "Go Seek";
                } else if (nll == 0) {
                    return "Winner";
                } else
                   return "Go Hide";

            } catch (UnknownHostException e) {
                return "it didn't work";
            }
        }
        @Override
        protected void onPostExecute(String result) {
            Gstatus.setText(result);
        }
    }

    public class LastWinner extends AsyncTask<Void, Text, String> {
        public String doInBackground(Void...voids ) {
            try {
                MongoClientURI uri = new MongoClientURI("mongodb://TGIS504GroupProj:h!d3andseek@ds039010.mongolab.com:39010/tgis504");
                MongoClient client = new MongoClient(uri);
                DB db = client.getDB(uri.getDatabase());
                DBCollection HideAndSeek = db.getCollection("Winners");
                DBCursor winner = HideAndSeek.find().sort(new BasicDBObject("$natural",-1));
                Answer = String.valueOf(winner.one().get("lastwinner"));
                HideTime = String.valueOf(winner.one().get("hidetime"));
                long HaS = HideAndSeek.count();
                if (HaS != 0 ) {
                    client.close();
                    return String.valueOf(Answer) + " Won the last game on " + HideTime + " Hurry and Try to Hide!";
                } else {
                    return "No winners yet";
                }
            } catch (UnknownHostException e) {
                return "it didn't work";}}
        @Override
        protected void onPostExecute(String result) {
            textView.setText(result);}}

    public void Seek(View view) {
        Button Hidebtn = (Button) findViewById(R.id.hideButton);
        Hidebtn.setEnabled(false);
        checkDistance checkDistance = new checkDistance();
        checkDistance.execute();}


    public void Hide(View view) {
        Button Hidebtn = (Button) findViewById(R.id.hideButton);
        Hidebtn.setEnabled(false);
        Button Seekbtn = (Button) findViewById(R.id.seekButton);
        Seekbtn.setEnabled(true);
        Checkstatus checkstatus = new Checkstatus();
        checkstatus.execute();
        PostLocation postlocation = new PostLocation();
        postlocation.execute();
    }

    public void lastWinner (View view) {
        LastWinner lastwinner = new LastWinner();
        lastwinner.execute();
    }

    private class checkDistance extends AsyncTask<Void, Void, String> {
        protected String doInBackground(Void... voids) {
            try {
                MongoClientURI uri = new MongoClientURI("mongodb://TGIS504GroupProj:h!d3andseek@ds039010.mongolab.com:39010/tgis504");
                MongoClient client = new MongoClient(uri);
                DB db = client.getDB(uri.getDatabase());
                DBCollection HideAndSeek = db.getCollection("HideAndSeek");
                long mll = HideAndSeek.count();

                //Check if game is on
                if (mll == 0) {

                    // Message if there is no hider
                    return "It's a new game! Hurry up and hide!";

                }

                else if ( mll == 1) {

                    //get hiding lat/long and assign variables
                    DBObject hideentry = HideAndSeek.findOne();
                    HideLat = String.valueOf(hideentry.get("dbHideLat"));
                    HideLon = String.valueOf(hideentry.get("dbHideLong"));


                    Location hideloc = new Location(" ");
                    hideloc.setLatitude(Double.valueOf(HideLat));
                    hideloc.setLongitude(Double.valueOf(HideLon));

                    //test distance for winning
                    distance = hideloc.distanceTo(mLastLocation);
                    //greater than 25 ft; using 7.62 because distanceTo() returns in meters
                    if (distance <= 7.62) {
                        /////clear entries for database

                        HideAndSeek.remove(new BasicDBObject());

                        //open Winners collection
                        DBCollection Winners = db.getCollection("Winners");

                        //build and insert Winner entry
                        DBObject winner = new BasicDBObject("lastwinner", String.valueOf(playerName))
                                .append("hidetime", timeH);
                        Winners.insert(winner);

                        //close client
                        client.close();

                        //Winning Message
                        return "You've won! Hurry up and hide before someone else!";
                    } else {

                        //insert first guess
                        BasicDBObject newSeek = new BasicDBObject("name", String.valueOf(playerName))
                                .append("dbSeekLat", Double.valueOf(mLastLocation.getLatitude()))
                                .append("dbSeekLong", Double.valueOf(mLastLocation.getLongitude()));

                        HideAndSeek.insert(newSeek);
                        client.close();
                        return "You have guessed first so we can't tell if you are colder or hotter.";
                    }
                }

                else {

                    //get hiding lat/long and assign variables
                    DBObject hideentry = HideAndSeek.findOne();
                    HideLat = String.valueOf(hideentry.get("dbHideLat"));
                    HideLon = String.valueOf(hideentry.get("dbHideLong"));


                    Location hideloc = new Location(" ");
                    hideloc.setLatitude(Double.valueOf(HideLat));
                    hideloc.setLongitude(Double.valueOf(HideLon));

                    //test distance for winning
                    distance = hideloc.distanceTo(mLastLocation);
                    //greater than 25 ft; using 7.62 because distanceTo() returns in meters
                    if (distance <= 7.62) {
                        /////clear entries for database

                        HideAndSeek.remove(new BasicDBObject());

                        //open Winners collection
                        DBCollection Winners = db.getCollection("Winners");

                        //build and insert Winner entry
                        DBObject winner = new BasicDBObject("lastwinner", String.valueOf(playerName));
                        Winners.insert(winner);

                        //close client
                        client.close();

                        //Winning Message
                        return "You've won! Hurry up and hide before someone else!";
                    } else {
                        //set cursor at latest entry
                        DBCursor cursor = HideAndSeek.find().skip((int) mll - 1);
                       /* DBCursor cursor = HideAndSeek.find().sort(new BasicDBObject("$natural", -1));*/
                        DBObject entry = cursor.next();
                        /*DBObject entry = cursor.one();*/

                        SeekLat = String.valueOf(entry.get("dbSeekLat"));
                        SeekLon = String.valueOf(entry.get("dbSeekLong"));
                        prevSeek = String.valueOf(entry.get("name"));}

                    if (prevSeek.equals(playerName)) {
                        prevSeek = "you" ;
                    }

                    Location lastSeekLocation = new Location(" ");
                    lastSeekLocation.setLatitude(Double.valueOf(SeekLat));
                    lastSeekLocation.setLongitude(Double.valueOf(SeekLon));

                    Location myCurrentLocation = new Location("start");
                    myCurrentLocation.setLatitude(mLastLocation.getLatitude());
                    myCurrentLocation.setLongitude(mLastLocation.getLongitude());

                    distanceHotCold = lastSeekLocation.distanceTo(myCurrentLocation);
                    int dhcfeet = (int)Math.round(((distanceHotCold)*3.28));
                    int dmainfeet = (int)Math.round(((distance)*3.28));

                    //build db Object for insertion
                    BasicDBObject newSeek = new BasicDBObject("name", playerName)
                            .append("distanceHotCold", Double.valueOf(distanceHotCold))
                            .append("distance", Double.valueOf(distance))
                            .append("dbSeekLat", Double.valueOf(mLastLocation.getLatitude()))
                            .append("dbSeekLong", Double.valueOf(mLastLocation.getLongitude()));

                    HideAndSeek.insert(newSeek);
                    client.close();

                    //hotter/colder message
                    if (distanceHotCold < distance) {
                        return "Good guess! You are closer than the last guess made by " + prevSeek + ".\n" +
                                "You were " + dhcfeet + " feet from last guessed location and " + dmainfeet + " feet away from hider!";
                    } else {
                        return "Sorry, but you are further away than the last guess by " + dhcfeet + " feet and "  + dmainfeet + " away from the hider!";
                    }

                }



            } catch (UnknownHostException e) {
                return "No body's home brah!";
            }

        }


        @Override
        protected void onPostExecute(String result) {
            textViewHide.setText(result);
        }
    }
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        if (mLastLocation != null) {
            mLatitudeText.setText(String.valueOf(" "  + mLastLocation.getLatitude()));
            mLongitudeText.setText(String.valueOf(" " + mLastLocation.getLongitude()));
        } else {
            Toast.makeText(this, R.string.no_location_detected, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onConnectionSuspended(int cause) {
        Log.i(TAG, "Connection suspended");
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = " + result.getErrorCode());
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public class PostLocation extends AsyncTask<Void, Void, String> {
        public String doInBackground(Void... voids) {
            try {
                MongoClientURI uri = new MongoClientURI("mongodb://TGIS504GroupProj:h!d3andseek@ds039010.mongolab.com:39010/tgis504");
                MongoClient client = new MongoClient(uri);
                DB db = client.getDB(uri.getDatabase());
                DBCollection HideAndSeek = db.getCollection("HideAndSeek");
                DBCursor hidecursor = HideAndSeek.find(new BasicDBObject());
                long hidecurs = hidecursor.count();
                SimpleDateFormat hideTime = new SimpleDateFormat(" EEE MMM dd ' at approximately ' hh:mm a ");
                String timeH = hideTime.format(new Date());

                {
                if (mLastLocation != null) {
                    BasicDBObject LastLocation = new BasicDBObject();
                    LastLocation.put("dbHideLat", String.valueOf(mLastLocation.getLatitude()));
                    LastLocation.put("dbHideLong", String.valueOf(mLastLocation.getLongitude()));
                    LastLocation.put("Time", String.valueOf(timeH));
                    /*LastLocation.put("Time", String.valueOf(now));*/

                        if (hidecurs == 0) {
                            LastLocation.put("name", playerName);
                            HideAndSeek.insert(LastLocation);
                            client.close();
                            Checkstatus checkstatus = new Checkstatus();
                            checkstatus.execute();

                            return playerName + " just hid On " + LastLocation.put("Time", String.valueOf(timeH)) + "," + "\n" + "Go find them! ";

                        } else {
                            client.close();
                            Checkstatus checkstatus = new Checkstatus();
                            checkstatus.execute();
                            return "A game is already started. Go find them!";}
                } else {
                    client.close();
                    return "You have no XY turn your location on Brah!";
                }}


            } catch (UnknownHostException e) {
                return "No body's home brah!";
            }/*return null;*/
        }

        @Override
        protected void onPostExecute(String result) {
            textViewHide.setText(result);
        }

    }
}

