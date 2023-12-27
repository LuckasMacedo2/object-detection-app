# Object Detector Aplication

## Descrição do aplicativo

Este projeto contempla o código para um aplicativo Android utilizada para detectar os objetos defeituosos manufaturados aditivamente.

O aplicativo foi desenvolvido utilizando a linguagem de programação Kotilin. A ideia do aplicativo é capturar imagens dos objetos, enviar para a API (https://github.com/LuckasMacedo2/object-detection-api) que realzará a detecção e retornará as informações que serão exibidas na tela e então exibir essas informações na tela pro usuário. Possui três modos de operação:

- Imagem: Usuário seleciona uma imagem na galeria;
- Foto: Usuário tira foto do objeto;
- Vídeo (tempo real): A câmera é exibida e os objetos são detectados e atualizados na tela do smartphone.

## Dependências

- Android studio;
- Linguagem Kotlin;
- API que forma o backend presente no repositório: https://github.com/LuckasMacedo2/object-detection-api;
- Executar a API mudando o endereço IP para o endereço IP do host. Tanto na API quanto no aplicativo. No aplicativo o IP deve ser alterado na classe: Utils/Constants/APIConstantes alterar a variável IP para o IP do host e executar tanto a API quanto o aplicativo.

Obs.: Para exxecutar no smartphone compilar o projeto com o smartphone em modo debbug ou criar um arquivo .apk e abrir no smartphone.

# Funcionamento do aplicativo

O aplicativo obtém uma imagem por vez, envia para a API e espera o retorno para exibir para o usuário. A Figura a seguir apresenta o funcionamento do aplicativo em conjunto com a API

![Imagem10](https://github.com/LuckasMacedo2/object-detection-app/assets/33878052/0f103842-85a3-47be-a3b8-22ba18c60b73)

As Figuras a seguir apresentam a interface gráfica da aplicação.

![Imagem11](https://github.com/LuckasMacedo2/object-detection-app/assets/33878052/4237b63f-2334-411b-aa69-fbb19df47c55)
![Imagem12](https://github.com/LuckasMacedo2/object-detection-app/assets/33878052/7a4dad8a-3571-4824-b570-dadbc23bcdb5)



# Dataset

O dataset utilizado para treinar os modelos se encontra no link: https://github.com/LuckasMacedo2/manufactured-objects-defectives-dataset/tree/master. Mais informações podem ser encontradas no próprio repositório.

# Referências:

SILVA, L. M. D.; ALCALÁ, S. G. S.; BARBOSA, T. M. G. D. A. PROPOSTA DE MODELOS DE INTELIGÊNCIA ARTIFICIAL PARA DETECÇÃO DE DEFEITOS EM PEÇAS MANUFATURADAS ADITIVAMENTE. 2022. 

SILVA, L. M. DA; ALCALÁ, S. G. S.; BARBOSA, T. M. G. DE A. Detecção de produtos manufaturados defeituosos utilizando modelos de inteligência artificial. 2022b. 

MACEDO, L.; GOMES, S. UMA REVISÃO SISTEMÁTICA SOBRE A DETECÇÃO DE OBJETOS DEFEITUOSOS PRODUZIDOS POR MANUFATURA ADITIVA. Anais ... Encontro Nacional de Engenharia de Produção, 27 out. 2023. 

SILVA, L. M. D. et al. ALGORITMOS DE APRENDIZAGEM PROFUNDA PARA DETECÇÃO DE OBJETOS DEFEITUOSOS PRODUZIDOS POR MANUFATURA ADITIVA. 2023.

# English

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

SILVA, L. M. D.; ALCALÁ, S. G. S.; BARBOSA, T. M. G. D. A. PROPOSTA DE MODELOS DE INTELIGÊNCIA ARTIFICIAL PARA DETECÇÃO DE DEFEITOS EM PEÇAS MANUFATURADAS ADITIVAMENTE. 2022. 

SILVA, L. M. DA; ALCALÁ, S. G. S.; BARBOSA, T. M. G. DE A. Detecção de produtos manufaturados defeituosos utilizando modelos de inteligência artificial. 2022b. 

MACEDO, L.; GOMES, S. UMA REVISÃO SISTEMÁTICA SOBRE A DETECÇÃO DE OBJETOS DEFEITUOSOS PRODUZIDOS POR MANUFATURA ADITIVA. Anais ... Encontro Nacional de Engenharia de Produção, 27 out. 2023. 

SILVA, L. M. D. et al. ALGORITMOS DE APRENDIZAGEM PROFUNDA PARA DETECÇÃO DE OBJETOS DEFEITUOSOS PRODUZIDOS POR MANUFATURA ADITIVA. 2023.
