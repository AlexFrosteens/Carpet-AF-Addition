package com.example.extensions;

import carpet.api.settings.Rule;

public class Settings {
    @Rule(
            categories = RuleCategory.AL,
            options = { "never", "item_only", "non_player_only", "always" }
    )
    public static String endGatewayLoadChunk = "always";

    @Rule(
            categories = RuleCategory.AL
    )
    public static Boolean dataCommandEnhance = false;

    @Rule(
            categories = RuleCategory.AL
    )
    public static Boolean dataCommandSimplify = false;

}
