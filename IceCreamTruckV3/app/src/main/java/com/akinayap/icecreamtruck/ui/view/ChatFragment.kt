package com.akinayap.icecreamtruck.ui.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintSet
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.window.layout.FoldingFeature
import androidx.window.layout.WindowInfoTracker
import androidx.window.layout.WindowLayoutInfo
import com.akinayap.icecreamtruck.R
import com.akinayap.icecreamtruck.data.Utils
import com.akinayap.icecreamtruck.databinding.FragmentChatBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class ChatFragment : Fragment() {
    private lateinit var viewBinding: FragmentChatBinding
    private lateinit var windowInfoTracker: WindowInfoTracker

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        viewBinding = FragmentChatBinding.inflate(layoutInflater, container, false)
        windowInfoTracker = WindowInfoTracker.getOrCreate(requireContext())
        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initialize()
    }

    private fun initialize() {
        initListeners()
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.Main) {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                windowInfoTracker.windowLayoutInfo(requireActivity()).collect(action = { value -> updateUI(value) })

            }
        }
    }

    private fun initListeners(){

    }

    private fun updateUI(newLayoutInfo: WindowLayoutInfo) {
        if (newLayoutInfo.displayFeatures.isNotEmpty()) { // Checks if is on opened screen
            alignViewToFoldingFeatureBounds(newLayoutInfo)
        } else {
            // Hide Stickers Layout
            viewBinding.layoutStickers.visibility = View.GONE
        }
    }
    private fun alignViewToFoldingFeatureBounds(newLayoutInfo: WindowLayoutInfo) {
        val constraintLayout = viewBinding.viewLayout
        val set = ConstraintSet()
        set.clone(constraintLayout)

        // Get and translate the feature bounds to the View's coordinate space and current
        // position in the window.
        val foldingFeature = newLayoutInfo.displayFeatures[0] as FoldingFeature
        val bounds = Utils.getFeatureBoundsInWindow(foldingFeature, viewBinding.root)

        bounds?.let { rect ->
            // Some devices have a 0px width folding feature. We set a minimum of 1px so we
            // can show the view that mirrors the folding feature in the UI and use it as reference.
            val horizontalFoldingFeatureHeight = (rect.bottom - rect.top).coerceAtLeast(1)
            val verticalFoldingFeatureWidth = (rect.right - rect.left).coerceAtLeast(1)

            set.constrainHeight(R.id.layout_stickers, 0)
            set.constrainHeight(R.id.layout_chat, 0)

            // Sets the view to match the height and width of the folding feature
            set.constrainHeight(R.id.folding_feature, horizontalFoldingFeatureHeight)
            set.constrainWidth(R.id.folding_feature, verticalFoldingFeatureWidth)

            set.connect(R.id.folding_feature, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, 0)
            set.connect(R.id.folding_feature, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, 0)

            if (foldingFeature.orientation == FoldingFeature.Orientation.VERTICAL) {
                set.setMargin(R.id.folding_feature, ConstraintSet.START, rect.left)
                set.connect(R.id.layout_input, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM)
                set.connect(R.id.layout_chat, ConstraintSet.END, R.id.folding_feature, ConstraintSet.START, 0)
                set.connect(R.id.layout_chat, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, 0)
                set.connect(R.id.layout_chat, ConstraintSet.BOTTOM, R.id.layout_input, ConstraintSet.TOP, 0)
                set.connect(R.id.layout_stickers, ConstraintSet.START, R.id.folding_feature, ConstraintSet.END, 0)
                set.connect(R.id.layout_stickers, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, 0)
                set.connect(R.id.layout_stickers, ConstraintSet.BOTTOM, R.id.layout_input, ConstraintSet.TOP, 0)
            } else {
                // FoldingFeature is Horizontal
                set.setMargin(R.id.folding_feature, ConstraintSet.TOP, rect.top)
                set.connect(R.id.layout_chat, ConstraintSet.BOTTOM, R.id.folding_feature, ConstraintSet.TOP, 0)
                set.connect(R.id.layout_stickers, ConstraintSet.TOP, R.id.folding_feature, ConstraintSet.BOTTOM, 0)
            }

            // Set the view to visible and apply constraints
            set.setVisibility(R.id.layout_stickers, View.VISIBLE)
            set.setVisibility(R.id.folding_feature, View.VISIBLE)
            set.applyTo(constraintLayout)
        }
    }
}