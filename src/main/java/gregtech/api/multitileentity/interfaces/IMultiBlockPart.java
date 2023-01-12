package gregtech.api.multitileentity.interfaces;

import net.minecraft.util.ChunkCoordinates;

public interface IMultiBlockPart extends IMultiTileEntity {
    ChunkCoordinates getTargetPos();

    void setTargetPos(ChunkCoordinates aTargetPos);

    boolean tickCoverAtSide(byte aSide, long aTickTimer);
}
