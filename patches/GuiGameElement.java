package com.simibubi.create.foundation.gui.element;

import com.jozufozu.flywheel.core.PartialModel;
import com.jozufozu.flywheel.core.virtual.VirtualEmptyBlockGetter;
import com.jozufozu.flywheel.fabric.model.DefaultLayerFilteringBakedModel;
import com.mojang.blaze3d.platform.GlStateManager.class_4534;
import com.mojang.blaze3d.platform.GlStateManager.class_4535;
import com.mojang.blaze3d.systems.RenderSystem;
import com.simibubi.create.foundation.fluid.FluidRenderer;
import com.simibubi.create.foundation.gui.ILightingSettings;
import com.simibubi.create.foundation.gui.UIRenderHelper;
import com.simibubi.create.foundation.utility.VecHelper;
import io.github.fabricators_of_create.porting_lib_ufo.fluids.FluidStack;
import io.github.fabricators_of_create.porting_lib_ufo.models.virtual.FixedColorTintingBakedModel;
import javax.annotation.Nullable;
import net.minecraft.class_1087;
import net.minecraft.class_1723;
import net.minecraft.class_1799;
import net.minecraft.class_1921;
import net.minecraft.class_1935;
import net.minecraft.class_2246;
import net.minecraft.class_2338;
import net.minecraft.class_2404;
import net.minecraft.class_243;
import net.minecraft.class_2680;
import net.minecraft.class_308;
import net.minecraft.class_310;
import net.minecraft.class_332;
import net.minecraft.class_3611;
import net.minecraft.class_4587;
import net.minecraft.class_4588;
import net.minecraft.class_4608;
import net.minecraft.class_4696;
import net.minecraft.class_4722;
import net.minecraft.class_4770;
import net.minecraft.class_5819;
import net.minecraft.class_776;
import net.minecraft.class_7833;
import net.minecraft.class_811;
import net.minecraft.class_918;
import net.minecraft.class_4597.class_4598;

public class GuiGameElement {
   public static com.simibubi.create.foundation.gui.element.GuiGameElement.GuiRenderBuilder of(class_1799 stack) {
      return new com.simibubi.create.foundation.gui.element.GuiGameElement.GuiItemRenderBuilder(stack);
   }

   public static com.simibubi.create.foundation.gui.element.GuiGameElement.GuiRenderBuilder of(class_1935 itemProvider) {
      return new com.simibubi.create.foundation.gui.element.GuiGameElement.GuiItemRenderBuilder(itemProvider);
   }

   public static com.simibubi.create.foundation.gui.element.GuiGameElement.GuiRenderBuilder of(class_2680 state) {
      return new com.simibubi.create.foundation.gui.element.GuiGameElement.GuiBlockStateRenderBuilder(state);
   }

   public static com.simibubi.create.foundation.gui.element.GuiGameElement.GuiRenderBuilder of(PartialModel partial) {
      return new com.simibubi.create.foundation.gui.element.GuiGameElement.GuiBlockPartialRenderBuilder(partial);
   }

   public static com.simibubi.create.foundation.gui.element.GuiGameElement.GuiRenderBuilder of(class_3611 fluid) {
      return new com.simibubi.create.foundation.gui.element.GuiGameElement.GuiBlockStateRenderBuilder(
         (class_2680)fluid.method_15785().method_15759().method_11657(class_2404.field_11278, 0)
      );
   }

   private static class GuiBlockModelRenderBuilder extends com.simibubi.create.foundation.gui.element.GuiGameElement.GuiRenderBuilder {
      protected class_1087 blockModel;
      protected class_2680 blockState;

      public GuiBlockModelRenderBuilder(class_1087 blockmodel, @Nullable class_2680 blockState) {
         this.blockState = blockState == null ? class_2246.field_10124.method_9564() : blockState;
         this.blockModel = blockmodel;
      }

      public void render(class_332 graphics) {
         class_4587 matrixStack = graphics.method_51448();
         this.prepareMatrix(matrixStack);
         class_310 mc = class_310.method_1551();
         class_776 blockRenderer = mc.method_1541();
         class_4598 buffer = mc.method_22940().method_23000();
         class_1921 renderType = this.blockState.method_26204() == class_2246.field_10124
            ? class_4722.method_24076()
            : class_4696.method_23683(this.blockState, true);
         class_4588 vb = buffer.getBuffer(renderType);
         this.transformMatrix(matrixStack);
         RenderSystem.setShaderTexture(0, class_1723.field_21668);
         this.renderModel(blockRenderer, buffer, renderType, vb, matrixStack);
         this.cleanUpMatrix(matrixStack);
      }

