package com.example.kioskv2.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.example.kioskv2.MainActivity
import com.example.kioskv2.R
import com.example.kioskv2.databinding.FragmentStartBinding
import com.example.kioskv2.language.LangStorage
import com.example.kioskv2.viewmodels.SharedViewModel

class StartFragment : Fragment(), View.OnClickListener {
    lateinit var binding: FragmentStartBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_start, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val model = ViewModelProvider(requireActivity())[SharedViewModel::class.java]

        binding.plFlagButton.setOnClickListener {
            if(LangStorage(requireContext()).getPreferredLocale() != "pl"){
                (activity as MainActivity).updateAppLocale("pl")
            }
        }
        binding.enFlagButton.setOnClickListener {
            if(LangStorage(requireContext()).getPreferredLocale() != "en"){
                (activity as MainActivity).updateAppLocale("en")
            }
        }

        binding.background.setOnClickListener(this)
        binding.character.setOnClickListener(this)
        binding.icon.setOnClickListener(this)
        binding.infoButton.setOnClickListener(this)
        binding.logo.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        if(v!=null){
            if(v == binding.background ||
                    v == binding.character ||
                    v == binding.icon ||
                    v == binding.infoButton ||
                    v == binding.logo){
                binding.root.findNavController().navigate(StartFragmentDirections.actionStartFragmentToOrderFragment())
            }
        }
    }
}