#version 150

#moj_import <light.glsl>
#moj_import <fog.glsl>

in vec3 Position;
in vec4 Color;
in vec2 UV0;
in ivec2 UV1;
in ivec2 UV2;
in vec3 Normal;

uniform sampler2D Sampler1;
uniform sampler2D Sampler2;

uniform mat4 ModelViewMat;
uniform mat4 ProjMat;
uniform int FogShape;

uniform vec3 Light0_Direction;
uniform vec3 Light1_Direction;

out float vertexDistance;
out vec4 vertexColor;
out vec4 lightMapColor;
out vec4 overlayColor;
out vec2 texCoord0;

void main() {

    vertexDistance = fog_distance(Position, FogShape);
    vertexColor = minecraft_mix_light(Light0_Direction, Light1_Direction, Normal, Color);
    lightMapColor = texelFetch(Sampler2, UV2 / 16, 0);
    overlayColor = texelFetch(Sampler1, UV1, 0);
    texCoord0 = UV0;

//    vec3 quadUp = normalize(mat3(ModelViewMat) * vec3(0.0, 1.0, 0.0));
//    float dX = abs(texCoord0.x - bubblePos.x);
//    // Compute a horizontal factor: 1.0 at bubbleX, dropping to 0 at bubbleRadius.
//    float horizontalFactor = 1.0 - smoothstep(bubbleRadius * 0.8, bubbleRadius, dX);
//
//    // Calculate the vertical distance from the bubble center.
//    float dY = abs(texCoord0.y - bubblePos.y);
//    // For a narrow slider, we want maximum effect near bubbleCenterY and taper off quickly.
//    float verticalFactor = clamp(1.0 - dY * 2.0, 0.0, 1.0);
//
//    // Combine factors so the bubble effect is strongest near the bubble center.
//    float factor = horizontalFactor;

    // Compute a factor (0 to 1) using smoothstep so that vertices within the radius get displaced,
    // with maximum displacement at the bubble center.
    //    float bubbleFactor = smoothstep(bubbleRadius, bubbleRadius - 0.1, dist);
    // Invert the factor so that center is 1.0 and edge is 0.0.
    //    float displacement = bubbleStrength * (1.0 - bubbleFactor);
//    vec3 displacement = Normal * bubbleStrength * factor;

    vec4 pos = vec4(Position, 1.0);
    gl_Position = ProjMat * ModelViewMat * pos;

}
