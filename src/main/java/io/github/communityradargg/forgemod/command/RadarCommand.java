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
package io.github.communityradargg.forgemod.command;

import io.github.communityradargg.forgemod.CommunityRadarMod;
import io.github.communityradargg.forgemod.radarlistmanager.RadarList;
import io.github.communityradargg.forgemod.radarlistmanager.RadarListManager;
import io.github.communityradargg.forgemod.radarlistmanager.RadarListVisibility;
import io.github.communityradargg.forgemod.radarlistmanager.RadarListEntry;
import io.github.communityradargg.forgemod.util.Messages;
import io.github.communityradargg.forgemod.util.RadarMessage;
import io.github.communityradargg.forgemod.util.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

/**
 * The class containing all logic for the radar command.
 */
public class RadarCommand extends CommandBase {
    /** {@inheritDoc} */
    @Override
    public @NotNull String getName() {
        return "radar";
    }

    /** {@inheritDoc} */
    @Override
    public @NotNull String getUsage(final @NotNull ICommandSender sender) {
        return "/radar";
    }

    /** {@inheritDoc} */
    @Override
    public @NotNull List<String> getAliases() {
        return Arrays.asList("communityradar", "scammer", "trustedmm", "mm");
    }

    /** {@inheritDoc} */
    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }

    /** {@inheritDoc} */
    @Override
    public boolean checkPermission(final @NotNull MinecraftServer server, final @NotNull ICommandSender sender) {
        return sender instanceof EntityPlayer;
    }

    /** {@inheritDoc} */
    @Override
    public void execute(final @NotNull MinecraftServer server, final @NotNull ICommandSender sender, final String @NotNull [] args) {
        if (!(sender instanceof EntityPlayer)) {
            sender.sendMessage(new RadarMessage.RadarMessageBuilder(Messages.NOT_PLAYER)
                    .build().toChatComponentText());
            return;
        }

        final EntityPlayer player = (EntityPlayer) sender;
        if (args.length == 0) {
            handleHelpSubcommand(player);
            return;
        }

        switch (args[0].toUpperCase(Locale.ENGLISH)) {
            case "CHECK":
                handleCheckSubcommand(player, args);
                break;
            case "LIST":
                handleListSubcommand(player, args);
                break;
            case "PLAYER":
                handlePlayerSubcommand(player, args);
                break;
            case "LISTS":
                handleListsSubcommand(player);
                break;
            default:
                handleHelpSubcommand(player);
                break;
        }
    }

    /**
     * Handles the player subcommand.
     *
     * @param player The player, which executed the subcommand.
     * @param args The arguments passed to the main command.
     */
    private void handlePlayerSubcommand(final @NotNull EntityPlayer player, final @NotNull String[] args) {
        if (args.length < 2) {
            // missing arguments
            player.sendMessage(new RadarMessage.RadarMessageBuilder(Messages.MISSING_ARGS)
                    .build().toChatComponentText());
            return;
        }

        switch (args[1].toUpperCase(Locale.ENGLISH)) {
            case "ADD":
                handlePlayerAddSubcommand(player, args);
                break;
            case "REMOVE":
                handlePlayerRemoveSubcommand(player, args);
                break;
            default:
                handleHelpSubcommand(player);
                break;
        }
    }

    /**
     * Handles the player - add subcommand.
     *
     * @param player The player, which executed the subcommand.
     * @param args The arguments passed to the main command.
     */
    private void handlePlayerAddSubcommand(final @NotNull EntityPlayer player, final @NotNull String[] args) {
        if (args.length < 5) {
            // missing arguments
            player.sendMessage(new RadarMessage.RadarMessageBuilder(Messages.MISSING_ARGS)
                    .build().toChatComponentText());
            return;
        }

        final RadarListManager listManager = CommunityRadarMod.getListManager();
        final Optional<RadarList> listOptional = listManager.getRadarList(args[2]);
        if (!listOptional.isPresent()) {
            // list not existing
            player.sendMessage(new RadarMessage.RadarMessageBuilder(Messages.Player.ADD_FAILED)
                    .build().toChatComponentText());
            return;
        }

        player.sendMessage(new RadarMessage.RadarMessageBuilder(Messages.INPUT_PROCESSING)
                .build().toChatComponentText());
        Utils.getUUID(args[3]).thenAccept(uuidOptional -> {
            if (!uuidOptional.isPresent()) {
                // player uuid could not be fetched
                player.sendMessage(new RadarMessage.RadarMessageBuilder(args[3].startsWith("!") ? Messages.Player.NAME_INVALID_BEDROCK : Messages.Player.NAME_INVALID)
                        .build().toChatComponentText());
                return;
            }

            final UUID uuid = uuidOptional.get();
            if (listOptional.get().isInList(uuid)) {
                // player already on list
                player.sendMessage(new RadarMessage.RadarMessageBuilder(Messages.Player.ADD_IN_LIST)
                        .build().toChatComponentText());
                return;
            }

            final StringBuilder notes = new StringBuilder();
            for (int i = 4; i < args.length; i++) {
                notes.append(args[i]).append(" ");
            }

            if (!CommunityRadarMod.getListManager().addRadarListEntry(args[2], uuid, args[3], notes.substring(0, notes.length() - 1))) {
                // list is not private
                player.sendMessage(new RadarMessage.RadarMessageBuilder(Messages.Player.ADD_FAILED)
                        .build().toChatComponentText());
                return;
            }

            player.sendMessage(new RadarMessage.RadarMessageBuilder(Messages.Player.ADD_SUCCESS)
                    .build().toChatComponentText());
            Utils.updatePlayerByUuid(uuid, listManager.getExistingPrefixes());
        });
    }

    /**
     * Handles the player - remove subcommand.
     *
     * @param player The player, which executed the subcommand.
     * @param args The arguments passed to the main command.
     */
    private void handlePlayerRemoveSubcommand(final @NotNull EntityPlayer player, final @NotNull String[] args) {
        if (args.length != 4) {
            // missing arguments
            player.sendMessage(new RadarMessage.RadarMessageBuilder(Messages.MISSING_ARGS)
                    .build().toChatComponentText());
            return;
        }

        final RadarListManager listManager =  CommunityRadarMod.getListManager();
        final Optional<RadarList> listOptional = listManager.getRadarList(args[2]);
        if (!listOptional.isPresent()) {
            // list is not existing
            player.sendMessage(new RadarMessage.RadarMessageBuilder(Messages.Player.REMOVE_FAILED)
                    .build().toChatComponentText());
            return;
        }

        player.sendMessage(new RadarMessage.RadarMessageBuilder(Messages.INPUT_PROCESSING)
                .build().toChatComponentText());
        final RadarList list = listOptional.get();
        Utils.getUUID(args[3]).thenAccept(uuidOptional -> {
            if (!uuidOptional.isPresent()) {
                // player uuid could not be fetched
                player.sendMessage(new RadarMessage.RadarMessageBuilder(args[3].startsWith("!") ? Messages.Player.NAME_INVALID_BEDROCK : Messages.Player.NAME_INVALID)
                        .build().toChatComponentText());
                return;
            }

            final UUID uuid = uuidOptional.get();
            if (!list.isInList(uuid)) {
                // player uuid not on list
                player.sendMessage(new RadarMessage.RadarMessageBuilder(Messages.Player.REMOVE_NOT_IN_LIST)
                        .build().toChatComponentText());
                return;
            }

            list.getPlayerMap().remove(uuid);
            Utils.updatePlayerByUuid(uuid, listManager.getExistingPrefixes());
            player.sendMessage(new RadarMessage.RadarMessageBuilder(Messages.Player.REMOVE_SUCCESS)
                    .build().toChatComponentText());
        });
    }

    /**
     * Handles the list subcommand.
     *
     * @param player The player, which executed the subcommand.
     * @param args The arguments passed to the main command.
     */
    private void handleListSubcommand(final @NotNull EntityPlayer player, final @NotNull String[] args) {
        if (args.length < 2) {
            // missing arguments
            player.sendMessage(new RadarMessage.RadarMessageBuilder(Messages.MISSING_ARGS)
                    .build().toChatComponentText());
            return;
        }

        switch (args[1].toUpperCase(Locale.ENGLISH)) {
            case "ADD":
                handleListAddSubcommand(player, args);
                break;
            case "PREFIX":
                handleListPrefixSubcommand(player, args);
                break;
            case "DELETE":
                handleListDeleteSubcommand(player, args);
                break;
            case "SHOW":
                handleListShowSubcommand(player, args);
                break;
            default:
                handleHelpSubcommand(player);
                break;
        }
    }

    /**
     * Handles the list - add subcommand.
     *
     * @param player The player, which executed the subcommand.
     * @param args The arguments passed to the main command.
     */
    private void handleListAddSubcommand(final @NotNull EntityPlayer player, final @NotNull String[] args) {
        if (args.length != 4) {
            // missing arguments
            player.sendMessage(new RadarMessage.RadarMessageBuilder(Messages.MISSING_ARGS)
                    .build().toChatComponentText());
            return;
        }

        if (CommunityRadarMod.getListManager().getRadarList(args[2]).isPresent()) {
            // list already existing
            player.sendMessage(new RadarMessage.RadarMessageBuilder(Messages.List.CREATE_FAILED)
                    .build().toChatComponentText());
            return;
        }

        if (!CommunityRadarMod.getListManager().registerPrivateList(args[2], args[3])) {
            // list could not be registered
            player.sendMessage(new RadarMessage.RadarMessageBuilder(Messages.List.CREATE_FAILED)
                    .build().toChatComponentText());
            return;
        }

        player.sendMessage(new RadarMessage.RadarMessageBuilder(Messages.List.CREATE_SUCCESS)
                .build().toChatComponentText());
    }

    /**
     * Handles the list - delete subcommand.
     *
     * @param player The player, which executed the subcommand.
     * @param args The arguments passed to the main command.
     */
    private void handleListDeleteSubcommand(final @NotNull EntityPlayer player, final @NotNull String[] args) {
        if (args.length != 3) {
            // missing arguments
            player.sendMessage(new RadarMessage.RadarMessageBuilder(Messages.MISSING_ARGS)
                    .build().toChatComponentText());
            return;
        }

        final RadarListManager listManager = CommunityRadarMod.getListManager();
        final Set<String> oldPrefixes = listManager.getExistingPrefixes();
        final Set<UUID> oldUuids = listManager.getRadarList(args[2])
                .map(radarList -> radarList.getPlayerMap().keySet())
                .orElse(Collections.emptySet());

        if (!listManager.unregisterList(args[2])) {
            // list is not existing, list is not private, file cannot be deleted
            player.sendMessage(new RadarMessage.RadarMessageBuilder(Messages.List.DELETE_FAILED)
                    .build().toChatComponentText());
            return;
        }

        oldUuids.forEach(uuid -> Utils.updatePlayerByUuid(uuid, oldPrefixes));
        player.sendMessage(new RadarMessage.RadarMessageBuilder(Messages.List.DELETE_SUCCESS)
                .build().toChatComponentText());
    }

    /**
     * Handles the list - show subcommand.
     *
     * @param player The player, which executed the subcommand.
     * @param args The arguments passed to the main command.
     */
    private void handleListShowSubcommand(final @NotNull EntityPlayer player, final @NotNull String[] args) {
        if (args.length != 3) {
            // missing arguments
            player.sendMessage(new RadarMessage.RadarMessageBuilder(Messages.MISSING_ARGS)
                    .build().toChatComponentText());
            return;
        }

        final Optional<RadarList> listOptional = CommunityRadarMod.getListManager().getRadarList(args[2]);
        if (!listOptional.isPresent()) {
            // list is not existing
            player.sendMessage(new RadarMessage.RadarMessageBuilder(Messages.List.SHOW_FAILED)
                    .build().toChatComponentText());
            return;
        }

        final RadarList list = listOptional.get();
        if (list.getPlayerMap().isEmpty()) {
            // list is empty
            player.sendMessage(new RadarMessage.RadarMessageBuilder(Messages.List.SHOW_EMPTY)
                    .build().toChatComponentText());
            return;
        }

        final StringBuilder players = new StringBuilder();
        list.getPlayerMap().values().forEach(value -> players.append(value.name()).append(", "));
        player.sendMessage(new RadarMessage.RadarMessageBuilder(Messages.List.SHOW_SUCCESS)
                .replace("{list}", list.getNamespace())
                .replaceWithColorCodes("{prefix}", listOptional.get().getPrefix())
                .replace("{players}", players.substring(0, players.length() - 2))
                .build().toChatComponentText());
    }

    /**
     * Handles the list - prefix subcommand.
     *
     * @param player The player, which executed the subcommand.
     * @param args The arguments passed to the main command.
     */
    private void handleListPrefixSubcommand(final @NotNull EntityPlayer player, final @NotNull String[] args) {
        if (args.length != 4) {
            // missing arguments
            player.sendMessage(new RadarMessage.RadarMessageBuilder(Messages.MISSING_ARGS)
                    .build().toChatComponentText());
            return;
        }

        final RadarListManager listManager = CommunityRadarMod.getListManager();
        final Optional<RadarList> listOptional = listManager.getRadarList(args[2]);
        if (!listOptional.isPresent()) {
            // list is not existing
            player.sendMessage(new RadarMessage.RadarMessageBuilder(Messages.List.PREFIX_FAILED)
                    .build().toChatComponentText());
            return;
        }

        final RadarList list = listOptional.get();
        final Set<String> oldPrefixes = listManager.getExistingPrefixes();
        list.setPrefix(args[3]);
        list.saveList();
        list.getPlayerMap().keySet().forEach(uuid -> Utils.updatePlayerByUuid(uuid, oldPrefixes));

        player.sendMessage(new RadarMessage.RadarMessageBuilder(Messages.List.PREFIX_SUCCESS)
                .replaceWithColorCodes("{prefix}", args[3])
                .build().toChatComponentText());
    }

    /**
     * Handles the check subcommand.
     *
     * @param player The player, which executed the subcommand.
     * @param args The arguments passed to the main command.
     */
    private void handleCheckSubcommand(final @NotNull EntityPlayer player, final @NotNull String[] args) {
        if (args.length != 2) {
            // missing arguments
            player.sendMessage(new RadarMessage.RadarMessageBuilder(Messages.MISSING_ARGS)
                    .build().toChatComponentText());
            return;
        }

        if (args[1].equalsIgnoreCase("*")) {
            // check all argument
            handleCheckAllSubcommand(player);
            return;
        }
        handleCheckPlayerSubcommand(player, args);
    }

    /**
     * Handles the check - player subcommand.
     *
     * @param player The player, which executed the subcommand.
     * @param args The arguments passed to the main command.
     */
    private void handleCheckPlayerSubcommand(final @NotNull EntityPlayer player, final @NotNull String[] args) {
        player.sendMessage(new RadarMessage.RadarMessageBuilder(Messages.INPUT_PROCESSING)
                .build().toChatComponentText());
        Utils.getUUID(args[1]).thenAccept(checkPlayerOptional -> {
            if (!checkPlayerOptional.isPresent()) {
                // player uuid could not be fetched
                player.sendMessage(new RadarMessage.RadarMessageBuilder(Messages.Check.FAILED)
                        .build().toChatComponentText());
                return;
            }

            final Optional<RadarListEntry> entryOptional = CommunityRadarMod.getListManager().getRadarListEntry(checkPlayerOptional.get());
            if (!entryOptional.isPresent()) {
                // player uuid is on no list
                player.sendMessage(new RadarMessage.RadarMessageBuilder(Messages.Check.FAILED)
                        .build().toChatComponentText());
                return;
            }

            final RadarListEntry entry = entryOptional.get();
            player.sendMessage(new RadarMessage.RadarMessageBuilder(Messages.Check.FOUND + "\n" + Messages.Check.CHECK_ENTRY)
                    .replaceWithColorCodes("{prefix}", CommunityRadarMod.getListManager().getPrefix(entry.uuid()))
                    .replace("{name}", entry.name())
                    .replace("{cause}", entry.cause())
                    .replace("{entryCreationDate}", Utils.formatDateTime(entry.entryCreationDate()))
                    .replace("{entryUpdateDate}", Utils.formatDateTime(entry.entryUpdateDate()))
                    .build().toChatComponentText());
        });
    }

    /**
     * Handles the check - all subcommand.
     *
     * @param player The player, which executed the subcommand.
     */
    private void handleCheckAllSubcommand(final @NotNull EntityPlayer player) {
        boolean anyPlayerFound = false;
        final NetHandlerPlayClient connection = Minecraft.getMinecraft().getConnection();
        if (connection != null) {
            for (final NetworkPlayerInfo networkPlayer : connection.getPlayerInfoMap()) {
                if (networkPlayer.getGameProfile().getId() == null) {
                    continue;
                }

                final Optional<RadarListEntry> listEntryOptional = CommunityRadarMod.getListManager()
                        .getRadarListEntry(networkPlayer.getGameProfile().getId());
                if (!listEntryOptional.isPresent()) {
                    // player uuid is on no list
                    continue;
                }

                if (!anyPlayerFound) {
                    player.sendMessage(new RadarMessage.RadarMessageBuilder(Messages.Check.EVERYONE)
                            .build().toChatComponentText());
                    anyPlayerFound = true;
                }

                final RadarListEntry entry = listEntryOptional.get();
                player.sendMessage(new RadarMessage.RadarMessageBuilder(Messages.Check.CHECK_ENTRY)
                        .replaceWithColorCodes("{prefix}", CommunityRadarMod.getListManager().getPrefix(entry.uuid()))
                        .replace("{name}", entry.name())
                        .replace("{cause}", entry.cause())
                        .replace("{entryCreationDate}", Utils.formatDateTime(entry.entryCreationDate()))
                        .replace("{entryUpdateDate}", Utils.formatDateTime(entry.entryUpdateDate()))
                        .build().toChatComponentText());
            }
        }

        if (!anyPlayerFound) {
            player.sendMessage(new RadarMessage.RadarMessageBuilder(Messages.Check.NOT_FOUND)
                    .build().toChatComponentText());
        }
    }

    /**
     * Handles the help subcommand.
     *
     * @param player The player, which executed the subcommand.
     */
    private void handleHelpSubcommand(final @NotNull EntityPlayer player) {
        player.sendMessage(new RadarMessage.RadarMessageBuilder(Messages.HELP)
                .replace("{code_version}", CommunityRadarMod.VERSION)
                .excludePrefix()
                .build().toChatComponentText());
    }

    /**
     * Handles the lists subcommand.
     *
     * @param player The player, which executed the subcommand.
     */
    private void handleListsSubcommand(final @NotNull EntityPlayer player) {
        final StringBuilder listsText = new StringBuilder();
        for (final String namespace : CommunityRadarMod.getListManager().getNamespaces()) {
            CommunityRadarMod.getListManager().getRadarList(namespace)
                    .ifPresent(radarList -> listsText.append("§e").append(namespace).append(" §7(§c")
                    .append(radarList.getRadarListVisibility() == RadarListVisibility.PRIVATE ? Messages.Lists.PRIVATE : Messages.Lists.PUBLIC)
                    .append("§7)").append(", "));
        }

        if (listsText.length() > 0) {
            // players on the list
            player.sendMessage(new RadarMessage.RadarMessageBuilder(Messages.Lists.FOUND)
                    .replace("{lists}", listsText.substring(0, listsText.length() - 2))
                    .build().toChatComponentText());
        } else {
            // list is empty
            player.sendMessage(new RadarMessage.RadarMessageBuilder(Messages.Lists.EMPTY)
                    .build().toChatComponentText());
        }
    }
}
