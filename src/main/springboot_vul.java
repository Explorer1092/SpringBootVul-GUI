package src.main;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import src.main.Exp.ExpImp.ExpImp;
import src.main.FileCommon.File;
import src.main.impl.ResultCallback;
import src.main.module.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;

public class springboot_vul extends Application {
        public int Vulvalue;
        public int clsValue;
        public static TextFlow consoleOutput;
        public static ExecutorService executorService;
        public static ScrollPane scrollPane; // 滚动
        public static ProgressBar progressBar; // 进度条
        public static ProgressBar progressBar1; // 进度条


        @Override
        public void start(Stage stage) throws IOException {
                File f = new File();
                Map<Integer, JsonArray> totallist = f.parseVulList("list.json");
                List<String> vulnLabels = new ArrayList<>();
                List<Integer> vulnValues = new ArrayList<>();
                for (JsonElement elem : totallist.get(1)) {
                        JsonObject vulnList = elem.getAsJsonObject();
                        String vulabel = vulnList.get("label").getAsString();
                        int vulvalue = vulnList.get("value").getAsInt();
                        vulnLabels.add(vulabel);
                        vulnValues.add(vulvalue);
                }
                String[] vulnLabelsArray = vulnLabels.toArray(new String[0]);
                int[] vulnValuesArray = vulnValues.stream().mapToInt(i -> i).toArray();

                List<String> clsLabels = new ArrayList<>();
                List<Integer> clsValues = new ArrayList<>();
                for (JsonElement elem : totallist.get(2)) {
                        JsonObject clsList = elem.getAsJsonObject();
                        String clslabel = clsList.get("label").getAsString();
                        int clsvalue = clsList.get("value").getAsInt();
                        clsLabels.add(clslabel);
                        clsValues.add(clsvalue);
                }
                String[] clsLabelsArray = clsLabels.toArray(new String[0]);
                int[] clsValuesArray = clsValues.stream().mapToInt(i -> i).toArray();

                // 地址标签
                Label addlabel = new Label("地址: ");
                addlabel.setMaxSize(50, 30);
                addlabel.setFont(new Font("宋体", 13));
                Label explabel = new Label("漏洞列表: ");
                explabel.setMaxSize(60, 30);
                explabel.setFont(new Font("宋体", 13));
                Label argslabel = new Label("脱敏参数: ");
                argslabel.setMaxSize(60, 30);
                argslabel.setFont(new Font("宋体", 13));
                Label cmdlabel = new Label("命令参数: ");
                cmdlabel.setMaxSize(60, 30);
                cmdlabel.setFont(new Font("宋体", 13));
                Label clslabel = new Label("调用类名: ");
                clslabel.setMaxSize(60,30);
                clslabel.setFont(new Font("宋体", 13));
                Label curstatuslabel = new Label("当前状态： ");
                curstatuslabel.setMaxSize(70,30);
                curstatuslabel.setFont(new Font("宋体", 13));

                // 类方法调用下拉框
                ComboBox<String> clsComboBox = new ComboBox<>();
                clsComboBox.setStyle("-fx-font-family: 'Microsoft YaHei, SimSun, PingFang SC'; -fx-font-size: 14;");                clsComboBox.getItems().addAll(clsLabelsArray);
                clsComboBox.setOnAction(event -> {
                        String optionsValue = clsComboBox.getValue();
                        // 查找选项对应的值
                        for (int i = 0; i < clsLabelsArray.length; i++) {
                                if (clsLabelsArray[i].equals(optionsValue)) {
                                        clsValue = clsValuesArray[i];
                                        break;
                                }
                        }
                });
                clsComboBox.setPrefWidth(200);
                clsComboBox.setValue(null); // 默认无

                //设置VPS内容
                Label vpsaddrlabel = new Label("IP：");
                vpsaddrlabel.setMaxSize(60, 30);
                vpsaddrlabel.setFont(new Font("宋体", 13));
                TextField VpsAddrtf = new TextField();
                VpsAddrtf.setPrefWidth(100);
                Label vpsportlabel = new Label("port：");
                vpsportlabel.setMaxSize(60,30);
                vpsaddrlabel.setFont(new Font("宋体", 13));
                TextField VpsPorttf = new TextField();
                VpsPorttf.setPrefWidth(80);

                // 代理
                Button proxybtn = new Button("设置代理");
                proxybtn.setPrefWidth(80);
                proxybtn.setOnAction(event -> {
                        Stage dialog = new Stage();
                        dialog.initOwner(stage); // 在显示之前设置拥有者
                        HBox hb1 = new HBox(10);
                        hb1.setAlignment(Pos.CENTER); // 设置hb1居中对齐
                        HBox hb2 = new HBox(10);
                        hb2.setAlignment(Pos.CENTER); // 设置hb2居中对齐
                        VBox vb1 = new VBox(10);
                        vb1.setAlignment(Pos.CENTER); // 设置VBox居中对齐
                        TextField ipField = new TextField();
                        TextField portField = new TextField();
                        ipField.setMaxWidth(80);
                        portField.setMaxWidth(80);
                        String systemIp = System.getProperty("http.proxyHost", "");
                        String systemPort = System.getProperty("http.proxyPort", "");
                        ipField.setText(systemIp);
                        portField.setText(systemPort);

                        Button updateButton = new Button("确定");
                        Button canccelButton = new Button("取消");
                        HBox hb3 = new HBox(10);
                        hb3.setAlignment(Pos.CENTER); // 设置hb3居中对齐
                        hb3.getChildren().addAll(updateButton,canccelButton);

                        hb1.getChildren().addAll(new Label("http(s)代理IP:"),ipField);
                        HBox.setMargin(ipField, new Insets(0, 0, 0, 15)); // 设置ipField左侧边距为10
                        hb2.getChildren().addAll(new Label("http(s)代理端口:"),portField);

                        vb1.getChildren().addAll(hb1,hb2,hb3);
                        Scene dialogScene = new Scene(vb1, 300, 200);
                        dialog.setScene(dialogScene);
                        dialog.show();
                        dialog.setOnCloseRequest(e -> {
                                dialog.close();
                        });

                        updateButton.setOnAction(e -> {
                                String ip = ipField.getText();
                                String port = portField.getText();
                                String ipPattern =
                                        "^((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}" +
                                                "(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$";
                                if (ip.isEmpty()) {
                                        applyProxy(ip, port);
                                        dialog.close();
                                } else {
                                        if (ip.matches(ipPattern)){
                                                applyProxy(ip, port);
                                                dialog.close();
                                        } else{
                                                showAlertEmpty("IP设置错误");
                                        }
                                }
                        });
                        canccelButton.setOnAction(e -> {
                                dialog.close();
                        });
                });

                // 调用类输入框容器
                HBox clsBox = new HBox(10);
                clsBox.setAlignment(Pos.TOP_LEFT);
                clsBox.setPrefHeight(40);
                HBox vpsbox = new HBox(10);
                vpsbox.getChildren().addAll(vpsaddrlabel,VpsAddrtf,vpsportlabel,VpsPorttf);
                clsBox.getChildren().addAll(clslabel,clsComboBox,vpsbox);
                clsBox.setPadding(new Insets(0, 0, 0, 20));

                // 下拉框
                ComboBox<String> comboBox = new ComboBox<>();
                comboBox.setStyle("-fx-font-family: 'Microsoft YaHei, SimSun, PingFang SC'; -fx-font-size: 14;");                comboBox.getItems().addAll(vulnLabelsArray);
                comboBox.setOnAction(event -> {
                        String optionsValue = comboBox.getValue();
                        Vulvalue = -1;
                        // 查找选项对应的值
                        for (int i = 0; i < vulnLabelsArray.length; i++) {
                                if (vulnLabelsArray[i].equals(optionsValue)) {
                                        Vulvalue = vulnValuesArray[i];
                                        break;
                                }
                        }
                });
                comboBox.setPrefWidth(200);
                comboBox.setValue(null); // 默认选中“无”

                // 输入框
                TextField Addrtf = new TextField();
                Addrtf.setPromptText("请输入地址");
                Addrtf.setPrefWidth(200);
                TextField Argstf = new TextField();
                Argstf.setPromptText("请输入参数");
                Argstf.setPrefWidth(200);
                TextField Cmdtf = new TextField();
                Cmdtf.setPromptText("请输入命令");
                Cmdtf.setPrefWidth(200);
                TextField Clstf = new TextField();
                Clstf.setPrefWidth(200);

                // 按钮
                Button Expbtn = new Button("开干");
                Expbtn.setOnAction(event -> {
                        try {
                                handlerExp(Addrtf,Argstf,Cmdtf,VpsAddrtf,VpsPorttf);
                        } catch (IOException e) {
                                throw new RuntimeException(e);
                        }
                });
                Expbtn.setPrefWidth(80);
                Button Shellbtn = new Button("Getshell");
                Shellbtn.setOnAction(event -> {
                        try {
                                handlerGetshell(Addrtf,VpsAddrtf,VpsPorttf);
                        } catch (IOException e) {
                                throw new RuntimeException(e);
                        }
                });
                Shellbtn.setPrefWidth(80);
                Button Delbtn = new Button("痕迹清除");
                Delbtn.setOnAction(event -> {
                        try {
                                handlerDelData(Addrtf);
                        } catch (IOException e) {
                                throw new RuntimeException(e);
                        }
                });
                Delbtn.setPrefWidth(80);
                FlowPane btnFlowPane = new FlowPane(Orientation.HORIZONTAL, 10, 10);
                btnFlowPane.getChildren().addAll(Expbtn, Shellbtn, Delbtn);
                btnFlowPane.setPrefWrapLength(200); // 设置宽度阈值，超出后换行

                VBox proxybtnBox = new VBox(20,btnFlowPane,proxybtn);
                proxybtnBox.setAlignment(Pos.TOP_LEFT);
                proxybtnBox.setPadding(new Insets(0, 10, 10, 10)); // 整体边距


                // 地址框和漏洞列表框
                HBox addrBox = new HBox(10);
                addrBox.getChildren().addAll(addlabel, Addrtf, explabel, comboBox);
                addrBox.setAlignment(Pos.TOP_LEFT);
                addrBox.setPadding(new Insets(0, 0, 0, 20));
                HBox.setMargin(Addrtf, new Insets(0, 0, 0, 25));  // 向右移动地址框

                // 漏洞利用框
                HBox menuBox = new HBox(10);
                menuBox.setAlignment(Pos.TOP_LEFT);
                menuBox.setPrefHeight(40);
                menuBox.getChildren().addAll(argslabel, Argstf, cmdlabel, Cmdtf);
                menuBox.setPadding(new Insets(0, 0, 0, 20));

                // 按钮容器
                VBox buttonBox = new VBox(10);
                buttonBox.getChildren().addAll(addrBox, menuBox, clsBox);

                HBox boxContainer = new HBox();
                // 左右两边各为一个容器
                boxContainer.getChildren().addAll(buttonBox,proxybtnBox);

                // 添加间隔区域
                Region topSpacer = new Region();
                VBox.setVgrow(topSpacer, Priority.ALWAYS);

                // 文本框 (改为 TextFlow)
                consoleOutput = new TextFlow();
                consoleOutput.setPadding(new Insets(10, 20, 10, 20)); // 设置边距
                scrollPane = new ScrollPane(consoleOutput);
                scrollPane.setFitToWidth(true);
                scrollPane.setPrefHeight(200);  // 调整高度
                Platform.runLater(()->{
                        consoleOutput.requestFocus();
                        scrollPane.setVvalue(1.0);
                });

                // 为 Text 添加右键菜单，允许复制
                ContextMenu contextMenu = new ContextMenu();
                MenuItem copyItem = new MenuItem("复制");
                contextMenu.getItems().add(copyItem);

                // 状态栏
                HBox statusBar = new HBox();
                // 创建容器，将“当前状态”标签和进度条放在里面
                HBox statusContainer = new HBox();
                Label statusLabel = new Label("禁止用于未授权测试！");
                statusLabel.setFont(new Font("宋体", 13));
                statusLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: red;");
                progressBar = new ProgressBar(0);  // 初始化进度条，默认值为0
                progressBar.setPrefWidth(200);     // 设置进度条的宽度
                // 创建一个空的区域占位符，推动右边的内容到最右侧
                Region spacer = new Region();
                HBox.setHgrow(spacer, Priority.ALWAYS);

                // 将标签和进度条放到状态容器中
                statusBar.getChildren().addAll(statusLabel, spacer, curstatuslabel, progressBar);
                statusContainer.setAlignment(Pos.CENTER_RIGHT);
                statusBar.getChildren().add(statusContainer);
                // 设置状态栏的样式
                statusBar.setStyle("-fx-background-color: #f0f0f0;");
                statusBar.setPadding(new Insets(5, 10, 5, 10));

                // 创建主界面布局
                BorderPane mainLayout = new BorderPane();
                mainLayout.setCenter(scrollPane);         // ScrollPane (包裹TextFlow) 放在中间
                mainLayout.setBottom(statusBar);          // 状态栏放在底部

                VBox topLayout = new VBox(10);
                topLayout.getChildren().addAll(topSpacer, boxContainer); // Add spacer before buttonBox

                mainLayout.setTop(topLayout);

                // 创建场景并设置到舞台
                Scene scene = new Scene(mainLayout, 800, 600);
                stage.setTitle("SpringBootGUI by wh1t3zer");
                stage.setScene(scene);
                stage.show();

                // 初始化线程池
                executorService = Executors.newSingleThreadExecutor();
        }

