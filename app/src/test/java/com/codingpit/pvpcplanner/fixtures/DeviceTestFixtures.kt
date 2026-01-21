package com.codingpit.pvpcplanner.fixtures

import com.codingpit.pvpcplanner.domain.models.Device
import com.codingpit.pvpcplanner.domain.models.TimeSlot

/**
 * Test fixtures for Device-related data.
 * Provides realistic device configurations for testing various scenarios.
 */
object DeviceTestFixtures {

    /**
     * Common household appliances with realistic power consumption patterns.
     */
    object CommonDevices {
        val WASHING_MACHINE = Device(
            id = 1,
            name = "Washing Machine",
            hours = 2,
            icon = "washing_machine"
        )

        val DISHWASHER = Device(
            id = 2,
            name = "Dishwasher",
            hours = 2,
            icon = "dishwasher"
        )

        val DRYER = Device(
            id = 3,
            name = "Clothes Dryer",
            hours = 1,
            icon = "dryer"
        )

        val WATER_HEATER = Device(
            id = 4,
            name = "Electric Water Heater",
            hours = 3,
            icon = "water_heater"
        )

        val ELECTRIC_CAR_CHARGER = Device(
            id = 5,
            name = "EV Charger",
            hours = 8,
            icon = "ev_charger"
        )

        val POOL_PUMP = Device(
            id = 6,
            name = "Swimming Pool Pump",
            hours = 6,
            icon = "pool_pump"
        )
    }

    /**
     * Premium/high-efficiency appliances.
     */
    object PremiumDevices {
        val ECO_WASHING_MACHINE = Device(
            id = 11,
            name = "Eco Washing Machine A+++",
            hours = 3,
            icon = "washing_machine_eco"
        )

        val INDUCTION_COOKTOP = Device(
            id = 12,
            name = "Induction Cooktop",
            hours = 1,
            icon = "induction_cooktop"
        )

        val HEAT_PUMP_DRYER = Device(
            id = 13,
            name = "Heat Pump Dryer",
            hours = 4,
            icon = "dryer_heat_pump"
        )
    }

    /**
     * Edge case devices for testing boundary conditions.
     */
    object EdgeCaseDevices {
        val MINIMAL_DEVICE = Device(
            id = 100,
            name = "Minimal Device",
            hours = 1,
            icon = "minimal"
        )

        val LONG_RUNNING_DEVICE = Device(
            id = 101,
            name = "Long Running Process",
            hours = 12,
            icon = "long_process"
        )

        val VERY_LONG_DEVICE = Device(
            id = 102,
            name = "24-Hour Process",
            hours = 24,
            icon = "continuous"
        )

        val EMPTY_NAME_DEVICE = Device(
            id = 103,
            name = "",
            hours = 2,
            icon = ""
        )

        val INVALID_HOURS_DEVICE = Device(
            id = 104,
            name = "Invalid Device",
            hours = -1,
            icon = "invalid"
        )

        val ZERO_HOURS_DEVICE = Device(
            id = 105,
            name = "Zero Hours Device",
            hours = 0,
            icon = "zero"
        )
    }

    /**
     * Device collections for testing multiple device scenarios.
     */
    object Collections {
        val BASIC_HOUSEHOLD = listOf(
            CommonDevices.WASHING_MACHINE,
            CommonDevices.DISHWASHER,
            CommonDevices.DRYER
        )

        val COMPLETE_HOUSEHOLD = listOf(
            CommonDevices.WASHING_MACHINE,
            CommonDevices.DISHWASHER,
            CommonDevices.DRYER,
            CommonDevices.WATER_HEATER,
            CommonDevices.POOL_PUMP
        )

        val PREMIUM_HOUSEHOLD = listOf(
            PremiumDevices.ECO_WASHING_MACHINE,
            CommonDevices.DISHWASHER,
            PremiumDevices.HEAT_PUMP_DRYER,
            PremiumDevices.INDUCTION_COOKTOP
        )

        val MIXED_DURATION_DEVICES = listOf(
            EdgeCaseDevices.MINIMAL_DEVICE,        // 1 hour
            CommonDevices.DISHWASHER,             // 2 hours
            CommonDevices.WATER_HEATER,           // 3 hours
            CommonDevices.POOL_PUMP,              // 6 hours
            CommonDevices.ELECTRIC_CAR_CHARGER    // 8 hours
        )

