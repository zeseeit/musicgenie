package any.audio.Activity;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;

import java.lang.reflect.Field;
import java.net.URLEncoder;
import java.util.ArrayList;

import any.audio.Managers.FontManager;
import any.audio.Network.VolleyUtils;
import any.audio.R;
import any.audio.SharedPreferences.SharedPrefrenceUtils;

public class SearchActivity extends AppCompatActivity {

    ListView suggestionsListView;
    EditText searchViewEdit;
    Toolbar toolbar;
    TextView cancelSearch;
    private ArrayList<String> suggestionsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
        setContentView(R.layout.activity_search);
        setUpSearch();
    }

    private void setUpSearch() {

        suggestionsListView = (ListView) findViewById(R.id.search_suggestions_listView);
        searchViewEdit = (EditText) findViewById(R.id.search_view);
        cancelSearch = (TextView) findViewById(R.id.search_clear);
        toolbar = (Toolbar) findViewById(R.id.search_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");

        cancelSearch.setTypeface(FontManager.getInstance(this).getTypeFace(FontManager.FONT_MATERIAL));
        try {
            Field f = TextView.class.getDeclaredField("mCursorDrawableRes");
            f.setAccessible(true);
            f.set(searchViewEdit, R.drawable.edittext_whitecursor);
        } catch (Exception ignored) {
        }

        searchViewEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {

                findSuggestion(charSequence.toString());

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }

        });

        searchViewEdit.setImeActionLabel("Search AnyAudio", KeyEvent.KEYCODE_ENTER);

        searchViewEdit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    String term = textView.getText().toString();
                    Log.d("Suggestioin", " term " + term);
                    if(term.length()>0)
                        finilizeSearch(textView.getText().toString());
                }
                return true;
            }
        });

        // Clear search text when clear button is tapped
        cancelSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                searchViewEdit.setText("");
                VolleyUtils.getInstance().cancelPendingRequests("SearchSuggestions");

            }
        });


    }

    private class SuggestionListAdapter extends ArrayAdapter<String> {

        private Context context;
        private ArrayList<String> suggestions;
        Typeface materialTypeface;

        public SuggestionListAdapter(Context context) {
            super(context, 0);
            this.context = context;
            materialTypeface = FontManager.getInstance(context).getTypeFace(FontManager.FONT_MATERIAL);

        }

        public void setSuggestions(ArrayList<String> suggestions) {
            this.suggestions = suggestions;
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            SuggestionViewHolder holder = null;

            if (convertView == null) {
                holder = new SuggestionViewHolder();
                convertView = LayoutInflater.from(context).inflate(R.layout.suggestion_layout, null, false);
                holder.optChoice = (TextView) convertView.findViewById(R.id.optOption);
                holder.historyOptions = (TextView) convertView.findViewById(R.id.saveToHistory);
                holder.suggestionTv = (TextView) convertView.findViewById(R.id.suggestion_title);
                convertView.setTag(holder);

            } else {
                holder = (SuggestionViewHolder) convertView.getTag();
            }

            holder.suggestionTv.setText(suggestions.get(position));
            holder.optChoice.setTypeface(materialTypeface);
            holder.historyOptions.setTypeface(materialTypeface);

            holder.suggestionTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    finilizeSearch(suggestions.get(position));
                    finish();

                }
            });

            holder.optChoice.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    searchViewEdit.setText(suggestions.get(position));

                }
            });

            holder.historyOptions.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    finilizeSearch(suggestions.get(position));
                    finish();

                }
            });

            return convertView;
        }

        @Override
        public int getCount() {
            return suggestions.size();
        }
    }

    static class SuggestionViewHolder {

        private TextView historyOptions;
        private TextView optChoice;
        private TextView suggestionTv;

    }

    private void findSuggestion(String term) {

        suggestionsList = new ArrayList<>();
        String url = "http://suggestqueries.google.com/complete/search?q=" + URLEncoder.encode(term) + "&client=firefox&hl=en&ds=yt";

        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONArray jsonArray = new JSONArray(response);
                    String suggestionsArr = jsonArray.get(1).toString();
                    JSONArray sarr = new JSONArray(suggestionsArr);

                    for (int i = 0; i < sarr.length(); i++) {
                        suggestionsList.add(sarr.get(i).toString());
                    }

                    // set data
                    setSuggestions(suggestionsList);

                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
            }
        });

        VolleyUtils.getInstance().addToRequestQueue(request, "SearchSuggestions", this);

    }

    private void setSuggestions(ArrayList<String> suggestionsList) {

        SuggestionListAdapter adapter = new SuggestionListAdapter(this);
        adapter.setSuggestions(suggestionsList);
        suggestionsListView.setAdapter(adapter);

    }

    private void finilizeSearch(String term) {
        SharedPrefrenceUtils utils = SharedPrefrenceUtils.getInstance(this);

        utils.setFirstSearchDone(true);
        utils.newSearch(!utils.getLastSearchTerm().equals(term));
        utils.setLastSearchTerm(term);
        finish();

    }

}
