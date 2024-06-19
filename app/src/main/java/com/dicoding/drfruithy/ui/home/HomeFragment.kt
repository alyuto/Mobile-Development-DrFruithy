package com.dicoding.drfruithy.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.drfruithy.R
import com.dicoding.drfruithy.data.pref.ArticleDao
import com.dicoding.drfruithy.databinding.FragmentHomeBinding
import com.dicoding.drfruithy.ui.article.ArticleAdapter
import com.dicoding.drfruithy.ui.detect.ChooseDetectActivity
import com.dicoding.drfruithy.ui.guide.GuideActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var dbRef: DatabaseReference
    private lateinit var articleAdapter: HomeAdapter
    private lateinit var articleList: ArrayList<ArticleDao>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        (activity as AppCompatActivity).supportActionBar?.title = "Beranda"

        articleList = arrayListOf()
        articleAdapter = HomeAdapter(articleList)

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = articleAdapter
        }

        dbRef = FirebaseDatabase.getInstance("https://dbtest-a7bc7-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("tumbuhan")

        getArticles()

        binding.detect.setOnClickListener { chooseDetect() }
        binding.articleSeeAll.setOnClickListener { article() }
        binding.guideCard.setOnClickListener{ guide()}

        return root
    }

    private fun guide() {
        val intentGuide = Intent(requireContext(), GuideActivity::class.java)
        startActivity(intentGuide)

    }

    private fun getArticles() {
        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                articleList.clear()
                if (snapshot.exists()) {
                    for (articleSnapshot in snapshot.children) {
                        val article = articleSnapshot.getValue(ArticleDao::class.java)
                        article?.let { articleList.add(it) }
                    }
                    // Batasi jumlah artikel yang ditampilkan
                    if (articleList.size > 5) {
                        articleList = ArrayList(articleList.subList(0, 5))
                    }
                    articleAdapter.notifyDataSetChanged()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        })
    }

    private fun article() {
        val navOptions = NavOptions.Builder()
            .setPopUpTo(R.id.navigation_home, true)
            .build()
        findNavController().navigate(R.id.navigation_article, null, navOptions)
    }

    private fun chooseDetect() {
        val intentDetect = Intent(requireContext(), ChooseDetectActivity::class.java)
        startActivity(intentDetect)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
