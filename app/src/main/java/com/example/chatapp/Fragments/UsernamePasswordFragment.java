package com.example.chatapp.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.chatapp.R;
import com.example.chatapp.Utils.Callback;
import com.example.chatapp.Utils.Services;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;

import java.util.HashMap;



/**
 * A simple {@link Fragment} subclass.
 * Use the {@link UsernamePasswordFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UsernamePasswordFragment extends Fragment {

    Button btnRegister;
    NavController navController;
    TextInputEditText inpEmail, inpPassword, inpConfirmPassword;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public UsernamePasswordFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment UsernamePasswordFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static UsernamePasswordFragment newInstance(String param1, String param2) {
        UsernamePasswordFragment fragment = new UsernamePasswordFragment();
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
        return inflater.inflate(R.layout.fragment_username_password, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        btnRegister = view.findViewById(R.id.btnRegister);

        navController = Navigation.findNavController(view);

        inpEmail = view.findViewById(R.id.tvEmail);
        inpPassword = view.findViewById(R.id.tvPassword);
        inpConfirmPassword = view.findViewById(R.id.tvConfirmPassword);

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = inpEmail.getText().toString(),
                        password = inpPassword.getText().toString(),
                        confPassword = inpConfirmPassword.getText().toString();

                String error = "";

                if (email.isEmpty()) {
                    error = "Vui lòng điền email!";
                } else if (password.isEmpty()) {
                    error = "Vui lòng điền mật khẩu!";
                } else if (confPassword.isEmpty()) {
                    error = "Vui lòng điền mật khẩu xác nhận!";
                } else if (!password.equals(confPassword)) {
                    error = "mật khẩu xác nhận không trùng khớp!";
                }

                if (!error.isEmpty()) {
                    Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
                    return;
                }

                Bundle store = getArguments();

                String firstname = store.getString("firstname");
                String lastname = store.getString("lastname");
                String address = store.getString("Address");
                String phone = store.getString("Phone");
                Double latitude = store.getDouble("latitude", 0);
                Double longitude = store.getDouble("longitude", 0);

                HashMap<String, Object> info = new HashMap<>();

                info.put("firstname", firstname);
                info.put("lastname", lastname);
                info.put("address", address);
                info.put("phone", phone);
                info.put("email", email);
                info.put("latitude", latitude);
                info.put("longitude", longitude);


                Services.registerAccount(email, password, info, new Callback() {
                    @Override
                    public void onError(Exception error) {
                        Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void call(@NonNull Task<AuthResult> task) {
                        getActivity().finish();
                    }
                });

            }
        });
    }
}