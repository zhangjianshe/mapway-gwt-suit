package cn.mapway.common.geo.tools;


import cn.mapway.biz.core.BizResult;
import cn.mapway.common.geo.sfile.SFileExtend;
import cn.mapway.common.geo.sfile.SLevelInfo;
import cn.mapway.geo.client.raster.ImageInfo;
import cn.mapway.geo.geometry.GeoObject;
import cn.mapway.geo.geometry.GeoPolygon;
import cn.mapway.geo.geometry.Line;
import cn.mapway.geo.shared.vector.Box;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.gdal.ogr.Geometry;
import org.nutz.json.Json;
import org.nutz.lang.Files;
import org.nutz.lang.Lang;
import org.nutz.lang.Streams;
import org.nutz.lang.Strings;
import org.sqlite.SQLiteConfig;
import org.sqlite.SQLiteDataSource;
import org.sqlite.SQLiteOpenMode;

import javax.imageio.ImageIO;
import javax.sql.DataSource;
import java.awt.*;
import java.awt.geom.Area;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.*;
import java.util.List;
import java.util.*;

import static java.awt.image.BufferedImage.TYPE_INT_ARGB;
import static org.gdal.ogr.ogrConstants.wkbMultiPolygon;
import static org.gdal.ogr.ogrConstants.wkbPolygon;

/**
 * TileTools
 * 瓦片工具，主要用于将GeoTiff切图存储在.S文件中
 * 将一个文件切片的主要过程分为四部
 * 打开输入
 * 生产元数据
 * 生成基本title
 * 生成索引title
 *  TODO 解决连接池数量和关闭的问题
 *
 * @author zhangjianshe <zhangjianshe@gmail.com>
 */
@Slf4j
@Data
public class TileTools {


    public final static int TILE_SIZE = 256;
    public final static String MEMORY_DB_PATH = ":MEMORY:";
    public static BufferedImage emptyImage;
    private static Map<String, byte[]> fileFormats;

    static {
        emptyImage = new BufferedImage(TILE_SIZE, TILE_SIZE, TYPE_INT_ARGB);
        Graphics2D g = emptyImage.createGraphics();
        emptyImage = g.getDeviceConfiguration().createCompatibleImage(TILE_SIZE, TILE_SIZE, Transparency.TRANSLUCENT);
        Color emptyColor = new Color(255, 255, 255, 0);
        g.setColor(emptyColor);
        g.fillRect(0, 0, TILE_SIZE, TILE_SIZE);
        g.dispose();

        fileFormats = new HashMap<>();
        //为了标识同样的tiff magic number 加了空格 客户取的的时候 trim
        fileFormats.put("image/tiff", new byte[]{0x49, 0x49, 0x2a, 0x00});
        fileFormats.put("image/tiff ", new byte[]{0x4D, 0x4D, 0x00, 0x2a});
        fileFormats.put("image/png", new byte[]{(byte) 0x89, 0x50, 0x4E, 0x47});
    }

    String basePath;
    Map<String, DataSource> connectionMap;
    /**
     * 是否是内存DB
     */
    boolean memoryDb;


    public TileTools(String basePath) {
        if (basePath.equals(MEMORY_DB_PATH)) {
            memoryDb = true;
        } else {
            this.basePath = basePath;
            Files.makeDir(new File(basePath));
            connectionMap = new HashMap<>();
        }
    }


    public static GeoObject toPolygon(Box box) {

        if (box == null) {
            return null;
        }
        GeoPolygon polygon = new GeoPolygon();
        Line line = new Line();
        line.add(box.xmin, box.ymin);
        line.add(box.xmin, box.ymax);
        line.add(box.xmax, box.ymax);
        line.add(box.xmax, box.ymin);
        line.add(box.xmin, box.ymin);
        polygon.getLines().add(line);
        return GeoUtilScript.parse(polygon.toGeoJson());
    }

