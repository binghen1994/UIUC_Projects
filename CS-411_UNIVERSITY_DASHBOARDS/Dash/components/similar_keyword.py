import dash
import plotly.express as px
from dash import dcc
from dash import html
from word_forms.word_forms import get_word_forms

from utils.mysql_utils import execute_dql
from utils.neo4j_utils import run_query
import utils.slug_conversion_utils as cvt

styles = {
    "row": {
        "border": "0.75px solid grey",
        'border-radius': '10px',
        "padding": "20px",
        "margin": "10px",
    },

    "link": {
        'margin': '8px',
    },

}


def similar_keyword(keyword_name: str):

    cur_all_form_list = get_cur_keyword_composes(keyword_name)

    similar_list = get_similar_keyword_list(cur_all_form_list)

    similar_list.remove(keyword_name)

    update_all_keyword_in_graphdb(keyword_name, similar_list)

    cur_similar_keyword = get_similar_from_graph(keyword_name)



    comp = html.Div(
        children=[
            html.H5(
                children=f'Similar keywords for {keyword_name}',
                style={
                    'textAlign': 'center',
                },
            ),

            html.Div(
                id="keyword_div",

                children=[
                    dcc.Link(
                        children=k,
                        href=f'/keyword/{cvt.toNameSlug(k)}',
                        style=styles['link'],
                    )
                    for k in cur_similar_keyword
                ]
            ),
        ],

        style=styles['row']
    )

    return comp



def get_cur_keyword_composes(keyword_name: str):
    cur_keyword_list = keyword_name.strip().split(' ')
    cur_result = {keyword_name}

    for cur_keyword in cur_keyword_list:
        cur_all_form = get_word_forms(cur_keyword, 0.5)
        for form_key in cur_all_form:
            cur_result.update(cur_all_form[form_key])

    return cur_result





def get_similar_keyword_list(keywords: list):

    cur_keyword_str = '|'.join(keywords)

    sql_str = "SELECT k.name " \
              "FROM keyword k " \
              f'WHERE k.name RLIKE "{cur_keyword_str}" ' \
              ';'

    cur_result = execute_dql(sql_str)

    return [r[0] for r in cur_result]



def update_all_keyword_in_graphdb(keyword_name: str, similar_list: list):

    for similar_key in similar_list:
        cypher_str = "MATCH (a:KEYWORD)-[r:SIMILAR_KEYWORD]-(b:KEYWORD) " \
                    'WHERE a.name = $keyword_name AND b.name = $similar_key ' \
                    "RETURN r"

        # print(cypher_str)
        detect_result = run_query(cypher_str, keyword_name=keyword_name, similar_key=similar_key).data()
        # print(detect_result)

        if(len(detect_result) == 0):
            cypher_create_str = "MATCH (a:KEYWORD), (b:KEYWORD) " \
                         'WHERE a.name = $keyword_name AND b.name = $similar_key ' \
                         "CREATE (a)-[r1:SIMILAR_KEYWORD]->(b), " \
                                "(b)-[r2:SIMILAR_KEYWORD]->(a) " \
                         "RETURN r1, r2"

            # print(cypher_create_str)
            create_result = run_query(cypher_create_str, keyword_name=keyword_name, similar_key=similar_key).data()
            # print(create_result)

# update_all_keyword_in_graphdb('algorithm', ['algorithms'])



def get_similar_from_graph(keyword_name:str):

    cypher_str = "MATCH " \
                 "(k1:KEYWORD{name: $keyword_name})-[:SIMILAR_KEYWORD]-(k2:KEYWORD), " \
                 "p=shortestpath((k1)-[r:SIMILAR_KEYWORD*]-(k2)) " \
                 "RETURN p " \
                 "LIMIT 20 "

    # print(cypher_str)
    cur_result = run_query(cypher_str, keyword_name=keyword_name).data()
    cur_similar_result = set([c["p"][2]['name'] for c in cur_result])

    return cur_similar_result

# get_similar_from_graph('algorithm')





