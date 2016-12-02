package any.audio.Fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

import any.audio.Adapters.DownloadedSongsAdapter;
import any.audio.Config.Constants;
import any.audio.Interfaces.DownloadItemInvalidatedListener;
import any.audio.R;

public class DownloadFragment extends Fragment implements DownloadItemInvalidatedListener {

    private static final String TAG = "DownloadFragment";
    private static ListView downloadedItemListView;
    private static Context context;
    private AdapterView.AdapterContextMenuInfo menuinfo;

    public DownloadFragment() {
        // Required empty public constructor
    }

    private static void setUpAdapter() {

        ArrayList<String> files = new ArrayList<>();
        File dir = new File(Constants.FILES_DIR);
        File[] _files = dir.listFiles();
        for (File f : _files) {
            String path = f.toString();
            Log.d("Downloaded", "" + path.toString());
            files.add(0, path);
        }

        DownloadedSongsAdapter adapter = new DownloadedSongsAdapter(context);
        adapter.setItemList(files);
        downloadedItemListView.setAdapter(adapter);


    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_download, container, false);
        downloadedItemListView = (ListView) view.findViewById(R.id.dowloadedItemsList);

        setUpAdapter();
        ((AppCompatActivity) context).registerForContextMenu(downloadedItemListView);


        return view;

    }
//
//    @Override
//    public void onCreateContextMenu(ContextMenu menu,
//                                    View v, ContextMenu.ContextMenuInfo menuInfo) {
//        menu.add(0, 1, 0, "Add");
//        menu.add(0, 2, 1, "Rename");
//        menu.add(0, 3, 2, "Delete");
//        super.onCreateContextMenu(menu, v, menuInfo);
//    }
//
//    @Override
//    public boolean onContextItemSelected(MenuItem item) {
//        super.onContextItemSelected(item);
//
//        if(item.getTitle().equals("Add")) {
//            //Add code
//        } else if(item.getTitle().equals("Rename")) {
//            //Rename code
//        } else if(item.getTitle().equals("Delete")) {
//            //Delete code
//        }
//        return true;
//    };
////
////    @Override
////    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
////
////        menu.setHeaderTitle("Select Action");
////        menu.add(0, v.getId(), 0, "Delete");
////        super.onCreateContextMenu(menu, v, menuInfo);
////    }
////
////    @Override
////    public boolean onContextItemSelected(MenuItem item) {
////
////        if(getUserVisibleHint()) {
////            menuinfo = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
////            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
////            int position = info.position;
////
////            Log.d("DownloadFragment", " selected item " + position);
////            if (item.getTitle().equals("Delete")) {
////                deleteItem(position);
////            }
////            return super.onContextItemSelected(item);
////        }
////        return false;
////    }

    private void deleteItem(int pos){

        ArrayList<String> files = new ArrayList<>();
        File dir = new File(Constants.FILES_DIR);
        File[] _files = dir.listFiles();
        for (File f : _files) {
            String path = f.toString();
            files.add(0, path);
        }

        File fdelete = new File(files.get(pos));
        if (fdelete.exists()) {
            if (fdelete.delete()) {
                Toast.makeText(getActivity(),"Deleted",Toast.LENGTH_SHORT).show();
                setUpAdapter();
            }
        }

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        context = activity;
    }


    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onItemsInvalidated() {
        setUpAdapter();
    }

    public void refreshDownloadedList() {
        setUpAdapter();
    }
}
