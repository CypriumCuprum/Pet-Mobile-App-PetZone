package com.example.petapp.view

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.widget.Button
import android.widget.Toast
import com.example.petapp.R
import com.example.petapp.data.local.AppDatabase
import com.example.petapp.data.model.submodel.PetReduceForHome
import com.example.petapp.data.repository.UserRepository
import com.example.petapp.view.auth.LoginActivity
import com.example.petapp.viewmodel.pet.PetViewModel
import com.example.petapp.viewmodel.user.LoginViewModel
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {
    private lateinit var recyclerViewHorizontal: RecyclerView
    private lateinit var buttonAddPet: Button
    private lateinit var petViewModel: PetViewModel
    private lateinit var petAdapter: YourPetAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        petViewModel = ViewModelProvider(
            this,
            PetViewModel.Factory(requireActivity().application)
        )[PetViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize views
        recyclerViewHorizontal = view.findViewById(R.id.recyclerViewHorizontal)
        //debug log
        println("Initializing RecyclerView: $recyclerViewHorizontal")
        buttonAddPet = view.findViewById(R.id.buttonAddPet)

        setupRecyclerView()
        loadPets()

        // Set up add pet button click listener
        buttonAddPet.setOnClickListener {
            // Navigate to add pet screen - implement navigation to your add pet fragment/activity
            // Example using Navigation Component:
            // findNavController().navigate(R.id.action_homeFragment_to_addPetFragment)

            // Or traditional way:
            // startActivity(Intent(requireContext(), AddPetActivity::class.java))
        }
    }

    private fun setupRecyclerView() {
        petAdapter = YourPetAdapter()
        //debug log
        println("Setting up RecyclerView with adapter: $petAdapter")
        recyclerViewHorizontal.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = petAdapter
        }
        println("RecyclerView setup complete with layout manager: ${recyclerViewHorizontal.layoutManager}")
    }

    private fun loadPets() {
        val loginViewModel = ViewModelProvider(
            requireActivity(),
            LoginViewModel.Factory(
                UserRepository(AppDatabase.getInstance(requireActivity().application).userDao()),
                requireActivity().application
            )
        )[LoginViewModel::class.java]

        // Get the current user ID from the LoginViewModel
        val userId = loginViewModel.getLoggedInUserId()
        val hardCodePet = listOf(
            PetReduceForHome("1", "Pet 1", ""),
            PetReduceForHome("2", "Pet 2", ""),
            PetReduceForHome("3", "Pet 3", ""),
            PetReduceForHome("4", "Pet 4", ""),
            PetReduceForHome("5", "Pet 5", ""),
            PetReduceForHome("6", "Pet 6", ""),
            PetReduceForHome("7", "Pet 7", ""),
            PetReduceForHome("8", "Pet 8", ""),
        )
        petAdapter.submitList(hardCodePet)

//        if (userId != null) {
//            viewLifecycleOwner.lifecycleScope.launch {
//                val pets = petViewModel.getPetsForHome(userId)
//                println("Pets for user $userId: $pets") // Debug log
//                petAdapter.submitList(pets)
//            }
//        } else {
//            // Handle case where user ID is null (e.g., show a message or redirect to login)
//            redirectToLogin()
//            Toast.makeText(requireContext(), "User not logged in", Toast.LENGTH_SHORT).show()
//        }
    }

    private fun redirectToLogin() {
        startActivity(Intent(requireContext(), LoginActivity::class.java))
        activity?.finish()
    }

    companion object {
        @JvmStatic
        fun newInstance() = HomeFragment()
    }
}