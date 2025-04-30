package com.example.faunafinder

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.faunafinder.navigation.AppNavigation
import com.example.faunafinder.ui.theme.FaunaFInderTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FaunaFInderTheme {
                AppNavigation()
            }
        }
    }
}