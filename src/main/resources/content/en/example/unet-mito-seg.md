# Model and data
This model is a **U-Net** from the [**BioImage Model Zoo**](https://bioimage.io/#/models), 
accessible at [bioimage.io/shivering-raccoon](https://bioimage.io/#/artifacts/shivering-raccoon), 
and was developed by Constantin Pape (EMBL Heidelberg). 
It was trained using data from [the VNC dataset](http://dx.doi.org/10.6084/m9.figshare.856713). 

Designed for semantic segmentation, this network identifies mitochondria in **D. melanogaster** nerve tissue sections 
imaged with transmission electron microscopy.

# Processing

## Pre-processing
These pre-processing steps are applied to each image before prediction:
1.  Resizing the image to the size expected by the model: 512x512.
2.  Normalizing the pixel values (using the `per_sample_scale_range.ijm` macro).

## Result
The final result consists of two probability masks:
*   one indicating the presence of mitochondria
*   and the other for the mitochondria boundaries