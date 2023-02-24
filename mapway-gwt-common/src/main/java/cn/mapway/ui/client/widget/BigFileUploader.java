package cn.mapway.ui.client.widget;

import cn.mapway.ui.client.event.MessageObject;
import cn.mapway.ui.client.util.Logs;
import cn.mapway.ui.client.util.StringUtil;
import cn.mapway.ui.shared.CommonEvent;
import cn.mapway.ui.shared.CommonEventHandler;
import cn.mapway.ui.shared.HasCommonHandlers;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.HTMLPanel;
import elemental2.core.Global;
import elemental2.core.JsNumber;
import elemental2.dom.File;
import elemental2.dom.FormData;
import elemental2.dom.HTMLInputElement;
import elemental2.dom.XMLHttpRequest;
import jsinterop.base.Js;

import java.util.*;

/**
 * BigFileUploader
 * 大文件上传
 *
 * @author zhangjianshe@gmail.com
 */
public class BigFileUploader extends HTMLPanel implements HasCommonHandlers {
    private static final String[] UNITS = {"KB", "MB", "GB", "TB"};
    private final List<String> accepted = new ArrayList<>();
    Element background;
    Element progress;
    Element text;
    FileUpload fileUpload;
    XMLHttpRequest request = null;
    String title = "";
    //本按钮点击事件
    private final ClickHandler clickHandler = new ClickHandler() {
        @Override
        public void onClick(ClickEvent event) {
            if (request != null) {
                boolean confirm = Window.confirm("正在上传文件,要取消上传吗?");
                if (confirm) {
                    cancelJob();
                }
            }
        }
    };
    Map<String, String> dataList = new HashMap<>();
    Map<String, String> headers = new HashMap<>();
    String action;
    String fileFieldName = null;
    private final ChangeHandler fileChangeHandler = new ChangeHandler() {
        @Override
        public void onChange(ChangeEvent event) {

            String suffix = suffix(fileUpload.getFilename());
            if (isAccepted(suffix)) {
                fileUpload.setWidth("1px");
                uploadJob();
            } else {
                String supportFileFormats = "";
                for (String f : accepted) {
                    supportFileFormats += f + ",";
                }
                Window.confirm("只能上传 " + supportFileFormats + "文件类型");
            }
        }
    };
    String styleName;

    public BigFileUploader() {
        super("");
        initWidget();
    }

    public BigFileUploader(String html) {
        super("");
        initWidget();
    }

    public void clearAccepted() {
        accepted.clear();
    }

    /**
     * 添加可以接受的文件名后缀 没有.
     *
     * @param suffix
     */
    public void addAccepted(String suffix) {
        if (StringUtil.isBlank(suffix)) {
            Logs.info("设置文件上传后缀 为空字符串");
            return;
        }
        suffix = suffix.toLowerCase(Locale.ROOT);
        if (suffix.startsWith(".")) {
            suffix = suffix.substring(1);
        }
        accepted.add(suffix);
    }

    public Boolean isAccepted(String suffix) {
        if (accepted.size() == 0 || suffix == null || suffix.length() == 0) {
            return true;
        } else {
            String temp = suffix.toLowerCase(Locale.ROOT);
            for (String t : accepted) {
                if (t.equals(suffix)) {
                    return true;
                }
            }
        }
        return false;
    }

    String suffix(String name) {
        if (StringUtil.isBlank(name)) {
            return "";
        }
        int index = name.lastIndexOf(".");
        if (index > 0) {
            String suffix = name.substring(index + 1);
            return suffix;
        }
        return name;
    }

    public void setText(String text) {
        title = text;
        this.text.setInnerText(text);
    }

    /**
     * 取消正在上传的任务
     */
    private void cancelJob() {

        request = null;
        fileUpload.setWidth(this.getOffsetWidth() + "px");
        text.setInnerText(calText());
        progress.getStyle().setWidth(0, Style.Unit.PX);
        com.google.gwt.user.client.Element element = fileUpload.getElement();
        element.setPropertyString("value", "");

    }

    private String calText() {
        if (title.length() == 0) {
            return "上传文件";
        }
        return title;
    }

    public void addHeader(String key, String value) {
        headers.put(key, value);
    }

    /**
     * 添加数据对
     *
     * @param fieldName
     * @param fieldValue
     */
    public void appendData(String fieldName, String fieldValue) {
        dataList.put(fieldName, fieldValue);
    }

    /**
     * 清空数据
     */
    public void clearData() {
        dataList.clear();
    }

    public void setAction(String action) {
        this.action = action;
    }

    public void setFileFieldName(String fieldName) {
        if (fieldName != null && fieldName.length() > 0) {
            fileFieldName = fieldName;
        }
        fileFieldName = null;
    }

