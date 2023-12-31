package io.model.textures;

public enum Texture {
    TILE_DARK(222.f / 256.f),
    TILE_LIGHT(222.f / 256.f),
    TILE_HIGHLIGHT(148.f / 256.f),
    FOG(180.f / 275.f),
    FOG_TALL(283.f / 328.f),
    FOG_LEFT(282.f / 328.f),
    FOG_RIGHT(282.f / 325.f),
    PAWN(152f / 80f),
    WARRIOR(137f / 106f),
    MARSH_WIGGLE(180f / 130f),
    BASE(255f / 208f),
    TOWER(241f / 158f),
    SHADOW(149.f / 256.f),
    ATTACK_MARKER(1.0f),
    ARROW_NONE(74.f / 128.f),
    ARROW_SW_NE(74.f / 128.f),
    ARROW_SE_NW(74.f / 128.f),
    ARROW_START_NE(74.f / 128.f),
    ARROW_START_SE(74.f / 128.f),
    ARROW_START_NW(74.f / 128.f),
    ARROW_START_SW(74.f / 128.f),
    ARROW_END_NE(74.f / 128.f),
    ARROW_END_SE(74.f / 128.f),
    ARROW_END_NW(74.f / 128.f),
    ARROW_END_SW(74.f / 128.f),
    ARROW_SE_NE(74.f / 128.f),
    ARROW_SW_SE(74.f / 128.f),
    ARROW_SW_NW(74.f / 128.f),
    ARROW_NW_NE(74.f / 128.f),
    BUTTON_SMALL(181f / 450f),
    BUTTON_SMALL_PRESSED(181f / 450f),
    SCROLL_TOP(34f / 62f),
    SCROLL_MID(33f / 62f),
    SCROLL_BOT(33f / 62f),
    SCROLL_BACK_TOP(33f / 62f),
    SCROLL_BACK_MID(33f / 62f),
    SCROLL_BACK_BOT(34f / 62f),
    LOGO(544f / 450f),
    BUTTON_TINY(181f / 182f),
    BUTTON_TINY_PRESSED(181f / 182f),
    BUTTON_BIG(182f / 720f),
    BUTTON_BIG_PRESSED(182f / 720f),
    BANNER_LEFT(246f / 386f),
    BANNER_TOP(247f / 524f),
    FIRE(307f / 180f),
    MUD(127f / 349f),
    ;
    private final float ratio;

    Texture(float aspectRatio) {
        ratio = aspectRatio;
    }

    public float aspectRatio() {
        return ratio;
    }
}
