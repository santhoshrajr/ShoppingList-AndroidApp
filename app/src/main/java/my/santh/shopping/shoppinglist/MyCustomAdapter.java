package my.santh.shopping.shoppinglist;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Paint;
import android.preference.PreferenceManager;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * My CustomAdapter Class to display all item information.It uses four text views Itemname ,Quantity, price,comments
 *  The inflater layout has these four Text views which are binded in this adapter class
 */


public class MyCustomAdapter extends CursorAdapter {

    private ArrayList<String> list = new ArrayList<String>();
    private  ArrayList<String> aList;
    private Context context;
    ListView itemList;
    private List<Pair<Integer, String>> mData;
    List<Pair<Integer, String>> values;

    MainActivity mn=new MainActivity();
    private ArrayAdapter<String> adapter;
    //DBHelper.FeedEntry.FeedReaderDbHelper mDbHelper;
    private ArrayList<Integer> listid=new ArrayList<Integer>();
    private LayoutInflater mLayoutInflater;
    private Context mContext;
    Cursor c;

    /**
     * Constructor function for MyCustomadapter
     * @param context
     * @param c
     */
    public MyCustomAdapter(Context context, Cursor c) {
        super(context, c);
      this.mContext = context;
        this.c=c;
        mLayoutInflater = LayoutInflater.from(context);
    }

    /**
     * Attaching View to the Adapter
     * @param context
     * @param cursor
     * @param parent
     * @return
     */
    @Override
    public View newView(final Context context, final Cursor cursor, ViewGroup parent) {
        View view = mLayoutInflater.inflate(R.layout.view_list_layout, parent, false);

        return view;
    }
    public long getItemId(int pos) {
        return 0;
        //     just return 0 if your list items do not have an Id variable.
    }

