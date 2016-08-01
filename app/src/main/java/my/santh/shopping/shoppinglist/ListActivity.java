package my.santh.shopping.shoppinglist;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Paint;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * List Activity Class is used to represent  all items with their respective information and performs delete operation of item and cross over.
 * It defines the List view on click listener and Long click Listener to perform edit and delete respectively
 */
public class ListActivity extends AppCompatActivity implements SensorEventListener, SwipeRefreshLayout.OnRefreshListener {
    EditText itemName;
    //DBHelper.FeedEntry.FeedReaderDbHelper mDbHelper;
    ListView itemList;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> aList;
    private ArrayList<String> pkID;
    private  ArrayList<String> cList;
    private  ArrayAdapter<Integer> pkAdapter;
    public AlertDialog myAlertDialog;
    public AlertDialog my2AlertDialog;
    protected static final int RESULT_SPEECH = 1;
    private SensorManager senSensorManager;
    private Sensor senAccelerometer;
    private long lastUpdate = 0;
    private float last_x, last_y, last_z;
    private static final int SHAKE_THRESHOLD = 600;
    TextView price;
    ImageView edit;
    ImageView add;
    SwipeRefreshLayout swipeLayout;
String ListName;

    /**
     * onCreate Method for setting up Listview Events
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        itemList=(ListView) findViewById(R.id.Lists);

        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        senSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        senAccelerometer = senSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        senSensorManager.registerListener(this, senAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        swipeLayout = (SwipeRefreshLayout) findViewById(R.id.swiperefresh);
        swipeLayout.setOnRefreshListener(this);
        edit=(ImageView) findViewById(R.id.update);
         add=(ImageView) findViewById(R.id.additem);
        Intent intent = getIntent();
        String listname=intent.getStringExtra("listname");
        setTitle(listname);

        //mDbHelper=new DBHelper.FeedEntry.FeedReaderDbHelper(this);
        //displayData();
        displayDataCustom();
        /**
         * onTouch Listview to enable Scroll
         */
        itemList.setOnTouchListener(new View.OnTouchListener() {
            // Setting on Touch Listener for handling the touch inside ScrollView
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // Disallow the touch request for parent scroll on touch of child view
                v.getParent().requestDisallowInterceptTouchEvent(true);

                return false;
            }
        });
        /**
         * onitemClick to strike off given item
         */
        itemList.setOnItemClickListener(new android.widget.AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MyCustomAdapter adapter = (MyCustomAdapter) parent.getAdapter();
                Cursor cur = (Cursor) adapter.getItem(position);
                cur.moveToPosition(position);
                int id1 = cur.getInt(cur.getColumnIndexOrThrow("_id"));
                DataBaseManager dbm =new DataBaseManager(ListActivity.this);
                dbm.open();
                Cursor resItem = dbm.getItemDetails(id1);
                resItem.moveToFirst();
                int purchased = Integer.parseInt(resItem.getString(resItem.getColumnIndex("purchased")));
                // int id1 = cur.getInt(cur.getColumnIndexOrThrow("purchased"));
                if (purchased == 0) {
                    dbm.updateItemPurchased(id1);
                    TextView text = (TextView) view.findViewById(R.id.name);
                    text.setPaintFlags(text.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    dbm.close();
                   /* TextView comments=(TextView)findViewById(R.id.comments);
                    comments.setVisibility(view.GONE);
                    TextView quantity=(TextView)findViewById(R.id.Quantity);
                    quantity.setVisibility(view.GONE);
                    TextView price=(TextView)findViewById(R.id.Price);
                    price.setVisibility(view.GONE);*/
                    getPrice();
                    displayDataCustom();
                } else {
                    dbm.updateItemNotPurchased(id1);
                    TextView text = (TextView) view.findViewById(R.id.name);
                    text.setPaintFlags(text.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                    dbm.close();
                   /* TextView comments=(TextView)findViewById(R.id.comments);
                    comments.setVisibility(view.VISIBLE);
                    TextView quantity=(TextView)findViewById(R.id.Quantity);
                    quantity.setVisibility(view.VISIBLE);
                    TextView price=(TextView)findViewById(R.id.Price);
                    price.setVisibility(view.VISIBLE);*/
                    getPrice();
                    displayDataCustom();
                }

                // MyItemAdapter adapter = (MyItemAdapter) parent.getAdapter();
                //Pair<Integer, String> pair = (Pair<Integer, String>) adapter.getItem(position);


                //ListName=pair.second;

            }
        });



      /*  itemList.setOnTouchListener(new OnSwipeTouchListener(ListActivity.this) {
            public boolean onTouch(AdapterView<?> av, View v, int position, long id) {
                TextView item = (TextView) v.findViewById(R.id.name);
                item.setPaintFlags(item.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                return  false;

            }
        });*/

        /**
         * OnItemLongClick to Edit or Deelte an item
         */
        itemList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener()

        {
            @Override
            public boolean onItemLongClick(AdapterView<?> av, final View v, int position, long id) {
                // TODO: Get the position of the item clicked.
                //       Delete it from your collection eg.ArrayList.
                //       Call notifydatasetChanged so that it will refresh
                //       the views displaying updated list.


                MyCustomAdapter adapter = (MyCustomAdapter) av.getAdapter();
                final Cursor cur = (Cursor) adapter.getItem(position);
                cur.moveToPosition(position);
                final int id1 = cur.getInt(cur.getColumnIndexOrThrow("_id"));


                PopupMenu popupMenu = new PopupMenu(getApplicationContext(), v);
                popupMenu.getMenuInflater()
                        .inflate(R.menu.pop_menu, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

                    @Override
                    public boolean onMenuItemClick(MenuItem item) {

                        switch (item.getItemId()) {

                            case R.id.pop_edit:
                                //ImageView edit=(ImageView) findViewById(R.id.update);
                                //ImageView add=(ImageView) findViewById(R.id.additem);

                                Intent itemid = new Intent(getApplicationContext(), ItemActivity.class);
                                Intent intent = getIntent();
                                int Id = intent.getIntExtra("id", 0);
                                String listname = intent.getStringExtra("listname");
                                //int value = pair.first;
                                itemid.putExtra("id", id1);
                                itemid.putExtra("ListID", Id);
                                itemid.putExtra("listname", listname);
                                itemid.putExtra("ListActivity", "Edit");
                                itemid.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(itemid);


                                return true;
                            case R.id.pop_delete:
                                my2AlertDialog = new AlertDialog.Builder(ListActivity.this)
                                        .setTitle("Delete Item")
                                        .setMessage("Are you sure you want to delete this Item?")
                                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                // continue with delete
                                                int id1 = cur.getInt(cur.getColumnIndexOrThrow("_id"));
                                                DataBaseManager dbm = new DataBaseManager(ListActivity.this);
                                                dbm.open();
                                                dbm.delete_ItembyID(id1);
                                                Toast.makeText(getApplicationContext(), "Deleted Item " + cur.getString(cur.getColumnIndex("itemname")),
                                                        Toast.LENGTH_SHORT).show();
                                                dbm.close();
                                                displayDataCustom();
                                                getPrice();

                                            }
                                        })
                                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                // do nothing
                                            }
                                        })

                                        .show();

                                return true;

                        }

                        return true;
                    }
                });
                popupMenu.show();


                return true;
            }
        });


        getPrice();

    }
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

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
     * on detecting Shake Delete all Items in the List
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

    public void onSensorChange(SensorEvent sensorEvent) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_item, menu);
        return true;
    }

    /**
     * Add Item rediretcs to Item activity
     * @param v
     */
    public void AddItem(View v)
    {
     //   ImageView additem=(ImageView) findViewById(R.id.Add);


        Intent ItemAdd=new Intent(this,ItemActivity.class);
        Intent intent = getIntent();
        int Id = intent.getIntExtra("id", 0);
        String name = intent.getStringExtra("listname");
        ItemAdd.putExtra("id",Id);
        ItemAdd.putExtra("ListID",Id);
        ItemAdd.putExtra("listname", name);
        ItemAdd.putExtra("ListActivity","Add");
        ItemAdd.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(ItemAdd);

    }

    private void updateInternal(){
        StaticData staticData = StaticData.getInstance();
        if(staticData.isSynced() != 7){
           // Log.d("database", "Not all synced " + staticData.isSynced());
            done();
            return;
        }
       // Log.d("database", "Starting internal sync");

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
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    {
                        Toast.makeText(ListActivity.this, "No Internet Found",
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
                        displayDataCustom();
                    }
                }
            });
        }

    }


    /**Determines if the device is internet able.*/
    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }




    public void SyncItems(View v)
    {
        DataBaseManager dbm = new DataBaseManager(getApplicationContext());
        dbm.open();
        String msg= dbm.getItemSyncStatus();
        dbm.close();
        Toast.makeText(ListActivity.this, msg,
                Toast.LENGTH_SHORT).show();
        if (msg.equals("DB Sync neededn"))
            new NewSyncItems().execute();

    }

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

    }


    class NewSyncItems extends AsyncTask<String,String,String>
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
                String link="http://people.cs.clemson.edu/~sravira/Add/db_iteminsert.php";
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
                JSONArray getitems= dbm.composeItemJSONfromSQLite();
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

                os.write(getitems.toString().getBytes());
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
                dbmupdate.updateitemSyncStatus();
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
     * Performs necessary function on clicking respective Menu Item
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();



        if(id==R.id.info)
        {
            Intent intent = getIntent();
            int Id = intent.getIntExtra("id", 0);
            String name = intent.getStringExtra("listname");
            Intent infoIntent = new Intent(this, InfoActivity.class);
            infoIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            infoIntent.putExtra("id",Id);
            infoIntent.putExtra("ListID",Id);
            infoIntent.putExtra("listname", name);
            infoIntent.putExtra("List", "List");
            startActivity(infoIntent);
        }
        else if(id==R.id.DeleteItems)

        {


            Deletedialog();


        }
        else if (id==R.id.MarkItems)
        {
            Intent intent = getIntent();
            int Id = intent.getIntExtra("id", 0);
            String name = intent.getStringExtra("listname");

            DataBaseManager dbm =new DataBaseManager(this);
            dbm.open();
            dbm.strikeallitems(Id);
            dbm.close();
            displayDataCustom();
            getPrice();


        }
        else if (id==R.id.UnmarkItems)
        {
            Intent intent = getIntent();
            int Id = intent.getIntExtra("id", 0);
            String name = intent.getStringExtra("listname");

            DataBaseManager dbm =new DataBaseManager(this);
            dbm.open();
            dbm.unstrikeallitems(Id);
            dbm.close();
            displayDataCustom();
            getPrice();


        }

        else if(id==R.id.DeletePurchasedItems)
        {
            DeletedPurchaseddialog();
        }

        else if(id==R.id.Settings)
        {
            Intent intent = getIntent();
            int Id = intent.getIntExtra("id", 0);
            String name = intent.getStringExtra("listname");
            //String message = intent.getStringExtra(InitialActivity.EXTRA_MESSAGE);

            Intent settingsIntent = new Intent(this, SettingsItemActivity.class);
            settingsIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            settingsIntent.putExtra("id",Id);
            settingsIntent.putExtra("ListID",Id);
            settingsIntent.putExtra("listname", name);
            settingsIntent.putExtra("List", "List");
            startActivity(settingsIntent);
        }

        else if (id== R.id.ShareList)
        {
            ShareListEmail();

        }
        
        else if(id==R.id.ShareListW)
        {
            ShareListW();
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Whatsapp Sharing List
     */
    private void ShareListW() {
        Intent whatsappIntent = new Intent(Intent.ACTION_SEND);
        whatsappIntent.setType("text/plain");
        whatsappIntent.setPackage("com.whatsapp");
        Intent intent = getIntent();
        int Id = intent.getIntExtra("id", 0);
        DataBaseManager dbm =new DataBaseManager(ListActivity.this);
        dbm.open();
        Cursor res = dbm.shareListdata(Id);
        aList = new ArrayList<String>();
        pkID = new ArrayList<String>();
        if (res != null ) {

            if (res.moveToFirst()) {
                do {

                    String ItemName = res.getString(res.getColumnIndex("itemname"));
                    String quantity = res.getString(res.getColumnIndex("quantity"));

                    aList.add(ItemName);
                    pkID.add(quantity);
                } while (res.moveToNext());

                ArrayList<String> outputarray = new ArrayList<String>(aList.size() + pkID.size());
                for (int j = 0; j < aList.size(); j++) {

                    outputarray.add(j +"."+aList.get(j) + " - " + pkID.get(j));

                }

                StringBuilder sb = new StringBuilder();
                for (String s : outputarray) {
                    sb.append(s);
                    sb.append("\n");
                    //sb.append("\t");
                }
                dbm.close();
                whatsappIntent.putExtra(Intent.EXTRA_TEXT, sb.toString());
                try {
                    startActivity(whatsappIntent);
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(ListActivity.this, "Whatsapp is not installed in your phone", Toast.LENGTH_SHORT).show();

                }

            }

            else
            {
                dbm.close();
                Toast.makeText(getApplicationContext(), "There are no items in the List  ",
                        Toast.LENGTH_SHORT).show();
            }


        }




    }


    public final static boolean isValidEmail(CharSequence target) {
        return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    /**
     * Share List by Email
     */
    private void ShareListEmail() {
        final AlertDialog.Builder alertEmail = new AlertDialog.Builder(ListActivity.this);
        LinearLayout layoutEmail = new LinearLayout(ListActivity.this);
        layoutEmail.setOrientation(LinearLayout.VERTICAL);
        //  String winner;

        alertEmail.setTitle("Share List by Mail ");
        final TextView descriptionEmail = new TextView(ListActivity.this);
        // final TextView descriptionBox = new Textview(game.this);
        descriptionEmail.setTextSize(20);
        descriptionEmail.setHint("Enter Email Address");
        descriptionEmail.setSingleLine();
        layoutEmail.addView(descriptionEmail);


        final EditText titleEmail = new EditText(ListActivity.this);
        titleEmail.setHint("Email ");
        layoutEmail.addView(titleEmail);

        alertEmail.setView(layoutEmail);

        alertEmail.setPositiveButton("OK", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int whichButton) {
                final String Name = titleEmail.getText().toString();
                boolean email=isValidEmail(Name);


                if (email ) {
                    Intent intent = getIntent();
                    int Id = intent.getIntExtra("id", 0);
                    String namelist = intent.getStringExtra("listname");
                    Intent i = new Intent(Intent.ACTION_SEND);
                    i.setType("message/rfc822");
                    i.putExtra(Intent.EXTRA_EMAIL, new String[]{Name});
                    i.putExtra(Intent.EXTRA_SUBJECT, namelist);
                    DataBaseManager dbm = new DataBaseManager(ListActivity.this);
                    dbm.open();
                    Cursor res = dbm.shareListdata(Id);
                    aList = new ArrayList<String>();
                    pkID = new ArrayList<String>();
                    cList = new ArrayList<String>();
                    if (res != null) {

                        if (res.moveToFirst()) {
                            do {

                                String ItemName = res.getString(res.getColumnIndex("itemname"));
                                String quantity = res.getString(res.getColumnIndex("quantity"));
                                String categoryName = res.getString(res.getColumnIndex("categoryname"));
                                cList.add(categoryName);
                                aList.add(ItemName);
                                pkID.add(quantity);
                            } while (res.moveToNext());

                            ArrayList<String> outputarray = new ArrayList<String>(aList.size() + pkID.size());
                            Set<String> distinct = new LinkedHashSet<>(cList);


                            for (int j = 0; j < aList.size(); j++) {

                                outputarray.add(j + 1 + "." + aList.get(j) + " - " + pkID.get(j));

                            }

                            StringBuilder sb = new StringBuilder();
                            for (String s : outputarray) {
                                sb.append(s);
                                sb.append("\n");
                                //sb.append("\t");
                            }

                            dbm.close();
                            i.putExtra(Intent.EXTRA_TEXT, sb.toString());
                            try {
                                startActivity(Intent.createChooser(i, "Send mail..."));
                            } catch (android.content.ActivityNotFoundException ex) {
                                Toast.makeText(ListActivity.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
                            }

                        }

                        else
                        {
                            dbm.close();
                            Toast.makeText(getApplicationContext(), "There are no items in the List  ",
                                    Toast.LENGTH_SHORT).show();
                        }

                    }



                } else {
                    Toast.makeText(getApplicationContext(), "Invalid Email ",
                            Toast.LENGTH_SHORT).show();
                }


            }

        });
        alertEmail.setNegativeButton("Cancel", null);


        alertEmail.create().show();

    }

    /**
     * Method to deleet all Purchased Items
     */
    private void DeletedPurchaseddialog()
    {
        if( myAlertDialog != null && myAlertDialog.isShowing()  ||( my2AlertDialog!=null && my2AlertDialog.isShowing())  ) return;

        final AlertDialog.Builder myQuittingDialogBox =new AlertDialog.Builder(this);
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        //set message, title, and icon
        myQuittingDialogBox.setTitle("Delete");
        final TextView descriptionBox = new TextView(this);
        // final TextView descriptionBox = new Textview(game.this);
        descriptionBox.setTextSize(20);
        descriptionBox.setHint("Do you  want to delete all Purchased Items in this List ?");
        layout.addView(descriptionBox);

        myQuittingDialogBox.setView(layout);

        myQuittingDialogBox.setPositiveButton("Delete", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int whichButton) {
                Intent intent = getIntent();
                int Id = intent.getIntExtra("id", 0);
                //your deleting code
                DataBaseManager dbm =new DataBaseManager(ListActivity.this);
                dbm.open();
             int count=   dbm.deletePurchasedItems(Id);
                if(count<=0)
                Toast.makeText(getApplicationContext(), "There are no Purchased items in list ",
                        Toast.LENGTH_LONG).show();
                else
                    Toast.makeText(getApplicationContext(), "All Purchased Items are  Deleted",
                            Toast.LENGTH_SHORT).show();
                dbm.close();
                displayDataCustom();
                getPrice();

            }

        });


        myQuittingDialogBox .setNegativeButton("Cancel", null);
        myAlertDialog= myQuittingDialogBox .create();
        myAlertDialog.show();

    }

    /**
     * Delete all Items in the List
     */
    public void Deletedialog()
    {
        if( myAlertDialog != null && myAlertDialog.isShowing()  ||( my2AlertDialog!=null && my2AlertDialog.isShowing())  ) return;

        final AlertDialog.Builder myQuittingDialogBox =new AlertDialog.Builder(this);
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        //set message, title, and icon
        myQuittingDialogBox.setTitle("Delete");
        final TextView descriptionBox = new TextView(this);
        // final TextView descriptionBox = new Textview(game.this);
        descriptionBox.setTextSize(20);
        descriptionBox.setHint("Do you  want to remove all the Items in this List ?");
        layout.addView(descriptionBox);

        myQuittingDialogBox.setView(layout);

        myQuittingDialogBox.setPositiveButton("Delete", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int whichButton) {
                Intent intent = getIntent();
                int Id = intent.getIntExtra("id", 0);
                //your deleting code
                DataBaseManager dbm = new DataBaseManager(ListActivity.this);
                dbm.open();
                int count= dbm.deleteItems(Id);
                dbm.close();
                if(count<=0)
                Toast.makeText(getApplicationContext(), "There are no items in list",
                        Toast.LENGTH_LONG).show();
                else
                    Toast.makeText(getApplicationContext(), "All Items are  Deleted ",
                            Toast.LENGTH_SHORT).show();
                displayDataCustom();
                getPrice();
            }

        });


        myQuittingDialogBox .setNegativeButton("Cancel", null);
        myAlertDialog= myQuittingDialogBox .create();
        myAlertDialog.show();
    }

    /**
     * Send Data to MyCustomAdapter
     */
    public void displayDataCustom() {
        List<Pair<Integer, String>> values;
        Cursor resItem=null ;
        Intent intent = getIntent();
        int Id= intent.getIntExtra("id", 0);

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        boolean checked = sharedPref.getBoolean("Sort Items", false);

        SharedPreferences sharedPreferences = getSharedPreferences("CheckBoxitem", MODE_PRIVATE);
        // boolean checked=sharedPreferences.getBoolean("CheckBoxitem_Value", false);
        //final CheckBox checkBox = (CheckBox) findViewById(R.id.checkbox_sortitems);

        SharedPreferences sharedPreferenceshide = getSharedPreferences("CheckBoxhide", MODE_PRIVATE);
        boolean checkedhide=sharedPref.getBoolean("Hide Items", false);

        SharedPreferences sharedPreferencescategory = getSharedPreferences("CheckBoxCategory", MODE_PRIVATE);
        boolean checkedcategory=sharedPref.getBoolean("Sort Category", false);
        //final CheckBox checkBoxcategory = (CheckBox) findViewById(R.id.checkbox_sortCategory);
        DataBaseManager dbm =new DataBaseManager(ListActivity.this);
        dbm.open();

       if(checked && checkedcategory && !checkedhide)//001
       {
           resItem=dbm.sortcategorydata(Id);
       }
        else if(checked && !checkedcategory && !checkedhide)//011
       {
           resItem=dbm.sortdata(Id);
       }
        else if(!checked && checkedcategory && !checkedhide)//101
       {
           resItem=dbm.sortcategory(Id);
       }
        else if(!checked && !checkedcategory && !checkedhide)//111
        {
            resItem=dbm.getDataItemCursor(Id);
        }
       else if(checked && checkedcategory && checkedhide)//000
       {
            resItem=dbm.sortshowall(Id);
       }
       else if(checked && !checkedcategory && checkedhide)//010
       {
            resItem=dbm.sortshow010(Id);
       }
       else if(!checked && checkedcategory && checkedhide)//100
       {
            resItem=dbm.sortshow100(Id);
       }
       else if(!checked && !checkedcategory && checkedhide)//110
       {
            resItem=dbm.sortshow110(Id);
       }





     //   dbm.close();

      /*  if(checked)
        {
            resItem=mDbHelper.sortdata(Id);
        }
        else
        {
            resItem=mDbHelper.getDataItemCursor(Id);
        }*/


        values=new ArrayList<Pair<Integer, String>>();


        if (resItem != null) {


            /*if (resItem.moveToFirst()) {
                do {
                    int Itemid=Integer.parseInt(resItem.getString(resItem.getColumnIndex("itemid")));
                    String ItemName = resItem.getString(resItem.getColumnIndex("itemname"));
                    Pair<Integer, String> pair = new Pair<Integer, String>(Itemid, ItemName);

                    values.add(pair);
                    pkID.add(Itemid);
                    aList.add(ItemName);
                } while (resItem.moveToNext());
            }*/
            //instantiate custom adapter
            MyCustomAdapter adapter=new MyCustomAdapter(getApplicationContext(),resItem);

            //handle listview and assign adapter

            itemList.setAdapter(adapter);
           // itemList.setDivider(null);
            if(swipeLayout.isRefreshing())
            {
                swipeLayout.setRefreshing(false);
            }
            dbm.close();


        }
        else {
            dbm.close();
        }

    }

    /**
     * Show the price of List
     */
    public void getPrice()
    {
        Intent intent = getIntent();
        int Id= intent.getIntExtra("id", 0);
        DataBaseManager dbm =new DataBaseManager(ListActivity.this);
        dbm.open();
        Cursor res=dbm.getPrice(Id);

        res.moveToFirst();
        float sum =res.getFloat(res.getColumnIndexOrThrow("price"));
        dbm.close();
        price=(TextView)findViewById(R.id.listprice);
        SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(this);


        String currency  = SP.getString("Currency", "1");
       // SharedPreferences sharedPreferences = getSharedPreferences("RadioBox", MODE_PRIVATE);
        //String currency=sharedPreferences.getString("RadioBox", "$");
        price.setText("Total list Price "+currency+sum);
    }

    /**
     * Back Pressed method
     */
    public void  onBackPressed()
    {
        Intent settingsIntent = new Intent(this, MainActivity.class);
        Intent intent = getIntent();
        int Id = intent.getIntExtra("id", 0);
        String name = intent.getStringExtra("listname");
        settingsIntent.putExtra("id",Id);
        settingsIntent.putExtra("ListID",Id);
        settingsIntent.putExtra("listname", name);
        settingsIntent.putExtra("List", "List");
        settingsIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(settingsIntent);
    }





}
