package com.sunayanpradhan.cloudynotes.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.Navigation
import com.google.firebase.auth.FirebaseAuth
import com.sunayanpradhan.cloudynotes.R
import com.sunayanpradhan.cloudynotes.databinding.FragmentForgetPasswordBinding


class ForgetPasswordFragment : Fragment() {

    lateinit var binding: FragmentForgetPasswordBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_forget_password, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding=FragmentForgetPasswordBinding.bind(view)

        binding.gobacktologin.setOnClickListener {

            Navigation.findNavController(view).navigate(R.id.action_forgetPasswordFragment_to_signInFragment)

        }


        binding.passwordrecover.setOnClickListener {

            binding.progressbar.visibility=View.VISIBLE

            val mail: String = binding.forgotpasswordtext.text.toString().trim()
            if (mail.isEmpty()) {
                Toast.makeText(requireContext(), "Enter your mail first", Toast.LENGTH_SHORT)
                    .show()

                binding.progressbar.visibility=View.INVISIBLE

            } else {
                FirebaseAuth.getInstance().sendPasswordResetEmail(mail).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(requireContext(),
                            "Mail Sent , You can recover your password using mail",
                            Toast.LENGTH_SHORT).show()

                        Navigation.findNavController(view).navigate(R.id.action_forgetPasswordFragment_to_signInFragment)

                        binding.progressbar.visibility=View.INVISIBLE


                    }

                    else {
                        Toast.makeText(requireContext(),
                            "Email is Wrong or Account Not Exits",
                            Toast.LENGTH_SHORT).show()

                        binding.progressbar.visibility=View.INVISIBLE
                    }
                }
            }
        }


    }

}