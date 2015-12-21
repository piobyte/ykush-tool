package de.piobyte;

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

public enum State {
    ON_1((byte) 0x11), ON_2((byte) 0x12), ON_3((byte) 0x13), ON_ALL((byte) 0x1a), OFF_1((byte) 0x01), OFF_2((byte) 0x02), OFF_3((byte) 0x03), OFF_ALL((byte) 0x0a);

    private final byte value;

    State(byte value) {
        this.value = value;
    }

    public byte getValue() {
        return value;
    }
}
