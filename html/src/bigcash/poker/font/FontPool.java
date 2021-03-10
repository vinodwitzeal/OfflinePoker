package bigcash.poker.font;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;

import java.util.HashMap;

import bigcash.poker.utils.PokerUtils;

public class FontPool {
    private static FontPool pool;
    private HashMap<String, Font> fontHashMap = new HashMap<String, Font>();
    private HashMap<FontType, FontData> dataHashMap = new HashMap<FontType, FontData>();
    private ShaderProgram distanceShader;
    private float density;

    private FontPool() {
        float width = Gdx.graphics.getWidth();
        float height = Gdx.graphics.getHeight();
        this.density=Math.min(width,height)/360.0f;
        clear();
        loadShader();
    }

    public static void init() {
        pool = null;
    }

    public static void dispose() {
        if (pool != null) {
            pool.disposePool();
        }
    }

    private static FontPool instance() {
        if (pool == null) {
            pool = new FontPool();
        }
        return pool;
    }

    public static Font obtain(FontType type, float size) {
        Font font = instance().getFont(type, size);
        font.setFontStyle(null);
        return font;
    }

    private void clear() {
        fontHashMap.clear();
        dataHashMap.clear();
    }

    private void loadShader() {
        ShaderProgram.pedantic = false;
        distanceShader = new ShaderProgram(Gdx.files.internal("shaders/distance.vert"), Gdx.files.internal("shaders/distance.frag"));
        boolean compiled = distanceShader.isCompiled();
    }

    private Font getFont(FontType type, float size) {
        size = PokerUtils.getValue(size * density);
        if (distanceShader == null) loadShader();
        String name = type.value() + size;
        if (fontHashMap.containsKey(name)) {
            Font font = fontHashMap.get(name);
            font.setFontStyle(null);
            return fontHashMap.get(name);
        } else {
            if (dataHashMap.containsKey(type)) {
                FontData fontData = dataHashMap.get(type);
                Font font = new Font(fontData, size, distanceShader);
                fontHashMap.put(name, font);
                return font;
            } else {
                FontData fontData = new FontData(type);
                dataHashMap.put(type, fontData);
                Font font = new Font(fontData, size, distanceShader);
                fontHashMap.put(name, font);
                return font;
            }
        }
    }

    private void disposePool() {
        distanceShader.dispose();
        for (Font font : fontHashMap.values()) {
            if (font != null) {
                font.dispose();
            }
        }

        for (FontData data : dataHashMap.values()) {
            if (data != null) {
                data.dispose();
            }
        }
    }
}
