package com.sunayanpradhan.cloudynotes.Fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.Navigation
import com.google.firebase.auth.FirebaseAuth
import com.sunayanpradhan.cloudynotes.Activities.NoteActivity
import com.sunayanpradhan.cloudynotes.R
import com.sunayanpradhan.cloudynotes.databinding.FragmentSignInBinding


class SignInFragment : Fragment() {

    lateinit var binding: FragmentSignInBinding

    lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_sign_in, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding=FragmentSignInBinding.bind(view)

        firebaseAuth= FirebaseAuth.getInstance()


        binding.gotosignup.setOnClickListener {

            Navigation.findNavController(view).navigate(R.id.action_signInFragment_to_signUpFragment)

        }

        binding.gotoforgotpassword.setOnClickListener {

            Navigation.findNavController(view).navigate(R.id.action_signInFragment_to_forgetPasswordFragment)

        }

        binding.login.setOnClickListener {
            val mail: String = binding.loginemail.text.toString().trim()
            val password: String = binding.loginpassword.text.toString().trim()
            if (mail.isEmpty() || password.isEmpty()) {
                Toast.makeText(requireContext(), "All Field Are Required", Toast.LENGTH_SHORT)
                    .show()
            } else {


                binding.progressbar.visibility= View.VISIBLE

                firebaseAuth.signInWithEmailAndPassword(mail, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {


                            checkMailVerification()
                        } else {
                            Toast.makeText(requireContext(),
                                "Account Doesn't Exist",
                                Toast.LENGTH_SHORT)
                                .show()
                            binding.progressbar.visibility= View.INVISIBLE
                        }
                    }
            }

        }



    }

    private fun checkMailVerification() {


        val firebaseUser = firebaseAuth.currentUser
        if (firebaseUser!!.isEmailVerified) {
            Toast.makeText(requireContext(), "Logged in", Toast.LENGTH_SHORT).show()
            binding.progressbar.visibility=View.INVISIBLE

            val intent = Intent(activity, NoteActivity::class.java)
            startActivity(intent)

            activity?.finish()




        } else {
            Toast.makeText(requireContext(), "Verify your mail first", Toast.LENGTH_SHORT).show()
            binding.progressbar.visibility=View.INVISIBLE
            firebaseAuth.signOut()
        }
    }


}