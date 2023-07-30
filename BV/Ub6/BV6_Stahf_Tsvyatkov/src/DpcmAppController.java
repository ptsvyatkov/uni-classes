// BV Ue1 WS2020/21 Vorgabe
//
// Copyright (C) 2019-2020 by Klaus Jung
// All rights reserved.
// Date: 2020-10-08


import java.io.File;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;

public class DpcmAppController {
	
	private static final String initialFileName = "test1.jpg";
	private static File fileOpenPath = new File(".");
	
	public enum PredictionType { 
		A("A (horizontal)"), 
		B("B (vertical)"),
		C("C (diagonal)"),
		ABC("A+B-C"),
		adaptive("adpative");
		
		private final String name;       
	    private PredictionType(String s) { name = s; }
	    public String toString() { return this.name; }
	};

	private double quantization;

    @FXML
    private Slider quantizationSlider;

    @FXML
    private Label quantizationLabel;

    @FXML
    private ComboBox<PredictionType> predictionSelection;

    @FXML
    private ImageView originalImageView;

    @FXML
    private ImageView errorImageView;

    @FXML
    private ImageView reconstructedImageView;

    @FXML
    private Label messageLabel;
    
    @FXML 
    private Label oEntropyLabel;
    
    @FXML
    private Label eEntropyLabel;
    
    @FXML
    private Label rEntropyLabel;
    
    @FXML Label mseLabel;

    @FXML
    void openImage() {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setInitialDirectory(fileOpenPath); 
		fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Images (*.jpg, *.png, *.gif)", "*.jpeg", "*.jpg", "*.png", "*.gif"));
		File selectedFile = fileChooser.showOpenDialog(null);
		if(selectedFile != null) {
			fileOpenPath = selectedFile.getParentFile();
			RasterImage img = new RasterImage(selectedFile);
			img.convertToGray();
			img.setToView(originalImageView);
	    	processImages();
	    	messageLabel.getScene().getWindow().sizeToScene();;
		}
    }
	
    @FXML
    void predictionChanged() {
    	processImages();
    }

    
    @FXML
    void quantizationChanged() {
    	quantization = quantizationSlider.getValue();
    	quantizationLabel.setText(String.format("%.1f", quantization));
    	processImages();
    }
    
	@FXML
	public void initialize() {
		// set combo boxes items
		predictionSelection.getItems().addAll(PredictionType.values());
		predictionSelection.setValue(PredictionType.A);
		
		// initialize parameters
		quantizationChanged();
		predictionChanged();
		
		// load and process default image
		RasterImage img = new RasterImage(new File(initialFileName));
		img.convertToGray();
		img.setToView(originalImageView);
		processImages();
	}
	
	private void processImages() {
		if(originalImageView.getImage() == null)
			return; // no image: nothing to do
		
		long startTime = System.currentTimeMillis();
		
		RasterImage origImg = new RasterImage(originalImageView); 
		
		DPCM dpcm = null;
		
		switch(predictionSelection.getValue()) {
		case A:
			dpcm = new DPCM(origImg, "A");
			break;
		case B:
			dpcm = new DPCM(origImg, "B");
			break;
		case C:
			dpcm = new DPCM(origImg, "C");
			break;
		case ABC:
			dpcm = new DPCM(origImg, "ABC");
			break;
		case adaptive:
			break;
		default:
			break;
		}
		
		RasterImage errorImg = new RasterImage(dpcm.getErrorImg());

		RasterImage reconstructedImg = new RasterImage(dpcm.getRecImg());

		
		errorImg.setToView(errorImageView);
		reconstructedImg.setToView(reconstructedImageView);
		

	   	oEntropyLabel.setText("Entropy = " + String.format("%.3f", origImg.entropy));
	   	eEntropyLabel.setText("Entropy = " + String.format("%.3f", errorImg.entropy));
	   	rEntropyLabel.setText("Entropy = " + String.format("%.3f", reconstructedImg.entropy));
	   	mseLabel.setText("MSE = " + String.format("%.3f", dpcm.getMSE()));
		
	   	messageLabel.setText("Processing time: " + (System.currentTimeMillis() - startTime) + " ms");

	}
}
