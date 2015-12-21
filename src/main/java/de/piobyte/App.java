package de.piobyte;

import org.hid4java.HidDevice;
import org.hid4java.HidManager;
import org.hid4java.HidServices;
import org.hid4java.HidServicesListener;
import org.hid4java.event.HidServicesEvent;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

import java.io.IOException;

/*
 Copyright (C) 20015 piobyte GmbH

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 */

public class App {

    private static final short VENDOR_ID = 0x4d8;
    private static final short PRODUCT_ID = 0xfffff2f7;
    static final int PACKET_LENGTH = 64;

    @Option(name = "-a", usage = "activate port with -a [1-3][4 for all]", required = false, forbids = "-d")
    private int activatePort;
    @Option(name = "-d", usage = "deactivate port with -d [1-3][4 for all]", required = false, forbids = "-a")
    private int deactivatePort;

    public static void main(String[] args) {
        try {
            new App().doMain(args);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void doMain(String[] args) throws IOException {
        CmdLineParser parser = new CmdLineParser(this);
        try {
            parser.parseArgument(args);
            State state = null;

            if (activatePort < 0 || activatePort > 4) {
                System.err.println("Usage -a [1-3][4 for all]");
                return;
            }

            if (deactivatePort < 0 || deactivatePort > 4) {
                System.err.println("Usage -d [1-3][4 for all]");
                return;
            }

            if (activatePort > 0) {
                switch (activatePort) {
                    case 1: {
                        state = State.ON_1;
                    }
                    break;
                    case 2: {
                        state = State.ON_2;
                    }
                    break;
                    case 3: {
                        state = State.ON_3;
                    }
                    break;
                    case 4: {
                        state = State.ON_ALL;
                    }
                    break;
                    case -1: {
                        System.err.println("Check argument -p (number of usb port (1-3) or 4 for all)");
                        return;
                    }
                }
            } else if (deactivatePort > 0) {
                switch (deactivatePort) {
                    case 1: {
                        state = State.OFF_1;
                    }
                    break;
                    case 2: {
                        state = State.OFF_2;
                    }
                    break;
                    case 3: {
                        state = State.OFF_3;
                    }
                    break;
                    case 4: {
                        state = State.OFF_ALL;
                    }
                    break;
                    case -1: {
                        System.err.println("Check argument -p (number of usb port (1-3) or 4 for all)");
                        return;
                    }
                }
            }


            final HidServices hidServices = HidManager.getHidServices();
            hidServices.addHidServicesListener(new HidServicesListener() {
                public void hidDeviceAttached(HidServicesEvent hidServicesEvent) {
                    System.out.println("Device attached: " + hidServicesEvent);
                }

                public void hidDeviceDetached(HidServicesEvent hidServicesEvent) {
                    System.err.println("Device detached: " + hidServicesEvent);
                }

                public void hidFailure(HidServicesEvent hidServicesEvent) {
                    System.err.println("Device errored: " + hidServicesEvent);
                }
            });

            HidDevice ykushDevice = hidServices.getHidDevice(VENDOR_ID, PRODUCT_ID, null);

            if (ykushDevice == null) {
                System.err.println("YKUSH not found.");
                return;
            }

            byte[] message = new byte[PACKET_LENGTH];
            message[0] = state.getValue();
            message[1] = state.getValue();

            int val = ykushDevice.write(message, PACKET_LENGTH, (byte) 0);
            if (val != -1) {
                System.out.println("operation successful device returned " + val);
            } else {
                System.err.println(ykushDevice.getLastErrorMessage());
            }

            if (ykushDevice.isOpen()) {
                ykushDevice.close();
            }

        } catch (CmdLineException e) {
            System.err.println(e.getMessage());
            parser.printUsage(System.err);
        }
    }

    private static void listAllHIDDevices() {
        final HidServices hidServices = HidManager.getHidServices();
        for (HidDevice hidDevice : hidServices.getAttachedHidDevices()) {
            System.out.println("HID device: " + hidDevice);
        }
    }

}