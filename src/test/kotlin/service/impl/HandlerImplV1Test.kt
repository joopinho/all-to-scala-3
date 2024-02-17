package service.impl

import adapters.ClientImplV1
import dto.ApplicationStatusResponse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.time.Clock
import java.time.Instant.now
import java.time.ZoneId
import java.util.Objects
import kotlin.time.DurationUnit
import kotlin.time.toDuration

class HandlerImplV1Test {
    companion object {
        private const val APPLICATION_ID = "1000001"
    }

    @Test
    fun `it should return fail with attempts`() {
        val client = ClientImplV1()
        val clock = Clock.fixed(now(), ZoneId.of("UTC"))
        val response = HandlerImplV1(client, clock).performOperation(APPLICATION_ID)

        assertTrue(Objects.equals(response, ApplicationStatusResponse.Failure(now(clock).nano.toDuration(DurationUnit.NANOSECONDS), 2 )))
    }
}