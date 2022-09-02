package com.sunayanpradhan.cloudynotes.Activities

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.core.widget.NestedScrollView
import androidx.lifecycle.lifecycleScope
import com.airbnb.lottie.LottieAnimationView
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.sunayanpradhan.cloudynotes.Connections.ConnectivityObserver
import com.sunayanpradhan.cloudynotes.R
import com.sunayanpradhan.deverse.Utils.NetworkConnectivityObserver
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class MainActivity : AppCompatActivity() {


    lateinit var mainLayout: NestedScrollView

    lateinit var noInternet: LottieAnimationView

    private lateinit var connectivityObserver: ConnectivityObserver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        mainLayout=findViewById(R.id.main_layout)

        noInternet=findViewById(R.id.no_internet)


        connectivityObserver = NetworkConnectivityObserver(applicationContext)


        if (isNetworkAvailable(this)){

            mainLayout.visibility= View.VISIBLE

            noInternet.visibility= View.GONE

        }

        else{


            mainLayout.visibility= View.GONE

            noInternet.visibility= View.VISIBLE

        }


        connectivityObserver.observe().onEach {

            if (it==ConnectivityObserver.Status.Available){

                mainLayout.visibility= View.VISIBLE

                noInternet.visibility= View.GONE

            }

            else{


                mainLayout.visibility= View.GONE

                noInternet.visibility= View.VISIBLE


            }


        }.launchIn(lifecycleScope)






        checkUser()



    }



    private fun checkUser(){

        val firebaseUser=FirebaseAuth.getInstance().currentUser


        if (firebaseUser!=null && firebaseUser.isEmailVerified){

            startActivity(Intent(this, NoteActivity::class.java))

            overridePendingTransition(0,0)

            finish()
        }

        else{

            try {
                val googleSignInOptions= GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken("1077959185777-edbl86nrvdlt8un69mqhicjc64qake21.apps.googleusercontent.com")
                    .requestEmail()
                    .build()



                val googleSignInClient: GoogleSignInClient = GoogleSignIn.getClient(this,googleSignInOptions)

                Auth.GoogleSignInApi.signOut(googleSignInClient.asGoogleApiClient())

            }
            catch (e:Exception){


            }


            FirebaseAuth.getInstance().signOut()

        }

    }


    fun isNetworkAvailable(context: Context?): Boolean {
        if (context == null) return false
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
            if (capabilities != null) {
                when {
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> {
                        return true
                    }
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> {
                        return true
                    }
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> {
                        return true
                    }
                }
            }
        } else {
            val activeNetworkInfo = connectivityManager.activeNetworkInfo
            if (activeNetworkInfo != null && activeNetworkInfo.isConnected) {
                return true
            }
        }
        return false
    }


    override fun onBackPressed() {

        if (noInternet.isVisible){

            finishAffinity()

        }


        super.onBackPressed()
    }




}