        public void handlerAllScan(String address){
                showAlertEmpty("暂时未开启全部扫描功能，防止批量造成服务器崩溃服务停止");
        }
        public void handlerScanVul(String address) throws IOException {
                final AtomicReference<Double> curlines = new AtomicReference<>(0.0);
                final AtomicReference<Double> totalLines = new AtomicReference<>(0.0);
                // 暂无证书模块，待设置
                if (address.endsWith("/")) {
                                address = address.replaceAll("/$", "");
                }
                if (!address.startsWith("http://") && !address.startsWith("https://")) {
                                address = "http://" + address;
                }
                consoleOutput.getChildren().clear();
                consoleOutput.getChildren().addAll(new Text("端点扫描进行中，请勿关闭！\n"));
                ScanVul sv = new ScanVul(address);  // 执行具体扫描操作
                executorService.submit(() -> {
                        try {
                                sv.scanVul(new ResultCallback() {
                                        @Override
                                        public void onResult(String result) {
                                                Platform.runLater(() -> {
                                                        if (result.contains("curlines")) {
                                                                int delimiterIndex = result.indexOf(":");
                                                                if (delimiterIndex != -1) {
                                                                        curlines.set(Double.parseDouble(result.substring(delimiterIndex + 1).trim()));
                                                                        // 更新进度条
                                                                        if ((curlines.get() > 0) && totalLines.get() > 0) {
                                                                                double progress = curlines.get() / totalLines.get();
                                                                                progressBar.setProgress(progress);
                                                                        }
                                                                }
                                                        } else if (result.contains("totalLines")) {
                                                                int delimiterIndex = result.indexOf(":");
                                                                if (delimiterIndex != -1) {
                                                                        totalLines.set(Double.parseDouble(result.substring(delimiterIndex + 1).trim()));
                                                                }
                                                        }else {
                                                                // 输出其他内容到控制台
                                                                Text text = new Text(result + "\n");
                                                                consoleOutput.getChildren().add(text);
                                                                // 自动滚动到最新内容
                                                                scrollPane.setVvalue(1.0);
                                                        }
                                                });

                                        }
                                        @Override
                                        public void onComplete() {
                                                Platform.runLater(() -> {
                                                        Text completeText = new Text("扫描完成！\n");
                                                        consoleOutput.getChildren().add(completeText);
                                                        // 自动滚动到最新内容
                                                        scrollPane.setVvalue(1.0);
                                                });
                                        }
                                });
                        } catch (InterruptedException e) {
                                throw new RuntimeException(e);
                        }
                });

        }

