package my.santh.shopping.shoppinglist;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * LaunchActivity for SplashScreen and calling threads to link external hp scripts with internal database .
 */

public class LaunchActivity extends AppCompatActivity {

    private static boolean splashLoaded = false;
    private LaunchActivity me;
    final LaunchActivity sPlashScreen = this;

    /**
     *
     * Duration of wait **/
    private final int SPLASH_DISPLAY_LENGTH = 3000;

    /**
     * Called when the activity is first created. */


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);

          /* New Handler to start the Menu-Activity
         * and close this Splash-Screen after some seconds.*/
       /* if (!splashLoaded) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    Intent mainIntent = new Intent(LaunchActivity.this, MainActivity.class);
                    LaunchActivity.this.startActivity(mainIntent);
                    LaunchActivity.this.finish();
                }
            }, SPLASH_DISPLAY_LENGTH);
        }*/
        /*else {
            Intent goToMainActivity = new Intent(LaunchActivity.this, MainActivity.class);
            goToMainActivity.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(goToMainActivity);
            finish();
        }*/

        Thread mSplashThread;
        mSplashThread =  new Thread(){
            @Override
            public void run(){
                try{
                    externalData();
                } catch (Exception e){
                    Log.d("Launch", "Splash Crash");
                    e.printStackTrace();

                }
            }
        };

        mSplashThread.start();

    }

    private void done(){
        finish();
        StaticData staticData = StaticData.getInstance();
        staticData.resetSync();
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setClass(sPlashScreen, MainActivity.class);
        startActivity(intent);
        overridePendingTransition(0, 0);
    }

    /** Takes all staticData JsonArray objects and copies over to internal database.
     *
     * Calling this will also call (DONE) to close the entire system.*/
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

    }//end updateInternal

    /**
     * Calls the Static Data sync object.  If not internet able will just go to the main screen.
     *
     * */
    private void externalData(){
        if(!isOnline()){
            //Toast.makeText(this, "No Internet Connection",
            //        Toast.LENGTH_LONG).show();
            Log.d("Launch", "No Internet Found");
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    {
                        Toast.makeText(LaunchActivity.this, "No Internet Found",
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

    /**Determines if the device is internet able.*/
    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

}
