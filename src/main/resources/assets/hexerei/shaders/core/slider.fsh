#version 150

#moj_import <fog.glsl>

uniform sampler2D Sampler0;

uniform vec4 ColorModulator;
uniform float FogStart;
uniform float FogEnd;
uniform vec4 FogColor;

uniform vec2 bubblePos;         // The bubble center in UV space (e.g. (sliderValue, 0.5)).
uniform float bubbleRadius;     // Radius (in normalized UV units) for the bubble effect.
uniform float bubbleStrength;   // How much to distort the texture (e.g. 0.2).
uniform int isHorizontal;       // Whether the effect is horizontal or vertical

in float vertexDistance;
in vec4 vertexColor;
in vec4 lightMapColor;
in vec4 overlayColor;
in vec2 texCoord0;

out vec4 fragColor;

const float PI = 3.14159265358979323846;

// Function to convert HSV to RGB.
// The input 'c.x' is hue (0.0-1.0), 'c.y' is saturation, and 'c.z' is value.
vec3 hsv2rgb(vec3 c) {
    vec3 rgb = clamp(abs(mod(c.x * 6.0 + vec3(0.0, 4.0, 2.0), 6.0) - 3.0) - 1.0, 0.0, 1.0);
    return c.z * mix(vec3(1.0), rgb, c.y);
}

void main() {
    float hue = texCoord0.x;
    vec3 rgb = vec3(1.0, 1.0, 1.0); // full saturation and brightness.
    vec4 color = vec4(rgb, 1.0);

    // Compute distances based on direction
    float directionDistance, perpDistance;
    if (isHorizontal == 1) {
        directionDistance = abs(texCoord0.x - bubblePos.x);
        perpDistance = abs(texCoord0.y - bubblePos.y);
    } else {
        directionDistance = abs(texCoord0.y - bubblePos.y);
        perpDistance = abs(texCoord0.x - bubblePos.x);
    }

    // Set baseline thickness values
    float minPerp = 0.0;      // Thin border (outside bubble effect)
    float maxPerp = 0.2;      // Maximum thickness for the main bubble

    // ---- Main bubble effect ----
    float allowedPerp_main;
    if (directionDistance < bubbleRadius) {
        // Inverted cosine for smooth transition
        float factor_main = 0.5 * (1.0 + cos(PI * directionDistance / bubbleRadius));
        allowedPerp_main = mix(minPerp, maxPerp, factor_main) * bubbleStrength;
    } else {
        allowedPerp_main = minPerp * bubbleStrength;
    }

    // Combine and add offset
    float allowedPerp = allowedPerp_main;
    allowedPerp += 0.2; // Overall offset

    // Smooth transition at the edge
    float edgeThickness = 0.38;
    float ease = 1.0 - smoothstep(allowedPerp - edgeThickness, allowedPerp, perpDistance);

    // Discard fragments outside the effect
    if (ease < 0.01)
        discard;

    color *= vertexColor * ColorModulator;
    color.rgb = mix(overlayColor.rgb, color.rgb, overlayColor.a);
    color *= lightMapColor;
    fragColor = linear_fog(color, vertexDistance, FogStart, FogEnd, FogColor);
}
