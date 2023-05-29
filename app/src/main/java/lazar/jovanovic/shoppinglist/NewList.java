package lazar.jovanovic.shoppinglist;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class NewList extends AppCompatActivity implements View.OnClickListener {

    Button bOk, bSave, bHome;
    TextView naslov;
    EditText edNaslov;
    boolean bYes;
    boolean bNo;
    RadioGroup rgYN;
    boolean ok_pressed = false;
    String stNaslov;
    DbHelper dbHelper;
    private final String DB_NAME = "database.db";
    HttpHelper httpHelper;
    public static String POST_LIST = "http://192.168.5.106:3000/lists";
    int flag;
    boolean rt_http;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_list);

        bOk = findViewById(R.id.naslov_ok);
        naslov = findViewById(R.id.naslov);
        edNaslov = findViewById(R.id.naslov_liste);
        bSave = findViewById(R.id.save_button);
        rgYN = findViewById(R.id.radio_group);
        bHome = findViewById(R.id.home_button4);
        dbHelper = new DbHelper(this, DB_NAME, null, 1);
        httpHelper = new HttpHelper();

        bOk.setOnClickListener(this);
        bSave.setOnClickListener(this);
        bHome.setOnClickListener(this);
        rgYN.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i){
                    case R.id.yes_button:{
                        bYes = true;
                        bNo = false;
                        break;
                    }
                    case R.id.no_button:{
                        bYes = false;
                        bNo = true;
                        break;
                    }
                }
            }
        });


    }
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.naslov_ok:{
                if(edNaslov.getText().toString().isEmpty()) {
                    Toast.makeText(this, "A list must have a headline", Toast.LENGTH_SHORT).show();
                    break;
                }else{
                    naslov.setText(edNaslov.getText().toString());
                    stNaslov = edNaslov.getText().toString();
                    edNaslov.getText().clear();
                    ok_pressed = true;
                    break;
                }
            }
            case R.id.save_button:{
                if(ok_pressed == true){
                    Bundle bundle = getIntent().getExtras();
                    String creator = bundle.getString("creator", "Default");
                    Boolean rt_db;
                    Log.d("New_List", "Naslov: " + stNaslov +
                            " Creator: " + creator);
                    if(bYes) {
                        Log.d("New_List", "Naslov: " + stNaslov +
                                " Creator: " + creator);
                        rt_db = dbHelper.insertList(stNaslov, creator, "yes");
                        //Ovde dodati za server
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    JSONObject jsonObject = new JSONObject();
                                    jsonObject.put("name", stNaslov);
                                    jsonObject.put("creator", creator);
                                    jsonObject.put("shared", "yes");
                                    Log.d("NEW_LSIT", "URL VALUE: " + POST_LIST);
                                    flag = httpHelper.postJSONObjectFromURL(POST_LIST, jsonObject);
                                    Log.d("NEW_LIST", "FLAG VALUE " + String.valueOf(flag));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

                                if(flag == 200 || flag == 201){
                                    rt_http = true;
                                }else if(flag == -1){
                                    rt_http = false;
                                    Log.d("NEW_LIST", "UNSUCCESSFUL");
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(getApplicationContext(), "Connection failed", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }else{
                                    Log.d("NEW_LIST", "UNSUCCESSFUL");
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(getApplicationContext(), "List name invalid/in use", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            }
                        }).start();
                    }else
                        rt_db = dbHelper.insertList(stNaslov, creator, "no");

                    if(!rt_db && !rt_http)
                        Toast.makeText(this, "Smth went wrong with insert", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(this, WelcomeActivity.class);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }else{
                    Toast.makeText(this, "A list must have a headline", Toast.LENGTH_SHORT).show();
                }
                break;
            }
            case R.id.home_button4:{
                Intent intent = new Intent(NewList.this, MainActivity.class);
                startActivity(intent);
                break;
            }
        }


    }
}