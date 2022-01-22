package com.example.mvi.presentation.post

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.mvi.domain.posts.entity.PostEntity

class ViewPagerAdapter(fragment: Fragment, private val videoDataList: List<PostEntity> ) : FragmentStateAdapter(fragment) {
    override fun createFragment(position: Int): Fragment {
        return PostViewFragment.newInstance(videoDataList[position])
    }

    override fun getItemCount(): Int {
        return videoDataList.size
    }

}