package com.mrhi2022.tpquickplaceforkakaoknr.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.util.Utility
import com.kakao.sdk.user.UserApiClient
import com.mrhi2022.tpquickplaceforkakaoknr.G
import com.mrhi2022.tpquickplaceforkakaoknr.databinding.ActivityLoginBinding
import com.mrhi2022.tpquickplaceforkakaoknr.model.NaverUserInfoResponse
import com.mrhi2022.tpquickplaceforkakaoknr.model.UserAccount
import com.mrhi2022.tpquickplaceforkakaoknr.network.RetrofitApiService
import com.mrhi2022.tpquickplaceforkakaoknr.network.RetrofitHelper
import com.navercorp.nid.NaverIdLoginSDK
import com.navercorp.nid.oauth.OAuthLoginCallback
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {

    val binding: ActivityLoginBinding by lazy { ActivityLoginBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        //둘러보기 글씨 클릭으로 로그인 없이 Main 화면 실행
        binding.tvGo.setOnClickListener {
            startActivity( Intent(this, MainActivity::class.java) )
            finish()
        }

        //회원가입 버튼 클릭
        binding.tvSignup.setOnClickListener {
            //회원 가입 화면으로 전환
            startActivity(Intent(this, SignUpActivity::class.java))
        }

        //이메일 로그인 버튼 클릭
        binding.layoutLoginEmail.setOnClickListener {Intent(this, EmailSignInActivity::class.java)
            startActivity(Intent(this, EmailSignInActivity::class.java))

        }

        // 간편로그인 버튼들 클릭
        binding.btnLoginKakao.setOnClickListener { clickLoginKakao() }
        binding.btnLoginGoogle.setOnClickListener { clickLoginGoogle() }
        binding.btnLoginNaver.setOnClickListener { clickLoginNaver() }

        //카카오 로그인 키해시 값 얻어오기
        val keyHash:String = Utility.getKeyHash(this)
        Log.i("keyHash", keyHash) //로그인 화면까지만 가면 실행됨
        //사이트에 플랫폼 등록(수정 키해시 등록)//hnJbVmLKwII2BxNivRo179CJNc=

    }//onCreate method...

    private fun clickLoginKakao(){
        //kakao login sdk....

        //카카오 로그인을 성공했을때 반응하는 콜백 객체 생성
        val callback: (OAuthToken?, Throwable?)->Unit = { token, error ->
            if(error != null){//에러가 있다면?
                Toast.makeText(this, "kakao 로그인 실패", Toast.LENGTH_SHORT).show()

            }else{
                Toast.makeText(this, "kakao 로그인 성공", Toast.LENGTH_SHORT).show()

                //사용자의 정보 요청
                UserApiClient.instance.me { user, error ->
                    if(user != null){
                        var id:String = user.id.toString()
                        var email:String = user.kakaoAccount?.email ?: ""//?: -
                        G.userAccount= UserAccount(id, email)

                        //메인 액티비티로 이동
                        startActivity(Intent(this, MainActivity::class.java))
                        finish()
                    }
                }

            }
        } //콜백인데 자료형이 함수 파라미터가 두개(자료형이 두개) 리턴이 없음

        //카카오톡이 설치되어 있으면 카카오톡 로그인 , 없으면 카카오 계정 로그인
        if(UserApiClient.instance.isKakaoTalkLoginAvailable(this)){
            UserApiClient.instance.loginWithKakaoTalk(this,callback = callback)
        }else{
            UserApiClient.instance.loginWithKakaoAccount(this, callback = callback)
        }

    }

    private fun clickLoginGoogle() {

        //Firebase Authentication - ID공급 업체 (Google)

        //google login 관련 가이드 문서를 firebase 사이트에서 보면
        //무조건 firebase와 연동하도록 auth제품과 연동하여 만들도록 안내

        //그래서 구글 로그인만 하려면 구글 개발자 사이트의 가이드 문서를 참고할 것을 추천

        //구글 로그인 옵션 객체를 생성 - Builder 이용
        val gso: GoogleSignInOptions= GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build()

        //구글 로그인 화면을 보여주는 액티비티를 실행시켜주는 Intent 객체 얻어오기
        val intent : Intent = GoogleSignIn.getClient(this, gso).signInIntent
        resultLauncher.launch(intent)//대신 StartActivityForResult()를 해줌
    }

    //새로운 액티비티를 실행하고 그 결과를 받아오는 객체를 새롭게 등록
    val resultLauncher : ActivityResultLauncher<Intent> = registerForActivityResult(ActivityResultContracts.StartActivityForResult(), object : ActivityResultCallback<ActivityResult>{
        override fun onActivityResult(result: ActivityResult?) {
            if(result?.resultCode== RESULT_CANCELED) return //만약 결과 코드가 캔슬이면 아무것도 하지마!

            //로그인 결과를 가져온 Intent 객체 소환
            val intent : Intent? = result?.data//nullable 일 수도 있으니 ?붙임

            //Intent로 부터 구글 계정 정보를 가져오는 작업 객체 생성하여 결과 데이터 받기
            val account : GoogleSignInAccount = GoogleSignIn.getSignedInAccountFromIntent(intent).result

            var id:String = account.id.toString()
            var email:String = account.email.toString() ?: ""//앞에 계정의 이메일이 null이면 ""로 해라!

            Toast.makeText(this@LoginActivity, "$email", Toast.LENGTH_SHORT).show()
            G.userAccount= UserAccount(id, email)

            //main 화면으로 이동
            startActivity(Intent(this@LoginActivity, MainActivity::class.java))
            finish()

        }
    })

    private fun clickLoginNaver(){

        //네이버 아이디 로그인[네아로] - 사용자 정보를 Rest API로 받아오는 방식
        //Retrofit 네트워크 라이브러리 사용하기

        //네이버 개발자 센터의 가이드문서 참고
        //Nid-Oauth sdk 추가

        //로그인 초기화
        NaverIdLoginSDK.initialize(this,"p7AP_tbgckVwYqe3pG1r","fK9SA1IEZR","주변 정보 검색")

        //로그인 인증 하기...로그인 정보를 받는 것이 아니라
        //로그인 정보를 갖기 위한 REST API의 접근 키(token:토큰)을 발급받는 것임
        //이 token으로 네트워크 API를 통해 json 데이터를 받아 정보를 얻어오는 것임
        NaverIdLoginSDK.authenticate(this, object : OAuthLoginCallback{
            override fun onError(errorCode: Int, message: String) {
                Toast.makeText(this@LoginActivity, "server error : $message", Toast.LENGTH_SHORT).show()
            }

            override fun onFailure(httpStatus: Int, message: String) {
                Toast.makeText(this@LoginActivity, "로그인 실패 : $message", Toast.LENGTH_SHORT).show()
            }

            override fun onSuccess() {
                Toast.makeText(this@LoginActivity, "로그인 성공", Toast.LENGTH_SHORT).show()

                //사용자 정보를 가져오는 REST API의 접속 토큰(Access token)
                val accessToken : String? = NaverIdLoginSDK.getAccessToken()

                //*사용자 정보 가져오는 네트워크 작업 수행(Retrofit library 이용)*
                //https://openapi.naver.com - host 주소(base Url)
                //2)
                val retrofit= RetrofitHelper.getRetrofitInstance("https://openapi.naver.com")

                //5)
                retrofit.create(RetrofitApiService::class.java).getNidUserInfo("Bearer $accessToken").enqueue(object : Callback<NaverUserInfoResponse>{ //object : 익명 객체 [뒤에 상속받을 자료형 명시하고 {}꼭 쓰기!!!!!!]
                    override fun onResponse(
                        call: Call<NaverUserInfoResponse>,
                        response: Response<NaverUserInfoResponse>
                    ) {
                        val userInfo: NaverUserInfoResponse?= response.body()
                        var id : String= userInfo?.response?.id ?: ""
                        val email:String= userInfo?.response?.email ?: ""

                        Toast.makeText(this@LoginActivity, "$email", Toast.LENGTH_SHORT).show()
                        G.userAccount= UserAccount(id, email)

                        //main 화면으로 이동
                        startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                        finish()
                    }

                    override fun onFailure(call: Call<NaverUserInfoResponse>, t: Throwable) {
                        Toast.makeText(this@LoginActivity, "${t.message}", Toast.LENGTH_SHORT).show()
                    }

                })



            }

        })

    }


}//LoginActivity class..기