    /**
     * raster Geometry intersects box in tile
     * tile （x,y,z)
     *
     * @param tileX
     * @param tileY
     * @param z
     * @param intersection webmercator cords' polygon
     * @return
     */
    public static BufferedImage rasterMask(long tileX, long tileY, int z, Geometry intersection) {
        Box tileBoundsWebMercator = GlobalMercator.get().tileBoundsWebMercator(tileX, tileY, z);
        //坐下角坐标
        double xmin = tileBoundsWebMercator.xmin;
        double ymin = tileBoundsWebMercator.ymin;
        double resolution = GlobalMercator.get().resolution(z);

        BufferedImage image = new BufferedImage(256, 256, TYPE_INT_ARGB);
        Graphics2D g = image.createGraphics();
        g.setColor(Color.WHITE);
        if (intersection.GetGeometryType() == wkbMultiPolygon) {
            for (int i = 0; i < intersection.GetGeometryCount(); i++) {
                Area area = toPolygonShape(xmin, ymin, resolution, intersection.GetGeometryRef(i));
                if (area != null) {
                    g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
                    g.fill(area);
                }
            }
        } else if (intersection.GetGeometryType() == wkbPolygon) {
            Area area = toPolygonShape(xmin, ymin, resolution, intersection);
            if (area != null) {
                g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
                g.fill(area);
            }
        }
        g.dispose();
        return image;
    }

    private static Area toPolygonShape(double xmin, double ymin, double resolution, Geometry geometry) {
        log.info("intection type {}", geometry.GetGeometryType());
        if (geometry.GetGeometryType() != wkbPolygon) {
            return null;
        }

        int lineCount = geometry.GetGeometryCount();
        Area area = null;
        for (int i = 0; i < lineCount; i++) {
            Polygon drawPolygon = new Polygon();
            Geometry line = geometry.GetGeometryRef(i);
            for (int j = 0; j < line.GetPointCount(); j++) {
                double[] pt = line.GetPoint(j);
                double dx =  ((pt[0] - xmin) / resolution);
                double  dy =  (256 - (pt[1] - ymin) / resolution);
                int x = dx<0.9?0:dx>255.2?256:(int)dx;
                int y = dy<0.9?0:dy>255.2?256:(int)dy;
                drawPolygon.addPoint(x, y);
            }

            if (i == 0) {
                area = new Area(drawPolygon);
            } else {
                area.subtract(new Area(drawPolygon));
            }
        }
        return area;
    }

    /**
     * 当前目录下写入元数据信息
     *
     * @param info
     */
    public void writeMetaFile(ImageInfo info) {
        Json.toJson(Streams.fileOutw(new File(basePath + File.separator + "mapInfo.json")), info);
    }

    /**
     * 拷贝瓦片到目标数据库中
     *
     * @param target
     * @param xIndex
     * @param yIndex
     * @param zoom
     */
    public void copyTo(TileTools target, long xIndex, long yIndex, int zoom) {
        byte[] data = read(xIndex, yIndex, zoom);
        target.writeBytes(xIndex, yIndex, zoom, data);
    }

    /**
     * 写入指定位置图像
     * 只有一个线程支持写操作
     *
     * @param xIndex tileX 坐标
     * @param yIndex tile Y坐标
     * @param zoom   图像Level
     * @param data   图像数据
     */
    public synchronized void write(long xIndex, long yIndex, int zoom, InputStream data) {
        Connection connection = sureWriteConnection(xIndex, yIndex, zoom);
        if (connection != null) {
            String insertSql = String.format("INSERT OR REPLACE INTO %s (ID,Data,X,Y,F) values(?,?,?,?,?)", getTableName(xIndex, yIndex, zoom));
            try (PreparedStatement statement = connection.prepareStatement(insertSql)) {
                int length = data.available();
                statement.setInt(1, (int) (xIndex % 64 + 64 * (yIndex % 64)));
                byte[] bytes = Streams.readBytes(data);
                statement.setBytes(2, bytes);
                statement.setInt(3, (int) xIndex);
                statement.setInt(4, (int) yIndex);
                statement.setInt(5, length);
                statement.execute();
                connection.close();
            } catch (SQLException | IOException exception) {
                exception.printStackTrace();
            }
        } else {
            log.error("数据库没有准备好");
        }
    }

