package cn.mapway.common.geo.sqlite;

import cn.mapway.common.geo.gdal.GdalUtil;
import cn.mapway.common.geo.tools.TileTools;
import lombok.extern.slf4j.Slf4j;
import org.gdal.gdal.gdal;
import org.gdal.ogr.*;

import java.awt.*;
import java.util.Locale;

/**
 * SqliteMapTools
 * 基于.S文件格式的操作
 *
 * @author zhangjianshe <zhangjianshe@gmail.com>
 */
@Slf4j
public class SqliteMapTools extends MapRender {


    DataSource worldDataSource;
    DataSource provinceDataSource;
    DataSource districtDataSource;
    TileTools tileTools;
    Font provinceFont;

    public static void main(String[] args) {
        SqliteMapTools tools = new SqliteMapTools();
        tools.range(0, 5);
        tools.export();
    }

    /**
     * 初始化之前做的事情
     */
    @Override
    public void initialize() {
        String world = "d:\\data\\World\\world_countries_2020.shp";
        String china = "d:\\data\\china\\chn_admbnda_adm0_ocha_2020.shp";
        String province = "d:\\data\\china\\chn_admbnda_adm1_ocha_2020.shp";
        String district = "d:\\data\\china\\chn_admbnda_adm2_ocha_2020.shp";

        GdalUtil gdalUtil = new GdalUtil();
        gdal.SetConfigOption("SHAPE_RESTORE_SHX", "YES");
        String strdrivername = "ESRI Shapefile";
        Driver odriver = ogr.GetDriverByName(strdrivername);

        log.info("world info @ {}", world);
        worldDataSource = odriver.Open(world);
        provinceDataSource = odriver.Open(province);
        districtDataSource = odriver.Open(district);
        tileTools = new TileTools("d:\\Data");

        provinceFont = new Font("黑体", Font.BOLD, 24);

    }

    @Override
    public TileTools getTileTools() {
        return tileTools;
    }

    @Override
    public void exportLevel(TileTools tileTools, int zoomLevel) {
        switch (zoomLevel) {
            case 0:
                commonDrawer(tileTools, zoomLevel, (g, tx, ty, zoom, filter) -> {
                    drawWorld(g, tx, ty, zoom, filter);
                });
                break;
            case 1:
                commonDrawer(tileTools, zoomLevel, (g, tx, ty, zoom, filter) -> {
                    drawWorld(g, tx, ty, zoom, filter);
                });
                break;
            case 2:
                commonDrawer(tileTools, zoomLevel, (g, tx, ty, zoom, filter) -> {
                    drawWorld(g, tx, ty, zoom, filter);
                });
                break;
            case 3:
                commonDrawer(tileTools, zoomLevel, (g, tx, ty, zoom, filter) -> {
                    drawWorld(g, tx, ty, zoom, filter);
                });
                break;
            case 4:
                commonDrawer(tileTools, 4, (g, tx, ty, zoom, filter) -> {
                    drawWorld(g, tx, ty, zoom, filter);
                    drawProvince(g, tx, ty, zoom, filter);
                });
                break;
            case 5:
                commonDrawer(tileTools, 5, (g, tx, ty, zoom, filter) -> {
                    drawWorld(g, tx, ty, zoom, filter);
                    drawProvince(g, tx, ty, zoom, filter);
                });
                break;
            case 6:
                commonDrawer(tileTools, 6, (g, tx, ty, zoom, filter) -> {
                    drawWorld(g, tx, ty, zoom, filter);
                    drawProvince(g, tx, ty, zoom, filter);
                    drawDistrict(g, tx, ty, zoom, filter);
                });
                break;
        }
    }

    private void drawWorld(Graphics2D g, double tx, double ty, int zoom, Geometry filter) {
        Layer world = worldDataSource.GetLayer(0);
        world.SetSpatialFilter(filter);
        world.ResetReading();
        Feature feature = world.GetNextFeature();
        while (feature != null) {
            Geometry geometry = feature.GetGeomFieldRef(0);
            String name = feature.GetFieldAsString("CNTRY_NAME").toLowerCase(Locale.ROOT);
            MapStyle style = new MapStyle();
            if (name.equals("china") || name.equals("taiwan")) {
                style.setFill(true);
                style.setFillColor(Color.RED);
                style.setBorderColor(Color.YELLOW);
                style.setShowName(false);
                style.setFont(provinceFont);
            } else {
                style.setShowName(false);
                style.setFill(false);
                style.setBorderColor(Color.GRAY);
            }
            drawGeometry(name, g, tx, ty, geometry, zoom, style);
            feature = world.GetNextFeature();
        }
    }

    private void drawProvince(Graphics2D g, double tx, double ty, int zoom, Geometry filter) {
        Layer layer = provinceDataSource.GetLayer(0);
        layer.SetSpatialFilter(filter);
        layer.ResetReading();
        Feature feature = layer.GetNextFeature();
        while (feature != null) {
            Geometry geometry = feature.GetGeomFieldRef(0);
            String name = feature.GetFieldAsString("ADM1_ZH").toLowerCase(Locale.ROOT);
            MapStyle style = new MapStyle();
            style.setFill(false);
            style.setBorderColor(Color.WHITE);
            style.setShowName(true);
            style.setFont(provinceFont);
            style.setFontColor(Color.BLACK);
            drawGeometry(name, g, tx, ty, geometry, zoom, style);
            feature = layer.GetNextFeature();
        }
    }

    private void drawDistrict(Graphics2D g, double tx, double ty, int zoom, Geometry filter) {
        Layer layer = districtDataSource.GetLayer(0);
        layer.SetSpatialFilter(filter);
        layer.ResetReading();
        Feature feature = layer.GetNextFeature();
        while (feature != null) {
            Geometry geometry = feature.GetGeomFieldRef(0);
            String name = feature.GetFieldAsString("ADM2_ZH").toLowerCase(Locale.ROOT);
            MapStyle style = new MapStyle();
            style.setFill(false);
            style.setBorderColor(Color.WHITE);

            drawGeometry(name, g, tx, ty, geometry, zoom, style);
            feature = layer.GetNextFeature();
        }
    }
}
