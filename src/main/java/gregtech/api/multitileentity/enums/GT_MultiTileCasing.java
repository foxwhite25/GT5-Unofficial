package gregtech.api.multitileentity.enums;

import static gregtech.api.util.GT_StructureUtilityMuTE.createMuTEStructureCasing;
import static gregtech.loaders.preload.GT_Loader_MultiTileEntities.CASING_REGISTRY_NAME;

import gregtech.api.enums.GT_Values;
import gregtech.api.util.GT_StructureUtilityMuTE;

public enum GT_MultiTileCasing {

    CokeOven(0),
    Chemical(1),
    Distillation(2),
    Macerator(18000),
    NONE(GT_Values.W);

    private final int meta;
    private final GT_StructureUtilityMuTE.MuTEStructureCasing casing;

    GT_MultiTileCasing(int meta) {
        this.meta = meta;
        casing = createMuTEStructureCasing(CASING_REGISTRY_NAME, meta);
    }

    public int getId() {
        return meta;
    }

    public short getRegistryId() {
        return (short) casing.getRegistryId();
    }

    public GT_StructureUtilityMuTE.MuTEStructureCasing getCasing() {
        return casing;
    }
}
