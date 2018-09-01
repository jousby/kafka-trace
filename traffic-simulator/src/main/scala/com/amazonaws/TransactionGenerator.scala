package com.amazonaws

import java.time.Instant
import java.time.temporal.ChronoField
import java.util.UUID

import com.amazonaws.model.Transaction


object TransactionGenerator extends MessageGenerator[Transaction] {

  override def create() = {
    // avro timestamp expects micros from epoch
    val txnTimestamp = {
      val now = Instant.now()
      val epochSecondsToMicros = now.getEpochSecond * 1000000 // number of micros in a second
      val remainingMicros = now.get(ChronoField.MICRO_OF_SECOND)
      epochSecondsToMicros + remainingMicros
    }

    Transaction(
      UUID.randomUUID().toString,
      10000.00,
      "account1",
      "account2",
      "desc",
      txnTimestamp)
  }
}
