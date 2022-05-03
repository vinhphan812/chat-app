package com.example.chatapp.fragment.signUpFragment;

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
import com.example.chatapp.Utils.FirebaseUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;

import java.util.HashMap;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link UserNameAndPasswordFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UserNameAndPasswordFragment extends Fragment {

    Button btnRegister;
    NavController navController;
    TextInputLayout edtEmail, edtPassword, edtConfirmPassword;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public UserNameAndPasswordFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment username_password.
     */
    // TODO: Rename and change types and number of parameters
    public static UserNameAndPasswordFragment newInstance(String param1, String param2) {
        UserNameAndPasswordFragment fragment = new UserNameAndPasswordFragment();
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
        edtEmail = view.findViewById(R.id.edt_email);
        edtPassword = view.findViewById(R.id.edt_pass);
        edtConfirmPassword = view.findViewById(R.id.edt_pass_confirm);
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Email = edtEmail.getEditText().getText().toString();
                String Password = edtPassword.getEditText().getText().toString();
                String ConfPassword = edtConfirmPassword.getEditText().getText().toString();
                String error = "";
                if (Email.isEmpty()) {
                    error = "Vui lòng điền email!";
                } else if (Password.isEmpty()) {
                    error = "Vui lòng điền mật khẩu!";
                } else if (ConfPassword.isEmpty()) {
                    error = "Vui lòng điền mật khẩu xác nhận!";
                } else if (!Password.equals(ConfPassword)) {
                    error = "mật khẩu xác nhận không trùng khớp!";
                }
                if (!error.isEmpty()) {
                    Snackbar.make(v, error, Snackbar.LENGTH_LONG).show();
                    return;
                }

                Bundle store = getArguments();

                String Firstname = store.getString("firstname");
                String Lastname = store.getString("lastname");
                String Gender = store.getString("gender");
                String Phone = store.getString("phone");

                FirebaseUtils.mAuth.createUserWithEmailAndPassword(Email, Password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (!task.isSuccessful()) {
                                    Toast.makeText(getContext(), task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                    return;
                                }
                                Map<String, Object> info = new HashMap<>();

                                info.put("firstname", Firstname);
                                info.put("lastname", Lastname);
                                info.put("gender", Gender);
                                info.put("phone", Phone);
                                info.put("email", Email);

                                FirebaseUtils.databaseReference.child("users").child(FirebaseUtils.mAuth.getUid()).setValue(info).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        View root = getActivity().getWindow().getDecorView().getRootView();
                                        if (!task.isSuccessful()) {
                                            Toast.makeText(getContext(), task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                        } else {
                                            getActivity().finish();
                                        }
                                    }
                                });
                            }
                        });
            }
        });
    }
}