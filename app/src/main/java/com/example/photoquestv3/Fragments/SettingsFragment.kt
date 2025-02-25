package com.example.photoquestv3.Fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import com.example.photoquestv3.Views.HomeActivity
import com.example.photoquestv3.databinding.FragmentSettingsBinding


class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.deleteAccount.setOnClickListener {
            showPopup()
        }

        binding.buttonLogout.setOnClickListener{

        }
    }

    private fun showPopup() {
        val builder = context?.let { AlertDialog.Builder(it) }
        builder!!.setTitle("Yo!")
            .setMessage("Do you want to delete account?")
            .setPositiveButton("Yes") { dialog, which ->
                returnHomeActivity()
            }
            .setNegativeButton("No") { dialog, which ->
                dialog.dismiss()
            }
        val alertDialog: AlertDialog = builder.create()
        alertDialog.show()
    }




    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    private fun returnHomeActivity() {
        val intent = Intent(requireContext(), HomeActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        requireActivity().finish()
    }

}
