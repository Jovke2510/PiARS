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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link registerFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class registerFragment extends Fragment implements View.OnClickListener {

    EditText user, email, pass;
    Button bRegister, bHome;

    DbHelper dbHelper;
    HttpHelper httpHelper;
    int flag;
    public static String POST_REGISTER = "http://192.168.5.106:3000/users";
    private final String DB_NAME = "database.db";
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
        bHome = v.findViewById(R.id.home_button2);

        dbHelper = new DbHelper(getContext(), DB_NAME, null, 1);
        httpHelper = new HttpHelper();

        bRegister.setOnClickListener(this);
        bHome.setOnClickListener(this);

        return v;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.register_2:{
                if(!user.getText().toString().isEmpty() && !pass.getText().toString().isEmpty() && !email.getText().toString().isEmpty()){
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                JSONObject jsonObject = new JSONObject();
                                jsonObject.put("username", user.getText().toString());
                                jsonObject.put("password", pass.getText().toString());
                                jsonObject.put("email", email.getText().toString());
                                Log.d("REGISTER", "URL VALUE: " + POST_REGISTER);
                                flag = httpHelper.postJSONObjectFromURL(POST_REGISTER, jsonObject);
                                Log.d("REGISTER", "FLAG VALUE " + String.valueOf(flag));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            if(flag == 200 || flag == 201){
                                if (dbHelper.insertRegister(user.getText().toString(), email.getText().toString(), pass.getText().toString())) {
                                    Log.d("REGISTER", "SUCCESSFUL");
                                    Intent intent = new Intent(getActivity(), WelcomeActivity.class);

                                    Bundle bundle = new Bundle();
                                    bundle.putString("user", user.getText().toString());

                                    intent.putExtras(bundle);

                                    startActivity(intent);
                                } else {
                                    Log.d("REGISTER", "UNSUCCESSFUL");
                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(getActivity(), "Registration not successful!", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            }else if(flag == -1){
                                Log.d("REGISTER", "UNSUCCESSFUL");
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(getActivity(), "Connection failed", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }else{
                                Log.d("REGISTER", "UNSUCCESSFUL");
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(getActivity(), "Username or email invalid/in use", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }
                    }).start();
                }else{
                    Log.d("REGISTER", "UNSUCCESSFUL");
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getActivity(), "Non of the fields can be empty", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                break;
            }
            case R.id.home_button2:{
                Intent intent = new Intent(getActivity(), MainActivity.class);
                startActivity(intent);
                getActivity().finish();
                break;
            }
        }


    }
}