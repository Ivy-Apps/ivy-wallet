package com.ivy.core.domain.action.calculate.transaction

import com.ivy.common.test.AndroidTest
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import io.kotest.matchers.shouldNotBe
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject

@AndroidTest
@HiltAndroidTest
class GroupTrnsFlowTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var groupTrnsFlow: GroupTrnsFlow

    @Before
    fun setUp() {
        hiltRule.inject()
    }

    @Test
    fun test() {
        groupTrnsFlow shouldNotBe null
    }

}