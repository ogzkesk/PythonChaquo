package com.ogzkesk.testproject.peerjs.web_rtc


private const val PERMISSION_EXCEPTION = "Permissions required"

class PermissionException(message: String = PERMISSION_EXCEPTION) : Exception(message)