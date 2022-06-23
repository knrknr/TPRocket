package com.mrhi2022.tpquickplaceforkakaoknr.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.mrhi2022.tpquickplaceforkakaoknr.activities.MainActivity
import com.mrhi2022.tpquickplaceforkakaoknr.adapters.PlaceListRecyclerAdapter
import com.mrhi2022.tpquickplaceforkakaoknr.databinding.FragmentPlaceListBinding

class PlaceListFragment : Fragment() {

    val binding : FragmentPlaceListBinding by lazy { FragmentPlaceListBinding.inflate(layoutInflater) }//by lazy를 하는 이유는 시작이 null이어서

    override fun onCreateView(//매개변수가 3개
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? { //View는 nullable 리턴 타입
        return binding.root//root - Relative layout 의미
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setPlaceListRecyclerAdapter()
    }

    fun setPlaceListRecyclerAdapter(){
        val ma : MainActivity = activity as MainActivity//메인 액티비티로 형변환 as 사용

        //아직 MainActivity의 파싱 작업이 완료 되지 않았다면...데이터가 없음
        if (ma.searchPlaceResponse==null) return//함수를 끝내려면 return/이 작업을 해야 에러가 안남

        binding.recyclerview.adapter= PlaceListRecyclerAdapter(requireContext(), ma.searchPlaceResponse!!.documents)
    }

}