        public void handlerGetSp_1(String address,String args) throws IOException {
                consoleOutput.getChildren().clear();
                if (args.isEmpty()){
                        showAlertEmpty("脱敏参数为空！");
                }else{
                        if (address.endsWith("/")) {
                                address = address.replaceAll("/$", "");
                        }
                        if (!address.startsWith("http://") && !address.startsWith("https://")) {
                                address = "http://" + address;
                        }
                        GetSpPassWord_I gsp = new GetSpPassWord_I(address,args,clsValue);
                        gsp.Exp(new ResultCallback() {
                                @Override
                                public void onResult(String result) {
                                        Platform.runLater(() -> {
                                                        // 输出其他内容到控制台
                                                        Text text = new Text(result + "\n");
                                                        consoleOutput.getChildren().add(text);
                                                        // 自动滚动到最新内容
                                                        scrollPane.setVvalue(1.0);
                                        });
                                }

                                @Override
                                public void onComplete() {

                                }
                        });
                }
        }
        public void handlerGetSp_2(String address,String args,String vpsIP,String vpsPORT) throws IOException {
                consoleOutput.getChildren().clear();
                // 暂无证书模块，待设置
                if (address.isEmpty()){
                        showAlertEmpty("地址为空！");
                }else {
                        if (address.endsWith("/")) {
                                address = address.replaceAll("/$", "");
                        }
                        if (!address.startsWith("http://") && !address.startsWith("https://")) {
                                address = "http://" + address;
                        }
                        if (vpsIP.isEmpty()) {
                                showAlertEmpty("反弹IP为空！");
                        }
                        GetSpPassWord_II gsp = new GetSpPassWord_II(address,vpsIP,vpsPORT,args);
                        gsp.Exp(new ResultCallback() {
                                @Override
                                public void onResult(String result) {
                                        Platform.runLater(() -> {
                                                // 输出其他内容到控制台
                                                Text text = new Text(result + "\n");
                                                consoleOutput.getChildren().add(text);
                                                // 自动滚动到最新内容
                                                scrollPane.setVvalue(1.0);
                                        });
                                }
                                @Override
                                public void onComplete() {
                                }
                        });

                }
        }
        public void handlerGetSp_3(String address,String args,String vpsIP,String vpsPORT) throws IOException {
                consoleOutput.getChildren().clear();
                if (address.isEmpty()){
                        showAlertEmpty("地址为空！");
                }else {
                        if (address.endsWith("/")) {
                                address = address.replaceAll("/$", "");
                        }
                        if (!address.startsWith("http://") && !address.startsWith("https://")) {
                                address = "http://" + address;
                        }
                        if (vpsIP.isEmpty()) {
                                showAlertEmpty("反弹IP为空！");
                        }
                        GetSpPassWord_III gsp = new GetSpPassWord_III(address,vpsIP,vpsPORT,args);
                        gsp.Exp(new ResultCallback() {
                                @Override
                                public void onResult(String result) {
                                        Platform.runLater(() -> {
                                                // 输出其他内容到控制台
                                                Text text = new Text(result + "\n");
                                                consoleOutput.getChildren().add(text);
                                                // 自动滚动到最新内容
                                                scrollPane.setVvalue(1.0);
                                        });
                                }
                                @Override
                                public void onComplete() {
                                }
                        });
                }
        }
        public void handlerSnakeYamlRce(String address,String vpsIP) throws IOException {
                consoleOutput.getChildren().clear();
                // 暂无证书模块，待设置
                if (address.isEmpty()){
                        showAlertEmpty("地址为空！");
                }else {
                        if (address.endsWith("/")) {
                                address = address.replaceAll("/$", "");
                        }
                        if (!address.startsWith("http://") && !address.startsWith("https://")) {
                                address = "http://" + address;
                        }
                        SnakeYamlRCE syr = new SnakeYamlRCE(address,vpsIP);
                        syr.Exp(new ResultCallback() {
                                @Override
                                public void onResult(String result) {
                                        Platform.runLater(() -> {
                                                // 输出其他内容到控制台
                                                Text text = new Text(result + "\n");
                                                consoleOutput.getChildren().add(text);
                                                // 自动滚动到最新内容
                                                scrollPane.setVvalue(1.0);
                                        });
                                }
                                @Override
                                public void onComplete() {
                                }
                        });
                }
        }
        public void handlerSpgRCE(String address, String command) throws IOException {
                // 清空控制台输出
                consoleOutput.getChildren().clear();
                // 暂无证书模块，待设置
                if (address.isEmpty()){
                        showAlertEmpty("地址为空！");
                }else {
                        if (address.endsWith("/")) {
                                address = address.replaceAll("/$", "");
                        }
                        if (!address.startsWith("http://") && !address.startsWith("https://")) {
                                address = "http://" + address;
                        }
                        SpringGawRCE spg = command.isEmpty() ? new SpringGawRCE(address) : new SpringGawRCE(address, command);
                        spg.GawExp(new ResultCallback() {
                                @Override
                                public void onResult(String result) {
                                        Platform.runLater(() -> {
                                                // 输出其他内容到控制台
                                                System.out.println(result);
                                                Text text = new Text(result + "\n");
                                                consoleOutput.getChildren().add(text);
                                                // 自动滚动到最新内容
                                                scrollPane.setVvalue(1.0);
                                        });
                                }
                                @Override
                                public void onComplete() {
                                }
                        });
                }
        }

