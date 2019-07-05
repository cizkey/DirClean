package com.lts.dirclean.ui


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.lts.dirclean.R
import com.lts.dirclean.adapter.OnDeleteClickListenter
import com.lts.dirclean.adapter.RecyclerAdapter
import com.lts.dirclean.constants.Constant
import com.lts.dirclean.constants.MessageEvent
import com.lts.dirclean.data.FileItem
import com.lts.dirclean.utils.InjectorUtils
import com.lts.dirclean.viewmodels.FileListViewModel
import kotlinx.android.synthetic.main.fragment_file_list.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.io.File


class FileListFragment : Fragment(), OnDeleteClickListenter {


    private var parentName: String = ""
    private var fileName: String = ""
    private var startDate : Long = 0
    private var endDate = System.currentTimeMillis()
    private lateinit var adapter: RecyclerAdapter

    companion object {
        fun getInstance(parentName: String, fileName: String): FileListFragment {
            val fragment = FileListFragment()
            val bundle = Bundle()
            bundle.putString(Constant.PARENT_NAME, parentName)
            bundle.putString(Constant.DIR_DATABASE_NAME, fileName)
            fragment.arguments = bundle

            return fragment
        }
    }

    private val viewModel: FileListViewModel by viewModels {
        InjectorUtils.provideFileListViewModelFactory(requireContext())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        parentName = arguments?.getString(Constant.PARENT_NAME) ?: ""
        fileName = arguments?.getString(Constant.DIR_DATABASE_NAME) ?: ""

    }

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(messageEvent: MessageEvent) {
        when(messageEvent.notification) {
             Constant.SORT -> {
                 if (adapter != null) {
                     adapter.notifySort()
                 }
            }

            Constant.UPDATE_UI -> {
                subscribleFiles()
            }
        }


    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_file_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        subscribleFiles()
    }

    private fun subscribleFiles() {
        val liveData = viewModel.getFiles(requireContext(), parentName, startDate, endDate)
        if (liveData == null) return

        liveData.observe(viewLifecycleOwner, Observer {

            adapter = RecyclerAdapter(requireContext(), it)
            val manager = GridLayoutManager(requireContext(), 3)

            manager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                override fun getSpanSize(position: Int): Int {
                    val viewType = adapter.getItemViewType(position)


                    return if (viewType == 2 ) 1 else 3
                }
            }

            adapter.setOnDeleteClickListener(this)

            recyclerView.layoutManager = manager
            recyclerView.adapter = adapter
        })
    }

    override fun onDeleteFile(fileItem: FileItem) {
        viewModel.deleteFile(fileItem)
        val delete = File(fileItem.filePath).delete()
        if (delete) {
            Snackbar.make(recyclerView,resources.getString(R.string.delete_success),Snackbar.LENGTH_SHORT).show()
        }

    }


}