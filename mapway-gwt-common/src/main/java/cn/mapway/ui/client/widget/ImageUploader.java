package cn.mapway.ui.client.widget;

import cn.mapway.ui.client.event.MessageObject;
import cn.mapway.ui.client.fonts.Fonts;
import cn.mapway.ui.client.resource.MapwayResource;
import cn.mapway.ui.shared.CommonEvent;
import cn.mapway.ui.shared.UploadReturn;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.http.client.URL;
import com.google.gwt.safehtml.shared.SafeUri;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.LayoutPanel;
import elemental2.core.Global;
import elemental2.dom.*;
import jsinterop.base.Js;
import jsinterop.base.JsArrayLike;
import jsinterop.base.JsPropertyMap;
import lombok.Setter;

import java.util.ArrayList;

/**
 * Image Uploader Component.
 * Supports image uploading, proportional responsive fitting, and clear features.
 *
 * @author zhangjianshe
 */
public class ImageUploader extends CommonEventComposite {

    public static final String EMPTY_PICTURE = GWT.getModuleBaseURL() + "../img/selectImage.png";
    public static final String DEFAULT_ACTION = GWT.getModuleBaseURL() + "../fileUpload";
    private static final ImageUploaderUiBinder uiBinder = GWT.create(ImageUploaderUiBinder.class);
    private static final String[] picTypes = {".png", ".jpg", ".jpeg", ".gif", ".bmp", ".tiff", ".tif", ".webp", ".svg"};

    HTMLInputElement fileInput;

    @Setter
    String extra = "";
    String relpath = "";

    @Setter
    String basePath = "";

    ArrayList<String> acceptableFiles = new ArrayList<String>();

    @UiField
    Image img;

    @UiField
    FontIcon btnClear;

    int currentImageWidth;
    int currentImageHeight;
    private String action = "";

