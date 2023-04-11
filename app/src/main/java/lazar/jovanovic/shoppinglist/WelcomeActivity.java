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

public class WelcomeActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView username;
    private Button bNewList;
    private ListView list;
    private ListElementAdapter leAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        list = findViewById(R.id.lists);
        leAdapter = new ListElementAdapter(this);
        list.setAdapter(leAdapter);

        leAdapter.addListElement(new ListElement("Lista 1", true));
        leAdapter.addListElement(new ListElement("Lista 2", true));
        leAdapter.addListElement(new ListElement("Lista 3", false));
        leAdapter.addListElement(new ListElement("Lista 4", true));
        leAdapter.addListElement(new ListElement("Lista 5", false));
        leAdapter.addListElement(new ListElement("Lista 6", true));
        leAdapter.addListElement(new ListElement("Lista 7", false));
        leAdapter.addListElement(new ListElement("Lista 8", false));
        leAdapter.addListElement(new ListElement("Lista 9", false));
        leAdapter.addListElement(new ListElement("Lista 10", false));
        leAdapter.addListElement(new ListElement("Lista 11", true));
        leAdapter.addListElement(new ListElement("Lista 12", true));
        leAdapter.addListElement(new ListElement("Lista 13", false));
        leAdapter.addListElement(new ListElement("Lista 14", true));
        leAdapter.addListElement(new ListElement("Lista 15", false));
        leAdapter.addListElement(new ListElement("Lista 16", true));
        leAdapter.addListElement(new ListElement("Lista 17", true));
        leAdapter.addListElement(new ListElement("Lista 18", false));
        leAdapter.addListElement(new ListElement("Lista 19", true));
        leAdapter.addListElement(new ListElement("Lista 20", false));

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

                Bundle bundle = new Bundle();
                ListElement le = (ListElement) leAdapter.getItem(i);
                bundle.putString("naslov", le.getmNaslov());

                intent.putExtras(bundle);
                startActivity(intent);

            }
        });

        Bundle bundle = getIntent().getExtras();
        username = findViewById(R.id.user_txt);

        username.setText(bundle.getString("user", "Default"));

        bNewList = findViewById(R.id.new_list);
        bNewList.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        Log.d("NEW LIST", "CLICKED");

        Intent intent = new Intent(this, NewList.class);

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
    }
}