package com.dicoding.drfruithy.ui.article

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dicoding.drfruithy.data.pref.ArticleDao
import com.dicoding.drfruithy.databinding.FragmentArticleBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ArticleFragment : Fragment() {

    private var _binding: FragmentArticleBinding? = null
    private val binding get() = _binding!!
    private lateinit var recyclerView: RecyclerView
    private lateinit var userArrayList: ArrayList<ArticleDao>
    private lateinit var dbref: DatabaseReference
    private lateinit var articleAdapter: ArticleAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        (activity as AppCompatActivity).supportActionBar?.title = "Artikel"
        _binding = FragmentArticleBinding.inflate(inflater, container, false)
        val root: View = binding.root

        recyclerView = binding.articleRecycleView
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.setHasFixedSize(true)

        userArrayList = arrayListOf()
        articleAdapter = ArticleAdapter(userArrayList)
        recyclerView.adapter = articleAdapter

        getUserData()

        return root
    }

    private fun getUserData() {
        // Show the ProgressBar before starting data fetch
        binding.progressBar.visibility = View.VISIBLE

        dbref = FirebaseDatabase.getInstance("https://dbtest-a7bc7-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("tumbuhan")
        val query = dbref.orderByChild("nama_penyakit")

        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    userArrayList.clear() // Clear the list before adding new items
                    Log.d("FirebaseData", "Data found: ${snapshot.childrenCount} items")

                    for (userSnapshot in snapshot.children) {
                        val user = userSnapshot.getValue(ArticleDao::class.java)
                        user?.let {
                            userArrayList.add(it)
                            Log.d("FirebaseData", "User added: ${it.nama_penyakit}")
                        }
                    }

                    articleAdapter.notifyDataSetChanged() // Notify adapter of data changes
                } else {
                    Log.d("FirebaseData", "No data exists.")
                }
                // Hide the ProgressBar after data is loaded
                binding.progressBar.visibility = View.GONE
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("FirebaseError", "Error: ${error.message}")
                // Hide the ProgressBar in case of error
                binding.progressBar.visibility = View.GONE
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
