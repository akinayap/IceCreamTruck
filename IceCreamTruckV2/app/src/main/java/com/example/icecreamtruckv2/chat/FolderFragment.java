package com.example.icecreamtruckv2.chat;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.example.icecreamtruckv2.R;
import com.example.icecreamtruckv2.utils.Constants;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import static com.example.icecreamtruckv2.chat.ChatFrag.database;

public class FolderFragment extends Fragment {
    public static List<ChatSticker> stickerList;
    public static List<List<ChatSticker>> folderList;
    public static List<String> folderNamesList;

    public static String selectedFolder = "";

    RecyclerView stickerRV, folderRV;

    public static ChatStickersAdapter stickerAdapter;
    private ChatFolderAdapter folderAdapter;
    private ValueEventListener stickerListener, folderListener;
    private DatabaseReference stickerRoot, folderRoot;

    SharedPreferences sharedPreferences;
    String userid;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        // Defines the xml file for the fragment
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        userid = sharedPreferences.getString("userid", "null");
        return inflater.inflate(R.layout.sticker_folder_frag, parent, false);
    }

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {

        stickerList = new ArrayList<>();
        folderList = new ArrayList<>();
        folderNamesList = new ArrayList<>();

        Button okBtn = view.findViewById(R.id.okBTN);
        okBtn.setOnClickListener(v->{
            database.getReference("users/" + userid + "/" + Constants.STICKERS_FOLDER_DB).removeValue();
            for(int i = 0; i < folderNamesList.size(); ++i)
            {
                for(int j = 0; j < folderList.get(i).size(); ++j) {
                    database.getReference("users/" + userid + "/" + Constants.STICKERS_FOLDER_DB + "/" + folderNamesList.get(i)).child(folderList.get(i).get(j).getName()).setValue(folderList.get(i).get(j).getName());
                    database.getReference("users/" + userid + "/" + Constants.STICKERS_FOLDER_DB + "/" + folderNamesList.get(i)).child(folderList.get(i).get(j).getName()).setPriority(j);
                }
                database.getReference("users/" + userid + "/" + Constants.STICKERS_FOLDER_DB + "/" + folderNamesList.get(i)).setPriority(i);
            }
            Fragment frag = new ChatFrag();
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.replace(R.id.frag, frag ); // give your fragment container id in first parameter
            transaction.commit();
        });

        Button addBtn = view.findViewById(R.id.addFolderBTN);
        addBtn.setOnClickListener(v->{
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("Input title of new folder");

            final EditText input = new EditText(getContext());
            input.setInputType(InputType.TYPE_CLASS_TEXT);
            builder.setView(input);

            // Set up the buttons
            builder.setPositiveButton("OK", (dialog, which) -> {
                if (!folderNamesList.contains(input.getText().toString())) {
                    folderNamesList.add(input.getText().toString());
                    folderList.add(new ArrayList<>());
                }
            });
            builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

            builder.show();

        });

        Button cancelBtn = view.findViewById(R.id.cancelBTN);
        cancelBtn.setOnClickListener(v -> getFragmentManager().popBackStack());

        initListener();
        loadStickers(view);
    }

    private void initListener() {
        stickerListener = new ValueEventListener(){
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    ChatSticker cs = new ChatSticker("", getContext(), ds.getKey());
                    cs.isInFolder();
                    stickerList.add(cs);
                }
                stickerAdapter.notifyDataSetChanged();
                loadFolders();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        folderListener = new ValueEventListener(){
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    folderList.add(new ArrayList<>());
                    folderNamesList.add(ds.getKey());
                    for(DataSnapshot sticker : ds.getChildren())
                    {
                        stickerList.removeIf(s->s.getName().equals(sticker.getKey()));
                        ChatSticker cs = new ChatSticker(ds.getKey(), getContext(), sticker.getKey());
                        folderList.get(folderList.size() - 1).add(cs);
                    }
                }
                stickerAdapter.notifyDataSetChanged();
                folderAdapter.notifyDataSetChanged();

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
    }

    private void loadStickers(View view){
        // Grid for remaining stickers
        GridLayoutManager glm = new GridLayoutManager(getContext(), 1, RecyclerView.HORIZONTAL, false);
        /** UI Components **/
        stickerRV = view.findViewById(R.id.rv_remaining_stickers);
        stickerAdapter = new ChatStickersAdapter(stickerList, v->{ // Click on Sticker to add to folder
            Log.e("sel", selectedFolder);
            if(!selectedFolder.equals("")){
                v.setFolder(selectedFolder);
                stickerAdapter.notifyItemRemoved(stickerList.indexOf(v));
                stickerList.remove(v);
                folderList.get(folderNamesList.indexOf(v.getFolder())).add(v);
                // HEREHEREHERE DONT KNOW IF ERROR
                ((ChatFolderAdapter.ChatFolderViewHolder)folderRV.findViewHolderForAdapterPosition(folderNamesList.indexOf(v.getFolder()))).getAdapter().notifyItemInserted(folderList.get(folderNamesList.indexOf(v.getFolder())).size()-1);

                //folderAdapter.notifyItemChanged();

            }
        });
        stickerRV.setAdapter(stickerAdapter);
        stickerRV.setLayoutManager(glm);

        ItemTouchHelper.Callback callback = new ItemMoveCallback(stickerAdapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(stickerRV);


        // Grid for folders
        GridLayoutManager glmV = new GridLayoutManager(getContext(), 2, RecyclerView.VERTICAL, false);
        /** UI Components **/
        folderRV = view.findViewById(R.id.rv_folders);
        folderAdapter = new ChatFolderAdapter(folderNamesList);
        folderRV.setAdapter(folderAdapter);
        folderRV.setLayoutManager(glmV);

        ItemTouchHelper.Callback folderCallback = new FolderMoveCallback(folderAdapter);
        ItemTouchHelper folderTouchHelper = new ItemTouchHelper(folderCallback);
        folderTouchHelper.attachToRecyclerView(folderRV);


        try {
            stickerRoot = database.getReference(Constants.STICKERS_DB);
            stickerRoot.addValueEventListener(stickerListener);
        } catch (Exception e) {
            Log.e("Error", "Failed to load chat");
        }
    }

    private void loadFolders(){

        try {
            folderRoot = database.getReference("users/" + userid + "/" + Constants.STICKERS_FOLDER_DB);
            folderRoot.addValueEventListener(folderListener);
        } catch (Exception e) {
            Log.e("Error", "Failed to load chat");
        }
    }
}
