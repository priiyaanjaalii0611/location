package com.example.android.location;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class DownloadUrl {

    public String  ReadUrl(String placeUrl) throws IOException{
        String Data="";
        // reading URL

        InputStream inputStream=null;  //initializing inputstream

        HttpURLConnection httpURLConnection =null;     // initializing http object



        try {
            URL url=new URL(placeUrl);                 // creating url

            httpURLConnection=(HttpURLConnection)url.openConnection();      //open connection

            //now connecting http url connection

            httpURLConnection.connect();        //connect

            //now read the data on url

            inputStream=httpURLConnection.getInputStream();
            BufferedReader bufferedReader=new BufferedReader(new InputStreamReader(inputStream));
            StringBuffer stringBuffer=new StringBuffer();


            // now we r going to read data line by line using while loop

            String line="";   // initializing string and we are going to append this string to string buffer

            while ((line=bufferedReader.readLine())!=null)
            {
                stringBuffer.append(line);
            }
            Data=stringBuffer.toString();

            bufferedReader.close();




        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // basically finally will try execute this block and in exception this code will finally run
        finally {
            inputStream.close();
            httpURLConnection.disconnect();

        }
        return Data;
    }
}
