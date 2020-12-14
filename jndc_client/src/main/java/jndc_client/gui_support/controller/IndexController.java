package jndc_client.gui_support.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialogLayout;
import com.jfoenix.controls.JFXListView;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.util.Callback;
import jndc_client.gui_support.utils.ContextMenuBuilder;
import jndc_client.gui_support.utils.RowPackaging;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class IndexController implements Initializable {


    @FXML
    private AnchorPane anchorPane;


    @FXML
    JFXListView<RowPackaging> jfxListView;

    @FXML
    public void addService(ActionEvent actionEvent) {
        JFXDialogLayout content = new JFXDialogLayout();
        content.setHeading(new Text("Error, No selection"));
        content.setBody(new Text("No student selected"));
        StackPane stackPane = new StackPane();
        stackPane.setLayoutX(500);
        stackPane.setLayoutY(150);
        JFXDialog dialog = new JFXDialog(stackPane, content, JFXDialog.DialogTransition.CENTER);

        List<JFXButton> list = new ArrayList<>();
        JFXButton button = new JFXButton("Okay");
        button.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                dialog.close();
            }
        });
        list.add(new JFXButton("确认"));
        list.add(new JFXButton("取消"));
        content.setActions(list);
        anchorPane.getChildren().add(stackPane);
        dialog.show();


    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        jfxListView.setOnMouseClicked(x -> {
            RowPackaging selectedItem = jfxListView.getSelectionModel().getSelectedItem();

            System.out.println(selectedItem);

        });

        ContextMenuBuilder.InnerButtonDescription start = new ContextMenuBuilder.InnerButtonDescription(" 启 动 ", x -> {
            System.out.println("启动" + x);
        });
        ContextMenuBuilder.InnerButtonDescription delete = new ContextMenuBuilder.InnerButtonDescription(" 删 除 ", x -> {
            System.out.println("删除" + x);
        });

        ContextMenuBuilder.InnerButtonDescription pause = new ContextMenuBuilder.InnerButtonDescription(" 暂 停 ", x -> {
            System.out.println("暂停" + x);
        });
        Callback remove = ContextMenuBuilder.multipleButton(start, pause, delete);

        jfxListView.setCellFactory(remove);
        ObservableList<RowPackaging> items = jfxListView.getItems();



        for (int i = 0; i < 30; i++) {
            items.add(new RowPackaging("item"+ i) );
        }

    }
}
