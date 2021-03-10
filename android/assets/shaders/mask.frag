#ifdef GL_ES
precision mediump float;
#endif

varying vec4 v_color;
varying vec2 v_texCoords;
uniform sampler2D u_texture;
 uniform sampler2D u_mask;
void main(void) {
    vec4 texColor0 = texture2D(u_texture, v_texCoords);
    float mask = texture2D(u_mask, v_texCoords).a;
    gl_FragColor =v_color*vec4(texColor0.rgb,mask);
}