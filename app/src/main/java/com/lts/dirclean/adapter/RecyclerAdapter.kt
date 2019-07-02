package com.lts.dirclean.adapter

import android.content.Context
import android.media.MediaPlayer
import android.opengl.Visibility
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.lts.dirclean.R
import com.lts.dirclean.data.FileItem
import com.lts.dirclean.utils.FileUtil
import com.lts.dirclean.utils.MediaUtil
import java.io.File
import android.content.Intent
import android.media.MediaRecorder
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.Handler
import android.text.TextUtils
import android.webkit.MimeTypeMap
import androidx.cardview.widget.CardView
import androidx.core.content.FileProvider
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.snackbar.Snackbar
import com.lts.dirclean.BuildConfig
import com.lts.dirclean.adapter.RecyclerAdapter.Companion.map
import com.lts.dirclean.data.FileBySizeSort
import com.lts.dirclean.data.FileByTimeSort
import com.lts.dirclean.glide.GlideApp
import com.lts.dirclean.utils.MimeUtils
import com.lts.dirclean.utils.Setting
import java.text.SimpleDateFormat
import java.util.*
import kotlin.Comparator
import kotlin.collections.ArrayList


class RecyclerAdapter(
    private val context: Context, private val data: List<FileItem>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private lateinit var setting: Setting
    private lateinit var deleteClickListenter: OnDeleteClickListenter
    private val formatDatas: ArrayList<FileItem>

    private val GROUP_VIEW = 1
    private val CONTENT_VIEW = 2

    companion object {
        val map = ArrayList<String>()


        init {
            map.add("jpg")
            map.add("png")
            map.add("jpeg")
            map.add("webp")
        }
    }

    init {
        initSort()
        formatDatas = ArrayList()
        groupData()


    }


    //将数据分组
    private fun groupData() {

        var lastModified = ""

        for (datum in data) {
            //如果是同一天，copy后添加到集合
            if (lastModified.equals(formatDate(datum.lastModified))) {
                formatDatas.add(datum.copy())
            } else {

                val copy = datum.copy()
                copy.isGroup = true
                copy.dateTitle = formatDate(datum.lastModified)
                formatDatas.add(copy)
                formatDatas.add(datum.copy())
                lastModified = formatDate(datum.lastModified)
            }

        }


    }

    private fun initSort() {
        setting = Setting.getInstances(context)

        if (setting.getBySort() == 1) {
            val fileByTimeSort = FileByTimeSort()
            Collections.sort(data, fileByTimeSort)

        } else {

            val fileBySize = FileBySizeSort()
            Collections.sort(data, fileBySize)
        }

    }

    fun setOnDeleteClickListener(deleteClickListenter: OnDeleteClickListenter) {
        this.deleteClickListenter = deleteClickListenter
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        if (viewType == CONTENT_VIEW) {

            val view = LayoutInflater.from(context).inflate(
                com.lts.dirclean
                    .R.layout.item_file, parent, false
            )

            return RecyclerHolder(view)

        } else {
            val inflater = LayoutInflater.from(context).inflate(R.layout.item_group, parent, false)

            return GroupViewHolder(inflater)
        }


    }

    override fun getItemCount(): Int = formatDatas.size


    override fun getItemViewType(position: Int): Int {

        return if (formatDatas.get(position).isGroup) GROUP_VIEW else CONTENT_VIEW
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        val viewType = getItemViewType(position)

        if (viewType == GROUP_VIEW) {

            holder as GroupViewHolder
            val item = formatDatas.get(position)
            holder.textView.setText(item.dateTitle)

        } else {

            holder as RecyclerHolder

            val metrics = context.resources.displayMetrics
            val scale = metrics.density
            val cardWidth = Math.round((metrics.widthPixels - (24 * scale + 0.5f)) / 3.0f)
            val params = RecyclerView.LayoutParams(cardWidth, ViewGroup.LayoutParams.WRAP_CONTENT)
            val margin = Math.round(4 * scale)
            params.setMargins(margin, margin, margin, margin)
            holder.cardView.layoutParams = params


            val fileItem = formatDatas.get(position)
            if (map.contains(fileItem.fileType)) {
                GlideApp.with(context)
                    .load(File(fileItem.filePath))
                    .centerCrop()
                    .into(holder.imageView)

            } else if (fileItem.fileType.contains("gif")) {
                GlideApp.with(context)
                    .asGif()
                    .load(File(fileItem.filePath))
                    .into(holder.imageView)
            } else if (fileItem.fileType.equals("amr") || fileItem.fileType.equals("mp3")) {
                GlideApp.with(context)
                    .load(R.mipmap.audio)
                    .into(holder.imageView)

            } else if (fileItem.fileType.equals("pdf")) {
                GlideApp.with(context)
                    .load(com.lts.dirclean.R.mipmap.pdf)
                    .into(holder.imageView)

            } else if (fileItem.fileType.equals("zip")) {
                GlideApp.with(context)
                    .load(com.lts.dirclean.R.mipmap.zip)
                    .into(holder.imageView)
            } else if (fileItem.fileType.equals("xls") || fileItem.fileType.equals("xlsx")) {
                GlideApp.with(context)
                    .load(com.lts.dirclean.R.mipmap.xls)
                    .into(holder.imageView)
            } else if (fileItem.fileType.equals("docx")) {
                GlideApp.with(context)
                    .load(com.lts.dirclean.R.mipmap.text)
                    .into(holder.imageView)
            } else if (fileItem.fileType.equals("mp4")) {
                GlideApp.with(context)
                    .load(fileItem.videoCover)
                    .into(holder.imageView)

                holder.tvDuration.setText(FileUtil.getTimeFromInt(fileItem.duration))

            } else {
                GlideApp.with(context)
                    .load(File(fileItem.filePath))
                    .placeholder(R.drawable.file)
                    .dontAnimate()
                    .into(holder.imageView)
            }


            holder.ivPlayer.visibility = if (fileItem.fileType.equals("mp4")) View.VISIBLE else View.GONE
            holder.tvDuration.visibility = if (fileItem.fileType.equals("mp4")) View.VISIBLE else View.GONE
            val builder = StringBuilder()
            builder.append(fileItem.fileSize)
            builder.append("K")
            holder.tvSize.setText(builder.toString())
            holder.tvName.setText(fileItem.fileName)
            holder.tvDate.setText(formatDate(Date(fileItem.lastModified)))

            holder.imageView.setOnClickListener(View.OnClickListener {

                val mimeType = MimeUtils.guessMimeTypeFromExtension(fileItem.fileType)

                if (TextUtils.isEmpty(mimeType)) {
                    Snackbar.make(holder.imageView, "Unknown file", Snackbar.LENGTH_SHORT).show()
                } else {
                    val intent = Intent(Intent.ACTION_VIEW)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        val uri = FileProvider.getUriForFile(
                            context, BuildConfig.APPLICATION_ID + ".fileprovider",
                            File(fileItem.filePath)
                        )
                        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                        intent.setDataAndType(uri, mimeType)
                        context.startActivity(intent)

                    } else {

                        intent.setDataAndType(Uri.fromFile(File(fileItem.filePath)), mimeType)
                        context.startActivity(intent)

                    }
                }
            })


            holder.imageView.setOnLongClickListener {
                val sheetDialog = BottomSheetDialog(context)
                sheetDialog.setContentView(R.layout.dialog_sheet)
                val tvShare = sheetDialog.findViewById<TextView>(R.id.tv_share)
                val tvDelete = sheetDialog.findViewById<TextView>(R.id.tv_delete)

                tvShare?.setOnClickListener(View.OnClickListener {
                    sheetDialog.dismiss()
                    val intent = Intent()

                    val uri: Uri
                    var mimeType = MimeUtils.guessMimeTypeFromExtension(fileItem.fileType)


                    if (TextUtils.isEmpty(mimeType)) {
                        mimeType = "*/*"
                    }


                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        uri = FileProvider.getUriForFile(
                            context, BuildConfig.APPLICATION_ID + ".fileprovider",
                            File(fileItem.filePath)
                        )
                    } else {
                        uri = Uri.fromFile(File(fileItem.filePath))
                    }

                    intent.setAction(Intent.ACTION_SEND)
                    intent.putExtra(Intent.EXTRA_STREAM, uri)
                    intent.setType(mimeType)
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    intent.addCategory("android.intent.category.DEFAULT")
                    context.startActivity(Intent.createChooser(intent, fileItem.fileName))

                })


                tvDelete?.setOnClickListener(View.OnClickListener {
                    sheetDialog.dismiss()
                    deleteClickListenter.onDeleteFile(fileItem)
                    formatDatas.remove(fileItem)
                    notifyItemRemoved(position)

                })

                sheetDialog.show()
                true
            }

        }

    }

    fun notifySort() {
        initSort()
        notifyDataSetChanged()
    }

    private fun formatDate(date: Date): String {
        val format = SimpleDateFormat("HH:mm", Locale.CHINA)

        return format.format(date)
    }

    private fun formatDate(date: Long): String {
        val format = SimpleDateFormat("MMM dd", Locale.US)

        return format.format(Date(date))
    }


}

class RecyclerHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val imageView: ImageView
    val ivPlayer: ImageView
    val tvDuration: TextView
    val tvSize: TextView
    val tvName: TextView
    val tvDate: TextView
    val cardView: CardView

    init {
        imageView = itemView.findViewById(com.lts.dirclean.R.id.image)
        ivPlayer = itemView.findViewById(com.lts.dirclean.R.id.iv_player)
        tvDuration = itemView.findViewById(com.lts.dirclean.R.id.tv_duration)
        tvSize = itemView.findViewById(com.lts.dirclean.R.id.tv_size)
        tvName = itemView.findViewById(com.lts.dirclean.R.id.tv_name)
        tvDate = itemView.findViewById(R.id.tv_date)
        cardView = itemView.findViewById(R.id.cardView)
    }
}

class GroupViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val textView: TextView

    init {
        textView = itemView.findViewById(R.id.textview)
    }
}

interface OnDeleteClickListenter {
    fun onDeleteFile(fileItem: FileItem)
}