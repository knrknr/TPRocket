package com.mrhi2022.tpquickplaceforkakaoknr.activities

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationRequest
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.view.Menu
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.*
import com.google.android.material.tabs.TabLayout
import com.mrhi2022.tpquickplaceforkakaoknr.R
import com.mrhi2022.tpquickplaceforkakaoknr.databinding.ActivityMainBinding
import com.mrhi2022.tpquickplaceforkakaoknr.fragments.PlaceListFragment
import com.mrhi2022.tpquickplaceforkakaoknr.fragments.PlaceMapFragment
import com.mrhi2022.tpquickplaceforkakaoknr.model.KakaoSearchPlaceResponse
import com.mrhi2022.tpquickplaceforkakaoknr.network.RetrofitApiService
import com.mrhi2022.tpquickplaceforkakaoknr.network.RetrofitHelper
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit

class MainActivity : AppCompatActivity() {

    val binding:ActivityMainBinding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    //1. 검색 장소 키워드
    var searchQuery:String = "화장실" //앱 초기 검색어 - 내 주변 개방 화장실

    //2. 현재 내 위치 정보 객체 생성(위도/경도 정보를 멤버로 보유)
    var myLocation : Location?= null

    //3. kakao 검색 결과 응답 객체 생성 : ListFragment, MapFregment 모두 이 정보를 사용하기 때문에...
    var searchPlaceResponse : KakaoSearchPlaceResponse? = null


    //[Google Fused Location API 사용 : play-services- location]
    val providerClient : FusedLocationProviderClient by lazy { LocationServices.getFusedLocationProviderClient(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root) //root- RelativeLayout

        //툴바를 제목줄로 설정
        setSupportActionBar(binding.toolbar)

        //첫 실행될 프레그먼트를 동적으로 추가[첫 시작은 무조건 list 형식으로]
        supportFragmentManager.beginTransaction().add(R.id.container_fragment, PlaceListFragment()).commit()

        //탭 레이아웃의 탭 버튼 클릭시에 보여줄 프레그먼트 변경
        binding.layoutTab.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener{
            override fun onTabSelected(tab: TabLayout.Tab?) { //탭이 선택됐을때//tab-TabItem
                if(tab?.text == "LIST"){
                    supportFragmentManager.beginTransaction().replace(R.id.container_fragment, PlaceListFragment()).commit()//기존의 프레그먼트를 없애고 새로 만듦
                }else if(tab?.text == "MAP"){
                    supportFragmentManager.beginTransaction().replace(R.id.container_fragment, PlaceMapFragment()).commit()
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {//같은 탭을 또 눌렀을때
            }

        })

        //검색어 입력에 따라 장소 검색 요청 하기
        binding.etSearch.setOnEditorActionListener { textView, i, keyEvent ->

            searchQuery = binding.etSearch.text.toString()
            //카카오 장소 검색 API 작업 요청
            searchPlaces()


            //true를 하면? 원래 키패드의 처리 작업을 아무것도 안하겠다는 뜻
            false
        }

        //특정 키워드 단축 choice 버튼들에 리스너 처리하는 함수 호출
        setChoiceButtonsListener()

        //내 위치 정보 제공은 동적 퍼미션
        val permissions : Array<String> = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
        if (checkSelfPermission(permissions[0]) == PackageManager.PERMISSION_DENIED) {
            //퍼미션을 요청하는 다이얼로그 보이기
            requestPermissions(permissions, 10)
        }else {
            //내 위치 탐색 요청하는 기능을 호출
            requestMyLocation()
        }

    }//onCreate method...

    override fun onRequestPermissionsResult( //퍼미션에 대한 결과
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == 10 && grantResults[0] == PackageManager.PERMISSION_GRANTED) requestMyLocation()
        else Toast.makeText(this, "내 위치 정보를 제공하지 않아 검색 기능을 사용할 수 없습니다!", Toast.LENGTH_SHORT).show()
    }