        public void handlerDelSpg(String address) throws IOException {
                // 清空控制台输出
                consoleOutput.getChildren().clear();
                // 暂无证书模块，待设置
                if (address.isEmpty()){
                        showAlertEmpty("地址为空！");
                }else {
                        if (address.endsWith("/")) {
                                address = address.replaceAll("/$", "");
                        }
                        if (!address.startsWith("http://") && !address.startsWith("https://")) {
                                address = "http://" + address;
                        }
                        SpringGawRCE spg = new SpringGawRCE(address);
                        spg.DelGaw(new ResultCallback() {
                                @Override
                                public void onResult(String result) {
                                        Platform.runLater(() -> {
                                                // 输出其他内容到控制台
                                                Text text = new Text(result + "\n");
                                                consoleOutput.getChildren().add(text);
                                                // 自动滚动到最新内容
                                                scrollPane.setVvalue(1.0);
                                        });
                                }
                                @Override
                                public void onComplete() {
                                }
                        });
                }
        }
        public void handlerSpElRCE(String address,String vpsIP,String vpsPORT) throws IOException {
                consoleOutput.getChildren().clear();
                // 暂无证书模块，待设置
                if (address.isEmpty()){
                        showAlertEmpty("地址为空！");
                }else {
                        if (address.endsWith("/")) {
                                address = address.replaceAll("/$", "");
                        }
                        if (!address.startsWith("http://") && !address.startsWith("https://")) {
                                address = "http://" + address;
                        }
                        SpelRCE sp = new SpelRCE(address,vpsIP,vpsPORT);
                        if (vpsIP.isEmpty() && vpsPORT.isEmpty()){
                                sp.Poc(new ResultCallback() {
                                        @Override
                                        public void onResult(String result) {
                                                Platform.runLater(() -> {
                                                        // 输出其他内容到控制台
                                                        Text text = new Text(result + "\n");
                                                        consoleOutput.getChildren().add(text);
                                                        // 自动滚动到最新内容
                                                        scrollPane.setVvalue(1.0);
                                                });
                                        }

                                        @Override
                                        public void onComplete() {
                                        }
                                });
                        }else {
                                sp.Exp(new ResultCallback() {
                                        @Override
                                        public void onResult(String result) {
                                                Platform.runLater(() -> {
                                                        // 输出其他内容到控制台
                                                        Text text = new Text(result + "\n");
                                                        consoleOutput.getChildren().add(text);
                                                        // 自动滚动到最新内容
                                                        scrollPane.setVvalue(1.0);
                                                });
                                        }

                                        @Override
                                        public void onComplete() {
                                        }
                                });
                        }

                }
        }
        public void handlerEurekaXstreamRCE(String address,String vpsIP,String vpsPORT) throws IOException {
                // 清空控制台输出
                consoleOutput.getChildren().clear();
                // 暂无证书模块，待设置
                if (address.isEmpty()){
                        showAlertEmpty("地址为空！");
                }else {
                        if (address.endsWith("/")) {
                                address = address.replaceAll("/$", "");
                        }
                        if (!address.startsWith("http://") && !address.startsWith("https://")) {
                                address = "http://" + address;
                        }
                        if (vpsIP.isEmpty() && vpsPORT.isEmpty()){
                                showAlertEmpty("反弹vps的IP和端口为空！");
                        }else {
                                EurekaXsRCE ekx = new EurekaXsRCE(address, vpsIP, vpsPORT);
                                ekx.Exp(new ResultCallback() {
                                        @Override
                                        public void onResult(String result) {
                                                Platform.runLater(() -> {
                                                        // 输出其他内容到控制台
                                                        Text text = new Text(result + "\n");
                                                        consoleOutput.getChildren().add(text);
                                                        // 自动滚动到最新内容
                                                        scrollPane.setVvalue(1.0);
                                                });
                                        }

                                        @Override
                                        public void onComplete() {
                                        }
                                });
                        }

                }
        }
        public void handlerJolokiaLogbackRCE(String address,String vpsIP,String vpsPORT){
                consoleOutput.getChildren().clear();
                // 暂无证书模块，待设置
                if (address.isEmpty()){
                        showAlertEmpty("地址为空！");
                }else {
                        if (address.endsWith("/")) {
                                address = address.replaceAll("/$", "");
                        }
                        if (!address.startsWith("http://") && !address.startsWith("https://")) {
                                address = "http://" + address;
                        }
                        if (vpsIP.isEmpty() && vpsPORT.isEmpty()) {
                                showAlertEmpty("反弹vps的IP和端口为空！");
                        } else {
                                JolokiaLogbackRCE jlb = new JolokiaLogbackRCE(address,vpsIP,vpsPORT);
                                jlb.Exp(new ResultCallback() {
                                        @Override
                                        public void onResult(String result) {
                                                Platform.runLater(() -> {
                                                        // 输出其他内容到控制台
                                                        Text text = new Text(result + "\n");
                                                        consoleOutput.getChildren().add(text);
                                                        // 自动滚动到最新内容
                                                        scrollPane.setVvalue(1.0);
                                                });
                                        }

                                        @Override
                                        public void onComplete() {
                                        }
                                });
                        }
                }
        }
        public void handlerJolokiaRealmRCE(String address,String vpsIP,String vpsPORT){
                consoleOutput.getChildren().clear();
                // 暂无证书模块，待设置
                if (address.isEmpty()){
                        showAlertEmpty("地址为空！");
                }else {
                        if (address.endsWith("/")) {
                                address = address.replaceAll("/$", "");
                        }
                        if (!address.startsWith("http://") && !address.startsWith("https://")) {
                                address = "http://" + address;
                        }
                        JolokiaRealmRCE jrl = new JolokiaRealmRCE(address,vpsIP,vpsPORT);
                        jrl.Exp(new ResultCallback() {
                                @Override
                                public void onResult(String result) {
                                        Platform.runLater(() -> {
                                                // 输出其他内容到控制台
                                                Text text = new Text(result + "\n");
                                                consoleOutput.getChildren().add(text);
                                                // 自动滚动到最新内容
                                                scrollPane.setVvalue(1.0);
                                        });
                                }

                                @Override
                                public void onComplete() {
                                }
                        });
                }
        }

