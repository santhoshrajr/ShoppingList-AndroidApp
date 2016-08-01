package my.santh.shopping.shoppinglist;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.hardware.SensorManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.speech.RecognizerIntent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.Pair;
import android.view.MotionEvent;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import android.hardware.SensorEventListener;
import android.hardware.Sensor;
import android.hardware.SensorEvent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Begin of class MainActivty where List names and its corresponding features are shown.
 * Functionalities include adding ,editing and deleting List.
 */
public class MainActivity extends AppCompatActivity implements SensorEventListener, SwipeRefreshLayout.OnRefreshListener {

    /**
     *Creating Instances and Initializing static variables
     */

    EditText itemName;
    TextView listName;
    private SensorManager senSensorManager;
    private Sensor senAccelerometer;
    //DBHelper.FeedEntry.FeedReaderDbHelper mDbHelper;
    ListView itemList;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> aList;
    private ArrayList<Integer> pkID;
    private  ArrayAdapter<Integer> pkAdapter;
    private long lastUpdate = 0;
    private float last_x, last_y, last_z;
    private static final int SHAKE_THRESHOLD = 600;
    int check;
    public AlertDialog myAlertDialog;
    public AlertDialog my2AlertDialog;
    protected static final int RESULT_SPEECH = 1;
    SwipeRefreshLayout swipeLayout;

    ImageButton butttonvoice;

