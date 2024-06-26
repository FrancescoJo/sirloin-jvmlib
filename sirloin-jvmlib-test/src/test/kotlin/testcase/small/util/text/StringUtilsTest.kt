/*
 * com.sirloin.jvmlib
 * Sir.LOIN Intellectual property. All rights reserved.
 */
package testcase.small.util.text

import io.kotest.matchers.ints.shouldBeInRange
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.RepeatedTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import test.com.sirloin.util.text.randomFillChars
import test.com.sirloin.util.text.randomNumeral
import java.util.stream.Stream

/**
 * @since 2022-02-08
 */
@Suppress("ClassName")  // For using class name literal as test names
internal class StringUtilsTest {
    @Nested
    inner class `Exception is raised if` {
        @Test
        fun `min is less than 0`() {
            assertThrows<IllegalArgumentException> { randomFillChars('.', -1) }
        }

        @Test
        fun `max is less than 0`() {
            assertThrows<IllegalArgumentException> { randomFillChars('.', 0, -1) }
        }

        @Test
        fun `max is less than min`() {
            assertThrows<IllegalArgumentException> { randomFillChars('.', 1, 0) }
        }
    }

    @Test
    fun `Returns empty string if min is 0`() {
        // when:
        val chars = randomFillChars('.', 0, 0)

        // expect:
        chars.length shouldBe 0
    }

    @Test
    fun `Returns filled String if sufficient range is given`() {
        // given:
        val min = 0
        val max = 32

        // when:
        val chars = randomFillChars('.', min, max)

        // expect:
        chars.length shouldBeInRange min..max
    }

    @DisplayName("randomNumeral should:")
    @Nested
    inner class RandomNumeral {
        @DisplayName("pad start with '0' while option is given, and if number is smaller than 'digits':")
        @Nested
        inner class PadStartWithZeros {
            @ParameterizedTest(name = "from: {0}, until: {1}, digits: {2}")
            @MethodSource("testcase.small.util.text.StringUtilsTest#randomNumeralPadStartWithZeros")
            fun `works as expected on various inputs`(
                from: Long,
                until: Long,
                digits: Int,
                assertion: (String) -> Unit
            ) {
                // when:
                val result = randomNumeral(from = from, until = until, digits = digits)

                // expect:
                assertion(result)
            }

            @Test
            fun `padded with zeros with positive random number`() {
                // given:
                val digits = 5

                // when:
                val result = randomNumeral(from = 0, until = 9, digits = digits)

                // expect:
                assertAll(
                    { result.length shouldBe digits },
                    { result.startsWith("0000") shouldBe true }
                )
            }

            @Test
            fun `padded with zeros with negative random number`() {
                // given:
                val digits = 5

                // when:
                val result = randomNumeral(from = -9, until = 0, digits = digits)

                // expect:
                assertAll(
                    { result.length shouldBe (digits + 1) },
                    { result.startsWith("-0000") shouldBe true }
                )
            }
        }

        @DisplayName("Returns non-padded random-lengthed numerals")
        @Nested
        inner class ReturnsRandomLengthNumerals {
            @Test
            fun `of positive number`() {
                // given:
                val digits = 5

                // when: "99999 is in 5 digits"
                val result = randomNumeral(from = 0, until = 99999)

                // then:
                assertAll(
                    { result.length shouldBeInRange 0..digits },
                    { result.startsWith("0") shouldBe false }
                )
            }

            @Test
            fun `of negative number`() {
                // given:
                val digits = 5

                // when: "-99999 is in 5 digits"
                val result = randomNumeral(from = -99999, until = 0)

                // then:
                assertAll(
                    { result.length shouldBeInRange 2..(digits+1) },
                    { result.startsWith("-0") shouldBe false }
                )
            }
        }
    }

    companion object {
        @JvmStatic
        fun randomNumeralPadStartWithZeros(): Stream<Arguments> = Stream.of(
            // from, until, digits, assertion
            Arguments.of(1, 10, 2, { it: String ->
                assertAll(
                    { it.length shouldBe 2 },
                    { it.startsWith("0") shouldBe true }
                )
            }),
            Arguments.of(1, 100, 3, { it: String ->
                assertAll(
                    { it.length shouldBe 3 },
                    { it.startsWith("0") shouldBe true }
                )
            })
        )
    }
}
