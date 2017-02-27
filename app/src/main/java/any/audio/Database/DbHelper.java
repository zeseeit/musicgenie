package any.audio.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import any.audio.Config.Constants;
import any.audio.Models.ItemModel;
import any.audio.helpers.L;
import any.audio.Models.ExploreItemModel;


/**
 * Create Local Cache Trending (temp)
 */
public class DbHelper extends SQLiteOpenHelper {

    private static final java.lang.String SECTION_TYPE_RESULTS = "Results";
    private static final String TIME_SINCE_UPLOADED_LEFT_VACCANT = "";
    private static final int DB_VERSION = 1;
    private static final int MX_DB_EXPLORE_ITEM_COUNT = 25;
    private static final String DB_NAME = "musicegenieDB";
    private static final String TABLE_TRENDING = "trendingTable";
    private static final String TABLE_RESULTS = "resultsTable";
    private static final String COL_TITLE = "TITLE";
    private static final String COL_TRACK_DURATION = "TRACK_DURATION";
    private static final String COL_UPLOADED_BY = "UPLOADED_BY";
    private static final String COL_THUMB_URL = "THUMB_URL";
    private static final String COL_VIDEO_ID = "VIDEO_ID";
    private static final String COL_TIME_SINCE_UPLOADED = "TIME_SINCE_UPLOADED";
    private static final String COL_USER_VIEWS = "USER_VIEWS";
    private static final String COL_TYPE = "TYPE";

    private static final String CREATE_TRENDING_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_TRENDING + "( " +

            COL_TITLE + " TEXT , " +
            COL_UPLOADED_BY + " TEXT , " +
            COL_THUMB_URL + " TEXT , " +
            COL_VIDEO_ID + " TEXT , " +
            COL_TIME_SINCE_UPLOADED + " TEXT , " +
            COL_USER_VIEWS + " TEXT , " +
            COL_TRACK_DURATION + " TEXT , " +
            COL_TYPE + " TEXT" +
            ")";
    private static final String CREATE_RESULTS_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_RESULTS + " ( " +
            COL_TITLE + " TEXT , " +
            COL_UPLOADED_BY + " TEXT , " +
            COL_THUMB_URL + " TEXT , " +
            COL_VIDEO_ID + " TEXT , " +
            COL_TIME_SINCE_UPLOADED + " TEXT , " +
            COL_USER_VIEWS + " TEXT , " +
            COL_TRACK_DURATION + " TEXT," +
            COL_TYPE + " TEXT" +
            ")";

    //String title, String trackDuration, String uploadedBy,
    // String thumbnail_url, String video_id, String timeSinceUploaded, String userViews, String type
    // create table trending ()
    private static final String DROP_TRENDING_TABLE = "DROP TABLE " + TABLE_TRENDING;
    private static final String DROP_RESULTS_TABLE = "DROP TABLE " + TABLE_RESULTS;
    private static Context context;
    private static DbHelper mInstance;
    TrendingLoadListener mTrendingLoadListener;
    ResultLoadListener mResultLoadListener;


