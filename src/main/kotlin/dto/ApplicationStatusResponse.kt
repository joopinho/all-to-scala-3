package dto

import kotlin.time.Duration

sealed interface ApplicationStatusResponse {
    data class Failure(val lastRequestTime: Duration?, val retriesCount: Int): ApplicationStatusResponse
    data class Success(val id: String, val status: String): ApplicationStatusResponse
}