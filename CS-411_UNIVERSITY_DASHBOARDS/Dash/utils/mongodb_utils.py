import pymongo




def get_client():
    return pymongo.MongoClient('mongodb://localhost:27017')


def get_database(name='academicworld'):
    return get_client()[name]


