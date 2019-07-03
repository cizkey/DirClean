package com.lts.dirclean

import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.lts.dirclean.constants.Constant
import kotlinx.android.synthetic.main.activity_about.*
import java.io.File
import java.io.FileOutputStream

class AboutActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)

        stepToolbar()
        initListener()

    }

    private fun initListener() {
        ll_project_des.setOnClickListener(View.OnClickListener {
            val uri = Uri.parse("https://github.com/TangBeiLiu/DirClean")
            val intent = Intent(Intent.ACTION_VIEW, uri)
            startActivity(intent)
        })

        ll_reward.setOnClickListener(View.OnClickListener {
            showRewardDialog()
        })
    }

    private fun showRewardDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setView(R.layout.dialog_reward)
        builder.setPositiveButton(resources.getString(R.string.save_reward), DialogInterface.OnClickListener { dialog, which ->
            saveImage()
        })
        val create = builder.create()
        create.show()
    }

    private fun saveImage()  {

        val inputStream = assets.open("mm_reward_qrcode_1562117797135.png")
        val image = "image_${System.currentTimeMillis()}.jpg"
        val file = File(Constant.REWARD_PATH, image)
        val outputStream = FileOutputStream(file)
        val bytes = ByteArray(1024)
        var index = 0

        while ({ index = inputStream.read(bytes);index }() != -1) {
            outputStream.write(bytes, 0, index)
            outputStream.flush()
        }

        outputStream.close()
        inputStream.close()

        val intent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
        intent.setData(Uri.fromFile(file))
        sendBroadcast(intent)
        Snackbar.make(toolbar,"赞赏码以保存在${Constant.REWARD_PATH}",Snackbar.LENGTH_SHORT).show()


    }

    private fun stepToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        tv_version_name.setText(BuildConfig.VERSION_NAME)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }


        }
        return super.onOptionsItemSelected(item)
    }
}