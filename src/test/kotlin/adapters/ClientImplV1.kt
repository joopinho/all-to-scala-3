package adapters

import adapters.model.Response
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlin.time.Duration.Companion.milliseconds

class ClientImplV1 : Client {
    private var retries: Int = 0
    override fun getApplicationStatus1(id: String): Response {
        runBlocking { delay(10000) }
        return Response.Success(applicationStatus = "OK", applicationId = id)
    }

    override fun getApplicationStatus2(id: String): Response {
        retries++
        runBlocking { delay(1000) }
        return if (retries < 3) {
            Response.RetryAfter(1000.milliseconds)
        } else {
            Response.Failure(IllegalStateException("fatal exception"))
        }
    }
}