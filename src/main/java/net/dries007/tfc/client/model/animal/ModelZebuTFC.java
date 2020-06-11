/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.client.model.animal;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import net.dries007.tfc.objects.entity.animal.EntityAnimalTFC;
import net.dries007.tfc.objects.entity.animal.EntityZebuTFC;

/**
 * ModelZebuTFC
 * Created using Tabula 7.1.0
 */

@SideOnly(Side.CLIENT)
@ParametersAreNonnullByDefault
public class ModelZebuTFC extends ModelBase
{
    public ModelRenderer legRFrontTop;
    public ModelRenderer humpMain;
    public ModelRenderer legLBackTop;
    public ModelRenderer legLFrontTop;
    public ModelRenderer legRBackTop;
    public ModelRenderer chest;
    public ModelRenderer body;
    public ModelRenderer rump;
    public ModelRenderer chestHump;
    public ModelRenderer legRFrontMiddle;
    public ModelRenderer lefRFrontBottom;
    public ModelRenderer legRFrontHoof;
    public ModelRenderer humpBack;
    public ModelRenderer legLBackMiddle;
    public ModelRenderer legLBackBottom;
    public ModelRenderer legLBackHoof;
    public ModelRenderer legLFrontMiddle;
    public ModelRenderer legLFrontBottom;
    public ModelRenderer legLFrontHoof;
    public ModelRenderer legRBackMiddle;
    public ModelRenderer legRBackBottom;
    public ModelRenderer legRBackHoof;
    public ModelRenderer udders;
    public ModelRenderer tailBase;
    public ModelRenderer teat1;
    public ModelRenderer teat3;
    public ModelRenderer teat2;
    public ModelRenderer teat4;
    public ModelRenderer tailBody;
    public ModelRenderer tailTip;
    public ModelRenderer neck;
    public ModelRenderer neckBase;
    public ModelRenderer head;
    public ModelRenderer nose;
    public ModelRenderer mouthTop;
    public ModelRenderer earL;
    public ModelRenderer earR;
    public ModelRenderer mouthBottom;
    public ModelRenderer hornML1;
    public ModelRenderer hornMR1;
    public ModelRenderer hornFL1;
    public ModelRenderer hornFR1;
    public ModelRenderer hornML2;
    public ModelRenderer hornML3a;
    public ModelRenderer hornML3b;
    public ModelRenderer hornML3c;
    public ModelRenderer hornML3d;
    public ModelRenderer hornML4;
    public ModelRenderer hornMR2;
    public ModelRenderer hornMR3a;
    public ModelRenderer hornMR3b;
    public ModelRenderer hornMR3c;
    public ModelRenderer hornMR3d;
    public ModelRenderer hornMR4;
    public ModelRenderer hornFL2;
    public ModelRenderer hornFR2;

