from pacote.utils import name_of_image,rename_all,split_data
from pacote.edits_image import Edicoes
from pacote.annotation_functions import XmlToOther

xml_to_other = XmlToOther()
label_map = {
    "ANTIGA BIBLIOTECA":1,
    "BIBLIOTECA UFRB":2,
    "ESTUFA":3,
    "LAB":4,
    "PAV ENG":5,
    "PAV I":6,
    "PAV II":7,
    "PREDIO SOLOS":8,
    "REITORIA UFRB":9,
    "CETEC": 10
}
"""
xml_to_other.xml_to_txt("C:/Users/Joelp/OneDrive/Imagens/blocos_ufrb/anotacao-fina/",
    "C:/Users/Joelp/OneDrive/Imagens/blocos_ufrb/anotacao-fina-txt/",
    ["ANTIGA BIBLIOTECA", "BIBLIOTECA UFRB", "ESTUFA", "LAB", "PAV ENG", "PAV I", "PAV II", "PREDIO SOLOS", "REITORIA UFRB"],
    type_image = "jpg",
    map_labels=label_map, with_images=True)
import glob
for path in ["ANTIGA BIBLIOTECA", "BIBLIOTECA UFRB", "ESTUFA", "LAB", "PAV ENG", "PAV I", "PAV II", "PREDIO SOLOS", "REITORIA UFRB"]:
    for file in glob.glob("C:/Users/Joelp/OneDrive/Imagens/blocos_ufrb/anotacao-fina/txt/"+path+"/*.xml"):
        os.remove(file)

"""
#Após as anotações não dependerem mais do tamanho da imagem, pode-se redimensionar
crop_scale = Edicoes("C:/Users/Joelp/OneDrive/Imagens/blocos_ufrb/anotacao-fina-txt", "../data/images/train")
crop_scale.rescale(512, 512, False, True)

#Aplica-se um desfoque
edicoes = Edicoes("../data/images/train", "../data/images/desfocada")
edicoes.blur_image('g', 2)



"""
Dados de treino
"""
label_map = {
    "ANTIGA BIBLIOTECA":1,
    "BIBLIOTECA UFRB":2,
    "ESTUFA":3,
    "LAB":4,
    "PAV ENG":5,
    "PAV I":6,
    "PAV II":7,
    "PREDIO SOLOS":8,
    "REITORIA UFRB":9,
    "CETEC": 10
}
# run
main("../data/images/train.csv","../data/images/train","../data/images/train.tfrecord",label_map)

"""
Dados de validação
"""
# run
main("../data/images/validation.csv","../data/images/validation","../data/images/validation.tfrecord",label_map)
