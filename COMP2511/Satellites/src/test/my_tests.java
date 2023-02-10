package test;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import java.time.LocalTime;

import test.test_helpers.DummyConnection;
import test.test_helpers.ResponseHelper;
import test.test_helpers.TestHelper;

/*
    Intergation testings:
    test 1: testblue: test the edge conditions of BlueOrigin Satellite
    testSoviet_move1 .. 5 : test the movement of Soviet_Sate
    test 3: test "Maximum of 9 devices but will always accept new connections by dropping the oldest connection" feature of soviet statellite.
    test 4: test spaceX and Xmobile
*/

@TestInstance(value = Lifecycle.PER_CLASS)
public class my_tests {
    @Test
    public void testBlue() {
        // Task 2
        // test the Blueorigin max connections

        String initialWorldState = new ResponseHelper(LocalTime.of(0, 0))
            .expectSatellite("BlueOriginSatellite", "Satellite1", 10000, 30, 141.66, new String[] { "DeviceA", "DeviceB", "DeviceC", "DeviceD", "DeviceE", "DeviceF", "DeviceG", "DeviceH", "DeviceI", "DeviceJ", "DeviceK", "DeviceL", "DeviceN" })
            .expectDevice("LaptopDevice", "DeviceA", 30, false, new LocalTime[][] { { LocalTime.of(0, 0), LocalTime.of(6, 40) } })
            .expectDevice("LaptopDevice", "DeviceB", 30.5, false, new LocalTime[][] { { LocalTime.of(0, 0), LocalTime.of(6, 40) } })
            .expectDevice("LaptopDevice", "DeviceC", 31, false, new LocalTime[][] { { LocalTime.of(0, 0), LocalTime.of(6, 40) } })
            .expectDevice("LaptopDevice", "DeviceD", 31.5, false, new LocalTime[][] { { LocalTime.of(0, 0), LocalTime.of(6, 40) } })
            .expectDevice("LaptopDevice", "DeviceE", 32, false, new LocalTime[][] { { LocalTime.of(0, 0), LocalTime.of(6, 40) } })
            .expectDevice("LaptopDevice", "DeviceF", 32.5, false, new LocalTime[][] { { LocalTime.of(0, 0), LocalTime.of(6, 40) } })
            .expectDevice("DesktopDevice", "DeviceG", 33, false, new LocalTime[][] { { LocalTime.of(0, 0), LocalTime.of(6, 40) } })
            .expectDevice("DesktopDevice", "DeviceH", 33.5, false, new LocalTime[][] { { LocalTime.of(0, 0), LocalTime.of(6, 40) } })
            .expectDevice("DesktopDevice", "DeviceI", 34, false, new LocalTime[][] { { LocalTime.of(0, 0), LocalTime.of(6, 40) } })
            .expectDevice("HandheldDevice", "DeviceJ", 34.5, false, new LocalTime[][] { { LocalTime.of(0, 0), LocalTime.of(6, 40) } })
            .expectDevice("HandheldDevice", "DeviceK", 35, false, new LocalTime[][] { { LocalTime.of(0, 0), LocalTime.of(6, 40) } })
            .expectDevice("HandheldDevice", "DeviceL", 35.5, false, new LocalTime[][] { { LocalTime.of(0, 0), LocalTime.of(6, 40) } })
            .expectDevice("HandheldDevice", "DeviceN", 36, false, new LocalTime[][] { { LocalTime.of(0, 0), LocalTime.of(6, 40) } })
            .toString();

        // then simulates for a full day (1440 mins)
        // 5L + 2D + 3H = 10
        String afterADay = new ResponseHelper(LocalTime.of(0, 0))
            .expectSatellite("BlueOriginSatellite", "Satellite1", 10000, 50.4,141.66,
                new String[] { "DeviceA", "DeviceB", "DeviceC", "DeviceD", "DeviceE", "DeviceF", "DeviceG", "DeviceH", "DeviceI", "DeviceJ", "DeviceK", "DeviceL", "DeviceN" },
                new DummyConnection[] {
                    new DummyConnection("DeviceA", LocalTime.of(0, 0), LocalTime.of(6, 41), 398),
                    new DummyConnection("DeviceB", LocalTime.of(0, 0), LocalTime.of(6, 41), 398),
                    new DummyConnection("DeviceC", LocalTime.of(0, 0), LocalTime.of(6, 41), 398),
                    new DummyConnection("DeviceD", LocalTime.of(0, 0), LocalTime.of(6, 41), 398),
                    new DummyConnection("DeviceE", LocalTime.of(0, 0), LocalTime.of(6, 41), 398),
                    new DummyConnection("DeviceG", LocalTime.of(0, 0), LocalTime.of(6, 41), 395), 
                    new DummyConnection("DeviceH", LocalTime.of(0, 0), LocalTime.of(6, 41), 395), 
                    new DummyConnection("DeviceJ", LocalTime.of(0, 0), LocalTime.of(6, 41), 399), 
                    new DummyConnection("DeviceK", LocalTime.of(0, 0), LocalTime.of(6, 41), 399), 
                    new DummyConnection("DeviceL", LocalTime.of(0, 0), LocalTime.of(6, 41), 399), 
                })
                .expectDevice("LaptopDevice", "DeviceA", 30, false, new LocalTime[][] { { LocalTime.of(0, 0), LocalTime.of(6, 40) } })
                .expectDevice("LaptopDevice", "DeviceB", 30.5, false, new LocalTime[][] { { LocalTime.of(0, 0), LocalTime.of(6, 40) } })
                .expectDevice("LaptopDevice", "DeviceC", 31, false, new LocalTime[][] { { LocalTime.of(0, 0), LocalTime.of(6, 40) } })
                .expectDevice("LaptopDevice", "DeviceD", 31.5, false, new LocalTime[][] { { LocalTime.of(0, 0), LocalTime.of(6, 40) } })
                .expectDevice("LaptopDevice", "DeviceE", 32, false, new LocalTime[][] { { LocalTime.of(0, 0), LocalTime.of(6, 40) } })
                .expectDevice("LaptopDevice", "DeviceF", 32.5, false, new LocalTime[][] { { LocalTime.of(0, 0), LocalTime.of(6, 40) } })
                .expectDevice("DesktopDevice", "DeviceG", 33, false, new LocalTime[][] { { LocalTime.of(0, 0), LocalTime.of(6, 40) } })
                .expectDevice("DesktopDevice", "DeviceH", 33.5, false, new LocalTime[][] { { LocalTime.of(0, 0), LocalTime.of(6, 40) } })
                .expectDevice("DesktopDevice", "DeviceI", 34, false, new LocalTime[][] { { LocalTime.of(0, 0), LocalTime.of(6, 40) } })
                .expectDevice("HandheldDevice", "DeviceJ", 34.5, false, new LocalTime[][] { { LocalTime.of(0, 0), LocalTime.of(6, 40) } })
                .expectDevice("HandheldDevice", "DeviceK", 35, false, new LocalTime[][] { { LocalTime.of(0, 0), LocalTime.of(6, 40) } })
                .expectDevice("HandheldDevice", "DeviceL", 35.5, false, new LocalTime[][] { { LocalTime.of(0, 0), LocalTime.of(6, 40) } })
                .expectDevice("HandheldDevice", "DeviceN", 36, false, new LocalTime[][] { { LocalTime.of(0, 0), LocalTime.of(6, 40) } })
            .toString();

        TestHelper plan = new TestHelper()
            .createDevice("LaptopDevice", "DeviceA", 30)
            .createDevice("LaptopDevice", "DeviceB", 30.5)
            .createDevice("LaptopDevice", "DeviceC", 31)
            .createDevice("LaptopDevice", "DeviceD", 31.5)
            .createDevice("LaptopDevice", "DeviceE", 32)
            .createDevice("HandheldDevice", "DeviceL", 35.5)
            .createDevice("LaptopDevice", "DeviceF", 32.5)
            .createDevice("DesktopDevice", "DeviceH", 33.5)
            .createDevice("DesktopDevice", "DeviceG", 33)
            .createDevice("HandheldDevice", "DeviceJ", 34.5)
            .createDevice("DesktopDevice", "DeviceI", 34)
            .createDevice("HandheldDevice", "DeviceK", 35)
            .createDevice("HandheldDevice", "DeviceN", 36)
            .createSatellite("BlueOriginSatellite", "Satellite1", 10000, 30)
            .scheduleDeviceActivation("DeviceA", LocalTime.of(0, 0), 400)
            .scheduleDeviceActivation("DeviceB", LocalTime.of(0, 0), 400)
            .scheduleDeviceActivation("DeviceC", LocalTime.of(0, 0), 400)
            .scheduleDeviceActivation("DeviceD", LocalTime.of(0, 0), 400)
            .scheduleDeviceActivation("DeviceE", LocalTime.of(0, 0), 400)
            .scheduleDeviceActivation("DeviceF", LocalTime.of(0, 0), 400)
            .scheduleDeviceActivation("DeviceG", LocalTime.of(0, 0), 400)
            .scheduleDeviceActivation("DeviceH", LocalTime.of(0, 0), 400)
            .scheduleDeviceActivation("DeviceI", LocalTime.of(0, 0), 400)
            .scheduleDeviceActivation("DeviceJ", LocalTime.of(0, 0), 400)
            .scheduleDeviceActivation("DeviceK", LocalTime.of(0, 0), 400)
            .scheduleDeviceActivation("DeviceL", LocalTime.of(0, 0), 400)
            .scheduleDeviceActivation("DeviceN", LocalTime.of(0, 0), 400)
            .showWorldState(initialWorldState)
            .simulate(1440)
            .showWorldState(afterADay);
        plan.executeTestPlan();
    }

