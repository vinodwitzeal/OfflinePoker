package bigcash.poker.widgets;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector2;

public class LayerMaskCShader extends CShader {
    public LayerMaskCShader() {
        super("LayerMask", CShader.layerMaskVertex, CShader.layerMaskFragment);
    }

    public void begin(Batch batch, Texture maskTexture,Vector2 maskMin, Vector2 maskMax,Vector2 glMin,Vector2 glMax){
        if (begin(batch)){
            maskTexture.bind(1);
            Gdx.gl.glActiveTexture(GL20.GL_TEXTURE0);
            ShaderProgram maskShader=getShaderProgram();
            maskShader.setUniformi("u_mask", 1);
            maskShader.setUniformf("u_gmin",glMin);
            maskShader.setUniformf("u_gmax",glMax);
            maskShader.setUniformf("u_mmin",maskMin);
            maskShader.setUniformf("u_mmax",maskMax);
        }
    }
}
