package com.example.commutingapp.views.adapters

import android.annotation.SuppressLint
import android.app.Activity
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView

import com.example.commutingapp.databinding.RvBookmarksBinding
import com.example.commutingapp.feature_note.domain.model.PlaceBookmarks
import com.example.commutingapp.feature_note.presentation.PlaceBookmarksFragmentDirections
import com.example.commutingapp.feature_note.presentation.place.components.PlaceBookmarksEvent
import com.example.commutingapp.feature_note.presentation.place.components.PlaceBookmarksViewModel
import com.example.commutingapp.views.ui.recycler_view_model.PlaceBookmarksRVModel
import com.google.android.material.snackbar.Snackbar
import com.mapbox.mapboxsdk.geometry.LatLng

class PlaceBookmarksAdapter(
    private val activity: Activity,
    private val viewModel: PlaceBookmarksViewModel,
    private val listOfPlaceBookmarks : MutableList<PlaceBookmarksRVModel>
): RecyclerView.Adapter<PlaceBookmarksAdapter.ViewHolder>() {


    private var currentPosition:Int = -1
    private lateinit var binding: RvBookmarksBinding



    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        binding = RvBookmarksBinding.inflate(activity.layoutInflater, parent, false)
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
                currentPosition = position
                provideClickListener(model)

            }
        }


        private fun provideClickListener(model: PlaceBookmarksRVModel) {
            binding.rvItem.setOnClickListener {
                val action = PlaceBookmarksFragmentDirections.actionListFragmentToCommuterFragment(
                    isOpenFromBookmarks = true,
                    bookmarkSelectedLocation = LatLng(
                        model.location.latitude,
                        model.location.longitude
                    )
                )
                Navigation.findNavController(binding.root).navigate(action)
            }

            binding.deleteIcon.setOnClickListener {
                showYesNoDialog(model)
            }
        }


        private fun showYesNoDialog(model: PlaceBookmarksRVModel){

            AlertDialog.Builder(activity)

                .setTitle("Delete place?")
                .setMessage("Are you sure you want to delete this place?")
                .setPositiveButton("YES"){ _ , _ ->

                    deletePlaceFromDB(model)

                    showDeletedSnackbar()

                }.setNegativeButton("NO"){ dialog,_ ->
                    dialog.dismiss()
                }.show()
        }

        @SuppressLint("NotifyDataSetChanged")
        private fun deletePlaceFromDB(model:PlaceBookmarksRVModel){


            viewModel.onEvent(
                PlaceBookmarksEvent.DeletePlaceBookmarks(
                    PlaceBookmarks(
                        placeName = model.placeName,
                        placeText = model.placeText,
                        longitude = model.location.longitude,
                        latitude = model.location.latitude )
                ))

            listOfPlaceBookmarks.removeAt(0)
            notifyItemRemoved(currentPosition)

        }


        private fun showDeletedSnackbar(){
            Snackbar
                .make(binding.root,"Place is deleted",Snackbar.LENGTH_SHORT)
                .setAction("UNDO"){
                    viewModel.onEvent(PlaceBookmarksEvent.RestoreNote)
                }.show()
        }
    }



}