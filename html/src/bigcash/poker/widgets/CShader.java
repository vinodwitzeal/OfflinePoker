package bigcash.poker.widgets;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.Disposable;

public class CShader implements Disposable {
    private boolean compiled;
    private ShaderProgram shaderProgram, defaultShader;

    public CShader(String name, String vertex, String fragment) {
        shaderProgram = new ShaderProgram(vertex, fragment);
        if (shaderProgram.isCompiled()) {
            compiled = true;
        }
    }

    public boolean begin(Batch batch) {
        if (compiled) {
            setDefaultShader(batch.getShader());
            batch.setShader(shaderProgram);
            return true;
        }
        return false;
    }

    public void end(Batch batch) {
        if (compiled) {
            batch.setShader(getDefaultShader());
        }
    }

    public boolean isCompiled() {
        return compiled;
    }


    public ShaderProgram getShaderProgram() {
        return shaderProgram;
    }

    public ShaderProgram getDefaultShader() {
        return defaultShader;
    }

    public void setDefaultShader(ShaderProgram defaultShader) {
        this.defaultShader = defaultShader;
    }

    @Override
    public void dispose() {
        shaderProgram.dispose();
    }

    //Layer Mask
    public static final String layerMaskVertex = "attribute vec4 a_position;\n" +
            "attribute vec4 a_color;\n" +
            "attribute vec2 a_texCoord0;\n" +
            "uniform mat4 u_projTrans;\n" +
            "varying vec4 v_color;\n" +
            "varying vec2 v_texCoords;\n" +
            "varying vec4 v_glPosition;\n" +
            "void main(){\n" +
            "    gl_Position =  u_projTrans *a_position;\n" +
            "    v_glPosition=gl_Position;\n" +
            "    v_color =a_color;\n" +
            "    v_texCoords =a_texCoord0;\n" +
            "}";

    public static final String layerMaskFragment = "#ifdef GL_ES\n" +
            "precision mediump float;\n" +
            "#endif\n" +
            "\n" +
            "varying vec4 v_color;\n" +
            "varying vec2 v_texCoords;\n" +
            "uniform sampler2D u_texture;\n" +
            "uniform sampler2D u_mask;\n" +
            "uniform vec2 u_gmin,u_gmax;\n" +
            "uniform vec2 u_mmin,u_mmax;\n" +
            "varying vec4 v_glPosition;\n" +
            "void main(void) {\n" +
            "vec4 texColor0 = texture2D(u_texture, v_texCoords);\n" +
            "float mask=0.0;\n" +
            "if(v_glPosition.x>=u_gmin.x && v_glPosition.x<=u_gmax.x){\n" +
            "if(v_glPosition.y>=u_gmin.y && v_glPosition.y<=u_gmax.y){\n" +
            "float xp=(v_glPosition.x-u_gmin.x)/(u_gmax.x-u_gmin.x);\n" +
            "float yp=(v_glPosition.y-u_gmin.y)/(u_gmax.y-u_gmin.y);\n" +
            "vec2 maskCoords=vec2(u_mmin.x+(u_mmax.x-u_mmin.x)*xp,u_mmin.y+(u_mmax.y-u_mmin.y)*yp);\n" +
            "mask =texture2D(u_mask, maskCoords).a;\n" +
            "}\n" +
            "}"+
//            "mask=1.0-mask;\n"
            "gl_FragColor =v_color*vec4(texColor0.rgb,mask);\n" +
            "}";
}
