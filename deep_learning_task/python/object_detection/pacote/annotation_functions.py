"""
Codigo encontrado em:
Este codigo serve para converter arquivos xml em um csv
"""
import numpy as np
import os
import glob
import pandas as pd
import xml.etree.ElementTree as ET
from pacote.utils import create_path
import shutil

def visualize_txt_annotations(path):

    for uri in glob.glob(path+"/*.txt"):

        file = open(uri, "r")
        for line in file.readlines():
            print(line)

        file.close()

"""
    #deleta todos arquivos xml que não têm uma imagem correspondente.
    """

"""
Renomeia todas as anotações para o nome da pasta que ela está.
"""
def parse_name_xml(path):
    print(path)
    name = os.path.split(path)[1]
    for file in glob.glob(path+"/*.xml"):
        tree = ET.parse(file)
        root = tree.getroot()

        for member in root.findall('object'):
            member[0].text = name

        tree.write(file)

def parse_name_txt(path, clas):
    for file in glob.glob(path+"/*.txt"):

        if "classes" in file:
            continue

        f = open(file, "r")
        lines = []

        for line in f.readlines():

            if not line or line == "":
                continue

            l = line.split(" ")
            s = "1 "+str(l[1])+" "+str(l[2])+" "+str(l[3])+" "+str(l[4])
            lines.append(s)

        f.close()

        g = open(file, "w")
        for line in lines:
            g.write(line)

        g.close()

    h = open(path+"/classes.txt", "w")
    h.write("undefined\n"+clas)
    h.close()

def check_csv(csv_path):
    df = pd.read_csv(csv_path)
    q = 0

    print("Quantidade de dados: "+str(len(df.filename)))
    for i in range(len(df.filename)):

        row = df.iloc[i]

        #verificar se o retângulo está dentro da imagem
        if row["xmin"] < 0.0 or row['xmin'] > 1.0 or row['ymin'] < 0.0 or row['ymin'] > 1.0 or row['xmax'] < 0.0 or row['xmax'] > 1.0 or row['ymax'] < 0.0 or row['ymax'] > 1.0:
            print("Erro!")

        if row['xmin'] > row['xmax'] or row['ymin'] > row['ymax']:
            print("Maior: "+row['filename'])
            q = q+1


    print("Quantidade: "+str(q))

def _detect_non_annotation(path, type_annotation, type_image):
    print("Detecting non annotations")
    """
    :param path: link das pasta onde estão as imagens anotadas
    :return:
    """
    for file in glob.glob(path + "/*." + type_annotation):
        link_name = file.replace("." + type_annotation, "")

        if not os.path.isfile(link_name + "."+type_image):
            os.remove(link_name + "." + type_annotation)
            print(link_name+" has removed")
    if "xml" in type_annotation or "txt" in type_annotation:
        _detect_non_annotation(path, type_image, type_annotation)


class TxtToOther():

    def txt_to_csv(self,path, to_path, classes, type_image):

        _detect_non_annotation(path, "txt", type_image)

        data = None
        for file in glob.glob(path + "/*.txt"):

            if "classes" in file:
                continue

            #absolute name of file (just name)
            name = os.path.split(file)[1].replace(".txt", "."+type_image)

            read_file = open(file, "r")

            data_file = []
            # adicionar conteudo no array
            for line in read_file.readlines():

                d = [name]
                d = d + line.split(" ") #name,id,x,y,w,h\n

                d[1] = classes[int(d[1])]
                # certificar que não tem "\n" nos valores
                d[5] = d[5].replace("\n", "")

                # certificar que não há anotações repetidas
                if d in data_file:
                    continue

                #converter os valores de xmin, ymin, xmax e ymax
                #A formatação no txt é : class, x, y, w, h
                #A formatação do csv é : filename, class, xmin, ymin, xmax, ymax
                #A formatação que o vetor d está é: filename, class, x, y, w, h
                #Quer-se converter para a formatação csv]

                x = float(d[2])
                y = float(d[3])
                w_2 = float(d[4])/2.0 #w/2
                h_2 = float(d[5])/2.0 #h/2

                d[2] = x-w_2 #xmin
                d[3] = y-h_2 #ymin
                d[4] = x+w_2 #xmax
                d[5] = y+h_2 #ymax

                data_file.append(d)

            read_file.close()

            if data is None:
                data = data_file
            else:
                data = np.concatenate((data, data_file), axis=0)

        dataframe = pd.DataFrame.from_records(data, columns=['filename', 'class', 'xmin', 'ymin', 'xmax', 'ymax'])
        dataframe.to_csv(to_path, index=False)

