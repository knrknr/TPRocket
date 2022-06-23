package com.mrhi2022.tpquickplaceforkakaoknr.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mrhi2022.tpquickplaceforkakaoknr.R
import com.mrhi2022.tpquickplaceforkakaoknr.activities.PlaceUrlActivity
import com.mrhi2022.tpquickplaceforkakaoknr.databinding.RecyclerItemListFragmentBinding
import com.mrhi2022.tpquickplaceforkakaoknr.model.Place

class PlaceListRecyclerAdapter(val context : Context, var documents: MutableList<Place>) : RecyclerView.Adapter<PlaceListRecyclerAdapter.VH>() {//클래스를 받을땐 ()있어야됨

    inner class VH (itemView:View) : RecyclerView.ViewHolder(itemView){
        val binding : RecyclerItemListFragmentBinding= RecyclerItemListFragmentBinding.bind(itemView)//VH 는 바인딩을 이용해서 tv 세개를 바인드
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val itemView : View = LayoutInflater.from(context).inflate(R.layout.recycler_item_list_fragment, parent, false)
        return VH(itemView)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val place : Place= documents[position]

        holder.binding.placeName.text= place.place_name
        if (place.road_address_name=="") holder.binding.tvAddress.text= place.address_name
        else holder.binding.tvAddress.text= place.road_address_name
        holder.binding.tvDistance.text= place.distance +"m"

        //장소 아이템뷰를 클릭했을때 장소에 대한 상세 정보 화면으로 이동
        holder.itemView.setOnClickListener {
            val intent: Intent = Intent(context, PlaceUrlActivity::class.java)
            intent.putExtra("placeUrl", place.place_url)
            context.startActivity(intent)
        }

    }

    override fun getItemCount(): Int = documents.size //할당연산자를 이용한 함수 리턴 축약 표현

}