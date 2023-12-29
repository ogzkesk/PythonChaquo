package com.ogzkesk.testproject.peerjs.web_rtc

sealed interface RoomType{

    object CREATE : RoomType
    object JOIN: RoomType
}
