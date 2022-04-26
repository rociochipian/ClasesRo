package ar.edu.utn.frba.mobile.clases.ui.main

data class Tweet(
    val profilePic: String,
    val name: String,
    val certified: Boolean,
    val username: String,
    val content: String,
    val image: String?,
    val commentCount: Int,
    val retweetCount: Int,
    val likeCount: Int)
data class TweetsWrappers(val tweets: MutableList<Tweet>)