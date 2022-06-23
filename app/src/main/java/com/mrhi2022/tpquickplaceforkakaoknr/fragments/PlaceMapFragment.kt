package com.mrhi2022.tpquickplaceforkakaoknr.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.mrhi2022.tpquickplaceforkakaoknr.activities.MainActivity
import com.mrhi2022.tpquickplaceforkakaoknr.activities.PlaceUrlActivity
import com.mrhi2022.tpquickplaceforkakaoknr.databinding.FragmentPlaceListBinding
import com.mrhi2022.tpquickplaceforkakaoknr.databinding.FragmentPlaceMapBinding
import com.mrhi2022.tpquickplaceforkakaoknr.model.Place
import net.daum.mf.map.api.MapPOIItem
import net.daum.mf.map.api.MapPoint
import net.daum.mf.map.api.MapView

class PlaceMapFragment : Fragment() {

    val binding : FragmentPlaceMapBinding by lazy { FragmentPlaceMapBinding.inflate(layoutInflater) }//by lazy를 하는 이유는 시작이 null이어서

    override fun onCreateView(//매개변수가 3개
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? { //View는 nullable 리턴 타입
        return binding.root//root - Relative layout 의미
    }

    //카카오 지도는 AVD에서는 테스트가 안됨 (Mac m1 사용자는 가능함)
    val mapView:MapView by lazy { MapView(context) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {//뷰가 다만들어진 다음에
        super.onViewCreated(view, savedInstanceState)

        //MapView 컨테이너에 맵뷰 객체 추가
        binding.containerMapview.addView(mapView)

        //지도 관련 설정(지도 위치, 마커 추가 등.....)
        setMapAndMarkers()
    }

    private fun setMapAndMarkers(){

        //마커 or 말풍선의 클릭이벤트에 반응하는 리스너 등록
        // ** 반드시 마커를 추가하는 것보다 먼저 등록되어 있어야 동작함.**
        mapView.setPOIItemEventListener(markerEventListener)


        //지도 중심좌표 설정
        //현재 내 위치로 이동
        //MainActivity 가 이미 내 위치를 가지고 있음
        //프레그먼트는 자기가 부르고 싶은 액티비티를 부를 수 있음/MainActivity의 2, 3 번을 써야됨
        var lat:Double = (activity as MainActivity).myLocation?.latitude ?: 37.566805 //:?- 앞에꺼가 null이면
        var lng:Double = (activity as MainActivity).myLocation?.longitude ?: 126.9784147

        //위도와 경도를 카카오지도의 맵 좌표 객체(MapPoint)로 생성
        var myMapPoint: MapPoint= MapPoint.mapPointWithGeoCoord(lat, lng)
        mapView.setMapCenterPointAndZoomLevel(myMapPoint, 5, true)
        mapView.zoomIn(true)//줌인 될 때 애니메이션 적용됨
        mapView.zoomOut(true)//줌아웃 될 때 애니메이션 적용됨

        //내 위치 마커 추가
        val marker= MapPOIItem()
        //마커 설정들
        marker.apply {
            //this.itemName="ME"
            //this 생략 가능
            itemName="ME"//마커를 눌렀을때 보여지는 글씨
            mapPoint= myMapPoint
            markerType= MapPOIItem.MarkerType.BluePin
            selectedMarkerType= MapPOIItem.MarkerType.YellowPin
        }

        mapView.addPOIItem(marker)

        //검색 결과 장소들 마커 추가하기
        val documents : MutableList<Place>? = (activity as MainActivity).searchPlaceResponse?.documents
        documents?.forEach {
            //? - 앞에 꺼가 null이면 forEach를 안함
            //forEach - 배열의 요소의 개수만큼 {}안을 실행시켜줌
            //it - 장소 하나하나
            val point : MapPoint = MapPoint.mapPointWithGeoCoord(it.y.toDouble(), it.x.toDouble())

            //마커 객체 생성
            var marker: MapPOIItem= MapPOIItem().apply {
                itemName= it.place_name
                mapPoint= point
                markerType= MapPOIItem.MarkerType.RedPin
                selectedMarkerType= MapPOIItem.MarkerType.YellowPin
                //해당 POI item(마커)와 관련된 정보를 저장하고 있는 데이터 객체를 보관
                userObject= it//it - place

            }//apply 실행 결과가 다시 MapPOIItem
            mapView.addPOIItem(marker)

        }//forEach../////////////////////////////////


    }//method ..

    //마커나 말풍선이 클릭되는 이벤트에 반응하는 리스너 객체//////////
    private val markerEventListener: MapView.POIItemEventListener= object : MapView.POIItemEventListener{
        override fun onPOIItemSelected(p0: MapView?, p1: MapPOIItem?) {//파라미터 첫번째 : 마커
            //마커가 클릭될때 발동하는 메소드

        }

        override fun onCalloutBalloonOfPOIItemTouched(p0: MapView?, p1: MapPOIItem?) {
            //deprecated.. 이제는 아래 오버로딩된 메소드 사용 권장
        }

        override fun onCalloutBalloonOfPOIItemTouched(
            p0: MapView?,
            p1: MapPOIItem?,
            p2: MapPOIItem.CalloutBalloonButtonType?
        ) {
            //마커의 말풍선을 클릭헸을때 발동하는 메소드
            //두번째 파라미터 p1 : 마커 객체
            if (p1?.userObject == null) return//userObject 가 저장이 안되어있으면?

            val place: Place= p1?.userObject as Place

            //장소에 대한 상세정보 보여주는 화면으로 이동
            val intent= Intent(context, PlaceUrlActivity::class.java)
            intent.putExtra("placeUrl", place.place_url)
            startActivity(intent)
        }

        override fun onDraggablePOIItemMoved(p0: MapView?, p1: MapPOIItem?, p2: MapPoint?) {
            //마커를 드래그하여 움직였을때 발동
        }
    }

}//Fragment class..