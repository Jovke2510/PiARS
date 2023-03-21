package lazar.jovanovic.shoppinglist;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class WelcomeActivity extends AppCompatActivity implements View.OnClickListener {

    TextView username;
    Button bNewList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

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