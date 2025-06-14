import dash
import plotly.express as px
from dash import dcc
from dash import html
import pandas as pd

from utils.mysql_utils import execute_dql

styles = {
    "graph-style": {
        'width': '60%',
    },

}


def test_graph(df, xname="name", yname="id"):

    return dcc.Graph(
        figure={
            "data": [
                {
                    "x": df[xname],
                    "y": df[yname],
                },
            ],
            "layout": {
                "title": "Sample graph which is so useless",
            },
        }
    )


def graph_pie(df, values:str, names:str):

    fig = px.pie(df, values=values, names=names)

    return dcc.Graph(
        figure=fig,
    )


def faculty_key_year(keyword_name:str):

    sql_str = 'SELECT COUNT(DISTINCT f_p.faculty_id), p.year ' \
              'FROM publication p ' \
              'JOIN publication_keyword p_k ON p_k.publication_id = p.id ' \
              'JOIN keyword k ON k.id = p_k.keyword_id ' \
              'JOIN faculty_publication f_p ON f_p.publication_id = p.id ' \
              f'WHERE k.name="{keyword_name}" ' \
              'AND p.id <> 2147483647 ' \
               'GROUP BY p.year ' \
              'ORDER BY p.year' \
              ';'


    df = pd.DataFrame(
        execute_dql(sql_str),
        columns=['count', 'year'],
    )

    # print(df)

    fig = px.bar(
        df,
         x="year",
         y="count",
         title=f"Faculty interest in {keyword_name}"
    )

    return dcc.Graph(
        figure=fig,
        style=styles["graph-style"]
    )


def affiliation_key_faculty_year(keyword_name:str):

    sql_str = 'SELECT COUNT(f.id), u.name ' \
              'FROM faculty f ' \
              'JOIN university u ON u.id = f.university_id ' \
              'JOIN faculty_keyword f_k ON f_k.faculty_id = f.id ' \
              'JOIN keyword k ON f_k.keyword_id = k.id ' \
              f'WHERE k.name="{keyword_name}" ' \
               'GROUP BY u.id ' \
              'ORDER BY COUNT(f.id) DESC ' \
              'LIMIT 10 ' \
              ';'


    df = pd.DataFrame(
        execute_dql(sql_str),
        columns=['count', 'affiliation_name'],
    )

    # print(df)

    fig = px.pie(
        df,
        values="count",
        names="affiliation_name",
        title=f"Faculty count of affiliation interest in {keyword_name}",
    )

    return dcc.Graph(
        figure=fig,
        style=styles["graph-style"]
    )



def publication_keyword_year(keyword_name:str):

    sql_str = 'SELECT COUNT(*), p.year ' \
              'FROM publication p ' \
              'JOIN publication_keyword p_k ON p_k.publication_id = p.id ' \
              'JOIN keyword k ON k.id = p_k.keyword_id ' \
              f'WHERE k.name="{keyword_name}" ' \
              'AND p.id <> 2147483647 ' \
              'GROUP BY p.year ' \
              'ORDER BY p.year ' \
              ';'


    df = pd.DataFrame(
        execute_dql(sql_str),
        columns=['count', 'year'],
    )

    # print(df)

    fig = px.scatter(
        df,
        x="year",
        y="count",
        size="count",
        color="year",
        title=f"Publication has keyword in {keyword_name}"
    )

    return dcc.Graph(
        figure=fig,
        style=styles["graph-style"]
    )



def graph_bar(df, x:str, y:str, color:str):

    fig = px.bar(df, x=x, y=y, color=color)

    return dcc.Graph(
        figure=fig,
    )

