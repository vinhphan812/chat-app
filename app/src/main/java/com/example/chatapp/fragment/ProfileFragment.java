package com.example.chatapp.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.chatapp.Models.User;
import com.example.chatapp.R;
import com.example.chatapp.Utils.LocationServiceTask;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {
    EditText fullnameChange, emailChange, phoneChange, addressChange;
    Button btnUpdate;
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
        fullnameChange = view.findViewById(R.id.fullName_profile);
        emailChange = view.findViewById(R.id.email_profile);
        phoneChange = view.findViewById(R.id.phone_profile);
        addressChange = view.findViewById(R.id.address_profile);
        String userID = fAuth.getCurrentUser().getUid();
        fDatabase.getReference().child("users").child(userID).get().addOnSuccessListener(dataSnapshot -> {
            User user = dataSnapshot.getValue(User.class);
            user.setUserID(userID);
            fullnameChange.setText(user.getLastName() + " " + user.getFirstName());
            emailChange.setText(user.getEmail());
            phoneChange.setText(user.getPhone());
            addressChange.setText(user.getAddress());
        });
        btnUpdate.setOnClickListener(v -> {
            String fullName = fullnameChange.getText().toString();
            String email = emailChange.getText().toString();
            String phone = phoneChange.getText().toString();
            String address = addressChange.getText().toString();

            String lastName = "";
            String firstName = "";
            if (fullName.split("\\w+").length > 1) {
                firstName = fullName.substring(fullName.lastIndexOf(" ") + 1);
                lastName = fullName.substring(0, fullName.lastIndexOf(' '));
            } else {
                firstName = fullName;
            }

            LatLng latLng = LocationServiceTask.getLatLngFromAddress(getContext(), address.toString());
            HashMap<String, Object> newData = new HashMap<>();
            newData.put("firstname", firstName);
            newData.put("lastname", lastName);
            newData.put("email", email);
            newData.put("phone", phone);
            newData.put("address", address);
            newData.put("latitude", latLng.latitude);
            newData.put("longitude", latLng.longitude);


            databaseReference = fDatabase.getReference("users");
            databaseReference.child(userID).updateChildren(newData).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    fullnameChange.setText(fullName);
                    emailChange.setText(email);
                    phoneChange.setText(phone);
                    addressChange.setText(address);

                    Toast.makeText(getActivity().getApplicationContext(), task.isSuccessful() ? "Change Info Successfull" : task.getException().getMessage(), Toast.LENGTH_LONG);
                }
            });
        });
    }
}