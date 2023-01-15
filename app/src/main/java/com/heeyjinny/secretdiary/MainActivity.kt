package com.heeyjinny.secretdiary

import android.content.Context
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.NumberPicker
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.edit
import com.heeyjinny.secretdiary.databinding.ActivityMainBinding

/**  비밀 다이어리 만들기  **/

//1
//레이아웃 설정 : activity_main.xml
//넘버피커, 앱컴팻 버튼, 커스텀 폰트 적용

class MainActivity : AppCompatActivity() {

    //2
    //레이아웃 넘버피커를 가지고 있는 변수 선언하고
    //최대, 최소값 설정 .apply{}
    //뷰바인딩 사용없이 작성하기...
    val numPicker1: NumberPicker by lazy {
        findViewById<NumberPicker>(R.id.numPicker1)
            .apply {
                minValue = 0
                maxValue = 9
            }
    }

    val numPicker2: NumberPicker by lazy {
        findViewById<NumberPicker>(R.id.numPicker2)
            .apply {
                minValue = 0
                maxValue = 9
            }
    }

    val numPicker3: NumberPicker by lazy {
        findViewById<NumberPicker>(R.id.numPicker3)
            .apply {
                minValue = 0
                maxValue = 9
            }
    }

    //3
    //오픈버튼과 패스워드 변경버튼 선언
    val btnOpen: AppCompatButton by lazy {
        findViewById<AppCompatButton>(R.id.btnOpen)
    }

    val btnChangePW: AppCompatButton by lazy {
        findViewById<AppCompatButton>(R.id.btnChangePW)
    }

    //6-1
    //비밀번호 변경 시 다른 위젯이 동작하지 못 하도록
    //전역변수 생성
    var changePWMode = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //4
        //넘버피커 초기화하여 메모리에 할당
        numPicker1
        numPicker2
        numPicker3

