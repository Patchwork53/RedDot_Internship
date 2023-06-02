# RedDot_Intership_P1

---
### Commit 1
### Task: construct an Android project using a different tensor-flow model

### Model Used
Custom CNN (500KB once converted to tflite)<br>
https://www.kaggle.com/sameen53/card-classifier-tf-reddot

### Dataset
CIFAR10

### Accuracy
69% on Test Dataset

### Guide Followed
https://www.youtube.com/watch?v=gVJC1j2n9tE

### Next Up
<ul>
  <li> Collect card dataset</li>
  <li> Make better model for card classification </li>
  <li> Look into openCV for detecting cards, cropping and reframing </li>
</li>
</ul>

---

### Commit 2
### Task: Add Tesseract
<ul>
  <li> new class Assets and Config added </li>
  <li> outputGenerator() changed </li>
  <li> eng and bn trained models added in assets </li>
</ul>

outputGenerator() structure:
```
outputGenerator(){
//Base
...
//Commit 1 - Basic tflite model
...
//Commit 2 - Tesseract OCR
```
---
### Commit 3
### Task: Segmenting Images and Extracting Key Value Pairs

![image](https://github.com/PatchworkProgrammer/RedDot_Intership_P1/assets/83033987/5f72eef6-1648-4902-9fa1-4e9f2d2f2a53)

<ul>
  <li> two TesseractAPI (en and bn) </li>
  <li> segmenting images based on ratios </li>
</ul>

### Next Up
<ul>
  <li> preprocessing images for alignment (openCV for android?)</li>
  <li> Fake ID check </li>
  <li> New NID check </li>
  <li> Liveliness check </li>
</ul>
