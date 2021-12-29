package com.example.commutingapp.views.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.commutingapp.R
import com.example.commutingapp.databinding.CommuterDataFragmentBinding
import com.example.commutingapp.viewmodels.MainViewModel
import com.example.commutingapp.views.adapters.CommuterDataAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CommuterDataFragment : Fragment(R.layout.commuter_data_fragment) {
    private var binding:CommuterDataFragmentBinding? = null
    private val mainViewModel: MainViewModel by viewModels()
    private lateinit var commuterAdapter: CommuterDataAdapter
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        mainViewModel.runSortedByDate.observe(viewLifecycleOwner){
            commuterAdapter.submitList(it)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = CommuterDataFragmentBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    private fun setupRecyclerView()=binding!!.recyclerViewDisplay.apply {
        commuterAdapter = CommuterDataAdapter(requireActivity())
        layoutManager = LinearLayoutManager(requireContext())
        adapter = commuterAdapter
    }
}