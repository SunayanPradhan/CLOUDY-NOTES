package com.sunayanpradhan.cloudynotes.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.Navigation
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import com.sunayanpradhan.cloudynotes.Models.InformationModel
import com.sunayanpradhan.cloudynotes.R
import com.sunayanpradhan.cloudynotes.databinding.FragmentSignUpBinding


class SignUpFragment : Fragment() {

    lateinit var binding: FragmentSignUpBinding

    lateinit var firebaseAuth: FirebaseAuth
    lateinit var firebaseDatabase: FirebaseDatabase


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_sign_up, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        binding=FragmentSignUpBinding.bind(view)

        firebaseAuth=FirebaseAuth.getInstance()

        firebaseDatabase=FirebaseDatabase.getInstance()

        binding.gotologin.setOnClickListener {

            Navigation.findNavController(view).navigate(R.id.action_signUpFragment_to_signInFragment)

        }

        binding.signup.setOnClickListener {

            val mail = binding.signupemail.text.toString().trim()
            val password = binding.signuppassword.text.toString().trim()
            val repassword = binding.signuprepassword.text.toString().trim()



            if (mail.isEmpty() || password.isEmpty() || repassword.isEmpty()) {
                Toast.makeText(requireContext(), "All Fields are Required", Toast.LENGTH_SHORT)
                    .show()
            } else if (password.length < 7) {


                Toast.makeText(requireContext(), "Password Should at least 8", Toast.LENGTH_SHORT)
                    .show()
            } else if (password != repassword) {

                Toast.makeText(requireContext(), "Password Does Not Match", Toast.LENGTH_SHORT)
                    .show()

            } else {

                binding.progressbar.visibility= View.VISIBLE

                firebaseAuth.createUserWithEmailAndPassword(mail, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {

                            val firebaseUser: FirebaseUser? = firebaseAuth.currentUser

                            val user = InformationModel(binding.signupname.text.toString(),
                                binding.signupemail.text.toString(),
                                binding.signuppassword.text.toString(),
                                "https://res.cloudinary.com/sunayan02/image/upload/v1659941770/suer_myphid.jpg",
                                firebaseUser?.uid.toString()
                            )

                            if (firebaseUser != null) {
                                firebaseDatabase.reference.child("Users").child(firebaseUser.uid)
                                    .setValue(user)
                            }

                            Toast.makeText(requireContext(),
                                "Registration Successful",
                                Toast.LENGTH_SHORT).show()
                            sendEmailVerification()




                        } else {
                            Toast.makeText(requireContext(),
                                "Failed To Register",
                                Toast.LENGTH_SHORT).show()
                            binding.progressbar.visibility=View.INVISIBLE
                        }

                    }

            }


        }



    }


    private fun sendEmailVerification() {

        var firebaseUser: FirebaseUser? =firebaseAuth.currentUser
        if (firebaseAuth!=null){

            binding.progressbar.visibility=View.INVISIBLE

            firebaseUser?.sendEmailVerification()?.addOnSuccessListener {


                Toast.makeText(
                    requireContext(), "Verification Email is Send \n Verify and Log In Again", Toast.LENGTH_SHORT).show()

                Navigation.findNavController(requireView()).navigate(R.id.action_signUpFragment_to_signInFragment)



            }
        }
        else{

            Toast.makeText(requireContext(), "Failed To Send Verification", Toast.LENGTH_SHORT).show()

        }
    }

}