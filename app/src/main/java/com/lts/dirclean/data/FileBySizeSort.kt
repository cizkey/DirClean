package com.lts.dirclean.data

class FileBySizeSort : Comparator<FileItem> {


    override fun compare(o1: FileItem?, o2: FileItem?): Int {

        if (o2!!.fileSize > o1!!.fileSize) {
            return 1
        }

        return if (o2.fileSize < o1.fileSize) {
            -1
        } else 0

    }
}