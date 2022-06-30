package com.example.kioskv2.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.example.kioskv2.R
import com.example.kioskv2.databinding.FragmentPaymentBinding

class PaymentFragment : Fragment() {
    lateinit var binding: FragmentPaymentBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_payment, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.cancelOrderButton.setOnClickListener {
            binding.root.findNavController().navigate(PaymentFragmentDirections.actionPaymentFragmentToStartFragment())
        }

        binding.editOrderButton.setOnClickListener {
            binding.root.findNavController().navigate(PaymentFragmentDirections.actionPaymentFragmentToOrderFragment())
        }
    }
}