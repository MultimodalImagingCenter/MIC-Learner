# MIC-Learner

MIC-Learner is a plugin for ImageJ that provides a GUI for discovering AI approaches in Image Processing.

## The GUI divides into 2 main parts: 
- the first in task-driven to help the user to find the best approach for a given task.
- the second is algorithm-driven to help the user to find the best algorithm for a given task. 

### The tasks are divided into 4 main categories:
- Image Classification
- Object Detection
- Image Segmentation (semantic or instance segmentation)
- Image Enhancement (denoising)

With each time a description of what are the expected results for each type of task.

### The provided algorithms corresponds to classics in computer vision.
- Random Forest (using) Weka
- Multi-Layered Perceptron (MLP)
- Convolutional Neural Network (CNN)
- You Only Look Once (YOLO)
- U-Net

The algorithms are explained and a test model is provided to allow the user to test the performance of the algorithms.
The test models use-cases are explained along with the algorithms.
Tests can be performed on a sample image or on an image provided by the user.