    /**
     * onCreate Method in Main Activity
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        /**Set Actionbar
         *
         */
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        /**create Instance for Sensor and SensorManager
         *
         */
        senSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        senAccelerometer = senSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        senSensorManager.registerListener(this, senAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        swipeLayout = (SwipeRefreshLayout) findViewById(R.id.swiperefresh);
        swipeLayout.setOnRefreshListener(this);
        itemList = (ListView) findViewById(R.id.listView);
        PreferenceManager.setDefaultValues(this, R.xml.prefernceslist, false);
        PreferenceManager.setDefaultValues(this,R.xml.preferncesitem,false);

        ///Create an object instance for DB Helper Class
        //dbm = new DBHelper.FeedEntry.FeedReaderDbHelper(this);




        displayData();
        
        ///Set Scrollview for Listview
        itemList.setOnTouchListener(new View.OnTouchListener() {
            // Setting on Touch Listener for handling the touch inside ScrollView
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // Disallow the touch request for parent scroll on touch of child view
                v.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });

        itemList.setOnItemClickListener(new android.widget.AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                newListAdapter adapter = (newListAdapter) parent.getAdapter();
                final Cursor cur = (Cursor) adapter.getItem(position);
                cur.moveToPosition(position);
                final int id1 = cur.getInt(cur.getColumnIndexOrThrow("_id"));
                final String ListName = cur.getString(cur.getColumnIndexOrThrow("listname"));
                // newListAdapter adapter = (newListAdapter)parent.getAdapter();
                //  Pair<Integer, String> pair = (Pair<Integer, String>) adapter.getItem(position);
                // Log.e("the information you want","id:"+ pair.first + " name" + pair.second);
                Intent item = new Intent(getApplicationContext(), ListActivity.class);
                //  int value=pair.first;
                item.putExtra("id", id1);
                item.putExtra("listname", ListName);
                item.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(item);


            }
        });

        itemList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener()

        {
            @Override
            public boolean onItemLongClick(AdapterView<?> av, View v, int position, long id) {
                // TODO: Get the position of the item clicked.
                //       Delete it from your collection eg.ArrayList.
                //       Call notifydatasetChanged so that it will refresh
                //       the views displaying updated list.


                newListAdapter adapter = (newListAdapter) av.getAdapter();
                final Cursor cur = (Cursor) adapter.getItem(position);
                cur.moveToPosition(position);
                final int id1 = cur.getInt(cur.getColumnIndexOrThrow("_id"));
                final String ListName = cur.getString(cur.getColumnIndexOrThrow("listname"));

                PopupMenu popupMenu = new PopupMenu(getApplicationContext(), v);
                popupMenu.getMenuInflater()
                        .inflate(R.menu.pop_menu, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

                    @Override
                    public boolean onMenuItemClick(MenuItem item) {

                        switch (item.getItemId()) {

                            case R.id.pop_edit:
                                final AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
                                LinearLayout layout = new LinearLayout(MainActivity.this);
                                layout.setOrientation(LinearLayout.VERTICAL);
                                String winner;

                                alert.setTitle("Rename List ");
                                final TextView descriptionBox = new TextView(MainActivity.this);
                                // final TextView descriptionBox = new Textview(game.this);
                                descriptionBox.setTextSize(20);
                                descriptionBox.setHint("Enter New List Name");
                                descriptionBox.setSingleLine();
                                layout.addView(descriptionBox);


                                final EditText titleBox = new EditText(MainActivity.this);
                                titleBox.setHint("List Name ");
                                layout.addView(titleBox);

                                alert.setView(layout);

                                alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog, int whichButton) {
                                         String Name = titleBox.getText().toString();
                                        if(Name.contains("'"))
                                        {
                                            Name=Name.replaceAll("[']","\'");
                                        }
                                        DataBaseManager dbm =new DataBaseManager(getApplicationContext());
                                        dbm.open();
                                        boolean checkdataItem = dbm.checkListdata(Name);
                                       // dbm.close();
                                        if (checkdataItem == false) {

                                            if (Name.length() > 0) {
                                                dbm.updateListdata(Name, ListName);
                                                dbm.close();
                                                Toast.makeText(MainActivity.this, "Updated List Name " + titleBox.getText().toString(),
                                                        Toast.LENGTH_SHORT).show();
                                                displayData();

                                                // adapter.notifyDataSetChanged();
                                            } else {
                                                dbm.close();
                                                Toast.makeText(MainActivity.this, "List Name Cannot be blank",
                                                        Toast.LENGTH_SHORT).show();

                                            }
                                        } else {
                                            dbm.close();
                                            Toast.makeText(MainActivity.this, "List Name Already exists",
                                                    Toast.LENGTH_SHORT).show();
                                        }


                                    }

                                });
                                alert.setNegativeButton("Cancel", null);


                                alert.create().show();


                                return true;
                            case R.id.pop_delete:

                                final AlertDialog.Builder myQuittingDialogBox =new AlertDialog.Builder(MainActivity.this);
                                LinearLayout layout1 = new LinearLayout(MainActivity.this);
                                layout1.setOrientation(LinearLayout.VERTICAL);
                                //set message, title, and icon
                                myQuittingDialogBox.setTitle("Delete");
                                final TextView descriptionBox1 = new TextView(MainActivity.this);
                                // final TextView descriptionBox = new Textview(game.this);
                                descriptionBox1.setTextSize(20);
                                descriptionBox1.setHint("Do you want to Delete ?");
                                layout1.addView(descriptionBox1);

                                myQuittingDialogBox.setView(layout1);

                                myQuittingDialogBox .setPositiveButton("Delete", new DialogInterface.OnClickListener() {

                                    public void onClick(DialogInterface dialog, int whichButton) {
                                        //your deleting code
                                        int y=id1;
                                        DataBaseManager dbm =new DataBaseManager(getApplicationContext());
                                        dbm.open();
                                        dbm.delete_ListbyID(id1);
                                        dbm.close();
                                        Toast.makeText(MainActivity.this, "Deleted List " + ListName,
                                                Toast.LENGTH_SHORT).show();
                                        
                                        displayData();
                                    }

                                });



                                myQuittingDialogBox .setNegativeButton("cancel", null);
                                myQuittingDialogBox .create().show();
                                return true;



                        }

                        return true;
                    }
                });
                popupMenu.show();


                return true;
            }
        });








    }




    /**
     * Sensor Changed Event to detect Shake of Device .
     * @param event
     */
    public void onSensorChanged(SensorEvent event) {
        Sensor mySensor = event.sensor;

        if (mySensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];

            long curTime = System.currentTimeMillis();

            if ((curTime - lastUpdate) > 100) {
                long diffTime = (curTime - lastUpdate);
                lastUpdate = curTime;

                float speed = Math.abs(x + y + z - last_x - last_y - last_z)/ diffTime * 10000;

                if (speed > SHAKE_THRESHOLD  ) {

                    Deletedialog();

                }

                last_x = x;
                last_y = y;
                last_z = z;

            }
            }
    }
    /**
     * Delete Dialog Method for alert box on selecting delete items .
     */

    public void Deletedialog()
    {
        if( myAlertDialog != null && myAlertDialog.isShowing()  ) return;

        final AlertDialog.Builder myQuittingDialogBox =new AlertDialog.Builder(this);
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        //set message, title, and icon
        myQuittingDialogBox.setTitle("Delete");
        final TextView descriptionBox = new TextView(this);
        // final TextView descriptionBox = new Textview(game.this);
        descriptionBox.setTextSize(20);
        descriptionBox.setHint("Do you  want to remove all the Lists ?");
        layout.addView(descriptionBox);

        myQuittingDialogBox.setView(layout);

        myQuittingDialogBox.setPositiveButton("Delete", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int whichButton) {
                DataBaseManager dbm =new DataBaseManager(getApplicationContext());
                dbm.open();
                //your deleting code
              int count=  dbm.deleteAll();
                dbm.close();
                if(count>0)
                Toast.makeText(getApplicationContext(), "All Lists are  Deleted",
                        Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(getApplicationContext(), "No Lists to Delete",
                            Toast.LENGTH_SHORT).show();
                displayData();
            }

        });


        myQuittingDialogBox .setNegativeButton("Cancel", null);
        myAlertDialog= myQuittingDialogBox .create();
        myAlertDialog.show();
    }


    /**
     * Implemeting OnSenorChange Method
     * @param sensorEvent
     */
    public void onSensorChange(SensorEvent sensorEvent) {

    }

    /**
     * Implementing onAccuracyChanged method
     * @param sensor
     * @param accuracy
     */

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    /**
     * onCreateOptionsMenu Method .
     * Inflate the menu; this adds items to the action bar if it is present.
     * @param menu
     * @return
     */

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    /**
     * onOptionsItemSelected Method.
     * Handle action bar item clicks here. The action bar will
     * automatically handle clicks on the Home/Up button, so long
     * as you specify a parent activity in AndroidManifest.xml.
     * @param item
     * @return
     */

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        check=0;


         if(id==R.id.info)
        {
            Intent intent = getIntent();
            Intent infoIntent = new Intent(this, InfoActivity.class);
            infoIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);

            infoIntent.putExtra("List", "main");
            startActivity(infoIntent);
        }
        else if(id==R.id.Delete)

        {


            Deletedialog();


        }

        else if(id==R.id.Settings)
        {
            Intent intent = getIntent();
            //String message = intent.getStringExtra(InitialActivity.EXTRA_MESSAGE);

            Intent settingsIntent = new Intent(this, SettingsListActivity.class);
            settingsIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);

            settingsIntent.putExtra("List", "main");
            startActivity(settingsIntent);
        }




        return super.onOptionsItemSelected(item);
    }

    /**
     * onPause method to pause the sensorManager
     */

    protected void onPause() {
        super.onPause();
        senSensorManager.unregisterListener(this);

    }

    /**
     * onDestroy Method .
     */

    protected void onDestroy() {
        super.onDestroy();

    }

    /**
     * onResume Method for resuming the sensor
     */
    protected void onResume() {
        super.onResume();

        senSensorManager.registerListener(this, senAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);


    }

    /**
     * AddItem Method to Add Item to list
     * @param view
     */

    public void AddItem(View view) {
        itemName = (EditText) findViewById(R.id.editTextItem);

         String Name = itemName.getText().toString();
        if(Name.contains("'"))
        {
            Name=Name.replaceAll("[']","\'");
        }
        if(Name!=null) {
            if (Name.length() > 0) {
                //My code, if the EditText is not empty
                DataBaseManager dbm =new DataBaseManager(getApplicationContext());
                dbm.open();
                boolean checkdataItem=dbm.checkListdata(Name);
                dbm.close();
                if(checkdataItem==false) {
                    DataBaseManager dbm1 =new DataBaseManager(getApplicationContext());
                    dbm1.open();
                    boolean dataItemInserted = dbm1.insertDataList(Name);
                    dbm1.close();
                    if (dataItemInserted == true) {

                        Toast.makeText(getApplicationContext(), "Added New List :" + Name,
                                Toast.LENGTH_SHORT).show();
                        itemName.setText("");
                        itemName.setHint("Shopping List Name");
                        
                        // mDbHelper.updatedata(Name, list.get(position));
                        displayData();
                    }
                }
                else
                {
                    Toast.makeText(getApplicationContext(), "List Name Alerady exists",
                            Toast.LENGTH_SHORT).show();
                    itemName.setText("");
                    itemName.setHint("New Item");
                }

            }


            else {
                itemName.requestFocus();
                itemName.setError("FIELD CANNOT BE EMPTY");

            }


        }

        }

    /**
     *onVoice Method for Google Sppech to text COnvertor.
     * @param v
     */
    public void onVoice(View v)
    {

        Intent i = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);

        i.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
          i.putExtra(RecognizerIntent.EXTRA_PROMPT, "Please Start Speaking");
        // i.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS,1);
        //i.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.ENGLISH);
        startActivityForResult(i, RESULT_SPEECH);

    }

    /**
     * onActivityResult method to retrieve ItemName from Google voice.
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        switch (requestCode) {
            case RESULT_SPEECH: {
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> Results;
                    Results = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    String text = Results.get(0);
                    itemName = (EditText) findViewById(R.id.editTextItem);
                    itemName.setText(text);


                }


            }

        }

    }


    public void SyncItem(View v)
    {
        DataBaseManager dbm = new DataBaseManager(getApplicationContext());
        dbm.open();
        String msg= dbm.getSyncStatus();
        dbm.close();
        Toast.makeText(MainActivity.this, msg,
                Toast.LENGTH_SHORT).show();
        if (msg.equals("DB Sync neededn"))
            new NewSyncItem().execute();

    }

    Syncexternal extobj=new Syncexternal();

    @Override
    public void onRefresh() {

        swipeLayout.post(new Runnable() {
            @Override
            public void run() {
                new Syncexternal().execute();


               // swipeLayout.setRefreshing(true);


                //swipeLayout.setRefreshing(false);
            }
        });


        //;

    }


    class Syncexternal extends AsyncTask<String,String,String>
    {
        protected void OnPreExecute()
        {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params)
        {
            try{
                externalData();
            } catch (Exception e){
                Log.d("main", "main crash");
                e.printStackTrace();

            }
            return null;
        }

        protected void onPostExecute(String str)
        {
            //swipeLayout.setRefreshing(false);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    {
                        displayData();
                    }
                }
            });
        }

    }

    private void updateInternal(){
        StaticData staticData = StaticData.getInstance();
        if(staticData.isSynced() != 7){
            //Log.d("database", "Not all synced " + staticData.isSynced());
            done();
            return;
        }
        //Log.d("database", "Starting internal sync");

        DataBaseManager dbm = new DataBaseManager(this);
        dbm.open();
        //dbm.reset();


        for (int i=0; i < staticData.lists.length(); i++){
            String array[] = new String[3];
            String id;
            try {
                JSONObject oneObject = staticData.lists.getJSONObject(i);
                //Log.d("mine", "location "+oneObject.getString("name"));
                // Pulling items from the array
                id = oneObject.getString("listid");
                array[0] = oneObject.getString("listname");
                array[1] = oneObject.getString("date");
                array[2] = oneObject.getString("deleted");

                dbm.addList(id, array);
                //  dbm.endTransaction();
            } catch (JSONException e) {
                Log.d("database", "Parse error on internal creation");
            }
        }
        //Log.d("database", "Synced Lists");

        for (int i=0; i < staticData.units.length(); i++){
            String array[] = new String[1];
            String id;
            try {
                JSONObject oneObject = staticData.units.getJSONObject(i);
                // Pulling items from the array
                id = oneObject.getString("unitid");
                array[0] = oneObject.getString("unitname");
                //  array[1] = oneObject.getString("plant");
                //array[2] = oneObject.getString("deleted");
                dbm.addunits(id, array);
                //  dbm.endTransaction();
            } catch (JSONException e) {
                Log.d("database", "Parse error on internal creation");
            }
        }
//        Log.d("database", "Synced Units");
        for (int i=0; i < staticData.catgeories.length(); i++){
            String array[] = new String[1];
            String id;
            try {
                JSONObject oneObject = staticData.catgeories.getJSONObject(i);
                // Pulling items from the array
                id = oneObject.getString("categoryid");
                array[0] = oneObject.getString("categoryname");
                dbm.addcategories(id, array);
                // dbm.endTransaction();
            } catch (JSONException e) {
                Log.d("database", "Parse error on internal creation");
            }
        }
  //      Log.d("database", "Synced categories");

        for (int i=0; i < staticData.Items.length(); i++){
            String array[] = new String[9];
            String id;
            try {
                JSONObject oneObject = staticData.Items.getJSONObject(i);
                // Pulling items from the array
                id = oneObject.getString("itemid");
                array[0] = oneObject.getString("itemname");
                array[1] = oneObject.getString("price");
                array[2] = oneObject.getString("quantity");
                array[3] = oneObject.getString("comments");
                array[4] = oneObject.getString("deleted");
                array[5] = oneObject.getString("purchased");
                array[6] = oneObject.getString("listid");

                //array[7] = oneObject.getString("deleted");
                array[7]=oneObject.getString("unitid");
                array[8]=oneObject.getString("categoryid");
                dbm.addItems(id, array);
                //  dbm.endTransaction();
            } catch (JSONException e) {
                Log.d("database", "Parse error on internal creation");
            }
        }
//        Log.d("database", "Synced Items");

        for (int i=0; i < staticData.deleteid.length(); i++){
            // String array[] = new String[1];
            String id;
            try {
                JSONObject oneObject = staticData.deleteid.getJSONObject(i);
                // Pulling items from the array
                id = oneObject.getString("listid");
                //array[0] = oneObject.getString("name");
                dbm.deletelistid(id);
                // dbm.endTransaction();
            } catch (JSONException e) {
                Log.d("database", "Parse error on internal creation");
            }
        }

//        Log.d("database", "Deleted lists");

        for (int i=0; i < staticData.deleteunit.length(); i++){
            // String array[] = new String[1];
            String id;
            try {
                JSONObject oneObject = staticData.deleteunit.getJSONObject(i);
                // Pulling items from the array
                id = oneObject.getString("unitid");
                //array[0] = oneObject.getString("name");
                dbm.deleteunitid(id);
                // dbm.endTransaction();
            } catch (JSONException e) {
                Log.d("database", "Parse error on internal creation");
            }
        }

//        Log.d("database", "Deleted units");

        for (int i=0; i < staticData.deeletecategory.length(); i++){
            // String array[] = new String[1];
            String id;
            try {
                JSONObject oneObject = staticData.deeletecategory.getJSONObject(i);
                // Pulling items from the array
                id = oneObject.getString("categoryid");
                //array[0] = oneObject.getString("name");
                dbm.deletecategoryid(id);
                // dbm.endTransaction();
            } catch (JSONException e) {
                Log.d("database", "Parse error on internal creation");
            }
        }

//        Log.d("database", "Deleted categories");


        /*for (int i=0; i < staticData.geni.length(); i++){
            String array[] = new String[1];
            String id;
            try {
                JSONObject oneObject = staticData.geni.getJSONObject(i);
                // Pulling items from the array
                id = oneObject.getString("id");
                array[0] = oneObject.getString("name");
                dbm.addGenus(id, array);
                //dbm.endTransaction();
            } catch (JSONException e) {
                Log.d("database", "Parse error on internal creation");
            }
        }

        for (int i=0; i < staticData.cultivar.length(); i++){
            String array[] = new String[1];
            String id;
            try {
                JSONObject oneObject = staticData.cultivar.getJSONObject(i);
                // Pulling items from the array
                id = oneObject.getString("id");
                array[0] = oneObject.getString("name");
                dbm.addCultivar(id, array);
                //dbm.endTransaction();
            } catch (JSONException e) {
                Log.d("database", "Parse error on internal creation");
            }
        }
        Log.d("database", "Synced Misc");
        for (int i=0; i < staticData.beacons.length(); i++){
            String array[] = new String[5];
            String id;
            try {
                JSONObject oneObject = staticData.beacons.getJSONObject(i);
                // Pulling items from the array
                id = oneObject.getString("id");
                array[0] = oneObject.getString("name");
                array[1] = oneObject.getString("mac");
                array[2] = oneObject.getString("descr");
                array[3] = oneObject.getString("deleted");

                dbm.addBeacon(id, array);
            } catch (JSONException e) {
                Log.d("database", "Parse error on internal creation");
            }
        }

        String[][] test = dbm.getAllBeacons()*/;
        dbm.incrementUpdate();
        // dbm.removeStranded();
        dbm.close();
        // for(int i=0;i<test.length;i++){
        //  Log.d("database", "Added: " + test[i][1] + ": MAC " + test[i][2] + " Descr: " + test[i][3]);
        // }


        //Toast.makeText(this, "Internal Synced",
        //       Toast.LENGTH_SHORT).show();
        done();

    }

    private void externalData(){
        if(!isOnline()){
            //Toast.makeText(this, "No Internet Connection",
            //        Toast.LENGTH_LONG).show();
            Log.d("Launch", "No Internet Found");
            Log.d("Launch", "No Internet Found");
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    {
                        Toast.makeText(MainActivity.this, "No Internet Found",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            });

            done();
            return;
        }
        DataBaseManager dbm = new DataBaseManager(this);
        dbm.open();
        String update;
        try {
            update = dbm.getUpdate();
        } catch (Exception e){
            Log.d("Launch", e.getMessage());
            e.printStackTrace();
            //dbm.close();
            done();

            return;
        }
        dbm.close();
        StaticData staticData = StaticData.getInstance();
        staticData.updateTime = update;
        //staticData.resetSync();
        Thread loc = staticData.SyncWithExternal(StaticData.getLists);
        loc.start();
        if(staticData.isSynced() != -1) {
            Thread pla = staticData.SyncWithExternal(StaticData.getItems);
            pla.start();

            Thread hol = staticData.SyncWithExternal(StaticData.getUnits);
            hol.start();

            Thread fam = staticData.SyncWithExternal(StaticData.getCategories);
            fam.start();

            Thread gen = staticData.SyncWithExternal(StaticData.getdeletelist);
            gen.start();

            Thread spec = staticData.SyncWithExternal(StaticData.getdeleteunit);
            spec.start();

            Thread cul = staticData.SyncWithExternal(StaticData.getdeletecategory);
            cul.start();

           /* Thread bec = staticData.SyncWithExternal(StaticData.getBeacons);
            bec.start();*/

            try {
                pla.join();
                hol.join();
                fam.join();
                gen.join();
                spec.join();
                cul.join();
                //bec.join();
            } catch (java.lang.InterruptedException exception){
                Log.d("database", exception.getMessage());
            }

            updateInternal();
        } else{
            Log.d("database", "error in syncing");
        }
    }//end externalData

    private void done(){
        //finish();
        StaticData staticData = StaticData.getInstance();
        staticData.resetSync();
        //displayData();

        //overridePendingTransition(0, 0);
    }


    /**Determines if the device is internet able.*/
    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }


    class NewSyncItem extends AsyncTask<String,String,String>
    {
        protected void OnPreExecute()
        {
            super.onPreExecute();
        }

        protected String doInBackground(String... args)
        {
            try {
                String ret = null;
                HttpURLConnection c = null;
                String link="http://people.cs.clemson.edu/~sravira/Add/db_insert.php";
                URL u = new URL(link);
                c = (HttpURLConnection) u.openConnection();
                c.setDoInput(true);
                c.setRequestProperty("Content-Type", "application/json;charset=utf-8");
                c.setRequestProperty("X-Requested-With", "XMLHttpRequest");

                //int responseCode=conn.getResponseCode();
                c.setRequestMethod("POST");
                //  c.setRequestProperty("Content-Type",
                //        "application/x-www-form-urlencoded");
                DataBaseManager dbm=new DataBaseManager(getApplicationContext());
                dbm.open();
                JSONArray getlist= dbm.composeListJSONfromSQLite();
                dbm.close();
               /* String urlParameters =
                        "time=" + URLEncoder.encode(StaticData.getInstance().updateTime, "UTF-8");*/

                // c.setRequestProperty("Content-Length", "" +
                //       Integer.toString(urlParameters.getBytes().length));
                //c.setRequestProperty("Content-Language", "en-US");
                //c.setRequestProperty("Content-length", "0");
                c.setUseCaches(false);
                c.setAllowUserInteraction(false);
                c.setConnectTimeout(10000);
                c.setReadTimeout(10000);
                //Send request
                OutputStream os = new BufferedOutputStream(c.getOutputStream());

                os.write(getlist.toString().getBytes());
//clean up
                os.flush();
                os.close();
                c.connect();

                int status = c.getResponseCode();
                if (status == 200 || status == 201) {
                    BufferedReader br = new BufferedReader(new InputStreamReader(c.getInputStream()));
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) sb.append(line + "\n");
                    br.close();
                    ret = sb.toString();
                }



                DataBaseManager dbmupdate=new DataBaseManager(getApplicationContext());
                dbmupdate.open();
                dbmupdate.updatelistSyncStatus();
                dbmupdate.close();
                /*HttpClient client = new DefaultHttpClient();
                HttpPost request = new HttpPost(this.link);
                List<NameValuePair> pairs = new ArrayList<NameValuePair>();
                pairs.add(new BasicNameValuePair("time", StaticData.getInstance().updateTime));
                request.setEntity(new UrlEncodedFormEntity(pairs));

                HttpResponse response = client.execute(request);
                BufferedReader in = new BufferedReader
                        (new InputStreamReader(response.getEntity().getContent()));

                String line="";
                while ((line = in.readLine()) != null) {
                    ret.append(line);
                    break;
                }
                in.close();*/
                String actual = ret.toString().replace("{\"success\":true,\"results\":", "");
                actual = actual.replace("]}", "]");
                Log.d("database updation done ", actual);
            }
            catch (Exception e){
                //synced = -1;//Networking is not available
                Log.d("database", "Network send error: " + e.toString());
            }
            return  null;
        }

        protected void onPostExecute(String str)
        {

        }
    }

    /**
     * DisplayData Method to Display the items in the List
     */

    public void displayData() {
        DataBaseManager dbm=new DataBaseManager(getApplicationContext());
        dbm.open();
        List<Pair<Integer, String>> values;
        Cursor resItem ;
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        boolean syncConnPref = sharedPref.getBoolean("Sort Lists",false);
        boolean price=sharedPref.getBoolean("Sort Price", false);
      SharedPreferences sharedPreferences = getSharedPreferences("CheckBox", MODE_PRIVATE);
boolean checked=sharedPreferences.getBoolean("CheckBox_Value", false);
       //final CheckBox checkBox = (CheckBox) findViewById(R.id.checkbox_sort);
resItem=null;
      if(syncConnPref && price)
        {
            resItem=dbm.sortListsPriceAlpha();
        }
        else if(!syncConnPref && price)
        {
            resItem=dbm.sortListsPrice();
        }
        else if(syncConnPref && !price)
      {
          resItem=dbm.sortLists();
      }
      else if(!syncConnPref && !price)
      {
          resItem=dbm.getLists();
      }

        values=new ArrayList<Pair<Integer, String>>();
        aList = new ArrayList<String>();
        pkID =new ArrayList<Integer>();

        if (resItem != null) {


           /* if (resItem.moveToFirst()) {
                do {
                    int Itemid=Integer.parseInt(resItem.getString(resItem.getColumnIndex("listid")));
                    String ItemName = resItem.getString(resItem.getColumnIndex("listname"));
                    Pair<Integer, String> pair = new Pair<Integer, String>(Itemid, ItemName);
                    values.add(pair);
                    pkID.add(Itemid);
                    aList.add(ItemName);
                } while (resItem.moveToNext());
            }*/
            //instantiate custom adapter
           newListAdapter adapter =new newListAdapter(getApplicationContext(),resItem);
           // ListAdapter adapter=new ListAdapter(getApplicationContext(),resItem);

            //handle listview and assign adapter

            itemList.setAdapter(adapter);
            if(swipeLayout.isRefreshing())
            {
                swipeLayout.setRefreshing(false);
            }

            dbm.close();


        } else {
            dbm.close();
        }

    }










    }
