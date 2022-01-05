package com.example.commutingapp.feature_note.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import com.example.commutingapp.R
import com.example.commutingapp.databinding.FragmentListBinding
import com.example.commutingapp.feature_note.presentation.place.components.PlaceViewModel


class ListFragment : Fragment(R.layout.fragment_list) {
    private lateinit var binding:FragmentListBinding
    private val viewModel:PlaceViewModel by viewModels()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.buttonNavigate.setOnClickListener {
            val action = ListFragmentDirections.actionListFragmentToCommuterFragment(isOpenFromBookmarks = true)
            Navigation.findNavController(binding.root).navigate(action)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        binding = FragmentListBinding.inflate(inflater, container, false)
        return binding.root
    }
}