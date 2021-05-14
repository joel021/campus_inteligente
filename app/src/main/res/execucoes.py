#Ordenar a lista

class BlocoRA():

    def __init__(self, p1, p2, t, id):
        self.p1 = p1
        self.p2 = p2
        self.t = t
        self.id = id


class Ponto():
    def __init__(self, lat, lon):
        self.lat = lat
        self.lon = lon


blocoList = list()

blocoList.append(BlocoRA( Ponto(-12.658567831849457, -39.087831768455096),  Ponto(-12.659766398168875, -39.08881134641973), "REITORIA UFRB","R.drawable.reitoria"))
blocoList.append(BlocoRA( Ponto(-12.658163195577893, -39.09012406919289),  Ponto(-12.658984821019407, -39.08901645008407), "PAV I","R.drawable.pav_i"))
blocoList.append(BlocoRA( Ponto(-12.657759826859012, -39.09210228275516),  Ponto(-12.658468433245261, -39.09138730199762), "BIBLIOTECA","R.drawable.biblioteca"))
blocoList.append(BlocoRA( Ponto(-12.657893226725244, -39.09349384957524),  Ponto(-12.657675362989409, -39.09221725932677), "PAV II","R.drawable.pav_ii"))
blocoList.append(BlocoRA( Ponto(-12.657031847991465, -39.09405903096211),  Ponto(-12.657991329712273, -39.09324480997324), "BOTANICA","R.drawable.antiga_biblioteca"))
blocoList.append(BlocoRA( Ponto(-12.657038449940227, -39.094018432723736),  Ponto(-12.65761722029418, -39.09497474768018), "PAV ENG","R.drawable.pav_eng"))
blocoList.append(BlocoRA( Ponto(-12.657129481429301, -39.09127227691614),  Ponto(-12.657695739379028, -39.09071846736173), "CETEC","R.drawable.cetec"))
blocoList.append(BlocoRA( Ponto(-12.65846381653017, -39.087447187401914),  Ponto(-12.65749813642967, -39.0864038081611), "PREDIO SOLOS","R.drawable.predio_solos"))
blocoList.append(BlocoRA( Ponto(-12.657550476903356, -39.08611949394223),  Ponto(-12.657848817426004, -39.08554818345819), "LAB 1", "R.drawable.lab_1"))
blocoList.append(BlocoRA( Ponto(-12.657683945075043, -39.08552404357859),  Ponto(-12.658298944571804, -39.08532019570634), "LAB 2", "R.drawable.lab_2"))
blocoList.append(BlocoRA( Ponto(-12.657757221688618, -39.08522095397907),  Ponto(-12.657757221688618, -39.08522095397907), "LAB 3", "R.drawable.lab_3"))
blocoList.append(BlocoRA( Ponto(-12.657817413176897, -39.08495273309454),  Ponto(-12.658424561307958, -39.084722063133846), "LAB 4", "R.drawable.lab_4"))
blocoList.append(BlocoRA( Ponto(-12.657903774852652, -39.084646961286175),  Ponto(-12.658510393877538, -39.08415810360615), "LAB 5", "R.drawable.lab_5"))

for b in blocoList:

    lat_min = min(b.p1.lat,b.p2.lat)
    lat_max = max(b.p1.lat,b.p1.lat)

    lon_min = min(b.p2.lon, b.p2.lon)
    lon_max = max(b.p2.lon,b.p2.lon)

    p1 = Ponto(lat_min,lon_min)
    p2 = Ponto(lat_max,lon_max)

    print("blocoList.add(new BlocoRA( new Ponto({},{}), new Ponto({},{}), \"{}\", {}));".format(p1.lat,p1.lon,p2.lat,p2.lon,b.t,b.id))