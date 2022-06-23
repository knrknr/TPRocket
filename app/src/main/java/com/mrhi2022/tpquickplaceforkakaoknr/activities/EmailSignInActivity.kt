package com.mrhi2022.tpquickplaceforkakaoknr.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import com.google.firebase.firestore.FirebaseFirestore
import com.mrhi2022.tpquickplaceforkakaoknr.G
import com.mrhi2022.tpquickplaceforkakaoknr.R
import com.mrhi2022.tpquickplaceforkakaoknr.databinding.ActivityEmailSignInBinding
import com.mrhi2022.tpquickplaceforkakaoknr.model.UserAccount

class EmailSignInActivity : AppCompatActivity() {

    val binding:ActivityEmailSignInBinding by lazy { ActivityEmailSignInBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        //툴바를 제목줄로 설정
        setSupportActionBar(binding.toolbar)

        supportActionBar?.setDisplayShowTitleEnabled(false) //타이틀을 안보여주기 위해
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_24)

        binding.btnSignin.setOnClickListener { clickSignIn() }
    }

    //업버튼 눌렀을때 자동으로 발동하는 메소드
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed() //백버튼을 눌렀을때와 같음
        return super.onSupportNavigateUp()
    }



    private fun clickSignIn(){

        var email:String = binding.etEmail.text.toString()
        var password:String = binding.etPassword.text.toString()

        //Firebase Firestore DB에서 이메일 로그인 확인
        val db:FirebaseFirestore= FirebaseFirestore.getInstance()//FirebaseFirestore.getInstance() - 이미 데이터베이스와 연결이 된 친구
        db.collection("emailUsers")
            .whereEqualTo("email", email)//앞에 있는 email은 사이트에 있는 필드값 식별자
            .whereEqualTo("password", password)
            .get().addOnSuccessListener {
                if(it.documents.size>0){
                    //로그인 성공
                    //firestoreDB의 랜덤한 document 명을 id로 사용하고자 함
                    var id:String = it.documents[0].id //랜덤한 document 명
                    G.userAccount= UserAccount(id, email) //추가적으로 필요한 정보는 G 클래스 안에 있는 companion object에 넣으면 됨

                    //로그인 성공했으면
                    val intent:Intent = Intent(this, MainActivity::class.java)

                    //기존 task의 모든 액티비티를 제거하고 새로운 task로 시작!!/new task가 실행된다는건 기존꺼 말고 새로운곳에서 작업한다는 뜻 ///Back Stack이 하나 만들어지는걸 한개의 task가 만들어졌다고 함
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)//모든 작업들이 사라짐
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)//새 작업 시작

                    startActivity(intent)

                }else{
                    //로그인 성공못함
                    AlertDialog.Builder(this).setMessage("이메일과 비밀번호를 다시 확인해주세요!").create().show()
                    binding.etEmail.requestFocus()
                    binding.etEmail.selectAll()
                }
            }
    }
}