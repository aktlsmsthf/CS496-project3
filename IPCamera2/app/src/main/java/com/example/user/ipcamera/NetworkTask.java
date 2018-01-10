package com.example.user.ipcamera;

import android.content.ContentValues;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

/**
 * Created by user on 2017-12-31.
 */
public class NetworkTask extends AsyncTask<Void, Void, String> {

    private String url = "http://13.125.60.120:3000/",routes,method;
    private ContentValues values;
    private JSONArray jsonarray;

    public NetworkTask(String routes, String method, ContentValues values,JSONArray jsonarray) {

        this.routes = routes;
        this.method = method;
        this.values = values;
        this.jsonarray = jsonarray;
    }

    @Override
    protected String doInBackground(Void... params) {
        url = url + routes;
        URL url = null;//url을 가져온다.
        HttpURLConnection con = null;
        try {
            url = new URL(this.url);
            con = (HttpURLConnection) url.openConnection();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.i("method",method);
        Log.i("url", this.url);

        switch (method) {
            case "post":
                Log.i("ss", "ss");
                try {
                    con.setRequestMethod("POST");//POST방식으로 보냄
                    con.setRequestProperty("Cache-Control", "no-cache");//캐시 설정
                    con.setRequestProperty("Content-Type", "application/json");//application JSON 형식으로 전송
                    con.setRequestProperty("Accept", "text/html");//서버에 response 데이터를 html로 받음
                    con.setDoOutput(true);//Outstream으로 post 데이터를 넘겨주겠다는 의미
                    con.setDoInput(true);//Inputstream으로 서버로부터 응답을 받겠다는 의미
                    con.connect(); //서버로 보내기위해서 스트림 만듬
                    OutputStream outStream = con.getOutputStream();
                    //버퍼를 생성하고 넣음
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outStream));
                    try {
                        writer.write(jsonarray.toString());
                    } catch (Exception e) {
                    }
                    writer.flush();
                    writer.close();//버퍼를 받아줌
                    outStream.close();
                    //return "";
                } catch (ProtocolException e) {
                    e.printStackTrace();
                    Log.i("error","first");
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.i("error","second");
                }
                break;
            case "delete":
                Log.i("ss", "ss");
                try {
                    con.setRequestMethod("DELETE");//POST방식으로 보냄
                    con.setRequestProperty("Cache-Control", "no-cache");//캐시 설정
                    con.setRequestProperty("Content-Type", "application/json");//application JSON 형식으로 전송
                    con.setRequestProperty("Accept", "text/html");//서버에 response 데이터를 html로 받음
                    con.setDoOutput(true);//Outstream으로 post 데이터를 넘겨주겠다는 의미
                    con.setDoInput(true);//Inputstream으로 서버로부터 응답을 받겠다는 의미
                    con.connect(); //서버로 보내기위해서 스트림 만듬
                    OutputStream outStream = con.getOutputStream();
                    //버퍼를 생성하고 넣음
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outStream));
                    try {
                        writer.write(jsonarray.toString());
                    } catch (Exception e) {
                    }
                    writer.flush();
                    writer.close();//버퍼를 받아줌
                    outStream.close();
                    //return "";
                } catch (ProtocolException e) {
                    e.printStackTrace();
                    Log.i("error","first");
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.i("error","second");
                }
                break;
            case "get":
                try {
                    con.connect();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case"update":
                try {
                    con.setRequestMethod("PUT");//POST방식으로 보냄
                    con.setRequestProperty("Cache-Control", "no-cache");//캐시 설정
                    con.setRequestProperty("Content-Type", "application/json");//application JSON 형식으로 전송
                    con.setRequestProperty("Accept", "text/html");//서버에 response 데이터를 html로 받음
                    con.setDoOutput(true);//Outstream으로 post 데이터를 넘겨주겠다는 의미
                    con.setDoInput(true);//Inputstream으로 서버로부터 응답을 받겠다는 의미
                    con.connect(); //서버로 보내기위해서 스트림 만듬
                    OutputStream outStream = con.getOutputStream();
                    //버퍼를 생성하고 넣음
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outStream));
                    try {
                        writer.write(jsonarray.toString());
                    } catch (Exception e) {
                    }
                    writer.flush();
                    writer.close();//버퍼를 받아줌
                    outStream.close();
                    //return "";
                } catch (ProtocolException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            default:
                break;
        }

        StringBuffer buffer = null;
        InputStream stream = null;
        try {
            stream = con.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
            buffer = new StringBuffer();
            String line = "";
            while ((line = reader.readLine()) != null) {
                buffer.append(line);
            }
            return buffer.toString();//서버로 부터 받은 값을 리턴해줌 아마 OK!!가 들어올것임
        } catch (IOException e) {
            return null;
//            e.printStackTrace();
        }
    }

    protected void onPostExecute(String s) {
        super.onPostExecute(s);

    }
}