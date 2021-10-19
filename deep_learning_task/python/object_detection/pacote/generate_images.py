"""
 Criado por Joel

 Este conjunto de classes proporcionam um tratamento inicial para um conjunto de imagens.

"""
import os
from tensorflow.keras.preprocessing.image import ImageDataGenerator, load_img, img_to_array
from threading import Thread
from pacote.utils import create_path


import glob

#Gera imagens com uso da API do Keras, augment data. Não tenho controle a nivel de imagens
class GenerateImage(Thread):

    def __init__(self, path, to_path, qtd):
        """
        :param path: pasta que contém as imagens anotadas
        :param to_path: pasta para onde as imagens serão salvas
        :param qtd: quantidade estimada para geração de imagens
        """
        Thread.__init__(self)
        self.path = path
        self.to_path = to_path
        self.qtd = qtd

    def run(self):
        self.generate()

    #gera as imagens
    def generate(self):

        uri_images = glob.glob(self.path + '/*.jpg')  # lista de nomes de arquivos que existem na pasta path
        # objeto de ImageDataGenerator, que gera imagens
        datagenerator = ImageDataGenerator(
            rotation_range=90,
            #width_shift_range=0.1,
            #height_shift_range=0.1,
            rescale=1./255,
            #shear_range=0.2,
            #zoom_range=0.2,
            horizontal_flip=True,
            vertical_flip=True,
            fill_mode='nearest'
        )

        # criar imagens a partir da lista na pasta
        for uri_image in uri_images:
            if not os.path.isfile(uri_image):  # Se não for arquivo, pula esse
                continue

            # cria uma pasta correspondente para essa imagem
            create_path(self.to_path + "/" + os.path.split(os.path.abspath(uri_image))[1].replace(".jpg", ""))

            image = load_img(uri_image)  # carrega a imagem
            image_array = img_to_array(image)
            image_array = image_array.reshape((1,) + image_array.shape)  # redimensiona a imagem

            i = 0
            for batch in datagenerator.flow(image_array,
                                            batch_size=1,
                                            save_to_dir=self.to_path,
                                            save_prefix="g",
                                            save_format='jpg'):

                print('.', end="")
                i = i + 1
                if (i >= self.qtd):
                    break


def rename_img_g(path):

    path_ = None
    for file in os.listdir(path):

        if os.path.isdir(path+"/"+file):
            rename_img_g(path+"/"+file)
        else:
            path_ = path


    i = 0

    print(path_)

    prefix = os.path.split(path_)[1]
    for file in glob.glob(path_+"/*.jpg"):

        name = os.path.split(path_)[1].replace(".jpg", "")

        try:
            os.rename(file, path_+"/"+str(prefix)+"_"+str(i)+".jpg")
            os.rename(path_+"/"+name+".txt", path_+"/"+str(prefix)+"_"+str(i)+".txt")
        except:
            pass
        i = i+1

if __name__ == '__main__':
    import argparse

    parser = argparse.ArgumentParser(
        description='Gerar imagens a partir de uma pasta.')
    parser.add_argument('--path', type=str, default='',
                        help='Pasta de origem das imagens.')
    parser.add_argument('--with_sub_cls', type=str, default='',
                        help='True ou False: existem classes, que são pastas, dentro da pasta de origem das imagens?')
    parser.add_argument('--to_path', type=str, default='',
                        help='Onde as imagens serão salvas.')
    parser.add_argument('--qtd', type=int, default='', help='Quantidade de imagens a serem geradas por imagem na pasta de origem.')
    args = parser.parse_args()

    if args.with_sub_cls:
        for classe in os.listdir(args.path):
            g = GenerateImage(args.path+"/"+classe, args.to_path+"/"+classe, args.qtd)
            g.start()
    else:
        g = GenerateImage(args.path, args.to_path, args.qtd)
        g.start()