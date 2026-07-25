package cn.mapway.common.geo.gdal;


public class CloseableTileCanvas extends TileCanvas implements AutoCloseable {
    public CloseableTileCanvas(long x, long y, int zoom) {
        super(x, y, zoom);
    }

    @Override
    public void close() {
        if (graphics != null) {
            graphics.dispose();
            graphics = null;
        }
        if (buffer != null) {
            buffer.flush();
            buffer = null;
        }
    }
}
