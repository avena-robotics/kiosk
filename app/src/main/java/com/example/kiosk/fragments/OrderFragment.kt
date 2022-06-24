package com.example.kiosk.fragments

import android.os.Bundle
import android.os.CountDownTimer
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.kiosk.R
import com.example.kiosk.viewmodels.SharedViewModel
import com.example.kiosk.adapter.ProductAdapter
import com.example.kiosk.databinding.FragmentOrderBinding
import com.example.kiosk.model.Storage

class OrderFragment : Fragment() {
    lateinit var binding: FragmentOrderBinding
    lateinit var model: SharedViewModel

    var total: Float = 0.0F
    lateinit var products: LiveData<MutableList<Storage>>
    lateinit var adapter: ProductAdapter

    var timerCheckFlag = false
    var noOrderFlag = true


    val timer = object: CountDownTimer( 60000, 1000){
        override fun onFinish() {
            if(!noOrderFlag){
                model.cancelOrder()
            }else{
                model.resetProducts()
            }

            println("Nav: Payment: Order to start timer")
            binding.root.findNavController().navigate(OrderFragmentDirections.actionOrderFragmentToStartFragment())
        }

        override fun onTick(p0: Long) {
            binding.clock = "${p0/1000}"

            model.updateStorage()

            if(model.changeFlag.value!!){
                timerCheckFlag = true
                adapter.notifyDataSetChanged()
                timerCheckFlag = false
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_order, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        model = ViewModelProvider(requireActivity())[SharedViewModel::class.java]
        products = model.products

        noOrderFlag = model.currentId.value == 0

        adapter = ProductAdapter(this.requireContext(), products.value!!)

        adapter.setHasStableIds(true)
        binding.list.adapter = adapter
        binding.list.layoutManager = object: LinearLayoutManager(context){
            override fun canScrollVertically(): Boolean {
                return false
            }

        }

        adapter.registerAdapterDataObserver(object: RecyclerView.AdapterDataObserver() {
            override fun onChanged() {
                super.onChanged()
                sumProducts()
                if(!timerCheckFlag){

                    if(noOrderFlag){
                        model.firstOrder()
                        noOrderFlag = false
                    }else{
                        model.orderChange(0)
                    }

                    timer.cancel()
                    timer.start()
                }
            }
        })

        binding.cancelButton.setOnClickListener {
            if(!noOrderFlag){
                model.cancelOrder()
            }else{
                model.resetProducts()
            }
            timer.cancel()
            println("Nav: Payment: Order To Start cancel button")
            binding.root.findNavController().navigate(OrderFragmentDirections.actionOrderFragmentToStartFragment())
        }

        binding.payButton.setOnClickListener {
            if(!noOrderFlag && binding.productTotal > 0){
                timer.cancel()
                model.total.value = binding.productTotal
                println("Nav: Payment: Order to Payment pay button")
                binding.root.findNavController().navigate(OrderFragmentDirections.actionOrderFragmentToPaymentFragment())
            }
        }

        binding.imageView.setOnClickListener {
            timer.cancel()
            view.findNavController().navigate(OrderFragmentDirections.actionOrderFragmentToAdminFragment())
        }

        timer.start()
    }

    fun sumProducts(){
        total = 0.0F
        for(i in 0 until products.value?.size!!){
            total += products.value!![i].price * products.value!![i].number
        }
        binding.productTotal = total
    }

}