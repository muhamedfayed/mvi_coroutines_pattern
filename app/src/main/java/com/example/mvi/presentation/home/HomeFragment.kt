package com.example.mvi.presentation.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.mvi.R
import com.example.mvi.databinding.FragmentHomeBinding
import com.example.mvi.presentation.post.ViewPagerAdapter
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.koin.android.viewmodel.ext.android.viewModel

class HomeFragment : Fragment() {


     private var _binding: FragmentHomeBinding? = null
     private lateinit var videoPagerAdapter: ViewPagerAdapter
     private val viewModel by viewModel<HomeViewModel>()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment'
        _binding = FragmentHomeBinding.inflate(inflater,container,false)

        return _binding?.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        render()
        getData()

        _binding!!.retryBtn.setOnClickListener {
            getData()
        }
    }

    private fun getData(){
        lifecycleScope.launch {
            viewModel.intentChannel.send(HomeIntent.GetData)
        }
    }

    private fun render(){
        //Render
        lifecycleScope.launch {
            viewModel.state.collect{
                when(it){
                    is HomeViewState.IsLoading -> {
                        if (it.isLoading){
                            _binding!!.progressBar.visibility = View.VISIBLE
                            _binding!!.retryBtn.visibility = View.GONE
                        }else{
                            _binding!!.progressBar.visibility = View.GONE
                        }
                    }
                    is HomeViewState.Data ->{
                        _binding!!.viewPager.visibility = View.VISIBLE
                        videoPagerAdapter = ViewPagerAdapter(this@HomeFragment, it.data.data!!)
                        _binding!!.viewPager.adapter = videoPagerAdapter
                        _binding!!.viewPager.offscreenPageLimit = 3
                    }
                    is HomeViewState.Error ->{
                        Toast.makeText(context, getString(R.string.something),
                            Toast.LENGTH_LONG).show()
                        _binding!!.retryBtn.visibility = View.VISIBLE
                    }
                    HomeViewState.Init -> {

                    }
                }
            }
        }
    }

}