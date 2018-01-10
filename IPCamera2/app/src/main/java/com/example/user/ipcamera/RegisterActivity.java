package com.example.user.ipcamera;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

public class RegisterActivity extends AppCompatActivity {

    EditText idedit;
    EditText passedit;
    Button register;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        idedit = (EditText) findViewById(R.id.edittext);
        passedit = (EditText) findViewById(R.id.edittext1);
        register = (Button) findViewById(R.id.button);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String id = idedit.getText().toString();
                String password = passedit.getText().toString();
                JSONArray jsonarray = new JSONArray();
                JSONObject jsonobject = new JSONObject();
                try {
                    jsonobject.accumulate("userid", id);
                    jsonobject.accumulate("password", password);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                jsonarray.put(jsonobject);
                Log.i("json", jsonarray.toString());
                NetworkTask registertask = new NetworkTask("register?userid="+id+"&password="+password, "get", null, jsonarray);
                registertask.execute();
                try {
                    String resultstring = registertask.get();
                    JSONObject resultjson = new JSONObject(resultstring);
                    int result = resultjson.getInt("result");
                    if(result==1){
                        AlertDialog.Builder loginfail = new AlertDialog.Builder(RegisterActivity.this);
                        loginfail.setTitle("이미 존재하는 아이디야");
                        loginfail.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        });
                        loginfail.show();
                    }
                    else{
                        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                        startActivity(intent);
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
    }

}
