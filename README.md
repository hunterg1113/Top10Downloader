# Top10Downloader

This is an app created to download RSS feeds from Apple, then display the data collected on a phone or tablet.

It utilizes:

AsyncTask to run tasks in background thread
XML parsing to extract data from a downloaded feed
Custom adapter (that extends ArrayAdapter) to provide data to ListViews
Menu to filter results
Security permissions to allow access to internet
Defining savedInstanceState to prevent redownload when orientation changed
