"""
Criado por Joel
este codigo serve para detectar imagens não anotadas, daí deleta-se elas.
"""

import os
import glob

class DetectFalseAnnotations():
    def __init__(self):
        pass

    def delete_all(self, path):

        for file in glob.glob(path+"/*.xml"):
            print(file)
            link_name = file.replace(".xml", "")

            if not os.path.isfile(link_name+".jpg"):
                os.remove(link_name+".xml")

#deletar todas falsas anotações no conjunto de teste
DetectFalseAnnotations().delete_all("C:/Users/Joelp/OneDrive/Imagens/blocos_ufrb/object_detection/anotadas/validation/")

#deletar todas falsas anotações no conjunto de treino
DetectFalseAnnotations().delete_all("C:/Users/Joelp/OneDrive/Imagens/blocos_ufrb/object_detection/anotadas/treino/")