      protected void renderModel(class_776 blockRenderer, class_4598 buffer, class_1921 renderType, class_4588 vb, class_4587 ms) {
         int color = class_310.method_1551().method_1505().method_1697(this.blockState, null, null, 0);
         class_1087 model = this.blockModel;
         if (model == null) {
            // Some mods/model loaders leave a block without a baked model; draw the missing model instead of crashing.
            model = class_310.method_1551().method_1554().method_4744();
         }
         model = DefaultLayerFilteringBakedModel.wrap(model);
         if (color == -1) {
            color = this.color;
         }

         if (color != -1) {
            model = FixedColorTintingBakedModel.wrap(model, color);
         }

         // GUI previews used tesselateBlock with a "full bright" virtual level, but with Sodium/Indigo that lighting
         // path reads zero light and plain block models (the depot in JEI's sequenced assembly view) came out black.
         // Render the model directly at full brightness, like vanilla's renderSingleBlock does.
         float r = color == -1 ? 1.0F : (color >> 16 & 0xFF) / 255.0F;
         float g = color == -1 ? 1.0F : (color >> 8 & 0xFF) / 255.0F;
         float b = color == -1 ? 1.0F : (color & 0xFF) / 255.0F;
         blockRenderer.method_3350().method_3367(ms.method_23760(), vb, this.blockState, model, r, g, b, 15728880, class_4608.field_21444);
         buffer.method_22993();
      }
   }

   public static class GuiBlockPartialRenderBuilder extends com.simibubi.create.foundation.gui.element.GuiGameElement.GuiBlockModelRenderBuilder {
      public GuiBlockPartialRenderBuilder(PartialModel partial) {
         super(partial.get(), null);
      }
   }

   public static class GuiBlockStateRenderBuilder extends com.simibubi.create.foundation.gui.element.GuiGameElement.GuiBlockModelRenderBuilder {
      public GuiBlockStateRenderBuilder(class_2680 blockstate) {
         super(class_310.method_1551().method_1541().method_3349(blockstate), blockstate);
      }

      @Override
      protected void renderModel(class_776 blockRenderer, class_4598 buffer, class_1921 renderType, class_4588 vb, class_4587 ms) {
         if (this.blockState.method_26204() instanceof class_4770) {
            class_308.method_24210();
            super.renderModel(blockRenderer, buffer, renderType, buffer.getBuffer(class_4696.method_23683(this.blockState, false)), ms);
            class_308.method_24211();
         } else {
            super.renderModel(blockRenderer, buffer, renderType, vb, ms);
            if (!this.blockState.method_26227().method_15769()) {
               FluidRenderer.renderFluidBox(
                  new FluidStack(this.blockState.method_26227().method_15772(), 81000L), 0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F, buffer, ms, 15728880, false
               );
               buffer.method_22993();
            }
         }
      }
   }

   public static class GuiItemRenderBuilder extends com.simibubi.create.foundation.gui.element.GuiGameElement.GuiRenderBuilder {
      private final class_1799 stack;

      public GuiItemRenderBuilder(class_1799 stack) {
         this.stack = stack;
      }

      public GuiItemRenderBuilder(class_1935 provider) {
         this(new class_1799(provider));
      }

      public void render(class_332 graphics) {
         class_4587 matrixStack = graphics.method_51448();
         this.prepareMatrix(matrixStack);
         this.transformMatrix(matrixStack);
         renderItemIntoGUI(matrixStack, this.stack, this.customLighting == null);
         this.cleanUpMatrix(matrixStack);
      }

      public static void renderItemIntoGUI(class_4587 matrixStack, class_1799 stack, boolean useDefaultLighting) {
         class_918 renderer = class_310.method_1551().method_1480();
         class_1087 bakedModel = renderer.method_4019(stack, null, null, 0);
         class_310.method_1551().method_1531().method_4619(class_1723.field_21668).method_4527(false, false);
         RenderSystem.setShaderTexture(0, class_1723.field_21668);
         RenderSystem.enableBlend();
         RenderSystem.enableCull();
         RenderSystem.blendFunc(class_4535.SRC_ALPHA, class_4534.ONE_MINUS_SRC_ALPHA);
         RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
         matrixStack.method_22903();
         matrixStack.method_46416(0.0F, 0.0F, 100.0F);
         matrixStack.method_46416(8.0F, -8.0F, 0.0F);
         matrixStack.method_22905(16.0F, 16.0F, 16.0F);
         class_4598 buffer = class_310.method_1551().method_22940().method_23000();
         boolean flatLighting = !bakedModel.method_24304();
         if (useDefaultLighting && flatLighting) {
            class_308.method_24210();
         }

         renderer.method_23179(stack, class_811.field_4317, false, matrixStack, buffer, 15728880, class_4608.field_21444, bakedModel);
         RenderSystem.disableDepthTest();
         buffer.method_22993();
         RenderSystem.enableDepthTest();
         if (useDefaultLighting && flatLighting) {
            class_308.method_24211();
         }

         matrixStack.method_22909();
      }
   }

