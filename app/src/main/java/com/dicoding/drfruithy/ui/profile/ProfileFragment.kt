package com.dicoding.drfruithy.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.dicoding.drfruithy.ViewModelFactory
import com.dicoding.drfruithy.databinding.FragmentProfileBinding

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        (activity as AppCompatActivity).supportActionBar?.title = "Pengaturan"

        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        val root: View = binding.root

        darkMode()
        setupLogoutButton()
        return root
    }

    private fun setupLogoutButton() {
        val factory = ViewModelFactory.getInstance(requireContext())
        val profileViewModel = ViewModelProvider(this, factory).get(ProfileViewModel::class.java)

        binding.logout.setOnClickListener {
            profileViewModel.logout()
        }
    }

    private fun darkMode() {
        binding.switchTheme

        val factory = ViewModelFactory.getInstance(requireContext())
        val profileViewModel = ViewModelProvider(this, factory).get(ProfileViewModel::class.java)

        profileViewModel.getThemeSettings().observe(viewLifecycleOwner) { isDarkModeActive: Boolean ->
            if (isDarkModeActive) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                binding.switchTheme.isChecked = true
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                binding.switchTheme.isChecked = false
            }
        }

        binding.switchTheme.setOnCheckedChangeListener { _: CompoundButton?, isChecked: Boolean ->
            profileViewModel.saveThemeSetting(isChecked)
        }
    }
}
