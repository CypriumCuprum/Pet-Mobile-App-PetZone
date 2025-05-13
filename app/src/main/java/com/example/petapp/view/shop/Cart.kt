package com.example.petapp.view.shop

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.petapp.R
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.petapp.data.model.CartItemEntity
import com.example.petapp.viewmodel.shop.CartAdapter

import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.ViewModelProvider
import com.example.petapp.data.model.CartItemResponse
import com.example.petapp.data.model.ItemEntity
import com.example.petapp.data.model.OrderCreate
import com.example.petapp.data.model.OrderItemCreate

import com.example.petapp.viewmodel.shop.CartItemViewModel
import com.example.petapp.viewmodel.shop.ItemTypeViewModel
import java.util.UUID

class Cart : Fragment() {

    private lateinit var cartViewModel: CartItemViewModel
    private lateinit var cartAdapter: CartAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var tvSubtotal: TextView
    private lateinit var tvShippingFee: TextView
    private lateinit var tvTotal: TextView
    private val selectedItems = mutableListOf<CartItemEntity>()
    private lateinit var layoutCheckbox: ConstraintLayout
    private lateinit var btnDelete: Button
    private lateinit var btnOrder: Button
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_cart, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val cartIcon  = requireActivity().findViewById<ImageView>(R.id.right_icon_toolbar)
        val toolbarTitle = requireActivity().findViewById<TextView>(R.id.toolbar_title)
        cartIcon.setImageResource(R.drawable.shop)
        toolbarTitle.setText(R.string.cart)
        cartIcon.setOnClickListener {
            val shopFragment = Shop() // thay bằng tên Fragment giỏ hàng thật sự của bạn
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.frame_container, shopFragment) // nhớ thay `frame_container` bằng ID layout chứa Fragment
                .addToBackStack(null)
                .commit()
        }

        cartViewModel = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory(requireActivity().application)
        )[CartItemViewModel::class.java]

        recyclerView = view.findViewById(R.id.rv_cart_items)
        progressBar = view.findViewById(R.id.progress_bar)
        tvSubtotal = view.findViewById(R.id.tv_subtotal)
        tvShippingFee = view.findViewById(R.id.tv_shipping_fee)
        tvTotal = view.findViewById(R.id.tv_total)
        layoutCheckbox = view.findViewById(R.id.select_layout_cart_item)
        layoutCheckbox.visibility = View.GONE
        btnOrder = view.findViewById(R.id.btn_checkout)
        btnDelete = view.findViewById(R.id.btn_delete_all)
        cartAdapter = CartAdapter(
            onIncreaseQuantity = { cartViewModel.increaseQuantity(it) },
            onDecreaseQuantity = { cartViewModel.decreaseQuantity(it) },
            onItemClick = {item -> navigateToProductDetail(item)},
            onItemSelectedChanged = { cartItem, isSelected ->
                // Track selected items
                if (isSelected) {
                    selectedItems.add(cartItem)
                } else {
                    selectedItems.remove(cartItem)
                }

                // Update UI or perform actions based on selection
                updateActionButtonsState()
            }
        )

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = cartAdapter

        observeViewModel()
        btnListener()
        progressBar.visibility = View.VISIBLE
        cartViewModel.loadUserCart(UUID.fromString("3d8f1550-0abb-11f0-992e-0250e6cba39f"))
    }

    private fun btnListener(){
        btnDelete.setOnClickListener(){
            val selectedCopy = selectedItems.toList()
            selectedCopy.forEach({item ->
                cartViewModel.deleteCartItem(item.id)
                selectedItems.removeIf{it.id == item.id }
            })
            cartAdapter.removeSelectedItems()
            layoutCheckbox.visibility = View.GONE
            Toast.makeText(context, "The item has been successfully deleted.", Toast.LENGTH_SHORT).show()

        }

        btnOrder.setOnClickListener(){
            val userId = UUID.fromString("3d8f1550-0abb-11f0-992e-0250e6cba39f")
            val selectedCopy = selectedItems.toList()
            val listItem = mutableListOf<OrderItemCreate>()
            selectedCopy.forEach({item ->
                val cartItemResponse = CartItemResponse(item.id, item.quantity)
                val orderItemCreate = OrderItemCreate(cartItemResponse)
                listItem.add(orderItemCreate)
            })
            val orderCreate = OrderCreate(userId, listItem)
            cartViewModel.createOrder(orderCreate) { success, message ->
                if(success) {
                    selectedCopy.forEach({ item ->
                        cartViewModel.removeCartItem(item.id)
                        selectedItems.removeIf { it.id == item.id }
                    })
                    cartAdapter.removeSelectedItems()
                    layoutCheckbox.visibility = View.GONE
                }
                Toast.makeText(
                    context,
                    message,
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun observeViewModel() {
        cartViewModel.cartData.observe(viewLifecycleOwner) { cart ->
            cartAdapter.updateItems(cart.listItem)
            val subtotal = cart.listItem.sumOf { (it.quantity * it.item.price).toDouble() }
            val shipping = 15.0
            val total = subtotal + shipping

            tvSubtotal.text = "$ %.2f".format(subtotal)
            tvShippingFee.text = "$ %.2f".format(shipping)
            tvTotal.text = "$ %.2f".format(total)
        }

        cartViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        cartViewModel.errorMessage.observe(viewLifecycleOwner) { errorMsg ->
            if (!errorMsg.isNullOrEmpty()) {
                Toast.makeText(requireContext(), errorMsg, Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun updateActionButtonsState() {
        // Enable/disable buttons based on selected items
        val hasSelectedItems = selectedItems.isNotEmpty()
        if(hasSelectedItems) {
            layoutCheckbox.visibility = View.VISIBLE
        } else {
            layoutCheckbox.visibility = View.GONE
        }
        val subtotal = selectedItems.sumOf { (it.quantity * it.item.price).toDouble() }
        val shipping = 15.0
        val total = subtotal + shipping

        tvSubtotal.text = "$ %.2f".format(subtotal)
        tvShippingFee.text = "$ %.2f".format(shipping)
        tvTotal.text = "$ %.2f".format(total)
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
}

