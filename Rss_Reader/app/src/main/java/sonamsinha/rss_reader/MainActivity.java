package sonamsinha.rss_reader;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.MalformedInputException;
import java.util.ArrayList;

public class MainActivity extends Activity {
    //private String[] listData = new String[]{"Post 1", "Post 2", "Post 3", "Post 4", "Post 5", "Post 6"};
    //private ArrayList<RssItemData> listData;
    //private ArrayAdapter<String> itemAdapter;
    private RssItemData[] listData;
    RssItemAdapter itemAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.sampleData();

        ListView listView = (ListView)this.findViewById(R.id.listView);
        //itemAdapter = new ArrayAdapter<String>(this, R.layout.list_item_layout.);
        itemAdapter = new RssItemAdapter(this,R.layout.list_item_layout,listData);
        listView.setAdapter(itemAdapter);
        //Sending url to Downloader class
        Downloader downloader = new Downloader();
        downloader.execute("http://rss.cnn.com/rss/cnn_topstories.rss");
    }
    /*public boolean onCreateOptionsMenu(Menu menu){ // Menu problem
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }*/

    private void sampleData(){
        RssItemData data =null;
        listData = new RssItemData[10];
        for (int i=0; i< 10; i++){
            data = new RssItemData();
            data.title = "Post" + (i+1) + "Title: This is the post title from RSS Feed";
            listData[i] = data;
        }
    }

    private enum RSSXMLTag{
        TITLE,DESCRIPTION,LINK,IGNORETAG;
    }

     class Downloader extends AsyncTask<String, Integer, ArrayList<RssItemData>> {

        private  RSSXMLTag currentTag;
        MainActivity mainActivity = new MainActivity();
        @Override
        protected ArrayList<RssItemData> doInBackground(String... urls) {
            String urlString = urls[0];
            InputStream inputStream = null;

            ArrayList<RssItemData> rssItemDataList = new ArrayList<RssItemData>();

            try {
                URL url = new URL(urlString);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                int response = connection.getResponseCode();
                Log.d("Debug", "The response is: " + response);
                inputStream = connection.getInputStream();

                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                factory.setNamespaceAware(true);
                XmlPullParser xmlParser = factory.newPullParser();
                xmlParser.setInput(inputStream,null);

                int eventType = xmlParser.getEventType();
                RssItemData rssData = null;
                while (eventType != XmlPullParser.END_DOCUMENT){
                    if(eventType == XmlPullParser.START_DOCUMENT){

                    }
                    else if(eventType == XmlPullParser.START_TAG){
                        if(xmlParser.getName().equals("item")){
                            rssData = new RssItemData();
                            currentTag = MainActivity.RSSXMLTag.IGNORETAG;
                        }
                        else if(xmlParser.getName().equals("title")){
                            currentTag = MainActivity.RSSXMLTag.TITLE;
                        }
                        else if(xmlParser.getName().equals("link")){
                            currentTag = RSSXMLTag.LINK;
                        }
                        else if(xmlParser.getName().equals("description")){
                            currentTag = RSSXMLTag.DESCRIPTION;
                        }
                    }
                    else if(eventType == XmlPullParser.END_TAG){
                        if (xmlParser.getName().equals("item")){
                            currentTag = MainActivity.RSSXMLTag.IGNORETAG;
                        }
                    }
                    else if(eventType == XmlPullParser.TEXT){
                        String content = xmlParser.getText();
                        //Log.w("Downloader", "Content is "+content);
                        content = content.trim();
                        //Log.d("Debug",content);
                        //Log.w("Downloader", "rss data "+rssData);
                        if(rssData != null){
                            //Log.w("Downloader ", " current tag "+currentTag);
                            switch (currentTag){
                                case TITLE:
                                    if (content.length()!= 0){
                                        if(rssData.title != null){
                                            //if(content.contains("http")){
                                              //  content = content.substring(0, content.indexOf("http"));
                                            //}
                                            rssData.title +=content;
                                            Log.w("Downloader", "Rss data "+rssData.title);
                                            rssItemDataList.add(rssData);
                                        }else{
                                            rssData.title = content;
                                        }
                                    }
                                    break;
                                case DESCRIPTION:
                                    if(content.length() > 0){
                                        if(rssData.description != null){
                                            rssData.description += content;
                                            Log.w("Downloader", "Rss description "+rssData.description);
                                        }else{
                                            rssData.description = content;
                                        }
                                    }
                                    break;
                                case LINK:
                                    if(content.length() > 0){
                                        if(rssData.link != null){
                                            rssData.link += content;
                                            Log.w("Downloader", "Rss url "+rssData.link);
                                        }else{
                                            rssData.link = content;
                                        }
                                    }
                                    break;
                                default:
                                    break;
                            }
                        }
                    }
                    eventType =xmlParser.next();
                }
                //logger
            }
            catch (MalformedInputException e){
                e.printStackTrace();

            }
            catch (IOException e){
                e.printStackTrace();
            }
            catch (XmlPullParserException e){
                e.printStackTrace();
            }

            //Read String
            /*final int bufferSize = 1024;
            byte[] buffer = new byte[1024];
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            while (true){
                int count = inputStream.read(buffer,0,bufferSize);
                if(count == -1){
                    break;
                }
                outputStream.write(buffer);
            }
            outputStream.close();

            String result = new String(outputStream.toByteArray(),"UTF-8");
            Log.d("Debug",result);
        }
        catch (MalformedInputException ex){
            ex.printStackTrace();
        }
        catch (IOException e){
            e.printStackTrace();
        }
        //return new RssItemData[0];*/
            return rssItemDataList;
        }

        protected void onPostExecute(ArrayList<RssItemData> result){
            //Log.w("OnPostExecute:-", "Coming here"+result.size());
            for(RssItemData rssItemData : result){
                //Log.w("OnPostExecute:-", "Result "+rssItemData.title);
            }
            for (int i= 0; i<listData.length; i++){
                //Log.w("OnPostExecute:-", "Setting results "+result.get(i));
                listData[i]=(result.get(i));
            }
            itemAdapter.notifyDataSetChanged();
        }
    }
}
