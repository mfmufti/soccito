package com.team9.soccermanager

import com.team9.soccermanager.model.Team
import com.team9.soccermanager.model.accessor.TeamAccessor
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 3 + 1)
        var x = TeamAccessor
    }
}