        public void handlerH2DatabaseQueryRCE(String address,String vpsIP,String vpsPORT){
                consoleOutput.getChildren().clear();
                // 暂无证书模块，待设置
                if (address.isEmpty()){
                        showAlertEmpty("地址为空！");
                }else {
                        if (address.endsWith("/")) {
                                address = address.replaceAll("/$", "");
                        }
                        if (!address.startsWith("http://") && !address.startsWith("https://")) {
                                address = "http://" + address;
                        }
                        if (vpsIP.isEmpty() && vpsPORT.isEmpty()) {
                                showAlertEmpty("反弹vps的IP和端口为空！");
                        } else {
                                H2DataQueryRCE hq = new H2DataQueryRCE(address,vpsIP,vpsPORT);
                                hq.Exp(new ResultCallback() {
                                        @Override
                                        public void onResult(String result) {
                                                Platform.runLater(() -> {
                                                        // 输出其他内容到控制台
                                                        Text text = new Text(result + "\n");
                                                        consoleOutput.getChildren().add(text);
                                                        // 自动滚动到最新内容
                                                        scrollPane.setVvalue(1.0);
                                                });
                                        }

                                        @Override
                                        public void onComplete() {
                                        }
                                });
                        }
                }
        }
        public void handlerH2DatabaseJNDIRCE(String address,String vpsIP,String vpsPORT){
                consoleOutput.getChildren().clear();
                if (address.isEmpty()){
                        showAlertEmpty("地址为空！");
                }else {
                        if (address.endsWith("/")) {
                                address = address.replaceAll("/$", "");
                        }
                        if (!address.startsWith("http://") && !address.startsWith("https://")) {
                                address = "http://" + address;
                        }
                        if (vpsIP.isEmpty() && vpsPORT.isEmpty()) {
                                showAlertEmpty("反弹vps的IP和端口为空！");
                        } else {
                                H2DataConsoleRCE hc = new H2DataConsoleRCE(address,vpsIP,vpsPORT);
                                hc.Exp(new ResultCallback() {
                                        @Override
                                        public void onResult(String result) {
                                                Platform.runLater(() -> {
                                                        // 输出其他内容到控制台
                                                        Text text = new Text(result + "\n");
                                                        consoleOutput.getChildren().add(text);
                                                        // 自动滚动到最新内容
                                                        scrollPane.setVvalue(1.0);
                                                });
                                        }
                                        @Override
                                        public void onComplete() {
                                        }
                                });
                        }
                }
        }
        public void handlerMysqlJdbcRCE(String address,String command){

        }
        public void handlerLoggingLogbackRCE(String address,String vpsIP,String vpsPORT){
                consoleOutput.getChildren().clear();
                if (address.isEmpty()){
                        showAlertEmpty("地址为空！");
                }else {
                        if (address.endsWith("/")) {
                                address = address.replaceAll("/$", "");
                        }
                        if (!address.startsWith("http://") && !address.startsWith("https://")) {
                                address = "http://" + address;
                        }
                        if (vpsIP.isEmpty() && vpsPORT.isEmpty()) {
                                showAlertEmpty("反弹vps的IP和端口为空！");
                        } else {
                                LoggingConfigRCE lr = new LoggingConfigRCE(address,vpsIP,vpsPORT);
                                lr.Exp(new ResultCallback() {
                                        @Override
                                        public void onResult(String result) {
                                                Platform.runLater(() -> {
                                                        // 输出其他内容到控制台
                                                        Text text = new Text(result + "\n");
                                                        consoleOutput.getChildren().add(text);
                                                        // 自动滚动到最新内容
                                                        scrollPane.setVvalue(1.0);
                                                });
                                        }

                                        @Override
                                        public void onComplete() {
                                        }
                                });
                        }
                }

        }
        public void handlerLoggingGroovyRCE(String address,String vpsIP,String vpsPORT){
                consoleOutput.getChildren().clear();
                if (address.isEmpty()){
                        showAlertEmpty("地址为空！");
                }else {
                        if (address.endsWith("/")) {
                                address = address.replaceAll("/$", "");
                        }
                        if (!address.startsWith("http://") && !address.startsWith("https://")) {
                                address = "http://" + address;
                        }
                        if (vpsIP.isEmpty() && vpsPORT.isEmpty()) {
                                showAlertEmpty("反弹vps的IP和端口为空！");
                        } else {
                                LoggingGroovyRCE lr = new LoggingGroovyRCE(address,vpsIP,vpsPORT);
                                lr.Exp(new ResultCallback() {
                                        @Override
                                        public void onResult(String result) {
                                                Platform.runLater(() -> {
                                                        // 输出其他内容到控制台
                                                        Text text = new Text(result + "\n");
                                                        consoleOutput.getChildren().add(text);
                                                        // 自动滚动到最新内容
                                                        scrollPane.setVvalue(1.0);
                                                });
                                        }

                                        @Override
                                        public void onComplete() {
                                        }
                                });
                        }
                }
        }
        public void handlerMainSourceGroovyRCE(String address,String vpsIP,String vpsPORT){
                consoleOutput.getChildren().clear();
                if (address.isEmpty()){
                        showAlertEmpty("地址为空！");
                }else {
                        if (address.endsWith("/")) {
                                address = address.replaceAll("/$", "");
                        }
                        if (!address.startsWith("http://") && !address.startsWith("https://")) {
                                address = "http://" + address;
                        }
                        if (vpsIP.isEmpty() && vpsPORT.isEmpty()) {
                                showAlertEmpty("反弹vps的IP和端口为空！");
                        } else {
                                MainSourceGroovyRCE mr = new MainSourceGroovyRCE(address,vpsIP,vpsPORT);
                                mr.Exp(new ResultCallback() {
                                        @Override
                                        public void onResult(String result) {
                                                Platform.runLater(() -> {
                                                        // 输出其他内容到控制台
                                                        Text text = new Text(result + "\n");
                                                        consoleOutput.getChildren().add(text);
                                                        // 自动滚动到最新内容
                                                        scrollPane.setVvalue(1.0);
                                                });
                                        }

                                        @Override
                                        public void onComplete() {
                                        }
                                });
                        }
                }
        }
        public void handlerH2DatabaseDatasourceRCE(String address,String vpsIP,String vpsPORT){
                // 清空控制台输出
                consoleOutput.getChildren().clear();
                // 暂无证书模块，待设置
                if (address.isEmpty()){
                        showAlertEmpty("地址为空！");
                }else {
                        if (address.endsWith("/")) {
                                address = address.replaceAll("/$", "");
                        }
                        if (!address.startsWith("http://") && !address.startsWith("https://")) {
                                address = "http://" + address;
                        }
                        if (vpsIP.isEmpty() && vpsPORT.isEmpty()) {
                                showAlertEmpty("反弹vps的IP和端口为空！");
                        } else {
                                H2DataSourceRCE hd = new H2DataSourceRCE(address,vpsIP,vpsPORT);
                                hd.Exp(new ResultCallback() {
                                        @Override
                                        public void onResult(String result) {
                                                Platform.runLater(() -> {
                                                        // 输出其他内容到控制台
                                                        Text text = new Text(result + "\n");
                                                        consoleOutput.getChildren().add(text);
                                                        // 自动滚动到最新内容
                                                        scrollPane.setVvalue(1.0);
                                                });
                                        }

                                        @Override
                                        public void onComplete() {
                                        }
                                });
                        }
                }
        }
        public void handlerDruidBruteForce(String address){
                consoleOutput.getChildren().clear();
                // 暂无证书模块，待设置
                if (address.isEmpty()){
                        showAlertEmpty("地址为空！");
                }else {
                        if (address.endsWith("/")) {
                                address = address.replaceAll("/$", "");
                        }
                        if (!address.startsWith("http://") && !address.startsWith("https://")) {
                                address = "http://" + address;
                        }
                        consoleOutput.getChildren().addAll(new Text("Druid爆破中，请勿关闭！\n"));
                        DruidBruteForce bf = new DruidBruteForce(address);
                        executorService.submit(() -> {
                                try {
                                        Stream<String> result = bf.BruteDruid();
                                        result.forEach(line -> {
                                                Platform.runLater(() -> {
                                                        Text text = new Text(line + "\n");
                                                        consoleOutput.getChildren().add(text);
                                                        // 自动滚动到最新内容
                                                        scrollPane.setVvalue(1.0);
                                                });
                                        });
                                } catch (IOException | InterruptedException e) {
                                        e.printStackTrace();
                                        Platform.runLater(() -> {
                                                Text errorText = new Text("发生错误，请检查与服务器的连接！\n");
                                                consoleOutput.getChildren().add(errorText);
                                        });
                                }
                        });
                }
        }
        public void handlerLogViewFile(String address,String filename){
                consoleOutput.getChildren().clear();
                // 暂无证书模块，待设置
                if (address.isEmpty()){
                        showAlertEmpty("地址为空！");
                }else {
                        if (address.endsWith("/")) {
                                address = address.replaceAll("/$", "");
                        }
                        if (!address.startsWith("http://") && !address.startsWith("https://")) {
                                address = "http://" + address;
                        }
                        LogViewFileLeak lf = new LogViewFileLeak(address,filename);
                        lf.Exp(new ResultCallback() {
                                @Override
                                public void onResult(String result) {
                                        Platform.runLater(() -> {
                                                // 输出其他内容到控制台
                                                Text text = new Text(result + "\n");
                                                consoleOutput.getChildren().add(text);
                                                // 自动滚动到最新内容
                                                scrollPane.setVvalue(1.0);
                                        });
                                }

                                @Override
                                public void onComplete() {
                                }
                        });
                }
        }
        public void handleAllVulnerabilities(String address,String command){

        }
        public void handlerGetshell(TextField addr,TextField vpsobj,TextField portobj) throws IOException {
                consoleOutput.getChildren().clear();
                // 一键上内存马
                String address = addr.getText();
                String command = "";
                String vpsIP = vpsobj.getText();
                String vpsPort = portobj.getText();
                ExpImp exp = new ExpImp(address);
                if (address.isEmpty()){
                        showAlertEmpty("地址为空！");
                }else{
                        if (address.endsWith("/")) {
                                address = address.replaceAll("/$", "");
                        }
                        if (!address.startsWith("http://") && !address.startsWith("https://")) {
                                address = "http://" + address;
                        }
                        switch (Vulvalue){
                                case -1:
                                        showAlertEmpty("你踏马还没选择漏洞类型呢！");
                                case 5:
                                        handlerSnakeYamlRce(address,vpsIP);
                                        break;
                                case 6:
                                        exp.handlerSpgRCE(address);
                                        break;
                                case 7:
                                        handlerSpElRCE(address,vpsIP,vpsPort);
                                        break;
                                case 8:
                                        handlerEurekaXstreamRCE(address, vpsIP, vpsPort);
                                        break;
                                case 9:
                                        handlerJolokiaLogbackRCE(address,vpsIP,vpsPort);
                                        break;
                                case 10:
                                        handlerJolokiaRealmRCE(address,vpsIP,vpsPort);
                                        break;
                                case 11:
                                        handlerH2DatabaseQueryRCE(address,vpsIP,vpsPort);
                                        break;
                                case 12:
                                        handlerH2DatabaseJNDIRCE(address,vpsIP,vpsPort);
                                        break;
                                case 13:
                                        handlerMysqlJdbcRCE(address, command);
                                        break;
                                case 14:
                                        handlerLoggingLogbackRCE(address,vpsIP,vpsPort);
                                        break;
                                case 15:
                                        handlerLoggingGroovyRCE(address,vpsIP,vpsPort);
                                        break;
                                case 16:
                                        handlerMainSourceGroovyRCE(address,vpsIP,vpsPort);
                                        break;
                                case 17:
                                        handlerH2DatabaseDatasourceRCE(address, vpsIP, vpsPort);
                                        break;
                                case 18:
                                        handlerDruidBruteForce(address);
                                        break;
                                case 19:
                                        handleAllVulnerabilities(address, command);
                                        break;
                                default:
                                        showAlertEmpty("你踏马还没选择漏洞类型呢！");
                                        break;
                        }
                }

        }
        public void handlerExp(TextField addr,TextField args,TextField cmdobj,TextField vpsobj,TextField portobj) throws IOException {
                boolean isPoc = false;
                String address = addr.getText();
                String arg = args.getText();
                String command = cmdobj.getText();
                String vpsIP = vpsobj.getText();
                String vpsPort = portobj.getText();
                String filename = "";
                if (address.isEmpty()) {
                        showAlertEmpty("地址为空！");
                } else {
                        switch (Vulvalue) {
                                case -1:
                                        showAlertEmpty("你踏马还没选择漏洞类型呢！");
                                        break;
                                case 0:
                                        handlerAllScan(address);
                                        break;
                                case 1:
                                        handlerScanVul(address);
                                        break;
                                case 2:
                                        handlerGetSp_1(address, arg);
                                        break;
                                case 3:
                                        handlerGetSp_2(address, arg, vpsIP, vpsPort);
                                        break;
                                case 4:
                                        handlerGetSp_3(address, arg, vpsIP, vpsPort);
                                        break;
                                case 5:
                                        showAlertEmpty("暂时没有写这里，直接getshell");
//                                        handlerSnakeYamlRce(address, vpsIP);
                                        break;
                                case 6:
                                        handlerSpgRCE(address, command);
                                        break;
                                case 7:
                                        handlerSpElRCE(address,vpsIP,vpsPort);
                                        break;
                                case 8:
//                                        handlerEurekaXstreamRCE(address, vpsIP, vpsPort);
                                        showAlertEmpty("暂时没有写这里，直接getshell");
                                        break;
                                case 9:
//                                        handlerJolokiaLogbackRCE(address, command);
                                        showAlertEmpty("暂时没有写这里，直接getshell");
                                        break;
                                case 10:
//                                        handlerJolokiaRealmRCE(address, command);
                                        showAlertEmpty("暂时没有写这里，直接getshell");
                                        break;
                                case 11:
//                                        handlerH2DatabaseQueryRCE(address, command);
                                        showAlertEmpty("你踏马还没选择漏洞类型呢！");
                                        break;
                                case 12:
                                        showAlertEmpty("暂时没有写这里，直接getshell");
//                                        handlerH2DatabaseJNDIRCE(address, command);
                                        break;
                                case 13:
//                                        handlerMysqlJdbcRCE(address, command);
                                        showAlertEmpty("暂时没有写这里，直接getshell");
                                        break;
                                case 14:
//                                        handlerLoggingLogbackRCE(address,vpsIP,vpsPort);
                                        showAlertEmpty("暂时没有写这里，直接getshell");
                                        break;
                                case 15:
                                        showAlertEmpty("暂时没有写这里，直接getshell");
                                        break;
                                case 16:
                                        showAlertEmpty("暂时没有写这里，直接getshell");
                                        break;
                                case 17:
                                        showAlertEmpty("暂时没有写这里，直接getshell");
//                                        handlerH2DatabaseDatasourceRCE(address, vpsIP, vpsPort);
                                        break;
                                case 18:
                                        handlerDruidBruteForce(address);
                                        break;
                                case 19:
                                        handlerLogViewFile(address,filename);
                                        break;
                                default:
                                        showAlertEmpty("你踏马还没选择漏洞类型呢！");
                                        break;
                        }
                }
        }

        public void handlerDelData(TextField addr) throws IOException{
                // 痕迹清除
                String address = addr.getText();
                if (address.isEmpty()) {
                        showAlertEmpty("地址为空！");
                } else {
                        switch (Vulvalue) {
                                case 6:
                                        handlerDelSpg(address);
                                        break;
                                default:
                                        showAlertEmpty("目前只限于spring cloud gateway 漏洞");
                                        break;
                        }
                }
        }

        private void applyProxy(String ip, String port) {
                System.setProperty("http.proxyHost", ip);
                System.setProperty("http.proxyPort", port);
                System.setProperty("https.proxyHost", ip);
                System.setProperty("https.proxyPort", port);
        }
        public void showAlertEmpty(String text) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.initStyle(StageStyle.UNDECORATED);
                alert.setContentText(text);
                alert.showAndWait();
        }

        public static void main(String[] args) {
                launch(args);
        }
        @Override
        public void stop() {
                if (executorService != null) {
                        executorService.shutdownNow();
                }
                Platform.exit();
        }
}