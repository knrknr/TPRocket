package com.mrhi2022.tpquickplaceforkakaoknr.network

import com.google.cloud.audit.AuthorizationInfo
import com.mrhi2022.tpquickplaceforkakaoknr.model.KakaoSearchPlaceResponse
import com.mrhi2022.tpquickplaceforkakaoknr.model.NaverUserInfoResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.Query

interface RetrofitApiService {

    //4)
    //네아로 사용자 정보 API
    @GET("/v1/nid/me")
    fun getNidUserInfo(@Header("Authorization") authorization: String):retrofit2.Call<NaverUserInfoResponse>//규격을 정하는 것이 인터페이/요청변수가 있음 @query
    //@Header("Authorization") - 강제 주석[무조건 이렇게 읽어야 됨] //매번 값이 바뀌는 것은 위 방법 사용-파라미터에 Header

    //카카오 키워드 장소 검색 API - 결과를 JSON 파싱한 KakaoSearchPlaceResponse로...
    @Headers("Authorization: KakaoAK 9734e24e876b77420ee02ff1282f143e")
    @GET("/v2/local/search/keyword.json")
    fun searchPlaces(@Query("query") query:String, @Query("x") longitude:String, @Query("y") latitude:String) : Call<KakaoSearchPlaceResponse> //Response 갹채안에 meta, document가 있음

    //카카오 키워드 장소 검색 API - 결과를 String으로...
    @Headers("Authorization: KakaoAK 9734e24e876b77420ee02ff1282f143e")
    @GET("/v2/local/search/keyword.json")
    fun searchPlacesToString(@Query("query") query:String, @Query("x") longitude:String, @Query("y") latitude:String) : Call<String>

}