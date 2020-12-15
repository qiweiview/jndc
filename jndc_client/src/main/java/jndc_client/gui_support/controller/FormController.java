package jndc_client.gui_support.controller;

import com.jfoenix.controls.JFXTextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.TextFormatter;
import javafx.util.converter.NumberStringConverter;
import jndc.core.TcpServiceDescription;
import jndc_client.core.ClientServiceDescription;
import jndc_client.gui_support.utils.AlertUtils;
import jndc_client.gui_support.utils.DialogClose;
import jndc_client.gui_support.utils.MenuItemPackaging;
import jndc_client.gui_support.utils.MenuItemPackagingListStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class FormController implements Initializable {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @FXML
    private JFXTextField ip_1;

    @FXML
    private JFXTextField ip_2;

    @FXML
    private JFXTextField ip_3;

    @FXML
    private JFXTextField ip_4;

    @FXML
    private JFXTextField port;

    @FXML
    private JFXTextField serviceName;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ip_1.setTextFormatter(new TextFormatter<>(new NumberStringConverter()));
        ip_2.setTextFormatter(new TextFormatter<>(new NumberStringConverter()));
        ip_3.setTextFormatter(new TextFormatter<>(new NumberStringConverter()));
        ip_4.setTextFormatter(new TextFormatter<>(new NumberStringConverter()));
        port.setTextFormatter(new TextFormatter<>(new NumberStringConverter()));
    }


    @FXML
    public void addService(ActionEvent actionEvent) {
        boolean checkBlank = checkBlank(ip_1.getText(), ip_2.getText(), ip_3.getText(), ip_4.getText(), port.getText(), serviceName.getText());
        if (checkBlank) {
            String ip = ip_1.getText() + "." + ip_2.getText() + "." + ip_3.getText() + "." + ip_4.getText();
            ClientServiceDescription service = new ClientServiceDescription();
            service.setServicePort(Integer.parseInt(port.getText()));
            service.setServiceIp(ip);
            service.setServiceName(serviceName.getText());
            service.performParameterVerification();
            service.setServiceEnable(false);
            MenuItemPackagingListStore.addItem(new MenuItemPackaging(service.getServiceName()+"("+service.getServiceIp()+":"+service.getServicePort()+")",service));
            DialogClose.close();
        } else {
            AlertUtils.error("unSupport null or blank");
            logger.error("unSupport null or blank");
        }

    }

    private boolean checkBlank(Object... objects) {
        for (int i = 0; i < objects.length; i++) {
            if (objects[i] == null||"".equals(objects[i] )) {
                return false;
            }
        }
        return true;
    }

    @FXML
    public void doClear(ActionEvent actionEvent) {
        ip_1.setText(null);
        ip_2.setText(null);
        ip_3.setText(null);
        ip_4.setText(null);
        port.setText(null);
        serviceName.setText(null);
    }
}
