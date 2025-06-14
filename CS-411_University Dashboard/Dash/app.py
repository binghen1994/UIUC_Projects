import dash
from dash import dcc
from dash import html
import dash_bootstrap_components as dbc

from configs.global_config import global_style
from views.navbar import navbar


app = dash.Dash(
    __name__,
    use_pages=True,
    external_stylesheets=[dbc.themes.MINTY],
)


app.layout = dbc.Container(

    children=[
        navbar,
        dash.page_container,
    ],

    style=global_style['global_container_style'],

)


if __name__ == "__main__":
    app.run_server(debug=True, )



