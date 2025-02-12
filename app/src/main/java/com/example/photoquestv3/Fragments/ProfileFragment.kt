package com.example.photoquestv3.Views.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.photoquestv3.Adapter.ImageAdapter
import com.example.photoquestv3.R
import com.example.photoquestv3.databinding.FragmentProfileBinding

class ProfileFragment : Fragment() {
    private lateinit var binding : FragmentProfileBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)

        val imageLis = listOf(
            R.drawable.trash,R.drawable.trash,R.drawable.trash
        )

        recyclerView.layoutManager = GridLayoutManager(requireContext(),3)
        recyclerView.adapter = ImageAdapter(imageLis)


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentProfileBinding.inflate(inflater,container,false)
        return binding.root




    }




}