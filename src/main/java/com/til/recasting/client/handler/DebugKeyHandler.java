package com.til.recasting.client.handler;

/**
 * 调试按键处理器
 * 用于调试召唤剑渲染角度
 */
/*
@EventBusSubscriber(modid = Recasting.MODID, value = Dist.CLIENT)
public class DebugKeyHandler {

    private static boolean xKeyPressed = false;
    private static boolean yKeyPressed = false;

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }

        Minecraft mc = Minecraft.getInstance();
        if (mc.screen != null) {
            // 如果打开了GUI界面，不处理按键
            return;
        }

        long window = mc.getWindow().getWindow();

        // 检测 X 键
        boolean xPressed = GLFW.glfwGetKey(window, GLFW.GLFW_KEY_X) == GLFW.GLFW_PRESS;
        if (xPressed && !xKeyPressed) {
            // X 键刚按下
            SummondSwordEntityRenderer.ya += 90.0f;
            // 归一化到 0-360 范围
            SummondSwordEntityRenderer.ya = SummondSwordEntityRenderer.ya % 360.0f;
            if (mc.player != null) {
                mc.player.displayClientMessage(
                    net.minecraft.network.chat.Component.literal("§e[调试] §aYA:" + SummondSwordEntityRenderer.ya + "ZA:" + SummondSwordEntityRenderer.za),
                    true
                );
            }
        }
        xKeyPressed = xPressed;

        // 检测 Y 键
        boolean yPressed = GLFW.glfwGetKey(window, GLFW.GLFW_KEY_Y) == GLFW.GLFW_PRESS;
        if (yPressed && !yKeyPressed) {
            // Y 键刚按下
            SummondSwordEntityRenderer.za += 90.0f;
            // 归一化到 0-360 范围
            SummondSwordEntityRenderer.za = SummondSwordEntityRenderer.za % 360.0f;
            if (mc.player != null) {
                mc.player.displayClientMessage(
                        net.minecraft.network.chat.Component.literal("§e[调试] §aYA:" + SummondSwordEntityRenderer.ya + "ZA:" + SummondSwordEntityRenderer.za),
                        true
                );
            }
        }
        yKeyPressed = yPressed;
    }
}

*/
