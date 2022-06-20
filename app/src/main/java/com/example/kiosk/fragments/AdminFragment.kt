package com.example.kiosk.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.example.kiosk.R
import com.example.kiosk.databinding.FragmentAdminBinding
import java.lang.NullPointerException

class AdminFragment : Fragment() {
    lateinit var binding: FragmentAdminBinding

    val password = "1234"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_admin, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.returnButton.setOnClickListener {
            view.findNavController().navigate(AdminFragmentDirections.actionAdminFragmentToStartFragment())
        }

        binding.confirmButton.setOnClickListener {
            if(binding.editTextTextPassword.text.toString() == password){
                binding.exitButton.visibility = View.VISIBLE
            }
        }

        binding.exitButton.setOnClickListener {
            android.os.Process.killProcess(android.os.Process.myPid())
        }

        binding.button6.setOnClickListener {
            throw NullPointerException()
        }


    }

}