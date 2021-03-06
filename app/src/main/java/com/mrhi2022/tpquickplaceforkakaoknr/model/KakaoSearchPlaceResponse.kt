package com.mrhi2022.tpquickplaceforkakaoknr.model

data class KakaoSearchPlaceResponse(var meta:PlaceMeta, var documents:MutableList<Place>)

data class PlaceMeta(var total_count:Int, var pageable_count:Int, var is_end:Boolean)

data class Place(
    var id: String,
    var place_name:String,
    var category_name:String,
    var phone:String,
    var address_name:String,//지번 주소
    var road_address_name:String,// 도로명 주소
    var x:String,//longitude
    var y:String, //latitude
    var place_url:String,
    var distance:String //내 좌표까지의 거리(단위 : meter) - x, y 파라미터를 준 경우만
)
