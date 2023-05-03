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

import java.util.ArrayList;
import java.util.List;

public class ShowListActivity extends AppCompatActivity implements View.OnClickListener {

    private ListView lvTasks;
    private TaskElementAdapter teAdapter;
    private Button bAdd;
    private EditText edTaskAdd;
    private TextView tvTitle;
    private DbHelper dbHelper;
    private final String DB_NAME = "database.db";
    private String stNaslov;
    private List<TaskElement> itemsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_list);

        lvTasks = findViewById(R.id.task_id);
        teAdapter = new TaskElementAdapter(this);
        lvTasks.setAdapter(teAdapter);

        dbHelper = new DbHelper(this, DB_NAME, null, 1);

        Bundle bundle = getIntent().getExtras();
        stNaslov = bundle.getString("naslov", "default");

        lvTasks.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                dbHelper.removeItem((TaskElement) teAdapter.getItem(i), stNaslov);
                teAdapter.removeTaskElements((TaskElement) teAdapter.getItem(i));
                return true;
            }
        });



        bAdd = findViewById(R.id.task_add);
        edTaskAdd = findViewById(R.id.task_title);
        tvTitle = findViewById(R.id.sh_naslov);
        tvTitle.setText(stNaslov);

        itemsList = new ArrayList<>();
        dbHelper.findItems(itemsList, stNaslov);
        if(!itemsList.isEmpty())
            teAdapter.updateTasks(itemsList);

        bAdd.setOnClickListener(this);

    }


    //TO DO
    //method that tracks if a task is checked or not (check if it does this automatically)
    @Override
    public void onClick(View view) {
        if(edTaskAdd.getText().toString().isEmpty()){
            Toast toast = Toast.makeText(this, "Task title can't be empty", Toast.LENGTH_SHORT);
            toast.show();
        }else{
            //add to database first then to adapter
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

    public String getItemListName() {
        return stNaslov;
    }
}