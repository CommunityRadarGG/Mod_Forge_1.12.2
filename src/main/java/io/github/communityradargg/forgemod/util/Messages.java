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
package io.github.communityradargg.forgemod.util;

/**
 * A class containing all texts.
 */
public class Messages {
    public static final String PREFIX = "§8[§cCommunityRadar§8]§r ";
    public static final String MISSING_ARGS = "§cNicht genug Argumente. Gib '/radar' für den korrekten Syntax ein.";
    public static final String NOT_PLAYER = "§cDieser Befehl kann nur von Spielern ausgeführt werden.";
    public static final String INPUT_PROCESSING = "§7Deine Eingabe wird verarbeitet. Dies kann einige Augenblicke benötigen.";

    public static final String HELP =
            "§7§l--------- §eRadar-Hilfe §7§l---------§r\n" +
                    "§e/radar lists §7-> Zeigt die vorhandenen Listen an.\n" +
                    "§e/radar list add <Liste> <Präfix> §7-> Erstellt eine neue Liste.\n" +
                    "§e/radar list prefix <Liste> <Präfix> §7-> Ändert den Präfix einer Liste.\n" +
                    "§e/radar list delete <Liste> §7-> Löscht eine Liste.\n" +
                    "§e/radar list show <Liste> §7-> Zeigt alle Spieler eine Liste an.\n" +
                    "§e/radar check <Name> §7-> Prüft ob sich ein Spieler auf einer Liste befindet.\n" +
                    "§e/radar check * §7-> Prüft ob sich einer der Spieler in der Welt auf einer Liste befindet.\n" +
                    "§e/radar player add <Liste> <Name> <Anmerkungen...> §7-> Fügt einen Spieler zu einer Liste hinzu.\n" +
                    "§e/radar player remove <Liste> <Name> §7-> Entfernt einen Spieler von einer Liste.\n" +
                    "§e/radar help §7-> Zeigt diese Hilfeübersicht an.\n" +
                    "§eEntwickler §7-> MrMystery, BlockyTheDev\n" +
                    "§eVersion §7-> §e{code_version}\n" +
                    "§eWebsite, Downloads & Tutorials §7-> https://community-radar.de/\n" +
                    "§eDiscord §7-> https://discord.community-radar.de/\n" +
                    "§eQuellcode §7-> https://github.com/CommunityRadarGG/Mod_Forge_1.8.9/\n" +
                    "§7§l--------- §eRadar-Hilfe §7§l---------§r";

    /**
     * Translations related to the listen subcommand.
     */
    public static class Lists {
        public static final String FOUND = "§7Listen: §e{lists}";
        public static final String EMPTY = "§7Es wurden§c keine §7Listen gefunden!";
        public static final String PRIVATE = "PRIVAT";
        public static final String PUBLIC = "ÖFFENTLICH";
    }

    /**
     * Translations related to the list subcommand.
     */
    public static class List {
        public static final String CREATE_SUCCESS = "§7Die Liste wurde§a erstellt§7!";
        public static final String CREATE_FAILED = "§cFehler beim Erstellen der Liste. Existiert bereits eine Liste mit diesem Namen?";

        public static final String DELETE_SUCCESS = "§7Diese Liste wurde§c gelöscht§7!";
        public static final String DELETE_FAILED = "§cFehler beim Löschen der Liste. Ist der Name korrekt und handelt es sich um eine private Liste?";

        public static final String SHOW_SUCCESS = "§7Liste: §e{list}§7, Präfix: §e{prefix}§7, Spieler: §e{players}";
        public static final String SHOW_FAILED = "§cFehler beim Anzeigen der Liste. Ist der Name korrekt?";
        public static final String SHOW_EMPTY = "§7Es befindet sich kein Spieler auf dieser Liste.";

        public static final String PREFIX_SUCCESS = "§7Der Präfix wurde zu §e{prefix} §7geändert.";
        public static final String PREFIX_FAILED = "§cFehler beim Ändern des Präfixes.";
    }

    /**
     * Translations related to the check command.
     */
    public static class Check {
        public static final String EVERYONE = "§7Online Spieler in einer Liste:";
        public static final String NOT_FOUND = "§cEs ist kein Spieler online, welcher in einer Liste eingetragen ist.";
        public static final String FAILED = "§7Der angegebene Spieler wurde auf§c keiner §7Liste gefunden.";

        public static final String FOUND = "§7Der Spieler wurde in einer Liste gefunden:";
        public static final String CHECK_ENTRY =
                "§7Präfix: §e{prefix}\n" +
                        "§7Name: §e{name}\n" +
                        "§7Grund: §e{cause}\n" +
                        "§7Hinzugefügt: §e{entryCreationDate}\n" +
                        "§7Letzte Aktualisierung: §e{entryUpdateDate}\n";
    }

    /**
     * Translations related to the player command.
     */
    public static class Player {
        public static final String NAME_INVALID = "§cDer Spieler konnte nicht gefunden werden. Ist der Name korrekt?";
        public static final String NAME_INVALID_BEDROCK = "§cDer Spieler konnte nicht gefunden werden. Da es sich um einen Spieler der Bedrock Version handelt, muss dieser in derselben Welt sein wie du, um zur Liste hinzugefügt werden zu können.";

        public static final String ADD_SUCCESS = "§7Der Spieler wurde zur Liste§a hinzugefügt§7. Handelt es sich um einen Fall für die öffentliche Liste, dann erstelle einen Beitrag im GrieferGames-Forum oder besuche unseren Discord.";
        public static final String ADD_FAILED = "§cDer Spieler konnte nicht hinzugefügt werden. Hast du eine private Liste verwendet? Um eine private Liste zu erstellen, nutze den Befehl '/radar list add <Liste> <Präfix>'.";
        public static final String ADD_IN_LIST = "§7Der Spieler befindet sich bereits auf einer Liste.";

        public static final String REMOVE_SUCCESS = "§7Der Spieler wurde aus der Liste§c entfernt§7.";
        public static final String REMOVE_FAILED = "§cDer Spieler konnte nicht entfernt werden. Hast du eine private Liste verwendet?";
        public static final String REMOVE_NOT_IN_LIST = "§7Der Spieler befindet sich auf§c keiner Liste§7.";
    }
}
