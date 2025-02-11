package com.example.photoquestv3.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.photoquestv3.Adapter.PostAdapter
import com.example.photoquestv3.Models.Post
import com.example.photoquestv3.R
import com.example.photoquestv3.databinding.FragmentHomeBinding


class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val postList = Post.mockData()
        val adapter = PostAdapter(postList)
        binding.recycleContainer.layoutManager = LinearLayoutManager(requireContext())
        binding.recycleContainer.adapter = adapter

    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}