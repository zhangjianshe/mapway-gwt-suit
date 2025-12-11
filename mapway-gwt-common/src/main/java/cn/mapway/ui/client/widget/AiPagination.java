package cn.mapway.ui.client.widget;

import cn.mapway.ui.client.widget.buttons.LeftButton;
import cn.mapway.ui.client.widget.buttons.RightButton;
import cn.mapway.ui.shared.CommonEvent;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.HTMLPanel;

public class AiPagination extends CommonEventComposite {

    /**
     * 最小的页
     */
    private static final Integer minPage = 1;
    // 每页显示条目个数
    private Integer pageSize = 40;
    // 总条目数
    // 总页数
    private Integer pageCount = 1;
    // 页码按钮的数量，当总页数超过该值时会折叠; 大于等于 3 且小于等于 21 的奇数
    private Integer showPage = 3;
    // 当前页两侧显示的页数, 根据 showPage 来计算
    private Integer sideNum = 1;
    // 当前页数
    private Integer currentPage = 1;
    // 是否开启快速抵达
    private boolean fastArrival = true;
    // 总条目数
    private Integer total = 0;

    private boolean hideOnSinglePage = false;

    LeftButton leftButton = new LeftButton();
    RightButton rightButton = new RightButton();

    @UiField
    MyStyle style;

    @UiField
    HTMLPanel root;

    AiTextBox aiNumBox = new AiTextBox();


    // 前一个
    private ClickHandler previousHandler = (ClickEvent event) -> {
        setCurrentPage(currentPage - 1);
    };

    // 后一个
    private ClickHandler nextHandler = (ClickEvent event) -> {
        setCurrentPage(currentPage + 1);
    };

    private ClickHandler numButtonHandler = (ClickEvent event) -> {
        TextLabel button = (TextLabel) event.getSource();
        if(button != null && button.getData() != null){
            Integer data = (Integer) button.getData();
            currentPage = data;
            setCurrentPage(currentPage);
        }
    };

    private ChangeHandler currentPageChangeHandler = (ChangeEvent event)->{
        AiTextBox source = (AiTextBox) event.getSource();
        String value = source.getValue();
        Double currentPage = Double.parseDouble(value);
        if(currentPage != null){
            setCurrentPage(currentPage.intValue());
        }
    };


    public AiPagination() {
        initWidget(ourUiBinder.createAndBindUi(this));
        leftButton.addClickHandler(previousHandler);
        rightButton.addClickHandler(nextHandler);
        aiNumBox.asNumber();
        aiNumBox.setWidth("50px");
        aiNumBox.setHeight("25px");
        leftButton.getElement().getStyle().setDisplay(Style.Display.INLINE_BLOCK);
        rightButton.getElement().getStyle().setDisplay(Style.Display.INLINE_BLOCK);
        aiNumBox.getElement().getStyle().setMargin(3, Style.Unit.PX);
        aiNumBox.getElement().getStyle().setFontSize(18, Style.Unit.PX);
        aiNumBox.addChangeHandler(currentPageChangeHandler);
    }

    public void render(){
        root.clear();
        if(hideOnSinglePage){
            if(pageCount <= 1){
                return;
            }
        }
        root.add(leftButton);
        // 计算显示的页数的范围
        int first = currentPage - sideNum;
        int last = currentPage + sideNum;
        if(first < minPage || last > pageCount){
            // 需要进行调整
            if(first < minPage){
                int moveDistance = minPage - first;
                first = minPage;
                last = showPage;
            }
            if(last > pageCount){
                last = pageCount;
                first = last - showPage + 1;
                if(first < minPage){
                    first = minPage;
                }
            }
        }

        // 根据显示的首位数来判断是否需要加 ... 省略号
        if(first != minPage){
            addNumButton(minPage);
            if(first > (minPage + 1)){
                addTextButton("...");
            }
        }
        // 进行遍历
        for (int i = first; i <= last; i++) {
            addNumButton(i);
        }
        if(last != pageCount){
            if(last < (pageCount - 1)){
                addTextButton("...");
            }
            addNumButton(pageCount);
        }
        root.add(rightButton);
        if(pageCount != 1 && fastArrival){
            addTextButton("前往");
            root.add(aiNumBox);
            addTextButton("页");
        }
    }


    public void setPageSize(Integer pageSize) {
        if(pageSize < 1){
            pageSize = 1;
        }
        this.pageSize = pageSize;
        setTotal(total);
    }

