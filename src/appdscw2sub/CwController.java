package appdscw2sub;
//IDEA IS TO ALLOW USERS TO FILTER SALES BY REGION, VEHICLE AND YR

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

public class CwController implements Initializable {

    private static String markup;
    private static List<Sales> sales;
    private static List<String> regions;
    private static List<Integer> years;
    private static List<String> vehicles;
    private static List<Integer> qtrs;

    private String sRegion, sVe;
    private Integer sYr, selQ;

    private BackUp backUp;
    private webService webService;

    @FXML
    private AnchorPane AnchorPane1, loadingScreen;

//BAR, LINE AND PIE SCENES
    @FXML
    private CategoryAxis xAxis;
    @FXML
    private NumberAxis yAxis;
    @FXML
    private BarChart<String, Number> bChart;
    @FXML
    private PieChart pChart1;
    @FXML
    private LineChart<String, Number> lineChart;

//table
    @FXML
    private TableColumn<Sales, String> LT1, BT1;
    @FXML
    private TableColumn<Sales, Integer> LT2, LT3, BT2;
    @FXML
    private TableView<Sales> lineTable, barTable;

//ui
    @FXML
    private ChoiceBox<String> veh, regi;
    @FXML
    private ChoiceBox<Integer> yea, sQtr;
    @FXML
    private Button resetF, appFil;
    @FXML
    private Label dateL, statusL;
    @FXML
    private VBox mainLayer;
    @FXML
    private ProgressBar progressData;
    @FXML
    private CheckBox enableQTR, vCom;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        backUp = new BackUp("SGetSales.JSON");

        webService = new webService(statusL);
        webService.setAddress("https://webteach.ljmu.ac.uk/DashService/SGetSales");

        loadingScreen.visibleProperty().bind(progressData.progressProperty().lessThan(1));
        mainLayer.visibleProperty().bind(progressData.progressProperty().isEqualTo(1));
        progressData.progressProperty().bind(webService.progressProperty());

