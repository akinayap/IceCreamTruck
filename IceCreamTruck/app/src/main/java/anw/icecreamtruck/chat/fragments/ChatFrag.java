package anw.icecreamtruck.chat.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import anw.icecreamtruck.R;
import anw.icecreamtruck.chat.adapter.ChatLogAdapter;
import anw.icecreamtruck.chat.objects.ChatMessage;
import anw.icecreamtruck.utils.Constants;

import static anw.icecreamtruck.utils.Constants.CHAT_LOG;

public class ChatFrag extends Fragment{
    private String userRole, userId;

    private List<ChatMessage> chatList = new ArrayList<>();
    private ChildEventListener chatListener;

    private EditText chatInput;
    private RecyclerView chatRV;
    private ChatLogAdapter chatAdapter;
    FirebaseDatabase db;

    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        userRole = sharedPreferences.getString("userRole", "null");
        userId = sharedPreferences.getString("userId", "null");
        Log.e(userId, userRole);
        db = FirebaseDatabase.getInstance();
        return inflater.inflate(R.layout.frag_chat, parent, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        chatListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                ChatMessage data = dataSnapshot.getValue(ChatMessage.class);
                data.setContext(getContext());
                chatList.add(data);
                chatAdapter.notifyItemInserted(chatList.size()-1);
                chatRV.smoothScrollToPosition(chatAdapter.getItemCount() - 1);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Log.e("Message", "Edited");
                ChatMessage data = dataSnapshot.getValue(ChatMessage.class);
            }
            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                Log.e("Message", "Deleted");
                ChatMessage data = dataSnapshot.getValue(ChatMessage.class);
            }
            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {}
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        };
        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        chatRV = view.findViewById(R.id.list_of_messages);
        chatAdapter = new ChatLogAdapter(chatList);
        chatRV.setAdapter(chatAdapter);
        chatRV.setLayoutManager(llm);

        try {
            db.getReference(CHAT_LOG).limitToLast(20).addChildEventListener(chatListener);
        } catch (Exception e) {
            Log.e("Error", "Failed to load chat");
        }
    }
}