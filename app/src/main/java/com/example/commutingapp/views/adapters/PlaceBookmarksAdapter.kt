package com.example.commutingapp.views.adapters

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.example.commutingapp.databinding.PlaceBookmarksRecyclerviewAdapterBinding
import com.example.commutingapp.feature_note.domain.model.PlaceBookmarks
import com.example.commutingapp.feature_note.presentation.place.components.PlaceBookmarksEvent
import com.example.commutingapp.feature_note.presentation.place.components.PlaceBookmarksViewModel
import com.example.commutingapp.views.ui.recycler_view_model.PlaceBookmarksRVModel
import com.google.android.material.snackbar.Snackbar

class PlaceBookmarksAdapter(
    private val activity: Activity,
    private val viewModel: PlaceBookmarksViewModel,
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

                provideClickListener(model)

            }
        }



        private fun provideClickListener(model: PlaceBookmarksRVModel){

            binding.deleteIcon.setOnClickListener {

                showYesNoDialog(model)
            }
        }


        private fun showYesNoDialog(model: PlaceBookmarksRVModel){

            AlertDialog.Builder(activity)

                .setTitle("Delete place?")
                .setMessage("Are you sure you want to delete this place?")
                .setPositiveButton("YES"){ _ , _ ->

                    insertToDatabase(model)
                    showDeletedSnackbar()

                }.setNegativeButton("NO"){ dialog,_ ->
                    dialog.dismiss()
                }.show()
        }

        private fun insertToDatabase(model:PlaceBookmarksRVModel){
            viewModel.onEvent(
                PlaceBookmarksEvent.DeletePlaceBookmarks(
                    PlaceBookmarks(
                        placeName = model.placeName,
                        placeText = model.placeText,
                        longitude = model.location.longitude,
                        latitude = model.location.latitude )
                ))
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