package com.example.petapp.view.shop

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.petapp.R
import com.example.petapp.viewmodel.shop.ItemTypeAdapter
import com.example.petapp.viewmodel.shop.ItemTypeViewModel
import com.example.petapp.viewmodel.statistic.StatisticTypeViewModel

class Shop : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var viewModel: ItemTypeViewModel
    private lateinit var adapter: ItemTypeAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_shop, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val searchView = view.findViewById<SearchView>(R.id.searchView)
        recyclerView = view.findViewById(R.id.recyclerShopView)
        recyclerView.layoutManager = LinearLayoutManager(view.context, LinearLayoutManager.HORIZONTAL, false)
        val snapHelper = LinearSnapHelper()
        snapHelper.attachToRecyclerView(recyclerView)

        viewModel = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory(requireActivity().application)
        )[ItemTypeViewModel::class.java]

        adapter = ItemTypeAdapter(emptyList())
        recyclerView.adapter = adapter

        viewModel.itemTypes.observe(viewLifecycleOwner) {
            adapter.updateData(it)
        }

        viewModel.fetchItemTypes()

    }
}