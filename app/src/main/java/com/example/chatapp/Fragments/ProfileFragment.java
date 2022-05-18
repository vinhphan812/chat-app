package com.example.chatapp.Fragments;

import static com.example.chatapp.Utils.Services.mAuth;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.example.chatapp.Models.User;
import com.example.chatapp.R;
import com.example.chatapp.Utils.Callback;
import com.example.chatapp.Utils.LocationServiceTask;
import com.example.chatapp.Utils.Services;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {
    EditText fullnameChange, emailChange, phoneChange, addressChange;
    Button btnUpdate, btnChangePass;
    FirebaseDatabase fDatabase;
    NavigationView navigationView;
    String userID;

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
           btnUpdate = view.findViewById(R.id.btn_update);
        fullnameChange = view.findViewById(R.id.fullName_profile);
        emailChange = view.findViewById(R.id.email_profile);
        phoneChange = view.findViewById(R.id.phone_profile);
        addressChange = view.findViewById(R.id.address_profile);
        navigationView = getActivity().getWindow().findViewById(R.id.navigationView);

        User user = Services.getUserInfo();

        fullnameChange.setText(user.Fullname());
        emailChange.setText(user.email);
        phoneChange.setText(user.phone);
        addressChange.setText(user.address);

        btnUpdate.setOnClickListener(v -> {
            String fullName = fullnameChange.getText().toString();
            String email = emailChange.getText().toString();
            String phone = phoneChange.getText().toString();
            String address = addressChange.getText().toString();

            String lastName = "";
            String firstName = "";
            if (fullName.split("\\w+").length > 1) {
                lastName = fullName.substring(fullName.lastIndexOf(" ") + 1);
                firstName = fullName.substring(0, fullName.lastIndexOf(' '));
            } else {
                firstName = fullName;
            }

            LatLng latLng = LocationServiceTask.getLatLngFromAddress(getContext(), address);

            HashMap<String, Object> newInfo = new HashMap<>();
            newInfo.put("firstname", firstName);
            newInfo.put("lastname", lastName);
            newInfo.put("email", email);
            newInfo.put("phone", phone);
            newInfo.put("address", address);
            newInfo.put("latitude", latLng.latitude);
            newInfo.put("longitude", latLng.longitude);

            Services.updateUserInfo(newInfo, new Callback() {
                @Override
                public void callVoid(@NonNull Task<Void> task) {
                    Toast.makeText(getActivity().getApplicationContext(), task.isSuccessful() ? "Change Info Successful" : task.getException().getMessage(), Toast.LENGTH_LONG).show();

                    if (!task.isSuccessful())
                        return;

                    View view = navigationView.getHeaderView(0);

                    TextView tvFullname = view.findViewById(R.id.name_header);
                    TextView tvEmail = view.findViewById(R.id.email_header);

                    tvFullname.setText(fullName);
                    tvEmail.setText(email);

                    fullnameChange.setText(fullName);
                    emailChange.setText(email);
                    phoneChange.setText(phone);
                    addressChange.setText(address);
                }
            });
        });
        btnChangePass = view.findViewById(R.id.btn_change_password);
        btnChangePass.setOnClickListener(v -> {
            v = LayoutInflater.from(getContext()).inflate(R.layout.dialog_change_pass, null);

            TextInputLayout edtOldPass = v.findViewById(R.id.edt_old_pass);
            TextInputLayout edtNewPass = v.findViewById(R.id.edt_new_pass);
            TextInputLayout edtConfirmPass = v.findViewById(R.id.edt_pass_confirm);

            Button btnChangePass = v.findViewById(R.id.btn_change_password);

            final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setView(v);
            final AlertDialog dialog = builder.create();

            btnChangePass.setOnClickListener(v1 -> {
                String oldPass = edtOldPass.getEditText().getText().toString();
                String newPass = edtNewPass.getEditText().getText().toString();
                String confirm = edtConfirmPass.getEditText().getText().toString();
                if (oldPass.isEmpty()) {
                    Toast.makeText(getContext(), "Please re-enter the old password!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (newPass.length() < 8) {
                    Toast.makeText(getContext(), "Password must be 8 characters or more!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (newPass.equals(oldPass)) {
                    Toast.makeText(getContext(), "The new password and the old password cannot be the same!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(!newPass.equals(confirm)) {
                    Toast.makeText(getContext(), "Incorrect confirm password!", Toast.LENGTH_SHORT).show();
                    return;
                }

                updatePassword(oldPass, newPass, new Callback() {
                    @Override
                    public void call() {
                        super.call();
                        dialog.dismiss();
                    }
                });
            });
            dialog.show();
        });
    }

    private void updatePassword(String oldPass, String newPass, Callback callback) {
        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        AuthCredential authCredential = EmailAuthProvider.getCredential(firebaseUser.getEmail(), oldPass);
        firebaseUser.reauthenticate(authCredential).addOnSuccessListener(unused -> {
            firebaseUser.updatePassword(newPass).addOnSuccessListener(unused1 -> {
                Toast.makeText(getActivity(), "Password updated!!!", Toast.LENGTH_SHORT).show();
            }).addOnFailureListener(e -> {
                Toast.makeText(getActivity(), "" + e.getMessage(), Toast.LENGTH_SHORT).show();
            });
        }).addOnFailureListener(e -> {
            Toast.makeText(getActivity(), "" + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
        callback.call();
    }
}