package app.ddc.lged.emcrp.connectivity;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class DBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "EMCRP_2020.db";
    public static final String PACKAGE = "Packages";
    public static final String SUBPACKAGE = "Subpackages";
    public static final String USER_TABLE_NAME = "Users";
    public static final String PROJECT_TABLE_SUBMISSION = "Submission";
    public static final String PROJECT_TABLE_FEEDBACK = "Feedback";
    public static final String PROJECT_TABLE_GALLERY = "ImageGallery";
    public static final String TABLE_MAJORTASK = "Majortasks";
    public static final String TABLE_SUBTASK = "Subtasks";
    Context context;
    private HashMap hp;
    SharedPreferences sharedPref;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME , null, 10);
    }

    private static final String CREATE_TABLE_PACKAGE = "CREATE TABLE " + PACKAGE
            + "(id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT,alias TEXT,group_id INTEGER,tid INTEGER,flag INTEGER, date DATETIME)";

    private static final String CREATE_TABLE_SUBPACKAGE = "CREATE TABLE " + SUBPACKAGE
            + "(id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT,internal_code TEXT,alias TEXT,package_id INTEGER,tid INTEGER, date DATETIME)";

    // Projects table create statement
    private static final String CREATE_TABLE_USERS = "CREATE TABLE " + USER_TABLE_NAME
            + "(id INTEGER PRIMARY KEY AUTOINCREMENT, userid INTEGER, name TEXT, contact TEXT, username TEXT, password TEXT, organization TEXT, designation TEXT, district INTEGER, pin INTEGER, date DATETIME)";


    // Projects table create statement
    private static final String CREATE_TABLE_SUBMISION = "CREATE TABLE " + PROJECT_TABLE_SUBMISSION
            + "(" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "refid INTEGER, " +
            "userid INTEGER, " +
            "package_id INTEGER, " +
            "subpackage_id INTEGER, " +
            "ShelterCode TEXT, " +
            "MajorTasks TEXT, " +
            "Tasks TEXT, " +
            "Comments TEXT, " +
            "Latitude TEXT, " +
            "Longitude TEXT, " +
            "Area TEXT, " +
            "Status TEXT, " +
            "SendServer INTEGER, " +
            "SubmisionDate DATETIME)";


    private static final String CREATE_TABLE_MAJORTASK = "CREATE TABLE " + TABLE_MAJORTASK
            + "(" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "tid INTEGER, " +
            "package_id TEXT, " +
            "name TEXT, " +
            "details TEXT, " +
            "Flag INTEGER)";

    private static final String CREATE_TABLE_SUBTASK = "CREATE TABLE " + TABLE_SUBTASK
            + "(" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "tid INTEGER, " +
            "majortask_id TEXT, " +
            "name TEXT, " +
            "details TEXT, " +
            "Flag INTEGER)";


    private static final String CREATE_TABLE_GALLERY = "CREATE TABLE " + PROJECT_TABLE_GALLERY
            + "(" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "userid INTEGER, " +
            "project_id INTEGER, " +
            "package_id INTEGER, " +
            "subid INTEGER, " +
            "shelter TEXT, " +
            "Image1 TEXT, " +
            "Caption1 TEXT, " +
            "Image2 TEXT, " +
            "Caption2 TEXT, " +
            "Image3 TEXT, " +
            "Caption3 TEXT, " +
            "Image4 TEXT, " +
            "Caption4 TEXT, " +
            "Image5 TEXT, " +
            "Caption5 TEXT, " +
            "task TEXT, " +
            "date DATETIME)";

    ///////////// Feedback form table ////////
    private static final String CREATE_TABLE_FEEDBACK = "CREATE TABLE " + PROJECT_TABLE_FEEDBACK
            + "(" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "refid INTEGER, " +
            "userid INTEGER, " +
            "subid INTEGER, " +
            "shelter TEXT, " +
            "mtask TEXT, " +
            "subdate DATETIME, " +
            "hqfeed TEXT, " +
            "status INTEGER, " +
            "hqfeeddate DATETIME, " +
            "userfeed TEXT, " +
            "userfeeddate DATETIME, " +
            "userimg TEXT)";


    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub
        db.execSQL(CREATE_TABLE_USERS);
        db.execSQL(CREATE_TABLE_GALLERY);
        db.execSQL(CREATE_TABLE_PACKAGE);
        db.execSQL(CREATE_TABLE_SUBPACKAGE);
        db.execSQL(CREATE_TABLE_SUBMISION);
        db.execSQL(CREATE_TABLE_FEEDBACK);
        db.execSQL(CREATE_TABLE_MAJORTASK);
        db.execSQL(CREATE_TABLE_SUBTASK);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        //db.execSQL("DROP TABLE IF EXISTS Projects");
        db.execSQL("DROP TABLE IF EXISTS " + USER_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + PACKAGE);
        db.execSQL("DROP TABLE IF EXISTS " + SUBPACKAGE);
        db.execSQL("DROP TABLE IF EXISTS " + PROJECT_TABLE_SUBMISSION);
        db.execSQL("DROP TABLE IF EXISTS " + PROJECT_TABLE_FEEDBACK);
        db.execSQL("DROP TABLE IF EXISTS " + PROJECT_TABLE_GALLERY);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MAJORTASK);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SUBTASK);
        onCreate(db);
    }







    /////// Insert ///////////
    public boolean insertAllSyncData(HashMap<String, String> shelterValues,HashMap<String, String> taskValues) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", shelterValues.get("name"));
        values.put("group_id", shelterValues.get("group_id"));
        values.put("alias", shelterValues.get("alias"));
        database.insert(PACKAGE, null, values);

        ContentValues sValues = new ContentValues();
        sValues.put("internal_code", shelterValues.get("internal_code"));
        sValues.put("package_id", shelterValues.get("package_id"));
        sValues.put("alias", shelterValues.get("alias"));
        database.insert(TABLE_MAJORTASK, null, sValues);

        ContentValues tVal = new ContentValues();
        tVal.put("package_id", taskValues.get("package_id"));
        tVal.put("name", taskValues.get("name"));
        tVal.put("details", taskValues.get("details"));
        tVal.put("Flag", taskValues.get("flag"));
        database.insert(SUBPACKAGE, null, tVal);

        ContentValues subVal = new ContentValues();
        subVal.put("majortask_id", taskValues.get("majortask_id"));
        subVal.put("name", taskValues.get("name"));
        subVal.put("details", taskValues.get("details"));
        subVal.put("Flag", taskValues.get("flag"));
        database.insert(TABLE_SUBTASK, null, subVal);

        database.close();
        return true;
    }

    ///////////////////////// Major Tasks ///////////////////////////////////
    public boolean insertTask(HashMap<String, String> queryValues) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("tid", queryValues.get("tid"));
        values.put("package_id", queryValues.get("package_id"));
        values.put("name", queryValues.get("name"));
        values.put("details", queryValues.get("details"));
        values.put("Flag", queryValues.get("flag"));
        database.insert(TABLE_MAJORTASK, null, values);
        database.close();
        return true;
    }

    /////// Get data with id ///////////
    public Cursor checkExistingTask(int code) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select id from " + TABLE_MAJORTASK + " where tid ='"+code+"'", null );
        //db.close();
        return res;
    }

    public boolean updateTask(Integer id,String package_id,String name,String details,Integer flag) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("package_id", package_id);
        contentValues.put("name", name);
        contentValues.put("details", details);
        contentValues.put("Flag", flag);

        db.update(TABLE_MAJORTASK, contentValues, "id = ? ", new String[] { Integer.toString(id) } );
        return true;
    }


    public Cursor getAllTasks(int pid) {
        //String sql = "SELECT * FROM " + TABLE_MAJORTASK + " WHERE package_id='all' AND (package_id LIKE '%"+pid+"%' || package_id LIKE '%"+pid+"' || package_id LIKE '%"+pid+"') ORDER BY name ASC";

        String sql = "SELECT * FROM " + TABLE_MAJORTASK;
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery(sql, null);
    }

    public Cursor getSelectedMajortask(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from " + TABLE_MAJORTASK + " where tid="+id+"", null );
        //res.close();
        return res;
    }


    public Cursor getAllSubTasks(int mtask) {
        String sql = "SELECT * FROM " + TABLE_SUBTASK + " WHERE majortask_id = '"+mtask+"' ORDER BY Flag ASC";
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery(sql, null);
    }


    public ArrayList<String> getAllTasks1() {
        ArrayList<String> array_list = new ArrayList<String>();
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT * FROM " + SUBPACKAGE + " ORDER BY Flag ASC";
        Cursor res = db.rawQuery(sql, null);
        res.moveToFirst();

        while(res.isAfterLast() == false){
            array_list.add(res.getString(res.getColumnIndex("name")));
            res.moveToNext();
        }
        res.close();
        return array_list;
    }



    public boolean deleteAllTasks () {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(SUBPACKAGE, null, null);
        return true;
    }

    /* ====================================== Packages All Query ================================================*/

    /////// Insert ///////////
    public boolean insertPackages(HashMap<String, String> queryValues) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("tid", queryValues.get("tid"));
        values.put("name", queryValues.get("name"));
        values.put("alias", queryValues.get("alias"));
        values.put("group_id", queryValues.get("group_id"));
        values.put("Flag", queryValues.get("flag"));
        database.insert(PACKAGE, null, values);
        database.close();
        return true;
    }
    /////// Get data with id ///////////
    public Cursor checkExisting(String name) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from " + PACKAGE + " where name='"+name+"'", null );
        //db.close();
        return res;
    }

    public Cursor getSelectedPackage(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from " + PACKAGE + " where tid="+id+"", null );
        //res.close();
        return res;
    }

    public Cursor getSelectedSubPackage(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from " + SUBPACKAGE + " where tid="+id+"", null );
        //res.close();
        return res;
    }

    public boolean updateShelter(Integer id,Integer tid,String name,String alias,Integer flag,Integer group_id) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("tid", tid);
        contentValues.put("name", name);
        contentValues.put("alias", alias);
        contentValues.put("group_id", group_id);
        contentValues.put("Flag", flag);
        db.update(PACKAGE, contentValues, "id = ? ", new String[] { Integer.toString(id) } );
        return true;
    }

    public Cursor getPackageData() {
        String sql = "SELECT * FROM " + PACKAGE + " ORDER BY name ASC";
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery(sql, null);
    }


    public ArrayList<String> getLikeShelter(String key, int did) {
        ArrayList<String> array_list = new ArrayList<String>();

        //hp = new HashMap();
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = null;
        sql = "SELECT name,alias FROM " + PACKAGE + " WHERE alias LIKE '%" + key + "%'";
        Cursor res = db.rawQuery(sql, null);
        res.moveToFirst();

        while(res.isAfterLast() == false){
            //array_list.add(res.getString(res.getColumnIndex(CONTACTS_COLUMN_NAME)));
            array_list.add(res.getString(res.getColumnIndex("code")));
            res.moveToNext();
        }
        res.close();
        return array_list;
    }

    public ArrayList<String> getAllShelter() {
        ArrayList<String> array_list = new ArrayList<String>();

        //hp = new HashMap();
        SQLiteDatabase db = this.getReadableDatabase();
        //Cursor res =  db.rawQuery( "select * from Projects", null );
        Cursor res = db.rawQuery("SELECT name,alias FROM " + PACKAGE, null);
        res.moveToFirst();

        while(res.isAfterLast() == false){
            //array_list.add(res.getString(res.getColumnIndex(CONTACTS_COLUMN_NAME)));
            array_list.add(res.getString(res.getColumnIndex("name")));
            array_list.add(res.getString(res.getColumnIndex("alias")));
            res.moveToNext();
        }
        res.close();
        return array_list;
    }

    /////// Count Total Data ///////////
    public int numberOfRows(){
        SQLiteDatabase db = this.getReadableDatabase();
        int numRows = (int) DatabaseUtils.queryNumEntries(db, PACKAGE);
        return numRows;
    }
    /////// Delete one Data ///////////
    public Integer deletePackage(Integer id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(PACKAGE,
                "id = ? ",
                new String[] { Integer.toString(id) });
    }
    /////// Delete All Data ///////////
    public boolean deleteAllShelter () {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(PACKAGE, null, null);
        return true;
    }


    ///////////////////////// Sub SubTasks ///////////////////////////////////
    public boolean insertSubTask(HashMap<String, String> queryValues) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("tid", queryValues.get("id"));
        values.put("majortask_id", queryValues.get("majortask_id"));
        values.put("name", queryValues.get("name"));
        values.put("details", queryValues.get("details"));
        values.put("Flag", queryValues.get("flag"));
        database.insert(TABLE_SUBTASK, null, values);
        database.close();
        return true;
    }

    /////// Get data with id ///////////
    public Cursor checkExistingSubTask(int code) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select id from " + TABLE_SUBTASK + " where tid ='"+code+"'", null );
        //db.close();
        return res;
    }

    public Cursor getSelectedSubtask(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from " + TABLE_SUBTASK + " where tid="+id+"", null );
        //res.close();
        return res;
    }

    public boolean updateSubTask(Integer id,Integer package_id,String name,String details,Integer flag) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("package_id", package_id);
        contentValues.put("name", name);
        contentValues.put("details", details);
        contentValues.put("Flag", flag);

        db.update(TABLE_SUBTASK, contentValues, "id = ? ", new String[] { Integer.toString(id) } );
        return true;
    }


    public Cursor getAllSubTasks() {
        String sql = "SELECT * FROM " + TABLE_SUBTASK + " ORDER BY Flag ASC";
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery(sql, null);
    }

    public Cursor getAllSubSubTasks(Integer mtask) {
        String sql = "SELECT * FROM " + TABLE_SUBTASK + " WHERE majortask_id = '"+mtask+"' ORDER BY Flag ASC";
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery(sql, null);
    }


    public ArrayList<String> getAllSubTasks1() {
        ArrayList<String> array_list = new ArrayList<String>();
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT * FROM " + TABLE_SUBTASK + " ORDER BY Flag ASC";
        Cursor res = db.rawQuery(sql, null);
        res.moveToFirst();

        while(res.isAfterLast() == false){
            array_list.add(res.getString(res.getColumnIndex("MajorSubTasks")));
            res.moveToNext();
        }
        res.close();
        return array_list;
    }



    public boolean deleteAllSubTasks () {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_SUBTASK, null, null);
        return true;
    }


    /* ====================================== Subpackages All Query ================================================*/

    /////// Insert ///////////
    public boolean insertSubpackages(HashMap<String, String> queryValues) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("tid", queryValues.get("tid"));
        values.put("internal_code", queryValues.get("internal_code"));
        values.put("alias", queryValues.get("alias"));
        values.put("name", queryValues.get("name"));
        values.put("package_id", queryValues.get("package_id"));
        database.insert(SUBPACKAGE, null, values);
        database.close();
        return true;
    }
    /////// Get data with id ///////////
    public Cursor checkExistingSub(String internal_code) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from " + SUBPACKAGE + " where internal_code='"+internal_code+"'", null );
        //db.close();
        return res;
    }

    public boolean updateSubpackage(Integer id,Integer tid,String internal_code,String name,String alias,Integer package_id) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("tid", tid);
        contentValues.put("internal_code", internal_code);
        contentValues.put("alias", alias);
        contentValues.put("name", name);
        contentValues.put("package_id", package_id);
        db.update(SUBPACKAGE, contentValues, "id = ? ", new String[] { Integer.toString(id) } );
        return true;
    }

    public Cursor getSubPackageData(int id) {
        String sql = "SELECT * FROM " + SUBPACKAGE + " where package_id="+id+" ORDER BY internal_code ASC";
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery(sql, null);
    }

    public ArrayList<String> getLikeSubpackage(String key, int did) {
        ArrayList<String> array_list = new ArrayList<String>();

        //hp = new HashMap();
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = null;
        sql = "SELECT internal_code,alias FROM " + SUBPACKAGE + " WHERE alias LIKE '%" + key + "%'";
        Cursor res = db.rawQuery(sql, null);
        res.moveToFirst();

        while(res.isAfterLast() == false){
            //array_list.add(res.getString(res.getColumnIndex(CONTACTS_COLUMN_internal_code)));
            array_list.add(res.getString(res.getColumnIndex("code")));
            res.moveToNext();
        }
        res.close();
        return array_list;
    }

    public ArrayList<String> getAllSubpackage() {
        ArrayList<String> array_list = new ArrayList<String>();

        //hp = new HashMap();
        SQLiteDatabase db = this.getReadableDatabase();
        //Cursor res =  db.rawQuery( "select * from Projects", null );
        Cursor res = db.rawQuery("SELECT internal_code,alias FROM " + SUBPACKAGE, null);
        res.moveToFirst();

        while(res.isAfterLast() == false){
            //array_list.add(res.getString(res.getColumnIndex(CONTACTS_COLUMN_internal_code)));
            array_list.add(res.getString(res.getColumnIndex("internal_code")));
            array_list.add(res.getString(res.getColumnIndex("alias")));
            res.moveToNext();
        }
        res.close();
        return array_list;
    }

    /////// Count Total Data ///////////
    public int numberOfRowsS(){
        SQLiteDatabase db = this.getReadableDatabase();
        int numRows = (int) DatabaseUtils.queryNumEntries(db, SUBPACKAGE);
        return numRows;
    }
    /////// Delete one Data ///////////
    public Integer deleteSubpackage (Integer id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(SUBPACKAGE,
                "id = ? ",
                new String[] { Integer.toString(id) });
    }
    /////// Delete All Data ///////////
    public boolean deleteAllSubpackage () {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(SUBPACKAGE, null, null);
        return true;
    }


    /* ====================================== Submittd Form All Query ================================================*/
    /////// Insert ///////////
    public boolean insertSubmittedForm(HashMap<String, String> queryValues) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("refid", queryValues.get("refid"));
        values.put("userid", queryValues.get("userid"));
        values.put("ShelterCode", queryValues.get("shelid"));
        values.put("MajorTasks", queryValues.get("mtask"));
        values.put("Tasks", queryValues.get("task"));
        values.put("Comments", queryValues.get("ucom"));
        values.put("Latitude", queryValues.get("latv"));
        values.put("Longitude", queryValues.get("longv"));
        values.put("SubmisionDate", queryValues.get("SubmisionDate"));
        values.put("Status", "New");
        values.put("SendServer", 1);

        database.insert(PROJECT_TABLE_SUBMISSION, null, values);
        database.close();
        return true;
    }

    /////// Insert ///////////
    public boolean insertSaveForm(HashMap<String, String> queryValues) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());

        values.put("userid", queryValues.get("userid"));
        values.put("package_id", queryValues.get("package_id"));
        values.put("subpackage_id", queryValues.get("subpackage_id"));
        values.put("ShelterCode", queryValues.get("sheltercode"));
        values.put("MajorTasks", queryValues.get("mtaskid"));
        values.put("Tasks", queryValues.get("staskid"));
        values.put("Comments", queryValues.get("ucom"));
        values.put("Latitude", queryValues.get("latv"));
        values.put("Longitude", queryValues.get("longv"));
        values.put("Area", queryValues.get("area"));
        values.put("SubmisionDate", date);
        values.put("Status", "New");
        values.put("SendServer", 0);

        database.insert(PROJECT_TABLE_SUBMISSION, null, values);
        database.close();
        return true;
    }


    /////// Get data with id ///////////
    public Cursor checkExistingSub(int refid) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from " + PROJECT_TABLE_SUBMISSION + " where refid="+refid+"", null );
        //db.close();
        return res;
    }
    /////// Get data with id ///////////
    public Cursor getDataSubmissionDetails(long id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from " + PROJECT_TABLE_SUBMISSION + " where id="+id+"", null );
        //res.close();
        return res;
    }


    public Cursor getDataSUbmittedForm(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from " + PROJECT_TABLE_SUBMISSION + " where id="+id+"", null );
        //res.close();
        return res;
    }


    public Cursor getDataMultiplrForm(String itemsid) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from " + PROJECT_TABLE_SUBMISSION + " where id in ("+ itemsid +") ", null );
        //res.close();
        return res;
    }

    public ArrayList<String> getAllLikeForms(int userid) {
        ArrayList<String> array_list = new ArrayList<String>();

        //hp = new HashMap();
        SQLiteDatabase db = this.getReadableDatabase();
        //Cursor res =  db.rawQuery( "select * from Projects", null );
        Cursor res = db.rawQuery("SELECT * FROM " + PROJECT_TABLE_SUBMISSION + " WHERE userid = '" + userid + "'", null);
        res.moveToFirst();

        while(res.isAfterLast() == false){
            //array_list.add(res.getString(res.getColumnIndex(CONTACTS_COLUMN_NAME)));
            array_list.add(res.getString(res.getColumnIndex("ShelterCode")));
            res.moveToNext();
        }
        res.close();
        return array_list;
    }

    public ArrayList<String> getAllFormsArray() {
        ArrayList<String> array_list = new ArrayList<String>();
        //hp = new HashMap();
        SQLiteDatabase db = this.getReadableDatabase();
        //Cursor res =  db.rawQuery( "select * from Projects", null );
        Cursor res = db.rawQuery("SELECT * FROM " + PROJECT_TABLE_SUBMISSION, null);
        res.moveToFirst();

        while(res.isAfterLast() == false){
            //array_list.add(res.getString(res.getColumnIndex(CONTACTS_COLUMN_NAME)));
            array_list.add(res.getString(res.getColumnIndex("refid")));
            res.moveToNext();
        }
        return array_list;
    }

    public Cursor getAllForms(int status,int userid) {
        String sql = "SELECT * FROM " + PROJECT_TABLE_SUBMISSION + " WHERE userid = '" + userid + "' AND SendServer = '" + status + "' ORDER BY id DESC";
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery(sql, null);
    }

    public int getLastSubmissionId() {
        int id = 0;
        String sql = "SELECT * FROM " + PROJECT_TABLE_SUBMISSION;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery(sql, null);
        if(res.moveToLast()){
            id = res.getInt(0);//to get id, 0 is the column index
        }
        //res.close();
        return id;
    }
    /////// Update Data ///////////
    public boolean updateSUbmittedForm (int id,int refid) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("SendServer", 1);
        contentValues.put("refid", refid);
        db.update(PROJECT_TABLE_SUBMISSION, contentValues, "id = ? ", new String[] { Integer.toString(id) } );
        return true;
    }

    public boolean updateSubmission (HashMap<String, String> queryValues,int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("refid", queryValues.get("refid"));
        values.put("userid", queryValues.get("userid"));
        values.put("ShelterCode", queryValues.get("shelid"));
        values.put("MajorTasks", queryValues.get("mtask"));
        values.put("Tasks", queryValues.get("task"));
        values.put("Comments", queryValues.get("ucom"));
        values.put("Latitude", queryValues.get("latv"));
        values.put("Longitude", queryValues.get("longv"));
        values.put("SubmisionDate", queryValues.get("SubmisionDate"));
        values.put("Status", "New");
        values.put("SendServer", 1);

        db.update(PROJECT_TABLE_SUBMISSION, values, "id = ? ", new String[] { Integer.toString(id) } );
        return true;
    }
    public boolean updateSaveSub (HashMap<String, String> queryValues,int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());

        ContentValues values = new ContentValues();
        values.put("userid", queryValues.get("userid"));
        values.put("package_id", queryValues.get("package_id"));
        values.put("subpackage_id", queryValues.get("subpackage_id"));
        values.put("ShelterCode", queryValues.get("sheltercode"));
        values.put("MajorTasks", queryValues.get("mtask"));
        values.put("Tasks", queryValues.get("stask"));
        values.put("Comments", queryValues.get("ucom"));
        values.put("SubmisionDate", date);
        values.put("Status", "New");
        values.put("SendServer", 0);

        db.update(PROJECT_TABLE_SUBMISSION, values, "refid = ? ", new String[] { Integer.toString(id) } );
        return true;
    }
    /////// Count Total Data ///////////
    public int numberOfRowsSUbmittedForm(){
        SQLiteDatabase db = this.getReadableDatabase();
        int numRows = (int) DatabaseUtils.queryNumEntries(db, PROJECT_TABLE_SUBMISSION);
        return numRows;
    }

    /////// Count Total Data ///////////
    public int totalPendingForm(int uid, int id){
        String sql = "SELECT * FROM " + PROJECT_TABLE_SUBMISSION + " WHERE SendServer = '"+ id +"' AND userid = '"+ uid +"'";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery(sql, null);
        res.moveToLast();
        res.close();
        db.close();

        if(res.getCount() > 0) {
            int totalid = res.getCount();
            return totalid;
        }
        else{
            return 0;
        }


    }
    /////// Delete one Data ///////////
    /*public Integer deleteSUbmittedForm (Integer id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(PROJECT_TABLE_SUBMISSION,
                "id = ? ",
                new String[] { Integer.toString(id) });
    }*/
    public int deleteSUbmittedForm (long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(PROJECT_TABLE_SUBMISSION,"id = " + id,null);
    }
    /////// Delete All Data ///////////
    public boolean deleteAllSUbmittedForm () {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(PROJECT_TABLE_SUBMISSION, null, null);
        return true;
    }



    /* ====================================== Users Form All Query ================================================*/

    public Cursor loginWithPin(String upin) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from " + USER_TABLE_NAME + " where pin='"+upin+"'", null );
        //db.close();
        return res;
    }


    public Cursor checkExistingUser(String code) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from " + USER_TABLE_NAME + " where userid='"+code+"'", null );
        //db.close();
        return res;
    }

    public boolean updateUsers(HashMap<String, String> queryValues,Integer id) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());

        values.put("userid", queryValues.get("userid"));
        values.put("name", queryValues.get("name"));
        values.put("contact", queryValues.get("contact"));
        values.put("username", queryValues.get("username"));
        values.put("password", queryValues.get("pass"));
        values.put("organization", queryValues.get("org"));
        values.put("designation", queryValues.get("desig"));
        values.put("district", queryValues.get("district"));
        values.put("pin", queryValues.get("pin"));
        values.put("date", date);
        db.update(USER_TABLE_NAME, values, "id = ? ", new String[] { Integer.toString(id) } );
        return true;
    }

    /////////////////////////////// Insert ///////////////////////////////
    public boolean insertUser(HashMap<String, String> queryValues) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());

        values.put("userid", queryValues.get("userid"));
        values.put("name", queryValues.get("name"));
        values.put("contact", queryValues.get("contact"));
        values.put("username", queryValues.get("username"));
        values.put("password", queryValues.get("pass"));
        values.put("organization", queryValues.get("org"));
        values.put("designation", queryValues.get("desig"));
        values.put("district", queryValues.get("district"));
        values.put("pin", queryValues.get("pin"));
        values.put("date", date);

        database.insert(USER_TABLE_NAME, null, values);
        database.close();
        return true;
    }


    /////// Get data with id ///////////
    public Cursor getDataUsers(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from " + USER_TABLE_NAME + " where userid="+id+"", null );
        return res;
    }


    public ArrayList<String> getAllLikeUsers(int userid) {
        ArrayList<String> array_list = new ArrayList<String>();
        //hp = new HashMap();
        SQLiteDatabase db = this.getReadableDatabase();
        //Cursor res =  db.rawQuery( "select * from Projects", null );
        Cursor res = db.rawQuery("SELECT * FROM " + USER_TABLE_NAME + " WHERE userid = '" + userid + "'", null);
        res.moveToFirst();

        while(res.isAfterLast() == false){
            array_list.add(res.getString(res.getColumnIndex("username")));
            res.moveToNext();
        }
        res.close();
        return array_list;
    }

    public ArrayList<String> getAllUsers() {
        ArrayList<String> array_list = new ArrayList<String>();

        //hp = new HashMap();
        SQLiteDatabase db = this.getReadableDatabase();
        //Cursor res =  db.rawQuery( "select * from Projects", null );
        Cursor res = db.rawQuery("SELECT code FROM " + USER_TABLE_NAME, null);
        res.moveToFirst();

        while(res.isAfterLast() == false){
            //array_list.add(res.getString(res.getColumnIndex(CONTACTS_COLUMN_NAME)));
            array_list.add(res.getString(res.getColumnIndex("username")));
            res.moveToNext();
        }
        return array_list;
    }
    /////// Update Data ///////////
    public boolean updateUsers (Integer id, String name, String code) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("name", name);
        contentValues.put("code", code);
        db.update(USER_TABLE_NAME, contentValues, "id = ? ", new String[] { Integer.toString(id) } );
        return true;
    }
    /////// Count Total Data ///////////
    public int numberOfRowsUsers(){
        SQLiteDatabase db = this.getReadableDatabase();
        int numRows = (int) DatabaseUtils.queryNumEntries(db, USER_TABLE_NAME);
        return numRows;
    }
    /////// Delete one Data ///////////
    public Integer deleteUsers (Integer id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(USER_TABLE_NAME,
                "id = ? ",
                new String[] { Integer.toString(id) });
    }
    /////// Delete All Data ///////////
    public boolean deleteAllUsers () {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(USER_TABLE_NAME, null, null);
        return true;
    }




    /* ====================================== Feedback form All Query ================================================*/
    /////////////////////////////// Insert ///////////////////////////////
    public boolean insertFeedback(HashMap<String, String> queryValues) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("refid", queryValues.get("refid"));
        values.put("userid", queryValues.get("userid"));
        values.put("shelter", queryValues.get("shelter"));
        values.put("mtask", queryValues.get("mtask"));
        values.put("subid", queryValues.get("subid"));
        values.put("subdate", queryValues.get("subdate"));
        values.put("hqfeeddate", queryValues.get("hqfeeddate"));
        values.put("hqfeed", queryValues.get("hqfeed"));
        values.put("status", queryValues.get("hqstatus"));
        values.put("userfeed", queryValues.get("userfeed"));
        //values.put("userimg", queryValues.get("userimg"));
        values.put("userfeeddate", queryValues.get("userfeeddate"));

        database.insert(PROJECT_TABLE_FEEDBACK, null, values);
        database.close();
        return true;
    }


    public int totalUserFeedback(int id){
        String sql = "SELECT * FROM " + PROJECT_TABLE_FEEDBACK + " WHERE userid = '"+ id +"' GROUP BY subid";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery(sql, null);
        res.moveToLast();
        if(res.getCount() > 0) {
            int totalid = res.getCount();
            return totalid;
        }
        else{
            return 0;
        }
    }

    /////// Get data with id ///////////
    public Cursor checkExistingFeed(int subid) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from " + PROJECT_TABLE_FEEDBACK + " where refid="+subid+"", null );
        //db.close();
        return res;
    }

    public Cursor checkExistingFeedback(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from " + PROJECT_TABLE_FEEDBACK + " where refid="+id+"", null );
        //db.close();
        return res;
    }


    public boolean updateUserFeedback (HashMap<String, String> queryValues,int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("subid",  queryValues.get("subid"));
        values.put("status",  queryValues.get("status"));
        values.put("userfeed", queryValues.get("userfeed"));
        values.put("userfeeddate", queryValues.get("userfeeddate"));
        values.put("userimg", queryValues.get("userimg"));

        db.update(PROJECT_TABLE_FEEDBACK, values, "refid = ? ", new String[] { Integer.toString(id) } );
        db.close();
        return true;
    }

    public boolean updateFeedback (HashMap<String, String> queryValues,int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("refid", queryValues.get("refid"));
        values.put("shelter", queryValues.get("shelter"));
        values.put("mtask", queryValues.get("mtask"));
        values.put("subdate", queryValues.get("subdate"));
        values.put("userid", queryValues.get("userid"));
        values.put("hqfeeddate", queryValues.get("hqfeeddate"));
        values.put("subid", queryValues.get("subid"));
        values.put("hqfeed", queryValues.get("hqfeed"));
        values.put("status", queryValues.get("hqstatus"));
        values.put("userfeed", queryValues.get("userfeed"));
        values.put("userimg", queryValues.get("userimg"));
        values.put("userfeeddate", queryValues.get("userfeeddate"));

        db.update(PROJECT_TABLE_FEEDBACK, values, "refid = ? ", new String[] { Integer.toString(id) } );
        db.close();
        return true;
    }

    /////// Get data with id ///////////
    public Cursor getDataFeedback(int userid) {
        //String sql = "SELECT * FROM " + PROJECT_TABLE_SUBMISSION + " WHERE userid = '" + userid + "' AND SendServer = '" + status + "'";
        //String sql = "SELECT * FROM " + PROJECT_TABLE_FEEDBACK + " WHERE userid = '" + userid + "' AND (hqfs1 != 12 OR hqfs2 != 12 OR hqfs3 != 12)  ORDER BY id DESC";
        String sql = "SELECT * FROM " + PROJECT_TABLE_FEEDBACK + " WHERE userid = '" + userid + "' GROUP BY subid ORDER BY id DESC";
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery(sql, null);
    }

    /////// Get data with id ///////////
    public Cursor getSubmissionFeedbacks(int userid,int subid) {
        //String sql = "SELECT * FROM " + PROJECT_TABLE_SUBMISSION + " WHERE userid = '" + userid + "' AND SendServer = '" + status + "'";
        //String sql = "SELECT * FROM " + PROJECT_TABLE_FEEDBACK + " WHERE userid = '" + userid + "' AND (hqfs1 != 12 OR hqfs2 != 12 OR hqfs3 != 12)  ORDER BY id DESC";
        String sql = "SELECT * FROM " + PROJECT_TABLE_FEEDBACK + " WHERE userid = '" + userid + "' AND subid = '" + subid + "' ORDER BY id DESC ";
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery(sql, null);
    }

    public ArrayList<String> getAllLikeFeedback(int userid,int subid) {
        ArrayList<String> array_list = new ArrayList<String>();

        //hp = new HashMap();
        SQLiteDatabase db = this.getReadableDatabase();
        //Cursor res =  db.rawQuery( "select * from Projects", null );
        String sql = "SELECT * FROM " + PROJECT_TABLE_FEEDBACK + " WHERE userid = ? AND subid = ?";

        Cursor res = db.rawQuery(sql,new String[]{String.valueOf(userid),String.valueOf(subid)});


        //Cursor res = db.rawQuery(sql, null);
        res.moveToFirst();

        while(res.isAfterLast() == false){
            //array_list.add(res.getString(res.getColumnIndex(CONTACTS_COLUMN_NAME)));
            array_list.add(res.getString(res.getColumnIndex("id")));
            array_list.add(res.getString(res.getColumnIndex("hqfeed")));
            array_list.add(res.getString(res.getColumnIndex("hqfeeddate")));
            array_list.add(res.getString(res.getColumnIndex("status")));
            array_list.add(res.getString(res.getColumnIndex("subid")));
            array_list.add(res.getString(res.getColumnIndex("userfeed")));
            res.moveToNext();
        }
        return array_list;
    }

    public ArrayList<String> getAllFeedback() {
        ArrayList<String> array_list = new ArrayList<String>();

        //hp = new HashMap();
        SQLiteDatabase db = this.getReadableDatabase();
        //Cursor res =  db.rawQuery( "select * from Projects", null );
        Cursor res = db.rawQuery("SELECT shelter,status,userid FROM " + PROJECT_TABLE_FEEDBACK, null);
        res.moveToFirst();

        while(res.isAfterLast() == false){
            //array_list.add(res.getString(res.getColumnIndex(CONTACTS_COLUMN_NAME)));
            array_list.add(res.getString(res.getColumnIndex("shelter")));
            array_list.add(res.getString(res.getColumnIndex("status")));
            array_list.add(res.getString(res.getColumnIndex("userid")));
            res.moveToNext();
        }
        return array_list;
    }
    /////// Update Data ///////////
    public boolean updateFeedback (Integer id, String name, String code) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("shelter", name);
        db.update(PROJECT_TABLE_FEEDBACK, contentValues, "id = ? ", new String[] { Integer.toString(id) } );
        return true;
    }
    /////// Count Total Data ///////////
    public int numberOfRowsFeedback(){
        SQLiteDatabase db = this.getReadableDatabase();
        int numRows = (int) DatabaseUtils.queryNumEntries(db, PROJECT_TABLE_FEEDBACK);
        return numRows;
    }
    /////// Delete one Data ///////////
    public Integer deleteFeedback (Integer id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(PROJECT_TABLE_FEEDBACK,
                "id = ? ",
                new String[] { Integer.toString(id) });
    }

    /////// Delete one Data ///////////
    public Integer deleteFeedbackBySubmission (Integer subid) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(PROJECT_TABLE_FEEDBACK,
                "subid = ? ",
                new String[] { Integer.toString(subid) });
    }
    /////// Delete All Data ///////////
    public boolean deleteAllFeedback () {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(PROJECT_TABLE_FEEDBACK, null, null);
        return true;
    }

    /* ====================================== Gallery form All Query ================================================*/
    /////////////////////////////// Insert ///////////////////////////////
    public boolean insertGallery(int uid,int sid,int pid,int spid, String shelter,int mtaskid, String c1,String c2,String c3,String c4,String c5,
                                 String img1,String img2,String img3,String img4,String img5) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("userid", uid);
        values.put("subid", sid);
        values.put("package_id", pid);
        values.put("project_id", spid);
        values.put("shelter", shelter);
        values.put("task", mtaskid);
        values.put("Image1", img1);
        values.put("Image2", img2);
        values.put("Image3", img3);
        values.put("Image4", img4);
        values.put("Image5", img5);
        values.put("Caption1", c1);
        values.put("Caption2", c2);
        values.put("Caption3", c3);
        values.put("Caption4", c4);
        values.put("Caption5", c5);

        database.insert(PROJECT_TABLE_GALLERY, null, values);
        database.close();
        return true;
    }


    public boolean updateGallery(int uid,int sid, String shelter, String c1,String c2,String c3,String c4,String c5,
                                 String img1,String img2,String img3,String img4,String img5) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("userid", uid);
        values.put("subid", sid);
        values.put("shelter", shelter);
        values.put("Image1", img1);
        values.put("Image2", img2);
        values.put("Image3", img3);
        values.put("Image4", img4);
        values.put("Image5", img5);
        values.put("Caption1", c1);
        values.put("Caption2", c2);
        values.put("Caption3", c3);
        values.put("Caption4", c4);
        values.put("Caption5", c5);

        database.update(PROJECT_TABLE_GALLERY, values, "subid = ? ", new String[] { Integer.toString(sid) } );
        database.close();
        return true;
    }

    /////// Get data with id ///////////
    public Cursor getDataGallery(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from " + PROJECT_TABLE_GALLERY + " where subid="+id+"", null );
        return res;
    }

    public Cursor getDataDetailsGallery(long id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from " + PROJECT_TABLE_GALLERY + " where subid="+id+"", null );
        return res;
    }


    public ArrayList<String> getAllLikeGallery(int userid) {
        ArrayList<String> array_list = new ArrayList<String>();

        //hp = new HashMap();
        SQLiteDatabase db = this.getReadableDatabase();
        //Cursor res =  db.rawQuery( "select * from Projects", null );
        Cursor res = db.rawQuery("SELECT * FROM " + PROJECT_TABLE_GALLERY + " WHERE userid = '" + userid + "'", null);
        res.moveToFirst();

        while(res.isAfterLast() == false){
            //array_list.add(res.getString(res.getColumnIndex(CONTACTS_COLUMN_NAME)));
            array_list.add(res.getString(res.getColumnIndex("shelter")));
            res.moveToNext();
        }
        return array_list;
    }

    public ArrayList<String> getAllGallery() {
        ArrayList<String> array_list = new ArrayList<String>();

        //hp = new HashMap();
        SQLiteDatabase db = this.getReadableDatabase();
        //Cursor res =  db.rawQuery( "select * from Projects", null );
        Cursor res = db.rawQuery("SELECT * FROM " + PROJECT_TABLE_GALLERY, null);
        res.moveToFirst();

        while(res.isAfterLast() == false){
            //array_list.add(res.getString(res.getColumnIndex(CONTACTS_COLUMN_NAME)));
            array_list.add(res.getString(res.getColumnIndex("shelter")));
            res.moveToNext();
        }
        return array_list;
    }

    /////// Count Total Data ///////////
    public int numberOfRowsGallery(){
        SQLiteDatabase db = this.getReadableDatabase();
        int numRows = (int) DatabaseUtils.queryNumEntries(db, PROJECT_TABLE_GALLERY);
        return numRows;
    }
    /////// Delete one Data ///////////
    public Integer deleteGallery (Integer id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(PROJECT_TABLE_GALLERY,
                "id = ? ",
                new String[] { Integer.toString(id) });
    }
    /////// Delete All Data ///////////
    public boolean deleteAllGallery () {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(PROJECT_TABLE_GALLERY, null, null);
        return true;
    }
}