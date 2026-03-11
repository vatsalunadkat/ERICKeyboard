# ============================================================
# GENETIC ALGORITHM + SA REFINEMENT
# ============================================================

GA_POP = 120
GA_GEN = 500
ELITE = 5
MUT_RATE = 0.03
SA_POLISH_ITER = 50000

def fitness(mapping):
    return -total_cost(mapping)

def random_mapping():
    mapping={}
    free=POSITIONS.copy()
    random.shuffle(free)
    for s,p in zip(symbols,free):
        mapping[s]=p
    return mapping

def pmx(parent1, parent2):
    size=len(symbols)
    child1,child2={},{}
    cut1,cut2=sorted(random.sample(range(size),2))
    
    p1_list=[parent1[s] for s in symbols]
    p2_list=[parent2[s] for s in symbols]

    c1_list=[None]*size
    c2_list=[None]*size

    c1_list[cut1:cut2]=p1_list[cut1:cut2]
    c2_list[cut1:cut2]=p2_list[cut1:cut2]

    def fill(child, parent):
        for i in range(size):
            if child[i] is None:
                candidate=parent[i]
                while candidate in child:
                    idx=parent.index(candidate)
                    candidate=parent[idx]
                child[i]=candidate
        return child

    c1_list=fill(c1_list,p2_list)
    c2_list=fill(c2_list,p1_list)

    child1={s:p for s,p in zip(symbols,c1_list)}
    child2={s:p for s,p in zip(symbols,c2_list)}

    return child1,child2

def mutate(mapping):
    if random.random()<MUT_RATE:
        s1,s2=random.sample(symbols,2)
        mapping[s1],mapping[s2]=mapping[s2],mapping[s1]
    return mapping

def tournament(pop):
    k=5
    contenders=random.sample(pop,k)
    contenders.sort(key=lambda x:x[1])
    return contenders[0][0]

def genetic_algorithm():

    print("Initializing GA population...")
    population=[random_mapping() for _ in range(GA_POP)]
    scored=[(m,total_cost(m)) for m in population]

    for gen in tqdm(range(GA_GEN)):

        scored.sort(key=lambda x:x[1])
        new_pop=[scored[i][0] for i in range(ELITE)]

        while len(new_pop)<GA_POP:
            p1=tournament(scored)
            p2=tournament(scored)
            c1,c2=pmx(p1,p2)
            c1=mutate(c1)
            c2=mutate(c2)
            new_pop.append(c1)
            if len(new_pop)<GA_POP:
                new_pop.append(c2)

        scored=[(m,total_cost(m)) for m in new_pop]

        if gen%50==0:
            print(f"Gen {gen} best:",scored[0][1])

    scored.sort(key=lambda x:x[1])
    return scored[:10]   # return top 10


# ============================================================
# SA POLISH
# ============================================================

def sa_refine(mapping):

    current=mapping.copy()
    best=mapping.copy()
    best_score=total_cost(mapping)

    for i in range(SA_POLISH_ITER):
        s1,s2=random.sample(symbols,2)
        current[s1],current[s2]=current[s2],current[s1]
        new_score=total_cost(current)

        T=0.03*(1-i/SA_POLISH_ITER)

        if new_score<best_score or random.random()<math.exp((best_score-new_score)/T):
            best_score=new_score
            best=current.copy()
        else:
            current[s1],current[s2]=current[s2],current[s1]

    return best,best_score


# ============================================================
# RUN HYBRID OPTIMIZER
# ============================================================

print("\nRunning Genetic Algorithm...")
top_candidates=genetic_algorithm()

print("\nRefining top candidates with SA...")

global_best=None
global_score=float("inf")

for idx,(candidate,score) in enumerate(top_candidates):
    print("Refining candidate",idx,"initial score:",score)
    refined,refined_score=sa_refine(candidate)
    print("Refined score:",refined_score)

    if refined_score<global_score:
        global_score=refined_score
        global_best=refined

print("\nFINAL BEST SCORE:",global_score)

print("\nFINAL 8x8 LAYOUT")
for l in DIRECTIONS:
    row=[]
    for r in DIRECTIONS:
        found=[k for k,v in global_best.items() if v==(l,r)]
        row.append(found[0] if found else "-")
    print(l,row)