    public ModelZebuTFC()
    {
        textureWidth = 128;
        textureHeight = 128;

        legLFrontTop = new ModelRenderer(this, 10, 114);
        legLFrontTop.setRotationPoint(4.8F, 3.0F, -4.0F);
        legLFrontTop.addBox(-0.5F, -1.0F, -3.0F, 3, 9, 5, 0.0F);
        setRotateAngle(legLFrontTop, 0.017453292519943295F, 0.0F, -0.08726646259971647F);
        udders = new ModelRenderer(this, 12, 70);
        udders.setRotationPoint(0.0F, 6.2F, -7.5F);
        udders.addBox(-3.5F, -1.5F, -4.0F, 7, 3, 8, 0.0F);
        setRotateAngle(udders, 0.08726646259971647F, 0.0F, 0.0F);
        hornMR1 = new ModelRenderer(this, 0, 60);
        hornMR1.setRotationPoint(-5.0F, -2.5F, -4.5F);
        hornMR1.addBox(-0.5F, -1.0F, -1.0F, 3, 2, 2, 0.0F);
        setRotateAngle(hornMR1, 0.0F, -0.5235987755982988F, 0.0F);
        hornFL2 = new ModelRenderer(this, 15, 58);
        hornFL2.setRotationPoint(2.3F, 0.0F, -0.1F);
        hornFL2.addBox(-0.5F, -0.5F, -0.5F, 3, 1, 1, 0.0F);
        setRotateAngle(hornFL2, 0.0F, 0.6981317007977318F, -0.6981317007977318F);
        legLBackTop = new ModelRenderer(this, 30, 114);
        legLBackTop.setRotationPoint(5.0F, 3.5F, 8.6F);
        legLBackTop.addBox(-0.7F, -1.5F, -3.0F, 3, 9, 5, 0.0F);
        setRotateAngle(legLBackTop, 0.0F, 0.0F, -0.08726646259971647F);
        legRFrontMiddle = new ModelRenderer(this, 11, 105);
        legRFrontMiddle.mirror = true;
        legRFrontMiddle.setRotationPoint(-1.1F, 7.7F, 0.1F);
        legRFrontMiddle.addBox(-1.1F, 0.0F, -2.5F, 3, 5, 4, 0.0F);
        setRotateAngle(legRFrontMiddle, 0.08726646259971647F, 0.0F, -0.03490658503988659F);
        tailBase = new ModelRenderer(this, 60, 88);
        tailBase.setRotationPoint(0.0F, -4.8F, -4.0F);
        tailBase.addBox(-1.0F, 0.0F, -1.0F, 2, 4, 2, 0.0F);
        setRotateAngle(tailBase, 0.4553564018453205F, 0.0F, 0.0F);
        mouthBottom = new ModelRenderer(this, 8, 4);
        mouthBottom.setRotationPoint(0.0F, 0.9F, 1.8F);
        mouthBottom.addBox(-2.0F, 0.0F, -1.4F, 4, 5, 1, 0.0F);
        setRotateAngle(mouthBottom, -0.091106186954104F, 0.0F, 0.0F);
        nose = new ModelRenderer(this, 6, 17);
        nose.setRotationPoint(0.0F, 3.6F, -2.8F);
        nose.addBox(-2.0F, -2.5F, -0.7F, 4, 5, 3, 0.0F);
        setRotateAngle(nose, 0.36425021489121656F, 0.0F, 0.0F);
        hornML3a = new ModelRenderer(this, 2, 52);
        hornML3a.setRotationPoint(1.76F, -0.2F, -0.24F);
        hornML3a.addBox(-0.5F, -0.5F, -0.5F, 2, 1, 1, 0.0F);
        setRotateAngle(hornML3a, 0.0F, 0.3490658503988659F, 0.0F);
        hornMR3d = new ModelRenderer(this, 2, 52);
        hornMR3d.setRotationPoint(-1.32F, 0.2F, -0.1F);
        hornMR3d.addBox(-0.5F, -0.5F, -0.5F, 2, 1, 1, 0.0F);
        setRotateAngle(hornMR3d, 0.0F, -0.3490658503988659F, 0.0F);
        hornML3d = new ModelRenderer(this, 2, 52);
        hornML3d.setRotationPoint(1.91F, 0.2F, 0.2F);
        hornML3d.addBox(-0.5F, -0.5F, -0.5F, 2, 1, 1, 0.0F);
        setRotateAngle(hornML3d, 0.0F, 0.3490658503988659F, 0.0F);
        hornMR4 = new ModelRenderer(this, 2, 48);
        hornMR4.setRotationPoint(-1.4F, -0.2F, -0.4F);
        hornMR4.addBox(-0.4F, -0.5F, -0.5F, 2, 1, 1, 0.0F);
        setRotateAngle(hornMR4, 0.0F, -0.2617993877991494F, 0.0F);
        legLFrontBottom = new ModelRenderer(this, 12, 95);
        legLFrontBottom.setRotationPoint(-0.6F, 4.5F, -0.4F);
        legLFrontBottom.addBox(-1.5F, 0.0F, -1.5F, 3, 7, 3, 0.0F);
        setRotateAngle(legLFrontBottom, -0.10471975511965977F, 0.0F, 0.05235987755982988F);
        earL = new ModelRenderer(this, 46, 34);
        earL.setRotationPoint(3.0F, -2.0F, -2.2F);
        earL.addBox(0.0F, -0.5F, -1.5F, 4, 1, 3, 0.0F);
        setRotateAngle(earL, 0.4363323129985824F, -0.08726646259971647F, 0.08726646259971647F);
        legLBackMiddle = new ModelRenderer(this, 31, 105);
        legLBackMiddle.setRotationPoint(1.1F, 6.5F, -0.3F);
        legLBackMiddle.addBox(-2.0F, 0.0F, -2.5F, 3, 5, 4, 0.0F);
        setRotateAngle(legLBackMiddle, 0.3141592653589793F, 0.0F, 0.08726646259971647F);
        legRFrontHoof = new ModelRenderer(this, 10, 88);
        legRFrontHoof.mirror = true;
        legRFrontHoof.setRotationPoint(0.0F, 5.9F, -0.1F);
        legRFrontHoof.addBox(-2.0F, 0.0F, -2.5F, 4, 3, 4, 0.0F);
        neckBase = new ModelRenderer(this, 95, 30);
        neckBase.setRotationPoint(0.0F, -2.5F, -2.8F);
        neckBase.addBox(-3.5F, -3.0F, -1.5F, 7, 7, 3, 0.0F);
        setRotateAngle(neckBase, -0.3490658503988659F, 0.0F, 0.0F);
        hornML1 = new ModelRenderer(this, 0, 0);
        hornML1.setRotationPoint(3.0F, -2.5F, -3.5F);
        hornML1.addBox(-0.5F, -1.0F, -1.0F, 3, 2, 2, 0.0F);
        setRotateAngle(hornML1, 0.0F, 0.5235987755982988F, 0.0F);
        hornMR3c = new ModelRenderer(this, 2, 2);
        hornMR3c.setRotationPoint(-1.21F, 0.2F, -0.4F);
        hornMR3c.addBox(-0.5F, -0.5F, -0.5F, 2, 1, 1, 0.0F);
        setRotateAngle(hornMR3c, 0.0F, -0.3490658503988659F, 0.0F);
        body = new ModelRenderer(this, 82, 88);
        body.setRotationPoint(0.0F, 9.0F, 1.5F);
        body.addBox(-6.0F, -8.1F, -4.0F, 12, 11, 11, 0.2F);
        setRotateAngle(body, 0.045553093477052F, 0.0F, 0.0F);
        hornMR2 = new ModelRenderer(this, 1, 55);
        hornMR2.setRotationPoint(-1.1F, 0.0F, -0.8F);
        hornMR2.addBox(-0.4F, -1.0F, -1.0F, 2, 2, 2, 0.0F);
        setRotateAngle(hornMR2, 0.0F, -0.6981317007977318F, 0.0F);
        legLBackHoof = new ModelRenderer(this, 30, 87);
        legLBackHoof.setRotationPoint(0.0F, 6.4F, -0.1F);
        legLBackHoof.addBox(-2.0F, 0.0F, -2.5F, 4, 3, 4, 0.0F);
        setRotateAngle(legLBackHoof, -0.13962634015954636F, 0.0F, 0.0F);
        teat3 = new ModelRenderer(this, 25, 66);
        teat3.setRotationPoint(2.0F, 2.5F, 1.4F);
        teat3.addBox(-0.5F, -1.0F, -0.5F, 1, 2, 1, 0.0F);
        hornFR2 = new ModelRenderer(this, 15, 58);
        hornFR2.setRotationPoint(-2.8F, 0.0F, -0.1F);
        hornFR2.addBox(-2.5F, -0.5F, -0.5F, 3, 1, 1, 0.0F);
        setRotateAngle(hornFR2, 0.0F, -0.6981317007977318F, 0.6981317007977318F);
        legRBackTop = new ModelRenderer(this, 30, 114);
        legRBackTop.mirror = true;
        legRBackTop.setRotationPoint(-3.9F, 3.5F, 8.0F);
        legRBackTop.addBox(-3.3F, -1.5F, -3.0F, 3, 9, 5, 0.0F);
        setRotateAngle(legRBackTop, 0.0F, 0.0F, 0.08726646259971647F);
        teat4 = new ModelRenderer(this, 25, 66);
        teat4.mirror = true;
        teat4.setRotationPoint(-2.0F, 2.5F, 1.4F);
        teat4.addBox(-0.5F, -1.0F, -0.5F, 1, 2, 1, 0.0F);
        hornFL1 = new ModelRenderer(this, 15, 62);
        hornFL1.setRotationPoint(3.5F, -2.5F, -4.0F);
        hornFL1.addBox(-0.5F, -0.5F, -0.5F, 3, 1, 1, 0.0F);
        setRotateAngle(hornFL1, 0.0F, 0.5235987755982988F, -0.3141592653589793F);
        tailBody = new ModelRenderer(this, 62, 78);
        tailBody.setRotationPoint(0.0F, 3.7F, 0.0F);
        tailBody.addBox(-0.5F, 0.0F, -0.5F, 1, 8, 1, 0.0F);
        setRotateAngle(tailBody, -0.27314402793711257F, 0.0F, 0.0F);
        teat1 = new ModelRenderer(this, 25, 66);
        teat1.setRotationPoint(2.0F, 2.5F, -1.7F);
        teat1.addBox(-0.5F, -1.0F, -0.5F, 1, 2, 1, 0.0F);
        neck = new ModelRenderer(this, 93, 18);
        neck.setRotationPoint(0.0F, -1.5F, -3.5F);
        neck.addBox(-3.0F, -4.4F, -4.6F, 6, 6, 6, 0.0F);
        setRotateAngle(neck, -0.7475245186291712F, 0.0F, 0.0F);
        mouthTop = new ModelRenderer(this, 6, 10);
        mouthTop.mirror = true;
        mouthTop.setRotationPoint(0.0F, 2.3F, -0.1F);
        mouthTop.addBox(-2.5F, -1.3F, -1.5F, 5, 5, 2, 0.0F);
        chestHump = new ModelRenderer(this, 91, 49);
        chestHump.setRotationPoint(0.0F, 6.9F, -6.5F);
        chestHump.addBox(-4.0F, -6.5F, -3.0F, 8, 14, 5, 0.0F);
        setRotateAngle(chestHump, -0.2617993877991494F, 0.0F, 0.0F);
        lefRFrontBottom = new ModelRenderer(this, 12, 95);
        lefRFrontBottom.mirror = true;
        lefRFrontBottom.setRotationPoint(0.6F, 4.5F, -0.4F);
        lefRFrontBottom.addBox(-1.5F, 0.0F, -1.5F, 3, 7, 3, 0.0F);
        setRotateAngle(lefRFrontBottom, -0.10471975511965977F, 0.0F, -0.05235987755982988F);
        hornML2 = new ModelRenderer(this, 1, 55);
        hornML2.setRotationPoint(2.2F, 0.0F, 0.0F);
        hornML2.addBox(-0.4F, -1.0F, -1.0F, 2, 2, 2, 0.0F);
        setRotateAngle(hornML2, 0.0F, 0.6981317007977318F, 0.0F);
        legRFrontTop = new ModelRenderer(this, 10, 114);
        legRFrontTop.mirror = true;
        legRFrontTop.setRotationPoint(-4.8F, 3.0F, -4.0F);
        legRFrontTop.addBox(-2.5F, -1.0F, -3.0F, 3, 9, 5, 0.0F);
        setRotateAngle(legRFrontTop, 0.017453292519943295F, 0.0F, 0.08726646259971647F);
        hornML4 = new ModelRenderer(this, 2, 48);
        hornML4.setRotationPoint(1.2F, -0.15F, -0.15F);
        hornML4.addBox(-0.4F, -0.5F, -0.5F, 2, 1, 1, 0.0F);
        setRotateAngle(hornML4, 0.0F, 0.2617993877991494F, 0.0F);
        tailTip = new ModelRenderer(this, 60, 71);
        tailTip.setRotationPoint(0.0F, 8.0F, 0.0F);
        tailTip.addBox(-1.0F, 0.0F, -1.0F, 2, 5, 2, 0.0F);
        setRotateAngle(tailTip, -0.045553093477052F, 0.0F, 0.0F);
        humpMain = new ModelRenderer(this, 40, 20);
        humpMain.setRotationPoint(0.0F, 1.4F, -3.6F);
        humpMain.addBox(-4.0F, -2.5F, -3.0F, 8, 5, 6, 0.2F);
        setRotateAngle(humpMain, -0.08726646259971647F, 0.0F, 0.0F);
        teat2 = new ModelRenderer(this, 25, 66);
        teat2.mirror = true;
        teat2.setRotationPoint(-2.0F, 2.5F, -1.7F);
        teat2.addBox(-0.5F, -1.0F, -0.5F, 1, 2, 1, 0.0F);
        chest = new ModelRenderer(this, 84, 68);
        chest.setRotationPoint(0.0F, 5.0F, -1.0F);
        chest.addBox(-6.5F, -4.5F, -7.5F, 13, 13, 7, -0.2F);
        setRotateAngle(chest, -0.045553093477052F, 0.0F, 0.0F);
        hornML3b = new ModelRenderer(this, 2, 52);
        hornML3b.setRotationPoint(1.92F, -0.2F, 0.2F);
        hornML3b.addBox(-0.5F, -0.5F, -0.5F, 2, 1, 1, 0.0F);
        setRotateAngle(hornML3b, 0.0F, 0.3490658503988659F, 0.0F);
        hornMR3b = new ModelRenderer(this, 2, 52);
        hornMR3b.setRotationPoint(-1.32F, -0.2F, -0.1F);
        hornMR3b.addBox(-0.5F, -0.5F, -0.5F, 2, 1, 1, 0.0F);
        setRotateAngle(hornMR3b, 0.0F, -0.3490658503988659F, 0.0F);
        legRBackMiddle = new ModelRenderer(this, 31, 105);
        legRBackMiddle.mirror = true;
        legRBackMiddle.setRotationPoint(-1.1F, 6.5F, -0.3F);
        legRBackMiddle.addBox(-2.0F, 0.0F, -2.5F, 3, 5, 4, 0.0F);
        setRotateAngle(legRBackMiddle, 0.3141592653589793F, 0.0F, -0.08726646259971647F);
        rump = new ModelRenderer(this, 86, 110);
        rump.setRotationPoint(0.0F, 6.9F, 15.0F);
        rump.addBox(-6.5F, -6.0F, -9.0F, 13, 12, 6, 0.0F);
        setRotateAngle(rump, -0.08726646259971647F, 0.0F, 0.0F);
        earR = new ModelRenderer(this, 46, 34);
        earR.mirror = true;
        earR.setRotationPoint(-2.0F, -2.0F, -2.2F);
        earR.addBox(-5.0F, -0.5F, -1.5F, 4, 1, 3, 0.0F);
        setRotateAngle(earR, 0.4363323129985824F, 0.08726646259971647F, -0.08726646259971647F);
        humpBack = new ModelRenderer(this, 44, 13);
        humpBack.setRotationPoint(0.0F, 0.0F, 2.8F);
        humpBack.addBox(-3.0F, -2.0F, -2.5F, 6, 3, 4, 0.0F);
        setRotateAngle(humpBack, -0.17453292519943295F, 0.0F, 0.0F);
        legLFrontHoof = new ModelRenderer(this, 10, 88);
        legLFrontHoof.setRotationPoint(0.0F, 5.9F, -0.1F);
        legLFrontHoof.addBox(-2.0F, 0.0F, -2.5F, 4, 3, 4, 0.0F);
        head = new ModelRenderer(this, 0, 25);
        head.setRotationPoint(0.0F, 0.5F, -4.5F);
        head.addBox(-3.5F, -4.5F, -4.5F, 7, 6, 6, 0.0F);
        setRotateAngle(head, -0.4363323129985824F, 0.0F, 0.0F);
        hornFR1 = new ModelRenderer(this, 15, 62);
        hornFR1.setRotationPoint(-3.5F, -2.5F, -4.0F);
        hornFR1.addBox(-3.0F, -0.5F, -0.5F, 3, 1, 1, 0.0F);
        setRotateAngle(hornFR1, 0.0F, -0.5235987755982988F, 0.3141592653589793F);
        legLFrontMiddle = new ModelRenderer(this, 11, 105);
        legLFrontMiddle.setRotationPoint(1.3F, 7.7F, 0.1F);
        legLFrontMiddle.addBox(-2.1F, 0.0F, -2.5F, 3, 5, 4, 0.0F);
        setRotateAngle(legLFrontMiddle, 0.08726646259971647F, 0.0F, 0.03490658503988659F);
        legRBackHoof = new ModelRenderer(this, 30, 87);
        legRBackHoof.mirror = true;
        legRBackHoof.setRotationPoint(0.0F, 6.4F, -0.1F);
        legRBackHoof.addBox(-2.0F, 0.0F, -2.5F, 4, 3, 4, 0.0F);
        setRotateAngle(legRBackHoof, -0.13962634015954636F, 0.0F, 0.0F);
        legRBackBottom = new ModelRenderer(this, 32, 94);
        legRBackBottom.mirror = true;
        legRBackBottom.setRotationPoint(-0.4F, 4.9F, -0.4F);
        legRBackBottom.addBox(-1.5F, -0.4F, -1.5F, 3, 8, 3, 0.0F);
        setRotateAngle(legRBackBottom, -0.17453292519943295F, 0.0F, 0.0F);
        hornML3c = new ModelRenderer(this, 2, 52);
        hornML3c.setRotationPoint(1.76F, 0.2F, -0.23F);
        hornML3c.addBox(-0.5F, -0.5F, -0.5F, 2, 1, 1, 0.0F);
        setRotateAngle(hornML3c, 0.0F, 0.3490658503988659F, 0.0F);
        legLBackBottom = new ModelRenderer(this, 32, 94);
        legLBackBottom.setRotationPoint(-0.6F, 4.9F, -0.4F);
        legLBackBottom.addBox(-1.5F, -0.4F, -1.5F, 3, 8, 3, 0.0F);
        setRotateAngle(legLBackBottom, -0.17453292519943295F, 0.0F, 0.0F);
        hornMR3a = new ModelRenderer(this, 2, 52);
        hornMR3a.setRotationPoint(-1.21F, -0.2F, -0.4F);
        hornMR3a.addBox(-0.5F, -0.5F, -0.5F, 2, 1, 1, 0.0F);
        setRotateAngle(hornMR3a, 0.0F, -0.3490658503988659F, 0.0F);
        rump.addChild(udders);
        head.addChild(hornMR1);
        hornFL1.addChild(hornFL2);
        legRFrontTop.addChild(legRFrontMiddle);
        rump.addChild(tailBase);
        head.addChild(mouthBottom);
        head.addChild(nose);
        hornML2.addChild(hornML3a);
        hornMR2.addChild(hornMR3d);
        hornML2.addChild(hornML3d);
        hornMR3d.addChild(hornMR4);
        legLFrontMiddle.addChild(legLFrontBottom);
        head.addChild(earL);
        legLBackTop.addChild(legLBackMiddle);
        lefRFrontBottom.addChild(legRFrontHoof);
        chestHump.addChild(neckBase);
        head.addChild(hornML1);
        hornMR2.addChild(hornMR3c);
        hornMR1.addChild(hornMR2);
        legLBackBottom.addChild(legLBackHoof);
        udders.addChild(teat3);
        hornFR1.addChild(hornFR2);
        udders.addChild(teat4);
        head.addChild(hornFL1);
        tailBase.addChild(tailBody);
        udders.addChild(teat1);
        chestHump.addChild(neck);
        head.addChild(mouthTop);
        legRFrontMiddle.addChild(lefRFrontBottom);
        hornML1.addChild(hornML2);
        hornML3d.addChild(hornML4);
        tailBody.addChild(tailTip);
        udders.addChild(teat2);
        hornML2.addChild(hornML3b);
        hornMR2.addChild(hornMR3b);
        legRBackTop.addChild(legRBackMiddle);
        head.addChild(earR);
        humpMain.addChild(humpBack);
        legLFrontBottom.addChild(legLFrontHoof);
        neck.addChild(head);
        head.addChild(hornFR1);
        legLFrontTop.addChild(legLFrontMiddle);
        legRBackBottom.addChild(legRBackHoof);
        legRBackMiddle.addChild(legRBackBottom);
        hornML2.addChild(hornML3c);
        legLBackMiddle.addChild(legLBackBottom);
        hornMR2.addChild(hornMR3a);
    }

