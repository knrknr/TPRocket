package com.mrhi2022.tpquickplaceforkakaoknr.model

//https://developers.naver.com/docs/login/profile/profile.md 참고

//3)
data class NaverUserInfoResponse(var resultcode:String, var message:String, var response:NidUser)

data class NidUser (var id:String, var email:String)
