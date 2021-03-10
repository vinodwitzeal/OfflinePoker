#ifdef GL_ES
precision mediump float;
#endif

uniform sampler2D u_texture;

varying vec4 v_color;
varying vec2 v_texCoord;

uniform float u_smoothing;

uniform int enableShadow;
uniform vec2 u_shadow;
uniform vec4 u_shadowColor;
const float shadowSmoothing=0.3;

uniform int enableOutline;
uniform float u_outline;
uniform vec4 u_outlineColor;


void main() {
    float distance = texture2D(u_texture, v_texCoord).a;
    float alpha = smoothstep(0.5 - u_smoothing, 0.5 + u_smoothing, distance);
    vec4 text = vec4(v_color.rgb, v_color.a * alpha);
    vec4 resultText=text;


    if(enableOutline>0){
        float outlineFactor = smoothstep(0.5 - u_smoothing, 0.5 + u_smoothing, distance);
        vec4 color = mix(u_outlineColor, v_color, outlineFactor);
        float alpha = smoothstep(u_outline - u_smoothing, u_outline + u_smoothing, distance);
        resultText= vec4(color.rgb, color.a * alpha*v_color.a);
    }
    if(enableShadow>0){
        float shadowDistance = texture2D(u_texture, v_texCoord-u_shadow).a;
        float shadowAlpha = smoothstep(0.5 - shadowSmoothing, 0.5 + shadowSmoothing, shadowDistance);
        vec4 shadow = vec4(u_shadowColor.rgb, u_shadowColor.a*shadowAlpha*v_color.a);
        resultText = mix(shadow,resultText,resultText.a);
    }
    gl_FragColor=resultText;
}