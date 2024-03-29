/*
 * Copyright (C) 2024. Lazar Dobrota and Nemanja Radovanovic
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package rs.raf.gym.commons.model;

import lombok.Getter;

@Getter
public enum Role {

    ADMIN("Admin"), //public static final Roles ADMIN = new Roles("Admin");
    MANAGER("Manager"),
    CLIENT("Client"),
    SERVICE("Service");

    private final String name;

    Role(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return this.name;
    }

    public static Role findRole(String name) {
        for (Role role : Role.values())
            if (role.name.equals(name))
                return role;

        return null;
    }

}
