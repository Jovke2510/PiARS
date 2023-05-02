package lazar.jovanovic.shoppinglist;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
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

import java.util.ArrayList;
import java.util.List;

public class WelcomeActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView username;
    private Button bNewList;
    private Button bSeeMyLists;
    private ListView list;
    private ListElementAdapter leAdapter;

    DbHelper dbHelper;
    private final String DB_NAME = "database.db";

    private List<ListElement> sharedLists;
    private String user;
    private Bundle bundle;
    private int seeMyListsPressed = 1;
    private List<ListElement> userLists;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        list = findViewById(R.id.lists);
        leAdapter = new ListElementAdapter(this);
        list.setAdapter(leAdapter);

        dbHelper = new DbHelper(this, DB_NAME, null, 1);

        //bSeeMyLists.findViewById(R.id.see_lists);
        //bSeeMyLists.setOnClickListener(this);

        //funkcija koja prolazi kroz bazu i vraca sve liste koje ulogovani korisnik ima i one koje se dele
        sharedLists = new ArrayList<>();
        userLists = new ArrayList<>();
        dbHelper.findSharedLists(sharedLists);
        if(!sharedLists.isEmpty()){
            updateList(sharedLists, userLists, seeMyListsPressed);
        }

        list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                leAdapter.removeListElement((ListElement) leAdapter.getItem(i));
                return true;
            }
        });

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //Go to the new activity
                Intent intent = new Intent(WelcomeActivity.this, ShowListActivity.class);

                Bundle bundle1 = new Bundle();
                ListElement le = (ListElement) leAdapter.getItem(i);
                bundle1.putString("naslov", le.getmNaslov());
                bundle1.putString("user", bundle.getString("user", "Default"));

                intent.putExtras(bundle1);
                startActivity(intent);

            }
        });

        bundle = getIntent().getExtras();
        username = findViewById(R.id.user_txt);

        user = bundle.getString("user", "Default");
        username.setText(bundle.getString("user", "Default"));

        bNewList = findViewById(R.id.new_list);
        bNewList.setOnClickListener(this);
        bSeeMyLists = findViewById(R.id.see_lists);
        bSeeMyLists.setOnClickListener(this);
    }

    /*private void addSharedListToAdapter(List<String> sharedLists, Boolean shared) {
        for(String listName : sharedLists){
            leAdapter.addListElement(new ListElement(listName, shared));
        }
    }

    private void addUserListToAdapter(String userListsCreator, Boolean userListsShared) {
        leAdapter.addListElement(new ListElement(userListsCreator, userListsShared));
    }

    private void removeListFromAdapter(List<String> sharedLists, List<ListElement> userLists, int seeMyListsPressed){
        leAdapter.removeAllListElements();
        if(seeMyListsPressed == 1){
            //1 for shared lists -1 for user lists
            addSharedListToAdapter(sharedLists, true);
        }else if(seeMyListsPressed == -1){
            //pull string and bool value from userLists
            for(ListElement le : userLists){
                String userListsCreator = le.getmNaslov();
                Boolean userListsShared = le.getmShared();
                addUserListToAdapter(userListsCreator, userListsShared);
            }

        }
    }*/

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
                if(!dbHelper.findUserLists(userLists, user)){
                    Toast toast = Toast.makeText(this, "User has no lists", Toast.LENGTH_SHORT);
                    toast.show();
                    break;
                }
                Log.d("WELCOME_ACTIVITY", "SEE_LISTS PRESSED");
                seeMyListsPressed *= (-1);
                updateList(sharedLists, userLists, seeMyListsPressed);

                //pull string list from userLists
                //removeListFromAdapter(sharedLists, userLists, seeMyListsPressed);
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