package com.example.kiosk

import android.database.DataSetObserver
import android.os.Bundle
import android.os.CountDownTimer
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.get
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.example.kiosk.adapter.ItemAdapter
import com.example.kiosk.databinding.FragmentOrderBinding
import com.example.kiosk.model.Storage

class OrderFragment : Fragment() {
    lateinit var binding: FragmentOrderBinding
    lateinit var model: SharedViewModel

    var total: Float = 0.0F
    lateinit var products: LiveData<MutableList<Storage>>
    lateinit var adapter: ItemAdapter

    var timerCheckFlag = false
    var noOrderFlag = true


    val timer = object: CountDownTimer( 60000, 1000){
        override fun onFinish() {
            if(!noOrderFlag){
                model.orderChange(5)
            }
            model.resetProducts()
            println("Nav: Payment: Order to start timer")
            binding.root.findNavController().navigate(OrderFragmentDirections.actionOrderFragmentToStartFragment())
        }

        override fun onTick(p0: Long) {
            binding.clock = "${p0/1000}"

            model.updateStorage()

            timerCheckFlag = true
            adapter.notifyDataSetChanged()
            timerCheckFlag = false

            //println("Order tick $p0")
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

        adapter = ItemAdapter(this.requireContext(), products.value!!)
        binding.list.adapter = adapter

        binding.list.setOnItemClickListener { adapterView, view, i, l ->
            timer.cancel()
            timer.start()
            println("list item click ${adapterView.count}, $i, $l, $view")
        }

        binding.list.adapter.registerDataSetObserver(object: DataSetObserver() {
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
                model.orderChange(5)
            }
            model.resetProducts()
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

        timer.start()
        println("4")
    }

    fun sumProducts(){
        total = 0.0F
        for(i in 0 until products.value?.size!!){
            total += products.value!![i].price * products.value!![i].number
        }
        binding.productTotal = total
    }

}