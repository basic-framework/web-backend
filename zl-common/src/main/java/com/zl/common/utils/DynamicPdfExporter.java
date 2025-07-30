package com.zl.common.utils;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.function.Function;

 /**
 * 动态PDF导出工具类
 * 支持自定义标题、列信息、数据转换和样式
 * @Author: GuihaoLv
 */
public class DynamicPdfExporter<T> {

    // 默认中文字体配置
    private static final String FONT_PATH = "STSong-Light";
    private static final String FONT_ENCODING = "UniGB-UCS2-H";

    // 导出配置参数
    private String title;
    private List<ColumnConfig<T>> columnConfigs;
    private float[] columnWidths;
    private FontConfig fontConfig = new FontConfig();

    // 构造函数，接收必要的导出配置
    public DynamicPdfExporter(String title, List<ColumnConfig<T>> columnConfigs) {
        this.title = title;
        this.columnConfigs = columnConfigs;
        this.columnWidths = new float[columnConfigs.size()];
        // 默认平均分配列宽
        for (int i = 0; i < columnWidths.length; i++) {
            columnWidths[i] = 1.0f;
        }
    }

    // 导出数据为PDF字节数组
    public byte[] export(List<T> dataList) throws DocumentException, IOException {
        // 创建文档
        Document document = new Document(PageSize.A4, 50, 50, 50, 50);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter.getInstance(document, baos);

        // 打开文档
        document.open();

        // 初始化字体
        initFonts();

        // 添加标题
        addTitle(document);

        // 添加数据统计信息
        addSummaryInfo(document, dataList.size());

        // 创建并添加表格
        PdfPTable table = createTable();
        addTableHeader(table);
        addTableData(table, dataList);
        document.add(table);

        // 关闭文档
        document.close();

        return baos.toByteArray();
    }

    // 初始化字体
    private void initFonts() throws IOException, DocumentException {
        BaseFont baseFont = BaseFont.createFont(FONT_PATH, FONT_ENCODING, BaseFont.NOT_EMBEDDED);
        fontConfig.titleFont = new Font(baseFont, fontConfig.titleFontSize, fontConfig.titleFontStyle);
        fontConfig.contentFont = new Font(baseFont, fontConfig.contentFontSize, fontConfig.contentFontStyle);
        fontConfig.headerFont = new Font(baseFont, fontConfig.headerFontSize, fontConfig.headerFontStyle);
    }

    // 添加标题
    private void addTitle(Document document) throws DocumentException {
        Paragraph titlePara = new Paragraph(title, fontConfig.titleFont);
        titlePara.setAlignment(Element.ALIGN_CENTER);
        titlePara.setSpacingAfter(20);
        document.add(titlePara);
    }

    // 添加统计信息
    private void addSummaryInfo(Document document, int dataCount) throws DocumentException {
        Paragraph summary = new Paragraph("总记录数: " + dataCount, fontConfig.contentFont);
        summary.setSpacingAfter(15);
        document.add(summary);
    }

    // 创建表格
    private PdfPTable createTable() throws DocumentException {
        PdfPTable table = new PdfPTable(columnConfigs.size());
        table.setWidthPercentage(100);
        table.setSpacingBefore(10f);
        table.setSpacingAfter(10f);
        table.setWidths(columnWidths);
        return table;
    }

    // 添加表格头部
    private void addTableHeader(PdfPTable table) {
        for (ColumnConfig<T> config : columnConfigs) {
            PdfPCell cell = new PdfPCell();
            cell.setPhrase(new Phrase(config.getHeaderName(), fontConfig.headerFont));
            cell.setBackgroundColor(config.getHeaderBgColor());
            cell.setHorizontalAlignment(config.getHeaderAlign());
            cell.setPadding(5f);
            table.addCell(cell);
        }
    }

    // 添加表格数据
    private void addTableData(PdfPTable table, List<T> dataList) {
        for (T data : dataList) {
            for (ColumnConfig<T> config : columnConfigs) {
                String cellValue = config.getValueExtractor().apply(data);
                PdfPCell cell = new PdfPCell(new Phrase(cellValue, fontConfig.contentFont));
                cell.setVerticalAlignment(config.getContentAlign());
                cell.setPadding(5f);
                table.addCell(cell);
            }
        }
    }

    // 设置列宽
    public DynamicPdfExporter<T> setColumnWidths(float[] columnWidths) {
        this.columnWidths = columnWidths;
        return this;
    }

    // 设置字体配置
    public DynamicPdfExporter<T> setFontConfig(FontConfig fontConfig) {
        this.fontConfig = fontConfig;
        return this;
    }

    /**
     * 列配置类
     * 用于定义每列的标题、数据提取方式和样式
     */
    public static class ColumnConfig<T> {
        private String headerName;
        private Function<T, String> valueExtractor;
        private int headerAlign = Element.ALIGN_CENTER;
        private int contentAlign = Element.ALIGN_MIDDLE;
        private BaseColor headerBgColor = BaseColor.LIGHT_GRAY;

        public ColumnConfig(String headerName, Function<T, String> valueExtractor) {
            this.headerName = headerName;
            this.valueExtractor = valueExtractor;
        }

        // getter和setter
        public String getHeaderName() { return headerName; }
        public Function<T, String> getValueExtractor() { return valueExtractor; }
        public int getHeaderAlign() { return headerAlign; }
        public ColumnConfig<T> setHeaderAlign(int headerAlign) {
            this.headerAlign = headerAlign;
            return this;
        }
        public int getContentAlign() { return contentAlign; }
        public ColumnConfig<T> setContentAlign(int contentAlign) {
            this.contentAlign = contentAlign;
            return this;
        }
        public BaseColor getHeaderBgColor() { return headerBgColor; }
        public ColumnConfig<T> setHeaderBgColor(BaseColor headerBgColor) {
            this.headerBgColor = headerBgColor;
            return this;
        }
    }

    /**
     * 字体配置类
     * 用于定义PDF中各种文本的字体样式
     */
    public static class FontConfig {
        private int titleFontSize = 16;
        private int titleFontStyle = Font.BOLD;
        private int headerFontSize = 12;
        private int headerFontStyle = Font.BOLD;
        private int contentFontSize = 12;
        private int contentFontStyle = Font.NORMAL;

        private Font titleFont;
        private Font headerFont;
        private Font contentFont;

        // getter和setter
        public FontConfig setTitleFontSize(int titleFontSize) {
            this.titleFontSize = titleFontSize;
            return this;
        }
        public FontConfig setTitleFontStyle(int titleFontStyle) {
            this.titleFontStyle = titleFontStyle;
            return this;
        }
        public FontConfig setHeaderFontSize(int headerFontSize) {
            this.headerFontSize = headerFontSize;
            return this;
        }
        public FontConfig setHeaderFontStyle(int headerFontStyle) {
            this.headerFontStyle = headerFontStyle;
            return this;
        }
        public FontConfig setContentFontSize(int contentFontSize) {
            this.contentFontSize = contentFontSize;
            return this;
        }
        public FontConfig setContentFontStyle(int contentFontStyle) {
            this.contentFontStyle = contentFontStyle;
            return this;
        }
    }
}