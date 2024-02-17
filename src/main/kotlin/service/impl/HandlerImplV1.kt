package service.impl

import adapters.Client
import adapters.model.Response
import dto.ApplicationStatusResponse
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import service.Handler
import java.time.Clock
import java.time.Instant.now
import kotlin.time.DurationUnit
import kotlin.time.toDuration

class HandlerImplV1 (private val applicationStatusClient: Client, private val clock: Clock) : Handler{

    override fun performOperation(id: String): ApplicationStatusResponse {
        return selectApplicationStatusResponseFromClient(id)
    }

    private fun selectApplicationStatusResponseFromClient(id: String): ApplicationStatusResponse = runBlocking{
      val channel = Channel<ApplicationStatusResponse>()

          launch {
              getApplicationStatus(id, channel, applicationStatusClient::getApplicationStatus1)
          }
          launch {
              getApplicationStatus(id, channel, applicationStatusClient::getApplicationStatus2)
          }

        val result = channel.receive()
        coroutineContext.cancelChildren()
        channel.close()

        result
    }

    private suspend fun getApplicationStatus(
        id: String,
        channel: Channel<ApplicationStatusResponse>,
        getter: (id: String) -> Response
    ) {
        var retries = 0
        while (true) {
            when (val clientResponse = getter(id)) {
                is Response.Failure -> {
                    channel.send(
                        ApplicationStatusResponse.Failure(
                            now(clock).nano.toDuration(DurationUnit.NANOSECONDS),
                            retries
                        )
                    )
                    break
                }

                is Response.Success -> {
                    channel.send(
                        ApplicationStatusResponse.Success(
                            clientResponse.applicationId,
                            clientResponse.applicationStatus
                        )
                    )
                    break
                }

                is Response.RetryAfter -> {
                    delay(clientResponse.delay)
                    retries++
                }
            }
        }
    }
}