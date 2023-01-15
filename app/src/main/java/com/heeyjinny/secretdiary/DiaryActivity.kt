package com.heeyjinny.secretdiary

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import androidx.core.widget.addTextChangedListener

//1
//액티비티 생성 후 매니페스트에 등록필수

class DiaryActivity : AppCompatActivity() {

    //4-3
    //메인 스레드에 연결되어 있는 핸들러 생성
    val handler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_diary)

        //2
        //다이어리 액티비티의 뷰가 실행되었을 때
        //내부 저장소에서 저장되어있는 내용을 가져와 화면 에디트텍스트에 보여주기

        //SharedPreferences사용하여 내부 저장소 파일에 데이터 저장...
        //앞에서 파일이름을 password로 하여 같이 저장해도 상관 없지만
        //다이어리 내용을 가지고 있어야 할 파일명과 맞지 않이 헷갈릴 수 있기때문에
        //새 파일명을 지정해 데이터 저장하여 사용할 것임...

        //2-1
        //레이아웃의 에디트텍스트 연결
        val etContent = findViewById<EditText>(R.id.etContent)

        //2-2
        //getSharedPreferences를 하여 컨텐츠파일의 내용 가져와 변수에 저장
        val contentPreferences = getSharedPreferences("content", Context.MODE_PRIVATE)

        //2-3
        //에디트텍스트에 가져온 내용을 문자열로 저장하기
        //getString(파일명, 기본값)
        etContent.setText(contentPreferences.getString("content", ""))

        //4
        //다이어리 텍스트가 한 글자씩 변경될 때마다
        //내용을 저장을 위해 계속 실행되기 때문에 실행 낭비가 됨
        //효율성을 높이기 위해 글 작성 중 지정 시간동안 멈출 때마다 저장되도록
        //백그라운드에서 작동하는 스레드 기능 이용하기

        //4-1
        //백그라운드 스레드에 넣을 때 Runnable 인터페이스 사용***
        val runnable = Runnable {
            //4-2
            //저장소에 내용을 비동기(apply)로 저장하기
            contentPreferences.edit {
                putString("content", etContent.text.toString())
            }
            //4-3
            //새로 생성한 백그라운드 스레드는 UI에 접근할 수 없기 때문에
            //Handler를 사용하여 메인스레드와 연결하여 UI에 접근하여 수정할 수 있음
            //전역변수로 핸들러 생성...
            //로그로 텍스트 저장이 잘 되는지 확인
            Log.d("DiaryActivity", "TextSaved ${etContent.text.toString()}")

        }//runnable

        //3
        //다이어리 컨텐츠의 텍스트 내용이 변경될 때마다
        //변경된 내용을 저장하기 위해 호출하는 기능 사용
        //.addTextChangedListener{}
        etContent.addTextChangedListener {
            //5
            //백그라운드 스레드에서 일어나는 기능 구현하고
            //비동기로 저장한 내용을 핸들러를 이용해 메인과 백그라운드 연결 후
            //Hadler기능 중 postDelay기능을 사용하여
            //지정된 시간 후에 백그라운드 스레드의 기능이 구현되도록 설정

            //0.5초 이내에 텍스트 체인지가 계속 일어나고 있다면
            //runnable의 동작 실행이 되지 않도록 .removeCallbacks()
            //만약 0.5초 이상 텍스트가 변경되지 않았다면
            //핸들러를 이용해 runnable의 동작이 수행되면서 내부저장소에 저장됨

            //로그로 텍스트 변경을 잘 읽을 수 있는지 확인
            Log.d("DiaryActivity", "TextChanged :: $it")

            //5-1
            //이전에 백그라운드스레드(runnable)이 진행되고 있다면
            //지우고 다시 실행되도록...
            handler.removeCallbacks(runnable)
            //5-2
            //0.5초(500밀리초) 후에 핸들러를 이용하여 백그라운드 스레드(변수 runnable)기능 동작
            handler.postDelayed(runnable, 500)
        }

    }//onCreate
}//DiaryActivity