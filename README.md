# 项目介绍

## 前言
本项目实现为 Android UiAutomator 2自定义报告
本项目是一个测试报告，报告清晰简单，有饼图，汇总，运行详情。有兴趣的可以下载试用！

## 使用方式
1. main目录下新建assets目录，然后将目录下的模板文件复制到自己项目中。
2. 复制ReportWatcher到自己项目中
3. 调用例子
    private static Instrumentation mInstrumentation;
    private static UiDevice mUiDevice;
    @Rule
    public ReportWatcher eu = new ReportWatcher(mInstrumentation);
4.报告位置
报告保存路径：<sdcard>/Android/<包名>/files/report
例如：/mnt/sdcard/Android/data/com.fxs.uireport/files/report

## 报告展示
![UI2REPORT](https://github.com/fan2597/AndroidUIAutomator2Report/blob/master/img/bg.png)

## 打赏 觉得有帮助，可以给作者赏跟烟抽
![打赏](https://github.com/fan2597/AndroidUIAutomator2Report/blob/master/img/zf.png)

## 鸣谢

感谢zhangfei兄弟的模板！

zhangfei兄弟git地址：[zhangfei地址](https://github.com/zhangfei19841004/ztest)