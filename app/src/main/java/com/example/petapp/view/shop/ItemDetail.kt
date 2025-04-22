package com.example.petapp.view.shop

import android.content.ClipData.Item
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.petapp.BuildConfig
import com.example.petapp.R
import com.example.petapp.data.model.ItemEntity
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.google.android.material.chip.Chip
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.text.NumberFormat
import java.util.Locale

class ItemDetail : Fragment() {

    private lateinit var item: ItemEntity

    // Views
    private lateinit var imgProductDetail: ImageView
    private lateinit var tvProductName: TextView
    private lateinit var chipManufacturerDetail: Chip
    private lateinit var tvPriceDetail: TextView
    private lateinit var tvQuantityDetail: TextView
    private lateinit var tvDescriptionDetail: TextView
    private lateinit var btnAddToCart: MaterialButton
    private lateinit var fabFavorite: FloatingActionButton
    private lateinit var toolbar: RelativeLayout
    private lateinit var toolbarTitle: TextView
    private lateinit var btnBack: ImageView
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_item_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Lấy dữ liệu từ arguments truyền thống
        arguments?.let {
            item = it.getParcelable(ARG_PRODUCT_ITEM) ?: return
        }

        initViews(view)
        setupToolbar()
        populateData()
        setupListeners()
    }

    private fun initViews(view: View) {
        imgProductDetail = view.findViewById(R.id.imgProductDetail)
        tvProductName = view.findViewById(R.id.tvProductName)
        chipManufacturerDetail = view.findViewById(R.id.chipManufacturerDetail)
        tvPriceDetail = view.findViewById(R.id.tvPriceDetail)
        tvQuantityDetail = view.findViewById(R.id.tvQuantityDetail)
        tvDescriptionDetail = view.findViewById(R.id.tvDescriptionDetail)
        btnAddToCart = view.findViewById(R.id.btnAddToCart)
        fabFavorite = view.findViewById(R.id.fabFavorite)
        toolbar = requireActivity().findViewById(R.id.toolbar)
        toolbarTitle = requireActivity().findViewById(R.id.toolbar_title)
        btnBack = requireActivity().findViewById(R.id.left_icon_toolbar)
    }

    private fun setupToolbar() {
        toolbarTitle.text = item.name
        btnBack.setOnClickListener{
            // Quay lại fragment trước đó
            requireActivity().supportFragmentManager.popBackStack()
        }
    }

    private fun populateData() {
        // Hiển thị thông tin chi tiết sản phẩm
        tvProductName.text = item.name
        chipManufacturerDetail.text = item.manufacturer

        // Format giá tiền
        val formatter = NumberFormat.getNumberInstance(Locale.US)
        tvPriceDetail.text = getString(R.string.currency) + formatter.format(item.price)

        tvQuantityDetail.text = item.quantity.toString()
        tvDescriptionDetail.text = item.description

        // Tải ảnh sản phẩm
        Glide.with(requireContext())
            .load("${BuildConfig.SERVER_URL}${item.image_url}")
            // Có thể thêm placeholder và error image nếu cần
            .into(imgProductDetail)
    }

    private fun setupListeners() {
        // Xử lý sự kiện khi người dùng nhấn thêm vào giỏ hàng
        btnAddToCart.setOnClickListener {
            // TODO: Thêm sản phẩm vào giỏ hàng
            Toast.makeText(context, "${item.name} has been added to your cart", Toast.LENGTH_SHORT).show()
        }
    }

    companion object {
        private const val ARG_PRODUCT_ITEM = "product_item"

        fun newInstance(item: ItemEntity): ItemDetail {
            val fragment = ItemDetail()
            val args = Bundle()
            args.putParcelable(ARG_PRODUCT_ITEM, item)
            fragment.arguments = args
            return fragment
        }
    }
}