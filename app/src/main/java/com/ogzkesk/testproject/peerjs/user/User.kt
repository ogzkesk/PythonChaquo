package com.ogzkesk.testproject.peerjs.user


data class User(
    val id: String,
    val email: String?,
    val displayName: String?,
    val imgUrl: String?,
    val followerIds: List<String>,
    val follows: List<String>,
) {
    constructor() : this(
        id = "",
        email = null,
        displayName = null,
        imgUrl = null,
        followerIds = emptyList(),
        follows = emptyList()
    )
}
