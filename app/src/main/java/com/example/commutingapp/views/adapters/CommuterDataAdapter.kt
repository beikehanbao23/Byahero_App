package com.example.commutingapp.views.adapters

import android.annotation.SuppressLint
import android.app.Activity
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.commutingapp.data.local_db.Commuter
import com.example.commutingapp.databinding.CommuterDataRecyclerViewAdapterBinding
import com.example.commutingapp.utils.others.WatchFormat
import java.text.SimpleDateFormat
import java.util.*

class CommuterDataAdapter(
    private val activity: Activity
): RecyclerView.Adapter<CommuterDataAdapter.ViewHolder>() {

    private lateinit var binding:CommuterDataRecyclerViewAdapterBinding

    private val diffCallback = object: DiffUtil.ItemCallback<Commuter>(){
        override fun areItemsTheSame(oldItem: Commuter, newItem: Commuter): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Commuter, newItem: Commuter): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }

    }

    private val differ = AsyncListDiffer(this,diffCallback)
     fun submitList(list:List<Commuter>) = differ.submitList(list)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommuterDataAdapter.ViewHolder {
        binding = CommuterDataRecyclerViewAdapterBinding.inflate(activity.layoutInflater,parent,false)
        return ViewHolder(binding.root)

    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: CommuterDataAdapter.ViewHolder, position: Int) {

        with(binding) {

            val commuter = differ.currentList[position]
            Glide.with(activity).load(commuter.image).override(1080, 600).thumbnail(0.5f).diskCacheStrategy(
                DiskCacheStrategy.ALL).into(ivCommuteImage)
            val calendar = Calendar.getInstance().apply {
                timeInMillis = commuter.timestamp
            }
            val dateFormat = SimpleDateFormat("dd:MM:yy", Locale.getDefault())


            tvDate.text = dateFormat.format(calendar.time)
            tvAvgSpeed.text = "${commuter.averageSpeed_KmH / 1000f}km/h"
            tvDistance.text = "${commuter.distanceInMeters / 1000f}km"
            tvTime.text = WatchFormat.getFormattedStopWatchTime(commuter.timeInMillis)
            tvWentPlaces.text = "${commuter.wentPlaces}"
        }
    }

    override fun getItemCount(): Int {
       return differ.currentList.size
    }
    override fun getItemViewType(position: Int): Int {
        return position
    }

    inner class ViewHolder(view: View):RecyclerView.ViewHolder(view)
}