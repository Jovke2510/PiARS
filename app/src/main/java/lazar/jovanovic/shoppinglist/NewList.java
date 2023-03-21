package lazar.jovanovic.shoppinglist;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

public class NewList extends AppCompatActivity implements View.OnClickListener {

    Button bOk;
    TextView naslov;
    EditText edNaslov;
    RadioButton bYes;
    RadioButton bNo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_list);

        bOk = findViewById(R.id.naslov_ok);
        naslov = findViewById(R.id.naslov);
        edNaslov = findViewById(R.id.naslov_liste);
        bYes = findViewById(R.id.yes_button);
        bNo = findViewById(R.id.no_button);

        bOk.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        naslov.setText(edNaslov.getText().toString());
    }
}