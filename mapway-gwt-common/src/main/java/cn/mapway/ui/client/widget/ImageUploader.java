package cn.mapway.ui.client.widget;


import cn.mapway.ui.client.event.MessageObject;
import cn.mapway.ui.client.fonts.Fonts;
import cn.mapway.ui.client.resource.MapwayResource;
import cn.mapway.ui.client.tools.MapwayLog;
import cn.mapway.ui.client.util.Logs;
import cn.mapway.ui.shared.CommonEvent;
import cn.mapway.ui.shared.UploadReturn;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.InputElement;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.http.client.URL;
import com.google.gwt.safehtml.shared.SafeUri;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.*;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteHandler;
import com.google.gwt.user.client.ui.Image;
import elemental2.core.Global;
import elemental2.core.JsObject;
import elemental2.dom.*;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;

import java.util.ArrayList;

/**
 * 图像上传组件,上传后自动展示该图片, 上传开始 报告 SUBMIT事件 上传完成 报告OK事件.
 *
 * @author zhangjianshe
 */
public class ImageUploader extends CommonEventComposite {

    /**
     * The Constant EMPTY_PICTURE.
     */
    public static final String EMPTY_PICTURE = GWT.getModuleBaseURL() + "../img/empty.png";

    /**
     * The Constant DEFAULT_ACTION.
     */
    public static final String DEFAULT_ACTION = GWT.getModuleBaseURL() + "../fileUpload";

    /**
     * The ui binder.
     */
    private static final ImageUploaderUiBinder uiBinder = GWT.create(ImageUploaderUiBinder.class);
    private static final String[] picTypes = {".png", ".jpg", ".jpeg", ".gif", ".bmp", ".tiff", ".tif",".webp",".svg"};
    /**
     * The extra.
     */
    String extra = "";
    /**
     * The relpath.
     */
    String relpath = "";
    String basePath = "";
    /**
     * The acceptable files.
     */
    ArrayList<String> acceptableFiles = new ArrayList<String>();
    /**
     * The img.
     */
    @UiField
    Image img;
    /**
     * The lb title.
     */
    @UiField
    Label lbTitle;
    /**
     * The uploader.
     */
    @UiField
    FileUpload uploader;
    /**
     * The btn uploader.
     */
    @UiField
    FontIcon btnUploader;
    @UiField
    FontIcon btnClear;
    @UiField
    FormPanel form;
    int currentImageWidth;
    int currentImageHeight;
    /**
     * The action.
     */
    private String action = "";
    /**
     * The file change.
     */
    private final ChangeHandler fileChange = new ChangeHandler() {
        @Override
        public void onChange(ChangeEvent event) {
            if (uploader.getFilename() == null || uploader.getFilename().length() == 0) {
                fireMessage(MessageObject.warn(MessageObject.CODE_FAIL, "没有选择文件"));
                return;
            }
            String msg = isFileAcceptable(uploader.getFilename());
            if (msg.length() > 0) {
                fireMessage(MessageObject.warn(MessageObject.CODE_FAIL, "不支持文件类型" + uploader.getFilename()));
                return;
            }
            if (action.length() > 0) {
                MapwayLog.info("上传文件到" + action);
                String sb = "extra=" + URL.encodeQueryString(extra) +
                        "&relPath=" + URL.encodeQueryString(relpath);
                String actionUrl = action + "?" + sb;

                XMLHttpRequest request = new XMLHttpRequest();
                FormData data = new FormData();
                HTMLInputElement inputElement = Js.uncheckedCast(uploader.getElement());
                data.set("file", inputElement.files.getAt(0));
                request.open("POST", actionUrl);
                request.onloadend = p0 -> {
                    UploadReturn r = (UploadReturn) Global.JSON.parse(request.responseText);
                    if (r.retCode == 0) {
                        CommonEvent ev = CommonEvent.okEvent(r);
                        if (isPicture(r.relPath)) {
                            img.setUrl(basePath + r.relPath);
                        }
                        fireEvent(ev);
                    } else {
                        fireMessage(MessageObject.warn(MessageObject.CODE_FAIL, r.msg));
                    }
                    form.reset();
                };
                request.send(data);
            } else {
                fireMessage(MessageObject.warn(MessageObject.CODE_FAIL, "没有设置上传网址"));
            }
        }
    };

