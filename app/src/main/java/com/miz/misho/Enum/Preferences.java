package com.miz.misho.Enum;

public enum Preferences {
    SEARCH_TYPE("search_preference"),
    POP_UPs_ROMAJI("pop_ups_romaji"),
    POP_UPS_JISHONOC("pop_ups_jishonoc"),
    CB_DARKTHEME("cb_usedt"),
    BG_COLOR_NORMAL("bg_color_normal"),
    FONT_COLOR_NORMAL("font_color_normal"),
    BAR_COLOR_NORMAL("bar_color_normal"),
    AUTO_SEARCH("auto_search"),
    MAX_RESULTS("max_results");

    private final String string;

    Preferences(final String s) {
        this.string = s;
    }

    @Override
    public String toString() {
        return string;
    }


}