package com.example.photoquestv3.Fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import com.example.photoquestv3.Views.HomeActivity
import com.example.photoquestv3.databinding.FragmentSettingsBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase


class SettingsFragment : Fragment() {
    lateinit var binding: FragmentSettingsBinding
    lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //  binding =FragmentSettingsBinding.inflate(layoutInflater)
        binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
        // Inflate the layout for this fragment
        // return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth = Firebase.auth
        binding.deleteAccount.setOnClickListener {
            showPopup()
        }

        binding.buttonLogout.setOnClickListener{
            auth.signOut()  //changed places of those two, otherwise sees HomeActivity that user is signed in
            returnHomeActivity()
        }
    }

    private fun showPopup() {
        val builder = context?.let { AlertDialog.Builder(it) }
        builder!!.setTitle("Yo!")
            .setMessage("Do you want to delete account?")
            .setPositiveButton("Yes") { dialog, which ->
                deleteComments()
                deletePosts()
                deleteAccountFromFirestore()
                deleteAccount()
                auth.signOut()  //changed places of those two, otherwise sees HomeActivity that user is signed in
                returnHomeActivity()
            }
            .setNegativeButton("No") { dialog, which ->
                dialog.dismiss()
            }
        val alertDialog: AlertDialog = builder.create()
        alertDialog.show()
    }

    private fun deleteAccount() {

        auth.currentUser?.delete()
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("!!!", "User deleted from auth")
                } else {
                    Log.d("!!!", "User NOT deleted from auth")
                }
            }
    }

    private fun deleteComments() {
        val userId = auth.currentUser!!.uid
        val db = FirebaseFirestore.getInstance()
        db.collection("comments")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val documentUserId = document.get("userId")
                    if (documentUserId == userId) {
                        db.collection("comments").document(document.id).delete()
                        Log.d("!!!!", "comments deleted from collection")
                    } else {
//else
                    }
                }
            }
    }

    private fun deletePosts() {
        val userId = auth.currentUser!!.uid
        val db = FirebaseFirestore.getInstance()
        db.collection("posts")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val documentUserId = document.get("userid")
                    if (documentUserId == userId) {
                        db.collection("posts").document(document.id).delete()
                        Log.d("!!!!", "Posts deleted from collection")
                    } else {
//else
                    }
                }
            }
    }

    private fun deleteAccountFromFirestore() {
        val userId = auth.currentUser?.uid
        val db = FirebaseFirestore.getInstance()
        val usersRef = db.collection("users")
        usersRef.whereEqualTo("uid", userId)
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    usersRef.document(document.id).delete()
                        .addOnSuccessListener {
                            Log.d("!!!!", "Användare raderad från kollektionen")
                        }
                        .addOnFailureListener { e ->
                            Log.w("!!!!", "Misslyckades med att radera användaren", e)
                        }
                }
            }
            .addOnFailureListener { exception ->
                Log.w("!!!!", "Misslyckades med att hämta användardokument", exception)
            }
    }


    private fun returnHomeActivity() {
        val intent = Intent(requireContext(), HomeActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        requireActivity().finish()
    }

}
