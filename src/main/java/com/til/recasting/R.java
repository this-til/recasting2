package com.til.recasting;

import net.minecraft.resources.ResourceLocation;

/**
 * 资源路径常量（P6 datagen 完整 R 生成前的手写子集）。
 * TODO(P6): 由 datagen 覆盖完整 R。
 */
public final class R {

    private R() {
    }

    public static final class Models {
        private Models() {
        }

        public static final class Special {
            private Special() {
            }

            public static final ResourceLocation imprisonment$obj = Recasting.prefix("models/special/imprisonment.obj");
            public static final ResourceLocation imprisonment$png = Recasting.prefix("models/special/imprisonment.png");
            public static final ResourceLocation matrix$obj = Recasting.prefix("models/special/matrix.obj");
            public static final ResourceLocation matrix$png = Recasting.prefix("models/special/matrix.png");
            public static final ResourceLocation starfallStar$obj = Recasting.prefix("models/special/starfall_star.obj");
            public static final ResourceLocation starfallStar$png = Recasting.prefix("models/special/starfall_star.png");
        }
    }
}
