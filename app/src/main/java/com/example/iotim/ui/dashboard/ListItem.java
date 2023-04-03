package com.example.iotim.ui.dashboard;

import android.graphics.Bitmap;
import android.net.Uri;

public class ListItem implements Comparable<ListItem>{
    String num;
    String name;
    Bitmap resId;

    public ListItem(String num, String name, Bitmap resId) {
        this.num = num;
        this.name = name;
        this.resId = resId;
    }

    public String getNum() {
        return num;
    }
    public void setNum(String num) {
        this.num = num;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public Bitmap getResId() {
        return resId;
    }
    public void setResId(Bitmap resId) {
        this.resId = resId;
    }

    @Override
    public int compareTo(ListItem listItem) {
        return -1 * this.name.compareTo(listItem.name);
    }
}
