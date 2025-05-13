package com.example.petapp.view.shop

import android.content.ClipData.Item
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.petapp.BuildConfig
import com.example.petapp.R
import com.example.petapp.data.model.CartItemCreateEntity
import com.example.petapp.data.model.ItemEntity
import com.example.petapp.viewmodel.shop.CartItemViewModel
import com.example.petapp.viewmodel.shop.ItemTypeViewModel
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.google.android.material.chip.Chip
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.text.NumberFormat
import java.util.Locale
import java.util.UUID

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
    private lateinit var tvQuantity: TextView
    private lateinit var btnIncrease: Button
    private lateinit var btnDecrease: Button
    private lateinit var viewModel: CartItemViewModel
    private var isMessageShown = false

    private var quantity = 1
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
        tvQuantity = view.findViewById(R.id.tvSelectedQuantity)
        btnDecrease = view.findViewById(R.id.btnDecrease)
        btnIncrease = view.findViewById(R.id.btnIncrease)
        viewModel = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory(requireActivity().application)
        )[CartItemViewModel::class.java]
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
        btnIncrease.setOnClickListener{
            if(quantity<tvQuantityDetail.text.toString().toInt()){
                quantity++
                tvQuantity.text = quantity.toString()
            } else if (!isMessageShown) {
                isMessageShown = true
                Toast.makeText(context, "Cannot increase further. Maximum quantity reached!", Toast.LENGTH_SHORT).show()
                Handler(Looper.getMainLooper()).postDelayed({
                    isMessageShown = false
                }, 1000)
            }
        }

        btnDecrease.setOnClickListener{
            if(quantity>1){
                quantity--
                tvQuantity.text = quantity.toString()
            } else if (!isMessageShown) {
                isMessageShown = true
                Toast.makeText(context, "Quantity cannot be less than 1", Toast.LENGTH_SHORT).show()
                Handler(Looper.getMainLooper()).postDelayed({
                    isMessageShown = false
                }, 1000)
            }
        }

        btnAddToCart.setOnClickListener {
            // TODO: Thêm sản phẩm vào giỏ hàng
            val userId = UUID.fromString("3d8f1550-0abb-11f0-992e-0250e6cba39f")
            val quantity = tvQuantity.text
            val itemId = item.id
            val cartItemCreate = CartItemCreateEntity(quantity.toString().toInt(), itemId, userId)
            viewModel.addToCart(cartItemCreate) { success, message ->
                Toast.makeText(
                    context,
                    message,
                    Toast.LENGTH_SHORT
                ).show()
            }
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