        webService.setOnSucceeded((WorkerStateEvent e) -> {
            markup = e.getSource().getValue().toString();
            backUp.backUp(markup);
            loadFeatures();

        });
        webService.setOnCancelled((WorkerStateEvent e) -> {
            if (backUp.exists()) {
                markup = backUp.restore();
                loadFeatures();

                Alert a = new Alert(AlertType.WARNING);
                a.getDialogPane().getStylesheets().addAll(AnchorPane1.getScene().getStylesheets());
                a.setTitle("chartVis");
                a.setHeaderText("BackUp JSON");
                a.setContentText("Car Sales");
                a.showAndWait();

                loadFeatures();

            } else {
                Alert a = new Alert(AlertType.ERROR);
                a.getDialogPane().getStylesheets().addAll(AnchorPane1.getScene().getStylesheets());
                a.setTitle("chartVis");
                a.setHeaderText("No JSON");
                a.setContentText("Car Sales");
                a.showAndWait();
            }
        });

        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                dateL.setText("Local Time: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm\nEEE dd/MM/yyy QQQ")));
            }
        };
        
        timer.start();

        webService.start();
    }

    private void dCerealise() {
        sales = (new Gson()).fromJson(markup, new TypeToken<LinkedList<Sales>>() {
        }.getType());

        regions = sales.stream().map(r -> r.getRegion()).distinct().collect(Collectors.toList());
        vehicles = sales.stream().map(r -> r.getVehicle()).distinct().collect(Collectors.toList());
        years = sales.stream().map(r -> r.getYear()).distinct().collect(Collectors.toList());
        qtrs = sales.stream().map(r -> r.getQTR()).distinct().collect(Collectors.toList());
    }

    private void regionsChoice() {
        System.out.println(regions);

        regi.getItems().add(null);
        regi.getItems().addAll(FXCollections.observableArrayList(regions));
        regi.setValue(null);

        regi.valueProperty().addListener((observable, oldValue, newValue) -> {
            sRegion = newValue;
        });
    }

    private void vChoice() {
        System.out.println(vehicles);

        veh.getItems().add(null);
        veh.getItems().addAll(FXCollections.observableArrayList(vehicles));
        veh.setValue(null);
        veh.valueProperty().addListener((observable, oldValue, newValue) -> {
            sVe = veh.getValue();

        });

    }

    private void yrChoice() {
        System.out.println(years);

        yea.getItems().add(null);
        yea.getItems().addAll(FXCollections.observableArrayList(years));
        yea.setValue(null);
        yea.valueProperty().addListener((observable, oldValue, newValue) -> {
            sYr = yea.getValue();
        });
    }

    private void qtChoice() {
        System.out.println(qtrs);

        sQtr.getItems().addAll(FXCollections.observableArrayList(qtrs));
        sQtr.setValue(null);
        sQtr.valueProperty().addListener((observable, oldValue, newValue) -> {
            selQ = sQtr.getValue();
        });

    }

    private void consBar() {
        boolean groupIn = enableQTR.isSelected();

        if (groupIn) {
            BT1.setText("Vehicle");
            BT1.setCellValueFactory(cellData -> cellData.getValue().vehicleProp());

        } else {
            BT1.setText("Quarter");
            BT1.setCellValueFactory(cellData -> cellData.getValue().qtrProp().asString());
        }

        BT2.setCellValueFactory(cellData -> cellData.getValue().quantProp().asObject());

        ObservableList<Sales> baDa = FXCollections.observableArrayList();

        bChart.getData().clear();
        baDa.clear();

        sales.stream()
                //default chart is displayed if all are null, applies to all charts
                .filter(bC -> sYr == null || bC.getYear().equals(sYr))
                .filter(bC -> selQ == null || bC.getQTR().equals(selQ))
                .filter(bC -> sRegion == null || bC.getRegion().equals(sRegion))
                //groupBy needs to be a consistent type before conversion back
                .collect(Collectors.groupingBy(bC -> {
                    if (groupIn) {
                        return bC.getVehicle();
                    } else {
                        return String.valueOf(bC.getQTR());
                    }
                }, Collectors.summingInt(Sales::getQuantity)))
                //key-value pair looped dependant on choice, although bars are not aligning with ticks...
                    .forEach((String vehOQtr, Integer quantity) -> {
                        XYChart.Series<String, Number> series = new XYChart.Series<>();
                        series.setName(vehOQtr);
                        series.getData().add(new XYChart.Data<>(vehOQtr, quantity));
                        bChart.getData().add(series);

                        if (groupIn) {
                            //using sales constructor to add to the table view
                            baDa.add(new Sales(0, 0, null, vehOQtr, quantity));
                        } else {
                            baDa.add(new Sales(0, Integer.valueOf(vehOQtr), null, null, quantity));
                        }
                    });

        if (groupIn) {
            xAxis.setLabel("Vehicle");
        } else {
            xAxis.setLabel("Quarter");
        }

        String dynTitle = "";

        dynTitle += "Total Vehicles Sold ";

        if (sRegion != null) {
            dynTitle += "in " + sRegion + " ";
        } else {
            dynTitle += "in All Regions ";
        }

        if (selQ != null) {
            dynTitle += "Q" + selQ + " ";
        } else {
            dynTitle += "by Quarter ";
        }

        if (sYr != null) {
            dynTitle += "(" + sYr + ")";
        } else {
            dynTitle += "(" + years.get(0) + "-" + years.get(years.size() - 1) + ")";
        }

        bChart.setTitle(dynTitle);
        barTable.setItems(baDa);
    }

    private void consPie() {

        pChart1.getData().clear();
        sales.stream()
                .filter(bC -> sYr == null || bC.getYear().equals(sYr))
                .filter(bC -> selQ == null || bC.getQTR().equals(selQ))
                .filter(bC -> sVe == null || bC.getVehicle().equals(sVe))
                .collect(Collectors.groupingBy(pC ->  pC.getRegion()
                , Collectors.summingInt(Sales::getQuantity)))
                
                .forEach((String regOQ, Integer quantity) -> {
                    PieChart.Data pR = new PieChart.Data(String.valueOf(regOQ), quantity);
                    pChart1.getData().add(pR);
                    pR.setName(String.valueOf(regOQ) + " - " + quantity);
                });

        String dynTitle = "Number of ";
        if (sVe != null) {
            dynTitle += sVe + "'s sold ";
        } else {
            dynTitle += "Total Sales ";
        }

        if (selQ != null) {
            dynTitle += "Q" + selQ + ", ";
        } else {
            dynTitle += "by Quarter, ";
        }
        
        dynTitle += "All Regions ";

        if (sYr != null) {
            dynTitle += "(" + sYr + ")";
        } else {
            dynTitle += "(" + years.get(0) + "-" + years.get(years.size() - 1) + ")";
        }

        pChart1.setTitle(dynTitle);
    }

    private void consLine() {
        ObservableList<Sales> liDa = FXCollections.observableArrayList();

        lineChart.getData().clear();
        liDa.clear();
        //default cahrt
        if (sVe == null && selQ == null && sRegion == null) {
            LT1.setText("Vehicle");
            LT1.setCellValueFactory(cellData -> cellData.getValue().vehicleProp());
            LT2.setText("Year");
            LT2.setCellValueFactory(cellData -> cellData.getValue().yrProp().asObject());
            LT3.setCellValueFactory(cellData -> cellData.getValue().quantProp().asObject());
            sales.stream()
                    .collect(Collectors.groupingBy(Sales::getVehicle))
                    .forEach((String vehicle, List<Sales> vs) -> {
                        XYChart.Series<String, Number> lV = new XYChart.Series<>();
                        lV.setName(vehicle);
                        //allow all vehicles to be shown
                        vs.stream()
                                .collect(Collectors.groupingBy(Sales::getYear, Collectors.summingInt(Sales::getQuantity)))
                                .forEach((year, quantity) -> {
                                    lV.getData().add(new XYChart.Data<>(String.valueOf(year), quantity));
                                    liDa.add(new Sales(year, 0, null, vehicle, quantity));

                                });

                        Platform.runLater(() -> {
                            lineChart.setLegendVisible(true);
                            lineChart.getData().add(lV);
                        });
                    });
        } else {
            XYChart.Series<String, Number> lV2 = new XYChart.Series<>();
            sales.stream()
                    .filter(lC -> sVe == null || lC.getVehicle().equals(sVe))
                    .filter(lC -> selQ == null || lC.getQTR().equals(selQ))
                    .filter(lC -> sRegion == null || lC.getRegion().equals(sRegion))
                    .collect(Collectors.groupingBy(Sales::getYear, Collectors.summingInt(Sales::getQuantity)))
                    .forEach((Integer year, Integer quantity) -> {
                        lV2.setName(sVe);
                        lV2.getData().add(new XYChart.Data<>(String.valueOf(year), quantity));
                        liDa.add(new Sales(year, 0, null, sVe, quantity));
                    });
            Platform.runLater(() -> {
                lineChart.getData().add(lV2);
                //since selected years are irrelevant for quarter selection
                lineChart.setLegendVisible(false);
            });

        }

        String dynTitle = "";

        if (sVe != null) {
            dynTitle += sVe + " ";
        } else {
            dynTitle += "All Vehicle ";
        }

        dynTitle += "Sales ";

        if (sRegion != null) {
            dynTitle += "in " + sRegion + " ";
        } else {
            dynTitle += "in All Regions ";
        }

        if (selQ != null) {
            dynTitle += "Q" + selQ + " ";
        } else {
            dynTitle += "All Years";
        }

        dynTitle += "(" + years.get(0) + "-" + years.get(years.size() - 1) + ")";
        lineChart.setTitle(dynTitle);

        Platform.runLater(() -> {
            lineTable.setItems(liDa);
        });
    }

    private void events() {

        appFil.setOnAction(event -> {
            consPie();
            consLine();
            consBar();

        });

        enableQTR.setOnAction(event -> {
            if (enableQTR.isSelected()) {
                sQtr.setDisable(false);
                sQtr.setValue(1);
            } else if (!enableQTR.isSelected()) {
                sQtr.setDisable(true);
                sQtr.setValue(null);
            }
        });

        resetF.setOnAction(event
                -> {
            veh.setValue(null);
            regi.setValue(null);
            yea.setValue(null);
            enableQTR.setSelected(false);
            sQtr.setValue(null);
            sQtr.setDisable(true);

            consPie();
            consLine();
            consBar();
        }
        );

        resetF.disableProperty()
                .bind(
                        Bindings.isNotNull(veh.valueProperty())
                                .or(Bindings.isNotNull(regi.valueProperty()))
                                .or(Bindings.isNotNull(yea.valueProperty()))
                                .or(Bindings.isNotNull(sQtr.valueProperty()))
                                .not()
                );
    }


    private void loadFeatures() {
        dCerealise();
        events();
        regionsChoice();
        vChoice();
        yrChoice();
        qtChoice();
        consBar();
        consPie();
        consLine();
    }

    private static class webService extends Service<String> {

        private final StringProperty address = new SimpleStringProperty();
        private final Label statusL;

        public webService(Label statusL) {
            this.statusL = statusL;
        }

        public final void setAddress(String address) {
            this.address.set(address);
        }

        public final String getAddress() {
            return address.get();
        }

        public final StringProperty addressProperty() {
            return address;
        }

        @Override
        protected Task<String> createTask() {
            return new Task<String>() {
                private HttpURLConnection http;
                private String markup;

                @Override
                protected String call() throws InterruptedException {
                    try {
                        http = (HttpURLConnection) (new URL(getAddress())).openConnection();
                        http.setRequestMethod("GET");
                        http.setRequestProperty("Accept", "application/json");
                        http.setRequestProperty("Content-Type", "application/json");

                        markup = (new BufferedReader(new InputStreamReader(http.getInputStream()))).readLine();

                        for (int i = 1; i <= 100; i++) {
                            Thread.sleep(20);
                            updateProgress(i / 100.0, 1.0);

                            final int progress = i;
                            //run this on javafx app thread for gui update
                            Platform.runLater(() -> {
                                if (progress <= 50) {
                                    statusL.setText("Loading Yummy Data... (" + progress + "%)");
                                } else if (progress <= 60) {
                                    statusL.setText("Setting up Interface... (" + progress + "%)");
                                } else if (progress <= 80) {
                                    statusL.setText("Getting Your Location... (" + progress + "%)");
                                } else if (progress <= 100) {
                                    statusL.setText("Almost there :D (" + progress + "%)");
                                }
                            });
                        }

                        return markup;

                    } catch (IOException e) {
                        e.printStackTrace();
                        this.cancel();

                    } finally {
                        if (http != null) {
                            http.disconnect();
                        }
                    }

                    return markup;
                }
            };
        }
    }
}
