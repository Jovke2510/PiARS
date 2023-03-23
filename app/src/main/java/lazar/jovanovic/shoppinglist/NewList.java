package lazar.jovanovic.shoppinglist;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

public class NewList extends AppCompatActivity implements View.OnClickListener {

    Button bOk, bSave;
    TextView naslov;
    EditText edNaslov;
    RadioButton bYes;
    RadioButton bNo;
    boolean ok_pressed = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_list);

        bOk = findViewById(R.id.naslov_ok);
        naslov = findViewById(R.id.naslov);
        edNaslov = findViewById(R.id.naslov_liste);
        bYes = findViewById(R.id.yes_button);
        bNo = findViewById(R.id.no_button);
        bSave = findViewById(R.id.save_button);

        bOk.setOnClickListener(this);
        bSave.setOnClickListener(this);

    }
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.naslov_ok:{
                naslov.setText(edNaslov.getText().toString());
                edNaslov.getText().clear();
                ok_pressed = true;
                break;
            }
            case R.id.save_button:{
                if(ok_pressed == true){
                    Intent intent = new Intent(this, WelcomeActivity.class);
                    startActivity(intent);
                }else{
                    Toast.makeText(this, "A list must have a headline", Toast.LENGTH_SHORT).show();
                }
            }
        }


    }
}