package com.licious.sample.scannersample.ui.menu

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.licious.sample.design.ui.base.BaseFragment
import com.licious.sample.scannersample.R
import com.licious.sample.scannersample.databinding.FragmentMenuBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MenuFragment : BaseFragment<FragmentMenuBinding>() {

    override fun getLogTag(): String = "MenuFragment"

    override fun getViewBinding(): FragmentMenuBinding =
        FragmentMenuBinding.inflate(layoutInflater)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    private fun initView() {
        binding.btnScanQr.setOnClickListener {
            findNavController().navigate(R.id.action_menuFragment_to_scannerFragment)
        }
        // TODO: Add other buttons
    }
}