package com.mrhi2022.tpquickplaceforkakaoknr.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.WebChromeClient
import android.webkit.WebViewClient
import com.mrhi2022.tpquickplaceforkakaoknr.databinding.ActivityPlaceUrlBinding

class PlaceUrlActivity : AppCompatActivity() {

    val binding:ActivityPlaceUrlBinding by lazy { ActivityPlaceUrlBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.wv.webViewClient= WebViewClient()//크롬 브라우저 말고 내 껄로 띄워주세요!
        binding.wv.webChromeClient= WebChromeClient()

        binding.wv.settings.javaScriptEnabled= true

        val placeUrl:String = intent.getStringExtra("placeUrl") ?: ""//앞에가 null이면 빈 url을 보여줌
        binding.wv.loadUrl(placeUrl)
    }

    //디바이스의 뒤로가기 버튼을 클릭헸을때
    //웹 페이지의 히스토리가 있다면..웹 페이지만 뒤로가기

    override fun onBackPressed() {
        if (binding.wv.canGoBack()) binding.wv.goBack()//페이지를 가랏
        else super.onBackPressed()
    }
}