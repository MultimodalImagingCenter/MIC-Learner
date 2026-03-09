# Model and data

## Data
This model was trained with the dataset _100,000 histological images of human colorectal cancer and healthy tissue_
(Kather, Halama, & Marx, 2018), which is available on [zenodo](https://zenodo.org/records/1214456).

**Data Description "NCT-CRC-HE-100K"**
- This is a set of 100,000 non-overlapping image patches from hematoxylin & eosin (H&E) stained histological images of human colorectal cancer (CRC) and normal tissue.
- All images are 224x224 pixels (px) at 0.5 microns per pixel (MPP). All images are color-normalized using Macenko's method (http://ieeexplore.ieee.org/abstract/document/5193250/, DOI [10.1109/ISBI.2009.5193250](https://doi.org/10.1109/ISBI.2009.5193250)).
- Tissue classes are: Adipose (ADI), background (BACK), debris (DEB), lymphocytes (LYM), mucus (MUC), smooth muscle (MUS), normal colon mucosa (NORM), cancer-associated stroma (STR), colorectal adenocarcinoma epithelium (TUM).
- These images were manually extracted from N=86 H&E stained human cancer tissue slides from formalin-fixed paraffin-embedded (FFPE) samples from the NCT Biobank (National Center for Tumor Diseases, Heidelberg, Germany) and the UMM pathology archive (University Medical Center Mannheim, Mannheim, Germany). Tissue samples contained CRC primary tumor slides and tumor tissue from CRC liver metastases; normal tissue classes were augmented with non-tumorous regions from gastrectomy specimen to increase variability.

Only 3 classes were kept : LYM, MUS and ADI.

The data was split into 70% for training, 15% for validation, and 15% for testing. Subsampling was applied to the test set to balance the classes.

## Model
This is a Multi-Layer Perceptron (MLP) composed of 3 fully connected layers with Batch Normalization and Dropout:

1.  **Flatten:** Unrolls the 3D image tensor (`3 x 64 x 64`) into a 1D vector.
    *   *Output Vector Size: `12,288`*

2.  **Linear Block 1:**
    *   **Linear:** Reduces dimension from 12,288 to **1024** neurons.
    *   **BatchNorm1d:** Normalizes the layer inputs to stabilize training.
    *   **ReLU:** Activation function.
    *   **Dropout (0.5):** Randomly zeros 50% of neurons to prevent overfitting.

3.  **Linear Block 2:**
    *   **Linear:** Reduces dimension from 1024 to **512** neurons.
    *   **BatchNorm1d:** Normalizes the layer inputs.
    *   **ReLU:** Activation function.
    *   **Dropout (0.5):** Randomly zeros 50% of neurons.

4.  **Linear (Output Layer):** Maps the 512 features to the **3** target classes.

## Expected image
An RGB image of a homogeneous tissue (HE staining), composed of a single cell type.

# Processing

## Pre-processing
These pre-processing steps are applied to each image before prediction:
1.  Resizing the image to **64x64** pixels (bilinear interpolation).
2.  Converting to Tensor (scaling pixel values from 0-255 to 0.0-1.0).
3.  Normalizing using ImageNet statistics (Mean: `[0.485, 0.456, 0.406]`, Std: `[0.229, 0.224, 0.225]`).
## Post-processing
These post-processing steps are applied to the raw prediction output:
1.  Apply a Softmax function to the logits to obtain a probability distribution.
2.  Select the index with the highest probability and map it to the corresponding class string (0=ADI, 1=LYM, 2=MUS).

## Result
The final output is a class + probability for each image.