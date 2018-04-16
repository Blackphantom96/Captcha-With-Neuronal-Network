from sys import stdin

hashes = dict()
reps = []


def main():
    x = stdin.readline().strip()
    while len(x) != 0:
        a, b = [i for i in x.split()]
        n = hashes.get(a, [])
        n.append(b)
        hashes[a] = n
        x = stdin.readline().strip()

    for a, b in hashes.items():
        assert isinstance(b, list)
        if len(b) > 1:
            # print(a, b, len(b))
            reps.extend(b)

    print(" ".join(reps))


main()