   public abstract static class GuiRenderBuilder extends RenderElement {
      protected double xLocal;
      protected double yLocal;
      protected double zLocal;
      protected double xRot;
      protected double yRot;
      protected double zRot;
      protected double scale = 1.0;
      protected int color = 16777215;
      protected class_243 rotationOffset = class_243.field_1353;
      protected ILightingSettings customLighting = null;

      public com.simibubi.create.foundation.gui.element.GuiGameElement.GuiRenderBuilder atLocal(double x, double y, double z) {
         this.xLocal = x;
         this.yLocal = y;
         this.zLocal = z;
         return this;
      }

      public com.simibubi.create.foundation.gui.element.GuiGameElement.GuiRenderBuilder rotate(double xRot, double yRot, double zRot) {
         this.xRot = xRot;
         this.yRot = yRot;
         this.zRot = zRot;
         return this;
      }

      public com.simibubi.create.foundation.gui.element.GuiGameElement.GuiRenderBuilder rotateBlock(double xRot, double yRot, double zRot) {
         return this.rotate(xRot, yRot, zRot).withRotationOffset(VecHelper.getCenterOf(class_2338.field_10980));
      }

      public com.simibubi.create.foundation.gui.element.GuiGameElement.GuiRenderBuilder scale(double scale) {
         this.scale = scale;
         return this;
      }

      public com.simibubi.create.foundation.gui.element.GuiGameElement.GuiRenderBuilder color(int color) {
         this.color = color;
         return this;
      }

      public com.simibubi.create.foundation.gui.element.GuiGameElement.GuiRenderBuilder withRotationOffset(class_243 offset) {
         this.rotationOffset = offset;
         return this;
      }

      public com.simibubi.create.foundation.gui.element.GuiGameElement.GuiRenderBuilder lighting(ILightingSettings lighting) {
         this.customLighting = lighting;
         return this;
      }

      protected void prepareMatrix(class_4587 matrixStack) {
         matrixStack.method_22903();
         RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
         RenderSystem.enableDepthTest();
         RenderSystem.enableBlend();
         RenderSystem.blendFunc(class_4535.SRC_ALPHA, class_4534.ONE_MINUS_SRC_ALPHA);
         this.prepareLighting(matrixStack);
      }

      protected void transformMatrix(class_4587 matrixStack) {
         matrixStack.method_46416(this.x, this.y, this.z);
         matrixStack.method_22905((float)this.scale, (float)this.scale, (float)this.scale);
         matrixStack.method_22904(this.xLocal, this.yLocal, this.zLocal);
         UIRenderHelper.flipForGuiRender(matrixStack);
         matrixStack.method_22904(this.rotationOffset.field_1352, this.rotationOffset.field_1351, this.rotationOffset.field_1350);
         matrixStack.method_22907(class_7833.field_40718.rotationDegrees((float)this.zRot));
         matrixStack.method_22907(class_7833.field_40714.rotationDegrees((float)this.xRot));
         matrixStack.method_22907(class_7833.field_40716.rotationDegrees((float)this.yRot));
         matrixStack.method_22904(-this.rotationOffset.field_1352, -this.rotationOffset.field_1351, -this.rotationOffset.field_1350);
      }

      protected void cleanUpMatrix(class_4587 matrixStack) {
         matrixStack.method_22909();
         this.cleanUpLighting(matrixStack);
      }

      protected void prepareLighting(class_4587 matrixStack) {
         if (this.customLighting != null) {
            this.customLighting.applyLighting();
         } else {
            class_308.method_24211();
         }
      }

      protected void cleanUpLighting(class_4587 matrixStack) {
         if (this.customLighting != null) {
            class_308.method_24211();
         }
      }
   }
}
