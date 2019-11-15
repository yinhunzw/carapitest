package com.gwm.carapitest.utils;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.gwm.carapitest.APITest.CarTestResult;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.format.Colour;
import jxl.write.Label;
import jxl.write.WritableCell;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;

public class ExcelUtil {
    private final static String TAG = "ExcelUtil";
    private Context mContext;
    public static WritableFont arial14font = null;
    public static WritableCellFormat arial14format = null;
    public static WritableFont arial10font = null;
    public static WritableCellFormat arial10format = null;
    public static WritableFont arial12font = null;
    public static WritableCellFormat arial12format = null;
    public static WritableCellFormat mRedFormat = null;
    public final static String UTF8_ENCODING = "UTF-8";
    public final static String GBK_ENCODING = "GBK";
    /** * 单元格的格式设置 字体大小 颜色 对齐方式、背景颜色等... */

    public ExcelUtil(Context context) {
        mContext = context;
    }
    public static void format() {
        try {
            arial14font = new WritableFont(WritableFont.ARIAL, 14, WritableFont.BOLD);
            arial14font.setColour(jxl.format.Colour.LIGHT_BLUE);
            arial14format = new WritableCellFormat(arial14font);
            arial14format.setAlignment(jxl.format.Alignment.CENTRE);
            arial14format.setBorder(jxl.format.Border.ALL,jxl.format.BorderLineStyle.THIN);
            arial14format.setBackground(jxl.format.Colour.VERY_LIGHT_YELLOW);
            arial10font = new WritableFont(WritableFont.ARIAL, 10, WritableFont.BOLD);
            arial10format = new WritableCellFormat(arial10font);
            arial10format.setAlignment(jxl.format.Alignment.CENTRE);
            arial10format.setBorder(jxl.format.Border.ALL,jxl.format.BorderLineStyle.THIN);
            arial10format.setBackground(Colour.GRAY_25);
            arial12font = new WritableFont(WritableFont.ARIAL, 10);
            arial12format = new WritableCellFormat(arial12font);
            arial10format.setAlignment(jxl.format.Alignment.CENTRE);//对齐格式
            arial12format.setBorder(jxl.format.Border.ALL,jxl.format.BorderLineStyle.THIN); //设置边框

            mRedFormat = new WritableCellFormat(arial10font);
            mRedFormat.setBorder(jxl.format.Border.ALL,jxl.format.BorderLineStyle.THIN);
            mRedFormat.setBackground(Colour.RED);
        } catch (WriteException e) {
            e.printStackTrace();
        }
    }
    /** * 初始化Excel * @param fileName * @param colName */
    public static void initExcel(String fileName, String[] colName)
    {
        format();
        WritableWorkbook workbook = null;
        try { File file = new File(fileName);
            if (!file.exists()) {
                file.createNewFile();
            }
            workbook = Workbook.createWorkbook(file);
            WritableSheet sheet = workbook.createSheet("测试结果", 0);
            //创建标题栏
            sheet.addCell((WritableCell) new Label(0, 0, fileName,arial14format));
            for (int col = 0; col < colName.length; col++) {
                Log.i(TAG, "initExcel: " + colName[col]);
                sheet.addCell(new Label(col, 0, colName[col], arial10format));
            }
            sheet.setRowView(0,340);
            //设置行高
            workbook.write();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (workbook != null) {
                try {
                    workbook.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
    @SuppressWarnings("unchecked")
    public static <T> void writeObjListToExcel(List<T> objList,String fileName, Context c) {
        if (objList != null && objList.size() > 0) {
            WritableWorkbook writebook = null;
            InputStream in = null;
            try {
                WorkbookSettings setEncode = new WorkbookSettings();
                setEncode.setEncoding(UTF8_ENCODING);
                in = new FileInputStream(new File(fileName));
                Workbook workbook = Workbook.getWorkbook(in);
                writebook = Workbook.createWorkbook(new File(fileName),workbook);
                WritableSheet sheet = writebook.getSheet(0);
                // sheet.mergeCells(0,1,0,objList.size()); //合并单元格
                // sheet.mergeCells()
                for (int j = 0; j < objList.size(); j++) {
                    ArrayList<String> list = (ArrayList<String>) objList.get(j);
                    for (int i = 0; i < list.size(); i++) {
                        if(i == 2 && "false".equals(list.get(i))) {
                            sheet.addCell(new Label(i, j + 1, list.get(i),mRedFormat));
                        } else {
                            sheet.addCell(new Label(i, j + 1, list.get(i),arial12format));
                        }

                        if (list.get(i).length() <= 5){
                            sheet.setColumnView(i,list.get(i).length()+8); //设置列宽
                        }else {
                            sheet.setColumnView(i,list.get(i).length()+5); //设置列宽
                        }
                    }
                    sheet.setRowView(j+1,350); //设置行高
                }
                writebook.write();
                Toast.makeText(c, "导出到手机存储中文件夹Record成功", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (writebook != null) {
                    try { writebook.close();
                    } catch (Exception e) { e.printStackTrace();
                    }
                } if (in != null) {
                    try { in.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    /** * 导出excel * @param view */
    public void exportExcel(String fileName, List<CarTestResult> resultList) {
        File file = new File(getSDPath() + "/Record");
        makeDir(file);
        ExcelUtil.initExcel(file.toString() + "/"+ fileName,
                new String[]{"class name", "method","result", "is system api","path", "feature", "expect value", "actual value", "log", "Exception"});
        String filePath = getSDPath() + "/Record/"+ fileName;
        System.out.println("=======================" + filePath);
        ExcelUtil.writeObjListToExcel(getRecordData(resultList), filePath, mContext);
    }

    /** * 将数据集合 转化成ArrayList<ArrayList<String>> * @return */
    private ArrayList<ArrayList<String>> getRecordData(List<CarTestResult> resultList) {
        ArrayList <ArrayList<String>> recordList = new ArrayList();
        for (int i = 0; i <resultList.size(); i++) {
            CarTestResult result = resultList.get(i);
            ArrayList<String> beanList = new ArrayList<String>();
            beanList.add(result.getClassName());
            beanList.add(result.getMethod());
            beanList.add(result.isPassed() + "");
            beanList.add(result.isSystemApi() + "");
            beanList.add(result.getPath());
            beanList.add(result.getFeature());
            beanList.add(result.getExpectedValue());
            beanList.add(result.getTrueValue());

            beanList.add(result.getComment());
            beanList.add(result.getException());
            recordList.add(beanList);
        }
        return recordList;
    }

    private String getSDPath() {
        File sdDir = null;
        boolean sdCardExist = Environment.getExternalStorageState().equals( Environment.MEDIA_MOUNTED);
        if (sdCardExist) {
            sdDir = Environment.getExternalStorageDirectory();
        }
        String dir = sdDir.toString();
        return dir;
    }

    public void makeDir(File dir) {
        Log.i("mainactivity", dir.getAbsolutePath() + "");
        if (!dir.getParentFile().exists()) {
            makeDir(dir.getParentFile());
        }
        try {
            Log.i("mainactivity", "makeDir: " + dir.mkdirs());
        } catch (Exception e) {
        }
    }

    public String getCurrentTimeString() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");// HH:mm:ss
        Date date = new Date(System.currentTimeMillis());
        return simpleDateFormat.format(date);
    }
}
