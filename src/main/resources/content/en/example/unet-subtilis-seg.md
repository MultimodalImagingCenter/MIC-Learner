# Warning
**This model can only be run with Java 11 (ImageJ default is Java 8)**

# Model and data
The model is a **U-Net** from the **BioImage Model Zoo**.
This instance segmentation model was trained to segment ***B. subtilis*** cells in widefield microscopy images.

This model is a **U-Net** from the [**BioImage Model Zoo**](https://bioimage.io/#/models),
accessible at [bioimage.io/placid-llama](https://bioimage.io/#/artifacts/placid-llama),
and was developed by Estibaliz Gómez de Mariscal (Instituto Gulbenkian de Ciência).

Designed for instance segmentation, this network identifies instances of ***B. subtilis*** cells in widefield microscopy images.
It predicts boundary, background and foreground probabilities maps.

# Processing

## Pre-processing
These pre-processing steps are applied to each image before prediction:
1.  Resizing the image to the size expected by the model: 512x512.
2.  Normalizing the pixel values (using the `per_sample_scale_range.ijm` macro).

## Post-processing
These post-processing steps are applied to the raw prediction output:
1.  Separating the individual objects and assigning a unique value to each, using the `Contours2InstanceSegmentation.ijm` macro.

## Result
The final output is an **instance mask**, where each object is represented by a unique pixel value. For clearer visualization, each value is assigned a distinct color (by applying a Look-Up Table, or LUT).