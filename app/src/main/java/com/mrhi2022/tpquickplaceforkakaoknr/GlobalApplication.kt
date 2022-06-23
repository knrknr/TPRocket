package com.mrhi2022.tpquickplaceforkakaoknr

import android.app.Application
import com.kakao.sdk.common.KakaoSdk

class GlobalApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        // Kakao SDK 초기화
        KakaoSdk.init(this, "67daa89e82521ee7f679c3dfef4312e6")//사이트에 있는 네이티브 앱 키 복붙
    }
}