    /**
     * Instantiates a new image uploader.
     */
    public ImageUploader() {
        initWidget(uiBinder.createAndBindUi(this));
        this.setAction(DEFAULT_ACTION, "default");
        uploader.addChangeHandler(fileChange);

        img.addErrorHandler(event -> img.setUrl(MapwayResource.INSTANCE.defaultImage().getSafeUri()));
        img.addLoadHandler(event -> resizeImage());

        for (int i = 0; i < picTypes.length; i++) {
            addAcceptFileExtension(picTypes[i]);
        }
        img.setUrl(MapwayResource.INSTANCE.defaultImage().getSafeUri());
        btnUploader.setLineHeight(28);
        btnClear.setIconUnicode(Fonts.CLOSE_L);
    }

    /**
     * 是否是图片
     *
     * @param url the url
     * @return boolean
     */
    public static boolean isPicture(String url) {
        if (url == null || url.length() == 0) {
            return false;
        } else {
            String urllow = url.toLowerCase();
            for (int i = 0; i < picTypes.length; i++) {
                if (urllow.endsWith(picTypes[i])) {
                    return true;
                }
            }
        }
        return false;
    }

    public void setUploaderIconUnicode(String unicode) {
        btnUploader.setIconUnicode(unicode);
    }

    private void resizeImage() {
        HTMLImageElement imgNative = Js.uncheckedCast(img.getElement());
        //图像宽度和高度
        int width = imgNative.naturalWidth;
        int height = imgNative.naturalHeight;

        //容器的宽度和高度
        int boxWidth = ImageUploader.this.getOffsetWidth();
        int boxHeight = ImageUploader.this.getOffsetHeight();
        if (boxHeight == 0 || boxWidth == 0) {
            return;
        }
        //计算缩放比例
        Style style = img.getElement().getStyle();
        //box 是一个长条状  [ || ]

        if (width < boxWidth) {
            if (height < boxHeight) {
                //不变 图片小于显示框
                img.setPixelSize(width, height);
                style.setTop((boxHeight - height) >> 1, Style.Unit.PX);
                style.setLeft((boxWidth - width) >> 1, Style.Unit.PX);
            } else {
                //高度超过限制,就水平居中
                img.setHeight(boxHeight + "px");
                style.setTop(0, Style.Unit.PX);
                style.setLeft((boxWidth - width) >> 1, Style.Unit.PX);
            }
        } else {
            if (height < boxHeight) {
                //需要上下居中
                img.setWidth(boxWidth + "px");
                style.setTop((boxHeight - height) >> 1, Style.Unit.PX);
                style.setLeft(0, Style.Unit.PX);
            } else {
                //高度也大 宽度也大 都缩小
                double scale = (double) width / (double) height;
                double scale1 = (double) boxWidth / (double) boxHeight;
                if (scale > scale1) {
                    //宽度缩小
                    int newHeight = (int) ((1.0 * boxWidth / width) * height);
                    img.setPixelSize(boxWidth, newHeight);
                    style.setLeft(0, Style.Unit.PX);
                    style.setTop((boxHeight - newHeight) >> 1, Style.Unit.PX);
                } else {
                    //高度缩小
                    int newWidth = (int) (((1.0 * boxHeight) / height) * width);
                    img.setPixelSize(newWidth, boxHeight);
                    style.setTop(0, Style.Unit.PX);
                    style.setLeft((boxWidth - newWidth) >> 1, Style.Unit.PX);
                }
            }
        }
    }

