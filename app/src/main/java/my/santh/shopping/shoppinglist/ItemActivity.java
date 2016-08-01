package my.santh.shopping.shoppinglist;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.speech.RecognizerIntent;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * This is a Item class which is used to add an item to list .Update the item.
 * It defines the List view on click listener and Long click Listener to perform edit and delete respectively
 */
public class ItemActivity extends AppCompatActivity {

EditText Nam,Quanity,Price,Comments;
    TextView dollarprice;
    TextView Name;
    String label;
    int ListID;
    Handler handler;
    Timer timer = new Timer();
    String ListName;
    ImageView add,update,cow;
    // Spinner element
    Spinner units,category;
    protected static final int RESULT_SPEECH = 1;
    MediaPlayer mCow;
    //MediaPlayer mdiceWin;
    //DBHelper.FeedEntry.FeedReaderDbHelper mDbHelper;
    /*
    * Change to type CustomAutoCompleteView instead of AutoCompleteTextView
    * since we are extending to customize the view and disable filter
    * The same with the XML view, type will be CustomAutoCompleteView
    */
    CustomAutoCompleteView myAutoComplete;

    // adapter for auto-complete
    ArrayAdapter<String> myAdapter;
    // just to add some initial value
    String[] item = new String[] {"Please search..."};

    /**
     * oncreate method for setting up events of listview
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
       // Name = (TextView) findViewById(R.id.Name);
        Quanity=(EditText) findViewById(R.id.Quantity);
        Price=(EditText) findViewById(R.id.Price);
        Comments=(EditText)findViewById(R.id.comments);
       // mCow=MediaPlayer.create(this,R.raw.cow);
        cow=(ImageView) findViewById(R.id.cow);
        //mDbHelper = new DBHelper.FeedEntry.FeedReaderDbHelper(this);
        // Spinner element
        units = (Spinner) findViewById(R.id.units);
        dollarprice=(TextView)findViewById(R.id.textdollars);
        category=(Spinner) findViewById(R.id.category);

        handler = new Handler(callback);
        //link handler to callback
      //  handler = new Handler(callback);

        // autocompletetextview is in activity_main.xml
        myAutoComplete = (CustomAutoCompleteView) findViewById(R.id.myautocomplete);

        // add the listener so it will tries to suggest while the user types
        myAutoComplete.addTextChangedListener(new CustomAutoCompleteTextChangedListener(this));

        // set our adapter
        myAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, item);
        myAutoComplete.setAdapter(myAdapter);



        dislplayunits();
        displaycategory();
        category.setSelection(20);
        add=(ImageView)findViewById(R.id.additem);
        update=(ImageView)findViewById(R.id.update);
        Intent i=getIntent();
        String className = getIntent().getStringExtra("ListActivity");
        if(className.equals("Edit")) {
            setTitle("Update Item");
            add.setVisibility(View.INVISIBLE);

            int id = i.getIntExtra("id",0);
            DataBaseManager dbm =new DataBaseManager(this);
            dbm.open();
            Cursor res = dbm.getItemDetails(id);

            if (res != null) {


                if (res.moveToFirst()) {
                    do {
                        dbm.close();
                        Quanity.setText(res.getString(res.getColumnIndex("quantity")));
                        myAutoComplete.setText(res.getString(res.getColumnIndex("itemname")));
                        myAutoComplete.dismissDropDown();
                        Price.setText(res.getString(res.getColumnIndex("price")));
                        Comments.setText(res.getString(res.getColumnIndex("comments")));
                        int unitposition = Integer.parseInt(res.getString(res.getColumnIndex("unitid")));
                        int categoryposition = Integer.parseInt(res.getString(res.getColumnIndex("category_id")));
                        dbm.open();
                       Cursor unitname= dbm.getunitname(unitposition);
                       if( unitname.moveToFirst()) {
                           String uname = unitname.getString(unitname.getColumnIndex("unitname"));
                           List<String> lables = dbm.getUnits();

                           // Creating adapter for spinner
                           ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                                   android.R.layout.simple_spinner_item, lables);

                           // Drop down layout style - list view with radio button
                           dataAdapter
                                   .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                           if (!uname.equals(null)) {
                               int spinnerPosition =  dataAdapter.getPosition(uname);
                               units.setSelection(spinnerPosition);
                           }
                       }



                        Cursor categoryname=dbm.getcategoryname(categoryposition);
                        if(categoryname.moveToFirst()) {
                            String cname = categoryname.getString(categoryname.getColumnIndex("categoryname"));

                            List<String> lablesc = dbm.getCategory();

                            // Creating adapter for spinner
                            ArrayAdapter<String> dataAdapterc = new ArrayAdapter<String>(this,
                                    android.R.layout.simple_spinner_item, lablesc);

                            // Drop down layout style - list view with radio button
                            dataAdapterc
                                    .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                            if (!cname.equals(null)) {
                                int spinnerPosition = dataAdapterc.getPosition(cname);
                                category.setSelection(spinnerPosition);
                            }
                        }

                        dbm.close();


                    } while (res.moveToNext());
                }



            }

        }
        else{
            setTitle("Add Item");
        update.setVisibility(View.INVISIBLE);}


        units.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position,
                                       long id) {
                // On selecting a spinner item
                String label = parent.getItemAtPosition(position).toString();
                SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(ItemActivity.this);


                String currency  = SP.getString("Currency","1");
                 if(label.equals("Bottle"))
                {
                    dollarprice.setText(currency+"/btl");
                }
                else if(label.equals("Gallon"))
                 {
                     dollarprice.setText(currency+"/gal");
                 }
                else if (label.equals("Ounce"))
                {
                    dollarprice.setText(currency+"/oz");
                }
                 else if (label.equals("Packet"))
                 {
                     dollarprice.setText(currency+"/pkt");
                 }

                 else if (label.equals("Pound"))
                 {
                     dollarprice.setText(currency+"/lbs");
                 }
                 else if (label.equals("Quart"))
                 {
                     dollarprice.setText(currency+"/qrt");
                 }
                else
                 {
                     dollarprice.setText(currency+"/" + Character.toLowerCase(label.charAt(0)) + label.substring(1));
                     //dollarprice.setText("$/qrt");
                     String s=dollarprice.toString();
                 }

                // Showing selected spinner item
                //Toast.makeText(parent.getContext(), "You selected: " + label,
                       // Toast.LENGTH_LONG).show();

            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub

            }

        });
        //displaycategory();
    }

    // this function is used in CustomAutoCompleteTextChangedListener.java and returns all the item suggestions
    public String[] getItemsFromDb(String searchTerm){
        DataBaseManager dbm =new DataBaseManager(this);
        dbm.open();
        if(searchTerm.contains("'"))
        {
            searchTerm=searchTerm.replaceAll("[']","''");
        }
        List<String> lables = dbm.read(searchTerm);
        String[] item = new String[lables.size()];
        item = lables.toArray(item);
        dbm.close();
       return  item;


    }


    protected void onPause() {
        if(mCow!=null) {
            cow.setImageResource(R.drawable.rszcow);
            mCow.stop();
            mCow.reset();
            mCow.release();
            mCow = null;
        }
        super.onPause();

        //dice_sound.pause(sound_id);
        //diceBonus.pause(sound_bonus);
        //diceLose.pause(sound_lose);

        //mdiceSound.pause();

        //mdiceLose.pause();
        //mdiceWin.pause();
        //System.out.println("On Pause");
        // onreturn();
    }

    protected void onDestroy() {
        super.onDestroy();
        timer.cancel();
    }
    protected void onResume() {
        if(mCow!=null) {
            cow.setImageResource(R.drawable.rszcow);
            mCow.stop();
            mCow.reset();
            mCow.release();
            mCow = null;
        }

        super.onResume();

       //System.out.println("On Resume");
        //Intent intent = getIntent();



        //String message = intent.getStringExtra(InitialActivity.EXTRA_MESSAGE);

    }

    /**
     * Dislay list of all categories in spinner
     */
    private void displaycategory() {
        DataBaseManager dbm =new DataBaseManager(this);
        dbm.open();

        List<String> lables = dbm.getCategory();

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, lables);

