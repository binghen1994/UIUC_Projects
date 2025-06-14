import json
import sys
import logging
import redis
import pymysql


DB_HOST = "mp6db.cluster-czccados7xks.us-east-1.rds.amazonaws.com"
DB_USER = "cs498mp6"
DB_PASS = "BINGhen1994"
DB_NAME = "mp6db"
DB_TABLE = "herostb"
REDIS_URL = "redis://cs498mp6-1.nv8wdf.clustercfg.use1.cache.amazonaws.com:6379"

TTL = 10
# Cache keyed by index
class DB:
    def __init__(self, **params):
        params.setdefault("charset", "utf8mb4")
        params.setdefault("cursorclass", pymysql.cursors.DictCursor)

        self.mysql = pymysql.connect(**params)

    def query(self, sql):
        with self.mysql.cursor() as cursor:
            cursor.execute(sql)
            return cursor.fetchall()

    def get_idx(self, table_name):
        with self.mysql.cursor() as cursor:
            cursor.execute(f"SELECT MAX(id) as id FROM {table_name}")
            idx = str(cursor.fetchone()['id'] + 1)
            return idx

    def insert(self, idx, data):
        with self.mysql.cursor() as cursor:
            hero = data["hero"]
            power = data["power"]
            name = data["name"]
            xp = data["xp"]
            color = data["color"]
            
            sql = f"INSERT INTO herostb (`id`, `hero`, `power`, `name`, `xp`, `color`) VALUES ('{idx}', '{hero}', '{power}', '{name}', '{xp}', '{color}')"

            cursor.execute(sql)
            self.mysql.commit()

def read(use_cache, indices, Database, Cache):
    result=[]
    for index in indices:
        sql=f"SELECT * FROM {DB_TABLE} WHERE id={index}"
        if use_cache:
            res = Cache.get(index)
            if res:
                result.append(json.loads(res))
            else:
                res = Database.query(sql)
                Cache.setex(index, TTL, json.dumps(res))
                result.append(res)

        else:
            res = Database.query(sql)
            Cache.setex(index, TTL, json.dumps(res))
            result.append(res)
    return result


def write(use_cache, sqls, Database, Cache):
    for data in sqls:
        idx=Database.get_idx(DB_TABLE)
        if use_cache:
            Cache.setex(idx, TTL, json.dumps(data))
            Database.insert(idx,data)
        # write through strategy
        else:
            Database.insert(idx,data)


def lambda_handler(event, context):
    
    USE_CACHE = (event['USE_CACHE'] == "True")
    REQUEST = event['REQUEST']
    
    # initialize database and cache
    try:
        Database = DB(host=DB_HOST, user=DB_USER, password=DB_PASS, db=DB_NAME)
    except pymysql.MySQLError as e:
        print("ERROR: Unexpected error: Could not connect to MySQL instance.")
        print(e)
        sys.exit()
        
    Cache = redis.Redis.from_url(REDIS_URL)
    
    result = []
    if REQUEST == "read":
        # event["SQLS"] should be a list of integers
        result = read(USE_CACHE, event["SQLS"], Database, Cache)
    elif REQUEST == "write":
        # event["SQLS"] should be a list of jsons
        write(USE_CACHE, event["SQLS"], Database, Cache)
        result = "write success"
    
    
    return {
        'statusCode': 200,
        'body': result
    }