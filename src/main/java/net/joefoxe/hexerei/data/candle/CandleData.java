package net.joefoxe.hexerei.data.candle;

public class CandleData {

    public int type;
    public boolean returnToBlock;
    public float x;
    public float y;
    public float z;
    public int height;
    public boolean lit;
    public int meltTimer;
    public static int meltTimerMAX = 6000;
    public int dyeColor = 0x422F1E;

    public CandleEffect effect;

    public CandleData(int type, boolean returnToBlock, float x, float y, float z, int height, int meltTimer, CandleEffect effect){
        this.type = type;
        this.returnToBlock = returnToBlock;
        this.x = x;
        this.y = y;
        this.z = z;
        this.height = height;
        this.meltTimer = meltTimer;
        this.effect = effect;
    }

    public CandleEffect getEffect() {
        return effect;
    }


}
