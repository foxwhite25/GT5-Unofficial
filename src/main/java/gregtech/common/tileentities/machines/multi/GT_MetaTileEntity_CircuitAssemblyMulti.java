package gregtech.common.tileentities.machines.multi;

import static com.gtnewhorizon.structurelib.structure.StructureUtility.ofBlock;
import static com.gtnewhorizon.structurelib.structure.StructureUtility.transpose;
import static gregtech.api.enums.GT_HatchElement.*;
import static gregtech.api.enums.GT_HatchElement.ExoticEnergy;
import static gregtech.api.enums.Textures.BlockIcons.*;
import static gregtech.api.enums.Textures.BlockIcons.casingTexturePages;
import static gregtech.api.util.GT_StructureUtility.buildHatchAdder;
import static gregtech.api.util.GT_StructureUtility.ofFrame;
import static gregtech.common.tileentities.machines.multi.GT_MetaTileEntity_PlasmaForge.DIM_TRANS_CASING;
import static java.lang.Math.max;
import static net.minecraft.util.EnumChatFormatting.GOLD;
import static net.minecraft.util.EnumChatFormatting.GRAY;

import javax.annotation.Nonnull;

import com.gtnewhorizon.structurelib.structure.ISurvivalBuildEnvironment;
import gregtech.api.render.TextureFactory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;

import org.jetbrains.annotations.NotNull;

import com.gtnewhorizon.structurelib.alignment.constructable.ISurvivalConstructable;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;

import gregtech.api.GregTech_API;
import gregtech.api.enums.Materials;
import gregtech.api.interfaces.IGlobalWirelessEnergy;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.interfaces.tileentity.IVoidable;
import gregtech.api.logic.ProcessingLogic;
import gregtech.api.metatileentity.implementations.GT_MetaTileEntity_EnhancedMultiBlockBase;
import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.recipe.check.CheckRecipeResultRegistry;
import gregtech.api.util.GT_Multiblock_Tooltip_Builder;
import gregtech.api.util.GT_OverclockCalculator;
import gregtech.api.util.GT_ParallelHelper;
import gregtech.api.util.GT_Recipe;
import gregtech.common.blocks.GT_Block_Casings8;
import gregtech.common.items.GT_IntegratedCircuit_Item;

