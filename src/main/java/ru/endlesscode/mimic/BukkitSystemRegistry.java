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

package ru.endlesscode.mimic;

import com.google.common.annotations.VisibleForTesting;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.ServicesManager;
import org.jetbrains.annotations.NotNull;
import ru.endlesscode.mimic.api.system.PlayerSystem;
import ru.endlesscode.mimic.api.system.SystemFactory;
import ru.endlesscode.mimic.api.system.registry.MetadataAdapter;
import ru.endlesscode.mimic.api.system.registry.SystemNotFoundException;
import ru.endlesscode.mimic.api.system.registry.SystemPriority;
import ru.endlesscode.mimic.api.system.registry.SystemRegistry;

/**
 * Implementation of system registry for bukkit.
 * Using {@link org.bukkit.plugin.ServicesManager}
 *
 * @author Osip Fatkullin
 * @since 1.0
 */
public class BukkitSystemRegistry extends SystemRegistry {
    private final Plugin plugin;
    private final ServicesManager servicesManager;

    BukkitSystemRegistry(Plugin plugin, ServicesManager servicesManager) {
        this.plugin = plugin;
        this.servicesManager = servicesManager;
    }

    /**
     * Registers approved subsystem factory.
     *
     * @param factoryClass     Class of the factory
     * @param subsystemFactory Concrete subsystem factory
     * @param meta             Subsystem metadata
     */
    @Override
    protected <FactoryT extends SystemFactory<? extends PlayerSystem>> void registerSystem(
            @NotNull Class<FactoryT> factoryClass,
            @NotNull FactoryT subsystemFactory,
            @NotNull MetadataAdapter meta) {
        ServicePriority priority = servicePriorityFromSystem(meta.getPriority());
        this.servicesManager.register(factoryClass, subsystemFactory, this.plugin, priority);
    }

    /**
     * Utility method that returns bukkit analog for {@code SystemPriority}
     *
     * @param priority System priority
     * @return Same service priority
     */
    @VisibleForTesting
    static @NotNull ServicePriority servicePriorityFromSystem(@NotNull SystemPriority priority) {
        int priorityIndex = priority.ordinal();
        return ServicePriority.values()[priorityIndex];
    }

    /**
     * Gets system factory by factory class.
     *
     * @param factoryClass Factory class
     * @return System factory
     * @throws SystemNotFoundException If factory for needed system not found in registry
     * @implSpec Never return {@code null}. Throw exception instead.
     */
    @NotNull
    @Override
    public <SystemT extends PlayerSystem> SystemFactory<SystemT> getFactory(
            @NotNull Class<? extends SystemFactory<SystemT>> factoryClass)
            throws SystemNotFoundException {
        RegisteredServiceProvider<? extends SystemFactory<SystemT>> systemProvider
                = this.servicesManager.getRegistration(factoryClass);
        if (systemProvider == null) {
            throw new SystemNotFoundException(String.format("No one system '%s' found", factoryClass.getName()));
        }

        return systemProvider.getProvider();
    }

    /**
     * Unregisters all subsystems
     *
     * @apiNote Use it before plugin disabling
     */
    @Override
    public void unregisterAllSubsystems() {
        servicesManager.unregisterAll(this.plugin);
    }

    /**
     * Unregister specified factory
     *
     * @param factory The factory
     */
    @Override
    public <SubsystemT extends PlayerSystem> void unregisterFactory(
            @NotNull SystemFactory<? extends SubsystemT> factory) {
        servicesManager.unregister(factory);
    }
}
