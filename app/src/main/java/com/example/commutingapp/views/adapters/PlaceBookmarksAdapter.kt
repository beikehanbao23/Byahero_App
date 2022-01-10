package com.example.commutingapp.views.adapters

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.commutingapp.databinding.PlaceBookmarksRecyclerviewAdapterBinding
import com.example.commutingapp.views.ui.recycler_view_model.PlaceBookmarksRVModel

class PlaceBookmarksAdapter(
    private val activity: Activity,
    private val listOfPlaceBookmarks : List<PlaceBookmarksRVModel>
): RecyclerView.Adapter<PlaceBookmarksAdapter.ViewHolder>() {

    private lateinit var binding: PlaceBookmarksRecyclerviewAdapterBinding



    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        binding = PlaceBookmarksRecyclerviewAdapterBinding.inflate(activity.layoutInflater, parent, false)
        return ViewHolder(binding.root)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindAttributes(position)
    }

    override fun getItemCount(): Int = listOfPlaceBookmarks.size


    inner class ViewHolder(view: View):RecyclerView.ViewHolder(view){
        fun bindAttributes(position: Int) {
            val model : PlaceBookmarksRVModel = listOfPlaceBookmarks[position]
            with(binding){
                textViewPlaceName.text = model.placeName
                textViewPlaceText.text = model.placeText
            }
        }
    }



}