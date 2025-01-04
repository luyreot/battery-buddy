package com.teoryul.batterybuddy.domain

import com.teoryul.batterybuddy.data.BatteryStats
import com.teoryul.batterybuddy.data.SharedPrefs
import com.teoryul.batterybuddy.domain.BatteryStatusUseCase.BatteryStatus
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@Suppress("DANGEROUS_CHARACTERS")
class BatteryStatusUseCaseTest {

    private val sharedPrefs: SharedPrefs = mockk(relaxed = true)
    private val batteryStats: BatteryStats = mockk(relaxed = true)

    private val scale = 100

    private lateinit var sut: BatteryStatusUseCase

    private val batteryStatsBatteryLvlIntSlot = slot<Int>()
    private val sharedPrefsBatteryLvlIntSlot = slot<Int>()

    @Before
    fun setup() {
        sut = BatteryStatusUseCase(sharedPrefs, batteryStats)

        batteryStatsBatteryLvlIntSlot.clear()
        sharedPrefsBatteryLvlIntSlot.clear()

        every { batteryStats.batteryLvlInt = capture(batteryStatsBatteryLvlIntSlot) } answers {}
        every { sharedPrefs.putBatteryLvl(capture(sharedPrefsBatteryLvlIntSlot)) } answers {}
    }

    @After
    fun cleanup() {
    }

    private fun verifyBatteryLevelUpdated(level: Int) {
        assertEquals(level, batteryStatsBatteryLvlIntSlot.captured)
        assertEquals(level, sharedPrefsBatteryLvlIntSlot.captured)
    }

    @Test
    fun `overheat between 15% to 85%`() {
        val level = 15

        every { sharedPrefs.getBatteryOverheat() } returns false

        val sharedPrefsOverheatSlot = slot<Boolean>()
        every { sharedPrefs.putBatteryOverheat(capture(sharedPrefsOverheatSlot)) } answers {}

        for (i in 0 until 8) {
            batteryStatsBatteryLvlIntSlot.clear()
            sharedPrefsBatteryLvlIntSlot.clear()
            sharedPrefsOverheatSlot.clear()

            val newLevel = level + i * 10

            val result = sut.onStatusChanged(
                level = newLevel,
                scale = scale,
                statusCharging = i % 2 == 0,
                statusDischarging = i % 3 == 0,
                statusNotCharging = i % 5 == 0,
                acCharge = i % 6 == 0,
                usbCharge = i % 7 == 0,
                wirelessCharge = i % 4 == 0,
                dockCharge = false,
                healthOverheat = true
            )

            verifyBatteryLevelUpdated(newLevel)
            assertTrue(sharedPrefsOverheatSlot.captured)
            verify(exactly = i + 1) { sharedPrefs.clearNotifyAtBatteryLvl() }
            assertEquals(BatteryStatus.Overheating, result)
        }
    }

    @Test
    fun `stop overheat between 15% to 85%`() {
        val level = 15

        every { sharedPrefs.getBatteryOverheat() } returns true

        val sharedPrefsOverheatSlot = slot<Boolean>()
        every { sharedPrefs.putBatteryOverheat(capture(sharedPrefsOverheatSlot)) } answers {}

        for (i in 0 until 8) {
            batteryStatsBatteryLvlIntSlot.clear()
            sharedPrefsBatteryLvlIntSlot.clear()
            sharedPrefsOverheatSlot.clear()

            val newLevel = level + i * 10

            val result = sut.onStatusChanged(
                level = newLevel,
                scale = scale,
                statusCharging = i % 2 == 0,
                statusDischarging = i % 3 == 0,
                statusNotCharging = i % 5 == 0,
                acCharge = i % 6 == 0,
                usbCharge = i % 7 == 0,
                wirelessCharge = i % 4 == 0,
                dockCharge = false,
                healthOverheat = false
            )

            verifyBatteryLevelUpdated(newLevel)
            assertFalse(sharedPrefsOverheatSlot.captured)
            verify(exactly = i + 1) { sharedPrefs.clearNotifyAtBatteryLvl() }
            assertEquals(BatteryStatus.StoppedOverheating, result)
        }
    }

