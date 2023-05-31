package lazar.jovanovic.shoppinglist;



import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class WelcomeActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView username;
    private Button bNewList;
    private Button bSeeMyLists, bHome;
    private ListView list;
    private ListElementAdapter leAdapter;

    DbHelper dbHelper;
    private final String DB_NAME = "database.db";
    HttpHelper httpHelper;
    public String BASE_URL;
    public static String DELETE_LIST;
    private List<ListElement> sharedLists;
    private String user;
    private Bundle bundle;
    private int seeMyListsPressed = 1;
    private List<ListElement> userLists;
    private List<ListElement> httpLists;
    private boolean flag;
    private Button bSeeSharedLists;

    private String creator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        BASE_URL = getString(R.string.BASE_IP) + ":3000/lists";

        list = findViewById(R.id.lists);
        leAdapter = new ListElementAdapter(this);
        list.setAdapter(leAdapter);

        dbHelper = new DbHelper(this, DB_NAME, null, 1);
        httpHelper = new HttpHelper();

        //bSeeMyLists.findViewById(R.id.see_lists);
        //bSeeMyLists.setOnClickListener(this);
        bundle = getIntent().getExtras();
        username = findViewById(R.id.user_txt);

        user = bundle.getString("user", "Default");
        username.setText(bundle.getString("user", "Default"));

        //funkcija koja prolazi kroz bazu i vraca sve liste koje ulogovani korisnik ima i one koje se dele
        sharedLists = new ArrayList<>();
        userLists = new ArrayList<>();
        dbHelper.findUserLists(userLists, user);

        if(!userLists.isEmpty()){
            seeMyListsPressed = -1;
            updateList(sharedLists, userLists, seeMyListsPressed);
        }

        list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                String listName = ((ListElement) leAdapter.getItem(i)).getmNaslov();
                String creator =  ((ListElement) leAdapter.getItem(i)).getmUsername();/*bundle.getString("user", "Default");*/
                Boolean shared = (((ListElement) leAdapter.getItem(i)).getmShared());
                DELETE_LIST = BASE_URL + "/" + creator + "/" + listName + "/";
                if(((ListElement) leAdapter.getItem(i)).getmShared()){
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Log.d("NEW_LSIT", "URL VALUE: " + DELETE_LIST);
                                flag = httpHelper.httpDelete(DELETE_LIST);
                                Log.d("NEW_LIST", "FLAG VALUE " + String.valueOf(flag));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            if(flag){
                                Log.d("WELCOME_ACTIVITY", "SUCCESSFUL");
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if(user.equals(creator))
                                            dbHelper.removeList(((ListElement) leAdapter.getItem(i)).getmNaslov());
                                        leAdapter.removeListElement((ListElement) leAdapter.getItem(i));
                                        Toast.makeText(getApplicationContext(), "Delete successful", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }else{
                                Log.d("WELCOME_ACTIVITY", "UNSUCCESSFUL");
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(getApplicationContext(), "Delete unsuccessful", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }
                    }).start();
                }else{
                    dbHelper.removeList(((ListElement) leAdapter.getItem(i)).getmNaslov());
                    leAdapter.removeListElement((ListElement) leAdapter.getItem(i));
                }

                return flag;
            }
        });

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //Go to the new activity
                //user iz bundla je onaj koji je ulogovan
                ListElement le = (ListElement) leAdapter.getItem(i);
                creator = null;

                if(le.getmShared()){
                    CountDownLatch latch = new CountDownLatch(1);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Log.d("WELCOME_ACTIVITY", "NASLOV LISTE: " + le.getmNaslov());
                                creator = httpHelper.findListCreator(le.getmNaslov(), BASE_URL);
                                Log.d("WELCOME_ACTIVITY", "CREATOR: " + creator);
                            } catch (JSONException e) {
                                throw new RuntimeException(e);
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            } finally {
                                latch.countDown();
                            }
                        }
                    }).start();

                    try{
                        latch.await();
                    }catch (InterruptedException e){
                        e.printStackTrace();
                    }
                }else{
                    creator = user;
                }

                Log.d("WELCOME_ACTIVITY", "CREATOR: " + creator);
                Log.d("WELCOME_ACTIVITY", "USER: " + user);
                boolean diff_creator = !creator.equals(user);

                Intent intent = new Intent(WelcomeActivity.this, ShowListActivity.class);

                Bundle bundle1 = new Bundle();
                bundle1.putString("naslov", le.getmNaslov());
                bundle1.putString("user", bundle.getString("user", "Default"));
                bundle1.putBoolean("shared", le.getmShared());
                bundle1.putBoolean("diff_creator", diff_creator);


                intent.putExtras(bundle1);
                startActivity(intent);

            }
        });



        bNewList = findViewById(R.id.new_list);
        bNewList.setOnClickListener(this);
        bSeeMyLists = findViewById(R.id.see_lists);
        bSeeMyLists.setOnClickListener(this);
        bSeeSharedLists = findViewById(R.id.see_shared_lists);
        bSeeSharedLists.setOnClickListener(this);
        bHome = findViewById(R.id.home_button3);
        bHome.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.new_list:{
                Log.d("NEW LIST", "CLICKED");

                Intent intent = new Intent(this, NewList.class);

                //Bundle bundle1 = new Bundle();
                bundle.putString("creator", user);
                intent.putExtras(bundle);

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("New List Dialog");
                builder.setMessage("Are you sure you want to create a new list?");
                builder.setCancelable(false);

                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Log.d("NEW LIST", "CONFIRMED");
                        startActivity(intent);
                    }
                });

                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });

                AlertDialog alertDialog = builder.create();
                alertDialog.show();
                break;
            }
            case R.id.see_lists:{
                dbHelper.findUserLists(userLists, user);
                if(userLists.isEmpty()){
                    Toast toast = Toast.makeText(this, "No user lists", Toast.LENGTH_SHORT);
                    toast.show();
                    break;
                }

                Log.d("WELCOME_ACTIVITY", "SEE_LISTS PRESSED");
                Log.d("WELOCME MRSH", "USER LISTS NUMBER: " + String.valueOf(
                        userLists.size()));
                seeMyListsPressed = -1;
                updateList(sharedLists/*namedLists*/, userLists, seeMyListsPressed);

                break;
            }
            case R.id.see_shared_lists:{
                httpLists = new ArrayList<>();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        JSONArray jsonArray = new JSONArray();
                        JSONObject jsonObject = new JSONObject();
                        try {
                            jsonArray = httpHelper.getJSONArrayFromURL(BASE_URL);
                            if(jsonArray == null){
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast toast = Toast.makeText(getApplicationContext(), "No shared lists available.", Toast.LENGTH_LONG);
                                        toast.show();
                                    }
                                });
                            }

                            for(int i = 0; i < jsonArray.length(); i++){
                                jsonObject = jsonArray.getJSONObject(i);
                                httpLists.add(new ListElement(jsonObject.getString("name"), jsonObject.getBoolean("shared"), jsonObject.getString("creator")));
                            }

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    seeMyListsPressed = 1;
                                    updateList(httpLists/*sharedLists*//*namedLists*/, userLists, seeMyListsPressed);
                                }
                            });
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
                Log.d("WELCOME_ACTIVITY", "SHARED_LISTS PRESSED");
                Log.d("WELOCME MRSH", "SHARED LISTS NUMBER: " + String.valueOf(
                        httpLists.size()));
                break;
            }
            case R.id.home_button3:{
                Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
                startActivity(intent);
                break;
            }
        }

    }

    private void updateList(List<ListElement> sharedLists, List<ListElement> userLists, int seeMyListsPressed) {
        if(seeMyListsPressed == 1){
            leAdapter.updateList(sharedLists);
        }else {
            leAdapter.updateList(userLists);
        }
    }
}