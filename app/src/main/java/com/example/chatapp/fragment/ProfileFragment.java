package com.example.chatapp.fragment;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.chatapp.Models.User;
import com.example.chatapp.R;
import com.example.chatapp.Utils.LocationServiceTask;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {
    TextInputLayout fullnameChange, emailChange, phoneChange, sexChange;
    Button btnUpdate, btnChangePass;
    FirebaseDatabase fDatabase;
    FirebaseAuth fAuth;
    DatabaseReference databaseReference;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
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
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fDatabase = FirebaseDatabase.getInstance();
        fAuth = FirebaseAuth.getInstance();
        btnUpdate = view.findViewById(R.id.btn_update);
        btnChangePass = view.findViewById(R.id.btn_change_password);
        fullnameChange = view.findViewById(R.id.fullName_profile);
        emailChange = view.findViewById(R.id.email_profile);
        phoneChange = view.findViewById(R.id.phone_profile);
        sexChange = view.findViewById(R.id.sex_profile);
        String userID = fAuth.getCurrentUser().getUid();
        fDatabase.getReference().child("users").child(userID).get().addOnSuccessListener(dataSnapshot -> {
            User user = dataSnapshot.getValue(User.class);
            user.setUserID(userID);
            fullnameChange.getEditText().setText(user.getFirstname() + " " + user.getLastname());
            emailChange.getEditText().setText(user.getEmail());
            phoneChange.getEditText().setText(user.getPhone());
            sexChange.getEditText().setText(user.getSex());
        });
        btnChangePass.setOnClickListener(v -> {
            showChangePasswordDialog();
        });
        btnUpdate.setOnClickListener(v -> {
            String fullName = fullnameChange.getEditText().getText().toString();
            String email = emailChange.getEditText().getText().toString();
            String phone = phoneChange.getEditText().getText().toString();
            String lastName = "";
            String firstName = "";
            if (fullName.split("\\w+").length > 1) {
                firstName = fullName.substring(fullName.lastIndexOf(" ") + 1);
                lastName = fullName.substring(0, fullName.lastIndexOf(' '));
            } else {
                firstName = fullName;
            }
            HashMap<String, Object> newData = new HashMap<>();
            newData.put("firstname", firstName);
            newData.put("lastname", lastName);
            newData.put("email", email);
            newData.put("phone", phone);
            databaseReference = fDatabase.getReference("users");
            databaseReference.child(userID).updateChildren(newData).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    fullnameChange.getEditText().setText(fullName);
                    emailChange.getEditText().setText(email);
                    phoneChange.getEditText().setText(phone);
                    Toast.makeText(getActivity().getApplicationContext(), task.isSuccessful() ? "Change Info Successfull" : task.getException().getMessage(), Toast.LENGTH_LONG);
                }
            });
        });
    }

    private void showChangePasswordDialog() {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_change_password, null);
        TextInputLayout oldPass, passUpdate;
        Button btnUpdatePassword;
        passUpdate = view.findViewById(R.id.edt_pass_update);
        oldPass = view.findViewById(R.id.edt_old_pass);
        btnUpdatePassword = view.findViewById(R.id.btn_update_new_password);
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(view);
        final AlertDialog dialog = builder.create();
        builder.create().show();
        btnUpdatePassword.setOnClickListener(v -> {
            String OldPass = oldPass.getEditText().getText().toString().trim();
            String PassUpdate = passUpdate.getEditText().getText().toString().trim();
            if (OldPass.isEmpty()) {
                Snackbar.make(v, "Vui lòng nhập lại mật khẩu cũ!", Snackbar.LENGTH_LONG).show();
                return;
            }
            if (PassUpdate.isEmpty()) {
                Snackbar.make(v, "Vui lòng nhập đầy đủ thông tin!", Snackbar.LENGTH_LONG).show();
                return;
            }
            if (PassUpdate.length() < 8) {
                Snackbar.make(v, "Độ dài mật khẩu trên 8 ký tự.", Snackbar.LENGTH_LONG).show();
                return;
            }
            dialog.dismiss();
            updatePass(OldPass, PassUpdate);
        });
    }

    private void updatePass(String oldPass, String passUpdate) {
        FirebaseUser user = fAuth.getCurrentUser();
        AuthCredential authCredential = EmailAuthProvider.getCredential(user.getEmail(), oldPass);
        user.reauthenticate(authCredential).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                user.updatePassword(passUpdate).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(getActivity(), "Đang cập nhật", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getActivity(), "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getActivity(), "" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}