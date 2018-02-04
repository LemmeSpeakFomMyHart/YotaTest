package com.icantstop.vikta.yotatest;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class SpacexDataFetcher {

    private static final String TAG="SpacexDataFetcher";

    public byte[] getUrlBytes(String urlSpec) throws IOException{
        URL url=new URL(urlSpec);
        HttpURLConnection connection=(HttpURLConnection) url.openConnection();

        try {
            ByteArrayOutputStream out=new ByteArrayOutputStream();
            InputStream in=connection.getInputStream();

            if (connection.getResponseCode()!= HttpURLConnection.HTTP_OK) {
                throw new IOException(connection.getResponseMessage()+": with "+urlSpec);
            }

            int bytesRead=0;
            byte[] buffer=new byte[1024];
            while ((bytesRead=in.read(buffer))>0){
                out.write(buffer,0,bytesRead);
            }
            out.close();
            return out.toByteArray();
        } finally {
            connection.disconnect();
        }
    }

    public String getUrlString(String urlSpec) throws IOException {
        return new String(getUrlBytes(urlSpec));
    }

    public List<RocketLaunch> fetchItems(){

        List<RocketLaunch> items=new ArrayList<>();
        try {
            String url="https://api.spacexdata.com/v2/launches?launch_year=2017";
            String jsonString=getUrlString(url);
            JSONArray jsonBody=new JSONArray(jsonString);
            parseItems(items,jsonBody);
        } catch (IOException ioe) {
            Log.e(TAG,"Failed to fetch items",ioe);
        } catch (JSONException e) {
            Log.e(TAG,"Failed to parse JSON",e);
        }
        return items;
    }

    private void parseItems(List<RocketLaunch> items,JSONArray jsonBody) throws IOException,
            JSONException{
        for (int i=0;i<jsonBody.length();i++) {

            JSONObject launchJsonObject=jsonBody.getJSONObject(i);
            JSONObject rocketJsonObject=launchJsonObject.getJSONObject("rocket");
            JSONObject linksJsonObject=launchJsonObject.getJSONObject("links");

            RocketLaunch item=new RocketLaunch();
            item.setLaunchDateUnix(launchJsonObject.getInt("launch_date_unix"));
            item.setRocketName(rocketJsonObject.getString("rocket_name"));
            item.setMissionPatch(linksJsonObject.getString("mission_patch"));
            item.setDetails(launchJsonObject.getString("details"));
            item.setVideoLink(linksJsonObject.getString("video_link"));
            item.setArticleLink(linksJsonObject.getString("article_link"));
            items.add(item);
        }
    }
}
