package mcjty.efab.blocks.tank;

import mcjty.efab.blocks.ModBlocks;
import mcjty.lib.gui.HudRenderHelper;
import mcjty.lib.varia.BlockTools;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.text.TextFormatting;
import org.lwjgl.opengl.GL11;

import java.util.Collections;
import java.util.List;

public class TankRenderer extends TileEntitySpecialRenderer<TankTE> {

    @Override
    public void renderTileEntityAt(TankTE te, double x, double y, double z, float partialTicks, int destroyStage) {
        super.renderTileEntityAt(te, x, y, z, partialTicks, destroyStage);

        Block block = te.getWorld().getBlockState(te.getPos().down()).getBlock();
        if (block != ModBlocks.tankBlock) {
            renderHud(te, x, y, z);
        }
    }

    public static void renderHud(TankTE tankTE, double x, double y, double z) {
        float scale = 1.1f;
        String name = tankTE.getClientFluidName();
        if (name.length() >= 16) {
            scale = 0.5f;
        } else if (name.length() >= 12) {
                scale = 0.6f;
        } else if (name.length() >= 8) {
            scale = 0.8f;
        }
        List<String> log = Collections.singletonList(TextFormatting.DARK_GRAY + name);
        EnumFacing orientation = BlockTools.getOrientationHoriz(tankTE.getBlockMetadata());

        HudRenderHelper.HudPlacement hudPlacement = HudRenderHelper.HudPlacement.HUD_FRONT;
        HudRenderHelper.HudOrientation hudOrientation = HudRenderHelper.HudOrientation.HUD_SOUTH;
        renderHud(log, hudPlacement, hudOrientation, orientation, x, y, z, scale);
    }

    // @todo generalize the version in McJtyLib
    public static void renderHud(List<String> messages,
                                 HudRenderHelper.HudPlacement hudPlacement,
                                 HudRenderHelper.HudOrientation hudOrientation,
                                 EnumFacing orientation,
                                 double x, double y, double z, float scale) {
        GlStateManager.pushMatrix();

        if (hudPlacement == HudRenderHelper.HudPlacement.HUD_FRONT) {
            GlStateManager.translate((float) x + 0.5F, (float) y + 0.75F, (float) z + 0.5F);
        } else if (hudPlacement == HudRenderHelper.HudPlacement.HUD_CENTER) {
            GlStateManager.translate((float) x + 0.5F, (float) y + 0.5F, (float) z + 0.5F);
        } else {
            GlStateManager.translate((float) x + 0.5F, (float) y + 1.75F, (float) z + 0.5F);
        }

        switch (hudOrientation) {
            case HUD_SOUTH:
                GlStateManager.rotate(-getHudAngle(orientation), 0.0F, 1.0F, 0.0F);
                break;
            case HUD_TOPLAYER_HORIZ:
                GlStateManager.rotate(-Minecraft.getMinecraft().getRenderManager().playerViewY, 0.0F, 1.0F, 0.0F);
                GlStateManager.rotate(180, 0.0F, 1.0F, 0.0F);
                break;
            case HUD_TOPLAYER:
                GlStateManager.rotate(-Minecraft.getMinecraft().getRenderManager().playerViewY, 0.0F, 1.0F, 0.0F);
                GlStateManager.rotate(Minecraft.getMinecraft().getRenderManager().playerViewX, 1.0F, 0.0F, 0.0F);
                GlStateManager.rotate(180, 0.0F, 1.0F, 0.0F);
                break;
        }

        if (hudPlacement == HudRenderHelper.HudPlacement.HUD_FRONT || hudPlacement == HudRenderHelper.HudPlacement.HUD_ABOVE_FRONT) {
//            GlStateManager.translate(0.0F, -0.2500F, -0.4375F + .9);  // <--- original
            GlStateManager.translate(0.28F, -0.2500F-.5f, -0.4375F + .87);
        } else if (hudPlacement != HudRenderHelper.HudPlacement.HUD_CENTER){
            GlStateManager.translate(0.0F, -0.2500F, -0.4375F + .4);
        }

        net.minecraft.client.renderer.RenderHelper.disableStandardItemLighting();
        Minecraft.getMinecraft().entityRenderer.disableLightmap();
        GlStateManager.disableBlend();
        GlStateManager.disableLighting();

        renderText(Minecraft.getMinecraft().fontRenderer, messages, 11, scale);
        Minecraft.getMinecraft().entityRenderer.enableLightmap();

//        RenderHelper.enableStandardItemLighting();
        GlStateManager.enableLighting();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        GlStateManager.popMatrix();
    }

    private static float getHudAngle(EnumFacing orientation) {
        float f3 = 0.0f;

        if (orientation != null) {
            switch (orientation) {
                case NORTH:
                    f3 = 180.0F;
                    break;
                case WEST:
                    f3 = 90.0F;
                    break;
                case EAST:
                    f3 = -90.0F;
                    break;
                default:
                    f3 = 0.0f;
            }
        }
        return f3;
    }

    private static void renderText(FontRenderer fontrenderer, List<String> messages, int lines, float scale) {
        GlStateManager.translate(-0.5F, 0.5F, 0.07F);
        float f3 = 0.0075F;
        GlStateManager.scale(f3 * scale, -f3 * scale, f3);
        GlStateManager.glNormal3f(0.0F, 0.0F, 1.0F);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

        renderLog(fontrenderer, messages, lines);
    }

    private static void renderLog(FontRenderer fontrenderer, List<String> messages, int lines) {
        int currenty = 7;
        int height = 10;
        int logsize = messages.size();
        int i = 0;
        for (String s : messages) {
            if (i >= logsize - lines) {
                // Check if this module has enough room
                if (currenty + height <= 124) {
                    fontrenderer.drawString(fontrenderer.trimStringToWidth(s, 115), 7, currenty, 0xffffff);
                    currenty += height;
                }
            }
            i++;
        }
    }

    public static enum HudPlacement {
        HUD_ABOVE,
        HUD_ABOVE_FRONT,
        HUD_FRONT,
        HUD_CENTER
    }

    public static enum HudOrientation {
        HUD_SOUTH,
        HUD_TOPLAYER_HORIZ,
        HUD_TOPLAYER
    }

}
