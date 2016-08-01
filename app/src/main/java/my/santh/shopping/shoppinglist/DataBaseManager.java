package my.santh.shopping.shoppinglist;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.os.Build;

import org.json.JSONArray;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 *
 * DataBaseManager class file to edit delete update and query the database
 */
public class DataBaseManager {
    public static final int DATABASE_VERSION = 2;
    public static final String DATABASE_NAME = "ShoppingList.db";
    public static final String TABLE_NAME_LIST = "List";
    public static final String TABLE_NAME_UNIT = "Unit";
    public static final String TABLE_NAME_CATEGORY = "Category";
    public static final String COLUMN_NAME_LISTID = "listid";
    public static final String COLUMN_NAME_LISTNAME = "listname";
    public static final String TABLE_NAME_ITEM = "Item";
    public static final String COLUMN_NAME_ITEM_ID = "itemid";
    public static final String COLUMN_NAME_ITEMNAME = "itemname";
    public static final String COLUMN_NAME_UNIT_ID = "unitid";
    public static final String COLUMN_NAME_QUANTITY = "quantity";
    public static final String COLUMN_NAME_COMMENTS = "comments";
    public static final String COLUMN_NAME_CATEGORY_ID = "category_id";
    public static final String COLUMN_NAME_UNITNAME = "unitname";
    public static final String COLUMN_NAME_CATEGORYNAME = "categoryname";
    public static final String COLUMN_NAME_Price = "price";
    public static final String COLUMN_NAME_Purchased="purchased";
    public  static  final String COLUMN_NAME_DATE="date";


    private static final String TEXT_TYPE = " TEXT";
    private static final String COMMA_SEP = ",";
    //creating table LIST
    private static final String SQL_CREATE_LIST =
            "CREATE TABLE " + TABLE_NAME_LIST + " (" +
                    COLUMN_NAME_LISTID + " INTEGER PRIMARY KEY autoincrement not null," +
                    COLUMN_NAME_LISTNAME + TEXT_TYPE + COMMA_SEP +
                    COLUMN_NAME_DATE + " Date" + COMMA_SEP +
                    "deleted INTEGER " +

                    " )";
    //creating table Unit
    private static final String SQL_CREATE_UNIT =
            "CREATE TABLE " + TABLE_NAME_UNIT + " (" +
                    COLUMN_NAME_UNIT_ID + " INTEGER PRIMARY KEY autoincrement not null," +
                    COLUMN_NAME_UNITNAME + TEXT_TYPE +
                    " )";
    //initialzing table category
    private static final String SQL_CREATE_Category =
            "CREATE TABLE " + TABLE_NAME_CATEGORY + " (" +
                    COLUMN_NAME_CATEGORY_ID + " INTEGER PRIMARY KEY autoincrement not null," +
                    COLUMN_NAME_CATEGORYNAME + TEXT_TYPE +
                    " )";
    //initializing tbale item
    private static final String SQL_CREATE_ITEM =
            "CREATE TABLE " + TABLE_NAME_ITEM + " (" +
                    COLUMN_NAME_ITEM_ID + " INTEGER PRIMARY KEY autoincrement not null," +
                    COLUMN_NAME_LISTID + " INTEGER " + COMMA_SEP +
                    COLUMN_NAME_UNIT_ID + " INTEGER " + COMMA_SEP +
                    COLUMN_NAME_QUANTITY + " INTEGER " + COMMA_SEP +
                    COLUMN_NAME_COMMENTS + TEXT_TYPE + COMMA_SEP +
                    COLUMN_NAME_CATEGORY_ID + " INTEGER " + COMMA_SEP +
                    COLUMN_NAME_Price + " INTEGER " + COMMA_SEP +
                    COLUMN_NAME_ITEMNAME + TEXT_TYPE + COMMA_SEP +
                    COLUMN_NAME_Purchased + " Boolean " + COMMA_SEP +
                    "deleted INTEGER " + COMMA_SEP +

                    "FOREIGN KEY(" + COLUMN_NAME_LISTID + ") REFERENCES " +
                    TABLE_NAME_LIST + "(" + COLUMN_NAME_LISTID + ")" + COMMA_SEP +
                    "FOREIGN KEY(" + COLUMN_NAME_UNIT_ID + ") REFERENCES " +
                    TABLE_NAME_UNIT + "(" + COLUMN_NAME_UNIT_ID + ")" + COMMA_SEP +
                    "FOREIGN KEY(" + COLUMN_NAME_CATEGORY_ID + ") REFERENCES " +
                    TABLE_NAME_CATEGORY + "(" + COLUMN_NAME_CATEGORY_ID + ")" +

                    " )";
    //initializing table unit
    private static final String SQL_INSERT_UNIT = "INSERT INTO " + TABLE_NAME_UNIT +
            "(" + COLUMN_NAME_UNITNAME + ")" + " VALUES " +
            "(" + "'Bag'" + ")" + "," +
            "(" + "'Bottle'" + ")" + "," +
            "(" + "'Box'" + ")" + "," +
            "(" + "'Can'" + ")" + "," +
            "(" + "'Gallon'" + ")" + "," +
            "(" + "'Item'" + ")" + "," +
            "(" + "'Jar'" + ")" + "," +
            "(" + "'Ounce'" + ")" + "," +
            "(" + "'Pack'" + ")" + "," +
            "(" + "'Packet'" + ")" + "," +
            "(" + "'Pint'" + ")" + "," +
            "(" + "'Pound'" + ")" + "," +
            "(" + "'Quart'" + ");";


    //initlaizing table category
    private static final String SQL_INSERT_CATEGORY = "INSERT INTO " + TABLE_NAME_CATEGORY +
            "(" + COLUMN_NAME_CATEGORYNAME + ")" + " VALUES " +
            "(" + "'Alcohol'" + ")" + "," +
            "(" + "'Baby & Childcare'" + ")" + "," +
            "(" + "'Bacon & Sausages'" + ")" + "," +
            "(" + "'Beverages'" + ")" + "," +
            "(" + "'Bread & Bakery'" + ")" + "," +
            "(" + "'Canned Goods'" + ")" + "," +
            "(" + "'Clothing'" + ")" + "," +
            "(" + "'Coffee & Tea'" + ")" + "," +
            "(" + "'Dairy & Eggs'" + ")" + "," +
            "(" + "'Frozen Food'" + ")" + "," +
            "(" + "'Groceries'" + ")" + "," +
            "(" + "'Health & Beauty'" + ")" + "," +
            "(" + "'Household Essentials'" + ")" + "," +
            "(" + "'Meat'" + ")" + "," +
            "(" + "'Party Supplies'" + ")" + "," +
            "(" + "'Pets'" + ")" + "," +
            "(" + "'Pharmacy'" + ")" + "," +
            "(" + "'Poultry'" + ")" + "," +
            "(" + "'SeaFood'" + ")" + "," +
            "(" + "'Stationery'" + ")" + "," +
            "(" + "'Uncategorized'" + ");";