    /**
     * @author will
     *
     * @param   v
     *          The view in which the elements we set up here will be displayed.
     *
     * @param   context
     *          The running context where this ListView adapter will be active.
     *
     * @param   c
     *          The Cursor containing the query results we will display.
     */
    /**
     * Bind View to Layout
     * @param v
     * @param context
     * @param c
     */
    @Override
    public void bindView(View v, final Context context, final Cursor c) {
        String ItemName = c.getString(c.getColumnIndexOrThrow("itemname"));
        //String date = c.getString(c.getColumnIndexOrThrow(ItemDbAdapter.KEY_DATE));
        String comment = c.getString(c.getColumnIndexOrThrow("comments"));
        String unitname=c.getString(c.getColumnIndexOrThrow("unitname"));
        String price = c.getString(c.getColumnIndexOrThrow("price"));
        String Quantity = c.getString(c.getColumnIndexOrThrow("quantity"));
        int categoryid=c.getInt(c.getColumnIndexOrThrow("category_id"));
        int purchased=c.getInt(c.getColumnIndexOrThrow("purchased"));
        float number=Float.parseFloat(Quantity);
        float actualPrice=Float.parseFloat(price);
        DecimalFormat df = new DecimalFormat("#.###");
        df.setRoundingMode(RoundingMode.CEILING);
        double totalPrice=number*actualPrice;
        String pr=df.format(totalPrice);

        DataBaseManager dbm=new DataBaseManager(context);
        dbm.open();
        Cursor res=dbm.getcategoryname(categoryid);
        res.moveToFirst();
        String categoryname=res.getString(res.getColumnIndexOrThrow("categoryname"));
        dbm.close();

        TextView header = (TextView) v.findViewById(R.id.Sectionheader);

        String nextcategory=null;
        String newcategoryname=null;
        int newcatgeoryid;

        // get previous item's date, for comparison
        if (c.getPosition() > 0 && c.moveToPrevious()) {
            newcatgeoryid=c.getInt(c.getColumnIndexOrThrow("category_id"));
            dbm.open();
            Cursor resc=dbm.getcategoryname(newcatgeoryid);
            resc.moveToFirst();
             newcategoryname=resc.getString(resc.getColumnIndexOrThrow("categoryname"));
            dbm.close();

            //nextcategory = c.getString(c.getColumnIndexOrThrow("category_id"));
            c.moveToNext();
        }

        // enable section heading if it's the first one, or
        // different from the previous one
        if (categoryname == null || !categoryname.equals(newcategoryname)) {
            header.setVisibility(View.VISIBLE);
            header.setText(""+categoryname);
        } else {
            header.setVisibility(View.GONE);
        }


        SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(context);


        String currency  = SP.getString("Currency","1");


       // SharedPreferences sharedPreferences = context.getSharedPreferences("RadioBox", context.MODE_PRIVATE);
        //String currency=sharedPreferences.getString("RadioBox", "$");

        /**
         * Next set the title of the entry.
         */
        if(unitname.equals("Bottle"))
        {
            unitname="btl";
        }
        else if(unitname.equals("Gallon"))
        {
            unitname="gal";
        }
        else if (unitname.equals("Ounce"))
        {
            unitname="oz";
        }
        else if (unitname.equals("Packet"))
        {
            unitname="pkt";
        }

        else if (unitname.equals("Pound"))
        {
            unitname="lbs";
        }
        else if (unitname.equals("Quart"))
        {
            unitname="qrt";
        }
        else
        {
            unitname= Character.toLowerCase(unitname.charAt(0)) + unitname.substring(1);
            //dollarprice.setText("$/qrt");
           ;
        }
        TextView title_text = (TextView) v.findViewById(R.id.name);
        if (title_text != null) {
            title_text.setText(ItemName);
        }


        /**
         * Set Date
         */



        TextView date_text = (TextView) v.findViewById(R.id.Price);
        if (date_text != null) {
            date_text.setText(currency+pr);
        }

        TextView items = (TextView) v.findViewById(R.id.Quantity);
        if (items != null) {
            items.setText(Quantity+unitname);
        }

        TextView comments = (TextView) v.findViewById(R.id.comments);
        if (comments != null) {
            comments.setText(comment);
        }


        if(purchased==1) {

            //TextView text = (TextView) v.findViewById(R.id.name);
           title_text.setPaintFlags(title_text.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            comments.setVisibility(v.GONE);
            items.setVisibility(v.GONE);
            date_text.setVisibility(v.GONE);

        } else
        {
            title_text.setPaintFlags(title_text.getPaintFlags() &(~ Paint.STRIKE_THRU_TEXT_FLAG));
            comments.setVisibility(v.VISIBLE);
            items.setVisibility(v.VISIBLE);
            date_text.setVisibility(v.VISIBLE);

        }

/*
       if(categoryid==21)
        {
            //palevioletred
            v.setBackgroundColor(Color.parseColor("#DB7093"));


        }
        else if(categoryid==1)
        {
            //violet
            v.setBackgroundColor(Color.parseColor("#EE82EE"));
        }
        else if(categoryid==2)
        {
            //purple1
            v.setBackgroundColor(Color.parseColor("#9B30FF"));
        }
        else if(categoryid==3)
        {
            //turquoiseblue
            v.setBackgroundColor(Color.parseColor("#00C78C"));
        }
        else if(categoryid==4)
        {
            //springgreen
            v.setBackgroundColor(Color.parseColor("#00FF7F"));
        }
        else if(categoryid==5)
        {
            //greenyellow
            v.setBackgroundColor(Color.parseColor("#ADFF2F"));
        }
        else if(categoryid==6)
        {
            //orange 2
            v.setBackgroundColor(Color.parseColor("#EE9A00"));
        }else if(categoryid==7)
        {
            //	 	indianred
            v.setBackgroundColor(Color.parseColor("#CD5C5C"));
        }else if(categoryid==8)
        {
            //sgi olivedrab
            v.setBackgroundColor(Color.parseColor("#8E8E38"));
        }else if(categoryid==9)
        {
            //olive*
            v.setBackgroundColor(Color.parseColor("#808000"));
        }else if(categoryid==10)
        {
            //lightseagreen
            v.setBackgroundColor(Color.parseColor("#20B2AA"));
        }else if(categoryid==11)
        {
            //steelblue 2
            v.setBackgroundColor(Color.parseColor("#5CACEE"));
        }else if(categoryid==12)
        {
            //raspberry
            v.setBackgroundColor(Color.parseColor("#872657"));
        }else if(categoryid==13)
        {
            //darkturquoise
            v.setBackgroundColor(Color.parseColor("#00CED1"));
        }else if(categoryid==14)
        {
            //cadmiumyellow
            v.setBackgroundColor(Color.parseColor("#FF9912"));
        }else if(categoryid==15)
        {
            //seashell 4
            v.setBackgroundColor(Color.parseColor("#8B8682"));
        }else if(categoryid==16)
        {
            //flesh
            v.setBackgroundColor(Color.parseColor("#FF7D40"));
        }else if(categoryid==17)
        {
            //lightsalmon 4
            v.setBackgroundColor(Color.parseColor("#8B5742"));
        }else if(categoryid==18)
        {
            //tomato 4
            v.setBackgroundColor(Color.parseColor("#8B3626"));
        }else if(categoryid==19)
        {
            //olivedrab 2
            v.setBackgroundColor(Color.parseColor("#C0FF3E"));
        }else if(categoryid==20)
        {
            //mint
            v.setBackgroundColor(Color.parseColor("#BDFCC9"));
        }


*/




        /**
         * Decide if we should display the deletion indicator
         */

    }

   /* public void displayDataCustom() {
        List<Pair<Integer, String>> values;
        Cursor resItem ;
        Intent intent=((ListActivity)context).getIntent();
        int id=intent.getIntExtra("id", 0);

         resItem = mDbHelper.getDataItem(id);





       // resItem=mDbHelper.getDataItemCursor(Id);

        values=new ArrayList<Pair<Integer, String>>();
        aList = new ArrayList<String>();
        //pkID =new ArrayList<Integer>();

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
            }
            //instantiate custom adapter


        }
        notifyDataSetChanged();

    }*/



}
