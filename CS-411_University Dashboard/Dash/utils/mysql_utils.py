from sqlalchemy import create_engine, text, insert



def get_engine():
    engine = create_engine(
        "mysql+pymysql://root:password@localhost:3306/academicworld",
        echo=True,
    )

    return engine



simple_query = 'SELECT COUNT(k.id), f.name ' \
               'FROM faculty f ' \
               'JOIN faculty_keyword f_k ON f_k.faculty_id = f.id ' \
               'JOIN keyword k ON f_k.keyword_id = k.id ' \
               'GROUP BY f.id ' \
               'ORDER BY COUNT(k.id) DESC ' \
               'LIMIT 10 ' \
               ';'



def is_keyword_name_exist(keyword_name: str):

    check_sql = 'SELECT * ' \
                'FROM keyword k ' \
                f'WHERE k.name = "{keyword_name}" '

    result = execute_dql(check_sql)

    # print(result)

    return len(result) > 0



def insert_keyword_search(keyword_name: str):

    cur_id = get_keyword_by_name(keyword_name)[0]

    sql_str = "INSERT INTO keyword_search " \
              f"VALUES({cur_id}, now()) " \
              ";"

    execute_dml(sql_str)

    pass



def get_keyword_by_name(keyword_name: str):

    sql_str = "SELECT * " \
              "FROM keyword k " \
              f'WHERE k.name = "{keyword_name}" ' \
              ";"

    cur_result = execute_dql(sql_str)[0]

    # print(cur_result)

    return cur_result



def execute_dql(sql_str=simple_query):

    engine = get_engine()

    # print('querying with string', sql_str)

    with engine.connect() as conn:
        cursor = conn.execute(text(sql_str))

    result = cursor.all()

    # print(result)

    return result



def execute_dml(sql_str=simple_query):

    engine = get_engine()

    # print('querying with string', sql_str)

    with engine.connect() as conn:
        conn.execute(text(sql_str))
        conn.commit()

    pass