class XmlToOther():

    def __init__(self):
        pass

    """
    deleta todas as anotações que tiverem xmax ou ymax com valores iguais a zero ou nulos
    """
    def _verfy_csv(self, dataframe):
        """
        :param dataframe: dataframe que contém as colunas xmax,ymax
        :return: dataframe
        """
        print("Verify csv dataframe")
        for i in range(0, len(dataframe.xmax)):
            row = dataframe.iloc[i]

            #filename,width,height,class,xmin,ymin,xmax,ymax
            if row['xmax'] == None or row['xmax'] == 0 or row['ymax'] == None or row['ymax'] == 0:
                dataframe = dataframe.drop(i) #drop row i

        return dataframe

    def _xml_to_csv(self, uri_path):
        """
        :param uri_path: pasta onde as imagens anotadas estão
        :return: retorna um dataframe pandas
        """

        print("Converting xml to csv")
        xml_list = []
        for xml_file in glob.glob(uri_path + '/*.xml'):
            tree = ET.parse(xml_file)
            root = tree.getroot()
            for member in root.findall('object'):
                value = (root.find('filename').text,
                         int(root.find('size')[0].text),
                         int(root.find('size')[1].text),
                         member[0].text,
                         int(member[4][0].text),
                         int(member[4][1].text),
                         int(member[4][2].text),
                         int(member[4][3].text)
                         )
                xml_list.append(value)
        columns_name = ['filename', 'width', 'height', 'class', 'xmin', 'ymin', 'xmax', 'ymax']
        xml_df = pd.DataFrame(xml_list, columns=columns_name)
        return xml_df

    # directories: ['train','validation', 'validation']
    def xml_to_csv(self, uri_path, to_path, directories, type_image):
        create_path(to_path)

        print("Reading csv data frame")
        """
        :param uri_path: pasta onde estão as pastas das imagens anotadas, ou todas as imagens anotadas mesmo. Mas geralemente é uma pasta que contém as pastas
        train, validation e validation
        :param to_path: pasta onde o arquivo csv irá ser salvo
        :param directories: subpastas de imagens que estão dentro da pasta
        :return:
        """
        csvs = []

        for directory in directories:
            print(directory)
            image_path = os.path.join(uri_path, '{}'.format(directory))  # path/directory/

            _detect_non_annotation(image_path,"xml",type_image) #deleta todos arquivos xml que não têm uma imagem correspondente.

            xml_df = self._xml_to_csv(image_path) #dataframe pandas
            xml_df = self._verfy_csv(xml_df)

            xml_df.to_csv('{}/{}_labels.csv'.format(to_path, directory), index=None)
            print('Successfully converted xml to csv.')
            csvs.append(to_path+"/"+directory+"_labels.csv")

        return csvs

    #A priori, se conhece as classes, geralemente definidas em um arquivo 'classes.txt'
    def xml_to_txt(self, uri_path, to_path, directories, type_image, map_labels, list_csv=None, with_images=False):
        """
       :param uri_path: pasta onde estão as pastas das imagens anotadas, ou todas as imagens anotadas mesmo. Mas geralemente é uma pasta que contém as pastas
       train, validation e validation
       :param to_path: pasta onde o arquivo csv irá ser salvo
       :param directories: subpastas de imagens que estão dentro da pasta
       :param map_labels: hashmap do tipo {"classe": numero}
       :param list_csv: csv que mapeia as anotações, recomendado deixar nulo
       :param with_images: se deseja copiar as images para a pasta "to_path" ou não
       :return:
       """

        if list_csv is None:
            list_csv = self.xml_to_csv(uri_path, to_path, directories, type_image)

        for csv,directory in zip(list_csv,directories):

            dataframe = pd.read_csv(csv)
            create_path(to_path+"/"+directory)
            """
            Colunas desse csv: 'filename', 'width', 'height', 'class', 'xmin', 'ymin', 'xmax', 'ymax
            """
            print("Writing txt")
            for i in range(0, len(dataframe.filename)):
                row = dataframe.iloc[i]

                base_name = row[0].replace("."+type_image, "")

                #Se for copiar as imagens para a nova pasta também
                if with_images:
                    shutil.copy(uri_path+"/"+directory+"/"+row['filename'], to_path+"/"+directory+"/"+row['filename'])

                x = (float(row['xmin'])+float(row['xmax']))/(2*row['width'])
                y = (row['ymin']+row['ymax'])/(2*row['height'])
                w = (row['xmax']-row['xmin'])/row['width']
                h = (row['ymax']-row['ymin'])/row['height']

                #values = (row['filename'],class_text_to_int(row['class']),row['xmin']/row['width'],row['ymin']/row['height'],row['xmax']/row['width'],row['ymax']/row['height'])
                #rows.append(values)

                content = str(map_labels[row['class']])+" "+\
                          str(x)+" "+\
                          str(y)+" "+\
                          str(w)+" "+\
                          str(h)+"\n"

                txt = open(to_path+"/"+directory+"/"+base_name+".txt", "a") # 'a' porque pode já haver uma anotação neste arquivo.
                txt.write(content)
                txt.close()


        f_labels = open(to_path+"/labels.txt", "w")
        for v in map_labels:
            f_labels.write(v+"\n")

        f_labels.close()

"""
Exemple: 
1    XmlToOther().xml_to_csv("../data/", "../data/", ['train', 'validation'])
"""
