package com.lts.dirclean.ui

import android.Manifest
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.lts.dirclean.utils.InjectorUtils
import com.lts.dirclean.viewmodels.MainViewmodel
import kotlinx.android.synthetic.main.activity_main.*
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.work.WorkInfo
import com.google.android.material.snackbar.Snackbar
import com.lts.dirclean.R
import com.lts.dirclean.adapter.FragmentAdapter
import com.lts.dirclean.constants.Constant
import com.lts.dirclean.constants.MessageEvent
import com.lts.dirclean.crash.CrashHandler
import com.lts.dirclean.data.DirItem
import com.lts.dirclean.utils.Setting
import org.greenrobot.eventbus.EventBus
import java.io.IOException
import kotlin.collections.ArrayList


class MainActivity : AppCompatActivity(), View.OnClickListener {


    private lateinit var settings: Setting
    private var isShowProgress: Boolean = true


    private val viewmodel: MainViewmodel by viewModels {
        InjectorUtils.provideMianViewModelFactory(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(toolbar)

        settings = Setting.getInstances(this)

        CrashHandler.instances.init(this)
        isShowProgress = settings.getFirstLoading()

        fab.hide()
        requestPermission()
        fab.setOnClickListener(this)

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {

        if (item?.itemId == R.id.about) {
            startActivity(Intent(this, AboutActivity::class.java))
            return true
        }
        return super.onOptionsItemSelected(item)
    }


    private fun requestPermission() {
        val permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)

        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE),
                Constant.RECORD_REQUEST_CODE
            )
        } else {
            subscribeUi()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            Constant.RECORD_REQUEST_CODE -> {

                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    finish()
                } else {
                    subscribeUi()
                }
            }
        }
    }


    private fun subscribeUi() {

        viewmodel.dirs.observe(this, Observer {
            createFileListFragment(it)
            subscribleFiles()
        })


    }

    private fun subscribleFiles() {
        viewmodel.queryFileItems(this).observe(this, Observer {
            if (it.state == WorkInfo.State.SUCCEEDED) {
                val size = it.outputData.getLong(Constant.TOTAL_SIZE, 0)
                val sizeM = size / 1024 / 1024
                val count = it.outputData.getInt(Constant.TOTAL_COUNT, 0)

                EventBus.getDefault().post(MessageEvent(Constant.UPDATE_UI))
                fab.show()

                if (isShowProgress) {
                    val text = "共扫描到$count 个文件，占用空间$sizeM M"
                    progress.visibility = View.GONE
                    showResultDialog(text)
                }

                settings.setFirstLoading(false)

            } else if (it.state == WorkInfo.State.RUNNING) {
                fab.hide()
                progress.visibility = if (isShowProgress) View.VISIBLE else View.GONE

            }
        })

    }

    private fun showResultDialog(text: String) {
        val create = AlertDialog.Builder(this)
            .setTitle(resources.getString(R.string.prompt))
            .setMessage(text)
            .setPositiveButton(resources.getString(R.string.cancel), { dialog, which ->
                dialog.dismiss()
            })
            .create()

        create.setCanceledOnTouchOutside(true)
        create.show()


    }


    private fun createFileListFragment(it: List<DirItem>?) {
        val adapter = FragmentAdapter(supportFragmentManager, getFragments(it), getPageTitle(it))
        viewpager.adapter = adapter
        tabLayout.setupWithViewPager(viewpager)
    }

    private fun getFragments(it: List<DirItem>?): ArrayList<Fragment> {
        val list = ArrayList<Fragment>()
        if (it != null) {
            for (dirItem in it.iterator()) {
                val instance = FileListFragment.getInstance(dirItem.aliasName, dirItem.name)

                list.add(instance)
            }
        }


        return list
    }

    private fun getPageTitle(it: List<DirItem>?): ArrayList<String> {

        val list = ArrayList<String>()

        if (it != null) {
            for (dirItem in it.iterator()) {
                list.add(dirItem.aliasName)
            }
        }

        return list
    }

    override fun onClick(v: View?) {
        settings.setIsCleaning(true)
        showDeleteDialog()

    }

    private fun showDeleteDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(resources.getString(R.string.warning))
        builder.setMessage(resources.getString(R.string.delete_message))
        builder.setPositiveButton(resources.getString(R.string.clean),
            { dialog, which -> deleteFiles() })
        builder.setNegativeButton(resources.getString(R.string.cancel),
            { dialog, which -> dialog.dismiss() })

        val dialog = builder.create()
        dialog.show()

    }

    private fun deleteFiles() {
        viewmodel.deleteFile(this).observe(this, Observer {

            if (it.state == WorkInfo.State.SUCCEEDED) {

                progress.visibility = View.GONE
                val sum = it.outputData.getLong(Constant.TOTAL_COUNT, 0)


                Snackbar.make(toolbar, "清理了$sum 个文件", Snackbar.LENGTH_LONG).show()
                EventBus.getDefault().post(MessageEvent(Constant.UPDATE_UI))

            } else if (it.state == WorkInfo.State.RUNNING) {
                tv_clean_title.setText(resources.getString(R.string.cleaning))
                progress.visibility = View.VISIBLE
            }

            settings.setIsCleaning(false)

        })
    }


}