        // Drop down layout style - list view with radio button
        dataAdapter
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        category.setAdapter(dataAdapter);

dbm.close();


    }

    /**
     * Display List of all units in Spinner
     */
    private void dislplayunits() {
        DataBaseManager dbm =new DataBaseManager(this);
        dbm.open();
        List<String> lables = dbm.getUnits();

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, lables);

        // Drop down layout style - list view with radio button
        dataAdapter
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        units.setAdapter(dataAdapter);
        dbm.close();
    }

    /**
     * this method adds an item to the List
     * @param view
     */
    public void AddItem(View view) {
        String name=myAutoComplete.getText().toString();
        if(name.contains("'"))
        {
            name=name.replaceAll("[']","\'");
        }
        String quantity=Quanity.getText().toString();
        String price=Price.getText().toString();
        int purchased=0;
        if(name.length()==0)
        {
            myAutoComplete.requestFocus();
            myAutoComplete.setError("FIELD CANNOT BE EMPTY");
        }
        else if(quantity.length()==0)
        {
            Quanity.requestFocus();
            Quanity.setError("FIELD CANNOT BE EMPTY");
        } else if(price.length()==0)
        {
            Price.requestFocus();
            Price.setError("FIELD CANNOT BE EMPTY");
        }
       else {
            String unitname = units.getSelectedItem().toString();
            String categoryname=category.getSelectedItem().toString();

            int unitid = units.getSelectedItemPosition() + 1;
            int categoryid = category.getSelectedItemPosition() + 1;


            Intent i = getIntent();
            ListID = i.getIntExtra("id", 0);
            ListName = i.getStringExtra("listname");

            DataBaseManager dbm =new DataBaseManager(this);
            dbm.open();
            Cursor uid=dbm.getunitid(unitname);
            uid.moveToFirst();
            Cursor cid=dbm.getcategoryid(categoryname);
            cid.moveToFirst();
            unitid=uid.getInt(uid.getColumnIndex("unitid"));
            categoryid=cid.getInt(cid.getColumnIndex("category_id"));

            boolean checkitemname=dbm.checkdata(name,ListID);

            if (checkitemname==false){
            boolean dataItemInserted = dbm.insertDataItem(name,
                    ListID, unitid, categoryid, Comments.getText().toString().replaceAll("[']", "\'"), Float.parseFloat(Quanity.getText().toString()),
                    Float.parseFloat(Price.getText().toString()), purchased
            );


                dbm.close();

                Toast.makeText(getApplicationContext(), "Added New Item :" + name,
                        Toast.LENGTH_SHORT).show();

                Intent afteradding = new Intent(this, ListActivity.class);
                afteradding.putExtra("id", ListID);
                afteradding.putExtra("listname", ListName);
                afteradding.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(afteradding);

            } else {
                dbm.close();
                Toast.makeText(getApplicationContext(), "Item Alerady exists",
                        Toast.LENGTH_SHORT).show();

            }


        }








    }

    /**
     * This method Updates Item in the List
     * @param view
     */
    public void  UpdateItem(View view)
    {
        String name=myAutoComplete.getText().toString();
        if(name.contains("'"))
        {
            name=name.replaceAll("[']","\'");
        }
        String quantity=Quanity.getText().toString();
        String price=Price.getText().toString();
        if(name.length()==0)
        {
            myAutoComplete.requestFocus();
            myAutoComplete.setError("FIELD CANNOT BE EMPTY");
        }
        else if(quantity.length()==0)
        {
            Quanity.requestFocus();
            Quanity.setError("FIELD CANNOT BE EMPTY");
        } else if(price.length()==0)
        {
            Price.requestFocus();
            Price.setError("FIELD CANNOT BE EMPTY");
        }
        else {
            String className = getIntent().getStringExtra("ListActivity");
            int id = 0;
            int ListId = 0;
            String ListName = "";
            if (className.equals("Edit")) {
                Intent i = getIntent();
                id = i.getIntExtra("id", 0);
                ListId = i.getIntExtra("ListID", 0);
                ListName = i.getStringExtra("listname");
            }
            String unitname = units.getSelectedItem().toString();
            String categoryname=category.getSelectedItem().toString();
            int unitid = units.getSelectedItemPosition() + 1;
            int categoryid = category.getSelectedItemPosition() + 1;

           // boolean checkitemname=dbm.checkdata(name, ListId);

           // if (checkitemname == false) {
                DataBaseManager dbm =new DataBaseManager(this);
                dbm.open();
            Cursor uid=dbm.getunitid(unitname);
            uid.moveToFirst();
            Cursor cid=dbm.getcategoryid(categoryname);
            cid.moveToFirst();
            unitid=uid.getInt(uid.getColumnIndex("unitid"));
            categoryid=cid.getInt(cid.getColumnIndex("category_id"));
                boolean dataItemUpdated = dbm.UpdateDataItem(name,
                        ListId, unitid, categoryid, Comments.getText().toString().replaceAll("[']","\'"), Float.parseFloat(Quanity.getText().toString()), Float.parseFloat(Price.getText().toString()), id);

                dbm.close();
                Toast.makeText(getApplicationContext(), "Updated  Item :" + name,
                        Toast.LENGTH_SHORT).show();
                Intent afterupdating = new Intent(this, ListActivity.class);
                afterupdating.putExtra("id", ListId);
                afterupdating.putExtra("listname", ListName);
                afterupdating.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(afterupdating);
            /*} else {
                dbm.close();
                Toast.makeText(getApplicationContext(), "Item Alerady exists",
                        Toast.LENGTH_SHORT).show();


            }*/

        }

    }


    /**
     * Performs necessary action on clikcing respective menu item.
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


            int id = item.getItemId();



            if(id==R.id.info)
            {
                Intent intent = getIntent();
                int Id = intent.getIntExtra("id", 0);
                String name = intent.getStringExtra("listname");
                Intent infoIntent = new Intent(this, InfoActivity.class);
                infoIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                infoIntent.putExtra("id",Id);
                infoIntent.putExtra("ListID", Id);
                infoIntent.putExtra("listname", name);
                infoIntent.putExtra("List", "Item");
                startActivity(infoIntent);
            }
            else if(id==android.R.id.home) {
                // System.out.println("back button clicked");
                Intent intent = getIntent();
                String strdata = getIntent().getStringExtra("ListActivity");
                if (strdata.equals("Edit") || strdata.equals("Add")) {
                    int ListId = intent.getIntExtra("ListID", 0);
                    String ListName = intent.getStringExtra("listname");
                    Intent intentMain = new Intent(this, ListActivity.class);
                    intentMain.putExtra("id", ListId);
                    intentMain.putExtra("listname", ListName);
                    intentMain.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intentMain);
                }
                }

        return true;



    }

    /**
     * onBackPresses redirects to respective intent
     */
    public void onBackPressed() {
        if (mCow != null) {
            cow.setImageResource(R.drawable.rszcow);
            mCow.stop();
            mCow.reset();
            mCow.release();
            mCow = null;
        }
        Intent intent = getIntent();
        String strdata = getIntent().getStringExtra("ListActivity");
        if (strdata.equals("Edit") || strdata.equals("Add")) {
            int ListId = intent.getIntExtra("ListID", 0);
            String ListName = intent.getStringExtra("listname");
            Intent intentMain = new Intent(this, ListActivity.class);
            intentMain.putExtra("id", ListId);
            intentMain.putExtra("listname", ListName);
            intentMain.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intentMain);
        }
    }

    public void goback(View v)
    {
        onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_updateitem, menu);
        return true;
    }




    /**
     *onVoice Method for Google Sppech to text COnvertor
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
        super.onActivityResult(requestCode,resultCode,data);


        switch (requestCode) {
            case RESULT_SPEECH: {
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> Results;
                    Results = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    String text = Results.get(0);
                    Name = (CustomAutoCompleteView) findViewById(R.id.myautocomplete);
                    Name.setText(text);


                }


            }

        }

    }


    class Roll extends TimerTask {
        public void run() {
            handler.sendEmptyMessage(0);

        }
    }

    public void onCow(View view) {


        if (mCow != null) {
            cow.setImageResource(R.drawable.rszcow);
            mCow.stop();
            mCow.reset();
            mCow.release();
            mCow = null;
        }
        mCow=MediaPlayer.create(this,R.raw.cow);
        cow.setImageResource(R.drawable.cowgrass);
        mCow.start();
       // timer.schedule(new Roll(), 400);

        mCow.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

            @Override
            public void onCompletion(MediaPlayer mp) {
                cow.setImageResource(R.drawable.rszcow);
            }

        });

    }

    Handler.Callback callback = new Handler.Callback() {

        public boolean handleMessage(Message msg) {




            return  true;
        }
        };


}
