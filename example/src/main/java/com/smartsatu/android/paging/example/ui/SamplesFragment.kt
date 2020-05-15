package com.smartsatu.android.paging.example.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.smartsatu.android.paging.example.R
import com.smartsatu.android.paging.example.databinding.FragmentSamplesBinding

class SamplesFragment : Fragment() {

    private lateinit var binding: FragmentSamplesBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentSamplesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val samplesAdapter = SamplesAdapter(object : SamplesAdapter.ItemCallback {
            override fun onItemClick(sample: Sample) {
                findNavController().navigate(sample.actionId)
            }
        }).apply { submitList(buildSamples()) }
        binding.samplesRecycler.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        binding.samplesRecycler.adapter = samplesAdapter
    }

    private fun buildSamples(): List<Sample> {
        return listOf(
                Sample("Users", "Demonstration of basic functions i.e.: load/refresh/clear paged list",
                        R.id.action_samplesFragment_to_usersFragment),
                Sample("Epoxy Users", "Paged list controller by AirBnb",
                        R.id.action_samplesFragment_to_epoxyUsersFragment)
        )
    }
}