    private static final String Update_Create = "CREATE TABLE Lastmod (id INTEGER PRIMARY KEY AUTOINCREMENT,"+
            "Timestamp DATETIME DEFAULT CURRENT_TIMESTAMP)";
    private static final String SQL_DELETE_ENTRIES_LIST =
            "DROP TABLE IF EXISTS " + TABLE_NAME_LIST;

    private static final String SQL_DELETE_ENTRIES_ITEMS =
            "DROP TABLE IF EXISTS " + TABLE_NAME_ITEM;

    private static final String SQL_DELETE_ENTRIES_UNIT =
            "DROP TABLE IF EXISTS " + TABLE_NAME_UNIT;

    private static final String SQL_DELETE_ENTRIES_Category =
            "DROP TABLE IF EXISTS " + TABLE_NAME_CATEGORY;

    private static final String SQL_DELETE_Update_Item =
            "DROP TABLE IF EXISTS  Lastmod " ;

    private final Context context;

    private DatabaseHelper DBHelper;
    private SQLiteDatabase db;


    public DataBaseManager(Context ctx) {
        context = ctx;
        DBHelper = new DatabaseHelper(context);
    }
    private static class DatabaseHelper extends SQLiteOpenHelper
    {
        public static String DB_FILEPATH ;
        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(SQL_CREATE_LIST);
            db.execSQL(SQL_CREATE_ITEM);
            db.execSQL(SQL_CREATE_UNIT);
            db.execSQL(SQL_CREATE_Category);
            db.execSQL(SQL_INSERT_UNIT);
            db.execSQL(SQL_INSERT_CATEGORY);
            db.execSQL(Update_Create);
            String update = "REPLACE INTO Lastmod (id, Timestamp) VALUES (1, '2015-04-12 12:00:00')";
            db.execSQL(update);
            db.execSQL("PRAGMA foreign_keys=ON;");
        }

        /**
         * Upgrade Database version drop all tables and recreate
         *
         * @param db
         * @param oldVersion
         * @param newVersion
         */
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // This database is only a cache for online data, so its upgrade policy is
           // to simply to discard the data and start over
            db.execSQL(SQL_DELETE_ENTRIES_ITEMS);
            db.execSQL(SQL_DELETE_ENTRIES_LIST);

