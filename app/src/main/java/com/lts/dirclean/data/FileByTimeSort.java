package com.lts.dirclean.data;

import java.util.Comparator;

public class FileByTimeSort implements Comparator<FileItem> {


    @Override
    public int compare(FileItem o1, FileItem o2) {

        if (o2.getLastModified() > o1.getLastModified()) {
            return 1;
        }

        if (o2.getLastModified() < o1.getLastModified()) {
            return -1;
        }

        return 0;
    }
}
