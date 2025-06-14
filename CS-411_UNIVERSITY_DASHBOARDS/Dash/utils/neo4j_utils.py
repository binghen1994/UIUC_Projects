import dash
from dash import dcc, html
from dash.dependencies import Input, Output
from neo4j import GraphDatabase


uri = "bolt://localhost:7687"  # Replace with your Neo4j URI
user = "neo4j"  # Replace with your Neo4j username
password = "password"


driver = GraphDatabase.driver(uri, auth=(user, password))
session = driver.session(database='academicworld')

def run_query(query, **params):
    return session.run(query,**params )

def relative_professor(name,university):
    query= "MATCH ((f1:FACULTY)-[:AFFILIATION_WITH]->(:INSTITUTE)),((f2:FACULTY{name:$name})-[:AFFILIATION_WITH]->(:INSTITUTE{name:$university})),p=shortestpath((f2)-[r:INTERESTED_IN*]-(f1)) RETURN p LIMIT 10"

    result = (run_query(query,name=name,university=university)).data()
    # print(result)
    element = []

    # store initial professor's node:
    orid = result[0]['p'][0]['id']
    orilabel = result[0]['p'][0]['name']
    source = orid
    element.append({'data': {'id': orid, 'label': orilabel}, 'classes': 'center'})
    # keep track of existing profesoors
    labelist = []

    for record in result:
        # store the similar professor
        id = record['p'][4]['id']
        label = record['p'][4]['name']
        if label not in labelist:
            element.append({'data': {'id': id, 'label': label}, 'classes': 'red'})
            labelist.append(label)
            # store the relation in edge
            target = id
            edgelabel = record['p'][2]['name']
            element.append({'data': {'source': source, 'target': target, 'label': edgelabel}, 'classes': 'blue'})

    return element

def collaborated_university(university):
    query="MATCH (faculty1:FACULTY)-[a1:AFFILIATION_WITH]-(institute1:INSTITUTE{name: $university})MATCH (faculty2:FACULTY)-[a2:AFFILIATION_WITH]-(institute2:INSTITUTE) WHERE institute1 <> institute2 MATCH p=(faculty1:FACULTY)-[r1:PUBLISH]->(publication:PUBLICATION)<-[r2:PUBLISH]-(faculty2:FACULTY) WITH institute2.name AS university_name, institute2.id AS university_id, COUNT(DISTINCT faculty1) AS faculty_count RETURN university_name, university_id, faculty_count ORDER BY faculty_count DESC LIMIT 10"
    orid=((run_query("MATCH (institute:INSTITUTE{name: $university}) RETURN institute.id", university=university)).data())[0]['institute.id']
    # print(orid)
    result = (run_query(query, university=university)).data()
    element = []

    # store initial professor's node:
    orilabel = university
    source = orid
    element.append({'data': {'id': orid, 'label': orilabel}, 'classes': 'center'})
    # keep track of existing university
    labelist = []

    for record in result:
        # store the similar professor
        id = record['university_id']
        label = record['university_name']
        if label not in labelist:
            element.append({'data': {'id': id, 'label': label}, 'classes': 'red'})
            labelist.append(label)
            # store the relation in edge
            target = id
            edgelabel = str(record['faculty_count'])
            element.append({'data': {'source': source, 'target': target, 'label': edgelabel}, 'classes': 'blue'})
    # print(element)

    return element



def publication_network_finder(title):
    # DO NOT PASS VALUE OTHER THAN STRING WHEN CREATE EDGE, NODE TO Cytoscape
    query="MATCH ((p1:PUBLICATION)-[:LABEL_BY]->(:KEYWORD)),((p2:PUBLICATION{title:$title})-[:LABEL_BY]->(:KEYWORD)), p=shortestpath((p1)-[r:LABEL_BY*]-(p2)) RETURN p ORDER BY p2.numcitations DESC LIMIT 5"
    result = (run_query(query, title=title)).data()
    keyword_dict = {}

    #Add center node into graph
    query_temp = ((run_query("MATCH (p2:PUBLICATION{title:$title}) RETURN p2", title=title)).data())[0]['p2']
    orid=query_temp['id']
    label=(query_temp['title'])
    numcitation=str(query_temp['numCitations'])

    #Temp Storage of last node's id
    pid = 0
    kid = 0
    labeltemp = ''
    element = []
    edgelist=[]
    element.append({'data': {'id': orid, 'info': {'type':'Publication', 'name':query_temp['title'],'id':orid,'numcitation':label}}, 'classes': 'center'})

    #Store nodes in graph
    for record in result:
        # print(record)
        result=record['p']
        for tuple in result:
            if tuple != 'LABEL_BY':
                if 'p' in tuple['id']:
                    label = tuple['title']
                    id = tuple['id']
                    edgelabel = str(tuple['numCitations'])
                    if id != orid:
                        element.append({'data': {'id': id, 'info': {'type':'Publication', 'name':tuple['title'],'id':id,'numcitation':edgelabel}}, 'classes': 'red'})
                        pid = id
                        labeltemp = edgelabel
                        if kid != 0:
                            edgelist.append({'data': {'source': kid, 'target': id, 'label': edgelabel}, 'classes': 'blue'})
                            kid = 0
                    else:
                        if kid != 0:
                            edgelist.append({'data': {'source': id, 'target': kid, 'label': edgelabel}, 'classes': 'blue'})
                            kid = 0
                else:
                    id = tuple['id']
                    label = tuple['name']
                    kid = id
                    edgelist.append({'data': {'source': pid, 'target': id, 'label': labeltemp}, 'classes': 'blue'})
                    if id not in keyword_dict:
                        keyword_dict[id] = 1
                        element.append({'data': {'id': id, 'info': {'type':'Keyword','name':tuple['name'],'id':id}}, 'classes': 'keyword'})
    for edge in edgelist:
        element.append(edge)
    return(element)