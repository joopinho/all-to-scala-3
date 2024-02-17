package service

import dto.ApplicationStatusResponse

fun interface Handler {
    fun performOperation(id: String): ApplicationStatusResponse
}