        val EDGE_CASE_DEVICES = listOf(
            EdgeCaseDevices.MINIMAL_DEVICE,
            EdgeCaseDevices.LONG_RUNNING_DEVICE,
            EdgeCaseDevices.EMPTY_NAME_DEVICE,
            EdgeCaseDevices.ZERO_HOURS_DEVICE
        )

        val EMPTY_DEVICES = emptyList<Device>()

        val SINGLE_DEVICE = listOf(CommonDevices.WASHING_MACHINE)

        val DUPLICATE_ID_DEVICES = listOf(
            CommonDevices.WASHING_MACHINE,
            Device(1, "Duplicate ID Device", 3, "duplicate"), // Same ID as washing machine
            CommonDevices.DISHWASHER
        )
    }

    /**
     * Expected time slots for devices with typical daily prices.
     * These correspond to optimal times based on PVPCTestFixtures.TYPICAL_DAILY_PRICES.
     */
    object ExpectedTimeSlots {
        // Based on cheapest consecutive hours from typical daily pattern
        val WASHING_MACHINE_OPTIMAL = TimeSlot(2, 4)  // 02-04h (cheapest 2-hour slot)
        val DISHWASHER_OPTIMAL = TimeSlot(2, 4)       // 02-04h (same as washing machine)
        val DRYER_OPTIMAL = TimeSlot(2, 3)            // 02-03h (cheapest 1-hour slot)
        val WATER_HEATER_OPTIMAL = TimeSlot(1, 4)     // 01-04h (cheapest 3-hour slot)
        val EV_CHARGER_OPTIMAL = TimeSlot(0, 8)       // 00-08h (cheapest night hours)
        val POOL_PUMP_OPTIMAL = TimeSlot(0, 6)        // 00-06h (night to early morning)
    }

    /**
     * Test scenarios for various use cases.
     */
    object Scenarios {
        /**
         * Morning routine devices that should run during night hours.
         */
        val MORNING_PREP_DEVICES = listOf(
            Device(201, "Coffee Maker Timer", 1, "coffee"),
            Device(202, "Bread Maker", 4, "bread_maker"),
            CommonDevices.DISHWASHER
        )

        /**
         * Heavy consumption devices that need long cheap periods.
         */
        val HEAVY_CONSUMPTION_DEVICES = listOf(
            CommonDevices.ELECTRIC_CAR_CHARGER,
            CommonDevices.POOL_PUMP,
            CommonDevices.WATER_HEATER
        )

        /**
         * Quick devices that can run during short cheap periods.
         */
        val QUICK_DEVICES = listOf(
            EdgeCaseDevices.MINIMAL_DEVICE,
            Device(301, "Microwave", 1, "microwave"),
            Device(302, "Kettle", 1, "kettle")
        )

        /**
         * Weekend batch processing scenario.
         */
        val WEEKEND_BATCH = listOf(
            Device(401, "Laundry Load 1", 2, "washing_machine"),
            Device(402, "Laundry Load 2", 2, "washing_machine"),
            Device(403, "Dry Load 1", 1, "dryer"),
            Device(404, "Dry Load 2", 1, "dryer"),
            CommonDevices.DISHWASHER
        )
    }

    /**
     * Device update scenarios for testing CRUD operations.
     */
    object UpdateScenarios {
        fun updateDeviceName(device: Device, newName: String) = device.copy(name = newName)

        fun updateDeviceHours(device: Device, newHours: Int) = device.copy(hours = newHours)

        fun updateDeviceIcon(device: Device, newIcon: String) = device.copy(icon = newIcon)

        val WASHING_MACHINE_UPDATED_NAME = updateDeviceName(
            CommonDevices.WASHING_MACHINE,
            "Samsung EcoBubble Washing Machine"
        )

        val DISHWASHER_EFFICIENCY_UPGRADE = updateDeviceHours(
            CommonDevices.DISHWASHER,
            3 // Upgraded to more thorough cycle
        )

        val DRYER_ICON_UPDATE = updateDeviceIcon(
            CommonDevices.DRYER,
            "dryer_modern"
        )
    }
}
