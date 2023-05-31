package lazar.jovanovic.shoppinglist;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

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
 * Use the {@link loginFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class loginFragment extends Fragment implements View.OnClickListener {

    EditText user, pass;
    Button bLogin, bHome;

    DbHelper dbHelper;
    HttpHelper httpHelper;
    public String POST_LOGIN;
    int flag;
    private final String DB_NAME = "database.db";
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public loginFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment loginFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static loginFragment newInstance(String param1, String param2) {
        loginFragment fragment = new loginFragment();
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
        POST_LOGIN = getString(R.string.BASE_IP) + ":3000/login";
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_login, container, false);

        user = v.findViewById(R.id.username_1);
        pass = v.findViewById(R.id.password_1);
        bLogin = v.findViewById(R.id.login_2);
        bHome = v.findViewById(R.id.home_button1);

        dbHelper = new DbHelper(getContext(), DB_NAME, null, 1);
        httpHelper = new HttpHelper();

        bLogin.setOnClickListener(this);
        bHome.setOnClickListener(this);


        return v;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.login_2:{
                if(!user.getText().toString().isEmpty() || !pass.getText().toString().isEmpty()) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                JSONObject jsonObject = new JSONObject();
                                jsonObject.put("username", user.getText().toString());
                                jsonObject.put("password", pass.getText().toString());
                                Log.d("LOGIN", "URL VALUE: " + POST_LOGIN);
                                flag = httpHelper.postJSONObjectFromURL(POST_LOGIN, jsonObject);
                                Log.d("LOGIN", "FLAG VALUE " + String.valueOf(flag));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            if(flag == 201 || flag == 200){
                                //intent
                                Log.d("LOGIN", user.getText().toString());
                                Intent intent = new Intent(getActivity(), WelcomeActivity.class);

                                Bundle bundle = new Bundle();
                                bundle.putString("user", user.getText().toString());

                                intent.putExtras(bundle);
                                startActivity(intent);
                            }else if(flag == -1){
                                Log.d("LOGIN", "UNSUCCESSFUL");
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(getActivity(), "Connection failed", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }else{
                                Log.d("LOGIN", "UNSUCCESSFUL");
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(getActivity(), "Username already in use!", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }
                    }).start();
                }else{
                    Log.d("LOGIN", "EMPTY");
                    //izbaci mehuric da nije dobar user ili pass
                    Toast toast = Toast.makeText(getContext(), "Username or password cannot be blank", Toast.LENGTH_SHORT);
                    toast.show();
                }
                break;
            } case R.id.home_button1:{
                Log.d("LOGIN_FRAGMENT", "PRESSED THE HOME BUTTON");
                Intent intent = new Intent(getActivity(), MainActivity.class);
                startActivity(intent);
                getActivity().finish();
                break;
            }
        }

    }
}
