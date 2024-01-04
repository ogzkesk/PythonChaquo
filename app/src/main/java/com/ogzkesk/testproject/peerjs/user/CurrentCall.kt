package com.ogzkesk.testproject.peerjs.user

data class CurrentCall(
    val roomId: String,
    val from: String,
    val to: String
) {
    companion object {
        fun initial() : CurrentCall {
            return CurrentCall("","","")
        }
    }
}