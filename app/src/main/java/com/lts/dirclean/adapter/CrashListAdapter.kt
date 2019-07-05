package com.lts.dirclean.adapter

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.lts.dirclean.BuildConfig
import com.lts.dirclean.R
import com.lts.dirclean.constants.Constant
import com.lts.dirclean.data.CrashInfo
import java.io.BufferedReader
import java.io.File
import java.io.FileInputStream
import java.io.InputStreamReader
import java.nio.file.Files
import java.util.*
import kotlin.collections.ArrayList

class CrashListAdapter(val context : Context, val data : ArrayList<CrashInfo>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val EMPTY_VIEW = 1
    private val CONTENT_VIEW = 2
    private val  inflater : LayoutInflater

    init {
        inflater = LayoutInflater.from(context)
        Collections.reverse(data)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        if (viewType == EMPTY_VIEW) {
            return EmptyHolder(inflater.inflate(R.layout.item_empty, parent, false))
        } else {
            return ViewHolder(inflater.inflate(R.layout.item_file_list, parent,false))
        }
    }

    override fun getItemCount(): Int {

        return if (data.size == 0) 1 else data.size
    }

    override fun getItemViewType(position: Int): Int {
        if (data.size == 0) {
            return EMPTY_VIEW
        } else {
            return CONTENT_VIEW
        }

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val viewType = getItemViewType(position)

        if (viewType == CONTENT_VIEW) {
            holder as ViewHolder
            val info = data.get(position)
            holder.textView.setText(info.name)

            holder.root.setOnClickListener(View.OnClickListener {
                showBottomSheetDialog(info)
            })

        }
    }

    private fun showBottomSheetDialog(info: CrashInfo) {
        val sheetDialog = BottomSheetDialog(context)
        sheetDialog.setContentView(R.layout.dialog_crash_bottom_sheet)
        sheetDialog.findViewById<TextView>(R.id.tv_open)?.setOnClickListener(View.OnClickListener {
            val uri : Uri
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                uri = FileProvider.getUriForFile(
                    context, BuildConfig.APPLICATION_ID + ".fileprovider",
                    File(info.path)
                )
            } else {
                uri = Uri.fromFile(File(info.path))
            }

            val intent = Intent(Intent.ACTION_VIEW)
            intent.setDataAndType(uri,"text/plain")
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            context.startActivity(intent)
            sheetDialog.dismiss()

        })

        sheetDialog.findViewById<TextView>(R.id.tv_send_email)?.setOnClickListener(View.OnClickListener {
            sendEmail(info)
            sheetDialog.dismiss()
        })

        sheetDialog.findViewById<TextView>(R.id.tv_delete)?.setOnClickListener(View.OnClickListener {
            deleteFile(info)
            sheetDialog.dismiss()
        })

        sheetDialog.show()
    }

    private fun deleteFile(info: CrashInfo) {
        val file = File(info.path)
        file.delete()
        data.remove(info)
        notifyDataSetChanged()
    }

    private fun sendEmail(info: CrashInfo) {
        val inputStream = FileInputStream(info.path)
        inputStream.buffered().reader().use {

            val uri = Uri.parse("mailto:${Constant.EMAIL}")
            val intent = Intent(Intent.ACTION_SENDTO, uri)
            intent.putExtra(Intent.EXTRA_SUBJECT, "Issue")
            intent.putExtra(Intent.EXTRA_TEXT, it.readText())

            if (intent.resolveActivity(context.packageManager) != null) {
                context.startActivity(intent)

            }
        }
    }
}

class ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {

    val textView : TextView
    val root : View

    init {
        textView = itemView.findViewById(R.id.tv_file_name)
        root = itemView
    }

}

class EmptyHolder(itemView : View) : RecyclerView.ViewHolder(itemView)
