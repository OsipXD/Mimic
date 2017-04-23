/*
 * This file is part of BukkitMimic.
 * Copyright (C) 2017 Osip Fatkullin
 *
 * BukkitMimic is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * BukkitMimic is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with BukkitMimic.  If not, see <http://www.gnu.org/licenses/>.
 */

package ru.endlesscode.mimic.system;

import org.bukkit.entity.Player;
import org.junit.Test;
import org.powermock.reflect.Whitebox;
import ru.endlesscode.mimic.api.ref.ExistingWeakReference;
import ru.endlesscode.mimic.bukkit.BukkitTest;

import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Osip Fatkullin
 * @since 1.0
 */
public class BukkitClassSystemTest extends BukkitTest {
    @Test
    public void testInitializedCopyMustHaveRightHandler() throws Exception {
        BukkitClassSystem classSystem = mock(BukkitClassSystem.class);
        Whitebox.setInternalState(classSystem, "playerRef", new ExistingWeakReference<>(this.player));
        when(classSystem.getHandler()).thenCallRealMethod();

        Player handler = classSystem.getHandler();
        assertSame(this.player, handler);
    }
}