    /**
     * 写入数据
     *
     * @param xIndex
     * @param yIndex
     * @param zoom
     * @param data
     */
    public void writeBytes(long xIndex, long yIndex, int zoom, byte[] data) {
        InputStream inputData = Streams.wrap(data);
        write(xIndex, yIndex, zoom, inputData);
    }

    /**
     * S数据库中瓦片坐标 是左上角为原点 向上 向下生长
     * leaflet 采用这种坐标系统
     * 注意  读取墨卡托 瓦片数据 瓦片坐标原点定位左上角 向下 向右生长
     *
     * @param xIndex 瓦片X方向的索引
     * @param yIndex 瓦片Y方向的索引
     * @param zoom   瓦片级别
     * @return
     */
    public byte[] read(long xIndex, long yIndex, int zoom) {
        Connection connection = sureConnection(xIndex, yIndex, zoom, false);
        String tableName = getTableName(xIndex, yIndex, zoom);
        if (connection == null) {
            log.error("没有找到数据库{} {}/{}", basePath, getDbFile(xIndex, yIndex, zoom), tableName);
            return null;
        }
        int index = (int) (xIndex % 64 + 64 * (yIndex % 64));
        String sqlStr = String.format("select Data from %s where ID=%d", tableName, index);

        try (Statement statement = connection.createStatement()) {

            boolean result = statement.execute(sqlStr);
            if (result) {
                ResultSet resultSet = statement.getResultSet();
                if (resultSet.next()) {
                    InputStream binaryStream = resultSet.getBinaryStream(1);
                    byte[] bytes = Streams.readBytes(binaryStream);
                    return bytes;
                } else {
                    // log.error("没有找到记录{}", sqlStr);
                }
            } else {
                log.error("执行sql语句{} 失败", sqlStr);
            }
        } catch (SQLException | IOException exception) {
            exception.printStackTrace();
            log.error("{} {}", sqlStr, exception.getMessage());
        } finally {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public void read(Integer x, Integer y, Integer level, OutputStream outputStream) {
        byte[] bytes = read(x, y, level);
        if (bytes != null) {
            try {
                outputStream.write(bytes);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 读取指定位置的图像
     *
     * @param xIndex
     * @param yIndex
     * @param zoom
     * @return
     */
    public BufferedImage readImage(long xIndex, long yIndex, int zoom) {
        byte[] data = read(xIndex, yIndex, zoom);
        if (data != null && data.length > 0) {
            try {
                return ImageIO.read(Streams.wrap(data));
            } catch (IOException e) {
                e.printStackTrace();
                log.error("read image error " + e.getMessage());
            }
        }
        return emptyImage;
    }

    public String getDbFile(long xIndex, long yIndex, int zoom) {

        String dbFile = (char) ('A' + Math.max(zoom, 9)) + "_" + xIndex / 256 + "_" + yIndex / 256 + ".s";
        return dbFile;

    }

    /**
     * 确保创建了一个连接池,该方法还确保相应的文件都已经生成，并且确保数据库表也已经创建
     *
     * @param xIndex
     * @param yIndex
     * @param zoom
     * @param create 如果不存在是否创建
     * @return
     */
    private Connection sureConnection(long xIndex, long yIndex, int zoom, boolean create) {
        int vz = Math.max(zoom, 9);

        String dbDir = "" + (char) ('A' + vz);
        String dbName = getDbFile(xIndex, yIndex, zoom);

        String dbPathFullName;
        String dbFileFullName;
        if (isMemoryDb()) {
            dbPathFullName = MEMORY_DB_PATH;
            dbFileFullName = dbPathFullName;
        } else {
            dbPathFullName = basePath + File.separator + dbDir;
            File file = new File(dbPathFullName);
            Files.createDirIfNoExists(file);
            dbFileFullName = dbPathFullName + File.separator + dbName;
            if (!create) {
                if (!Files.isFile(new File(dbFileFullName))) {
                    log.error("数据库文件不存在{}", dbFileFullName);
                    return null;
                }
            }
        }
        DataSource ds = connectionMap.get(dbFileFullName);
        if (ds == null) {
            //还没有缓存 需要重建缓存
            String url = "jdbc:sqlite:" + dbFileFullName;

            SQLiteConfig config = new SQLiteConfig();
            config.setLockingMode(SQLiteConfig.LockingMode.NORMAL);
            config.setOpenMode(SQLiteOpenMode.READONLY);
            config.setReadOnly(true);
            SQLiteDataSource unpooled = new SQLiteDataSource(config);
            unpooled.setUrl(url);
            connectionMap.put(dbFileFullName, unpooled);
            ds = unpooled;
        }
        if (ds == null) {
            log.error("open connection error");
            return null;
        }

        Connection connection = null;
        try {
            connection = ds.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
        //确保数据库表已经创建
        String tableName = getTableName(xIndex, yIndex, zoom);
        boolean tableExist = sureTable(connection, tableName, create);
        if (tableExist) {
            return connection;
        }
        return null;
    }


    /**
     * 确保创建了一个连接池,该方法还确保相应的文件都已经生成，并且确保数据库表也已经创建
     *
     * @param xIndex
     * @param yIndex
     * @param zoom
     * @return
     */
    private Connection sureWriteConnection(long xIndex, long yIndex, int zoom) {
        int vz = Math.max(zoom, 9);

        String dbDir = "" + (char) ('A' + vz);
        String dbName = getDbFile(xIndex, yIndex, zoom);

        String dbPathFullName;
        String dbFileFullName;
        if (isMemoryDb()) {
            dbPathFullName = MEMORY_DB_PATH;
            dbFileFullName = dbPathFullName;
        } else {
            dbPathFullName = basePath + File.separator + dbDir;
            Files.createDirIfNoExists(new File(dbPathFullName));
            dbFileFullName = dbPathFullName + File.separator + dbName;
        }
        try {
            String url = "jdbc:sqlite:" + dbFileFullName;
            Connection conn = DriverManager.getConnection(url);
            //确保数据库表已经创建
            String tableName = getTableName(xIndex, yIndex, zoom);
            boolean tableExist = sureTable(conn, tableName, true);
            if (tableExist) {
                return conn;
            } else {
                if (conn != null) {
                    conn.close();
                }
            }
        } catch (SQLException e) {
            log.error("不能创建数据库 或者打开数据库错误{} {}", dbFileFullName, e.getMessage());
        }
        return null;
    }

    private String getTableName(long xIndex, long yIndex, int zoom) {
        String tableName = (char) ('A' + zoom) + "_" + xIndex / 64 + "_" + yIndex / 64;
        return tableName;
    }

    public boolean sureTable(Connection connection, String tableName, boolean create) {

        ResultSet rs = null;
        try {
            DatabaseMetaData md = connection.getMetaData();
            rs = md.getTables(null, null, tableName, null);
            if (!rs.next()) {
                if (create) {
                    String sql = String.format("CREATE TABLE IF NOT EXISTS %s (ID INTEGER NOT NULL PRIMARY KEY, Data Blob,X INTEGER,Y INTEGER, F INTEGER)", tableName);
                    Statement stmt = connection.createStatement();
                    stmt.execute(sql);
                    String indexSql = String.format("CREATE UNIQUE INDEX IDX_%s ON %s (ID ASC)", tableName, tableName);
                    stmt.execute(indexSql);
                } else {
                    return false;
                }
            }
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public String guessFileFormat(byte[] data) {
        if (data == null || data.length <= 2) {
            return "image/png";
        }
        log.info(String.format("%X%X%X%X", data[0], data[1], data[2], data[3]));
        for (String key : fileFormats.keySet()) {
            byte[] magicNumber = fileFormats.get(key);
            boolean match = true;
            if (data.length > magicNumber.length) {
                for (int i = 0; i < magicNumber.length; i++) {
                    byte b1 = magicNumber[i];
                    byte b2 = data[i];
                    if (b1 != b2) {
                        match = false;
                        break;
                    }
                }
                if (match) {
                    return key.trim();
                }

            }
        }
        return "text/html";
    }

    /**
     * 清空.S数据文件
     */
    public void clear() {
        File dir = new File(basePath);
        Files.clearDir(dir);
    }

    /**
     * 搜索数据库找出该SFile的经纬度范围
     * 此值是一个估计值，不是精确值
     * S文件中 tile编号是 左上角为原点 向右 向下生长
     * 找到一个有值就返回
     *
     * @return
     */
    public BizResult<SFileExtend> searchExtend() {

        String infoFile = this.getBasePath() + "/imagebot.txt";
        if (Files.isFile(new File(infoFile))) {
            SFileExtend extend = Json.fromJson(SFileExtend.class, Files.read(infoFile));
            if (extend != null) {
                return BizResult.success(extend);
            }
        }

        File[] dirs = Files.lsDir(new File(getBasePath()), "^[A-Z]$");
        if (dirs.length == 0) {
            return BizResult.error(500, "空的S数据库");
        }
        List<SLevelInfo> levels = new ArrayList<>(26);
        for (int i = 'A'; i <= 'Z'; i++) {
            levels.add(new SLevelInfo(i - 'A'));
        }

        //从 目录中 读取文件排序
        for (File dir : dirs) {
            //读取所有表 该目录下包含 A-J级别的表,我们按照 字母排序后依次处理各个.S数据库
            File[] j_s_files = Files.lsFile(dir, "\\.[sS]$");
            //
            List<File> files = Lang.list(j_s_files);
            Collections.sort(files, new Comparator<File>() {
                @Override
                public int compare(File o1, File o2) {
                    return o2.getName().compareToIgnoreCase(o1.getName());
                }
            });
            //读取1个文件
            int count = 0;
            for (File f : files) {
                //依次处理.S文件
                String majorName = Files.getMajorName(f);
                int level = majorName.charAt(0) - 'A';
                SLevelInfo levelInfo = levels.get(level);
                calculateSFile(f.getAbsolutePath(), levelInfo);
                count++;
                if (count > 0) {
                    break;
                }
            }
        }
        // 0--> count  -> extent
        // 1--> count  -> extent
        // 2--> count  -> extent
        // 3--> count  -> extent

        int minZoom = levels.size();
        int maxZoom = 0;
        Box extend = new Box(180, 90, -180, -90);

        Collections.sort(levels, new Comparator<SLevelInfo>() {
            @Override
            public int compare(SLevelInfo o1, SLevelInfo o2) {
                return o2.getTileCount() > o1.getTileCount() ? 1 : -1;
            }
        });


        for (int i = 0; i < levels.size(); i++) {
            SLevelInfo levelInfo = levels.get(i);
            log.info("Level {} {} {}", levelInfo.getLevel(), levelInfo.getTileCount(), levelInfo.getBox().center().toString());
            if (levelInfo.getTileCount() > 0) {
                if (minZoom > levelInfo.getLevel()) {
                    minZoom = levelInfo.getLevel();
                }
                if (maxZoom < levelInfo.getLevel()) {
                    maxZoom = levelInfo.getLevel();
                }
                extend.merge(levelInfo.getBox());
            }
        }

        if (minZoom > maxZoom) {
            String msg = String.format("不能计算SFile数据库 {0} {1}", minZoom, maxZoom);
            log.info(msg);
            return BizResult.error(500, msg);
        } else {
            SFileExtend data = new SFileExtend();
            data.setBox(extend);
            data.setMaxZoom(17);
            data.setMinZoom(2);
            Files.write(infoFile, Json.toJson(data));
            return BizResult.success(data);
        }
    }

    private void calculateSFile(String absolutePath, SLevelInfo levelInfo) {
        String url = "jdbc:sqlite:" + absolutePath;
        SQLiteConfig config = new SQLiteConfig();
        config.setLockingMode(SQLiteConfig.LockingMode.NORMAL);
        config.setOpenMode(SQLiteOpenMode.READONLY);
        config.setReadOnly(true);
        SQLiteDataSource dataSource = new SQLiteDataSource(config);
        dataSource.setUrl(url);
        try (Connection connection = dataSource.getConnection()) {
            List<String> tableNames = listSFileTableNames(connection);
            int count = 0;
            for (String tableName : tableNames) {
                mergeTableExtend(connection, tableName, levelInfo);
                count++;
                if (count > 0) {
                    break;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 计算 table的范围和 tile数量
     *
     * @param connection
     * @param tableName
     * @param levelInfo
     */
    private void mergeTableExtend(Connection connection, String tableName, SLevelInfo levelInfo) {
        String extendSql = "select min(X),max(X),min(Y),max(Y) from " + tableName;
        try (Statement statement1 = connection.createStatement()) {
            boolean execute1 = statement1.execute(extendSql);
            if (execute1) {
                ResultSet resultSet1 = statement1.getResultSet();
                if (resultSet1.next()) {
                    Long minx = resultSet1.getLong(1);
                    Long maxx = resultSet1.getLong(2);
                    Long miny = resultSet1.getLong(3);
                    Long maxy = resultSet1.getLong(4);
                    //extend是tile编号的范围，我们需要将其转化为经纬度
                    // 编号坐标原点为 左上角 向下 向右生长
                    // GlobalMercator 计算方式是 右下角为坐标原点 所以 做个转换
                    int zoom = tableName.charAt(0) - 'A';
                    Box tileBounds = GlobalMercator.get().tileBounds(minx, miny, zoom);
                    levelInfo.getBox().merge(tileBounds);
                    tileBounds = GlobalMercator.get().tileBounds(maxx, maxy, zoom);
                    levelInfo.getBox().merge(tileBounds);
                }
            }
        } catch (SQLException e) {
            log.error(e.getMessage());
        }

        String countSQL = "select count(ID) from " + tableName;
        try (Statement statement1 = connection.createStatement()) {
            boolean execute1 = statement1.execute(countSQL);
            if (execute1) {
                ResultSet resultSet1 = statement1.getResultSet();
                if (resultSet1.next()) {
                    levelInfo.setTileCount(levelInfo.getTileCount() + resultSet1.getLong(1));
                }
            }
        } catch (SQLException e) {
            log.error(e.getMessage());
        }
    }

    private List<String> listSFileTableNames(Connection connection) {
        List<String> tables = new ArrayList<String>();
        String fetchTablesSql = "select name from sqlite_master where type='table'  order by name";

        try (Statement statement = connection.createStatement()) {
            boolean execute = statement.execute(fetchTablesSql);
            if (execute) {
                ResultSet resultSet = statement.getResultSet();
                while (resultSet.next()) {
                    String tableName = resultSet.getString(1);
                    // table like P_204_124
                    if (Strings.split(tableName, false, '_').length == 3) {
                        tables.add(tableName);
                    }
                }
            }
        } catch (SQLException e) {
            log.error(e.getMessage());
        }
        return tables;
    }


    /**
     * tile是否存在
     *
     * @param x
     * @param y
     * @param level
     * @return
     */
    public boolean exists(Integer x, Integer y, Integer level) {
        Connection connection = sureConnection(x, y, level, false);
        String tableName = getTableName(x, y, level);
        if (connection == null) {
            log.error("没有找到数据库{} {}/{}", basePath, getDbFile(x, y, level), tableName);
            return false;
        }
        int index = x % 64 + 64 * (y % 64);
        String sqlStr = String.format("select count(*) from %s where ID=%d", tableName, index);

        try (Statement statement = connection.createStatement()) {

            boolean result = statement.execute(sqlStr);
            if (result) {
                ResultSet resultSet = statement.getResultSet();
                return resultSet.next();
            } else {
                log.error("执行sql语句{} 失败", sqlStr);
                return false;
            }
        } catch (SQLException e) {
            log.error("{} {}", sqlStr, e.getMessage());
        } finally {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * 删除数据库的一条记录
     *
     * @param x
     * @param y
     * @param level
     */
    public void delete(Integer x, Integer y, Integer level) {
        Connection connection = sureConnection(x, y, level, false);
        String tableName = getTableName(x, y, level);
        if (connection == null) {
            log.error("没有找到数据库{} {}/{}", basePath, getDbFile(x, y, level), tableName);
        }
        int index = x % 64 + 64 * (y % 64);
        String sqlStr = String.format("delete from %s where ID=%d", tableName, index);

        try (Statement statement = connection.createStatement()) {
            boolean result = statement.execute(sqlStr);
        } catch (SQLException e) {
            log.error("{} {}", sqlStr, e.getMessage());
        } finally {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
