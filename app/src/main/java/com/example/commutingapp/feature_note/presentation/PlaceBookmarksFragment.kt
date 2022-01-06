package com.example.commutingapp.feature_note.presentation

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.commutingapp.R
import com.example.commutingapp.databinding.PlaceBookmarksFragmentBinding
import com.example.commutingapp.views.adapters.PlaceBookmarksAdapter
import com.example.commutingapp.views.ui.recycler_view_model.PlaceBookmarksRVModel
import com.mapbox.mapboxsdk.geometry.LatLng


class PlaceBookmarksFragment : Fragment(R.layout.place_bookmarks_fragment) {
    private var binding:PlaceBookmarksFragmentBinding? = null
    private var listOfPlaceBookmarks:MutableList<PlaceBookmarksRVModel> = ArrayList()


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = PlaceBookmarksFragmentBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    private fun setupRecyclerView() = binding!!.recyclerViewDisplay.apply {
        layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        adapter?.setHasStableIds(true)
        adapter = PlaceBookmarksAdapter(requireActivity(), listOfPlaceBookmarks )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fillList()
        setupRecyclerView()
        binding!!.buttonAdd.setOnClickListener {
            val action = PlaceBookmarksFragmentDirections.actionListFragmentToCommuterFragment(isOpenFromBookmarks = true)
            Navigation.findNavController(binding!!.root).navigate(action)
        }
    }


    @SuppressLint("NotifyDataSetChanged")
    private fun fillList(){
        for(i in 1..12){

            listOfPlaceBookmarks.add(
                PlaceBookmarksRVModel(
                    placeName = "Dita Sta Rosa",
                    placeText = "Agatha homes phase 1",
                    location = LatLng(14.5995,120.9842)
                )
            )
        }
        binding!!.recyclerViewDisplay.adapter?.notifyDataSetChanged()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}