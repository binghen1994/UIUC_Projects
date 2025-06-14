import dash
from dash import dcc
from dash import html
import pandas as pd

import components.graph as graph_comp
from utils.mysql_utils import execute_dql



dash.register_page(__name__, path_template='/test')



def layout():

    data2 = pd.DataFrame(execute_dql(), columns=['count', 'name'])


    return html.Div(

        children=[
            html.H1(
                children="This is a useless app with trash code",
                style={
                   'textAlign': 'center',
                    'color': '#200090',
                },
            ),
            html.P(
                children="This is a useless app with trash code *2"
            ),
            graph_comp.test_graph(df=data2, xname='count', yname='name'),
            graph_comp.graph_pie(df=data2, values="count", names="name"),
            graph_comp.graph_bar(df=data2, x='count', y='name', color='name')
        ],
    )


