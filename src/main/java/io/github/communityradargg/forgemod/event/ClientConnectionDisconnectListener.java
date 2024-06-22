/*
 * Copyright 2024 - present CommunityRadarGG <https://community-radar.de/>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.communityradargg.forgemod.event;

import io.github.communityradargg.forgemod.CommunityRadarMod;
import io.github.communityradargg.forgemod.util.Utils;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import org.jetbrains.annotations.NotNull;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

/**
 * A class containing listeners for player connect and disconnect from and to servers.
 */
public class ClientConnectionDisconnectListener {
    private final CommunityRadarMod communityRadarMod;

    /**
     * Constructs the class {@link ClientConnectionDisconnectListener}.
     *
     * @param communityRadarMod An instance of the {@link CommunityRadarMod} class.
     */
    public ClientConnectionDisconnectListener(final @NotNull CommunityRadarMod communityRadarMod) {
        this.communityRadarMod = communityRadarMod;
    }

    /**
     * The listener for the {@link FMLNetworkEvent.ClientConnectedToServerEvent} event.
     *
     * @param event The event.
     */
    @SubscribeEvent
    @SuppressWarnings("unused") // called by forge
    public void onFMLNetworkClientConnectedToServer(final FMLNetworkEvent.ClientConnectedToServerEvent event) {
        if (event.isLocal()) {
            return;
        }

        final SocketAddress socketAddress = event.getManager().getRemoteAddress();
        if (!(socketAddress instanceof InetSocketAddress)) {
            return;
        }

        final String hostname = ((InetSocketAddress) socketAddress).getHostName();
        if (Utils.isGrieferGamesHostName(hostname)) {
            communityRadarMod.setOnGrieferGames(true);
            return;
        }
        communityRadarMod.setOnGrieferGames(false);
    }

    /**
     * The listener for the {@link FMLNetworkEvent.ClientDisconnectionFromServerEvent} event.
     *
     * @param event The event.
     */
    @SubscribeEvent
    @SuppressWarnings("unused") // called by forge
    public void onFMLNetworkClientDisconnectionFromServer(final FMLNetworkEvent.ClientDisconnectionFromServerEvent event) {
        communityRadarMod.setOnGrieferGames(false);
    }
}