            db.execSQL(SQL_DELETE_ENTRIES_UNIT);
            db.execSQL(SQL_DELETE_ENTRIES_Category);
            db.execSQL(SQL_DELETE_Update_Item);
            onCreate(db);
        }

        /**
         * onDowngrade Method
         *
         * @param db
         * @param oldVersion
         * @param newVersion
         */
        public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            onUpgrade(db, oldVersion, newVersion);
        }

        /**
         * onConfigure for setting foeign key constraints
         *
         * @param database
         */
        public void onConfigure(SQLiteDatabase database) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                database.setForeignKeyConstraintsEnabled(true);
            } else {
                database.execSQL("PRAGMA foreign_keys=ON");
            }
        }
    }

    public DataBaseManager open() throws SQLException {
        db = DBHelper.getWritableDatabase();
        db.beginTransactionNonExclusive();
        return this;
    }

    public void close() {
        db.setTransactionSuccessful();
        db.endTransaction();
        DBHelper.close();


    }

    public String getUpdate(){
        String sql = "SELECT Timestamp FROM Lastmod WHERE id = 1";
        Cursor cursor = db.rawQuery(sql, null);
        cursor.moveToFirst();
        String retval =cursor.getString(cursor.getColumnIndex("Timestamp"));
        return retval;
    }

    public void incrementUpdate(){
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        String updateDate = dateFormat.format(date);
        String update = "REPLACE INTO Lastmod (id, Timestamp) VALUES (1,'"+updateDate+"')";
        db.execSQL(update);
    }

    private SQLiteStatement itemStatment = null;
    public void addItems(String id, String[] values){
        /*String sql = "REPLACE INTO "+"Plant"+" (id, name, family, genus, species, cultivar, tag, ebd) " +
                "VALUES " +
                "("+id+",'"+ values[0] + "',"+ values[1] + ","+ values[2] + ","+ values[3]
                + ","+ values[4] + ",'" + values[5] + "'," + values[6]+")";*/
        //db.beginTransaction();
        //        db.execSQL("delete from Item where UpdateStatus=1  ");
        if(itemStatment == null){
            String sql = "REPLACE INTO "+"Item"+" (itemid, itemname,price,quantity,comments,deleted,purchased, listid, unitid, category_id) " +
                    "VALUES " +
                    "(?,?,?,?,?,?,?,?,?,?)";
            // DBHelper.getWritableDatabase();
            itemStatment = db.compileStatement(sql);
        }

        itemStatment.bindString(1, id);
        itemStatment.bindString(2, ""+values[0]+"");
        itemStatment.bindString(3, ""+values[1]+"");//f
        itemStatment.bindString(4, ""+values[2]+"");//g
        itemStatment.bindString(5, ""+values[3]+"");//s
        itemStatment.bindString(6, values[4]);      //c
        itemStatment.bindString(7, ""+values[5]+"");//tag
        itemStatment.bindString(8, ""+values[6]+"");//ebd
        itemStatment.bindString(9, ""+values[7]+"");//del
        itemStatment.bindString(10,""+values[8]+"");

        itemStatment.execute();
        itemStatment.clearBindings();
        //System.out.println(sql);
        //db.execSQL(sql);
    }//end addPlant


    /**
     * Compose JSON out of SQLite records
     * @return
     */
    public JSONArray  composeItemJSONfromSQLite(){
        ArrayList<HashMap<String, String>> wordList;
        wordList = new ArrayList<HashMap<String, String>>();
        String selectQuery = "SELECT  * FROM Item where UpdateStatus = '"+"0"+"'";
        //SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {

                HashMap<String, String> map = new HashMap<String, String>();
                map.put(COLUMN_NAME_ITEM_ID, cursor.getString(0));
                map.put(COLUMN_NAME_ITEMNAME, cursor.getString(7));
                map.put(COLUMN_NAME_Price,cursor.getString(6));
                map.put(COLUMN_NAME_QUANTITY,cursor.getString(3));
                map.put(COLUMN_NAME_COMMENTS,cursor.getString(4));
                map.put(COLUMN_NAME_Purchased, cursor.getString(8));
                map.put(COLUMN_NAME_LISTID, cursor.getString(1));
                map.put(COLUMN_NAME_UNIT_ID, cursor.getString(2));
                map.put(COLUMN_NAME_CATEGORY_ID, cursor.getString(5));
                map.put("deleted", cursor.getString(9));
                wordList.add(map);
            } while (cursor.moveToNext());
        }
      //  database.close();
        JSONArray jsArray = new JSONArray(wordList);
        //Gson gson = new GsonBuilder().create();
        //Use GSON to serialize Array List to JSON
        return jsArray;
    }

    /**
     * Get Sync status of SQLite
     * @return
     */
    public String getItemSyncStatus(){
        String msg = null;
        if(this.dbItemSyncCount() == 0){
            msg = "SQLite and Remote MySQL DBs are in Sync!";
        }else{
            msg = "DB Sync neededn";
        }
        return msg;
    }

    /**
     * Get SQLite records that are yet to be Synced
     * @return
     */
    public int dbItemSyncCount(){
        int count = 0;
        String selectQuery = "SELECT  * FROM Item where UpdateStatus = '"+"0"+"'";
        //SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        count = cursor.getCount();
        //database.close();
        return count;
    }

    /**
     * Update Sync status against each User ID

     */
    public void updateitemSyncStatus(){
        //SQLiteDatabase database = this.getWritableDatabase();
        String updateQuery = "Update Item set UpdateStatus =1 where UpdateStatus =0" ;
     //   Log.d("query",updateQuery);
        db.execSQL(updateQuery);
       // database.close();
    }

    /**
     * Compose JSON out of SQLite records
     * @return
     */
    public JSONArray  composeListJSONfromSQLite(){
        ArrayList<HashMap<String, String>> wordList;
        wordList = new ArrayList<HashMap<String, String>>();
        String selectQuery = "SELECT  * FROM list where UpdateStatus = '"+"0"+"'";
        //SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
//androidid idnew=new androidid();
      //  String aid=idnew.generateID();
        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> map = new HashMap<String, String>();

                map.put("listid", cursor.getString(0));
                map.put("listname", cursor.getString(1));
                map.put("date",cursor.getString(2));
                map.put("deleted",cursor.getString(3));
                map.put("UpdateStatus",cursor.getString(4));
             //   map.put("androidid",aid);
                wordList.add(map);
            } while (cursor.moveToNext());
        }
        //  database.close();
        JSONArray jsArray = new JSONArray(wordList);
        //Gson gson = new GsonBuilder().create();
        //Use GSON to serialize Array List to JSON
        return jsArray;
    }

    /**
     * Get Sync status of SQLite
     * @return
     */
    public String getSyncStatus(){
        String msg = null;
        if(this.dbSyncCount() == 0){
            msg = "SQLite and Remote MySQL DBs are in Sync!";
        }else{
            msg = "DB Sync neededn";
        }
        return msg;
    }

    /**
     * Get SQLite records that are yet to be Synced
     * @return
     */
    public int dbSyncCount(){
        int count = 0;
        String selectQuery = "SELECT  * FROM list where UpdateStatus = '"+"0"+"'";
        //SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        count = cursor.getCount();
        //database.close();
        return count;
    }

    /**
     * Update Sync status against each User ID

     */
    public void updatelistSyncStatus(){
        //SQLiteDatabase database = this.getWritableDatabase();
        String updateQuery = "Update List set UpdateStatus =1 where UpdateStatus =0" ;
        //   Log.d("query",updateQuery);
        db.execSQL(updateQuery);
        // database.close();
    }




    private SQLiteStatement categoryStatment = null;
    public void addcategories(String id, String[] values){
        /*String sql = "REPLACE INTO "+"Plant"+" (id, name, family, genus, species, cultivar, tag, ebd) " +
                "VALUES " +
                "("+id+",'"+ values[0] + "',"+ values[1] + ","+ values[2] + ","+ values[3]
                + ","+ values[4] + ",'" + values[5] + "'," + values[6]+")";*/
        //db.beginTransaction();
        if(categoryStatment == null){
            String sql = "REPLACE INTO "+"Category"+" (category_id, categoryname) " +
                    "VALUES " +
                    "(?,?)";
            // DBHelper.getWritableDatabase();
            categoryStatment = db.compileStatement(sql);
        }

        categoryStatment.bindString(1, id);
        categoryStatment.bindString(2, ""+values[0]+"");

        categoryStatment.execute();
        categoryStatment.clearBindings();
        //System.out.println(sql);
        //db.execSQL(sql);
    }//end addPlant

    private SQLiteStatement unitStatment = null;
    public void addunits(String id, String[] values){
        /*String sql = "REPLACE INTO "+"Plant"+" (id, name, family, genus, species, cultivar, tag, ebd) " +
                "VALUES " +
                "("+id+",'"+ values[0] + "',"+ values[1] + ","+ values[2] + ","+ values[3]
                + ","+ values[4] + ",'" + values[5] + "'," + values[6]+")";*/
        //db.beginTransaction();
        if(unitStatment == null){
            String sql = "REPLACE INTO "+"unit"+" (unitid, unitname) " +
                    "VALUES " +
                    "(?,?)";
            // DBHelper.getWritableDatabase();
            unitStatment = db.compileStatement(sql);
        }

        unitStatment.bindString(1, id);
        unitStatment.bindString(2, ""+values[0]+"");

        unitStatment.execute();
        unitStatment.clearBindings();
        //System.out.println(sql);
        //db.execSQL(sql);
    }//end addPlant



    public Cursor getPrice(int ID)
    {


        Cursor res = db.rawQuery("Select round(sum(price * quantity),3) as price from item where listid = ? and purchased=0", new String[]{String.valueOf(ID)});
        //
        //

        return res;
    }

    public boolean UpdateDataItem(String ItemName, int ListID, int unitid, int categoryid, String Comments, float Quantity, float Price, int id) {
        // mDbHelper = new DBHelper.FeedReaderDbHelper(this);
        // Gets the data repository in write mode
        
        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME_LISTID, ListID);
        values.put(COLUMN_NAME_ITEMNAME, ItemName);
        values.put(COLUMN_NAME_UNIT_ID, unitid);
        values.put(COLUMN_NAME_CATEGORY_ID, categoryid);
        values.put(COLUMN_NAME_COMMENTS, Comments);
        values.put(COLUMN_NAME_QUANTITY, Quantity);
        values.put(COLUMN_NAME_Price, Price);
        // values.put(COLUMN_NAME_LISTID,listID);
        // values.put(DBHelper.COLUMN_NAME_CONTENT, content);

        // Insert the new row, returning the primary key value of the new row
        long newRowId;
        db.update(TABLE_NAME_ITEM, values, COLUMN_NAME_ITEM_ID + " =?", new String[]{String.valueOf(id)});

        //
        
        return true;


    }

    private SQLiteStatement listStatement = null;

    public void addList(String id, String[] values){
        /*String sql = "REPLACE INTO "+"Location"+" (id, name, descr, longitude, latitude, radius, landmark) " +
                "VALUES " +
                "("+id +",'"+ values[0] + "','"+ values[1] + "','"+ values[2] + "','"+ values[3] + "',"+ values[4] + "," + values[5]+")";
        */
        //db.beginTransaction();

        //db.execSQL("delete from Item where listid = "+ id +"");
        //db.execSQL("delete from List where listid = " + id + "");
        /*Cursor res=db.rawQuery("select listid from List where UpdateStatus=1" , null);
        res.moveToFirst();
        while (res.moveToNext()) {
            int listid=res.getInt(res.getColumnIndexOrThrow("listid"));

            int item=db.delete(TABLE_NAME_ITEM, COLUMN_NAME_LISTID + "=?", new String[]{String.valueOf(listid)});
            int i = db.delete(TABLE_NAME_LIST, COLUMN_NAME_LISTID + "=?", new String[]{String.valueOf(listid)});

        }*/

        if(listStatement == null){
            String sql = "REPLACE INTO "+"List"+" (" +COLUMN_NAME_LISTID +" , "+ COLUMN_NAME_LISTNAME +" , "+COLUMN_NAME_DATE+" , "+" deleted"+"  )  "+
                    "VALUES " +
                    "(?,?,?,?)";
            //DBHelper.getWritableDatabase();
            listStatement = db.compileStatement(sql);
        }

        listStatement.bindString(1, id);
        listStatement.bindString(2, "" + values[0] + "");
        listStatement.bindString(3, "" + values[1] + "");
        listStatement.bindString(4, "" + values[2] + "");
        //listStatement.bindString(5, "" + 1 + "");
       /* locationStatement.bindString(6, values[4]);
        locationStatement.bindString(7, values[5]);
        locationStatement.bindString(8, values[6]);*/
        listStatement.execute();
        listStatement.clearBindings();
        //db.execSQL(sql);
    }//end addLocation

    public void deletelistid(String id){

        db.execSQL("Delete from Item where listid = "+id);
        db.execSQL("DELETE FROM List where listid = "+ id);

    }

    public void deletecategoryid(String id)
    {
        db.execSQL("Delete from Item where category_id = "+id);
        db.execSQL("DELETE FROM Category where category_id = "+ id);
    }

    public void deleteunitid(String id)
    {
        db.execSQL("Delete from Item where unitid = "+id);
        db.execSQL("DELETE FROM Unit where unitid = "+ id);
    }


    /**
     *
     * Insert Function to insert values into table List
     * @param ListName
     * @return
     */
    public boolean insertDataList(String ListName) {

        // mDbHelper = new DBHelper.FeedReaderDbHelper(this);
        // Gets the data repository in write mode
        
        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        //values.put(DBHelper.COLUMN_NAME_LISTID, id);
        values.put(COLUMN_NAME_LISTNAME, ListName);

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        values.put(COLUMN_NAME_DATE,dateFormat.format(date));
    //    values.put("UpdateStatus",0);
        // ContentValues initialValues = new ContentValues();
        //initialValues.put("date_created", dateFormat.format(date));
        // values.put(DBHelper.COLUMN_NAME_CONTENT, content);

        // Insert the new row, returning the primary key value of the new row
        long newRowId;
        newRowId = db.insert(
                TABLE_NAME_LIST,
                null,
                values);

        if (newRowId == -1)
            return false;
        else
            return true;
        //
        //

    }

    /**
     *
     *get all the items and qunatitites to share the list through whatsapp and Email
     * @param ListId
     * @return
     */
    public Cursor  shareListdata(int ListId)
    {
        
        String query="SELECT categoryname,itemname,coalesce(quantity,'')" +
                " || coalesce(unitname,'') as quantity from item join Category on Item.category_id=Category.category_id " +
                "join Unit on item.unitid=Unit.unitid where " + COLUMN_NAME_LISTID +" = ?" +
                " order by Category.category_id ";
        Cursor res=db.rawQuery(query,new String[]{String.valueOf(ListId)});

       /* Cursor res=db.rawQuery("SELECT categoryname,itemname,coalesce(quantity,'')" +
                " || coalesce(unitname,'') as quantity from item join Category on Item.category_id=Category.category_id " +
                "join Unit on item.unitid=Unit.unitid where " + COLUMN_NAME_LISTID +" = " + ListId +
                " order by Category.category_id ",null);*/
        //
        //

        return  res;
    }

    /**
     *
     * Method to get the product suggestions on text changed
     * @param SearchTerm
     * @return
     */
    public List<String> read(String SearchTerm)
    {
        List<String> labels = new ArrayList<String>();
        
        Cursor res= db.rawQuery("Select itemname from Item where itemname LIKE '%"+ SearchTerm + "%'" +"ORDER BY itemid DESC LIMIT 0,5",null);

        if (res.moveToFirst()) {
            do {
                labels.add(res.getString(0));
            } while (res.moveToNext());
        }

        // closing connection



        // returning lables
        return labels;
    }


    /**
     * insertDataItem inserts dataitem to List
     *
     * @param ItemName
     * @return
     */
    public boolean insertDataItem(String ItemName, int ListID, int unitid, int categoryid, String Comments, float Quantity, float Price,int purchased) {
        // mDbHelper = new DBHelper.FeedReaderDbHelper(this);
        // Gets the data repository in write mode
        
        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME_LISTID, ListID);
        values.put(COLUMN_NAME_ITEMNAME, ItemName);
        values.put(COLUMN_NAME_UNIT_ID, unitid);
        values.put(COLUMN_NAME_CATEGORY_ID, categoryid);
        values.put(COLUMN_NAME_COMMENTS, Comments);
        values.put(COLUMN_NAME_QUANTITY, Quantity);
        values.put(COLUMN_NAME_Price, Price);
        values.put(COLUMN_NAME_Purchased,purchased);
        //values.put("UpdateStatus",0);
        // values.put(COLUMN_NAME_LISTID,listID);
        // values.put(DBHelper.COLUMN_NAME_CONTENT, content);

        // Insert the new row, returning the primary key value of the new row
        long newRowId;
        newRowId = db.insert(
                TABLE_NAME_ITEM,
                null,
                values);

        if (newRowId == -1)
            return false;
        else
            return true;

        //
        // 
    }

    public Cursor getListID(String ListName) {
        
        Cursor res = db.rawQuery("Select listID from List where listname= ?", new String[]{ListName});
        // 
        //  

        return res;
    }



    /**
     * Cursor to retrieve data Items
     *
     * @return
     */
    public Cursor getDataItem(int ID) {
        


        Cursor res = db.rawQuery("Select * from Item where " + COLUMN_NAME_LISTID + "=" + ID, null);
        // 
        //

        return res;
    }

    /**
     * Cursor to retrieve data Items
     *
     * @return
     */
    public Cursor getDataItemCursor(int ID) {
        

        String query="Select " + COLUMN_NAME_ITEM_ID + " AS _id " + " , " + COLUMN_NAME_ITEMNAME + " , "
                + COLUMN_NAME_UNITNAME + " , " + COLUMN_NAME_Price + " , " + COLUMN_NAME_QUANTITY +
                " , " + COLUMN_NAME_COMMENTS + " , " + COLUMN_NAME_CATEGORY_ID + " , " + COLUMN_NAME_Purchased + " " +
                "from Item Join Unit on " + TABLE_NAME_ITEM + "." + COLUMN_NAME_UNIT_ID + " = " +
                TABLE_NAME_UNIT + "." + COLUMN_NAME_UNIT_ID +
                "  where " + COLUMN_NAME_LISTID + "= ?"  +" ORDER BY "+ COLUMN_NAME_Purchased;
        Cursor res = db.rawQuery(query,new String[]{String.valueOf(ID)});

        /*Cursor res = db.rawQuery("Select " + COLUMN_NAME_ITEM_ID + " AS _id " + " , " + COLUMN_NAME_ITEMNAME + " , "
                + COLUMN_NAME_UNITNAME + " , " + COLUMN_NAME_Price + " , " + COLUMN_NAME_QUANTITY +
                " , " + COLUMN_NAME_COMMENTS + " , " + COLUMN_NAME_CATEGORY_ID + " , " + COLUMN_NAME_Purchased + " " +
                "from Item Join Unit on " + TABLE_NAME_ITEM + "." + COLUMN_NAME_UNIT_ID + " = " +
                TABLE_NAME_UNIT + "." + COLUMN_NAME_UNIT_ID +
                "  where " + COLUMN_NAME_LISTID + "=" + ID +" ORDER BY "+ COLUMN_NAME_Purchased , null);*/
        // 
        //

        return res;
    }


    /**
     * Delete all records in table
     *
     * @return
     */
    public int deleteAll() {
        
        db.delete(TABLE_NAME_ITEM, null, null);
        return db.delete(TABLE_NAME_LIST,null,null);
    }

    /**
     *
     * Delete all items in the List
     * @param id
     * @return
     */
    public int deleteItems(int id) {
        

        return db.delete(TABLE_NAME_ITEM, COLUMN_NAME_LISTID + "= ? ", new String[]{String.valueOf(id)});
    }

    /**
     *
     * Delete all Purchased Items
     * @param id
     */
    public int deletePurchasedItems(int id) {
        
       // db.execSQL("delete from "+ TABLE_NAME_ITEM +" where "+COLUMN_NAME_Purchased +" = 1 and "+COLUMN_NAME_LISTID+" = "+id);
         return db.delete(TABLE_NAME_ITEM, COLUMN_NAME_LISTID + "= ? " +" AND "+COLUMN_NAME_Purchased +" =1", new String[]{String.valueOf(id)});

        // 
        //

    }


    /**
     * Delete by ID in Table
     *
     * @param id
     * @return
     */
    public Integer delete_ItembyID (int id) {
        
        int i = db.delete(TABLE_NAME_ITEM, COLUMN_NAME_ITEM_ID + "=?", new String[]{String.valueOf(id)});
        //
        //

        return i;
    }

    /**
     *
     * Delete List by ID
     * @param id
     * @return
     */
    public Integer delete_ListbyID(int id) {
        
        int item=db.delete(TABLE_NAME_ITEM, COLUMN_NAME_LISTID + "=?", new String[]{String.valueOf(id)});
        int i = db.delete(TABLE_NAME_LIST, COLUMN_NAME_LISTID + "=?", new String[]{String.valueOf(id)});
        // 
        //

        return i;
    }

    /**
     * Update ItemName in Database
     *
     * @param itemname
     * @param original
     * @return
     */
    public boolean updatedata(String itemname, String original) {
        
        ContentValues con = new ContentValues();
        con.put(COLUMN_NAME_ITEMNAME, itemname);
        db.update(TABLE_NAME_ITEM, con, COLUMN_NAME_ITEMNAME + "=?", new String[]{original});
        // 
        //

        return true;
    }

    /**
     * Update List Name
     * @param itemname
     * @param original
     * @return
     */
    public boolean updateListdata(String itemname, String original) {
        
        ContentValues con = new ContentValues();
        con.put(COLUMN_NAME_LISTNAME, itemname);
        db.update(TABLE_NAME_LIST, con, COLUMN_NAME_LISTNAME + "=?", new String[]{original});
        //
        //

        return true;
    }

    /**
     * Update Item as Purchased
     * @param id1
     * @return
     */
    public boolean updateItemPurchased(int id1)
    {
        
        ContentValues con = new ContentValues();
        con.put(COLUMN_NAME_Purchased, 1);
        db.update(TABLE_NAME_ITEM, con, COLUMN_NAME_ITEM_ID + "=?", new String[]{String.valueOf(id1)});

        //
        //

        return true;
    }

    /**
     * Update Item as Not purchased
     * @param id1
     * @return
     */
    public boolean updateItemNotPurchased(int id1)
    {
        
        ContentValues con = new ContentValues();
        con.put(COLUMN_NAME_Purchased, 0);
        db.update(TABLE_NAME_ITEM, con, COLUMN_NAME_ITEM_ID + "=?", new String[] {String.valueOf(id1)});
        //
        //

        return true;
    }
    /**
     * SortData to arrange items in list
     *
     * @return
     */

    public Cursor sortdata(int Id) {

        String query="Select " + COLUMN_NAME_ITEM_ID + " AS _id " + " , " + COLUMN_NAME_ITEMNAME + " , "
                + COLUMN_NAME_UNITNAME + " , " + COLUMN_NAME_Price + " , " + COLUMN_NAME_QUANTITY + " , " + COLUMN_NAME_COMMENTS + " , "+COLUMN_NAME_CATEGORY_ID + " , " + COLUMN_NAME_Purchased + " " +
                "from Item Join Unit on " + TABLE_NAME_ITEM + "." + COLUMN_NAME_UNIT_ID + " = " +
                TABLE_NAME_UNIT + "." + COLUMN_NAME_UNIT_ID +
                "  where " + COLUMN_NAME_LISTID + " = ?" + " ORDER BY " + COLUMN_NAME_Purchased +
                " , "  +  COLUMN_NAME_ITEMNAME
                + " COLLATE NOCASE;";

        Cursor res = db.rawQuery(query,new String[]{String.valueOf(Id)});
        
      /*  Cursor res = db.rawQuery("Select " + COLUMN_NAME_ITEM_ID + " AS _id " + " , " + COLUMN_NAME_ITEMNAME + " , "
                + COLUMN_NAME_UNITNAME + " , " + COLUMN_NAME_Price + " , " + COLUMN_NAME_QUANTITY + " , " + COLUMN_NAME_COMMENTS + " , "+COLUMN_NAME_CATEGORY_ID + " , " + COLUMN_NAME_Purchased + " " +
                "from Item Join Unit on " + TABLE_NAME_ITEM + "." + COLUMN_NAME_UNIT_ID + " = " +
                TABLE_NAME_UNIT + "." + COLUMN_NAME_UNIT_ID +
                "  where " + COLUMN_NAME_LISTID + "=" + Id + " ORDER BY " + COLUMN_NAME_Purchased +
                " , " + COLUMN_NAME_ITEMNAME
                + " COLLATE NOCASE;", null);*/


               /* Cursor res = db.rawQuery("SELECT * "
                        + " FROM " + TABLE_NAME_ITEM +" Where "+ COLUMN_NAME_LISTID +" = "+ Id
                        + " ORDER BY " + COLUMN_NAME_ITEMNAME
                        + " COLLATE NOCASE;", null);*/
        //  
        //

        return res;
    }


    /**
     *
     * Sort Items By Category
     * @param Id
     * @return
     */
    public Cursor sortcategory(int Id) {

        String query="Select " + COLUMN_NAME_ITEM_ID + " AS _id " + " , " + COLUMN_NAME_ITEMNAME + " , "
                + COLUMN_NAME_UNITNAME + " , " + COLUMN_NAME_Price + " , " + COLUMN_NAME_QUANTITY + " , " + COLUMN_NAME_COMMENTS + " , "+COLUMN_NAME_CATEGORY_ID + " , " + COLUMN_NAME_Purchased + " " +
                "from Item Join Unit on " + TABLE_NAME_ITEM + "." + COLUMN_NAME_UNIT_ID + " = " +
                TABLE_NAME_UNIT + "." + COLUMN_NAME_UNIT_ID +
                "  where " + COLUMN_NAME_LISTID + " = ?" + " ORDER BY " + COLUMN_NAME_Purchased +
                " , " + COLUMN_NAME_CATEGORY_ID
                + " COLLATE NOCASE;";

        Cursor res = db.rawQuery(query,new String[]{String.valueOf(Id)});
        
       /* Cursor res = db.rawQuery("Select " + COLUMN_NAME_ITEM_ID + " AS _id " + " , " + COLUMN_NAME_ITEMNAME + " , "
                + COLUMN_NAME_UNITNAME + " , " + COLUMN_NAME_Price + " , " + COLUMN_NAME_QUANTITY + " , " + COLUMN_NAME_COMMENTS + " , "+COLUMN_NAME_CATEGORY_ID + " , " + COLUMN_NAME_Purchased + " " +
                "from Item Join Unit on " + TABLE_NAME_ITEM + "." + COLUMN_NAME_UNIT_ID + " = " +
                TABLE_NAME_UNIT + "." + COLUMN_NAME_UNIT_ID +
                "  where " + COLUMN_NAME_LISTID + "=" + Id + " ORDER BY " + COLUMN_NAME_Purchased +
                " ,"+COLUMN_NAME_CATEGORY_ID
                + " COLLATE NOCASE;", null);*/


               /* Cursor res = db.rawQuery("SELECT * "
                        + " FROM " + TABLE_NAME_ITEM +" Where "+ COLUMN_NAME_LISTID +" = "+ Id
                        + " ORDER BY " + COLUMN_NAME_ITEMNAME
                        + " COLLATE NOCASE;", null);*/
        //
        //

        return res;
    }

    public Cursor sortshowall(int Id)
    {
        String query="Select " + COLUMN_NAME_ITEM_ID + " AS _id " + " , " + COLUMN_NAME_ITEMNAME + " , "
                + COLUMN_NAME_UNITNAME + " , " + COLUMN_NAME_Price + " , " + COLUMN_NAME_QUANTITY + " , " + COLUMN_NAME_COMMENTS + " , "+COLUMN_NAME_CATEGORY_ID + " , " + COLUMN_NAME_Purchased + " " +
                "from Item Join Unit on " + TABLE_NAME_ITEM + "." + COLUMN_NAME_UNIT_ID + " = " +
                TABLE_NAME_UNIT + "." + COLUMN_NAME_UNIT_ID +
                "  where " + COLUMN_NAME_LISTID + " = ?" + " and purchased =0 ORDER BY " + COLUMN_NAME_Purchased +
                " , " + COLUMN_NAME_CATEGORY_ID + " , "+ COLUMN_NAME_ITEMNAME
                + " COLLATE NOCASE;";

        Cursor res = db.rawQuery(query,new String[]{String.valueOf(Id)});
        return  res;
    }

    public Cursor sortshow010(int Id)
    {
        String query="Select " + COLUMN_NAME_ITEM_ID + " AS _id " + " , " + COLUMN_NAME_ITEMNAME + " , "
                + COLUMN_NAME_UNITNAME + " , " + COLUMN_NAME_Price + " , " + COLUMN_NAME_QUANTITY + " , " + COLUMN_NAME_COMMENTS + " , "+COLUMN_NAME_CATEGORY_ID + " , " + COLUMN_NAME_Purchased + " " +
                "from Item Join Unit on " + TABLE_NAME_ITEM + "." + COLUMN_NAME_UNIT_ID + " = " +
                TABLE_NAME_UNIT + "." + COLUMN_NAME_UNIT_ID +
                "  where " + COLUMN_NAME_LISTID + " = ?" + " and purchased =0 ORDER BY " + COLUMN_NAME_Purchased +
                  " , "+ COLUMN_NAME_ITEMNAME
                + " COLLATE NOCASE;";

        Cursor res = db.rawQuery(query,new String[]{String.valueOf(Id)});
        return  res;
    }


    public Cursor sortshow100(int Id)
    {
        String query="Select " + COLUMN_NAME_ITEM_ID + " AS _id " + " , " + COLUMN_NAME_ITEMNAME + " , "
                + COLUMN_NAME_UNITNAME + " , " + COLUMN_NAME_Price + " , " + COLUMN_NAME_QUANTITY + " , " + COLUMN_NAME_COMMENTS + " , "+COLUMN_NAME_CATEGORY_ID + " , " + COLUMN_NAME_Purchased + " " +
                "from Item Join Unit on " + TABLE_NAME_ITEM + "." + COLUMN_NAME_UNIT_ID + " = " +
                TABLE_NAME_UNIT + "." + COLUMN_NAME_UNIT_ID +
                "  where " + COLUMN_NAME_LISTID + " = ?" + " and purchased=0  ORDER BY " + COLUMN_NAME_Purchased +
                " , " + COLUMN_NAME_CATEGORY_ID
                + " COLLATE NOCASE;";

        Cursor res = db.rawQuery(query,new String[]{String.valueOf(Id)});
        return  res;
    }


    public Cursor sortshow110(int Id)
    {
        String query="Select " + COLUMN_NAME_ITEM_ID + " AS _id " + " , " + COLUMN_NAME_ITEMNAME + " , "
                + COLUMN_NAME_UNITNAME + " , " + COLUMN_NAME_Price + " , " + COLUMN_NAME_QUANTITY + " , " + COLUMN_NAME_COMMENTS + " , "+COLUMN_NAME_CATEGORY_ID + " , " + COLUMN_NAME_Purchased + " " +
                "from Item Join Unit on " + TABLE_NAME_ITEM + "." + COLUMN_NAME_UNIT_ID + " = " +
                TABLE_NAME_UNIT + "." + COLUMN_NAME_UNIT_ID +
                "  where " + COLUMN_NAME_LISTID + " = ?" + " and purchased=0 ORDER BY " + COLUMN_NAME_Purchased
                + " COLLATE NOCASE;";

        Cursor res = db.rawQuery(query,new String[]{String.valueOf(Id)});
        return  res;
    }


    public void strikeallitems(int id) {
        ContentValues con = new ContentValues();
        con.put(COLUMN_NAME_Purchased, 1);
        db.update(TABLE_NAME_ITEM, con, COLUMN_NAME_LISTID + "=?", new String[]{String.valueOf(id)});
    }

    public void unstrikeallitems(int id) {
        ContentValues con = new ContentValues();
        con.put(COLUMN_NAME_Purchased, 0);
        db.update(TABLE_NAME_ITEM, con, COLUMN_NAME_LISTID + "=?", new String[]{String.valueOf(id)});
    }




    public Cursor sortcategorydata(int Id) {

        String query="Select " + COLUMN_NAME_ITEM_ID + " AS _id " + " , " + COLUMN_NAME_ITEMNAME + " , "
                + COLUMN_NAME_UNITNAME + " , " + COLUMN_NAME_Price + " , " + COLUMN_NAME_QUANTITY + " , " + COLUMN_NAME_COMMENTS + " , "+COLUMN_NAME_CATEGORY_ID + " , " + COLUMN_NAME_Purchased + " " +
                "from Item Join Unit on " + TABLE_NAME_ITEM + "." + COLUMN_NAME_UNIT_ID + " = " +
                TABLE_NAME_UNIT + "." + COLUMN_NAME_UNIT_ID +
                "  where " + COLUMN_NAME_LISTID + " = ?" + " ORDER BY " + COLUMN_NAME_Purchased +
                " , " + COLUMN_NAME_CATEGORY_ID + " , "+ COLUMN_NAME_ITEMNAME
                + " COLLATE NOCASE;";

        Cursor res = db.rawQuery(query,new String[]{String.valueOf(Id)});
        /*Cursor res = db.rawQuery("Select " + COLUMN_NAME_ITEM_ID + " AS _id " + " , " + COLUMN_NAME_ITEMNAME + " , "
                + COLUMN_NAME_UNITNAME + " , " + COLUMN_NAME_Price + " , " + COLUMN_NAME_QUANTITY + " , " + COLUMN_NAME_COMMENTS + " , "+COLUMN_NAME_CATEGORY_ID + " , " + COLUMN_NAME_Purchased + " " +
                "from Item Join Unit on " + TABLE_NAME_ITEM + "." + COLUMN_NAME_UNIT_ID + " = " +
                TABLE_NAME_UNIT + "." + COLUMN_NAME_UNIT_ID +
                "  where " + COLUMN_NAME_LISTID + "=" + Id + " ORDER BY " + COLUMN_NAME_Purchased +
                " , " + COLUMN_NAME_CATEGORY_ID + " , "+ COLUMN_NAME_ITEMNAME
                + " COLLATE NOCASE;", null);*/
        //
        //

               /* Cursor res = db.rawQuery("SELECT * "
                        + " FROM " + TABLE_NAME_ITEM +" Where "+ COLUMN_NAME_LISTID +" = "+ Id
                        + " ORDER BY " + COLUMN_NAME_ITEMNAME
                        + " COLLATE NOCASE;", null);*/

        return res;
    }

    /**
     * Sort Lists Alphabetically
     * @return
     */
    public Cursor sortLists() {
        
        Cursor res = db.rawQuery("Select " + COLUMN_NAME_LISTID + " AS _id " + " , " + COLUMN_NAME_LISTNAME + " , "
                + COLUMN_NAME_DATE + " From List "
                + " ORDER BY " + COLUMN_NAME_LISTNAME
                + " COLLATE NOCASE;", null);
        //
        //

        return res;

    }

    public Cursor sortListsPrice() {


        Cursor res =db.rawQuery("SELECT * FROM ( SELECT list.listid AS _id , listname, date, ROUND( SUM( price * quantity ) , 3 ) AS price FROM list" +
                       " LEFT JOIN item ON list.listid = item.listid " +
                        "WHERE (purchased =0 or purchased is null) " +
       " " +
        "GROUP BY list.listid " +

               " UNION " +
        "select list.listid, listname, DATE,0 AS price "+
        "FROM list " +
        " LEFT JOIN item ON list.listid = item.listid " +
        "Where purchased is not null " +

         " " +
         " GROUP BY list.listid " +

        "having count(*) =sum(purchased) "+
        ")a "+
        "ORDER BY price DESC ",null);
       

      /*  Cursor res = db.rawQuery("Select " +TABLE_NAME_LIST+"."+COLUMN_NAME_LISTID + " AS _id " + " , " + COLUMN_NAME_LISTNAME + " , "
                + COLUMN_NAME_DATE + " From List left join Item on List.listid=Item.listid " +" where (purchased=0 or purchased is null) GROUP BY list.listid "
                + " ORDER BY  round(sum(price * quantity),3)"
                + " ;", null);*/
        //
        //

        return res;

    }
 /*   SELECT list.listid, listname, DATE
    FROM list
    JOIN item ON list.listid = item.itemid
    WHERE purchased =0
    GROUP BY list.listid
    ORDER BY ROUND( SUM( price * quantity ) , 3 )
    LIMIT 0 , 30*/

    public Cursor sortListsPriceAlpha() {

        Cursor res=db.rawQuery("SELECT * " +
                "FROM (" +
                "" +
                "SELECT list.listid AS _id, listname, date , ROUND( SUM( price * quantity ) , 3 ) AS price " +
                "FROM list " +
                "LEFT JOIN item ON list.listid = item.listid " +
                "WHERE (purchased =0 or purchased is null) " +
                " " +
                "GROUP BY list.listid " +
                "" +
                "UNION " +
                "select list.listid, listname, DATE,0 AS price " +
                "FROM list " +
                " LEFT JOIN item ON list.listid = item.listid " +
                "" +
                "Where purchased is not null" +

                " " +
                "GROUP BY list.listid " +
                " " +
                "having count(*) =sum(purchased) " +
                ")a " +
                "ORDER BY price DESC ,listname ",null);

       /* Cursor res = db.rawQuery("Select " + TABLE_NAME_LIST+"."+COLUMN_NAME_LISTID + " AS _id " + " , " + COLUMN_NAME_LISTNAME + " , "
                + COLUMN_NAME_DATE + " From List left join Item on List.listid=Item.listid " +" where (purchased=0 or purchased is null) GROUP BY list.listid "
                + " ORDER BY " + COLUMN_NAME_LISTNAME +" , round(sum(price * quantity),3)"
                + " COLLATE NOCASE ; ", null);*/
        //
        //

        return res;

    }


    /**
     * Get Count of items Purchased in the List
     * @param id
     * @return
     */
    public Cursor getPurchasedList(int id)
    {
        String query="Select count(purchased) as Purchased from " +
                TABLE_NAME_ITEM + " where " + COLUMN_NAME_LISTID + " = ? "+" and purchased =1";
        Cursor res = db.rawQuery(query, new String[]{String.valueOf(id)});

        //

        return res;
    }

    /**
     * Get Count of Items which are not Yet Purchased
     * @param id
     * @return
     */
    public Cursor getUnPurchasedList(int id)
    {
        List<String> labels = new ArrayList<String>();
        String query="Select count(purchased) as UnPurchased from " +
                TABLE_NAME_ITEM + " where " + COLUMN_NAME_LISTID + " = ? ";
        Cursor res = db.rawQuery(query, new String[]{String.valueOf(id)});
        //

        return res;
    }


    /**
     * CheckIfDataitem Exists
     *
     * @param itemName
     * @return
     */
    public boolean checkdata(String itemName,int id) {
        
        String query = "SELECT * "
                + " FROM " + TABLE_NAME_ITEM
                + " where " + COLUMN_NAME_ITEMNAME +" = ? and " +COLUMN_NAME_LISTID +" = ?"
                ;
        Cursor res = db.rawQuery(query, new String[]{itemName,String.valueOf(id)});
        if (res.getCount() <= 0) {

            return false;
        }
        //
        //

        return true;
    }

    /**
     * Check if a List Name Exists
     * @param itemName
     * @return
     */
    public boolean checkListdata(String itemName) {
        
        String query = "SELECT * "
                + " FROM " + TABLE_NAME_LIST
                + " where " + COLUMN_NAME_LISTNAME
                + " = ?";
        Cursor res = db.rawQuery(query, new String[] {itemName });
        if (res.getCount() <= 0) {

            return false;
        }
        //

        return true;
    }

    /**
     * get all List Names
     * @return
     */
    public Cursor getLists() {
        
        Cursor res = db.rawQuery("Select " + COLUMN_NAME_LISTID + " AS _id " + " , " + COLUMN_NAME_LISTNAME + " , "
                + COLUMN_NAME_DATE +
                " from List ORDER BY " + COLUMN_NAME_DATE +" DESC ", null);

        //Cursor res = db.rawQuery("Select * from List", null);
        //
        //

        return res;
    }

    /**
     * Get all Units
     * @return
     */
    public List<String> getUnits() {
        List<String> labels = new ArrayList<String>();
        


        Cursor res = db.rawQuery("Select * from Unit", null);
        // looping through all rows and adding to list
        if (res.moveToFirst()) {
            do {
                labels.add(res.getString(1));
            } while (res.moveToNext());
        }

        // closing connection

        // 

        // returning lables

        return labels;
    }


    public Cursor getunitname(int unitid)
    {
        Cursor res=db.rawQuery("Select unitname from unit where unitid = "+unitid,null);
        return  res;
    }

    public Cursor getcategoryname(int categoryid)
    {
        Cursor res=db.rawQuery("Select categoryname from category where category_id = "+categoryid,null);
        return  res;
    }

    public Cursor getunitid(String unitname)
    {
        Cursor res=db.rawQuery("Select unitid from unit where unitname = ? ",new String[]{unitname});
        return  res;
    }

    public Cursor getcategoryid(String categoryname)
    {
        Cursor res=db.rawQuery("Select category_id from category where categoryname = ? ",new String[]{categoryname});
        return  res;
    }



    /**
     * Get all categories
     * @return
     */
    public List<String> getCategory() {
        List<String> labels = new ArrayList<String>();
        


        Cursor res = db.rawQuery("Select * from Category", null);
        // looping through all rows and adding to list
        if (res.moveToFirst()) {
            do {
                labels.add(res.getString(1));
            } while (res.moveToNext());
        }

        // closing connection

        //

        // returning lables

        return labels;

    }

    /**
     * Get all Item Details
     * @param ID
     * @return
     */
    public Cursor getItemDetails(int ID) {
        

String query ="Select * from item where itemid = ?";
        Cursor res = db.rawQuery(query, new String[]{String.valueOf(ID)});
        //
        //

        return res;

    }

}