    @Test
    public void testSoviet_move1() {
        String after = new ResponseHelper(LocalTime.of(0, 0))
            .expectSatellite("SovietSatellite", "Satellite1", 10000, 154.4, 100.0, new String[] {})
            .toString();

        TestHelper plan = new TestHelper()
            .createSatellite("SovietSatellite", "Satellite1", 10000, 140)
            .simulate(1440)
            .showWorldState(after);
        plan.executeTestPlan();
    }

    @Test
    public void testSoviet_move2() {
        String after = new ResponseHelper(LocalTime.of(0, 0))
            .expectSatellite("SovietSatellite", "Satellite1", 10000.0, 329.6, -100.0, new String[] {})
            .toString();

        TestHelper plan = new TestHelper()
            .createSatellite("SovietSatellite", "Satellite1", 10000.0, 344)
            .simulate(1440)
            .showWorldState(after);
        plan.executeTestPlan();
    }

    @Test
    public void testSoviet_move3() {
        String after = new ResponseHelper(LocalTime.of(0, 0))
            .expectSatellite("SovietSatellite", "Satellite1", 10000.0, 330.6, -100.0, new String[] {})
            .toString();

        TestHelper plan = new TestHelper()
            .createSatellite("SovietSatellite", "Satellite1", 10000.0, 345)
            .simulate(1440)
            .showWorldState(after);
        plan.executeTestPlan();
    }

