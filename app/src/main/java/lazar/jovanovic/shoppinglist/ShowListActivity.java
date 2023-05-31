package lazar.jovanovic.shoppinglist;

import static java.security.AccessController.getContext;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ShowListActivity extends AppCompatActivity implements View.OnClickListener {

    public String POST_TASKS;
    private ListView lvTasks;
    private TaskElementAdapter teAdapter;
    private Button bAdd, bRefresh, bHome;
    private EditText edTaskAdd;
    private TextView tvTitle;
    private DbHelper dbHelper;
    private HttpHelper httpHelper;
    private final String DB_NAME = "database.db";
    private String stNaslov;
    private boolean shared, diff_creator;
    private List<TaskElement> itemsList;
    private int flag;
    private boolean flag2;
    public static String GET_TASKS;
    public static String DELETE_LIST = "";
    public static String BASE_URL;
    private String Id = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_list);

        POST_TASKS = getString(R.string.BASE_IP) + ":3000/tasks";
        GET_TASKS = getString(R.string.BASE_IP) + ":3000/tasks";
        BASE_URL = getString(R.string.BASE_IP) + ":3000/tasks";

        lvTasks = findViewById(R.id.task_id);
        teAdapter = new TaskElementAdapter(this);
        lvTasks.setAdapter(teAdapter);

        dbHelper = new DbHelper(this, DB_NAME, null, 1);
        httpHelper = new HttpHelper();

        bAdd = findViewById(R.id.task_add);
        bRefresh = findViewById(R.id.refresh_button);
        edTaskAdd = findViewById(R.id.task_title);
        bHome = findViewById(R.id.home_button5);

        Bundle bundle = getIntent().getExtras();
        stNaslov = bundle.getString("naslov", "default");

        shared = bundle.getBoolean("shared", Boolean.parseBoolean("default"));
        diff_creator = bundle.getBoolean("diff_creator", Boolean.parseBoolean("default"));
        lvTasks.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                TaskElement te = (TaskElement) teAdapter.getItem(i);
                String url = te.getmNaslov() + te.getmItemListName();
                Log.d("SHOWLISTACTIVITY", "STINNG URL: " + url);


                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        JSONArray jsonArray = new JSONArray();
                        JSONObject jsonObject = new JSONObject();
                        try {
                            jsonArray = httpHelper.getJSONArrayFromURL(BASE_URL + "/" + te.getmItemListName() + "/");
                            if(jsonArray == null){
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast toast = Toast.makeText(getApplicationContext(), "No tasks available.", Toast.LENGTH_LONG);
                                        toast.show();
                                    }
                                });
                            }
                            for(int i = 0; i < jsonArray.length(); i++){
                                jsonObject = jsonArray.getJSONObject(i);
                                if(jsonObject.getString("name").equals(te.getmNaslov())){
                                    Id = jsonObject.getString("_id");
                                    Log.d("SHOW_LIST_ACTIVITY", Id);
                                }
                                //httpTasks.add(new TaskElement(jsonObject.getString("name"),
                                        //jsonObject.getBoolean("done"), jsonObject.getString("taskId")));
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();

                if(shared){
                    DELETE_LIST = BASE_URL + "/" + Id;

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Log.d("SHOW_LIST_ACTIVITY", "URL VALUE: " + DELETE_LIST);
                                flag2 = httpHelper.httpDelete(DELETE_LIST);
                                Log.d("SHOW_LIST_ACTIVITY", "FLAG VALUE " + String.valueOf(flag));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            if(flag2){
                                Log.d("SHOW_LIST_ACTIVITY", "SUCCESSFUL");
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        dbHelper.removeList(((TaskElement) teAdapter.getItem(i)).getmNaslov());
                                        teAdapter.removeTaskElements((TaskElement) teAdapter.getItem(i));
                                        Toast.makeText(getApplicationContext(), "Delete successful", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }else{
                                Log.d("SHOW_LIST_ACTIVITY", "UNSUCCESSFUL");
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(getApplicationContext(), "Delete unsuccessful", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }
                    }).start();
                }

                dbHelper.removeItem((TaskElement) teAdapter.getItem(i), stNaslov);
                teAdapter.removeTaskElements((TaskElement) teAdapter.getItem(i));
                return true;
            }
        });




        tvTitle = findViewById(R.id.sh_naslov);
        tvTitle.setText(stNaslov);

        //add visibility that depends on user
        Log.d("SHOW_LIST_ACTIVITY", "DIFF_CREATOR: " + String.valueOf(diff_creator));
        if(diff_creator)
            bRefresh.setVisibility(View.VISIBLE);
        else
            bRefresh.setVisibility(View.INVISIBLE);

        itemsList = new ArrayList<>();
        dbHelper.findItems(itemsList, stNaslov);
        if(!itemsList.isEmpty())
            teAdapter.updateTasks(itemsList);

        bAdd.setOnClickListener(this);
        bRefresh.setOnClickListener(this);
        bHome.setOnClickListener(this);

    }


    //TO DO
    //method that tracks if a task is checked or not (check if it does this automatically)
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.task_add:{
                if(edTaskAdd.getText().toString().isEmpty()){
                    Toast toast = Toast.makeText(this, "Task title can't be empty", Toast.LENGTH_SHORT);
                    toast.show();
                }else{
                    //add to http server first database second then to adapter
                    if(shared){
                        //send post
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    JSONObject jsonObject = new JSONObject();
                                    jsonObject.put("name", edTaskAdd.getText().toString());
                                    jsonObject.put("list", stNaslov);
                                    jsonObject.put("done", "false");
                                    jsonObject.put("taskId", edTaskAdd.getText().toString() + stNaslov);
                                    Log.d("SHOW_LIST_ACTIVITY", "URL VALUE: " + POST_TASKS);
                                    flag = httpHelper.postJSONObjectFromURL(POST_TASKS, jsonObject);
                                    Log.d("SHOW_LIST_ACTIVITY", "FLAG VALUE " + String.valueOf(flag));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

                                if(flag == 200 || flag == 201){
                                    Log.d("SHOW_LIST_ACTIVITY", "SUCCESSFUL");
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(getApplicationContext(), "SUCCESS", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }else if(flag == -1){
                                    Log.d("SHOW_LIST_ACTIVITY", "UNSUCCESSFUL");
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(getApplicationContext(), "Connection failed", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }else{
                                    Log.d("SHOW_LIST_ACTIVITY", "UNSUCCESSFUL");
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(getApplicationContext(), "Username or email invalid/in use", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            }
                        }).start();

                    }
                    TaskElement te = dbHelper.insertTask(edTaskAdd.getText().toString(), stNaslov, "no", edTaskAdd.getText().toString() + stNaslov);
                    Log.d("ShowListActivity", "STING koji glumi id: " + edTaskAdd.getText().toString() + stNaslov);
                    if(te == null){
                        Toast toast = Toast.makeText(this, "Name already used", Toast.LENGTH_SHORT);
                        toast.show();
                        return;
                    }
                    teAdapter.addTaskElements(te);
                    edTaskAdd.getText().clear();

                }
            }
            case R.id.refresh_button:{
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        JSONArray jsonArray = new JSONArray();
                        JSONObject jsonObject = new JSONObject();
                        try {
                            jsonArray = httpHelper.getJSONArrayFromURL(GET_TASKS + "/" + stNaslov);
                            if(jsonArray == null){
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast toast = Toast.makeText(getApplicationContext(), "No tasks available.", Toast.LENGTH_LONG);
                                        toast.show();
                                    }
                                });
                            }
                            List<TaskElement> httpTasks = new ArrayList<>();
                            for(int i = 0; i < jsonArray.length(); i++){
                                jsonObject = jsonArray.getJSONObject(i);
                                httpTasks.add(new TaskElement(jsonObject.getString("name"),
                                        jsonObject.getBoolean("done"), jsonObject.getString("taskId")));
                            }

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    teAdapter.updateTasks(httpTasks);
                                }
                            });
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
                break;
            }
            case R.id.home_button5:{
                Intent intent = new Intent(ShowListActivity.this, MainActivity.class);
                startActivity(intent);
                break;
            }
        }

    }

    public String getItemListName() {
        return stNaslov;
    }
}