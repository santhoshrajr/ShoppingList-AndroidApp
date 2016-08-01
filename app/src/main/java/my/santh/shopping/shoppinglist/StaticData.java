package my.santh.shopping.shoppinglist;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Acessing PHP files using Static data Class .All Php files are accessed using this class.
 *
 */
public class StaticData {
    ///Holds the currently active profile the user has selected
    static Context context;
    private static String activeProfile;
    ///Holds an instance of the class (used as singleton)
    private static final StaticData hold = new StaticData();

    private int synced = 0;//7 is all externally gathered, -1 is network error, 8 is synced


    public JSONArray lists;
    public JSONArray Items;
    public JSONArray units;
    public JSONArray catgeories;
    public  JSONArray deleteid;
    public  JSONArray deleteunit;
    public JSONArray deeletecategory;
  /*  public JSONArray geni;
    public JSONArray species;
    public JSONArray cultivar;
    public JSONArray beacons;*/
    public String updateTime;

  /*  public String[] lastSearch;
    public String[] lastLocalPlant;
    public String[] lastLocalLocation;
    public String[] pinLocation;*/

    private static final String viewPHP = "https://people.cs.clemson.edu/~sravira/Viewing/";
    public static final String getLists = viewPHP + "db_getalllists.php";
    public static final String getItems =    viewPHP + "db_getallitems.php";
    public static final String getUnits =    viewPHP + "db_getallunits.php";
    public static final String getCategories =  viewPHP + "db_getallcategories.php";
    public static final String getdeletelist="https://people.cs.clemson.edu/~sravira/Delete/db_getdeletelist.php";
    public static final String getdeleteunit="https://people.cs.clemson.edu/~sravira/Delete/db_getdeleteunit.php";
    public static final String getdeletecategory="https://people.cs.clemson.edu/~sravira/Delete/db_getdeletecategory.php";
//    public static  final String aid=android.provider.Settings.Secure.getString(context.getContentResolver(),
  //  android.provider.Settings.Secure.ANDROID_ID);
    /*public static final String getGeni =      viewPHP + "GetGenus.php";
    public static final String getSpecies =   viewPHP + "GetSpecies.php";
    public static final String getCultivar =  viewPHP + "GetCultivar.php";
    public static final String getBeacons =  viewPHP + "GetBeacons.php";

    public static final String getPlantImage = viewPHP + "GetImageEchoTwo.php?ID=";*/
    public int isSynced(){ return synced; }
    public void resetSync(){synced = 0;}

    public static StaticData getInstance(){return hold;}

    /**
     * Makes and runs a Networker Thread using some link.
     *
     *
     * */
    public Networker SyncWithExternal(String link){
        Networker thread = new Networker(link);
        return thread;
    }

    /**
     * Class for a Networker Thread
     *
     *  Used to communicate to external database php scripts.
     *
     *  Will set all the JsonArray objects inside staticData instance.
     *
     *
     *
     *
     *
     * */
    private class Networker extends Thread {
        String link;
        public Networker(Object parameter) {
            link = parameter.toString();
        }
        /**
         * Goes through and syncs each internal database object.
         *
         * Catches it's own HTTPClient exceptions.
         * */
        public void run() {
            try {
                String ret = null;
                Log.d("database", "Doing: " + this.link + " Time:" + StaticData.getInstance().updateTime);
                HttpURLConnection c = null;
                URL u = new URL(this.link);
                c = (HttpURLConnection) u.openConnection();


                //int responseCode=conn.getResponseCode();
                c.setRequestMethod("POST");
                c.setRequestProperty("Content-Type",
                        "application/x-www-form-urlencoded");
                //androidid idnew=new androidid();
                ;
                String urlParameters =
                        "time=" + URLEncoder.encode(StaticData.getInstance().updateTime, "UTF-8")   ;

                c.setRequestProperty("Content-Length", "" +
                        Integer.toString(urlParameters.getBytes().length));
                c.setRequestProperty("Content-Language", "en-US");
                //c.setRequestProperty("Content-length", "0");
                c.setUseCaches(false);
                c.setAllowUserInteraction(false);
                c.setConnectTimeout(10000);
                c.setReadTimeout(10000);
                //Send request
                DataOutputStream wr = new DataOutputStream(
                        c.getOutputStream ());
                wr.writeBytes(urlParameters);
                wr.flush();
                wr.close ();
                c.connect();

                int status = c.getResponseCode();
                if(status==200 || status==201) {
                    BufferedReader br = new BufferedReader(new InputStreamReader(c.getInputStream()));
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) sb.append(line + "\n");
                    br.close();
                    ret = sb.toString();
                }
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
                Log.d("database", actual);
                if(synced != -1) {
                    //Setting matching JSON Objects
                    try {
                        if (link.matches(StaticData.getLists)) {
                            lists = new JSONArray(actual);
                            synced++;
                        }
                        if (link.matches(StaticData.getItems)) {
                            Items = new JSONArray(actual);
                            synced++;
                        }
                        if (link.matches(StaticData.getUnits)) {
                            units = new JSONArray(actual);
                            synced++;
                        }
                        if (link.matches(StaticData.getCategories)) {
                            catgeories = new JSONArray(actual);
                            synced++;
                        }
                        if (link.matches(StaticData.getdeletelist)) {
                            deleteid = new JSONArray(actual);
                            synced++;
                        }
                        if (link.matches(StaticData.getdeleteunit)) {
                            deleteunit = new JSONArray(actual);
                            synced++;
                        }
                        if (link.matches(StaticData.getdeletecategory)) {
                            deeletecategory = new JSONArray(actual);
                            synced++;
                        }
                       /* if (link.matches(StaticData.getCultivar)) {
                            cultivar = new JSONArray(actual);
                            synced++;
                        }
                        if (link.matches(StaticData.getBeacons)) {
                            beacons = new JSONArray(actual);
                            synced++;
                        }*/

                    } catch (JSONException e) {
                        Log.d("database", "  Json Parse error " + e.toString());
                        //synced = -1;
                    }
                }
            }catch (Exception e){
                synced = -1;//Networking is not available
                Log.d("database", "Network send error: " + e.toString());
            }
        }//end run
    }//end Networker
/*
    private void updateInternal(String sjson){
        try {
            JSONArray jsonArray = new JSONArray(sjson);
        } catch (JSONException e){
            Log.d("database", "Json Parse error "+ e.toString());
        }
    }
*/
}
