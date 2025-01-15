package io.nosite.DBIJ.managers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.Color;

public class FontManager {

    private static BitmapFont font;

    public static void initialize() {
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/ThaleahFat.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 24; // Basis-Größe
        parameter.color = Color.WHITE;
        font = generator.generateFont(parameter);
        generator.dispose();
    }

    public static BitmapFont getFont() {
        return font;
    }

    public static void dispose() {
        if(font != null) {
            font.dispose();
        }
    }

}