    /**
     * 判断文件是否可以被接受.
     *
     * @param filename the filename
     * @return the string
     */
    protected String isFileAcceptable(String filename) {
        int index = 0;
        index = filename.lastIndexOf('.');
        if (index <= 0) {
            return "不能判断上传的文件格式";
        }
        String fileext = filename.substring(index + 1);
        for (String e : acceptableFiles) {
            if (e.compareToIgnoreCase(fileext) == 0) {
                return "";
            }
        }
        return "不能上传文件格式" + fileext;
    }

    /**
     * 清楚可接受的文件格式.
     */
    public void clearAcceptFileExtension() {
        acceptableFiles.clear();
    }

    /**
     * 添加可以接受的上传文件格式.
     *
     * @param ext 可以接受的上传文件后缀 不需要添加 . 如 pdf png apk etc..
     */
    public void addAcceptFileExtension(String ext) {
        if (ext != null && ext.length() > 0) {
            if (ext.startsWith(".")) {
                acceptableFiles.add(ext.substring(1));
            } else {
                acceptableFiles.add(ext);
            }
        }
    }

    /**
     * 添加可以接受的上传文件格式.
     *
     * @param exts 可以接受的上传文件后缀 不需要添加 . 如 pdf png apk etc..
     */
    public void addAcceptFileExtensions(String... exts) {
        for (String ext : exts) {
            addAcceptFileExtension(ext);
        }
    }

    public String removeHTMLTag(String input) {
        if (input == null || input.trim().equals("")) {
            return "";
        }
        // 去掉所有html元素
        String str = removePrev(input);
        return str;
    }

    private native String removePrev(String html)/*-{
        return html.replace(/<.*?>/ig, "");
    }-*/;

    /**
     * 设置图片的相对路径前缀
     *
     * @param path
     */
    public void setBasePath(String path) {
        basePath = path;
    }

    /**
     * 设置上传的参数.
     *
     * @param action   远程接受文件的URL
     * @param relative 服务器保存的相对路径
     */
    public void setAction(String action, String relative) {
        this.action = action;
        if (relative == null || relative.length() == 0) {
            relative = "upload";
        }
        relpath = relative;
    }

    /**
     * Gets url.
     *
     * @return the url
     */
    public String getUrl() {
        return img.getUrl();
    }

    /**
     * Sets the url.
     *
     * @param url the new url
     */
    public void setUrl(String url) {
        currentImageHeight = 0;
        currentImageWidth = 0;
      //  Logs.info("setUrl " + url);
        if (url == null || url.length() == 0) {
            img.setUrl(EMPTY_PICTURE);
        } else {
            img.setUrl(url);
        }
    }

    /**
     * Sets url.
     *
     * @param url the url
     */
    public void setUrl(SafeUri url) {
        img.setUrl(url);
        btnUploader.setVisible(false);
    }

    /**
     * Gets image title.
     *
     * @return the image title
     */
    public String getImageTitle() {
        return lbTitle.getText();
    }

    /**
     * Sets the image title.
     *
     * @param string the new image title
     */
    public void setImageTitle(String string) {
        lbTitle.setText(string);
    }

    /**
     * Sets the extra.
     *
     * @param value the new extra
     */
    public void setExtra(String value) {
        extra = (value);
    }

    @UiHandler("btnClear")
    public void btnClearClick(ClickEvent event) {
        setUrl("");
        UploadReturn uploadReturn = Js.uncheckedCast(JsPropertyMap.of());
        uploadReturn.size = 0;
        uploadReturn.relPath = "";
        uploadReturn.msg = "清空图片";
        uploadReturn.fileName = "";
        uploadReturn.retCode = 400;
        uploadReturn.extra = extra;
        fireEvent(CommonEvent.okEvent(uploadReturn));
    }

    /**
     * The Interface ImageUploaderUiBinder.
     */
    interface ImageUploaderUiBinder extends UiBinder<LayoutPanel, ImageUploader> {
    }
}