    @Test
    fun `continue overheat between 15% to 85%`() {
        val level = 15

        every { sharedPrefs.getBatteryOverheat() } returns true

        for (i in 0 until 8) {
            batteryStatsBatteryLvlIntSlot.clear()
            sharedPrefsBatteryLvlIntSlot.clear()

            val newLevel = level + i * 10

            val result = sut.onStatusChanged(
                level = newLevel,
                scale = scale,
                statusCharging = i % 2 == 0,
                statusDischarging = i % 3 == 0,
                statusNotCharging = i % 5 == 0,
                acCharge = i % 6 == 0,
                usbCharge = i % 7 == 0,
                wirelessCharge = i % 4 == 0,
                dockCharge = false,
                healthOverheat = true
            )

            verifyBatteryLevelUpdated(newLevel)
            verify(exactly = 0) { sharedPrefs.putBatteryOverheat(any()) }
            verify(exactly = 0) { sharedPrefs.clearNotifyAtBatteryLvl() }
            assertEquals(BatteryStatus.NoStatus, result)
        }
    }

    @Test
    fun `dismiss notifications when unplugged and discharging, battery lvl unchanged and above 60%`() {
        val level = 61
        every { sharedPrefs.getBatteryOverheat() } returns false

        for (i in 0 until 10) {
            batteryStatsBatteryLvlIntSlot.clear()
            sharedPrefsBatteryLvlIntSlot.clear()

            val newLevel = level + i * 5

            every { sharedPrefs.getBatteryLvl() } returns newLevel

            val result = sut.onStatusChanged(
                level = newLevel,
                scale = scale,
                statusCharging = false,
                statusDischarging = true,
                statusNotCharging = false,
                acCharge = false,
                usbCharge = false,
                wirelessCharge = false,
                dockCharge = false,
                healthOverheat = false
            )

            verifyBatteryLevelUpdated(newLevel)
            verify(exactly = i + 1) { sharedPrefs.clearNotifyAtBatteryLvl() }
            assertEquals(BatteryStatus.DismissBatteryLvlNotification, result)
        }
    }

    @Test
    fun `no status when unplugged and discharging, battery lvl unchanged and equal or below 59%`() {
        val level = 59
        every { sharedPrefs.getBatteryOverheat() } returns false

        for (i in 0 until 10) {
            batteryStatsBatteryLvlIntSlot.clear()
            sharedPrefsBatteryLvlIntSlot.clear()

            val newLevel = level - i * 5

            every { sharedPrefs.getBatteryLvl() } returns newLevel

            val result = sut.onStatusChanged(
                level = newLevel,
                scale = scale,
                statusCharging = false,
                statusDischarging = true,
                statusNotCharging = false,
                acCharge = false,
                usbCharge = false,
                wirelessCharge = false,
                dockCharge = false,
                healthOverheat = false
            )

            verifyBatteryLevelUpdated(newLevel)
            verify(exactly = 0) { sharedPrefs.clearNotifyAtBatteryLvl() }
            assertEquals(BatteryStatus.NoStatus, result)
        }
    }

    @Test
    fun `stop charging when plugged and charging or not charging, battery lvl changes and equal or above 80%`() {
        val level = 80
        every { sharedPrefs.getBatteryOverheat() } returns false

        for (i in 0 until 20) {
            batteryStatsBatteryLvlIntSlot.clear()
            sharedPrefsBatteryLvlIntSlot.clear()

            val newLevel = level + i * 1

            every { sharedPrefs.getBatteryLvl() } returns newLevel - 1

            val result = sut.onStatusChanged(
                level = newLevel,
                scale = scale,
                statusCharging = i % 2 == 0,
                statusDischarging = false,
                statusNotCharging = i % 2 != 0,
                acCharge = false,
                usbCharge = false,
                wirelessCharge = true,
                dockCharge = false,
                healthOverheat = false
            )

            verifyBatteryLevelUpdated(newLevel)
            verify(exactly = i + 1) { sharedPrefs.clearNotifyAtBatteryLvl() }
            assertEquals(BatteryStatus.StopCharging, result)
        }
    }

    @Test
    fun `dismiss notification when plugged and charging or not charging, battery lvl changes and below 80%`() {
        every { sharedPrefs.getBatteryOverheat() } returns false

        for (newLevel in 1 until 79) {
            batteryStatsBatteryLvlIntSlot.clear()
            sharedPrefsBatteryLvlIntSlot.clear()

            every { sharedPrefs.getBatteryLvl() } returns newLevel - 1

            val result = sut.onStatusChanged(
                level = newLevel,
                scale = scale,
                statusCharging = newLevel % 2 == 0,
                statusDischarging = false,
                statusNotCharging = newLevel % 2 != 0,
                acCharge = false,
                usbCharge = false,
                wirelessCharge = true,
                dockCharge = false,
                healthOverheat = false
            )

            verifyBatteryLevelUpdated(newLevel)
            verify(exactly = newLevel) { sharedPrefs.clearNotifyAtBatteryLvl() }
            assertEquals(BatteryStatus.DismissBatteryLvlNotification, result)
        }
    }

