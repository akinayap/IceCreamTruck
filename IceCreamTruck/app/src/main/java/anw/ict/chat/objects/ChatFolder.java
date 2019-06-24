package anw.ict.chat.objects;

import java.util.List;

public class ChatFolder {
    private List<ChatSticker> stickers;
    private String folderName;


    public ChatFolder(String name, List<ChatSticker> list) {
        folderName = name;
        stickers = list;
    }

    public String getFolderName(){
        return folderName;
    }

    public List<ChatSticker> getStickers(){
        return stickers;
    }
}
