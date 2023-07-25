package gregtech.common.tileentities.machines.multi;

import static com.gtnewhorizon.structurelib.structure.StructureUtility.*;
import static gregtech.api.enums.GT_HatchElement.*;
import static gregtech.api.enums.GT_Values.AuthorTheEpicGamer274;
import static gregtech.api.enums.Textures.BlockIcons.*;
import static gregtech.api.enums.Textures.BlockIcons.casingTexturePages;
import static gregtech.api.util.GT_StructureUtility.buildHatchAdder;
import static gregtech.common.tileentities.machines.multi.GT_MetaTileEntity_PlasmaForge.*;
import static net.minecraft.util.EnumChatFormatting.GOLD;
import static net.minecraft.util.EnumChatFormatting.GRAY;

import javax.annotation.Nonnull;

import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.ForgeDirection;

import org.jetbrains.annotations.NotNull;

import com.gtnewhorizon.structurelib.alignment.constructable.ISurvivalConstructable;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.ISurvivalBuildEnvironment;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;

import gregtech.api.GregTech_API;
import gregtech.api.interfaces.IGlobalWirelessEnergy;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.interfaces.tileentity.IVoidable;
import gregtech.api.logic.ProcessingLogic;
import gregtech.api.metatileentity.implementations.GT_MetaTileEntity_EnhancedMultiBlockBase;
import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.recipe.check.CheckRecipeResultRegistry;
import gregtech.api.render.TextureFactory;
import gregtech.api.util.*;

