package my.santh.shopping.shoppinglist;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.preference.PreferenceManager;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * NewListAdapter Class to Display List name and other parameters .It uses four text view Listname ,Date,List price
 * and count of purchased items/UnPurchaseditems. The inflater layout has these four Text views which are binded in this adapter class
 */


public class newListAdapter extends CursorAdapter {

    private ArrayList<String> list = new ArrayList<String>();
    private  ArrayList<String> aList;
    private Context context;
    ListView itemList;
    private List<Pair<Integer, String>> mData;
    List<Pair<Integer, String>> values;

   // MainActivity mn=new MainActivity();
    private ArrayAdapter<String> adapter;
    //DBHelper.FeedEntry.FeedReaderDbHelper mDbHelper;
    private ArrayList<Integer> listid=new ArrayList<Integer>();
    private LayoutInflater mLayoutInflater;
    private Context mContext;
    Cursor c;
    public newListAdapter(Context context, Cursor c) {
        super(context, c);
      this.mContext = context;
        this.c=c;
        mLayoutInflater = LayoutInflater.from(context);
    }

    /**
     * Inflating the layout with the view
     * @param context
     * @param cursor
     * @param parent
     * @return
     */
    @Override
    public View newView(final Context context, final Cursor cursor, ViewGroup parent) {
        View view = mLayoutInflater.inflate(R.layout.view_newlayout, parent, false);

        return view;
    }
    public long getItemId(int pos) {
        return 0;
        //     just return 0 if your list items do not have an Id variable.
    }

    /**
     *
     * Binding the view to Layout
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

    @Override
    public void bindView(View v, final Context context, final Cursor c) {
        String ListName = c.getString(c.getColumnIndexOrThrow("listname"));
        //String date = c.getString(c.getColumnIndexOrThrow(ItemDbAdapter.KEY_DATE));
        String Date = c.getString(c.getColumnIndexOrThrow("date"));
        DataBaseManager dbm =new DataBaseManager(context);
        dbm.open();
       // dbm = new DBHelper.FeedEntry.FeedReaderDbHelper(context);
       Cursor Purchased= dbm.getPurchasedList(c.getInt(c.getColumnIndexOrThrow("_id")));
Purchased.moveToFirst();

        String PurchasedItems=Purchased.getString(Purchased.getColumnIndexOrThrow("Purchased"));
        Purchased.close();
        Cursor UnPurchased= dbm.getUnPurchasedList(c.getInt(c.getColumnIndexOrThrow("_id")));

        UnPurchased.moveToFirst();
        String UnPurchasedItems=UnPurchased.getString(UnPurchased.getColumnIndexOrThrow("UnPurchased"));
        UnPurchased.close();
        Cursor Price = dbm.getPrice(c.getInt(c.getColumnIndexOrThrow("_id")));
        Price.moveToFirst();

        double sum =Price.getDouble(Price.getColumnIndexOrThrow("price"));
        Price.close();
dbm.close();

        TextView date_text = (TextView) v.findViewById(R.id.Date);
        if (date_text != null) {
            date_text.setText(Date);
        }

        TextView ListNa = (TextView) v.findViewById(R.id.Listname);
        if (ListNa != null) {
            ListNa.setText(ListName);
        }

        TextView comments = (TextView) v.findViewById(R.id.purchased);
        if (comments != null) {
            comments.setText(PurchasedItems+"/"+UnPurchasedItems);
        }
        SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(context);


        String currency  = SP.getString("Currency","1");

       // SharedPreferences sharedPreferences = context.getSharedPreferences("RadioBox", context.MODE_PRIVATE);
        //String currency=sharedPreferences.getString("RadioBox", "$");

        TextView pric = (TextView) v.findViewById(R.id.Price);
        if (pric != null) {
            pric.setText(currency+sum);
        }




    }

 /*   public void displayDataCustom() {
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