    /**
     * 创建上传任务
     */
    private void uploadJob() {


        HTMLInputElement inputElement = Js.uncheckedCast(fileUpload.getElement());
        if (inputElement.files.length == 0) {
            fireEvent(CommonEvent.messageEvent(0, MessageObject.CODE_FAIL, "没有选择文件"));
            return;
        }

        if (action == null || action.equals("")) {
            String message = "没有配置上传文件路径";
            Logs.info(message);
            fireEvent(CommonEvent.messageEvent(0, MessageObject.CODE_FAIL, message));
            cancelJob();
            return;
        }

        XMLHttpRequest request = new XMLHttpRequest();
        request.upload.onprogress = (progressEvent) -> {
            int percent = (int) Math.floor(100 * progressEvent.loaded / progressEvent.total);
            int width = percent * background.getClientWidth() / 100;
            text.setInnerText(percent + "%");
            progress.getStyle().setWidth(width, Style.Unit.PX);
        };
        request.onabort = (abortEvent) -> {
            Logs.info("上传任务取消");
            cancelJob();
        };
        request.onerror = (e) -> {
            Logs.info("error " + e.type + " " + e.path + " " + e.currentTarget.toString());
            cancelJob();
            return true;
        };
        request.onload = (o) -> {
            Logs.info("onload calleds " + o.total);
        };
        request.onloadstart = (o) -> {
            Logs.info("load start " + o.total);
        };

        request.onloadend = (o) -> {
            Logs.info("load end " + o.total);
        };
        request.onreadystatechange = (readyStateEvent) -> {
            if (request.readyState == XMLHttpRequest.DONE ) {
                if( request.status == 200) {
                    //成功返回
                    BigFileUploadReturn data = (BigFileUploadReturn) Global.JSON.parse(request.responseText);
                    if (data.code != 200) {
                        CommonEvent ev = CommonEvent.messageEvent(0, MessageObject.CODE_FAIL, data.message);
                        fireEvent(ev);
                    } else {
                        fireEvent(CommonEvent.okEvent(data));
                    }
                    cancelJob();
                }
                else
                {
                    CommonEvent messageEvent = CommonEvent.messageEvent(0, MessageObject.CODE_FAIL, request.responseText);
                    fireEvent(messageEvent);
                }
            }
            return "";
        };
        FormData formData = new FormData();
        for (String key : dataList.keySet()) {
            formData.append(key, dataList.get(key));
        }

        File file = inputElement.files.getAt(0);
        if (fileFieldName == null) {
            formData.append("file", file);
        } else {
            formData.append(fileFieldName, file);
        }

        request.open("POST", action, true);
        for (String key : headers.keySet()) {
            request.setRequestHeader(key, headers.get(key));
        }
        request.send(formData);
    }

    private String formatSize(double size) {
        int threshold = 1000;
        if (Math.abs(size) < threshold)
            return size + " B";

        int unitIndex = -1;
        do {
            size /= threshold;
            ++unitIndex;
        } while (Math.abs(size) >= threshold && unitIndex < UNITS.length - 1);
        return new JsNumber(size).toFixed(1) + " " + UNITS[unitIndex];
    }

    public boolean isEnabled() {
        return fileUpload.isEnabled();
    }

    public void setEnabled(Boolean enabled) {
        fileUpload.setEnabled(enabled);
    }

    @Override
    public void setStyleName(String style) {
        super.setStyleName(style);
        List<String> styles = StringUtil.splitIgnoreBlank(style, " ");
        if (styles.size() > 0) {
            if (styles.size() > 0) {
                styleName = styles.get(0);
                background.setClassName(styleName + "-background");
                progress.setClassName(styleName + "-progress");
                text.setClassName(styleName + "-text");
            }
        }
    }
    public void reset()
    {

    }
    private void initWidget() {
        background = DOM.createDiv();
        progress = DOM.createDiv();
        text = DOM.createDiv();
        fileUpload = new FileUpload();

        Element body = this.getElement();
        body.appendChild(background);
        body.appendChild(progress);
        body.appendChild(text);
        this.add(fileUpload);

        Style style = body.getStyle();
        style.setPosition(Style.Position.RELATIVE);

        makeAbsolute(background.getStyle(), 1000);
        makeAbsolute(progress.getStyle(), 1001).setWidth(0, Style.Unit.PX);
        makeAbsolute(text.getStyle(), 1002);
        makeAbsolute(fileUpload.getElement().getStyle(), 1003).setOpacity(0.0);
        text.setInnerText("上传文件");

        fileUpload.addChangeHandler(fileChangeHandler);
        addDomHandler(clickHandler, ClickEvent.getType());
    }

    private Style makeAbsolute(Style style, int zIndex) {
        style.setPosition(Style.Position.ABSOLUTE);
        style.setLeft(0, Style.Unit.PX);
        style.setRight(0, Style.Unit.PX);
        style.setTop(0, Style.Unit.PX);
        style.setBottom(0, Style.Unit.PX);
        //style.setZIndex(zIndex);
        return style;
    }

    @Override
    public HandlerRegistration addCommonHandler(CommonEventHandler handler) {
        return addHandler(handler, CommonEvent.TYPE);
    }

}