    @Test
    public void testSoviet_move4() {
        String after = new ResponseHelper(LocalTime.of(0, 0))
            .expectSatellite("SovietSatellite", "Satellite1", 10000.0, 0.4, 100.0, new String[] {})
            .toString();

        TestHelper plan = new TestHelper()
            .createSatellite("SovietSatellite", "Satellite1", 10000.0, 346)
            .simulate(1440)
            .showWorldState(after);
        plan.executeTestPlan();
    }

    @Test
    public void testSoviet_move5() {
        String after = new ResponseHelper(LocalTime.of(0, 0))
            .expectSatellite("SovietSatellite", "Satellite1", 10000.0, 185.62, -100.0, new String[] {})
            .toString();

        TestHelper plan = new TestHelper()
            .createSatellite("SovietSatellite", "Satellite1", 10000.0, 180)
            .simulate(1440)
            .showWorldState(after);
        plan.executeTestPlan();
    }

    @Test
    public void testSoviet() {
        // Task 2
        // Smallest satellite angle takes priority

        String initialWorldState = new ResponseHelper(LocalTime.of(0, 0))
        .expectSatellite("SovietSatellite", "Satellite1", 10000, 30, 100.0, new String[] { "DeviceA", "DeviceB", "DeviceC", "DeviceD", "DeviceE", "DeviceF", "DeviceG", "DeviceH", "DeviceI", "DeviceJ"})
        .expectDevice("LaptopDevice", "DeviceA", 30, false, new LocalTime[][] { { LocalTime.of(0, 0), LocalTime.of(3, 00) } })
        .expectDevice("LaptopDevice", "DeviceB", 30.5, false, new LocalTime[][] { { LocalTime.of(0, 0), LocalTime.of(6, 40) } })
        .expectDevice("LaptopDevice", "DeviceC", 31, false, new LocalTime[][] { { LocalTime.of(0, 0), LocalTime.of(6, 40) } })
        .expectDevice("LaptopDevice", "DeviceD", 31.5, false, new LocalTime[][] { { LocalTime.of(0, 0), LocalTime.of(6, 40) } })
        .expectDevice("LaptopDevice", "DeviceE", 32, false, new LocalTime[][] { { LocalTime.of(0, 0), LocalTime.of(6, 40) } })
        .expectDevice("LaptopDevice", "DeviceF", 32.5, false, new LocalTime[][] { { LocalTime.of(0, 0), LocalTime.of(6, 40) } })
        .expectDevice("DesktopDevice", "DeviceG", 33, false, new LocalTime[][] { { LocalTime.of(0, 0), LocalTime.of(6, 40) } })
        .expectDevice("DesktopDevice", "DeviceH", 33.5, false, new LocalTime[][] { { LocalTime.of(0, 0), LocalTime.of(6, 40) } })
        .expectDevice("DesktopDevice", "DeviceI", 34, false, new LocalTime[][] { { LocalTime.of(0, 0), LocalTime.of(6, 40) } })
        .expectDevice("DesktopDevice", "DeviceJ", 34.5, false, new LocalTime[][] { { LocalTime.of(3, 0), LocalTime.of(6, 40) } })
        .toString();

        String afterADay = new ResponseHelper(LocalTime.of(0, 0))
        .expectSatellite("SovietSatellite", "Satellite1", 10000, 44.40 ,100.0,
        new String[] { "DeviceA", "DeviceB", "DeviceC", "DeviceD", "DeviceE", "DeviceF", "DeviceG", "DeviceH", "DeviceI", "DeviceJ"},
        new DummyConnection[] {
            new DummyConnection("DeviceA", LocalTime.of(0, 0), LocalTime.of(3, 0), 176),
            new DummyConnection("DeviceB", LocalTime.of(0, 0), LocalTime.of(6, 41), 396),
            new DummyConnection("DeviceC", LocalTime.of(0, 0), LocalTime.of(6, 41), 396),
            new DummyConnection("DeviceD", LocalTime.of(0, 0), LocalTime.of(6, 41), 396),
            new DummyConnection("DeviceE", LocalTime.of(0, 0), LocalTime.of(6, 41), 396),
            new DummyConnection("DeviceF", LocalTime.of(0, 0), LocalTime.of(6, 41), 396),
            new DummyConnection("DeviceG", LocalTime.of(0, 0), LocalTime.of(6, 41), 390), 
            new DummyConnection("DeviceH", LocalTime.of(0, 0), LocalTime.of(6, 41), 390), 
            new DummyConnection("DeviceI", LocalTime.of(0, 0), LocalTime.of(6, 41), 390), 
            new DummyConnection("DeviceJ", LocalTime.of(3, 0), LocalTime.of(6, 41), 210),
        })
        .expectDevice("LaptopDevice", "DeviceA", 30, false, new LocalTime[][] { { LocalTime.of(0, 0), LocalTime.of(3, 00) } })
        .expectDevice("LaptopDevice", "DeviceB", 30.5, false, new LocalTime[][] { { LocalTime.of(0, 0), LocalTime.of(6, 40) } })
        .expectDevice("LaptopDevice", "DeviceC", 31, false, new LocalTime[][] { { LocalTime.of(0, 0), LocalTime.of(6, 40) } })
        .expectDevice("LaptopDevice", "DeviceD", 31.5, false, new LocalTime[][] { { LocalTime.of(0, 0), LocalTime.of(6, 40) } })
        .expectDevice("LaptopDevice", "DeviceE", 32, false, new LocalTime[][] { { LocalTime.of(0, 0), LocalTime.of(6, 40) } })
        .expectDevice("LaptopDevice", "DeviceF", 32.5, false, new LocalTime[][] { { LocalTime.of(0, 0), LocalTime.of(6, 40) } })
        .expectDevice("DesktopDevice", "DeviceG", 33, false, new LocalTime[][] { { LocalTime.of(0, 0), LocalTime.of(6, 40) } })
        .expectDevice("DesktopDevice", "DeviceH", 33.5, false, new LocalTime[][] { { LocalTime.of(0, 0), LocalTime.of(6, 40) } })
        .expectDevice("DesktopDevice", "DeviceI", 34, false, new LocalTime[][] { { LocalTime.of(0, 0), LocalTime.of(6, 40) } })
        .expectDevice("DesktopDevice", "DeviceJ", 34.5, false, new LocalTime[][] { { LocalTime.of(3, 0), LocalTime.of(6, 40) } })
    .toString();

        TestHelper plan = new TestHelper()
            .createDevice("LaptopDevice", "DeviceA", 30)
            .createDevice("LaptopDevice", "DeviceB", 30.5)
            .createDevice("LaptopDevice", "DeviceC", 31)
            .createDevice("LaptopDevice", "DeviceE", 32)
            .createDevice("LaptopDevice", "DeviceF", 32.5)
            .createDevice("DesktopDevice", "DeviceH", 33.5)
            .createDevice("DesktopDevice", "DeviceG", 33)
            .createDevice("DesktopDevice", "DeviceI", 34)
            .createDevice("DesktopDevice", "DeviceJ", 34.5)
            .createDevice("LaptopDevice", "DeviceD", 31.5)
            .createSatellite("SovietSatellite", "Satellite1", 10000, 30)
            .scheduleDeviceActivation("DeviceA", LocalTime.of(0, 0), 180)
            .scheduleDeviceActivation("DeviceB", LocalTime.of(0, 0), 400)
            .scheduleDeviceActivation("DeviceC", LocalTime.of(0, 0), 400)
            .scheduleDeviceActivation("DeviceD", LocalTime.of(0, 0), 400)
            .scheduleDeviceActivation("DeviceE", LocalTime.of(0, 0), 400)
            .scheduleDeviceActivation("DeviceF", LocalTime.of(0, 0), 400)
            .scheduleDeviceActivation("DeviceG", LocalTime.of(0, 0), 400)
            .scheduleDeviceActivation("DeviceH", LocalTime.of(0, 0), 400)
            .scheduleDeviceActivation("DeviceI", LocalTime.of(0, 0), 400)
            .scheduleDeviceActivation("DeviceJ", LocalTime.of(3, 0), 220)
            .showWorldState(initialWorldState)
            .simulate(1440)
            .showWorldState(afterADay);
        plan.executeTestPlan();
    }

