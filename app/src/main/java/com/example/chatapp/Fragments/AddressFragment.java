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
import com.example.chatapp.Utils.LocationServiceTask;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.textfield.TextInputEditText;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AddressFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddressFragment extends Fragment {
    Button bntNext;
    NavController navController;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public AddressFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AddressFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AddressFragment newInstance(String param1, String param2) {
        AddressFragment fragment = new AddressFragment();
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
        return inflater.inflate(R.layout.fragment_address, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        bntNext = view.findViewById(R.id.btnNext);
        navController = Navigation.findNavController(view);
        TextInputEditText addressEdt = view.findViewById(R.id.Address);
        TextInputEditText phoneEdt = view.findViewById(R.id.Mobile);

        bntNext.setOnClickListener(v -> {
            String address = addressEdt.getText().toString();
            String phone = phoneEdt.getText().toString();

            Bundle bundle = new Bundle(getArguments());

            LatLng latLng = LocationServiceTask.getLatLngFromAddress(getContext(), address);

            if (address.isEmpty() || phone.isEmpty()) {
                Toast.makeText(getContext(),"Vui lòng nhập đầy đủ thông tin!", Toast.LENGTH_LONG).show();
                return;
            } else {
                bundle.putString("Address", address);
                bundle.putString("Phone", phone);
                bundle.putDouble("latitude", latLng.latitude);
                bundle.putDouble("longitude", latLng.longitude);
            }
            navController.navigate(R.id.action_addressFragment_to_usernamePasswordFragment, bundle);
        });

    }
}