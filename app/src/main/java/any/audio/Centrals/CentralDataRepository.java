package any.audio.Centrals;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import any.audio.Config.Constants;
import any.audio.Database.DbHelper;
import any.audio.Managers.CloudManager;
import any.audio.Models.ResultMessageObjectModel;
import any.audio.Models.ExploreItemModel;
import any.audio.SharedPreferences.SharedPrefrenceUtils;
import any.audio.helpers.L;

/**
 * call addAction()
 * register adapters for data updates
 */
public class CentralDataRepository {

    /**
     * Flag is refrenced when activity first load
     */
    public static final int FLAG_FIRST_LOAD = 0;
    /**
     * Flag is refrenced when back navigation
     */
    public static final int FLAG_RESTORE = 1;
    /**
     * Flag is refrenced during search
     */
    public static final int FLAG_SEARCH = 2;
    /**
     * Flag is refrenced refress is triggred
     */
    public static final int FLAG_REFRESS = 3;
    /**
     * Flag is refrenced show all is triggred
     */
    public static final int FLAG_SHOW_ALL = 4;



    /**
     * Result type
     */
    public static final int TYPE_TRENDING = 405;
    /**
     * Result type
     */
    public static final int TYPE_RESULT = 406;
    private static Context context;
    private static CentralDataRepository mInstance;
    // private ActionCompletedListener mActionCompletdListener;
    //private DataReadyToSubmitListener dataReadyToSubmitListener;
    private DbHelper mDBHelper;
    private CloudManager mCloudManager;
    private Handler mHandler;
    private String sectionType = "";
    private String MESSAGE_TO_PASS = "";
    private SharedPrefrenceUtils sharedPrefrenceUtils;

    /**
     * Default constructor
     */
    private CentralDataRepository() {
    }

    /**
     * @param context Subscriber`s context
     */
    public CentralDataRepository(Context context) {

        CentralDataRepository.context = context;
        this.mDBHelper = DbHelper.getInstance(context);
        this.mCloudManager = CloudManager.getInstance(context);
        sharedPrefrenceUtils = SharedPrefrenceUtils.getInstance(context);

    }

    /**
     * @param context Singleton Pattern method to access same instance
     * @return
     */
    public static CentralDataRepository getInstance(Context context) {

        if (mInstance == null) {
            mInstance = new CentralDataRepository(context);
        }
        return mInstance;

    }

    /**
     * @param type    Action Type
     * @param handler Main Thread Handler
     */
    public void submitAction(final int type, Handler handler) {

       //L.m(("CDR", "Action Invoke Type:" + type);

        this.mHandler = handler;

        new Thread() {
            @Override
            public void run() {

                switch (type) {

                    case FLAG_FIRST_LOAD:
                        loadTrendingOrRequestTrending();
                        break;
                    case FLAG_RESTORE:
                        submitLastLoaded();
                        break;
                    case FLAG_SEARCH:
                        searchAndSubmit();
                        break;
                    case FLAG_REFRESS:
                        refressAndSubmit();
                        break;
                    case FLAG_SHOW_ALL:
                        getAllAndSubmit();
                        break;
                    default:
                        break;              // do nothing
                }
            }
        }.start();

       //L.m(("CDR", "started thread for action");

    }
    /* get the group title and fetch it from the db and submit
    * */

    public void setSectionType(String type){
        this.sectionType = type;
    }

    private void getAllAndSubmit() {

        mDBHelper.setTrendingLoadListener(new DbHelper.TrendingLoadListener() {
            @Override
            public void onTrendingLoad(ExploreItemModel trendingItem) {

                dispatchMessage(
                        Constants.MESSAGE_STATUS_OK,
                        MESSAGE_TO_PASS,
                        trendingItem
                );
            }
        });

        mDBHelper.pokeForShowAll(sectionType);

    }

    /**
     * gets Last Loaded and Request
     * save to db + callback + submit
     * not need to set Last loaded
     */
    private void refressAndSubmit() {

        int mLastLoadedType = sharedPrefrenceUtils.getLastLoadedType();

        if (mLastLoadedType == TYPE_TRENDING) {

            mDBHelper.setTrendingLoadListener(new DbHelper.TrendingLoadListener() {
                @Override
                public void onTrendingLoad(ExploreItemModel trendingItem) {
                    // Create Message Object
                    dispatchMessage(
                            Constants.MESSAGE_STATUS_OK,
                            MESSAGE_TO_PASS,
                            trendingItem
                    );

                }
            });

            mCloudManager.lazyRequestTrending();

        } else {
            // re-use: same symptoms
            searchAndSubmit();
        }

    }

    /**
     * Searches for result and submit + save to db
     * + callback
     * After submittion must setLastLoaded
     */
    private void searchAndSubmit() {

        mDBHelper.setResultLoadListener(new DbHelper.ResultLoadListener() {
            @Override
            public void onResultLoadListener(ExploreItemModel result) {

                dispatchMessage(
                        Constants.MESSAGE_STATUS_OK,
                        MESSAGE_TO_PASS,
                        result
                );


            }
        });

        SharedPrefrenceUtils utils = SharedPrefrenceUtils.getInstance(context);
        String searchTerm = utils.getLastSearchTerm();

        Log.i("SearchTest"," searching term .."+searchTerm);

        mCloudManager.requestSearch(searchTerm);

        sharedPrefrenceUtils.setLastLoadedType(TYPE_RESULT);

    }

    /**
     * (Restore State)
     * Checks Last Loaded
     * gets from DB and submits to registered adapters
     * After submition must setLastLoaded
     */
    private void submitLastLoaded() {

        Log.d("DBHelper", " last loaded result");

        mDBHelper.setResultLoadListener(new DbHelper.ResultLoadListener() {
            @Override
            public void onResultLoadListener(ExploreItemModel result) {

                dispatchMessage(
                        Constants.MESSAGE_STATUS_OK,
                        MESSAGE_TO_PASS,
                        result
                );

            }
        });

        mDBHelper.pokeForResults();

    }


    /**
     * Checks DB for saved data
     * if !Available
     * Request - > save to db + action callback +  adapter callback
     * else
     * action callback + adapter callback
     * After submittion must setLastLoaded
     */
    private void loadTrendingOrRequestTrending() {

        //  check for available cache
        boolean isAnyCache = mDBHelper.isTrendingsCached();
        Log.d("Database", " Cached " + isAnyCache);
        // subscribe for callback from database
        mDBHelper.setTrendingLoadListener(new DbHelper.TrendingLoadListener() {
            @Override
            public void onTrendingLoad(ExploreItemModel trendingItem) {

                dispatchMessage(
                        Constants.MESSAGE_STATUS_OK,
                        MESSAGE_TO_PASS,
                        trendingItem
                );
            }
        });


        if (!isAnyCache) {    // request for trending and then update
            // request
            mCloudManager.lazyRequestTrending();
        } else {
            // poke for available data
            mDBHelper.pokeForTrending();
        }

        sharedPrefrenceUtils.setLastLoadedType(TYPE_TRENDING);

    }

    private void dispatchMessage(int status, String message, ExploreItemModel data) {

       //L.m(("CDR", "dispatching Message");
        Message msg = Message.obtain();
        msg.obj = new ResultMessageObjectModel(
                status,
                message,    // temp : for future requis. if needed
                data
        );

        // send message
        mHandler.sendMessage(msg);
    }


}
