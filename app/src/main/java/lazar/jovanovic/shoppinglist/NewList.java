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

public class NewList extends AppCompatActivity implements View.OnClickListener {

    Button bOk, bSave;
    TextView naslov;
    EditText edNaslov;
    boolean bYes;
    boolean bNo;
    RadioGroup rgYN;
    boolean ok_pressed = false;
    String stNaslov;
    DbHelper dbHelper;
    private final String DB_NAME = "database.db";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_list);

        bOk = findViewById(R.id.naslov_ok);
        naslov = findViewById(R.id.naslov);
        edNaslov = findViewById(R.id.naslov_liste);
        bSave = findViewById(R.id.save_button);
        rgYN = findViewById(R.id.radio_group);
        dbHelper = new DbHelper(this, DB_NAME, null, 1);

        bOk.setOnClickListener(this);
        bSave.setOnClickListener(this);
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
                    Boolean rt;
                    Log.d("New_List", "Naslov: " + stNaslov +
                            " Creator: " + creator);
                    if(bYes) {
                        Log.d("New_List", "Naslov: " + stNaslov +
                                " Creator: " + creator);
                        rt = dbHelper.insertList(stNaslov, creator, "yes");
                    }else
                        rt = dbHelper.insertList(stNaslov, creator, "no");

                    if(!rt)
                        Toast.makeText(this, "Smth went wrong with insert", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(this, WelcomeActivity.class);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }else{
                    Toast.makeText(this, "A list must have a headline", Toast.LENGTH_SHORT).show();
                }
            }
        }


    }
}