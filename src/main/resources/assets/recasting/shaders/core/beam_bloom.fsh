#version 150

in vec4 vertexColor;
in vec2 texCoord0;

out vec4 fragColor;

const float EXPOSURE = 1.2;
const float SOFT_POWER = 2.4;
const float CORE_POWER = 8.0;

void main() {
    float across = abs(texCoord0.y);
    float soft = exp(-across * SOFT_POWER);
    float core = exp(-across * CORE_POWER);
    float energy = soft * 0.7 + core * 0.9;
    if (energy < 0.004) {
        discard;
    }

    vec3 tint = max(vertexColor.rgb, vec3(0.02));
    vec3 col = tint * energy * EXPOSURE * vertexColor.a;
    float alpha = clamp(energy * vertexColor.a, 0.0, 1.0);
    if (dot(col, col) < 0.000004) {
        discard;
    }

    fragColor = vec4(col, alpha);
}
