package lazar.jovanovic.shoppinglist;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link registerFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class registerFragment extends Fragment implements View.OnClickListener {

    EditText user, email, pass;
    Button bRegister;

    DbHelper dbHelper;
    private final String DB_NAME = "register.db";
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public registerFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment registerFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static registerFragment newInstance(String param1, String param2) {
        registerFragment fragment = new registerFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =  inflater.inflate(R.layout.fragment_register, container, false);

        user = v.findViewById(R.id.username_2);
        email = v.findViewById(R.id.email_1);
        pass = v.findViewById(R.id.password_2);
        bRegister = v.findViewById(R.id.register_2);

        dbHelper = new DbHelper(getContext(), DB_NAME, null, 1);

        bRegister.setOnClickListener(this);

        return v;
    }

    @Override
    public void onClick(View view) {
        if(!user.getText().toString().isEmpty() && !pass.getText().toString().isEmpty() && !email.getText().toString().isEmpty()){
            if(dbHelper.insertRegister(user.getText().toString(), email.getText().toString(), pass.getText().toString())){
                Log.d("REGISTER", "SUCCESSFUL");
                Intent intent = new Intent(getActivity(), WelcomeActivity.class);

                Bundle bundle = new Bundle();
                bundle.putString("user", user.getText().toString());

                intent.putExtras(bundle);

                startActivity(intent);
            }else{
                Log.d("REGISTER", "UNSUCCESSFUL");
                //izbaci mehuric da nije dobar user ili pass
                Toast toast = Toast.makeText(getContext(), "Email or username already used", Toast.LENGTH_SHORT);
                toast.show();
            }
        }else{
            Log.d("REGISTER", "UNSUCCESSFUL");
            //izbaci mehuric da nije dobar user ili pass
            Toast toast = Toast.makeText(getContext(), "All required fields must be filled", Toast.LENGTH_SHORT);
            toast.show();
        }
    }
}