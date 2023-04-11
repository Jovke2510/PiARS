package lazar.jovanovic.shoppinglist;

import static java.security.AccessController.getContext;

import androidx.appcompat.app.AppCompatActivity;

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

public class ShowListActivity extends AppCompatActivity implements View.OnClickListener {

    private ListView lvTasks;
    private TaskElementAdapter teAdapter;
    private Button bAdd;
    private EditText edTaskAdd;
    private TextView tvTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_list);

        lvTasks = findViewById(R.id.task_id);
        teAdapter = new TaskElementAdapter(this);
        lvTasks.setAdapter(teAdapter);

        lvTasks.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                teAdapter.removeTaskElements((TaskElement) teAdapter.getItem(i));
                return true;
            }
        });

        bAdd = findViewById(R.id.task_add);
        edTaskAdd = findViewById(R.id.task_title);
        tvTitle = findViewById(R.id.sh_naslov);

        Bundle bundle = getIntent().getExtras();
        tvTitle.setText(bundle.getString("naslov", "default"));

        bAdd.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        if(edTaskAdd.getText().toString().isEmpty()){
            Toast toast = Toast.makeText(this, "Task title can't be empty", Toast.LENGTH_SHORT);
            toast.show();
        }else{
            teAdapter.addTaskElements(new TaskElement(edTaskAdd.getText().toString(), false));
            edTaskAdd.getText().clear();
        }
    }
}