    @Test
    fun `dismiss notifications when plugged and charging or not charging, battery lvl changes and below80%`() {
        val level = 79
        every { sharedPrefs.getBatteryOverheat() } returns false

        for (i in 0 until 10) {
            batteryStatsBatteryLvlIntSlot.clear()
            sharedPrefsBatteryLvlIntSlot.clear()

            val newLevel = level - i * 5

            every { sharedPrefs.getBatteryLvl() } returns newLevel + 1

            val result = sut.onStatusChanged(
                level = newLevel,
                scale = scale,
                statusCharging = i % 2 == 0,
                statusDischarging = false,
                statusNotCharging = i % 2 != 0,
                acCharge = false,
                usbCharge = false,
                wirelessCharge = true,
                dockCharge = false,
                healthOverheat = false
            )

            verifyBatteryLevelUpdated(newLevel)
            verify(exactly = i + 1) { sharedPrefs.clearNotifyAtBatteryLvl() }
            assertEquals(BatteryStatus.DismissBatteryLvlNotification, result)
        }
    }

    @Test
    fun `charge at 20 when unplugged and discharging, battery lvl changes and equal or below 20%`() {
        val level = 19
        every { sharedPrefs.getBatteryOverheat() } returns false

        for (i in 0 until 10) {
            batteryStatsBatteryLvlIntSlot.clear()
            sharedPrefsBatteryLvlIntSlot.clear()

            val newLevel = level - i * 1

            every { sharedPrefs.getBatteryLvl() } returns newLevel + 1

            val result = sut.onStatusChanged(
                level = newLevel,
                scale = scale,
                statusCharging = false,
                statusDischarging = true,
                statusNotCharging = false,
                acCharge = false,
                usbCharge = false,
                wirelessCharge = false,
                dockCharge = false,
                healthOverheat = false
            )

            verifyBatteryLevelUpdated(newLevel)
            verify(exactly = i + 1) { sharedPrefs.clearNotifyAtBatteryLvl() }
            assertEquals(BatteryStatus.Charge20, result)
        }
    }

    @Test
    fun `charge at 60 when unplugged and discharging, battery lvl changes and between 20% and 59%`() {
        every { sharedPrefs.getBatteryOverheat() } returns false

        for (lvl in 59 downTo 20) {
            batteryStatsBatteryLvlIntSlot.clear()
            sharedPrefsBatteryLvlIntSlot.clear()

            every { sharedPrefs.getBatteryLvl() } returns lvl + 1
            every { sharedPrefs.getNotifyAtBatteryLvl() } returns -1

            val result = sut.onStatusChanged(
                level = lvl,
                scale = scale,
                statusCharging = false,
                statusDischarging = true,
                statusNotCharging = false,
                acCharge = false,
                usbCharge = false,
                wirelessCharge = false,
                dockCharge = false,
                healthOverheat = false
            )

            verifyBatteryLevelUpdated(lvl)
            verify(exactly = 0) { sharedPrefs.clearNotifyAtBatteryLvl() }
            assertEquals(BatteryStatus.Charge60, result)
        }
    }

    @Test
    fun `charge at 60 when unplugged and discharging, battery lvl changes and between 20% and 59% with lvl skip 10`() {
        every { sharedPrefs.getBatteryOverheat() } returns false

        val lvlStart = 59
        val notifyAt = 49

        every { sharedPrefs.getNotifyAtBatteryLvl() } returns notifyAt

        for (lvl in lvlStart downTo 49) {
            batteryStatsBatteryLvlIntSlot.clear()
            sharedPrefsBatteryLvlIntSlot.clear()

            every { sharedPrefs.getBatteryLvl() } returns lvl + 1

            val result = sut.onStatusChanged(
                level = lvl,
                scale = scale,
                statusCharging = false,
                statusDischarging = true,
                statusNotCharging = false,
                acCharge = false,
                usbCharge = false,
                wirelessCharge = false,
                dockCharge = false,
                healthOverheat = false
            )

            verifyBatteryLevelUpdated(lvl)

            if (lvl != notifyAt) {
                verify(exactly = 0) { sharedPrefs.clearNotifyAtBatteryLvl() }
                assertEquals(BatteryStatus.NoStatus, result)
            } else {
                verify(exactly = 1) { sharedPrefs.clearNotifyAtBatteryLvl() }
                assertEquals(BatteryStatus.Charge60, result)
            }
        }
    }


}