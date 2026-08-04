#version 150

in vec4 vertexColor;
in vec2 texCoord0;

uniform sampler2D Sampler0;

out vec4 fragColor;

const float EXPOSURE = 1.25;
const float HALO_STRENGTH = 0.4;
const float HALO_RADIUS_1 = 0.05;
const float HALO_RADIUS_2 = 0.1;

// 加法粒子贴图软边常在 RGB；alpha 可能恒为 1。用亮度×alpha 作能量。
float sampleEnergy(vec2 uv) {
    vec4 t = texture(Sampler0, uv);
    float lum = max(t.r, max(t.g, t.b));
    return t.a * lum;
}

float blurredEnergy(vec2 uv) {
    float e = 0.0;
    e += sampleEnergy(uv + vec2(HALO_RADIUS_1, 0.0));
    e += sampleEnergy(uv + vec2(-HALO_RADIUS_1, 0.0));
    e += sampleEnergy(uv + vec2(0.0, HALO_RADIUS_1));
    e += sampleEnergy(uv + vec2(0.0, -HALO_RADIUS_1));
    e += sampleEnergy(uv + vec2(HALO_RADIUS_2, HALO_RADIUS_2)) * 0.55;
    e += sampleEnergy(uv + vec2(-HALO_RADIUS_2, HALO_RADIUS_2)) * 0.55;
    e += sampleEnergy(uv + vec2(HALO_RADIUS_2, -HALO_RADIUS_2)) * 0.55;
    e += sampleEnergy(uv + vec2(-HALO_RADIUS_2, -HALO_RADIUS_2)) * 0.55;
    return e / 6.2;
}

void main() {
    vec4 tex = texture(Sampler0, texCoord0);
    float core = sampleEnergy(texCoord0);
    float blur = blurredEnergy(texCoord0);
    float energy = max(core, blur * 0.55);
    if (energy < 0.004) {
        discard;
    }

    vec3 tint = max(vertexColor.rgb, vec3(0.02));
    float halo = max(blur - core, 0.0);
    // 芯跟贴图色；晕只用 tint，避免在黑色底上造实心色块
    vec3 col = tint * tex.rgb * EXPOSURE + tint * halo * HALO_STRENGTH;
    col *= vertexColor.a;
    if (dot(col, col) < 0.000004) {
        discard;
    }

    fragColor = vec4(col, clamp(energy * vertexColor.a, 0.0, 1.0));
}
