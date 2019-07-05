package com.lts.dirclean.ui

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.lts.dirclean.R
import com.lts.dirclean.adapter.CrashListAdapter
import com.lts.dirclean.data.CrashInfo
import kotlinx.android.synthetic.main.activity_about.*
import kotlinx.android.synthetic.main.activity_about.toolbar
import kotlinx.android.synthetic.main.activity_crash_list.*

class CrashListActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_crash_list)

        setpToolbar()

        getCrashList()
    }

    private fun getCrashList() {
        val fileLsit = ArrayList<CrashInfo>()
        val filesDir = getExternalFilesDir("log")

        if (filesDir != null) {
            for (listFile in filesDir.listFiles()) {
                val crashInfo = CrashInfo(
                    name = listFile.name,
                    path = listFile.absolutePath
                )

                fileLsit.add(crashInfo)
            }
        }

        val adapter = CrashListAdapter(this, fileLsit)
        recyclerView.adapter = adapter

    }

    private fun setpToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}