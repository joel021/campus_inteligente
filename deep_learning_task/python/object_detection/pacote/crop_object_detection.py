import argparse

from pacote.utils import create_path
import glob
from PIL import Image
import os

"""
Recorta as detecções em imagens para imagens individuais.
Recorta as annotações em imagens para imagens individuais.
"""

class CropObjectDetection():

    def _init_(self):

        pass
    """
    Entre imagens anotadas em uma pasta e salva os recortes em uma pasta com o nome da classe de anotação correspondente
    """
    def from_annotation_path(self, path_annotation, to_path):
        print("path_annotation")
        """
        :param path_annotation: pasta que contém as imagens anotadas no formato de posição relativa
        :param to_path: pasta mãe de onde as imagens deverão ser salvas
        :return:
        """

        clas_f = open(path_annotation+"/classes.txt", "r")
        clas = clas_f.readlines() #classes names
        clas_f.close()

        #list annotations
        for uri_annotation in glob.glob(path_annotation+"/*.txt"):
            if "classes" in uri_annotation:
                continue

            annotation_f = open(uri_annotation, "r")

            image_uri = uri_annotation.replace(".txt", ".jpg")

            for line in annotation_f.readlines():
                r_position = line.split(" ") #a forma nos arquivos txt é: class, x, y, w, h
                self.from_detection(image_uri, clas, r_position, to_path)
            annotation_f.close()


    def from_detection(self, image_detection, classes, rect_box, to_path):
        """
        :param image_detection: (string) uri da imagem origina
        :param classes: (array) com nomes das classes de anotação. ex.: classe1,classe2,classe3
        :param rect_box: (tupla) posições relativas: posição classe,x,y,w,h
        :param to_path: (string) uri da pasta mãe de onde as imagens deverão ser salavas
        :return:
        """

        """
        *rect_box (tupla) = [p_classe,x,y,w,h], x e y são o centro do retangulo.
        """
        if not os.path.exists(to_path):
            create_path(to_path)

        img = Image.open(image_detection)
        W, H = img.size
        left = W*(float(rect_box[1])-float(rect_box[3])/2)
        top = H*(float(rect_box[2])-float(rect_box[4])/2)
        right = W*(float(rect_box[1])+float(rect_box[3])/2)
        bottom = H*(float(rect_box[2])+float(rect_box[4])/2)
        img = img.crop((left, top, right, bottom)) #left, top, right, bottom

        classe = classes[int(rect_box[0])].replace('\n',"")
        if not os.path.exists(to_path+"/"+classe):
            create_path(to_path+"/"+classe)

        name = os.path.split(image_detection)[1].replace(".txt", ".jpg")
        img.save(to_path+"/"+classe+"/"+name)


def options():
    parser = argparse.ArgumentParser()
    parser.add_argument('--to_path', type=bool, default='C:/Users/Joelp/OneDrive/Imagens/blocos_ufrb/anotacao-recorte',
                        help='If is from annotation path or from detection directly')
    parser.add_argument('--path_annotation', type=str, default='C:/Users/Joelp/OneDrive/Imagens/blocos_ufrb/anotacao-fina-txt/',
                        help='If is from annotation path or from detection directly')
    return parser.parse_args()

if __name__ == "__main__":
    opt = options()
    crop_annotation = CropObjectDetection()
    crop_annotation.from_annotation_path(path_annotation=opt.path_annotation,
                                         to_path=opt.to_path)