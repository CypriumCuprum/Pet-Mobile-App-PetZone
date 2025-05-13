package com.example.petapp.view.shop

import android.graphics.Rect
import android.media.Image
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.petapp.R
import com.example.petapp.data.model.ItemEntity
import com.example.petapp.viewmodel.shop.ItemAdapter
import com.example.petapp.viewmodel.shop.ItemTypeAdapter
import com.example.petapp.viewmodel.shop.ItemTypeViewModel
import com.example.petapp.viewmodel.statistic.StatisticTypeViewModel

class Shop : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var viewModel: ItemTypeViewModel
    private lateinit var adapter: ItemTypeAdapter
    private lateinit var itemAdapter: ItemAdapter
    private lateinit var recyclerItemView: RecyclerView
    private lateinit var allItems: List<ItemEntity>
    private lateinit var loadingIndicator: ProgressBar
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout

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
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                // Không cần xử lý nếu không submit tìm kiếm
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                itemAdapter.filter(newText ?: "")
                return true
            }
        })

        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout)
        swipeRefreshLayout.setOnRefreshListener {
            reloadFragment()
            swipeRefreshLayout.isRefreshing = false
        }

        loadingIndicator = view.findViewById(R.id.loadingIndicator)
        loadingIndicator.visibility = View.VISIBLE

        val cartIcon  = requireActivity().findViewById<ImageView>(R.id.right_icon_toolbar)
        val toolbarTitle = requireActivity().findViewById<TextView>(R.id.toolbar_title)
        cartIcon.setImageResource(R.drawable.cart)
        toolbarTitle.setText(R.string.shop)
        cartIcon.setOnClickListener {
            val cartFragment = Cart() // thay bằng tên Fragment giỏ hàng thật sự của bạn
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.frame_container, cartFragment) // nhớ thay `frame_container` bằng ID layout chứa Fragment
                .addToBackStack(null)
                .commit()
        }


        recyclerView = view.findViewById(R.id.recyclerShopView)
        recyclerView.layoutManager = LinearLayoutManager(view.context, LinearLayoutManager.HORIZONTAL, false)
        recyclerView.addItemDecoration(EqualSpacingItemDecoration(16))
        val snapHelper = LinearSnapHelper()
        snapHelper.attachToRecyclerView(recyclerView)

        // --- RecyclerView hiển thị danh sách sản phẩm ---
        recyclerItemView = view.findViewById(R.id.recyclerItemView)
//        recyclerItemView.layoutManager = GridLayoutManager(requireContext(), 2)
        recyclerItemView.layoutManager = LinearLayoutManager(view.context)
        itemAdapter = ItemAdapter(emptyList()){ item ->
            navigateToProductDetail(item)
        } // ban đầu chưa có dữ liệu
        recyclerItemView.adapter = itemAdapter

        viewModel = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory(requireActivity().application)
        )[ItemTypeViewModel::class.java]

        adapter = ItemTypeAdapter(emptyList()) { selectedType ->
//            val filteredItems = allItems.filter { it.id == selectedType.id }
            itemAdapter.updateData(selectedType.listItem)
        }
        recyclerView.adapter = adapter

        viewModel.itemTypes.observe(viewLifecycleOwner) {
            loadingIndicator.visibility = View.GONE
            swipeRefreshLayout.isRefreshing = false // Ngừng xoay tròn
            adapter.updateData(it)

        }

        viewModel.fetchItemTypes()

    }

    inner class EqualSpacingItemDecoration(private val space: Int) : RecyclerView.ItemDecoration() {
        override fun getItemOffsets(
            outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State
        ) {
            outRect.left = space
            outRect.right = space
            outRect.bottom = space
            outRect.top = space
        }
    }


    private fun navigateToProductDetail(item: ItemEntity) {
        // Tạo instance của ProductDetailFragment
        val productDetailFragment = ItemDetail.newInstance(item)

        // Thực hiện transaction để thay thế fragment hiện tại bằng fragment chi tiết
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.frame_container, productDetailFragment) // Thay fragment_container bằng ID container của bạn
            .addToBackStack(null) // Thêm vào back stack để có thể quay lại
            .commit()
    }

    private fun reloadFragment() {

    }
}

