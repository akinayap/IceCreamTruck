package anw.ict.chat.fragments;

import android.app.AlertDialog;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import anw.ict.R;
import anw.ict.chat.adapter.ChatFolderAdapter;
import anw.ict.chat.adapter.ChatStickersAdapter;
import anw.ict.chat.callback.FolderMoveCallback;
import anw.ict.chat.callback.ItemMoveCallback;
import anw.ict.chat.objects.ChatFolder;
import anw.ict.chat.objects.ChatSticker;

import static anw.ict.utils.Constants.STICKERS;
import static anw.ict.utils.Constants.STICKERS_FOLDER;

public class FolderFrag extends Fragment {
    public static String selectedFolder = "";

    public static ChatStickersAdapter stickerAdapter;
    public static List<ChatSticker> stickerList = new ArrayList<>();

    private List<ChatFolder> folderList = new ArrayList<>();
    private RecyclerView folderRV;
    private ChatFolderAdapter folderAdapter;
    private ValueEventListener stickerListener, folderListener;
    private FirebaseDatabase db;
    private String userId;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        userId = sharedPreferences.getString("userId", "null");
        db = FirebaseDatabase.getInstance();

        folderListener = new ValueEventListener(){
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                folderAdapter.notifyItemRangeRemoved(0, folderList.size());
                folderList.clear();
                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    List<ChatSticker> stickers = new ArrayList<>();
                    for(DataSnapshot sticker : ds.getChildren())
                    {
                        stickerList.removeIf(s->s.getName().equals(sticker.getKey()));
                        ChatSticker cs = new ChatSticker(ds.getKey(), getContext(), sticker.getKey());
                        stickers.add(cs);
                    }
                    folderList.add(new ChatFolder(ds.getKey(), stickers));
                }
                stickerAdapter.notifyDataSetChanged();
                folderAdapter.notifyDataSetChanged();

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        stickerListener = new ValueEventListener(){
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                stickerAdapter.notifyItemRangeRemoved(0, stickerList.size());
                stickerList.clear();
                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    ChatSticker cs = new ChatSticker("", getContext(), ds.getKey());
                    stickerList.add(cs);
                }
                stickerAdapter.notifyDataSetChanged();
                db.getReference("users/" + userId + "/" + STICKERS_FOLDER).addValueEventListener(folderListener);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        return inflater.inflate(R.layout.frag_folder, parent, false);
    }
    @Override
    public void onViewCreated(@NonNull final View view, Bundle savedInstanceState) {
        LinearLayoutManager llm = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        RecyclerView stickerRV = view.findViewById(R.id.rv_remaining_stickers);
        stickerAdapter = new ChatStickersAdapter(stickerList,v->{
            // Click on Sticker to add to folder
            Log.e("sel", selectedFolder);
            if(!selectedFolder.equals("")) {
                v.setFolder(selectedFolder);
                stickerAdapter.notifyItemRemoved(stickerList.indexOf(v));
                stickerList.remove(v);

                ChatFolder folder = folderList.stream().filter(f -> f.getFolderName().equals(v.getFolder())).findAny().orElse(null);
                if (folder != null) {
                    folder.getStickers().add(v);
                    ((ChatFolderAdapter.ChatFolderViewHolder) Objects.requireNonNull(folderRV.findViewHolderForAdapterPosition(folderList.indexOf(folder)))).getAdapter().notifyItemInserted(folder.getStickers().size() - 1);

                }
            }
        });
        stickerRV.setAdapter(stickerAdapter);
        stickerRV.setLayoutManager(llm);

        ItemTouchHelper.Callback callback = new ItemMoveCallback(stickerAdapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(stickerRV);

        GridLayoutManager glmV = new GridLayoutManager(getContext(), 2, RecyclerView.VERTICAL, false);
        folderRV = view.findViewById(R.id.rv_folders);
        folderAdapter = new ChatFolderAdapter(folderList);
        folderRV.setAdapter(folderAdapter);
        folderRV.setLayoutManager(glmV);

        ItemTouchHelper.Callback folderCallback = new FolderMoveCallback(folderAdapter);
        ItemTouchHelper folderTouchHelper = new ItemTouchHelper(folderCallback);
        folderTouchHelper.attachToRecyclerView(folderRV);

        db.getReference(STICKERS).addValueEventListener(stickerListener);

        Button okBtn = view.findViewById(R.id.okBTN);
        Button addBtn = view.findViewById(R.id.addFolderBTN);
        Button cancelBtn = view.findViewById(R.id.cancelBTN);

        okBtn.setOnClickListener(v->{
            db.getReference("users/" + userId + "/" + STICKERS_FOLDER).removeValue();
            for(int i = 0; i < folderList.size(); ++i)
            {
                String folderName = folderList.get(i).getFolderName();
                List<ChatSticker> stickers = folderList.get(i).getStickers();

                for(int j = 0; j < folderList.get(i).getStickers().size(); ++j) {
                    db.getReference("users/" + userId + "/" + STICKERS_FOLDER + "/" + folderName).child(stickers.get(j).getName()).setValue(stickers.get(j).getName());
                    db.getReference("users/" + userId + "/" + STICKERS_FOLDER + "/" + folderName).child(stickers.get(j).getName()).setPriority(j);
                }
                db.getReference("users/" + userId + "/" + STICKERS_FOLDER + "/" + folderName).setPriority(i);
            }

            selectedFolder = null;
            Fragment frag = new ChatFrag();
            FragmentTransaction transaction = Objects.requireNonNull(getFragmentManager()).beginTransaction();
            transaction.replace(R.id.frag, frag); // give your fragment container id in first parameter
            transaction.commit();
        });
        addBtn.setOnClickListener(v->{
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("Input title of new folder");

            final EditText input = new EditText(getContext());
            input.setInputType(InputType.TYPE_CLASS_TEXT);
            builder.setView(input);

            // Set up the buttons
            builder.setPositiveButton("OK", (dialog, which) -> {
                if(folderList.stream().noneMatch(f -> f.getFolderName().equals(input.getText().toString()))){
                    folderList.add(new ChatFolder(input.getText().toString(), new ArrayList<>()));
                    folderAdapter.notifyItemInserted(folderList.size() - 1);
                }
            });
            builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

            builder.show();

        });
        cancelBtn.setOnClickListener(v -> {
            selectedFolder = null;
            Objects.requireNonNull(getFragmentManager()).popBackStack();
        });
    }
    @Override
    public void onDestroyView(){
        super.onDestroyView();
        Log.e("ChatFrag", "Destroy View");
        db.getReference(STICKERS).removeEventListener(stickerListener);
        db.getReference("users/" + userId + "/" + STICKERS_FOLDER).removeEventListener(folderListener);
    }

}
