import random
import math
def heron(x):
    a=math.sqrt( (x[0] - x[2])**2 + (x[1] - x[3])**2)
    b=math.sqrt( (x[2] - x[4])**2 + (x[3] - x[5])**2)
    c=math.sqrt( (x[0] - x[4])**2 + (x[1] - x[5])**2)
    s=(a+b+c)/2
    return math.sqrt(s*(s-a)*(s-b)*(s-c))

for i in range(1,106):
    x=[random.randint(1,299) for i in range(6)]
    while(400<(math.ceil(heron(x))<300)):
        x=[random.randint(1,299) for i in range(6)]
    print('convert test/{}.jpg -fill white -stroke black -draw \"polyline {},{} {},{} {},{}\" test/{}.jpg'.format(i,x[0],x[1],x[2],x[3],x[4],x[5],i))