        //5
        //오픈버튼 동작 정의
        //버튼을 눌렀을 때 저장되어있는 패스워드 값을 가져와 넘버피커 1,2,3의 숫자들과 비교
        //SharedPreference를 사용하여 기기에 저장되어있는 패스워드를 가져오기
        btnOpen.setOnClickListener {

            //6-1
            //비밀번호 변경 시 다른 위젯이 동작하지 못 하도록 메시지 띄우고
            //onCreate()에서 나가는 것인지 클릭리스너 람다에서 나가는 것인지 리턴 함수를 통해 명시
            //다시 리스너로 돌아가기...
            if (changePWMode){
                Toast.makeText(this, "비밀번호 변경 중입니다...", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            //5-1
            //SharedPreference를 사용해 password라는 파일에 저장되어 있는 값 접근하기
            //모드는 공유하지 않고 이 앱에서만 사용할 수 있는 Context.MODE_PRIVATE 모드 정의
            val passwordPreferences = getSharedPreferences("password", Context.MODE_PRIVATE)

            //5-2
            //현재 넘버피커에 설정되어있는 값 받아오기
            //문자열 타입으로 값 가져오기
            //val passwordFromUser = numPicker1.value.toString() + numPicker2.value.toString() + numPicker2.value.toString()
            val passwordFromUser = "${numPicker1.value}${numPicker2.value}${numPicker3.value}"

            //5-3
            //현재 넘버피커에 설정된 값(passwordFromUser)과
            //SharedPreference를 사용해 특정 파일에 접근한 값(passwordPreferences)을 비교
            //get메서드 입력값의 파라미터는 기본값을 지정할 수 있음
            //기본값(defaultValue)를 지정하면 해당 키의 데이터가 없을 때 지정한 기본값 반환
            //앱 처음 실행하면 데이터가 없으므로 000이 비밀번호의 기본값으로 설정된다고 보면 됨...
            if (passwordPreferences.getString("password", "000").equals(passwordFromUser)){

                //5-4
                //만약 저장되어있는 패스워드가 현재 패스워드와 같을 때
                //패스워드 성공
                //다음 페이지인 다이어리 페이지로 이동
                //인텐트를 통해 넘기기...
                startActivity(Intent(this,DiaryActivity::class.java))

                //넘버피커 값 초기화
                numPicker1.value = 0
                numPicker2.value = 0
                numPicker3.value = 0

            }else{
                //5-5
                //패스워드 실패
                //실패 다이얼로그 생성
                showErrorDialog()
            }
        }//btnOpen

        //6
        //비밀번호 변경버튼 정의
        //비밀번호 변경할 때 다른 동작을 하지 못 하도록 예외처리하기...
        //false를 가지고 있는 전역변수를 생성하여 사용함
        btnChangePW.setOnClickListener {

            //6-2
            //SharedPreference를 사용해 password라는 파일에 저장되어 있는 값 접근하는 변수생성
            val passwordPreferences = getSharedPreferences("password", Context.MODE_PRIVATE)
            //6-2-1
            //현재 피커에 설정한 번호를 변수생성하여 문자열로 저장
            val passwordFromUser = "${numPicker1.value}${numPicker2.value}${numPicker3.value}"

            //6-3
            //만약 비밀번호 변경모드라면 현재 넘버피커에 설정한 번호를
            //SharedPreference를 사용해 password라는 파일을 읽어와 .edit하여 값 변경
            if (changePWMode){

                //6-3-1
                //파일에 저장되어 있는 값을 읽어와 변경하기
                //자바에서는 .edit()함수를 이용해 .putString()형식으로 변경했으나
                //코틀린 에서는 edit함수가 이미 정의 되어 있어 쉽게 사용할 수 있음...
                //정의되어있는 edit함수를 람다 형식으로 사용하기...
                passwordPreferences.edit{

                    //6-3-2
                    //저장파일에 문자열로 저장된 번호 덮어쓰기
                    putString("password", passwordFromUser)
                    //6-3-3
                    //저장하는 값이 큰 경우에
                    //모든 작업이 끝난 후 저장하고 UI스레드를 사용하는 commit()사용하면 화면이 멈출 수 있으므로
                    //apply()를 하여 비동기적으로 바로 처리함
                    //현재 이 앱은 저장하는 값이 크지 않으므로 commit()처리를 하였음...
                    commit()
                }

                //6-3-4
                //파일의 비밀번호 변경 후 비밀번호 변경모드 다시 false로 하고
                //빨강색으로 활성화 되어있던 변경 버튼의 색상을 되돌려 놓기
                changePWMode = false
                btnChangePW.setBackgroundColor(getColor(R.color.custom_black))
                AlertDialog.Builder(this)
                    .setMessage("비밀번호가 변경되었습니다.")
                    .setNegativeButton("확인"){_,_ -> }
                    .create().show()

                //넘버피커 값 초기화
                numPicker1.value = 0
                numPicker2.value = 0
                numPicker3.value = 0

                //6-3-1 ***
                //위 passwordPreferences.edit{}코드를 더 편리하게 사용하는 방법
                //commit()의 실행 명령을 할 때... 꼭 명령을 해줘야 수정이 되는데
                //작성하다보면 이 부분을 놓치는 경우가 있음... 그래서
                //코틀린 에서는 실행 명령어를 까먹어도 이미 적용되어 있는
                //명령어를 사용할 수 있게 메서드를 정의해 두었음...
//                passwordPreferences.edit(true){
//                    val passwordFromUser = "${numPicker1.value}${numPicker2.value}${numPicker3.value}"
//                    putString("password", passwordFromUser)
//                }

            }else{
                //6-4
                //만약 비밀번호 변경모드가 활성화 되어 있지 않다면
                //활성화 될 때(::) 비밀번호가 저장되어있는 비밀번호와 맞는지 체크하여 활성화 하기...

                //6-4-1
                //저장되어있는 값과 현재 값 비교하여 실행
                if (passwordPreferences.getString("password", "000").equals(passwordFromUser)){

                    //6-4-2
                    //비밀번호가 일치 한다면 비밀번호 모드 true로 변경하고
                    //패스워드 변경모드가 활성화 되었음을 알려주는 토스트 생성
                    changePWMode = true
                    Toast.makeText(this, "변경할 비밀번호를 입력하세요.", Toast.LENGTH_SHORT).show()

                    //6-4-3
                    //변경버튼이 활성화되었으므로 버튼 백그라운드 색상을 빨간색으로 변경
                    btnChangePW.setBackgroundColor(Color.RED)

                }else{
                    //6-4-4
                    //비밀번호가 일치하지 않다면
                    //실패 다이얼로그 생성
                    showErrorDialog()
                }

            }

        }//btnChangePW

    }//onCreate

    //7
    //비밀번호가 일치하지 않을 때 생성되는 다이얼로그
    //오픈버튼과 비밀번호 변경 버튼을 눌렀을 때
    //저장되어있는 번호와 현재 넘버피커에 설정한 번호를 비교하여
    //일치하지 않았을 경우 같은 메시지를 보여줌
    //중복되는 코드를 최소화 하기 위해 메서드를 생성하여
    //각 버튼에 적용!
    fun showErrorDialog(){

        //7-1
        //Builder()를 하여 값을 세팅할 수 있음
        //실패창의 제목과 메시지 설정
        //닫기(PositiveButton)버튼 설정,
        //포지티브버튼은 두 개의 파라미터를 받기 때문에
        //람다로 두개의 파라미터와 내용 생략하기... (코드 Cmd + 클릭하여 상세보기 가능)
        //항상 .create().show()를 하여 다이얼로그 생성하기...
        AlertDialog.Builder(this)
            .setTitle("⚠️ Error")
            .setMessage("비밀번호가 일치하지 않습니다.")
            .setPositiveButton("확인"){ _, _ ->}
            .create().show()

    }//showErrorDialog()

}//MainActivity