public class GT_MetaTileEntity_CircuitAssemblyMulti
    extends GT_MetaTileEntity_EnhancedMultiBlockBase<GT_MetaTileEntity_CircuitAssemblyMulti>
    implements ISurvivalConstructable, IGlobalWirelessEnergy {

    private static final String STRUCTURE_PIECE_MAIN = "main";
    protected IVoidable machine;
    protected int mCraftingTier = 0;
    private static final IStructureDefinition<GT_MetaTileEntity_CircuitAssemblyMulti> STRUCTURE_DEFINITION = StructureDefinition
        .<GT_MetaTileEntity_CircuitAssemblyMulti>builder()
        .addShape(
            STRUCTURE_PIECE_MAIN,
            transpose(new String[][] { { "hhh", "hhh", "hhh" }, { "h~h", "h-h", "hhh" }, { "hhh", "hhh", "hhh" }, }))
        .addElement(
            'h',
            buildHatchAdder(GT_MetaTileEntity_CircuitAssemblyMulti.class)
                .atLeast(InputHatch, OutputBus, InputBus, Maintenance)
                .casingIndex(DIM_INJECTION_CASING)
                .dot(1)
                .buildAndChain(GregTech_API.sBlockCasings1, DIM_INJECTION_CASING))
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
    public IStructureDefinition<GT_MetaTileEntity_CircuitAssemblyMulti> getStructureDefinition() {
        return STRUCTURE_DEFINITION;
    }

    @Override
    protected GT_Multiblock_Tooltip_Builder createTooltip() {
        final GT_Multiblock_Tooltip_Builder tt = new GT_Multiblock_Tooltip_Builder();
        tt.addMachineType("Circuit Assembler")
            .addInfo("Assisting in all your Circuit needs.")
            .addInfo("All EU is deducted from wireless EU networks only.")
            .addInfo(AuthorTheEpicGamer274)
            .addSeparator()
            .beginStructureBlock(5, 7, 5, false)
            .addStructureInfo(GOLD + "1+ " + GRAY + "Input Hatch")
            .addStructureInfo(GOLD + "1+ " + GRAY + "Output Hatch")
            .addStructureInfo(GOLD + "1+ " + GRAY + "Input Bus")
            .addStructureInfo(GOLD + "1 " + GRAY + "Maintenance Hatch")
            .toolTipFinisher("Gregtech");
        return tt;
    }

    @Override
    public GT_Recipe.GT_Recipe_Map getRecipeMap() {
        return GT_Recipe.GT_Recipe_Map.sCircuitAssemblerMulti;
    }

    private String ownerUUID;
    int multiplier = 1;
    long mWirelessEUt = 0;

    @Override
    protected long getActualEnergyUsage() {
        return mWirelessEUt;
    }

    @Override
    protected ProcessingLogic createProcessingLogic() {
        return new ProcessingLogic() {

            @NotNull
            @Override
            protected GT_ParallelHelper createParallelHelper(@NotNull GT_Recipe recipe) {
                return new GT_ParallelHelper().setRecipe(recipe)
                    .setItemInputs(inputItems)
                    .setFluidInputs(inputFluids)
                    .setAvailableEUt(availableVoltage * availableAmperage)
                    .setMachine(machine, protectItems, protectFluids)
                    .setRecipeLocked(recipeLockableMachine, isRecipeLocked)
                    .setMaxParallel(getCraftingTier() * 6)
                    .enableBatchMode(batchSize)
                    .enableOutputCalculation();
            }

            @NotNull
            @Override
            protected CheckRecipeResult validateRecipe(@Nonnull GT_Recipe recipe) {
                mWirelessEUt = 10L * (long) recipe.mEUt * (long) multiplier;
                if (!addEUToGlobalEnergyMap(ownerUUID, -mWirelessEUt * recipe.mDuration)) {
                    return CheckRecipeResultRegistry.insufficientPower(mWirelessEUt * recipe.mDuration);
                }
                if (recipe.mSpecialValue > getCraftingTier()) {
                    return CheckRecipeResultRegistry.insufficientMachineTier(recipe.mSpecialValue);
                }
                return CheckRecipeResultRegistry.SUCCESSFUL;
            }

            @Override
            protected double calculateDuration(@Nonnull GT_Recipe recipe, @Nonnull GT_ParallelHelper helper,
                @Nonnull GT_OverclockCalculator calculator) {
                return calculator.getDuration();
            }

            @Nonnull
            @Override
            protected GT_OverclockCalculator createOverclockCalculator(@Nonnull GT_Recipe recipe,
                @Nonnull GT_ParallelHelper helper) {
                return new GT_OverclockCalculator().setRecipeEUt(recipe.mEUt)
                    .setParallel((int) Math.floor(helper.getCurrentParallel() / helper.getDurationMultiplierDouble()))
                    .setDuration((int) Math.ceil(recipe.mDuration * helper.getDurationMultiplierDouble()))
                    .setAmperage(availableAmperage)
                    .setEUt(availableVoltage)
                    .setDurationDecreasePerOC(overClockTimeReduction)
                    .setEUtIncreasePerOC(overClockPowerIncrease);
            }

            @NotNull
            @Override
            public CheckRecipeResult process() {
                CheckRecipeResult result = super.process();
                // Power will be directly consumed through wireless
                setCalculatedEut(0);
                return result;
            }
        };
    }

    @Override
    protected void setProcessingLogicPower(ProcessingLogic logic) {
        // The voltage is only used for recipe finding
        logic.setAvailableVoltage(Long.MAX_VALUE);
        logic.setAvailableAmperage(1);
    }

    private void setCraftingTier(int tier) {
        mCraftingTier = tier;
    }

    private int getCraftingTier() {
        return mCraftingTier;
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new GT_MetaTileEntity_CircuitAssemblyMulti(mName);
    }

    @Override
    public void onPreTick(IGregTechTileEntity aBaseMetaTileEntity, long aTick) {

        super.onPreTick(aBaseMetaTileEntity, aTick);

        if (aBaseMetaTileEntity.isServerSide() && (aTick == 1)) {
            // Adds player to the wireless network if they do not already exist on it.
            ownerUUID = processInitialSettings(aBaseMetaTileEntity);
        }
    }

    public boolean checkMachine(IGregTechTileEntity aBaseMetaTileEntity, ItemStack aStack) {

        // Check the main structure
        if (!checkPiece(STRUCTURE_PIECE_MAIN, 1, 1, 0)) {
            return false;
        }

        return (mMaintenanceHatches.size() == 1);
    }

    @Override
    public void construct(ItemStack stackSize, boolean hintsOnly) {
        buildPiece(STRUCTURE_PIECE_MAIN, stackSize, hintsOnly, 1, 1, 0);
    }

    @Override
    public int survivalConstruct(ItemStack stackSize, int elementBudget, ISurvivalBuildEnvironment env) {
        if (mMachine) return -1;
        return survivialBuildPiece(STRUCTURE_PIECE_MAIN, stackSize, 1, 1, 0, elementBudget, env, false, true);
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
