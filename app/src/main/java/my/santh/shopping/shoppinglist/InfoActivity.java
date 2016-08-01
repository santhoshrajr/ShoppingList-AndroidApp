package my.santh.shopping.shoppinglist;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Info Activity Class for description of App
 */
public class InfoActivity extends AppCompatActivity {

    public final static String EXTRA_MESSAGE = "com.example.santh.MESSAGE";
    //initializing titles
    private String[] titles = new String[]{
            "Application Name",
            "Developer",
            "Overview",
            "Features"

    };

    //initializing values for titles
    private String[] values = new String[]{
            "Shopping List",
            "Santhosh Raj Ravirala",
            "<b>Forget about Forgetting!!.</b><br/>" +
                    "Shopping List is the most easy way to organize your shopping lists." +
                    "You can manage multiple lists using this App . All products you enter in your shopping cart will later be available as suggestions so that you will save time when preparing your shopping list. " +
                    "Quantity, price are also added against each item and the overall price of the item and the list total price are shown ." +
                    "You can strike out items purchased while shopping with a click .You can share the list by email and whatsapp to family and friends." +
                    "This app allows you to sort the items both by category and alphabetically. You can also track the date on which the list is created." +
                    "Against each list number of items purchased in the list by total number of items in the list is shown  .you can delete all purchased items at once or all the items in the list by simple device shake. " +
                    "In addition the app has some new features -user can choose their preferred Currency,Sorting the lists based on total cost of the list with alphabetical sorting as well.Marking and Unmarking all items as purchased can be \n" +
                    "done by choosing mark all items and unmark all items respectively .Purchased Items can be hidden by Selecting hide Purchased Items so that they do not appear in the list .Item view  is divided into Section headers of category .\n" +
                    "You can also make a cow eat grass when bored  by clicking on the image in Item Activity which plays a cow grazing sound.",
            "1.Preference Settings for Choosing Currency.<br/>" +
                    "2.Multiple Sorting of  Lists based on Total cost of the list and alphabetically <br/>"+
                    "3.Hiding Purchased Items in the List with Multiple sorting of Items .<br/>" +
                    "4.Mark and Unmark all items in the List <br/>"




    };

    /**
     * oncreate Method for Info Activity where titles and values are mapped
     * @param savedInstanceState
     */

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        LinearLayout parent = (LinearLayout) findViewById(R.id.scrollParent);
        for (int i = 0; i < titles.length; i++) {
            //In addition to creating layouts through an XML file you can also create and add views
            //programmatically
            TextView title = new TextView(this);
            title.setText(Html.fromHtml("<b>" + titles[i] + "</b>"));
            title.setTextSize(16);
            parent.addView(title);

            TextView content = new TextView(this);
            //Converts the text to HTML
            content.setText(Html.fromHtml(values[i] + "<br>"));
            //Allows the user to follow any links in the text
            content.setMovementMethod(LinkMovementMethod.getInstance());
            parent.addView(content);
        }
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.

    }



    @Override
    public void onStart() {
        super.onStart();


    }

    @Override
    public void onStop() {
        super.onStop();


    }

    /**
     * onOptionsItemSelected to goback to MainActivity
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // System.out.println("back button clicked");
                Intent intent = getIntent();
                String strdata = intent.getExtras().getString("List");
                if (strdata.equals("main")) {
                    Intent intentMain = new Intent(this, MainActivity.class);
                    intentMain.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intentMain);

                }

                else if(strdata.equals("List"))
                {
                    Intent intentMain = new Intent(this, ListActivity.class);
                    intentMain.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    int ListId=intent.getIntExtra("ListID", 0);
                    String ListName=intent.getStringExtra("listname");

                    intentMain.putExtra("id",ListId);
                    intentMain.putExtra("listname", ListName);

                    startActivity(intentMain);
                }


        }
                return true;



    }

    /**
     * onBackPressed redirect to respective activity using intent
     */
    @Override
    public void onBackPressed() {

        // moveTaskToBack(true);
        Intent intent = getIntent();
        String strdata = intent.getExtras().getString("List");
        if (strdata.equals("main")) {
            Intent intentMain = new Intent(this, MainActivity.class);
            intentMain.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intentMain);

        }

        else if(strdata.equals("List"))
        {
            Intent intentMain = new Intent(this, ListActivity.class);
            intentMain.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            int ListId=intent.getIntExtra("ListID", 0);
            String ListName=intent.getStringExtra("listname");

            intentMain.putExtra("id",ListId);
            intentMain.putExtra("listname", ListName);

            startActivity(intentMain);
        }

        else if(strdata.equals("Item"))
        {
            Intent intentMain = new Intent(this, ItemActivity.class);
            intentMain.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            int ListId=intent.getIntExtra("ListID", 0);
            String ListName=intent.getStringExtra("listname");

            intentMain.putExtra("id",ListId);
            intentMain.putExtra("listname", ListName);
            startActivity(intentMain);
        }
       // super.onBackPressed();
    }


}
