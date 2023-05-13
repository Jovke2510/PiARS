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
    private List<ListElement> namedLists;

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
        /*dbHelper.findSharedLists(sharedLists);
        if(!sharedLists.isEmpty()){
            seeMyListsPressed = 1;
            updateList(sharedLists, userLists, seeMyListsPressed);
        }*/

        namedLists = new ArrayList<>();
        String nameList = "lista25";
        if(!dbHelper.findListsNamed(namedLists, nameList)){
            Toast toast = Toast.makeText(this, "No lists named " + nameList, Toast.LENGTH_SHORT);
            toast.show();
        }else{
            if(!namedLists.isEmpty()){
                seeMyListsPressed = 1;
                updateList(namedLists, userLists, seeMyListsPressed);
            }
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
                Log.d("WELOCME MRSH", "USER LISTS NUMBER: " + String.valueOf(
                        userLists.size()));
                seeMyListsPressed *= (-1);
                updateList(/*sharedLists*/namedLists, userLists, seeMyListsPressed);

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