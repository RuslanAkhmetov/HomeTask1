package com.geekbrain.myapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.geekbrain.myapplication.contentprovider.ContentProvider
import com.geekbrain.myapplication.databinding.ActivityMainBinding
import com.geekbrain.myapplication.log.LogFragment


class MainActivity : AppCompatActivity() {
    private val TAG = "MainActivity"

    private lateinit var  binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        binding = ActivityMainBinding.inflate(layoutInflater)

/*      //Получение токена
        FirebaseMessaging.getInstance().token.addOnCompleteListener(
            OnCompleteListener
            {task->
                if(!task.isSuccessful){
                    return@OnCompleteListener
                }
                val token = task.result
            }
        )*/

        setContentView(binding.root)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, MainFragment.newInstance())
                .commit()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_screen_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            R.id.menu_requestslog -> {
                supportFragmentManager.apply {
                    beginTransaction()
                        .add(R.id.container, LogFragment.newInstance())
                        .addToBackStack("")
                        .commitAllowingStateLoss()
                }
                true
            }
            R.id.menu_content_provider -> {
                supportFragmentManager.apply {
                    beginTransaction()
                        .add(R.id.container, ContentProvider.newInstance())
                        .addToBackStack("")
                        .commitAllowingStateLoss()

                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

}





