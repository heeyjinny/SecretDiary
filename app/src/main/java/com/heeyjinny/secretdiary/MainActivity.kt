package com.heeyjinny.secretdiary

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.heeyjinny.secretdiary.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    //뷰바인딩
    val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
    }
}