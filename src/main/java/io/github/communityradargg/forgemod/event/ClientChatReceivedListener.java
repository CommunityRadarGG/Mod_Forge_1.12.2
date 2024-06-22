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
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.jetbrains.annotations.NotNull;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A class containing a listener for client chat receiving.
 */
public class ClientChatReceivedListener {
    /**
     * A pattern matching private messages (in and out) and payments (in and out) as well as global and plot chat messages with the player name (nicked, bedrock and java) as only group.
     */
    private static final Pattern pattern = Pattern.compile("[A-Za-z\\-+]+\\s\\u2503\\s(~?!?\\w{1,16})");
    private final CommunityRadarMod communityRadarMod;

    /**
     * Constructs the class {@link ClientChatReceivedListener}.
     *
     * @param communityRadarMod An instance of the {@link CommunityRadarMod} class.
     */
    public ClientChatReceivedListener(final @NotNull CommunityRadarMod communityRadarMod) {
        this.communityRadarMod = communityRadarMod;
    }

    /**
     * The listener for the {@link ClientChatReceivedEvent} event.
     *
     * @param event The event.
     */
    @SubscribeEvent
    @SuppressWarnings("unused") // event listener
    public void onClientChatReceived(final ClientChatReceivedEvent event) {
        if (!communityRadarMod.isOnGrieferGames()) {
            return;
        }

        final Matcher matcher = pattern.matcher(event.getMessage().getUnformattedText());
        if (!matcher.find()) {
            return;
        }

        final String playerName = matcher.group(1);
        if (playerName.startsWith("~")) {
            // nicked player
            return;
        }

        Utils.getUUID(playerName).thenAccept(uuid -> {
            if (uuid.isPresent() && CommunityRadarMod.getListManager().isInList(uuid.get())) {
                event.setMessage(new TextComponentString(CommunityRadarMod.getListManager().getPrefix(uuid.get()).replace("&", "§"))
                        .appendText(" §r")
                        .appendText(event.getMessage().getFormattedText()));
            }
        });
    }
}
