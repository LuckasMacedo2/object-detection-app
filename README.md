# Object Detector Application

## Application description

This project includes the code for an Android application used to detect defective additively manufactured objects.

The application was developed using the Kotilin programming language. The idea of the application is to capture images of objects, send them to the API (https://github.com/LuckasMacedo2/object-detection-api) which will perform the detection and return the information that will be displayed on the screen and then display this information on the screen for the user. It has three operating modes:

- Image: User selects an image from the gallery;
- Photo: User takes a photo of the object;
- Video (real-time): The camera is displayed and objects are detected and updated on the smartphone screen.

## Dependencies

- Android studio;
- Kotlin language;
- API that forms the backend present in the repository: https://github.com/LuckasMacedo2/object-detection-api;
- Run the API by changing the IP address to the host IP address. Both in the API and in the application. In the application, the IP must be changed in the class: Utils/Constants/APIConstants change the IP variable to the host IP and run both the API and the application.

Note: To run it on your smartphone, compile the project with your smartphone in debbug mode or create an .apk file and open it on your smartphone.

# How the application works

The application takes one image at a time, sends it to the API and waits for it to return to display it to the user. The following figure shows how the application works together with the API

![Image10](https://github.com/LuckasMacedo2/object-detection-app/assets/33878052/0f103842-85a3-47be-a3b8-22ba18c60b73)

The following figures show the application's graphical interface.

![Image11](https://github.com/LuckasMacedo2/object-detection-app/assets/33878052/4237b63f-2334-411b-aa69-fbb19df47c55)
![Image12](https://github.com/LuckasMacedo2/object-detection-app/assets/33878052/7a4dad8a-3571-4824-b570-dadbc23bcdb5)

# Dataset

The dataset used to train the models can be found at the link: https://github.com/LuckasMacedo2/manufactured-objects-defectives-dataset/tree/master. More information can be found in the repository itself.

# References:

SILVA, L. M.; ALCALÁ, S. G. S.; BARBOSA, T. M. G. A.; ARAÚJO, R. Object and defect detection in additive manufacturing using deep learning algorithms. Production Engineering, p. 1-14, 2024. DOI: http://dx.doi.org/10.1007/s11740-024-01278-y
