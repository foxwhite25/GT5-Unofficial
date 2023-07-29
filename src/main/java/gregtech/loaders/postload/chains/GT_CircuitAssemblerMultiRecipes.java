package gregtech.loaders.postload.chains;

import static gregtech.api.util.GT_Recipe.GT_Recipe_Map.sCircuitAssemblerMulti;
import static gregtech.api.util.GT_Recipe.GT_Recipe_Map.sTranscendentPlasmaMixerRecipes;
import static gregtech.api.util.GT_RecipeBuilder.SECONDS;

import gregtech.api.enums.*;
import gregtech.api.util.GT_OreDictUnificator;
import gregtech.api.util.GT_Utility;

public class GT_CircuitAssemblerMultiRecipes {

    public static void run() {

        GT_Values.RA.stdBuilder()
            .itemInputs(
                ItemList.Steam_Valve_IV.get(1),
                GT_OreDictUnificator.get(OrePrefixes.circuit.get(Materials.Elite), 2),
                GT_Utility.getIntegratedCircuit(3))
            .itemOutputs(ItemList.Steam_Regulator_IV.get(1))
            .noFluidInputs()
            .noFluidOutputs()
            .duration(10 * SECONDS)
            .eut(TierEU.RECIPE_IV)
            .addTo(sCircuitAssemblerMulti);
        GT_Values.RA.stdBuilder()
            .itemInputs(GT_Utility.getIntegratedCircuit(1))
            .noItemOutputs()
            .fluidInputs(
                Materials.Helium.getPlasma(1000),
                Materials.Iron.getPlasma(1000),
                Materials.Calcium.getPlasma(1000),
                Materials.Niobium.getPlasma(1000))
            .fluidOutputs(MaterialsUEVplus.ExcitedDTCC.getFluid(1000L))
            .duration(100)
            .eut(TierEU.RECIPE_UIV)
            .noOptimize()
            .addTo(sCircuitAssemblerMulti);
    }
}
