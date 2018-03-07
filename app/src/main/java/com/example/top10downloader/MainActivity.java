package com.example.top10downloader;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private static final String STATE_URL = "StateUrl";
    private static final String STATE_LIMIT = "StateLimit";

    private ListView feedListView;
    private ArrayList<FeedEntry> appsList;
    private int feedLimit = 10;
    private String feedUrl = "http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/topsongs/limit=%d/xml";
    private String cachedUrl = "INVALIDATED";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState != null) {
            feedUrl = savedInstanceState.getString(STATE_URL);
            feedLimit = savedInstanceState.getInt(STATE_LIMIT);
        }

        feedListView = findViewById(R.id.feedListView);
        downloadFeed(String.format(feedUrl, feedLimit));
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString(STATE_URL, feedUrl);
        outState.putInt(STATE_LIMIT, feedLimit);
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        if (feedLimit == 10) menu.findItem(R.id.mnu10).setChecked(true);
        else menu.findItem(R.id.mnu25).setChecked(true);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.mnuSongs:
                feedUrl = "http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/topsongs/limit=%d/xml";
                break;
            case R.id.mnuMovies:
                feedUrl = "http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/topMovies/limit=%d/xml";
                break;
            case R.id.mnuFreeApps:
                feedUrl = "http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/topfreeapplications/limit=%d/xml";
                break;
            case R.id.mnuPaidApps:
                feedUrl = "http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/toppaidapplications/limit=%d/xml";
                break;
            case R.id.mnu10:
            case R.id.mnu25:
                if (!item.isChecked()) {
                    item.setChecked(true);
                    feedLimit = 35 - feedLimit;
                }
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        downloadFeed(String.format(feedUrl, feedLimit));
        return true;
    }

    private void downloadFeed(String urlPath) {
        if (!cachedUrl.equals(urlPath)) {
            cachedUrl = urlPath;
            FeedDownloader feedDownloader = new FeedDownloader();
            feedDownloader.execute(urlPath);
        }
    }

    private class FeedDownloader extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            return downloadXml(strings[0]);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            parseXml(s);
            FeedAdapter feedAdapter = new FeedAdapter(MainActivity.this, appsList);
            feedListView.setAdapter(feedAdapter);
        }

        public String downloadXml(String urlPath) {
            StringBuilder xmlResult = new StringBuilder();
            try {
                URL url = new URL(urlPath);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                BufferedReader reader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
                int charRead;
                char[] buffer = new char[500];
                while (true) {
                    charRead = reader.read(buffer);
                    if (charRead > 0) xmlResult.append(String.copyValueOf(buffer, 0, charRead));
                    if (charRead < 0) break;
                }
                reader.close();
            } catch (MalformedURLException e) {
                Log.e(TAG, "downloadXml: Invalid URL...  \n" + e.getMessage());
            } catch (IOException e) {
                Log.e(TAG, "downloadXml: IO Exception...  \n" + e.getMessage());
            }
            return xmlResult.toString();
        }

        public void parseXml(String xml) {
            boolean inEntry = false;
            String tagName, text = null;
            appsList = new ArrayList<>();
            FeedEntry feedEntry = null;

            try {
                XmlPullParserFactory xppFactory = XmlPullParserFactory.newInstance();
                xppFactory.setNamespaceAware(true);
                XmlPullParser xpp = xppFactory.newPullParser();
                xpp.setInput(new StringReader(xml));

                int eventType = xpp.getEventType();
                while (eventType != XmlPullParser.END_DOCUMENT) {
                    tagName = xpp.getName();
                    switch (eventType) {
                        case XmlPullParser.START_TAG:
                            if ("entry".equalsIgnoreCase(tagName)) {
                                inEntry = true;
                                feedEntry = new FeedEntry();
                            } else if (inEntry && "category".equalsIgnoreCase(tagName)) {
                                String genre = xpp.getAttributeValue(null, "label");
                                feedEntry.setGenre(genre);
                            } else if (inEntry && "releaseDate".equalsIgnoreCase(tagName)) {
                                String releaseDate = xpp.getAttributeValue(null, "label");
                                feedEntry.setReleaseDate(releaseDate);
                            }
                            break;
                        case XmlPullParser.TEXT:
                            if (inEntry) text = xpp.getText();
                            break;
                        case XmlPullParser.END_TAG:
                            if (inEntry) {
                                if ("name".equalsIgnoreCase(tagName)) feedEntry.setName(text);
                                if ("artist".equalsIgnoreCase(tagName)) feedEntry.setArtist(text);
                                if ("summary".equalsIgnoreCase(tagName)) feedEntry.setSummary(text);
                                if ("entry".equalsIgnoreCase(tagName)) {
                                    appsList.add(feedEntry);
                                }
                            }
                            break;
                    }
                    eventType = xpp.next();
                }
            } catch (Exception e) {
                Log.e(TAG, "parseXml: " + e.getMessage());
            }
        }
    }
}
