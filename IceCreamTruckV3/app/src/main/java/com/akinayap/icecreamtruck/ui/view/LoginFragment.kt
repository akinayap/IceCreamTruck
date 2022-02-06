package com.akinayap.icecreamtruck.ui.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.akinayap.icecreamtruck.databinding.FragmentLoginBinding

class LoginFragment: Fragment() {
    lateinit var viewBinding: FragmentLoginBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewBinding = FragmentLoginBinding.inflate(layoutInflater, container, false)
        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initObservers()
        initListeners()
    }

    fun initObservers(){}
    fun initListeners(){}
}