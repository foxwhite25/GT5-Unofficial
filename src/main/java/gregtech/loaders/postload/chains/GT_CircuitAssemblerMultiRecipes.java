package gregtech.loaders.postload.chains;

import static gregtech.api.util.GT_Recipe.GT_Recipe_Map.sCircuitAssemblerMulti;
import static gregtech.api.util.GT_RecipeBuilder.SECONDS;
import static gregtech.common.tileentities.machines.multi.GT_MetaTileEntity_CircuitAssemblyMulti.CraftingTier;

import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import gregtech.api.enums.*;
import gregtech.api.util.GT_OreDictUnificator;

public class GT_CircuitAssemblerMultiRecipes {

    private static final int WETWARE_EU_T = 300000;

    public static void run() {

        Fluid solderUEV = FluidRegistry.getFluid("molten.mutatedlivingsolder") != null
            ? FluidRegistry.getFluid("molten.mutatedlivingsolder")
            : FluidRegistry.getFluid("molten.solderingalloy");

        Fluid solderIndalloy = FluidRegistry.getFluid("molten.indalloy140") != null
            ? FluidRegistry.getFluid("molten.indalloy140")
            : FluidRegistry.getFluid("molten.solderingalloy");

        GT_Values.RA.stdBuilder()
            .itemInputs(
                GT_OreDictUnificator.get(OrePrefixes.frameGt, Materials.Tritanium, 2),
                ItemList.Circuit_Wetwaresupercomputer.get(2L),
                ItemList.Circuit_Parts_InductorASMD.get(16L),
                ItemList.Circuit_Parts_CapacitorASMD.get(16L),
                ItemList.Circuit_Parts_ResistorASMD.get(16L),
                ItemList.Circuit_Parts_TransistorASMD.get(16L),
                ItemList.Circuit_Parts_DiodeASMD.get(16L),
                ItemList.Circuit_Chip_Ram.get(48L),
                GT_OreDictUnificator.get(OrePrefixes.wireGt01, Materials.SuperconductorZPM, 64L),
                new Object[] { OrePrefixes.foil.get(Materials.AnySyntheticRubber), 64L })
            .fluidInputs(
                new FluidStack(solderIndalloy, 2880),
                new FluidStack(FluidRegistry.getFluid("ic2coolant"), 10000),
                Materials.Radon.getGas(2500L))
            .itemOutputs(ItemList.Circuit_Wetwaremainframe.get(1))
            .specialValue(CraftingTier = 8)
            .noFluidOutputs()
            .duration(20 * SECONDS)
            .eut(WETWARE_EU_T)
            .addTo(sCircuitAssemblerMulti);
        GT_Values.RA.stdBuilder()
            .itemInputs(
                GT_OreDictUnificator.get(OrePrefixes.frameGt, Materials.Tritanium, 2),
                ItemList.Circuit_Wetwaresupercomputer.get(2L),
                ItemList.Circuit_Parts_InductorXSMD.get(4L),
                ItemList.Circuit_Parts_CapacitorXSMD.get(4L),
                ItemList.Circuit_Parts_ResistorXSMD.get(4L),
                ItemList.Circuit_Parts_TransistorXSMD.get(4L),
                ItemList.Circuit_Parts_DiodeXSMD.get(4L),
                ItemList.Circuit_Chip_Ram.get(48L),
                GT_OreDictUnificator.get(OrePrefixes.wireGt01, Materials.SuperconductorZPM, 64L),
                new Object[] { OrePrefixes.foil.get(Materials.AnySyntheticRubber), 64L })
            .fluidInputs(
                new FluidStack(solderIndalloy, 2880),
                new FluidStack(FluidRegistry.getFluid("ic2coolant"), 10000),
                Materials.Radon.getGas(2500L))
            .itemOutputs(ItemList.Circuit_Wetwaremainframe.get(1))
            .noFluidOutputs()
            .duration(20 * SECONDS)
            .eut(WETWARE_EU_T)
            .addTo(sCircuitAssemblerMulti);
    }
}
