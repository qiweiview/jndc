package jndc_client.gui_support.controller;

import ch.qos.logback.classic.spi.ILoggingEvent;
import com.jfoenix.controls.*;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.util.Callback;
import jndc.core.UniqueBeanManage;
import jndc_client.core.ClientServiceDescription;
import jndc_client.core.JNDCClientConfig;
import jndc_client.gui_support.utils.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.LinkedBlockingQueue;

public class IndexController implements Initializable {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @FXML
    private JFXTextArea logArea;

    @FXML
    private Label chooseServiceIp;

    @FXML
    private Label chooseServicePort;

    @FXML
    private Label chooseServiceName;


    @FXML
    private Label currentJNDCServer;

    @FXML
    private AnchorPane anchorPane;


    @FXML
    JFXListView<MenuItemPackaging> jfxListView;


    public IndexController() {

    }


    @FXML
    public void moreAboutJNDC(ActionEvent actionEvent) {
        try {
            Desktop.getDesktop().browse(new URI("https://github.com/qiweiview/jndc"));
        } catch (Exception e) {
          logger.error(e.getMessage());
        }
    }


    @FXML
    public void clearConsoleLog(ActionEvent actionEvent) {
        logArea.setText(null);
    }

    @FXML
    public void addService(ActionEvent actionEvent) {
        DialogBuilder.openTemplateDialog(anchorPane, "addForm.fxml");
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        new Thread(() -> {
            LinkedBlockingQueue<ILoggingEvent> deathQueue = GuiLogAppender.deathQueue;
            while (true) {
                try {
                    ILoggingEvent take = deathQueue.take();
                    String text = logArea.getText();
                    logArea.setText("[" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + "]" + take + "\n\r" + text);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        jfxListView.setOnMouseClicked(x -> {
            MenuItemPackaging<ClientServiceDescription> selectedItem = jfxListView.getSelectionModel().getSelectedItem();
            if (selectedItem != null) {
                ClientServiceDescription value = selectedItem.getValue();

                chooseServiceIp.setText(value.getServiceIp());
                chooseServicePort.setText(value.getServicePort() + "");
                chooseServiceName.setText(value.getServiceName());

            }

        });

        ContextMenuOnListViewBuilder.InnerButtonDescription start = new ContextMenuOnListViewBuilder.InnerButtonDescription(" 启 用 ", x -> {
            ClientServiceDescription clientServiceDescription = (ClientServiceDescription) x.getValue();

            if (clientServiceDescription.isServiceEnable()){
                AlertUtils.error("服务已经启用");
                return;
            }

            //about gui
            EventHandler<ActionEvent> startCallBack = z -> {
                clientServiceDescription.setServiceEnable(true);
                MenuItemPackagingListStore.reloadItem();
            };

            //about jndc_client
            DialogBuilder.InnerAutoCloseButton startB = new DialogBuilder.InnerAutoCloseButton("启动", startCallBack);
            startB.setButtonType(DialogBuilder.InnerAutoCloseButton.SUCCESS_TYPE);
            DialogBuilder.openMessageDialog(anchorPane, "启动后客户端将自动向服务端注册服务",startB );
        });


        ContextMenuOnListViewBuilder.InnerButtonDescription delete = new ContextMenuOnListViewBuilder.InnerButtonDescription(" 删 除 ", x -> {
            ClientServiceDescription clientServiceDescription = (ClientServiceDescription) x.getValue();



            //----------------- about gui -----------------
            EventHandler<ActionEvent> del = z -> {
                MenuItemPackagingListStore.deleteItem(x);
            };

            DialogBuilder.openMessageDialog(anchorPane, "服务将从列表中移除，已建立连接也将中断", new DialogBuilder.InnerAutoCloseButton("移除", del));

            //----------------- about jndc_client -----------------
            boolean serviceEnable = clientServiceDescription.isServiceEnable();
            if (serviceEnable){
                //todo stop client service


            }else {
                //todo do nothing because the service has not been used


            }
        });

        ContextMenuOnListViewBuilder.InnerButtonDescription pause = new ContextMenuOnListViewBuilder.InnerButtonDescription(" 停 用 ", x -> {
            ClientServiceDescription clientServiceDescription = (ClientServiceDescription) x.getValue();

            if (!clientServiceDescription.isServiceEnable()){
                AlertUtils.error("服务未启用");
                return;
            }

            //----------------- about gui -----------------
            EventHandler<ActionEvent> pauseCallBack = z -> {
                clientServiceDescription.setServiceEnable(false);
                MenuItemPackagingListStore.reloadItem();
            };
            DialogBuilder.openMessageDialog(anchorPane, "确认暂停服务 \"" + x + "\" ？ 暂停后，已建立连接也将中断", new DialogBuilder.InnerAutoCloseButton("暂停", pauseCallBack));


            //----------------- about jndc_client -----------------

        });


        //create cell factory
        Callback remove = ContextMenuOnListViewBuilder.multipleButton(start, pause, delete);
        jfxListView.setCellFactory(remove);

        ObservableList<MenuItemPackaging> items = jfxListView.getItems();
        MenuItemPackagingListStore.register(items);

        JNDCClientConfig clientConfig = UniqueBeanManage.getBean(JNDCClientConfig.class);

        currentJNDCServer.setText(clientConfig.getServerIp() + ":" + clientConfig.getServerPort());

        List<ClientServiceDescription> clientServiceDescriptions = clientConfig.getClientServiceDescriptions();

        List<MenuItemPackaging> menuItemPackagings = new ArrayList<>();
        clientServiceDescriptions.forEach(x -> {
            menuItemPackagings.add(new MenuItemPackaging(x.getServiceName() + "(" + x.getServiceIp() + ":" + x.getServicePort() + ")", x));
        });

        MenuItemPackagingListStore.addItems(menuItemPackagings);


    }
}