    @Test
    public void testSpaceX1() 
    {
        String before = new ResponseHelper(LocalTime.of(0, 0))
            .expectSatellite("SpaceXSatellite", "Satellite1", 10000.0, 30, 55.5, new String[] {"DeviceA","DeviceB","DeviceC"})
            .expectSatellite("BlueOriginSatellite", "Satellite2", 10000.0, 20, 141.66, new String[] {"DeviceA","DeviceB","DeviceC"})
            .expectDevice("MobileXPhone", "DeviceA", 30, false, new LocalTime[][] { { LocalTime.of(0, 0), LocalTime.of(6, 40) } })
            .expectDevice("HandheldDevice", "DeviceB", 29, false, new LocalTime[][] { { LocalTime.of(0, 0), LocalTime.of(6, 40) } })
            .expectDevice("HandheldDevice", "DeviceC", 31, false, new LocalTime[][] { { LocalTime.of(0, 0), LocalTime.of(6, 40) } })
            .toString();

        String after = new ResponseHelper(LocalTime.of(0, 0))
            .expectSatellite("SpaceXSatellite", "Satellite1", 10000.0, 37.992, 55.5, new String[] {"DeviceA","DeviceB","DeviceC"},
                new DummyConnection[] {
                    new DummyConnection("DeviceA", LocalTime.of(0, 0), LocalTime.of(6, 41),400),
                }
            )
            .expectSatellite("BlueOriginSatellite", "Satellite2", 10000.0, 40.4, 141.66, new String[] {"DeviceA","DeviceB","DeviceC"},
                new DummyConnection[] {
                    new DummyConnection("DeviceB", LocalTime.of(0, 0), LocalTime.of(6, 41),399),
                    new DummyConnection("DeviceC", LocalTime.of(0, 0), LocalTime.of(6, 41),399),
                }
            )
            .expectDevice("MobileXPhone", "DeviceA", 30, false, new LocalTime[][] { { LocalTime.of(0, 0), LocalTime.of(6, 40) } })
            .expectDevice("HandheldDevice", "DeviceB", 29, false, new LocalTime[][] { { LocalTime.of(0, 0), LocalTime.of(6, 40) } })
            .expectDevice("HandheldDevice", "DeviceC", 31, false, new LocalTime[][] { { LocalTime.of(0, 0), LocalTime.of(6, 40) } })
            .toString();

        TestHelper plan = new TestHelper()
            .createDevice("MobileXPhone", "DeviceA", 30)
            .createDevice("HandheldDevice", "DeviceB", 29)
            .createDevice("HandheldDevice", "DeviceC", 31)
            .createSatellite("SpaceXSatellite", "Satellite1", 10000.0, 30)
            .createSatellite("BlueOriginSatellite", "Satellite2", 10000.0, 20)
            .scheduleDeviceActivation("DeviceA", LocalTime.of(0, 0), 400)
            .scheduleDeviceActivation("DeviceB", LocalTime.of(0, 0), 400)
            .scheduleDeviceActivation("DeviceC", LocalTime.of(0, 0), 400)
            .showWorldState(before)
            .simulate(1440)
            .showWorldState(after);
        plan.executeTestPlan();
    }
}



