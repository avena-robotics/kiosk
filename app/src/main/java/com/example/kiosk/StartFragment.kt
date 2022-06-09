package com.example.kiosk

import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.example.kiosk.data.DataBase
import com.example.kiosk.databinding.FragmentStartBinding
import com.example.kiosk.language.LocaleUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.lang.NullPointerException

class StartFragment : Fragment() {
    lateinit var binding: FragmentStartBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_start, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val model = ViewModelProvider(requireActivity())[SharedViewModel::class.java]
        model.productsInit()


        //binding.video.rotation = 180f
        binding.video.setVideoURI(Uri.parse("android.resource://" + BuildConfig.APPLICATION_ID + "/" + R.raw.pizza))
        binding.video.setOnPreparedListener {
            it.isLooping = true
        }
        binding.video.start()

        binding.video.setOnClickListener {
            println("Nav: Payment: Start To Order Video click")
            view.findNavController().navigate(StartFragmentDirections.actionStartFragmentToOrderFragment())
        }

        binding.text.setOnClickListener {
            println("Nav: Payment: Start To Order text click")
            view.findNavController().navigate(StartFragmentDirections.actionStartFragmentToOrderFragment())
        }

        binding.polishFlagButton.setOnClickListener {
            (activity as MainActivity).updateAppLocale("pl")
        }

        binding.englishFlagButton.setOnClickListener {
            (activity as MainActivity).updateAppLocale("en")
        }
    }

}