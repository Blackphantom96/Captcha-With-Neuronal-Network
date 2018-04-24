import random

for i in range(0,100):
    x=[random.randint(0,300) for i in range(6)]
    print('convert -fill white -stroke black -draw \"polyline {},{} {},{} {},{}\" {}.jpg'.format(x[0],x[1],x[2],x[3],x[4],x[5],i))