    @Override
    public void render(@Nonnull Entity entity, float par2, float par3, float par4, float par5, float par6, float par7)
    {
        this.setRotationAngles(par2, par3, par4, par5, par6, par7, entity);
        EntityZebuTFC zebu = ((EntityZebuTFC) entity);

        float percent = (float) zebu.getPercentToAdulthood();
        float ageScale = 2.0F - percent;

        if (zebu.getGender() == EntityAnimalTFC.Gender.MALE)
        {
           udders.isHidden = true;
           hornFR1.isHidden = true;
           hornFL1.isHidden = true;

        }
        else
        {
           hornMR1.isHidden = true;
           hornML1.isHidden = true;
        }

        GlStateManager.pushMatrix();
        GlStateManager.scale(1 / ageScale, 1 / ageScale, 1 / ageScale);
        GlStateManager.translate(0.0F, 1.5f - (1.5f * percent), 0f);

        legLFrontTop.render(par7);
        legLBackTop.render(par7);
        body.render(par7);
        legRBackTop.render(par7);
        chestHump.render(par7);
        legRFrontTop.render(par7);
        humpMain.render(par7);
        chest.render(par7);
        rump.render(par7);
        GlStateManager.popMatrix();
    }

    @Override
    public void setRotationAngles(float par1, float par2, float par3, float par4, float par5, float par6, Entity ent)
    {
        this.head.rotateAngleX = par5 / (180F / (float) Math.PI);
        this.head.rotateAngleY = par4 / (180F / (float) Math.PI);
        this.legRFrontTop.rotateAngleX = MathHelper.cos(par1 * 0.6662F) * 1.4F * par2;
        this.legLFrontTop.rotateAngleX = MathHelper.cos(par1 * 0.6662F + (float) Math.PI) * 1.4F * par2;
        this.legRBackTop.rotateAngleX = MathHelper.cos(par1 * 0.6662F + (float) Math.PI) * 1.4F * par2;
        this.legLBackTop.rotateAngleX = MathHelper.cos(par1 * 0.6662F) * 1.4F * par2;

        hornMR1.isHidden = false;
        hornML1.isHidden = false;
        hornFR1.isHidden = false;
        hornFL1.isHidden = false;
        udders.isHidden = false;
    }

    public void setRotateAngle(ModelRenderer modelRenderer, float x, float y, float z) {
        modelRenderer.rotateAngleX = x;
        modelRenderer.rotateAngleY = y;
        modelRenderer.rotateAngleZ = z;
    }
}