    /**
     * Instantiates a new image uploader.
     */
    public ImageUploader() {
        initWidget(uiBinder.createAndBindUi(this));
        this.setAction(DEFAULT_ACTION, "default");

        // Error handling fallback image
        img.addErrorHandler(event -> img.setUrl(MapwayResource.INSTANCE.defaultImage().getSafeUri()));

        // Triggers aspect calculation whenever a new source loads
        img.addLoadHandler(event -> resizeImage());

        for (int i = 0; i < picTypes.length; i++) {
            addAcceptFileExtension(picTypes[i]);
        }

        img.setUrl(MapwayResource.INSTANCE.defaultImage().getSafeUri());
        btnClear.setIconUnicode(Fonts.CLEAR);

        // Click trigger for hidden file system input
        img.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                if (fileInput != null) {
                    fileInput.click();
                }
            }
        });

        initUploader();
    }

    private void initUploader() {
        fileInput = (HTMLInputElement) DomGlobal.document.createElement("input");
        fileInput.type = "file";
        fileInput.multiple = false;
        fileInput.accept = "image/*";

        fileInput.onchange = (e) -> {
            FileList files = fileInput.files;
            handleFiles(files);
            fileInput.value = ""; // Clear file selector string buffer
            return null;
        };
    }

    private void handleFiles(JsArrayLike<File> files) {
        if (files == null || files.getLength() == 0) {
            fireMessage(MessageObject.warn(MessageObject.CODE_FAIL, "No file selected"));
            return;
        }
        File file = files.getAt(0);

        String msg = isFileAcceptable(file.name);
        if (!msg.isEmpty()) {
            fireMessage(MessageObject.warn(MessageObject.CODE_FAIL, "Unsupported type: " + file.name));
            return;
        }

        if (!action.isEmpty()) {
            DomGlobal.console.log("Uploading to: " + action);
            String sb = "extra=" + URL.encodeQueryString(extra) +
                    "&relPath=" + URL.encodeQueryString(relpath);
            String actionUrl = action + "?" + sb;

            XMLHttpRequest request = new XMLHttpRequest();
            FormData data = new FormData();
            data.append("file", file); // Fixed: Changed .set() to cross-browser standard .append()

            request.open("POST", actionUrl);
            request.onloadend = p0 -> {
                if (request.status == 200) { // Check HTTP Success safely
                    UploadReturn r = (UploadReturn) Global.JSON.parse(request.responseText);
                    if (r.retCode == 0) {
                        CommonEvent ev = CommonEvent.okEvent(r);
                        if (isPicture(r.relPath)) {
                            setUrl(basePath + r.relPath);
                        }
                        fireEvent(ev);
                    } else {
                        fireMessage(MessageObject.warn(MessageObject.CODE_FAIL, r.msg));
                    }
                } else {
                    fireMessage(MessageObject.warn(MessageObject.CODE_FAIL, "Network Upload Error status: " + request.status));
                }
                fileInput.value = "";
            };
            request.send(data);
        } else {
            fireMessage(MessageObject.warn(MessageObject.CODE_FAIL, "Action target missing"));
        }
    }

    @Override
    protected void onLoad() {
        super.onLoad();
        // Force rendering layout context overrides immediately after entering DOM
        Style imgStyle = img.getElement().getStyle();
        imgStyle.setPosition(Style.Position.ABSOLUTE);
        resizeImage();
    }

    public static boolean isPicture(String url) {
        if (url == null || url.isEmpty()) {
            return false;
        }
        String urllow = url.toLowerCase();
        for (String picType : picTypes) {
            if (urllow.endsWith(picType)) {
                return true;
            }
        }
        return false;
    }

    private void resizeImage() {
        if (!isAttached()) {
            return; // Guard statement protecting dimensions evaluation when detached
        }

        HTMLImageElement imgNative = Js.uncheckedCast(img.getElement());

        int width = imgNative.naturalWidth;
        int height = imgNative.naturalHeight;

        // Fallback checks if metadata hasn't loaded fully
        if (width == 0 || height == 0) {
            return;
        }

        int boxWidth = ImageUploader.this.getOffsetWidth();
        int boxHeight = ImageUploader.this.getOffsetHeight();
        if (boxHeight == 0 || boxWidth == 0) {
            return;
        }

        Style style = img.getElement().getStyle();

        if (width < boxWidth) {
            if (height < boxHeight) {
                // Case 1: Completely fits within margins
                img.setPixelSize(width, height);
                style.setTop((boxHeight - height) >> 1, Style.Unit.PX);
                style.setLeft((boxWidth - width) >> 1, Style.Unit.PX);
            } else {
                // Case 2: Narrower but exceeding target bounds vertically
                int newWidth = (int) (((double) boxHeight / height) * width);
                img.setPixelSize(newWidth, boxHeight);
                style.setTop(0, Style.Unit.PX);
                style.setLeft((boxWidth - newWidth) >> 1, Style.Unit.PX);
            }
        } else {
            if (height < boxHeight) {
                // Case 3: Wider but inside height boundaries
                int newHeight = (int) (((double) boxWidth / width) * height);
                img.setPixelSize(boxWidth, newHeight);
                style.setTop((boxHeight - newHeight) >> 1, Style.Unit.PX);
                style.setLeft(0, Style.Unit.PX);
            } else {
                // Case 4: Scale relative cross aspect-ratios
                double scale = (double) width / height;
                double scale1 = (double) boxWidth / boxHeight;

                if (scale > scale1) {
                    int newHeight = (int) (((double) boxWidth / width) * height);
                    img.setPixelSize(boxWidth, newHeight);
                    style.setLeft(0, Style.Unit.PX);
                    style.setTop((boxHeight - newHeight) >> 1, Style.Unit.PX);
                } else {
                    int newWidth = (int) (((double) boxHeight / height) * width);
                    img.setPixelSize(newWidth, boxHeight);
                    style.setTop(0, Style.Unit.PX);
                    style.setLeft((boxWidth - newWidth) >> 1, Style.Unit.PX);
                }
            }
        }
    }

    protected String isFileAcceptable(String filename) {
        int index = filename.lastIndexOf('.');
        if (index <= 0) {
            return "Missing file extension context";
        }
        String fileext = filename.substring(index + 1);
        for (String e : acceptableFiles) {
            if (e.compareToIgnoreCase(fileext) == 0) {
                return "";
            }
        }
        return "Unsupported format: " + fileext;
    }

    public void clearAcceptFileExtension() {
        acceptableFiles.clear();
    }

    public void addAcceptFileExtension(String ext) {
        if (ext != null && !ext.isEmpty()) {
            if (ext.startsWith(".")) {
                acceptableFiles.add(ext.substring(1));
            } else {
                acceptableFiles.add(ext);
            }
        }
    }

    public void addAcceptFileExtensions(String... fileExtensions) {
        if (fileExtensions == null) return;
        for (String ext : fileExtensions) {
            addAcceptFileExtension(ext);
        }
    }

    public String removeHTMLTag(String input) {
        if (input == null || input.trim().isEmpty()) {
            return "";
        }
        return removePrev(input);
    }

    private native String removePrev(String html)/*-{
        return html.replace(/<.*?>/ig, "");
    }-*/;

    public void setAction(String action, String relative) {
        this.action = action;
        if (relative == null || relative.isEmpty()) {
            relative = "upload";
        }
        relpath = relative;
    }

    public String getUrl() {
        return img.getUrl();
    }

    public void setUrl(String url) {
        currentImageHeight = 0;
        currentImageWidth = 0;
        if (url == null || url.isEmpty()) {
            img.setUrl(EMPTY_PICTURE);
        } else {
            img.setUrl(url);
        }
    }

    public void setUrl(SafeUri url) {
        img.setUrl(url);
    }

    public String getImageTitle() { return ""; }
    public void setImageTitle(String string) {}

    @UiHandler("btnClear")
    public void btnClearClick(ClickEvent event) {
        event.stopPropagation();
        event.preventDefault();
        setUrl("");
        UploadReturn uploadReturn = Js.uncheckedCast(JsPropertyMap.of());
        uploadReturn.size = 0;
        uploadReturn.relPath = "";
        uploadReturn.msg = "Cleared";
        uploadReturn.fileName = "";
        uploadReturn.retCode = 400;
        uploadReturn.extra = extra;
        fireEvent(CommonEvent.okEvent(uploadReturn));
    }

    interface ImageUploaderUiBinder extends UiBinder<LayoutPanel, ImageUploader> {}
}