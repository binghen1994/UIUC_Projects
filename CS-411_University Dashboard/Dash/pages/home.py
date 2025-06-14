import dash
from dash import dcc
from dash import html
from dash import callback, Input, Output, State
import dash_bootstrap_components as dbc

from configs.global_config import global_style
from utils.mysql_utils import is_keyword_name_exist
import utils.slug_conversion_utils as cvt
from utils.mysql_utils import execute_dql

dash.register_page(__name__, path='/')

styles = {
    "title": {
        'textAlign': 'center',
        'margin': '20px',
    },

    "centered-comp": {
        "width": "500px",
        'textAlign': 'center',
        'margin': '8px',
    },

    "link": {
        'margin': '8px',
    },

    "keyword-container": {
        "border": "0.75px solid grey",
        'border-radius': '10px',
        "padding": "20px",
        "margin": "16px",
        "width": "500px",
    },

}


def layout():
    sql_str = "SELECT COUNT(*), k.name " \
              "FROM keyword_search k_s " \
              "JOIN keyword k ON k_s.id = k.id " \
              "GROUP BY k.id " \
              "ORDER BY COUNT(*) DESC " \
              "LIMIT 10 " \
              ";"

    hot_keyword_data = execute_dql(sql_str)

    print(hot_keyword_data)

    return html.Div(
        children=dbc.Col(
            children=[
                html.H3(
                    children='Data you need for quit Computer Science',
                    style=styles['title']
                ),

                dbc.Input(
                    id="input_key",
                    type="text",
                    placeholder="Type a keyword...",

                    style=styles["centered-comp"]
                ),

                dbc.Button(
                    'Submit',
                    id='search-key',
                    n_clicks=0,
                    style=styles["centered-comp"]
                ),

                html.Div(
                    id='hidden-div-for-redirect',

                    style=styles["centered-comp"]
                ),

                html.Div(
                    children=[
                        html.H6(
                            children='Hot Keywords',
                            style=styles["title"],
                        ),

                        html.Div(
                            id="keyword_div",

                            children=[
                                dcc.Link(
                                    children=k[1],
                                    href=f'/keyword/{cvt.toNameSlug(k[1])}',
                                    style=styles['link'],
                                )
                                for k in hot_keyword_data
                            ]
                        ),

                    ],
                    style=styles["keyword-container"],
                )

            ],

            style={
                "width": '550px',
                "align-items": 'center',
                "justify-content": 'center',
                "display": "flex",
                "flex-direction": "column",
            },
        ),
        style={
            "width": '100vw',
            "height": '90vh',
            "align-items": 'center',
            "justify-content": 'center',
            "display": "flex",
            "flex-direction": "row",
        },
    )


@callback(
    Output('hidden-div-for-redirect', 'children'),
    Input('search-key', 'n_clicks'),
    State('input_key', 'value')
)
def update_output(n_clicks, value):
    # check validity of title

    if value is None or len(value) == 0:
        return "Please write a keyword!"
    elif is_keyword_name_exist(value):
        nameslug = cvt.toNameSlug(value)
        return dcc.Location(pathname=f"/keyword/{nameslug}", id="useless-id")
    else:
        return "Invalid keyword"
