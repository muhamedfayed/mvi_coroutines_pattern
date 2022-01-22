package com.example.mvi.domain.posts.entity

import com.google.gson.annotations.SerializedName

class PostsResponse {
    @SerializedName("data") var data : Data? = null
}