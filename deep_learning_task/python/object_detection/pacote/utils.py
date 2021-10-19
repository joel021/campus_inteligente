import glob
import os
import xml.etree.ElementTree as ET


"""
Este método serve para colocar os nomes das imagens de forma correta nos xml de anotação
"""
def name_of_image(path, type_annotation="xml"):
    """
    :param path: pasta que contém imagens anotadas por xml
    :return:
    """
    """
        Não verifico anotação txt porque no meu projeto as txt já vieram do xml, então basta verificar o xml...
    """
    if "xml" in type_annotation:

        for image in glob.glob(path+"/*.jpg"):
            split = os.path.split(image)

            xml_file = split[0]+"/"+split[1].replace(".jpg", "."+type_annotation)
            tree = ET.parse(xml_file)
            root = tree.getroot()

            root.find('filename').text = split[1]
            root.find('path').text = split[1]

            tree.write(xml_file)

def create_path(path):
    if not path or path == "":
        return

    if not os.path.isdir(path):
        create_path(os.path.split(path)[0])
        os.mkdir(path)
        return

"""
# TO-DO replace this with label map
def class_text_to_int(row_label):
    if row_label == 'OBJECT':
        return 1
    else:
        None
"""
"""
Renomeia todos para que fiquem com nome sem espaço e caracteres especiais
"""
def rename_all(path):

    abs_path = os.path.abspath(path)

    files = glob.glob(abs_path+"/*.txt")
    i = 2*len(files)
    for file in files:

        base = os.path.split(file)

        if "classes" in base[1]:
            continue


        name_img = base[1].replace(".txt",".jpg")
        #renomear txt
        os.rename(file, base[0]+"/imagefile"+str(i)+".txt")

        #renomear jpg
        os.rename(base[0]+"/"+name_img,base[0]+"/imagefile"+str(i)+".jpg")

        i = i+1

def split_data(path, to_path_split, split=0.2, type_annotation="txt"):
    """
    :param path: pasta onde estão os arquivos de anotação e as imagens
    :param to_path_split: pasta onde as imagens e arquivos separados serão salvos
    :param split:
    :param type_annotation:
    :return:
    """
    create_path(to_path_split)

    abs_path = os.path.abspath(path)

    files = glob.glob(abs_path+"/*."+type_annotation)
    q = len(files)
    q_split = int(q*split)

    passo = int(q/q_split)

    for i in range(0,q,passo):

        file = files[i]
        base = os.path.split(file)

        if "classes" in base[1]:
            continue

        image_name = base[1].replace("."+type_annotation,".jpg")

        os.rename(file, to_path_split+"/"+base[1])
        os.rename(base[0]+"/"+image_name, to_path_split+"/"+image_name)
