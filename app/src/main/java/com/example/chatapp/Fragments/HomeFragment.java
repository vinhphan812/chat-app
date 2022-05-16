package com.example.chatapp.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatapp.Adapters.ChatAdapter;
import com.example.chatapp.ChatActivity;
import com.example.chatapp.Models.Chat;
import com.example.chatapp.R;
import com.example.chatapp.Utils.Callback;
import com.example.chatapp.Utils.Services;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DatabaseError;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {
    RecyclerView rvChat;
    ChatAdapter chatAdapter;
    TextView txtNoContent;
    FloatingActionButton btnJoinGroup, btnCreateGroup;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
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
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rvChat = view.findViewById(R.id.rvChat);
        txtNoContent = view.findViewById(R.id.no_content);

        List<Chat> chats = new ArrayList<>();

        ChatAdapter.Listener listener = chat -> {
            Intent intent = new Intent(getActivity(), ChatActivity.class);

            intent.putExtra("chatId", chat.code);

            startActivity(intent);
        };

        loadChats();

        chatAdapter = new ChatAdapter(getContext(), chats, listener);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        rvChat.setLayoutManager(layoutManager);

        rvChat.setAdapter(chatAdapter);
        btnCreateGroup=(FloatingActionButton) view.findViewById(R.id.btn_create_group);
        btnJoinGroup=(FloatingActionButton) view.findViewById(R.id.btn_join_group);
        btnCreateGroup.setOnClickListener(v -> {
            showDialogCreateGroup();
        });
        btnJoinGroup.setOnClickListener(v -> {
            showDialogJoinGroup();
        });
    }

    private void showDialogJoinGroup() {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_join_group, null);
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(view);
        final AlertDialog dialog = builder.create();
        builder.create().show();
    }

    private void showDialogCreateGroup() {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_create_group, null);
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(view);
        final AlertDialog dialog = builder.create();
        builder.create().show();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    private void loadChats() {
        Services.getAllGroup(new Callback() {
            @Override
            public void call(Object list) {
                List<Chat> chats = (List<Chat>) list;

                txtNoContent.setVisibility(chats.size() == 0 ? View.VISIBLE : View.GONE);

                if (chats.size() > 0) {
                    chatAdapter.list = sortByLastMessage(chats);
                    chatAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onError(DatabaseError error) {
                super.onError(error);
                Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private List<Chat> sortByLastMessage(List<Chat> list) {
        for (int i = 0; i < list.size() - 1; i++)
            for (int j = i + 1; j < list.size(); j++) {
                Chat item1 = list.get(i), item2 = list.get(j);

                if (item1.last_message == null || item2.last_message == null) {
                    if (item1.last_message == null) {
                        Chat chat = item1;

                        list.set(i, item2);
                        list.set(j, chat);
                    }

                } else if (item1.last_message.send_at < item2.last_message.send_at) {
                    Chat chat = item1;

                    list.set(i, item2);
                    list.set(j, chat);
                }

            }


        return list;
    }
}