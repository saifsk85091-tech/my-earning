package com.example

import org.junit.Assert.*
import org.junit.Test

class ExampleUnitTest {
  @Test
  fun addition_isCorrect() {
    assertEquals(4, 2 + 2)
  }

  @Test
  fun testCoinToCashConversion() {
    val coins = 5000L
    val cash = coins.toDouble() / 1000.0
    assertEquals(5.0, cash, 0.001)
  }

  @Test
  fun testMultiplierCalculation() {
    val baseReward = 100L
    val isBoostActive = true
    val multiplier = if (isBoostActive) 2L else 1L
    val total = baseReward * multiplier
    assertEquals(200L, total)
  }
}
