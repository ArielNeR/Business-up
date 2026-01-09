package com.businessup.ui.profile

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import androidx.fragment.app.viewModels
import com.businessup.R
import com.businessup.databinding.FragmentProfileBinding
import com.businessup.ui.clients.ClientsActivity
import com.businessup.ui.inventory.InventoryActivity
import com.businessup.ui.login.LoginActivity
import com.businessup.ui.sales.SalesListActivity
import com.businessup.ui.sales.SalesListType

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ProfileViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupClickListeners()
        observeViewModel()
    }

    private fun setupClickListeners() {
        binding.btnEditProfile.setOnClickListener {
            startActivity(Intent(requireContext(), EditProfileActivity::class.java))
        }

        binding.btnProductsServices.setOnClickListener {
            startActivity(Intent(requireContext(), InventoryActivity::class.java))
        }

        binding.btnClients.setOnClickListener {
            startActivity(Intent(requireContext(), ClientsActivity::class.java))
        }

        binding.btnSales.setOnClickListener {
            val intent = Intent(requireContext(), SalesListActivity::class.java)
            intent.putExtra("type", SalesListType.ALL.name)
            startActivity(intent)
        }

        binding.btnContact.setOnClickListener {
            // Open email intent
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "message/rfc822"
                putExtra(Intent.EXTRA_EMAIL, arrayOf("soporte@businessup.com"))
                putExtra(Intent.EXTRA_SUBJECT, "Contacto desde Business Up")
            }
            startActivity(Intent.createChooser(intent, "Enviar correo"))
        }

        binding.btnLogout.setOnClickListener {
            showLogoutConfirmation()
        }
    }

    private fun observeViewModel() {
        viewModel.usuario.observe(viewLifecycleOwner) { usuario ->
            usuario?.let {
                binding.tvUserName.text = it.nombre
                binding.tvUserEmail.text = it.correo

                it.fotoPerfil?.let { bytes ->
                    val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                    binding.ivProfilePhoto.setImageBitmap(bitmap)
                }
            }
        }
    }

    private fun showLogoutConfirmation() {
        MaterialAlertDialogBuilder(requireContext(), R.style.Theme_BusinessUp_Dialog)
            .setTitle(R.string.profile_logout)
            .setMessage(R.string.profile_logout_confirm)
            .setPositiveButton(R.string.btn_accept) { _, _ ->
                viewModel.logout()
                navigateToLogin()
            }
            .setNegativeButton(R.string.btn_cancel, null)
            .show()
    }

    private fun navigateToLogin() {
        val intent = Intent(requireContext(), LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        requireActivity().finish()
    }

    override fun onResume() {
        super.onResume()
        viewModel.loadUserData()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
