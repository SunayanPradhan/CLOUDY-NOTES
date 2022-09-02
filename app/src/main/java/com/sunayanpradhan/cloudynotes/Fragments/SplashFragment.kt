package com.sunayanpradhan.cloudynotes.Fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.TextView
import android.widget.Toast
import androidx.core.widget.NestedScrollView
import androidx.navigation.Navigation
import com.airbnb.lottie.LottieAnimationView
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.FirebaseDatabase
import com.sunayanpradhan.cloudynotes.Activities.NoteActivity
import com.sunayanpradhan.cloudynotes.Connections.ConnectivityObserver
import com.sunayanpradhan.cloudynotes.Models.InformationModel
import com.sunayanpradhan.cloudynotes.R


class SplashFragment : Fragment() {

    lateinit var googleSignIn: Button
    lateinit var googleSignInClient: GoogleSignInClient
    lateinit var firebaseAuth: FirebaseAuth
    private val RC_SIGN_IN=100

    lateinit var mainLayout: NestedScrollView

    lateinit var noInternet: LottieAnimationView

    lateinit var signIn:Button

    lateinit var checkBox: CheckBox

    lateinit var termsConditions:TextView




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_splash, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        googleSignIn=view.findViewById(R.id.google_sign_in)

        mainLayout=view.findViewById(R.id.main_layout)

        noInternet=view.findViewById(R.id.no_internet)

        signIn=view.findViewById(R.id.sign_in)

        checkBox=view.findViewById(R.id.checkbox)

        termsConditions=view.findViewById(R.id.terms_conditions)


        firebaseAuth= FirebaseAuth.getInstance()


        checkUser()

        val googleSignInOptions= GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("888079073965-gvd0guh64imme79p9fimpagms0pi6anp.apps.googleusercontent.com")
            .requestEmail()
            .build()


        googleSignInClient= GoogleSignIn.getClient(requireActivity(),googleSignInOptions)

        termsConditions.setOnClickListener {

            val openUrlIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://iamtechnosp.blogspot.com/2022/07/cloudy-notes-terms-conditions.html"))
            startActivity(openUrlIntent)

        }


        googleSignIn.setOnClickListener {

            if (checkBox.isChecked) {

                val intent = googleSignInClient.signInIntent
                startActivityForResult(intent, RC_SIGN_IN)
            }


            else{
            Toast.makeText(requireContext(), "Accept Terms & Conditions First", Toast.LENGTH_SHORT).show()
        }

        }


        signIn.setOnClickListener {

            if (checkBox.isChecked) {


                Navigation.findNavController(view)
                    .navigate(R.id.action_splashFragment_to_signInFragment)

            }

            else{
                Toast.makeText(requireContext(), "Accept Terms & Conditions First", Toast.LENGTH_SHORT).show()
            }


        }


    }


    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {

            RC_SIGN_IN -> {
                val accountTask = GoogleSignIn.getSignedInAccountFromIntent(data)
                try {
                    val account=accountTask.getResult(ApiException::class.java)
                    firebaseAuthWithGoogleAccount(account)
                }
                catch (e:Exception){

                    Toast.makeText(requireContext(),"SignIn Unsuccessful", Toast.LENGTH_SHORT).show()

                }

            }
        }
    }

    private fun firebaseAuthWithGoogleAccount(account: GoogleSignInAccount?) {

        val credential = GoogleAuthProvider.getCredential(account!!.idToken, null)



        firebaseAuth.signInWithCredential(credential)
            .addOnSuccessListener {




                val firebaseUser = firebaseAuth.currentUser


                if (it.additionalUserInfo!!.isNewUser){

                    var user = InformationModel(
                        firebaseUser?.displayName.toString(),
                        firebaseUser?.email.toString(),
                        "",
                        firebaseUser?.photoUrl.toString(),
                        firebaseUser?.uid.toString()
                    )

                    if (firebaseUser != null) {
                        FirebaseDatabase.getInstance().reference.child("Users").child(firebaseUser.uid)
                            .setValue(user)

                        Toast.makeText(
                            requireContext(),
                            "Registration Successful",
                            Toast.LENGTH_SHORT
                        ).show()


                    }

                    else{

                        Toast.makeText(requireContext(), "User is not found", Toast.LENGTH_SHORT).show()

                    }



                }

                else{

                    Toast.makeText(requireContext(), "Logged in", Toast.LENGTH_SHORT).show()

                }


                requireActivity().startActivity(Intent(requireContext(),NoteActivity::class.java))

                requireActivity().overridePendingTransition(0,0)

                requireActivity().finish()





            }
            .addOnFailureListener {

                Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
            }



    }

    private fun checkUser(){

        val firebaseUser=firebaseAuth.currentUser


        if (firebaseUser!=null && firebaseUser.isEmailVerified){




            startActivity(Intent(requireActivity(), NoteActivity::class.java))

            activity?.overridePendingTransition(0,0)

            activity?.finish()
        }

        else{

            try {
                val googleSignInOptions= GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken("1077959185777-edbl86nrvdlt8un69mqhicjc64qake21.apps.googleusercontent.com")
                    .requestEmail()
                    .build()



                val googleSignInClient: GoogleSignInClient = GoogleSignIn.getClient(requireActivity(),googleSignInOptions)

                Auth.GoogleSignInApi.signOut(googleSignInClient.asGoogleApiClient())

            }
            catch (e:Exception){


            }


            firebaseAuth.signOut()

        }

    }




}