    private fun requestMyLocation(){
        //내 위치 정보를 얻어오는 기능 코드

        //위치 검색 기중 설정값 객체
        val request: com.google.android.gms.location.LocationRequest= com.google.android.gms.location.LocationRequest.create()
        request.interval= 1000//1초마다 갱신
        request.priority= Priority.PRIORITY_HIGH_ACCURACY //높은 정확도 우선시

        //실시간 위치 정보 갱신을 요청
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(//둘 중 하나라도 허가되어 있지 않으면 리턴햇
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        providerClient.requestLocationUpdates(request, locationCallback, Looper.getMainLooper())

    }

    //위치 정보 검색 결과에 대한 콜백 객체
    private val locationCallback : LocationCallback= object : LocationCallback(){
        override fun onLocationResult(p0: LocationResult) {//콜백이 받아온 객체를 가지고 있는게 p0
            super.onLocationResult(p0)

            //갱신된 위치정보결과 객체에게 위치 정보 얻어오기
            myLocation= p0.lastLocation//갱신된 마지막 위치를 줘

            //위치 탐색이 끝났으니 내 위치 정보 업데이트는 이제 그만...종료....
            providerClient.removeLocationUpdates(this) //this - locationCallback 객체

            //내 위치 정보가 있으니 카카오 검색을 시작!
            searchPlaces()
        }
    }


    private fun setChoiceButtonsListener(){
        binding.layoutChoice.choiceWc.setOnClickListener { clickChoice(it) }
        binding.layoutChoice.choiceGas.setOnClickListener { clickChoice(it) }
        binding.layoutChoice.choiceEv.setOnClickListener { clickChoice(it) }
        binding.layoutChoice.choiceMovie.setOnClickListener { clickChoice(it) }
        binding.layoutChoice.choicePark.setOnClickListener { clickChoice(it) }
        binding.layoutChoice.choiceFood.setOnClickListener { clickChoice(it) }
        binding.layoutChoice.choiceFood1.setOnClickListener { clickChoice(it) }
        binding.layoutChoice.choiceFood2.setOnClickListener { clickChoice(it) }
        binding.layoutChoice.choiceFood3.setOnClickListener { clickChoice(it) }
        binding.layoutChoice.choiceFood4.setOnClickListener { clickChoice(it) }
        binding.layoutChoice.choiceFood5.setOnClickListener { clickChoice(it) }
    }

    //멤버 변수 위치 - 액티비티가 꺼져있어도 남아있음
    //멤버 변수로 기존 선택된 뷰의 id를 저장하는 변수
    var choiceID= R.id.choice_wc

    private fun clickChoice(view: View){

        //기존 선택된 뷰의 배경 이미지를 변경
        //기존의 뷰를 찾음
        findViewById<ImageView>(choiceID).setBackgroundResource(R.drawable.bg_choice)

        //현재 선택된 뷰의 배경 이미지를 변경
        view.setBackgroundResource(R.drawable.bg_choice_selected)

        //현재 선택한 뷰의 id를 멤버 변수에 저장
        choiceID= view.id

        //초이스 할 것에 따라 검색 장소 키워드를 변경하여 다시 요청
        when(choiceID){
            R.id.choice_wc-> searchQuery= "화장실"
            R.id.choice_gas-> searchQuery= "주유소"
            R.id.choice_ev-> searchQuery= "전기차 충전소"
            R.id.choice_movie-> searchQuery= "영화관"
            R.id.choice_park-> searchQuery= "공원"
            R.id.choice_food-> searchQuery= "맛집"
            R.id.choice_food1-> searchQuery= "약국"
            R.id.choice_food2-> searchQuery= "약국"
            R.id.choice_food3-> searchQuery= "약국"
            R.id.choice_food4-> searchQuery= "약국"
            R.id.choice_food5-> searchQuery= "약국"
        }

        //새로운 검색 요청
        searchPlaces()

        //검색창에 글씨가 있다면 지우기
        binding.etSearch.text.clear()
        binding.etSearch.clearFocus()//이전 포커스로 인해 커서가 남아 있을 수 있어서

    }

    //카카오 키워드 장소 검색 API 작업을 수행하는 기능 매소드
    private fun searchPlaces(){
        //Toast.makeText(this, "$searchQuery : ${myLocation?.latitude} , ${myLocation?.longitude}", Toast.LENGTH_SHORT).show()

        binding.progressbar.visibility= View.VISIBLE//이전에 검색때문에 gone 시켜뇟을 수도 있어서 search를 누르면 이거

        //레트로핏 작업을 이용하여 카카오 키워드 장소 검색 API 파싱하기
        //###################################################################-RetrofitApiService.kt[인터페이스]/참고
        //https://developers.kakao.com/docs/latest/ko/local/dev-guide#search-by-keyword -카카오 개발자 문서 참고
        //GET /v2/local/search/keyword.${FORMAT} HTTP/1.1//GET 방식//${FORMAT} 부분에 JSON 으로 바꾸기
        //Host: https://dapi.kakao.com //baseUrl
        //Authorization: KakaoAK ${REST_API_KEY}
        //#####################################################################

        //레트로핏 작업을 이용하여 카카오 키워드 장소 검색 API 파싱하기
        val retrofit : Retrofit= RetrofitHelper.getRetrofitInstance("https://dapi.kakao.com")
        retrofit.create(RetrofitApiService::class.java).searchPlaces(searchQuery, myLocation?.longitude.toString(), myLocation?.latitude.toString()).enqueue(object :Callback<KakaoSearchPlaceResponse>{
            override fun onResponse(
                call: Call<KakaoSearchPlaceResponse>,
                response: Response<KakaoSearchPlaceResponse>
            ) {

                searchPlaceResponse= response.body()

                binding.progressbar.visibility= View.GONE

                //우선...객체가 잘 파싱되었는지 확인
                //AlertDialog.Builder(this@MainActivity).setMessage(searchPlaceResponse?.documents!!.size.toString()).show()

                //무조건 검색이 완료되면 PlaceListFragment부터 보여주기
                supportFragmentManager.beginTransaction().replace(R.id.container_fragment, PlaceListFragment()).commit() //기존의 프래그먼트를 뜯어내고 PlaceListFragment를 보여줌

                //탭 버튼의 위치를 "LIST"라는 탭으로 변경/검색을 하면 기본 탭이 'LIST'로 됨
                binding.layoutTab.getTabAt(0)?.select()//getTabAt()-파라미터 index - 순서 /0-'LIST'

            }

            override fun onFailure(call: Call<KakaoSearchPlaceResponse>, t: Throwable) {
                Toast.makeText(this@MainActivity, "서버 오류가 있습니다! \n잠시뒤에 다시 시도해주시기 바랍니다.", Toast.LENGTH_SHORT).show()
            }

        })

//        retrofit.create(RetrofitApiService::class.java).searchPlacesToString(searchQuery, myLocation?.longitude.toString(), myLocation?.latitude.toString()).enqueue(object : Callback<String>{
//            override fun onResponse(call: Call<String>, response: Response<String>) {
//               var s= response.body()
//                AlertDialog.Builder(this@MainActivity).setMessage(s.toString()).create().show()
//            }
//
//            override fun onFailure(call: Call<String>, t: Throwable) {
//                Toast.makeText(this@MainActivity, "서버 오류가 있습니다! \n 잠시뒤에 다시 시도해주시기 바랍니다.", Toast.LENGTH_SHORT).show()
//            }
//        })//레트로핏 보고 설계도면 줄게 이 설계도면처럼 만들어줭




    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.actionbar_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

}