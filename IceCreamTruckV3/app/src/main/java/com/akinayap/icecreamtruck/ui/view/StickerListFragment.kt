package com.akinayap.icecreamtruck.ui.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.window.layout.WindowInfoTracker
import androidx.window.layout.WindowLayoutInfo
import androidx.window.layout.WindowMetricsCalculator
import com.akinayap.icecreamtruck.R
import com.akinayap.icecreamtruck.data.BundleArg
import com.akinayap.icecreamtruck.data.autoFitColumns
import com.akinayap.icecreamtruck.data.pxToDp
import com.akinayap.icecreamtruck.databinding.FragmentStickerListBinding
import com.akinayap.icecreamtruck.ui.adapter.StickerListAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class StickerListFragment : Fragment() {
    private lateinit var viewBinding: FragmentStickerListBinding
    private lateinit var windowInfoTracker: WindowInfoTracker

    private var stickerList = ArrayList<Long>()
    private lateinit var stickerListAdapter: StickerListAdapter

    companion object {
        fun newInstance(stickerId: String): Fragment {
            val fragment = StickerListFragment()
            val argument = Bundle()
            argument.putString(BundleArg.STICKER_GROUP_ID, stickerId)
            fragment.arguments = argument
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewBinding = FragmentStickerListBinding.inflate(layoutInflater, container, false)
        windowInfoTracker = WindowInfoTracker.getOrCreate(requireContext())
        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initialize()
    }

    private fun initialize() {
        arguments?.let { bundle ->
            //var stickerID = bundle?.getString(BundleArg.STICKER_GROUP_ID)
            stickerList.add(123213213)
            stickerList.add(123213213)
            stickerList.add(123213213)
            stickerList.add(123213213)
            stickerList.add(123213213)
            stickerList.add(123213213)
            stickerList.add(123213213)
            stickerList.add(123213213)
            stickerList.add(123213213)
            stickerList.add(123213213)
            stickerList.add(123213213)
            stickerList.add(123213213)
            stickerList.add(123213213)
            stickerList.add(123213213)
            stickerList.add(123213213)
            stickerList.add(123213213)
            stickerList.add(123213213)
            stickerList.add(123213213)
            stickerList.add(123213213)
            stickerList.add(123213213)
            stickerList.add(123213213)
            stickerList.add(123213213)
            stickerList.add(123213213)
            stickerList.add(123213213)
            stickerList.add(123213213)
        }

        stickerListAdapter = StickerListAdapter(requireContext())
        stickerListAdapter.setItemList(stickerList)
        viewBinding.rvStickers.adapter = stickerListAdapter

        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.Main) {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                windowInfoTracker.windowLayoutInfo(requireActivity())
                    .collect(action = { value -> updateUI(value) })
            }
        }
    }

    private fun updateUI(newLayoutInfo: WindowLayoutInfo) {
        if (newLayoutInfo.displayFeatures.isNotEmpty()) { // Checks if is on opened screen
            val wmc = WindowMetricsCalculator.getOrCreate()
            val currentWM = wmc.computeCurrentWindowMetrics(requireActivity()).bounds
            val screenWidth = currentWM.width() / 2
            viewBinding.rvStickers.autoFitColumns(screenWidth, 70)
        }
    }


}