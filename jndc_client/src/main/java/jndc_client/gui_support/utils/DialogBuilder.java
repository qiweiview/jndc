package jndc_client.gui_support.utils;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialogLayout;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import jndc_client.gui_support.GuiStart;

import java.io.IOException;
import java.net.URL;


public class DialogBuilder {
    private static final int LAYOUT_X = 300;
    private static final int LAYOUT_y = 50;

    public static void openTemplateDialog(Pane rootPane, String template, InnerAutoCloseButton... innerAutoCloseButtons) {

        try {
            URL resource = GuiStart.class.getResource(template);
            Parent parent = FXMLLoader.load(resource);
            openAutoCloseDialog(rootPane, parent, innerAutoCloseButtons);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void openMessageDialog(Pane rootPane, String message, InnerAutoCloseButton... innerAutoCloseButtons) {
        openAutoCloseDialog(rootPane, new Text(message), innerAutoCloseButtons);
    }

    private static void openAutoCloseDialog(Pane rootPane, Node body, InnerAutoCloseButton... innerAutoCloseButtons) {
        ObservableList<Node> children = rootPane.getChildren();

        StackPane stackPane = new StackPane();

        JFXDialogLayout content = new JFXDialogLayout();
        content.setHeading(new Text("提示"));
        content.setBody(body);

        stackPane.setLayoutX(LAYOUT_X);
        stackPane.setLayoutY(LAYOUT_y);
        JFXDialog dialog = new JFXDialog(stackPane, content, JFXDialog.DialogTransition.CENTER);


        JFXButton[] na = new JFXButton[innerAutoCloseButtons.length + 1];

        EventHandler<ActionEvent> tConsumer = event -> {
            dialog.close();
            children.remove(stackPane);
        };

        DialogClose.tConsumer = tConsumer;

        JFXButton defaultClose = new JFXButton("关闭");
        defaultClose.setMaxWidth(60);
        defaultClose.getStyleClass().add("button-raised-info");


        defaultClose.setOnAction(tConsumer);
        na[innerAutoCloseButtons.length] = defaultClose;


        for (int i = 0; i < innerAutoCloseButtons.length; i++) {
            InnerAutoCloseButton innerAutoCloseButton = innerAutoCloseButtons[i];
            innerAutoCloseButton.setCloseAction(tConsumer);
            na[i] = innerAutoCloseButton.toJFXButton();
        }

        content.setActions(na);
        dialog.show();
        children.add(stackPane);
    }


    public static class InnerAutoCloseButton {

        public static  final int SUCCESS_TYPE = 1;
        public static  final int INFO_TYPE = 2;
        public static  final int DANGER_TYPE = 0;

        private int buttonType = DANGER_TYPE;//0 danger 1success 2info
        private String buttonName;
        private EventHandler<ActionEvent> buttonAction;
        private EventHandler<ActionEvent> closeAction;

        public JFXButton toJFXButton() {
            JFXButton jfxButton = new JFXButton(buttonName);
            jfxButton.setMaxWidth(60);
            if (SUCCESS_TYPE == buttonType) {
                jfxButton.getStyleClass().add("button-raised-success");
            } else if (INFO_TYPE == buttonType) {
                jfxButton.getStyleClass().add("button-raised-info");
            } else {
                jfxButton.getStyleClass().add("button-raised-danger");
            }


            EventHandler<ActionEvent> action = (x) -> {
                if (buttonAction != null) {
                    buttonAction.handle(x);
                }
                if (closeAction != null) {
                    closeAction.handle(x);
                }
            };
            jfxButton.setOnAction(action);
            return jfxButton;
        }

        public InnerAutoCloseButton(String buttonName, EventHandler<ActionEvent> buttonAction) {
            this.buttonName = buttonName;
            this.buttonAction = buttonAction;
        }

        public void setButtonType(int buttonType) {
            this.buttonType = buttonType;
        }

        public void setCloseAction(EventHandler<ActionEvent> closeAction) {
            this.closeAction = closeAction;
        }
    }
}
