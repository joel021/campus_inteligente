from PIL import Image, ImageFilter
import os
import glob
from pacote.utils import name_of_image
import shutil

from pacote.utils import create_path


def resolucao_imagens(path, type_image="jpg"):

    for uri in glob.glob(path+"/*."+type_image):
        img = Image.open(uri)

        w,h = img.size

        print("w: "+str(w)+", h = "+str(h))

# Escala de cinza
class Edicoes():

    def __init__(self, path, to_path):
        """
        :param path: pasta que contém as imagens a serem editadas
        :param to_path: pasta para salvamento das imagens editadas.
        """
        self.path = path
        self.to_path = to_path

        create_path(to_path)

    def copy_annotation(self, uri_img, type="txt"):
        qtd = str(len(glob.glob(self.to_path+"/*."+type)))
        base_uri = os.path.split(uri_img)
        name_annotation = base_uri[1].replace(".jpg", "."+type)

        shutil.copy(base_uri[0]+"/"+name_annotation,self.to_path+"/"+qtd+"_"+name_annotation)#copy xml to path/qtd_name_xml.xml
        return self.to_path+"/"+qtd+"_"+base_uri[1]

    def blur_image(self, type, level, type_annotation="txt"):
        print("\nblur")
        for image in glob.glob(self.path+"/*.jpg"):
            img = Image.open(image)

            if type == 'b' and (level is None or level == 0):
                img = img.filter(ImageFilter.BLUR)
            elif type == 'b':
                img = img.filter(ImageFilter.BoxBlur(level))
            elif type == 'g':
                img = img.filter(ImageFilter.GaussianBlur(level))

            img.save(self.copy_annotation(image,type_annotation))


        name_of_image(self.to_path)

    def gray_scale(self, type_annotation="txt"):
        images = os.listdir(self.path)

        for image in images:
            img = Image.open(self.path + "/" + image)

            # converte a imagem para o modo L (escala de cinza)
            img = img.convert('L')

            # salva a nova imagem
            img.save(self.copy_annotation(image, type_annotation))

        name_of_image(self.to_path)

    """
        scaling redimensiona a imagem com base nos parametros dados.
    """
    """ Redimensiona cada imagem da path para o tamanho correspondente
        Esta classe não trabalha com anotação. Certifica-se que se a imagem já estiver anotada, que a anotação seja com valores
        relativos
    """
    def rescale(self, width, height, with_crop, with_resize):
        """
        :param path: pasta onde as imagens a serem trabalhadas estão.: string
        :param to_path: pasta de onde se deseja salvar o resultado da modificação. Se for igual a path, a imagem original é substituída.: string
        :param width: Largura da imagem resultante.: int
        :param height: Altura da imagem resultante.: int
        :param with_crop: se verdadeiro, quer-se redimensionar a imagem por meio de corte: boolean
        :param with_resize:se verdadeiro, quer-se redimensionar a imagem por meio de tração ou compressão
        """
        print("rescale")
        for image in glob.glob(self.path + "/*.jpg"):

            try:
                img = Image.open(image)
            except:
                print("{} não aberta".format(os.path.split(image)[1]))
                continue

            # recortar a imagem
            """
                Se for por corte, deixa-se a imagem quadrada, depois corta-se a imagem, caso ela ainda for maior que o tamanho especificado.
            """
            if with_crop:
                w = img.size[0]
                h = img.size[1]

                # mantém a imagem quadrada
                if w > h:
                    dw = (w - h) / 2.0
                    img = img.crop((dw, 0, w - dw, h))  # position 0 is xi, 1 is yi, 2 is xf, 3 is yf
                elif h > w:
                    dh = (h - w) / 2.0  # position 0 is xi, 1 is yi, 2 is xf, 3 is yf
                    img = img.crop((0, dh, w, h - dh))

                if not with_resize:
                    w, h = img.size()
                    if w > width and not with_resize:
                        dw = (w - width) / 2
                        img = img.crop((dw, 0, w - dw, h))
                    elif h > height:
                        dh = (h - height) / 2
                        img = img.crop((0, dh, w, h - dh))

            # redimenciona comprimindo ou esticando
            if with_resize:
                # redimensionar a imagem
                img = img.resize((width, height), Image.ANTIALIAS)

            img.save(self.to_path+"/"+os.path.split(image)[1])

            if not self.to_path in self.path:
                name = os.path.split(image)[1].replace(".jpg", ".txt")
                shutil.copy(self.path+"/"+name, self.to_path+"/"+name)
            print(".", end="")  # indica o andamento do processo.
