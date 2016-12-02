package any.audio.Centrals;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import any.audio.Config.Constants;
import any.audio.Database.DbHelper;
import any.audio.Managers.CloudManager;
import any.audio.Models.ResultMessageObjectModel;
import any.audio.Models.SectionModel;
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
     * Result type
     */
    private static final int TYPE_TRENDING = 405;

    /**
     * Result type
     */
    private static final int TYPE_RESULT = 406;
    private static Context context;
    private static CentralDataRepository mInstance;
   // private ActionCompletedListener mActionCompletdListener;
    //private DataReadyToSubmitListener dataReadyToSubmitListener;
    private DbHelper mDBHelper;
    private CloudManager mCloudManager;
    private Handler mHandler;
    private int mLastLoadedType = TYPE_TRENDING;
    private String MESSAGE_TO_PASS = "";

    /**
     * Default constructor
     */
    private CentralDataRepository() {
    }

    /**
     * @param context Subscriber`s context
     */
    private CentralDataRepository(Context context) {

        CentralDataRepository.context = context;
        this.mDBHelper = DbHelper.getInstance(context);
        this.mCloudManager = CloudManager.getInstance(context);

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
     * @param type Action Type
     * @param handler   Main Thread Handler
     */
    public void submitAction(final int type, Handler handler){

        L.m("CDR","Action Invoke Type:"+type);

        this.mHandler = handler;

        new Thread(){
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
                    default:
                        break;              // do nothing
                }
            }
        }.start();

        L.m("CDR","started thread for action");

    }

    /**
     * gets Last Loaded and Request
     * save to db + callback + submit
     * not need to set Last loaded
     */
    private void refressAndSubmit() {

        if (mLastLoadedType == TYPE_TRENDING) {

            mDBHelper.setTrendingLoadListener(new DbHelper.TrendingLoadListener() {
                @Override
                public void onTrendingLoad(SectionModel trendingItem) {
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
            public void onResultLoadListener(SectionModel result) {

                dispatchMessage(
                        Constants.MESSAGE_STATUS_OK,
                        MESSAGE_TO_PASS,
                        result
                );


            }
        });

        SharedPrefrenceUtils utils = SharedPrefrenceUtils.getInstance(context);
        String searchTerm = utils.getLastSearchTerm();
        mCloudManager.requestSearch(searchTerm);

        mLastLoadedType = TYPE_RESULT;

    }

    /**
     * (Restore State)
     * Checks Last Loaded
     * gets from DB and submits to registered adapters
     * After submition must setLastLoaded
     */
    private void submitLastLoaded() {

       // L.m("CDR ", " last loaded was " + mLastLoadedType);

        if (mLastLoadedType == TYPE_TRENDING) {

            mDBHelper.setTrendingLoadListener(new DbHelper.TrendingLoadListener() {
                @Override
                public void onTrendingLoad(SectionModel trendingItem) {

                    dispatchMessage(
                            Constants.MESSAGE_STATUS_OK,
                            MESSAGE_TO_PASS,
                            trendingItem
                    );

                }
            });

            mDBHelper.pokeForTrending();

            mLastLoadedType = TYPE_TRENDING;

        } else {

            mDBHelper.setResultLoadListener(new DbHelper.ResultLoadListener() {
                @Override
                public void onResultLoadListener(SectionModel result) {

                    dispatchMessage(
                            Constants.MESSAGE_STATUS_OK,
                            MESSAGE_TO_PASS,
                            result
                    );

                }
            });

            mDBHelper.pokeForResults();

            mLastLoadedType = TYPE_RESULT;
        }


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

        // subscribe for callback from database
        mDBHelper.setTrendingLoadListener(new DbHelper.TrendingLoadListener() {
            @Override
            public void onTrendingLoad(SectionModel trendingItem) {


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

        mLastLoadedType = TYPE_TRENDING;

    }

    private void dispatchMessage(int status,String message,SectionModel data){

        L.m("CDR","dispatching Message");
        Message msg = Message.obtain();
        msg.obj = new ResultMessageObjectModel(
                status,
                message,    // temp : for future requis. if needed
                data
        );

        // send message
        mHandler.sendMessage(msg);
    }

//    /**
//     * @param listener callback from data seekers adapters
//     */
//    public void registerForDataLoadListener(DataReadyToSubmitListener listener) {
//        this.dataReadyToSubmitListener = listener;
//    }

//    /**
//     * @param mListener callback for action complete to operation initiater
//     */
//    public void setListener(ActionCompletedListener mListener) {
//        this.mActionCompletdListener = mListener;
//    }

    public int getLastLoadedType() {
        return this.mLastLoadedType;
    }

    public void setLastLoadedType(int mLastLoadedType) {
        this.mLastLoadedType = mLastLoadedType;
    }

//    public interface ActionCompletedListener {      // these listeners will be summitted by Operation Initiater
//        void onActionCompleted();
//    }

//    public interface DataReadyToSubmitListener {    // these listeners will be summitted by Adapter who are waiting for our data
//
//        // for Result  there will be single item
//        // for Trending there will be list of items
//        void onDataSubmit(SectionModel item);
//    }

//    public class InvalidCallbackException extends Exception {
//        public InvalidCallbackException(String detailMessage) {
//            super(detailMessage);
//            System.out.println(detailMessage);
//            Log.e("CentralDataRepository", detailMessage);
//        }

    }
