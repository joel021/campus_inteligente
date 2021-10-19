from pacote.annotation_functions import TxtToOther
from pacote.utils import split_data,rename_all

rename_all("../data/images/train")
split_data("../data/images/train", "../data/images/validation")

txt_to_other = TxtToOther()
classes = [
    "OBJECT",
    "ANTIGA BIBLIOTECA",
    "BIBLIOTECA UFRB",
    "ESTUFA",
    "LAB",
    "PAV ENG",
    "PAV I",
    "PAV II",
    "PREDIO SOLOS",
    "REITORIA UFRB",
    "CETEC"]

txt_to_other.txt_to_csv("../data/images/train", "../data/images/train.csv", classes, "jpg")
txt_to_other.txt_to_csv("../data/images/validation", "../data/images/validation.csv", classes,"jpg")