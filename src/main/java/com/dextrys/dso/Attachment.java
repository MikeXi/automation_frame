package com.dextrys.dso;

import com.dextrys.config.Const;
import com.dextrys.utils.Rand;
import com.dextrys.utils.Utils;


public class Attachment {
    private String name;
    private String type;
    private String description;
    private String filePath;

    public Attachment(String name, String type, String desc, String file) {
        this.name = name;
        this.type = type;
        this.description = desc;
        this.filePath = file;
    }

    @Override
    public String toString(){
        return name + "(" + type + ")";
    }

    public static Attachment defaultAttachment() {
        Attachment obj = new Attachment("attach" + Rand.uid(), "Photographs", "description" + Rand.uid(), Utils.getResourcePath(Const.ATTACHMENT));
        return obj;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getDescription() {
        return description;
    }

    public CharSequence getFilePath() {
        return filePath;
    }
}
