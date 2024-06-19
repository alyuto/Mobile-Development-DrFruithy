package com.dicoding.drfruithy.ui.saved

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dicoding.drfruithy.R
import com.dicoding.drfruithy.data.pref.ResultItem
import com.dicoding.drfruithy.databinding.FragmentSavedBinding
import com.google.firebase.storage.FirebaseStorage
import com.google.gson.Gson

class SavedFragment : Fragment(), ResultAdapter.OnItemDeleteListener {

    private var _binding: FragmentSavedBinding? = null
    private val binding get() = _binding!!
    private lateinit var recyclerView: RecyclerView
    private lateinit var savedList: ArrayList<ResultItem>
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var resultAdapter: ResultAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSavedBinding.inflate(inflater, container, false)
        val root: View = binding.root

        recyclerView = binding.savedRecycle
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.setHasFixedSize(true)

        savedList = arrayListOf()
        resultAdapter = ResultAdapter(savedList, this)
        recyclerView.adapter = resultAdapter

        // Initialize SharedPreferences
        sharedPreferences = requireContext().getSharedPreferences("savedResults", Context.MODE_PRIVATE)

        // Show the ProgressBar before starting data fetch
        binding.progressBar.visibility = View.VISIBLE

        fetchSavedResultsFromLocal()

        return root
    }

    private fun fetchSavedResultsFromLocal() {
        savedList.clear()

        val allEntries = sharedPreferences.all
        for ((_, value) in allEntries) {
            try {
                val resultData = Gson().fromJson(value.toString(), ResultItem::class.java)
                savedList.add(resultData)
            } catch (e: Exception) {
                Log.e("SharedPreferences", "Error retrieving saved result: ${e.message}")
            }
        }

        resultAdapter.notifyDataSetChanged()

        loadImagesFromStorage()
    }

    private fun loadImagesFromStorage() {
        val storageRef = FirebaseStorage.getInstance("gs://dbtest-a7bc7.appspot.com").reference

        for (item in savedList) {
            val title = item.title
            val imageRef = storageRef.child("images/$title.jpg")

            // Download image using Glide or another image loading library
            imageRef.downloadUrl.addOnSuccessListener { uri ->
                // Find the correct ImageView in the RecyclerView's items
                val position = savedList.indexOf(item)
                val viewHolder = recyclerView.findViewHolderForAdapterPosition(position) as? ResultAdapter.MyViewHolder
                viewHolder?.let {
                    Glide.with(requireContext())
                        .load(uri)
                        .placeholder(R.drawable.home_tree) // Placeholder image
                        .error(R.drawable.earth) // Error image
                        .into(it.imageSaved)
                }

                // Hide the ProgressBar after all images are loaded
                if (position == savedList.size - 1) {
                    binding.progressBar.visibility = View.GONE
                }
            }.addOnFailureListener { e ->
                Log.e("FirebaseStorage", "Error downloading image for $title: ${e.message}")
                // Handle error loading image
                if (savedList.indexOf(item) == savedList.size - 1) {
                    binding.progressBar.visibility = View.GONE
                }
            }
        }

        // If there are no images to load, hide the ProgressBar
        if (savedList.isEmpty()) {
            binding.progressBar.visibility = View.GONE
        }
    }

    override fun onItemDelete(item: ResultItem) {
        val alertDialogBuilder = AlertDialog.Builder(requireContext())
        alertDialogBuilder.apply {
            setTitle("Konfirmasi")
            setMessage("Apakah Anda yakin ingin menghapus item ini?")
            setPositiveButton("Ya") { _, _ ->
                // Hapus item dari SharedPreferences
                val editor = sharedPreferences.edit()
                val allEntries = sharedPreferences.all
                for ((key, value) in allEntries) {
                    val resultData = Gson().fromJson(value.toString(), ResultItem::class.java)
                    if (resultData == item) {
                        editor.remove(key)
                        editor.apply()
                        break
                    }
                }
                // Hapus item dari RecyclerView
                savedList.remove(item)
                resultAdapter.notifyDataSetChanged()
            }
            setNegativeButton("Tidak") { dialog, _ ->
                dialog.dismiss()
            }
            show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}