    public void setTotal(Integer total) {
        if(total < 0){
            total = 0;
        }
        this.total = total;
        this.pageCount = calcPageCount();
        this.currentPage = 1;
        aiNumBox.setValue(this.currentPage + "");
        aiNumBox.setMin(1);
        aiNumBox.setMax(pageCount);
        render();
    }

    public void setTotal(Long total) {
        int totalInt = 0;
        if(total == null){
            totalInt = 0;
        } else {
            totalInt = total.intValue();
        }
        setTotal(totalInt);
    }

    public void updateTotal(Integer total) {
        if(total < 0){
            total = 0;
        }
        this.total = total;
        this.pageCount = calcPageCount();
        if(currentPage > pageCount){
            currentPage = pageCount;
        }
        aiNumBox.setValue(this.currentPage + "");
        aiNumBox.setMin(1);
        aiNumBox.setMax(pageCount);
        render();
    }

    public void setShowPage(Integer showPage) {
        if(showPage > 21 || showPage < 3){
            return;
        }
        int remain = showPage & 2;
        if(remain == 1){
            showPage = showPage - 1;
        }
        this.showPage = showPage;
        this.sideNum = (showPage - 1) / 2;
        render();
    }

    public void setCurrentPage(Integer currentPage) {
        setCurrentPage(currentPage, true);
    }

    public void setCurrentPage(Integer currentPage, boolean eventFlag) {
        if(pageNumCheck(currentPage)){
            this.currentPage = currentPage;
            aiNumBox.setValue(this.currentPage + "");
            selectPage(eventFlag);
            render();
        }
    }

    public void setFastArrival(boolean fastArrival) {
        this.fastArrival = fastArrival;
        render();
    }

    public void setHideOnSinglePage(boolean hideOnSinglePage) {
        this.hideOnSinglePage = hideOnSinglePage;
        render();
    }

    public Integer getTotal() {
        return total;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public Integer getCurrentPage() {
        return currentPage;
    }

    public Integer getShowPage() {
        return showPage;
    }

    public Boolean getFastArrival() {
        return fastArrival;
    }

    public boolean isHideOnSinglePage() {
        return hideOnSinglePage;
    }

    public Integer[] getPageScope() {
        Integer[] scope = new Integer[2];

        int start = (currentPage - 1) * pageSize;
        int end = start + pageSize;
        if(end > total){
            end = total;
        }
        scope[0] = start;
        scope[1] = end;
        return scope;
    }

    private void selectPage(boolean eventFlag){
        if(eventFlag){
            CommonEvent ev = CommonEvent.paginationSelectEvent(currentPage);
            fireEvent(ev);
        }
    }

    private boolean pageNumCheck(int value){
        return value >= 1 && value <= pageCount;
    }

    public Integer getPageCount() {
        return this.pageCount;
    }

    private int calcPageCount(){
        int pageCount = total / pageSize;
        if(total % pageSize != 0){
            pageCount = pageCount + 1;
        }
        if(pageCount == 0){
            pageCount = 1;
        }
        return pageCount;
    }

    private void addNumButton(int num){
        TextLabel button = new TextLabel(num + "");
        button.setStyleName("ai-ib-font " + style.button());
        button.getElement().getStyle().setFontSize(20, Style.Unit.PX);
        if(num != currentPage){
            button.setData(num);
            button.addClickHandler(numButtonHandler);
        } else {
            button.addStyleName( style.buttonSelect());
            button.getElement().getStyle().setFontWeight(Style.FontWeight.BOLD);
        }
        root.add(button);
    }

    private void addTextButton(String text){
        TextLabel button = new TextLabel( text);
        button.setStyleName("ai-ib-font ");
        button.getElement().getStyle().setFontSize(20, Style.Unit.PX);
        root.add(button);
    }

//    private void addButton(Integer text){
//        AiButton btn = new AiButton(text + "");
////        button.setStyleName("ai-ib-font ");
//        btn.setStyleName(style.button());
//        btn.getElement().getStyle().setFontSize(20, Style.Unit.PX);
//        root.add(btn);
//    }

    public void clear() {
        setTotal(0);
    }

    public void decrease(int num) {
        total = total - num;
        Integer nowPage = currentPage;
        setTotal(total);
        if(nowPage > pageCount){
            nowPage = pageCount;
        }
        if(nowPage < 1){
            nowPage = 1;
        }
        setCurrentPage(nowPage);
    }


    interface AiPaginationUiBinder extends UiBinder<HTMLPanel, AiPagination> {}

    private static AiPaginationUiBinder ourUiBinder = GWT.create(AiPaginationUiBinder.class);

    public interface MyStyle extends CssResource
    {
        String pagination();
        String button();
        String buttonSelect();
    }
}