package lazar.jovanovic.shoppinglist;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

public class MainActivity extends AppCompatActivity implements View.OnClickListener /*implements View.OnClickListener*/ {

    Button bLogin_1, bRegister_1;
    LinearLayout lLinearLayout_1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lLinearLayout_1 = findViewById(R.id.linear_layout_1);

        bLogin_1 = findViewById(R.id.login_1);
        bRegister_1 = findViewById(R.id.register_1);

        bLogin_1.setOnClickListener(this);
        bRegister_1.setOnClickListener(this);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        lLinearLayout_1 = findViewById(R.id.linear_layout_1);
        lLinearLayout_1.setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.login_1:{
                Log.d("Buttons", "Login_1 dugme");
                lLinearLayout_1.setVisibility(View.INVISIBLE);
                fragmentReplace(new loginFragment());
                break;
            }

            case R.id.register_1:{
                Log.d("Buttons", "Register_1 dugme");
                lLinearLayout_1.setVisibility(View.INVISIBLE);
                fragmentReplace(new registerFragment());
                break;
            }

        }
    }

    private void fragmentReplace(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_1, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();

    }
}