    private DbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        DbHelper.context = context;
    }

    public static DbHelper getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new DbHelper(context);
        }
        return mInstance;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        sqLiteDatabase.execSQL(CREATE_TRENDING_TABLE);
        L.m("DBHelper", "created trending table");
        sqLiteDatabase.execSQL(CREATE_RESULTS_TABLE);
        L.m("DBHelper", "created results table");

    }

    public void upgradeDB() {
        int oldVer = 1;
        int newVer = 2;
        onUpgrade(getReadableDatabase(), oldVer, newVer);
    }

    /**
     * @param sqLiteDatabase database object
     * @param oldVersion     older version of database
     * @param newVersion     newer version of database
     */
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {

        sqLiteDatabase.execSQL(DROP_RESULTS_TABLE);
        sqLiteDatabase.execSQL(DROP_TRENDING_TABLE);
        onCreate(sqLiteDatabase);

    }

    private ContentValues getCVObject(ItemModel data) {

        ContentValues temp = new ContentValues();
        temp.put(COL_TITLE, data.Title);
        temp.put(COL_TIME_SINCE_UPLOADED, data.TimeSinceUploaded);
        temp.put(COL_THUMB_URL, data.Thumbnail_url);
        temp.put(COL_TYPE, data.type);
        temp.put(COL_TRACK_DURATION, data.TrackDuration);
        temp.put(COL_UPLOADED_BY, data.UploadedBy);
        temp.put(COL_VIDEO_ID, data.Video_id);
        temp.put(COL_USER_VIEWS, data.UserViews);

        return temp;
    }

    public boolean isTrendingsCached() {

        boolean hasStoredTrending = false;

        String[] cols = {COL_THUMB_URL};

        String[] selectionArgs = {};

        Cursor cr = getReadableDatabase().query(TABLE_TRENDING, cols, "", selectionArgs, null, null, null);
        cr.moveToFirst();

        hasStoredTrending = cr.getCount() > 0;
        L.m("DBHelper", " isTrendingCached() " + hasStoredTrending);
        cr.close();
        return hasStoredTrending;
    }

    public void resetTrendingList() {
        SQLiteDatabase db = getReadableDatabase();
        String deleteClause = "";
        String[] selectionArgs = {};
        db.delete(TABLE_TRENDING, deleteClause, selectionArgs);
        L.m("DBHelper", "Wiped Out Trending Data");
    }

    public void addTrendingList(ExploreItemModel list, boolean doReset) {

        SQLiteDatabase database = getWritableDatabase();
        ContentValues values;

        ArrayList<ItemModel> itemModels = list.getList();
        String type = list.sectionTitle;
        //     L.m("DBH"," size : "+itemModels.size()+" in "+type);
        for (int j = 0; j < itemModels.size(); j++) {
            ItemModel itemModel = itemModels.get(j);
            values = getCVObject(itemModel);
            values.put(COL_TYPE, type);
            long id = database.insert(TABLE_TRENDING, null, values);
        }

        Log.d("DBHelper", "added " + list.sectionTitle);

        if (mTrendingLoadListener != null) {
            // must-pass flag for data reset
            if (doReset) passFlagToResetExplore();
              mTrendingLoadListener.onTrendingLoad(list);
        }

    }

    ///////////////////////////////////////////////////////////////////////////
    //                  Results Operation Section
    ///////////////////////////////////////////////////////////////////////////

    private void passFlagToResetExplore() {

        ExploreItemModel tModel = new ExploreItemModel(Constants.FLAG_RESET_ADAPTER_DATA, null);
        mTrendingLoadListener.onTrendingLoad(tModel);

    }


    /**
     * @return Read Trending From database
     */
    private ArrayList<ExploreItemModel> getTrendingList(int limitSize) {

        ArrayList<ExploreItemModel> trendingFromDbList = new ArrayList<>();

        String[] cols = {COL_THUMB_URL,
                COL_USER_VIEWS,
                COL_TYPE,
                COL_TITLE,
                COL_UPLOADED_BY,
                COL_TIME_SINCE_UPLOADED,
                COL_VIDEO_ID,
                COL_TRACK_DURATION};

        String[] selectionArgs = {};


        Cursor cr = getReadableDatabase().query(TABLE_TRENDING, cols, "", selectionArgs, null, null, null);

        cr.moveToFirst();
        //  L.m("DBHT"," cursor size "+cr.getCount());

        ArrayList<ItemModel> mapList;
        HashMap<String, ArrayList<ItemModel>> trendingMap = new HashMap<>();
        boolean hasNext = cr.getCount() > 0;

        while (hasNext) {
            // get type of song from cursor
            ItemModel itemModelObj;
            String type = cr.getString(cr.getColumnIndex(COL_TYPE));
            itemModelObj = new ItemModel(cr.getString(cr.getColumnIndex(COL_TITLE)),
                    cr.getString(cr.getColumnIndex(COL_TRACK_DURATION)),
                    cr.getString(cr.getColumnIndex(COL_UPLOADED_BY)),
                    cr.getString(cr.getColumnIndex(COL_THUMB_URL)),
                    cr.getString(cr.getColumnIndex(COL_VIDEO_ID)),
                    "",
                    cr.getString(cr.getColumnIndex(COL_USER_VIEWS)),
                    cr.getString(cr.getColumnIndex(COL_TYPE)));

            mapList = trendingMap.get(type);
            //L.m("DBH","mapList type - "+type);
            if (mapList == null) {       // accessing list from map for first time
                mapList = new ArrayList<>();
                trendingMap.put(type, mapList);
                //      L.m("DBH", "[1st ent]mapList type - " + type);
                mapList.add(itemModelObj);
            } else {
                //        L.m("DBH","mapList type - "+type);
                if(mapList.size()<=limitSize)
                    mapList.add(itemModelObj);
            }
            hasNext = cr.moveToNext();
        }

        // get each item from map and get list and add

        for (Object o : trendingMap.entrySet()) {
            Map.Entry pair = (Map.Entry) o;
            //L.m("DBHelper", " Iterator " + pair.getKey().toString());
            //  L.m("DBH","type - >"+pair.getKey().toString()+" size - "+trendingMap.get(pair.getKey()).size());
            trendingFromDbList.add(new ExploreItemModel(pair.getKey().toString(), trendingMap.get(pair.getKey())));
        }

        L.m("DBHelper", " collected total " + trendingFromDbList.size());
        cr.close();
        return trendingFromDbList;
    }

    public boolean isAnyResult() {

        boolean hasStoredResult = false;

        String[] cols = {COL_THUMB_URL};

        String[] selectionArgs = {};
        Cursor cr = getReadableDatabase().query(TABLE_RESULTS, cols, "", selectionArgs, null, null, null);

        cr.moveToFirst();
        hasStoredResult = cr.getCount() > 0;
        cr.close();
        return hasStoredResult;
    }

    private void clearResultsIfAny() {
        SQLiteDatabase db = getReadableDatabase();
        String deleteClause = "";
        String[] selectionArgs = {};
        db.delete(TABLE_RESULTS, deleteClause, selectionArgs);
        L.m("DBHelper", "Wiped Out Result Data");
    }

    ///////////////////////////////////////////////////////////////////////////
    //              Callback Interface
    ///////////////////////////////////////////////////////////////////////////

    public void addResultsList(ExploreItemModel modelItem) {        // actually Overwrite the old data

        // clear back storage
        clearResultsIfAny();

        SQLiteDatabase database = getWritableDatabase();
        ContentValues values;

        ArrayList<ItemModel> itemModels = modelItem.getList();

        for (int j = 0; j < itemModels.size(); j++) {

            values = getCVObject(itemModels.get(j));

            long id = database.insert(TABLE_RESULTS, null, values);

            if (id < 0) {
                Log.d("DBHelper (Results)", "Cannot Add Row");
            } else {
                Log.d("DBHelper (Results)", "Added Successfully ");
            }
        }


        if (mResultLoadListener != null) {
            mResultLoadListener.onResultLoadListener(modelItem);
        }

    }

    private ExploreItemModel getResultList() {

        ExploreItemModel returnExploreItemModel = null;

        String[] cols = {
                COL_THUMB_URL,
                COL_USER_VIEWS,
                COL_TITLE,
                COL_UPLOADED_BY,
                COL_TIME_SINCE_UPLOADED,
                COL_VIDEO_ID,
                COL_TRACK_DURATION};

        String[] selectionArgs = {};

        ItemModel itemModelObj;
        ArrayList<ItemModel> itemModelArrayList = new ArrayList<>();

        Cursor cr = getReadableDatabase().query(TABLE_RESULTS, cols, "", selectionArgs, null, null, null);

        cr.moveToFirst();
        boolean hasNext = cr.getCount() > 0;

        while (hasNext) {

            itemModelObj = new ItemModel(cr.getString(cr.getColumnIndex(COL_TITLE)),
                    cr.getString(cr.getColumnIndex(COL_TRACK_DURATION)),
                    cr.getString(cr.getColumnIndex(COL_UPLOADED_BY)),
                    cr.getString(cr.getColumnIndex(COL_THUMB_URL)),
                    cr.getString(cr.getColumnIndex(COL_VIDEO_ID)),
                    TIME_SINCE_UPLOADED_LEFT_VACCANT,
                    cr.getString(cr.getColumnIndex(COL_USER_VIEWS)),
                    SECTION_TYPE_RESULTS);

            itemModelArrayList.add(itemModelObj);
            hasNext = cr.moveToNext();
        }
        returnExploreItemModel = new ExploreItemModel(SECTION_TYPE_RESULTS, itemModelArrayList);
        Log.d("DBHelper", " returning Results " + itemModelArrayList.size());
        cr.close();
        return returnExploreItemModel;
    }

    public void setTrendingLoadListener(TrendingLoadListener mTrendingLoadListener) {
        this.mTrendingLoadListener = mTrendingLoadListener;
    }

    public void pokeForTrending() {

        if (mTrendingLoadListener != null) {
            ArrayList<ExploreItemModel> tempTrendingList = getTrendingList(7);
            passFlagToResetExplore();
            for (int i = 0; i < tempTrendingList.size(); i++) {
                mTrendingLoadListener.onTrendingLoad(tempTrendingList.get(i));
            }

        }
    }

    public void pokeForResults() {

        if (mResultLoadListener != null) {

            mResultLoadListener.onResultLoadListener(getResultList());
        }
    }

    public void setResultLoadListener(ResultLoadListener mResultLoadListener) {
        this.mResultLoadListener = mResultLoadListener;
    }

    public void pokeForShowAll(String type) {

        if (mTrendingLoadListener != null) {
            ArrayList<ExploreItemModel> tempTrendingList = getTrendingList(MX_DB_EXPLORE_ITEM_COUNT);
            for (int i = 0; i < tempTrendingList.size(); i++) {

                ExploreItemModel item = tempTrendingList.get(i);
                if(item.sectionTitle.toLowerCase().equals(type.toLowerCase())) {
                    mTrendingLoadListener.onTrendingLoad(item);
                }

            }

        }

    }

    public interface TrendingLoadListener {
        void onTrendingLoad(ExploreItemModel trendingItem);
    }

    public interface ResultLoadListener {
        void onResultLoadListener(ExploreItemModel result);
    }

}
