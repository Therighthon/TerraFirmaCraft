/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.client.render.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.function.Function;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.HangingSignRenderer;
import net.minecraft.client.resources.model.Material;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.SignBlock;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.WoodType;

import net.dries007.tfc.client.RenderHelpers;
import net.dries007.tfc.common.blocks.wood.TFCCeilingHangingSignBlock;
import net.dries007.tfc.common.blocks.wood.TFCWallHangingSignBlock;
import net.dries007.tfc.mixin.client.accessor.SignRendererAccessor;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.Metal;

public class TFCHangingSignBlockEntityRenderer extends HangingSignRenderer
{
    public static final Function<SignBlock, ResourceLocation> RESOURCE_LOCATION = (sign) -> {
        final String wood = getWoodName(sign.type());
        final String metal = getMetal(sign).getSerializedName();
        return Helpers.identifier("entity/signs/hanging/" + metal + "/" + wood);
    };

    private static String getWoodName(WoodType woodType)
    {
        final String id = woodType.name();
        final int index = id.indexOf(":") + 1;
        return id.substring(index);
    }

    private static Metal getMetal(SignBlock sign)
    {
        if (sign instanceof TFCCeilingHangingSignBlock)
        {
            return ((TFCCeilingHangingSignBlock) sign).getMetal();
        }
        else if (sign instanceof TFCWallHangingSignBlock)
        {
            return ((TFCWallHangingSignBlock) sign).getMetal();
        }
        return null;
    }

    private final BlockEntityRendererProvider.Context context;

    public TFCHangingSignBlockEntityRenderer(BlockEntityRendererProvider.Context context)
    {
        super(context);
        this.context = context;
    }

    @Override
    public void render(SignBlockEntity sign, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int light, int overlay)
    {
        final BlockState state = sign.getBlockState();
        final SignBlock signBlock = (SignBlock) state.getBlock();
        final WoodType wood = signBlock.type();
        final HangingSignModel model = getModel(wood);
        final Material modelMaterial = getModelMaterial(getMetal(signBlock), wood);

        model.evaluateVisibleParts(state);
        renderSignWithText(sign, poseStack, buffer, light, overlay, state, signBlock, modelMaterial, model);
    }

    // behavior copied from SignRenderer#renderSignWithText
    void renderSignWithText(SignBlockEntity sign, PoseStack poseStack, MultiBufferSource buffer, int light, int overlay, BlockState blockstate, SignBlock signblock, Material modelMaterial, Model model)
    {
        poseStack.pushPose();
        ((SignRendererAccessor) this).invoke$translateSign(poseStack, -signblock.getYRotationDegrees(blockstate), blockstate);
        this.renderSign(poseStack, buffer, light, overlay, modelMaterial, model);
        ((SignRendererAccessor) this).invoke$renderSignText(sign.getBlockPos(), sign.getFrontText(), poseStack, buffer, light, sign.getTextLineHeight(), sign.getMaxTextLineWidth(), true);
        ((SignRendererAccessor) this).invoke$renderSignText(sign.getBlockPos(), sign.getBackText(), poseStack, buffer, light, sign.getTextLineHeight(), sign.getMaxTextLineWidth(), false);
        poseStack.popPose();
    }

    // behavior copied from SignRenderer#renderSign
    void renderSign(PoseStack poseStack, MultiBufferSource buffer, int light, int overlay, Material modelMaterial, Model model)
    {
        poseStack.pushPose();
        float f = this.getSignModelRenderScale();
        poseStack.scale(f, -f, -f);

        VertexConsumer vertexconsumer = modelMaterial.buffer(buffer, model::renderType);
        ((SignRendererAccessor) this).invoke$renderSignModel(poseStack, light, overlay, model, vertexconsumer);
        poseStack.popPose();
    }

    public record Provider<T>(
        Material modelMaterial,
        ResourceLocation textureLocation,
        T model
    ) {}

    private Material getModelMaterial(Metal metal, WoodType wood)
    {
        final String woodName = getWoodName(wood);
        return new Material(
            Sheets.SIGN_SHEET,
            Helpers.identifier("entity/signs/hanging/" + metal.getSerializedName() + "/" + woodName)
        );
    }

    private HangingSignModel getModel(WoodType wood)
    {
        return new HangingSignModel(context.bakeLayer(RenderHelpers.layerId("hanging_sign/" + getWoodName(wood))));
    }
}
