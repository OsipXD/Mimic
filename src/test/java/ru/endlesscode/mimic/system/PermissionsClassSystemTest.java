/*
 * This file is part of BukkitMimic.
 * Copyright (C) 2017 Osip Fatkullin
 *
 * BukkitMimic is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * BukkitMimic is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with BukkitMimic.  If not, see <http://www.gnu.org/licenses/>.
 */

package ru.endlesscode.mimic.system;

import org.bukkit.permissions.PermissionAttachmentInfo;
import org.junit.Before;
import org.junit.Test;
import ru.endlesscode.mimic.bukkit.BukkitTest;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Osip Fatkullin
 * @since 1.0
 */
public class PermissionsClassSystemTest extends BukkitTest {
    private BukkitClassSystem classSystem;

    @Before
    public void setUp() {
        super.setUp();

        try {
            BukkitClassSystem permClassSystem = new PermissionsClassSystem();
            this.classSystem = permClassSystem.initializedCopy(this.player);
        } catch (CloneNotSupportedException e) {
            fail("System must support cloning");
        }
    }

    @Test
    public void testHasRequiredClassMustReturnTrue() throws Exception {
        this.addClasses("first", "second", "third");

        assertTrue(this.classSystem.hasRequiredClass("first"));
        assertTrue(this.classSystem.hasRequiredClass("First"));
        assertTrue(this.classSystem.hasRequiredClass("second"));
        assertTrue(this.classSystem.hasRequiredClass("Second"));
        assertTrue(this.classSystem.hasRequiredClass("third"));
        assertTrue(this.classSystem.hasRequiredClass("Third"));
    }

    @Test
    public void testHasRequiredClassMustReturnFalse() throws Exception {
        this.addClasses("first", "second");

        assertFalse(this.classSystem.hasRequiredClass("third"));
        assertFalse(this.classSystem.hasRequiredClass("FirstClass"));
        assertFalse(this.classSystem.hasRequiredClass("Sec"));
        assertFalse(this.classSystem.hasRequiredClass(""));
    }

    private void addClasses(String... classes) {
        for (String theClass : classes) {
            when(this.player.hasPermission(PermissionsClassSystem.PERMISSION_PREFIX + theClass)).thenReturn(true);
        }
    }

    @Test
    public void testGetClassesReturnList() throws Exception {
        List<PermissionAttachmentInfo> perms = Arrays.asList(
                createClassPermissionMock("first", true),
                createClassPermissionMock("second", false),
                createClassPermissionMock("third", true),
                createPermissionMock("mimic.other.permission", true));
        when(this.player.getEffectivePermissions()).thenReturn(new HashSet<>(perms));

        List<String> expected = Arrays.asList("first", "third");
        List<String> actual = this.classSystem.getClasses();
        assertTrue(expected.containsAll(actual) && actual.containsAll(expected));
    }

    private PermissionAttachmentInfo createClassPermissionMock(String theClass, boolean value) {
        String classPermission = PermissionsClassSystem.PERMISSION_PREFIX + theClass;
        return createPermissionMock(classPermission, value);
    }

    private PermissionAttachmentInfo createPermissionMock(String permission, boolean value) {
        PermissionAttachmentInfo perm = mock(PermissionAttachmentInfo.class);
        when(perm.getPermission()).thenReturn(permission);
        when(perm.getValue()).thenReturn(value);

        return perm;
    }

    @Test
    public void testIsEnabledAlwaysReturnTrue() throws Exception {
        assertTrue(this.classSystem.isEnabled());
    }

    @Test
    public void testNameAlways_PermissionClassSystem() throws Exception {
        assertEquals("Permission Class System", this.classSystem.getName());
    }
}