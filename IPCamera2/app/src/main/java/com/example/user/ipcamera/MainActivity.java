package com.example.user.ipcamera;


import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {

    private EditText idedit;
    private EditText passedit;
    private Button login;
    private Button register;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        idedit = (EditText) findViewById(R.id.edittext);
        passedit = (EditText) findViewById(R.id.edittext1);
        login  =(Button) findViewById(R.id.button);
        register = (Button) findViewById(R.id.button2);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("button", "click");
                String id = idedit.getText().toString();
                String password = passedit.getText().toString();

                JSONArray jsonarray = new JSONArray();
                JSONObject jsonobject = new JSONObject();
                try {
                    jsonobject.accumulate("userid", id);
                    jsonobject.accumulate("password", password);
                    jsonarray.put(jsonobject);
                    Log.i("login", "start");
                    NetworkTask logintask = new NetworkTask("login2?userid="+id+"&password="+password, "get", null, null);
                    logintask.execute();
                    Log.i("login", "finish");
                    String resultstring = logintask.get();
                    Log.i("result","get");
                    JSONObject resultjson = new JSONObject(resultstring);
                    int result = resultjson.getInt("result");
                    if(result==1){
                        Intent intent = new Intent(MainActivity.this, IPCamera.class);
                        startActivity(intent);
                    }
                    else{
                        AlertDialog.Builder loginfail = new AlertDialog.Builder(MainActivity.this);
                        loginfail.setTitle("잘못된 로그인 정보입니다.");
                        loginfail.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        });
                        loginfail.show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }

            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET) +
                ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)!= PackageManager.PERMISSION_GRANTED) {
            Log.i("permission", "permission request");
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.INTERNET,
                    Manifest.permission.READ_CONTACTS
            }, 0);
        } else {

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch(requestCode){
            case 0:
                if(grantResults.length > 0 && grantResults[0]==PackageManager.PERMISSION_GRANTED &&
                        grantResults[1] == PackageManager.PERMISSION_GRANTED){

                }
        }
    }
}