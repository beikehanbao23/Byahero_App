package com.example.commutingapp.feature_note.presentation

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.commutingapp.R
import com.example.commutingapp.databinding.PlaceBookmarksFragmentBinding
import com.example.commutingapp.feature_note.presentation.place.components.PlaceBookmarksViewModel
import com.example.commutingapp.views.adapters.PlaceBookmarksAdapter
import com.example.commutingapp.views.ui.recycler_view_model.PlaceBookmarksRVModel
import com.mapbox.mapboxsdk.geometry.LatLng
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PlaceBookmarksFragment : Fragment(R.layout.place_bookmarks_fragment) {
    private val viewModel: PlaceBookmarksViewModel by viewModels()
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
        setupRecyclerView()

        renderData()
        provideClickListener()

    }

    @SuppressLint("NotifyDataSetChanged")
    private fun renderData(){

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED){
                 viewModel.state.collect{

                     if(it.placeBookmarks.isEmpty()){
                         binding!!.tvDisplay.visibility = View.VISIBLE
                         return@collect
                     }

                     binding!!.tvDisplay.visibility = View.INVISIBLE
                     it.placeBookmarks.forEach { placeBookmarks->
                         listOfPlaceBookmarks.add(
                             PlaceBookmarksRVModel(
                                 placeName = placeBookmarks.placeName,
                                 placeText = placeBookmarks.placeText,
                                 location = LatLng(placeBookmarks.latitude, placeBookmarks.longitude)
                             )
                         )
                     }
                     binding!!.recyclerViewDisplay.adapter?.notifyDataSetChanged()


                 }

            }
        }





                



    }
    private fun provideClickListener(){
        binding!!.imageButtonOrder.setOnClickListener {

        }
        binding!!.buttonAdd.setOnClickListener {
            val action = PlaceBookmarksFragmentDirections.actionListFragmentToCommuterFragment(isOpenFromBookmarks = true)
            Navigation.findNavController(binding!!.root).navigate(action)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}