public class GT_MetaTileEntity_CircuitAssemblyMulti
    extends GT_MetaTileEntity_EnhancedMultiBlockBase<GT_MetaTileEntity_CircuitAssemblyMulti>
    implements ISurvivalConstructable, IGlobalWirelessEnergy {

    private static final String STRUCTURE_PIECE_MAIN = "main";
    protected IVoidable machine;
    private static final IStructureDefinition<GT_MetaTileEntity_CircuitAssemblyMulti> STRUCTURE_DEFINITION = StructureDefinition
        .<GT_MetaTileEntity_CircuitAssemblyMulti>builder()
        // spotless:off
        .addShape(STRUCTURE_PIECE_MAIN, transpose(new String[][] {
            {"         ","         ","    F    ","    C    ","    ~    ","    C    ","    C    ","    F    ","         ","         "},
        }))
        //spotless:on
        .addElement('F', ofFrame(Materials.StellarAlloy))
        .addElement('C', ofBlock(GregTech_API.sBlockCasings8, 10))
        .addElement('A', ofBlock(GregTech_API.sBlockCasings2, 5))
        .addElement(
            'B',
            buildHatchAdder(GT_MetaTileEntity_CircuitAssemblyMulti.class)
                .atLeast(InputHatch, OutputBus, InputBus, Maintenance, Energy.or(ExoticEnergy))
                .dot(1)
                .casingIndex(((GT_Block_Casings8) GregTech_API.sBlockCasings8).getTextureIndex(10))
                .buildAndChain(GregTech_API.sBlockCasings8, 10))
        .build();

    public GT_MetaTileEntity_CircuitAssemblyMulti(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    public GT_MetaTileEntity_CircuitAssemblyMulti(String aName) {
        super(aName);
    }

    @Override
    public boolean isCorrectMachinePart(ItemStack aStack) {
        return false;
    }

    @Override
    public boolean checkMachine(IGregTechTileEntity aBaseMetaTileEntity, ItemStack aStack) {
        return false;
    }

    @Override
    public int getMaxEfficiency(ItemStack aStack) {
        return 0;
    }

    @Override
    public int getDamageToComponent(ItemStack aStack) {
        return 0;
    }

    @Override
    public boolean explodesOnComponentBreak(ItemStack aStack) {
        return false;
    }

    @Override
    public void construct(ItemStack stackSize, boolean hintsOnly) {

    }

    @Override
    public IStructureDefinition<GT_MetaTileEntity_CircuitAssemblyMulti> getStructureDefinition() {
        return null;
    }

    @Override
    protected GT_Multiblock_Tooltip_Builder createTooltip() {
        return null;
    }

    @Override
    public GT_Recipe.GT_Recipe_Map getRecipeMap() {
        return GT_Recipe.GT_Recipe_Map.sNanoForge;
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new GT_MetaTileEntity_CircuitAssemblyMulti(this.mName) {

            @Override
            public IStructureDefinition<GT_MetaTileEntity_CircuitAssemblyMulti> getStructureDefinition() {
                return STRUCTURE_DEFINITION;
            }

            @Override
            protected GT_Multiblock_Tooltip_Builder createTooltip() {
                final GT_Multiblock_Tooltip_Builder tt = new GT_Multiblock_Tooltip_Builder();
                tt.addMachineType("Circuit Assembler")
                    .addInfo("Assisting in all your Circuit needs.")
                    .addInfo("All EU is deducted from wireless EU networks only.")
                    .addInfo("AuthorTheEpicGamer274")
                    .addSeparator()
                    .beginStructureBlock(5, 7, 5, false)
                    .addStructureInfo(GOLD + "1+ " + GRAY + "Input Hatch")
                    .addStructureInfo(GOLD + "1+ " + GRAY + "Output Hatch")
                    .addStructureInfo(GOLD + "1+ " + GRAY + "Input Bus")
                    .addStructureInfo(GOLD + "1 " + GRAY + "Maintenance Hatch")
                    .toolTipFinisher("Gregtech");
                return new GT_Multiblock_Tooltip_Builder();
            }

            private String ownerUUID;
            int multiplier = 1;
            long mWirelessEUt = 0;

            @Override
            protected ProcessingLogic createProcessingLogic() {
                return new ProcessingLogic() {

                    @NotNull
                    @Override
                    protected CheckRecipeResult validateRecipe(@Nonnull GT_Recipe recipe) {
                        mWirelessEUt = 10L * (long) recipe.mEUt * (long) multiplier;
                        if (!addEUToGlobalEnergyMap(ownerUUID, -mWirelessEUt * recipe.mDuration)) {
                            return CheckRecipeResultRegistry.insufficientPower(mWirelessEUt * recipe.mDuration);
                        }
                        return CheckRecipeResultRegistry.SUCCESSFUL;
                    }

                    @Nonnull
                    @Override
                    protected GT_OverclockCalculator createOverclockCalculator(@Nonnull GT_Recipe recipe,
                        @Nonnull GT_ParallelHelper helper) {
                        return GT_OverclockCalculator.ofNoOverclock(recipe);
                    }

                    @NotNull
                    @Override
                    public CheckRecipeResult process() {
                        CheckRecipeResult result = super.process();
                        // Power will be directly consumed through wireless
                        setCalculatedEut(0);
                        return result;
                    }
                }.setMaxParallelSupplier(() -> {
                    ItemStack controllerStack = getControllerSlot();
                    if (controllerStack != null && controllerStack.getItem() instanceof GT_IntegratedCircuit_Item) {
                        multiplier = controllerStack.stackSize * max(1, controllerStack.getItemDamage());
                    }
                    return multiplier;
                });
            }

            @Override
            protected void setProcessingLogicPower(ProcessingLogic logic) {
                // The voltage is only used for recipe finding
                logic.setAvailableVoltage(Long.MAX_VALUE);
                logic.setAvailableAmperage(1);
            }
            @Override
            public void saveNBTData(NBTTagCompound aNBT) {
                aNBT.setInteger("eMultiplier", multiplier);
                super.saveNBTData(aNBT);
            }

            @Override
            public void loadNBTData(final NBTTagCompound aNBT) {
                multiplier = aNBT.getInteger("eMultiplier");
                super.loadNBTData(aNBT);
            }

            @Override
            public void construct(ItemStack stackSize, boolean hintsOnly) {
                buildPiece(STRUCTURE_PIECE_MAIN, stackSize, hintsOnly, 0, 1, 0);
            }
            private static final int HORIZONTAL_OFFSET = 2;
            private static final int VERTICAL_OFFSET = 3;
            private static final int DEPTH_OFFSET = 0;

            @Override
            public int survivalConstruct(ItemStack stackSize, int elementBudget, ISurvivalBuildEnvironment env) {
                return survivialBuildPiece(
                    STRUCTURE_PIECE_MAIN,
                    stackSize,
                    HORIZONTAL_OFFSET,
                    VERTICAL_OFFSET,
                    DEPTH_OFFSET,
                    elementBudget,
                    env,
                    false,
                    true);
            }

            @Override
            public boolean isCorrectMachinePart(ItemStack aStack) {
                return false;
            }

            @Override
            public boolean checkMachine(IGregTechTileEntity aBaseMetaTileEntity, ItemStack aStack) {
                if (!checkExoticAndNormalEnergyHatches()) {
                }
                return false;
            }

            @Override
            public int getMaxEfficiency(ItemStack aStack) {
                return 10000;
            }

            @Override
            public int getDamageToComponent(ItemStack aStack) {
                return 0;
            }

            @Override
            public boolean explodesOnComponentBreak(ItemStack aStack) {
                return false;
            }

            @Override
            public ITexture[] getTexture(IGregTechTileEntity baseMetaTileEntity, ForgeDirection side,
                ForgeDirection facing, int colorIndex, boolean active, boolean redstoneLevel) {
                return new ITexture[0];
            }
        };
    }

    @Override
    public ITexture[] getTexture(IGregTechTileEntity aBaseMetaTileEntity, ForgeDirection side, ForgeDirection aFacing,
                                 int colorIndex, boolean aActive, boolean redstoneLevel) {
        if (side == aFacing) {
            if (aActive) return new ITexture[] { casingTexturePages[0][DIM_TRANS_CASING], TextureFactory.builder()
                .addIcon(OVERLAY_DTPF_ON)
                .extFacing()
                .build(),
                TextureFactory.builder()
                    .addIcon(OVERLAY_FUSION1_GLOW)
                    .extFacing()
                    .glow()
                    .build() };
            return new ITexture[] { casingTexturePages[0][DIM_TRANS_CASING], TextureFactory.builder()
                .addIcon(OVERLAY_DTPF_OFF)
                .extFacing()
                .build() };
        }

        return new ITexture[] { casingTexturePages[0][DIM_TRANS_CASING] };
    }
}
